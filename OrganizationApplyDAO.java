/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.EventVolunteer;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class OrganizationApplyDAO {

    private Connection conn;

    public OrganizationApplyDAO() {
        try {
            DBContext db = new DBContext();

            this.conn = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //lấy các danh sách VolunteerApplytheo từng event_id;
    public List<EventVolunteer> getVolunteersByEvent(int eventId, int organizationId) {
        List<EventVolunteer> list = new ArrayList<>();
        String sql = """
                     SELECT 
                         ev.id,
                         ev.event_id,
                         ev.volunteer_id,
                         ev.note,
                         ev.apply_date,
                         ev.status,
                         o.username AS organization_name,
                         c.name AS category_name,
                         u.full_name AS volunteer_name
                     FROM Event_Volunteers ev
                     JOIN Events e ON ev.event_id = e.id
                     JOIN Accounts o ON e.organization_id = o.id
                     JOIN Categories c ON e.category_id = c.category_id
                     JOIN Users u ON ev.volunteer_id = u.account_id
                     WHERE ev.event_id = ?
                       AND e.organization_id = ?
                     ORDER BY ev.apply_date DESC;
                     """;
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, organizationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EventVolunteer ev = new EventVolunteer(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("volunteer_id"),
                        rs.getTimestamp("apply_date"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getString("organization_name"),
                        rs.getString("category_name"),
                        rs.getString("volunteer_name"));
                list.add(ev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // hàm xử lý thao túc
    public void updateVolunteerStatus(int volunteerId, String status) {
        String sql = "UPDATE Event_Volunteers SET status = ? WHERE id = ?";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, volunteerId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Đếm số volunteer đã được approved cho event (dùng để check slot)
    public int countApprovedVolunteers(int eventId) {
        String sql = "SELECT COUNT(*) FROM Event_Volunteers WHERE event_id = ? AND status = 'approved'";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // hàm xử lý nút lọc theo trạng thái
    public List<EventVolunteer> getFilterVolunteersByEvent(int organizationId, int eventId, String statusFilter) {
        List<EventVolunteer> list = new ArrayList<>();
        String sql = """
        SELECT 
            ev.id,
            ev.event_id,
            ev.volunteer_id,
            ev.apply_date,
            ev.status,
            ev.note,
            o.username AS organizationName,  
            c.name AS categoryName,
            u.full_name AS volunteerName
        FROM Event_Volunteers ev
        JOIN Events e ON ev.event_id = e.id
        JOIN Accounts o ON e.organization_id = o.id
        JOIN Categories c ON e.category_id = c.category_id
        JOIN Users u ON ev.volunteer_id = u.account_id
        WHERE e.organization_id = ?
          AND e.id = ?
    """;

        if (statusFilter != null && !statusFilter.equals("all")) {
            sql += " AND ev.status = ?";
        }

        sql += " ORDER BY ev.apply_date DESC";

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ps.setInt(2, eventId);
            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(3, statusFilter);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                EventVolunteer ev = new EventVolunteer(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("volunteer_id"),
                        rs.getTimestamp("apply_date"),
                        rs.getString("status"),
                        rs.getString("note"),
                        rs.getString("organizationName"),
                        rs.getString("categoryName"),
                        rs.getString("volunteerName")
                );
                list.add(ev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tự động chuyển status "pending" → "rejected" nếu không duyệt trong 24h trước sự kiện, lấy chuẩn đến từng minutes
    public int autoRejectPendingApplications(int eventId) {
        String sql = """
            UPDATE Event_Volunteers 
            SET status = 'rejected', 
                note = CASE 
                    WHEN note IS NULL OR note = '' THEN N'Tự động từ chối do không được xử lý trong 24h trước sự kiện'
                    ELSE note + N' (Tự động từ chối)'
                END
            WHERE event_id = ? 
              AND status = 'pending'
              AND EXISTS (
                  SELECT 1 FROM Events 
                  WHERE id = ? 
                    AND DATEDIFF(MINUTE, GETDATE(), start_date) > 0
                    AND DATEDIFF(MINUTE, GETDATE(), start_date) < 24
              )
        """;

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, eventId);
            return ps.executeUpdate(); // Trả về số row bị ảnh hưởng
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Tự động reject đơn cho TẤT CẢ events đang trong khoảng 24h trước diễn ra , lấy chuẩn từng minutes
    // Trả về danh sách các volunteer bị reject để gửi thông báo
    public List<EventVolunteer> autoRejectAllPendingApplications() {
        List<EventVolunteer> rejectedVolunteers = new ArrayList<>();
        
        // Bước 1: Lấy danh sách pending volunteers trước khi reject
        String selectSql = """
        SELECT ev.id, ev.event_id, ev.volunteer_id, ev.apply_date, ev.status, ev.note,
               e.title AS event_title, e.organization_id
        FROM Event_Volunteers ev
        JOIN Events e ON ev.event_id = e.id
        WHERE ev.status = 'pending'
          AND DATEDIFF(MINUTE, GETDATE(), e.start_date) BETWEEN 1 AND 1440
        """;
        
        try (Connection con = DBContext.getConnection(); 
             PreparedStatement ps = con.prepareStatement(selectSql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                EventVolunteer ev = new EventVolunteer();
                ev.setId(rs.getInt("id"));
                ev.setEventId(rs.getInt("event_id"));
                ev.setVolunteerId(rs.getInt("volunteer_id"));
                ev.setApplyDate(rs.getTimestamp("apply_date"));
                ev.setStatus(rs.getString("status"));
                ev.setNote(rs.getString("note"));
                // Thêm thông tin để gửi notification
                ev.setEventTitle(rs.getString("event_title"));
                ev.setOrganizationId(rs.getInt("organization_id"));
                rejectedVolunteers.add(ev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Bước 2: Update status thành rejected
        String updateSql = """
        UPDATE Event_Volunteers
        SET status = 'rejected',
            note = CASE
                WHEN note IS NULL OR note = '' THEN N'Tự động từ chối do còn dưới 24h trước sự kiện'
                ELSE note + N' (Tự động từ chối)'
            END
        WHERE status = 'pending'
          AND EXISTS (
              SELECT 1 FROM Events
              WHERE id = Event_Volunteers.event_id
                AND DATEDIFF(MINUTE, GETDATE(), start_date) BETWEEN 1 AND 1440
          )
        """;

        try (Connection con = DBContext.getConnection(); 
             PreparedStatement ps = con.prepareStatement(updateSql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return rejectedVolunteers;
    }

}
