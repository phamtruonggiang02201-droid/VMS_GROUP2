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
import model.Category;
import model.Event;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class AdminEventsDAO {

    private Connection conn;

    public AdminEventsDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy tất cả sự kiện (không lọc theo organization)
    public List<Event> getAllEvents() {
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
                u.full_name AS organization_name,
                e.category_id,
                c.name AS category_name,
                e.total_donation
            FROM Events e
            JOIN Accounts a ON e.organization_id = a.id
            JOIN Users u ON a.id = u.account_id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            ORDER BY e.id DESC
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
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

    // Lấy sự kiện với filter (status, category, visibility)
    public List<Event> getEventsWithFilters(String status, String categoryName, String visibility) {
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
                u.full_name AS organization_name,
                e.category_id,
                c.name AS category_name,
                e.total_donation
            FROM Events e
            JOIN Accounts a ON e.organization_id = a.id
            JOIN Users u ON a.id = u.account_id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            WHERE 1=1
            """;

        // Thêm điều kiện filter
        if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
            sql += " AND e.status = ?";
        }
        if (categoryName != null && !categoryName.isEmpty() && !"Tất cả".equals(categoryName)) {
            sql += " AND c.name = ?";
        }
        if (visibility != null && !visibility.isEmpty() && !"Tất cả".equals(visibility)) {
            sql += " AND e.visibility = ?";
        }

        sql += " ORDER BY e.id DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
                ps.setString(index++, status);
            }
            if (categoryName != null && !categoryName.isEmpty() && !"Tất cả".equals(categoryName)) {
                ps.setString(index++, categoryName);
            }
            if (visibility != null && !visibility.isEmpty() && !"Tất cả".equals(visibility)) {
                ps.setString(index++, visibility);
            }

            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Lấy chi tiết sự kiện theo ID (bao gồm số lượng volunteer đã approved)
    public Event getEventDetailById(int eventId) {
        Event event = null;
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
                u.full_name AS organization_name,
                e.category_id,
                c.name AS category_name,
                e.total_donation
            FROM Events e
            JOIN Accounts a ON e.organization_id = a.id
            JOIN Users u ON a.id = u.account_id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            WHERE e.id = ?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    event = new Event(
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
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return event;
    }

    // Đếm số lượng volunteer đã approved cho một sự kiện
    public int getApprovedVolunteersCount(int eventId) {
        String sql = "SELECT COUNT(*) FROM Event_Volunteers WHERE event_id = ? AND status = 'approved'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Cập nhật trạng thái sự kiện thành inactive (khóa)
    public boolean updateEventStatus(int eventId, String status) {
        String sql = "UPDATE Events SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, eventId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Lấy start_date của sự kiện để kiểm tra thời gian
    public java.sql.Timestamp getEventStartDate(int eventId) {
        String sql = "SELECT start_date FROM Events WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("start_date");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Lấy tất cả categories để hiển thị trong filter
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT category_id, name, description FROM Categories ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = new Category(
                        rs.getInt("category_id"),
                        rs.getString("name"),
                        rs.getString("description")
                );
                list.add(c);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số sự kiện theo filter
    public int countEvents(String status, String categoryName, String visibility) {
        String sql = """
            SELECT COUNT(*) AS total
            FROM Events e
            JOIN Accounts a ON e.organization_id = a.id
            JOIN Users u ON a.id = u.account_id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            WHERE 1=1
            """;

        if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
            sql += " AND e.status = ?";
        }
        if (categoryName != null && !categoryName.isEmpty() && !"Tất cả".equals(categoryName)) {
            sql += " AND c.name = ?";
        }
        if (visibility != null && !visibility.isEmpty() && !"Tất cả".equals(visibility)) {
            sql += " AND e.visibility = ?";
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
                ps.setString(index++, status);
            }
            if (categoryName != null && !categoryName.isEmpty() && !"Tất cả".equals(categoryName)) {
                ps.setString(index++, categoryName);
            }
            if (visibility != null && !visibility.isEmpty() && !"Tất cả".equals(visibility)) {
                ps.setString(index++, visibility);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Lấy sự kiện với filter và phân trang
    public List<Event> getEventsPaged(String status, String categoryName, String visibility, int offset, int limit) {
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
                u.full_name AS organization_name,
                e.category_id,
                c.name AS category_name,
                e.total_donation
            FROM Events e
            JOIN Accounts a ON e.organization_id = a.id
            JOIN Users u ON a.id = u.account_id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            WHERE 1=1
            """;

        if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
            sql += " AND e.status = ?";
        }
        if (categoryName != null && !categoryName.isEmpty() && !"Tất cả".equals(categoryName)) {
            sql += " AND c.name = ?";
        }
        if (visibility != null && !visibility.isEmpty() && !"Tất cả".equals(visibility)) {
            sql += " AND e.visibility = ?";
        }

        sql += " ORDER BY e.id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int index = 1;
            if (status != null && !status.isEmpty() && !"Tất cả".equals(status)) {
                ps.setString(index++, status);
            }
            if (categoryName != null && !categoryName.isEmpty() && !"Tất cả".equals(categoryName)) {
                ps.setString(index++, categoryName);
            }
            if (visibility != null && !visibility.isEmpty() && !"Tất cả".equals(visibility)) {
                ps.setString(index++, visibility);
            }
            ps.setInt(index++, offset);
            ps.setInt(index, limit);

            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Kiểm tra xem sự kiện đã thực sự kết thúc chưa (end_date < now)
    public boolean isEventActuallyEnded(int eventId) {
        String sql = "SELECT CASE WHEN end_date < GETDATE() THEN 1 ELSE 0 END AS is_ended FROM Events WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("is_ended") == 1;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

// Lấy end_date của sự kiện
    public java.sql.Timestamp getEventEndDate(int eventId) {
        String sql = "SELECT end_date FROM Events WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("end_date");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
