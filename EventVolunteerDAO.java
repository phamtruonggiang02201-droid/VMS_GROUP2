package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.EventVolunteer;
import utils.DBContext;

public class EventVolunteerDAO {

    // Lấy list danh sách các sự kiện mà volunteer đã đăng ký 
    public List<EventVolunteer> getEventRegistrationsByVolunteerId(int volunteerId) {
        List<EventVolunteer> list = new ArrayList<>();
        String sql = """
            SELECT ev.id, ev.event_id, ev.volunteer_id, ev.apply_date, ev.status, ev.note,
                   e.title AS eventTitle,
                   c.name AS categoryName,
                   u_org.full_name AS organizationName,
                   u_vol.full_name AS volunteerName
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            LEFT JOIN Accounts o ON e.organization_id = o.id
            LEFT JOIN Users u_org ON o.id = u_org.account_id
            LEFT JOIN Accounts v ON ev.volunteer_id = v.id
            LEFT JOIN Users u_vol ON v.id = u_vol.account_id
            WHERE ev.volunteer_id = ?
            ORDER BY ev.apply_date DESC
        """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventVolunteer ev = new EventVolunteer();
                    ev.setId(rs.getInt("id"));
                    ev.setEventId(rs.getInt("event_id"));
                    ev.setVolunteerId(rs.getInt("volunteer_id"));
                    Timestamp ts = rs.getTimestamp("apply_date");
                    ev.setApplyDate(ts != null ? new java.util.Date(ts.getTime()) : null);
                    ev.setStatus(rs.getString("status"));
                    ev.setNote(rs.getString("note"));
                    ev.setEventTitle(rs.getString("eventTitle"));
                    ev.setCategoryName(rs.getString("categoryName"));
                    ev.setOrganizationName(rs.getString("organizationName"));
                    ev.setVolunteerName(rs.getString("volunteerName"));

                    list.add(ev);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lấy danh sách sự kiện với filter, sort và phân trang
    public List<EventVolunteer> getEventRegistrationsFiltered(int volunteerId, String statusFilter, String sortOrder, int page, int pageSize) {
        List<EventVolunteer> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        StringBuilder sql = new StringBuilder("""
            SELECT ev.id, ev.event_id, ev.volunteer_id, ev.apply_date, ev.status, ev.note,
                   e.title AS eventTitle,
                   c.name AS categoryName,
                   u_org.full_name AS organizationName,
                   u_vol.full_name AS volunteerName
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            LEFT JOIN Accounts o ON e.organization_id = o.id
            LEFT JOIN Users u_org ON o.id = u_org.account_id
            LEFT JOIN Accounts v ON ev.volunteer_id = v.id
            LEFT JOIN Users u_vol ON v.id = u_vol.account_id
            WHERE ev.volunteer_id = ?
        """);
        
        // Thêm filter theo status
        if (statusFilter != null && !statusFilter.equals("all")) {
            sql.append(" AND ev.status = ?");
        }
        
        // Thêm ORDER BY theo apply_date
        if ("asc".equals(sortOrder)) {
            sql.append(" ORDER BY ev.apply_date ASC");
        } else {
            sql.append(" ORDER BY ev.apply_date DESC");
        }
        
        // Thêm phân trang
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = DBContext.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            ps.setInt(paramIndex++, volunteerId);
            
            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(paramIndex++, statusFilter);
            }
            
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EventVolunteer ev = new EventVolunteer();
                    ev.setId(rs.getInt("id"));
                    ev.setEventId(rs.getInt("event_id"));
                    ev.setVolunteerId(rs.getInt("volunteer_id"));
                    Timestamp ts = rs.getTimestamp("apply_date");
                    ev.setApplyDate(ts != null ? new java.util.Date(ts.getTime()) : null);
                    ev.setStatus(rs.getString("status"));
                    ev.setNote(rs.getString("note"));
                    ev.setEventTitle(rs.getString("eventTitle"));
                    ev.setCategoryName(rs.getString("categoryName"));
                    ev.setOrganizationName(rs.getString("organizationName"));
                    ev.setVolunteerName(rs.getString("volunteerName"));
                    list.add(ev);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    
    // Đếm tổng số sự kiện theo filter
    public int countEventRegistrations(int volunteerId, String statusFilter) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM Event_Volunteers WHERE volunteer_id = ?"
        );
        
