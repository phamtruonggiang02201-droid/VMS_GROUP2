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
import model.Event;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class OrganizationEventsDAO {

    private Connection conn;

    public OrganizationEventsDAO() {
        try {
            DBContext db = new DBContext();

            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Đóng tất cả events đã hết hạn
    private void closeExpiredEvents() {
        String sql = """
            UPDATE Events
            SET status = 'closed'
            WHERE status IN ('active', 'inactive')
              AND end_date < GETDATE()
        """;
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[OrganizationEventsDAO] Đã đóng " + rows + " sự kiện hết hạn");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // lấy EventsByOrganization theo người tổ chức 
    public List<Event> getEventsByOrganization(int organizationId) {
        // Tự động đóng events hết hạn trước khi query
        closeExpiredEvents();
        
        List<Event> list = new ArrayList<>();
        String sql = """ 
                     SELECT 
                         e.id,
                         e.title,
                         e.images,
                         e.description,
                         e.start_date,
                         e.end_date,
                         e.location,
                         e.needed_volunteers,
                         e.status,
                         e.visibility,
                         e.organization_id,
                         a.username AS organization_name,   -- lấy tên tổ chức từ Accounts
                         e.category_id,
                         c.name AS category_name,           -- lấy tên category từ Categories
                         e.total_donation,
                         e.created_at
                     FROM Events e
                     JOIN Accounts a ON e.organization_id = a.id
                     LEFT JOIN Categories c ON e.category_id = c.category_id
                     WHERE e.organization_id = ?
                     """;
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ResultSet rs = ps.executeQuery();
             while (rs.next()) {
                Event e = new Event(
                        rs.getInt("id"),
                        rs.getString("images"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getTimestamp("start_date"),
                        rs.getTimestamp("end_date"),
                        rs.getString("location"),
                        rs.getInt("needed_volunteers"),
                        rs.getString("status"),
                        rs.getString("visibility"),
                        rs.getInt("organization_id"),
                        rs.getInt("category_id"),
                        rs.getDouble("total_donation"),
                        rs.getString("organization_name"),
                        rs.getString("category_name")
                );
                list.add(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    // lọc danh sách các loại sự kiện, trạng thái, chế độ
    public List<Event> getEventsByOrganizationFiltered(int organizationId, String categoryName, String status, String visibility) {
        List<Event> list = new ArrayList<>();
        String sql = """
            SELECT e.*, a.username AS organization_name, c.name AS category_name
            FROM Events e
            JOIN Accounts a ON e.organization_id = a.id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            WHERE e.organization_id = ?
            """;

        // thêm điều kiện filter nếu có
        if (categoryName != null && !categoryName.isEmpty() && !"Tất cả".equals(categoryName)) {
            sql += " AND c.name = ?";
        }
        if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
            sql += " AND e.status = ?";
        }
        if (visibility != null && !visibility.isEmpty() && !"Tất cả".equals(visibility)) {
            sql += " AND e.visibility = ?";
        }

        try (Connection con = DBContext.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int index = 1;
            ps.setInt(index++, organizationId);
            if (categoryName != null && !categoryName.isEmpty() && !"Tất cả".equals(categoryName)) {
                ps.setString(index++, categoryName);
            }
            if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
                ps.setString(index++, status);
            }
            if (visibility != null && !visibility.isEmpty() && !"Tất cả".equals(visibility)) {
                ps.setString(index++, visibility);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Event e = new Event(
                    rs.getInt("id"),
                    rs.getString("images"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getTimestamp("start_date"),
                    rs.getTimestamp("end_date"),
                    rs.getString("location"),
                    rs.getInt("needed_volunteers"),
                    rs.getString("status"),
                    rs.getString("visibility"),
                    rs.getInt("organization_id"),
                    rs.getInt("category_id"),
                    rs.getDouble("total_donation"),
                    rs.getString("organization_name"),
                    rs.getString("category_name")
                );
                list.add(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
}