        if (statusFilter != null && !statusFilter.equals("all")) {
            sql.append(" AND status = ?");
        }
        
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            ps.setInt(1, volunteerId);
            
            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(2, statusFilter);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    // --- Lấy trạng thái đơn ---
    public String getApplicationStatus(int eventId, int volunteerId) {
        String sql = "SELECT status FROM Event_Volunteers WHERE event_id = ? AND volunteer_id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setInt(2, volunteerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- Xóa đơn  --
    public boolean deletePendingApplication(int eventId, int volunteerId) {
        String sql = "DELETE FROM Event_Volunteers WHERE event_id = ? AND volunteer_id = ? AND status = 'Pending'";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, volunteerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy thông tin chi tiết 1 đơn đăng ký của volunteer cho 1 sự kiện (dùng cho lịch sử sự kiện)
    public EventVolunteer getRegistrationByEventAndVolunteer(int eventId, int volunteerId) {
        EventVolunteer ev = null;
        String sql = """
        SELECT 
            ev.id,
            ev.event_id,
            e.title AS eventTitle,
            e.location AS eventLocation,
            e.description AS eventDescription,
            e.status AS eventStatus,
            c.name AS categoryName,
            u_org.full_name AS organizationName,
            u_org.phone AS orgPhone,
            u_org.email AS orgEmail,
            ev.volunteer_id,
            v.username AS volunteerName,
            ev.apply_date,
            ev.status,
            ev.note,
            ISNULL(d.totalDonate, 0) AS totalDonate,
            e.start_date AS startDateEvent,
            e.end_date AS endDateEvent,
            a.status AS attendanceReport
        FROM Event_Volunteers ev
        INNER JOIN Events e ON ev.event_id = e.id
        LEFT JOIN Categories c ON e.category_id = c.category_id
        
        -- join Accounts và Users để lấy fullname, phone, email tổ chức
        INNER JOIN Accounts o ON e.organization_id = o.id
        INNER JOIN Users u_org ON u_org.account_id = o.id
        
        INNER JOIN Accounts v ON ev.volunteer_id = v.id
        
        LEFT JOIN (
            SELECT volunteer_id, event_id, SUM(amount) AS totalDonate
            FROM Donations
            GROUP BY volunteer_id, event_id
        ) d ON d.event_id = ev.event_id AND d.volunteer_id = ev.volunteer_id
        
        LEFT JOIN Attendance a ON a.event_id = ev.event_id AND a.volunteer_id = ev.volunteer_id
        
        WHERE ev.event_id = ? AND ev.volunteer_id = ?
        ORDER BY ev.apply_date DESC;
    """;

        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, volunteerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ev = new EventVolunteer();
                    ev.setId(rs.getInt("id"));
                    ev.setEventId(rs.getInt("event_id"));
                    ev.setVolunteerId(rs.getInt("volunteer_id"));
                    Timestamp ts = rs.getTimestamp("apply_date");
                    ev.setApplyDate(ts != null ? new java.util.Date(ts.getTime()) : null);
                    ev.setStatus(rs.getString("status"));
                    ev.setNote(rs.getString("note"));
                    ev.setEventTitle(rs.getString("eventTitle"));
                    ev.setCategoryName(rs.getString("categoryName"));
                    ev.setOrganizationName(rs.getString("organizationName"));
                    ev.setVolunteerName(rs.getString("volunteerName"));
                    ev.setTotalDonate(rs.getDouble("totalDonate"));
                    ev.setStartDateEvent(rs.getTimestamp("startDateEvent"));
                    ev.setEndDateEvent(rs.getTimestamp("endDateEvent"));
                    ev.setAttendanceReport(rs.getString("attendanceReport"));
                    ev.setEventLocation(rs.getString("eventLocation"));
                    ev.setEventDescription(rs.getString("eventDescription"));
                    ev.setEventStatus(rs.getString("eventStatus"));
                    ev.setOrgPhone(rs.getString("orgPhone"));
                    ev.setOrgEmail(rs.getString("orgEmail"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ev;
    }
}
