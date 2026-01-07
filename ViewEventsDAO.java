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
 * @author ADDMIN
 */
public class ViewEventsDAO {

    private Connection conn;

    public ViewEventsDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Đóng tất cả events đã hết hạn (end_date < now)
    public void closeExpiredEvents() {
        String sql = """
            UPDATE Events
            SET status = 'closed'
            WHERE status IN ('active', 'inactive')
              AND end_date < GETDATE()
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[ViewEventsDAO] Đã đóng " + rows + " sự kiện hết hạn");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Lấy danh sách sự kiện đang active và public để hiển thị lên jsp
    public List<Event> getActiveEvents() {
        // Tự động đóng events hết hạn trước khi query
        closeExpiredEvents();
        
        List<Event> list = new ArrayList<>();
        String sql = """
    SELECT e.*, 
           u.full_name AS organization_name, 
           c.name AS category_name
    FROM Events e
    JOIN Accounts a ON e.organization_id = a.id
    JOIN Users u ON a.id = u.account_id
    JOIN Categories c ON e.category_id = c.category_id
    WHERE e.status = 'active' and e.visibility = 'public'
""";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Event e = new Event(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("images"),
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

    // lấy từng sự kiện để apply
    public Event getEventById(int eventId) {
        Event event = null;
        String sql = """
        SELECT e.*, 
               u.full_name AS organization_name, 
               c.name AS category_name
        FROM Events e
        JOIN Accounts a ON e.organization_id = a.id
        JOIN Users u ON a.id = u.account_id
        JOIN Categories c ON e.category_id = c.category_id
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

    // Lấy 3 sự kiện mới nhất active + public để update lên màn hình giao diện quảng bá
    public List<Event> getLatestActivePublicEvents() {
        List<Event> list = new ArrayList<>();
        String sql = """
        SELECT TOP 3 e.*, 
               u.full_name AS organization_name, 
               c.name AS category_name
        FROM Events e
        JOIN Accounts a ON e.organization_id = a.id
        JOIN Users u ON a.id = u.account_id
        JOIN Categories c ON e.category_id = c.category_id
        WHERE e.status = 'active' 
          AND e.visibility = 'public'
        ORDER BY e.start_date DESC
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

    //cú pháp phân trang sự kiện
    public List<Event> getActiveEventsPaged(int offset, int limit) {
        List<Event> list = new ArrayList<>();
        String sql = """
        SELECT e.*, 
               u.full_name AS organization_name, 
               c.name AS category_name
        FROM Events e
        JOIN Accounts a ON e.organization_id = a.id
        JOIN Users u ON a.id = u.account_id
        JOIN Categories c ON e.category_id = c.category_id
        WHERE e.status = 'active' and e.visibility = 'public'
        ORDER BY e.start_date DESC
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
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

    //tính tổng event đang hoạt động + công khai nhằm chia trang
    public int getTotalActiveEvents() {
        String sql = "SELECT COUNT(*) FROM Events WHERE status = 'active' and visibility = 'public'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    // Kiểm tra volunteer đã đăng ký sự kiện chưa

    public boolean hasVolunteerApplied(int volunteerId, int eventId) {
        String sql = "SELECT COUNT(*) FROM Event_Volunteers WHERE volunteer_id = ? AND event_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, volunteerId);
            ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Kiểm tra volunteer đã ủng hộ thành công sự kiện chưa
    public boolean hasVolunteerDonated(int volunteerId, int eventId) {
        String sql = "SELECT COUNT(*) FROM Donations WHERE volunteer_id = ? AND event_id = ? AND status = 'success'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, volunteerId);
            ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả categories
    public List<model.Category> getAllCategories() {
        List<model.Category> list = new ArrayList<>();
        String sql = "SELECT category_id, name FROM Categories ORDER BY name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.Category cat = new model.Category();
                cat.setCategoryId(rs.getInt("category_id"));
                cat.setName(rs.getString("name"));
                list.add(cat);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Lọc events theo category, date range, sort order + phân trang
    public List<Event> getFilteredEventsPaged(Integer categoryId, String startDateStr, String endDateStr, 
                                              String sortOrder, int offset, int limit) {
        List<Event> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("""        
            SELECT e.*, 
                   u.full_name AS organization_name, 
                   c.name AS category_name
            FROM Events e
            JOIN Accounts a ON e.organization_id = a.id
            JOIN Users u ON a.id = u.account_id
            JOIN Categories c ON e.category_id = c.category_id
            WHERE e.status = 'active' AND e.visibility = 'public'
        """);
        
        // Thêm điều kiện lọc
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND e.category_id = ?");
        }
        if (startDateStr != null && !startDateStr.isEmpty()) {
            sql.append(" AND e.start_date >= ?");
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            sql.append(" AND e.start_date <= ?");
        }
        
        // Thêm sắp xếp
        if ("asc".equals(sortOrder)) {
            sql.append(" ORDER BY e.start_date ASC");
        } else {
            sql.append(" ORDER BY e.start_date DESC");
        }
        
        sql.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (categoryId != null && categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }
            if (startDateStr != null && !startDateStr.isEmpty()) {
                ps.setString(paramIndex++, startDateStr + " 00:00:00");
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                ps.setString(paramIndex++, endDateStr + " 23:59:59");
            }
            
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, limit);
            
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

    // Đếm tổng số events sau khi filter
    public int getTotalFilteredEvents(Integer categoryId, String startDateStr, String endDateStr) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM Events e WHERE e.status = 'active' AND e.visibility = 'public'");
        
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND e.category_id = ?");
        }
        if (startDateStr != null && !startDateStr.isEmpty()) {
            sql.append(" AND e.start_date >= ?");
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            sql.append(" AND e.start_date <= ?");
        }
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (categoryId != null && categoryId > 0) {
                ps.setInt(paramIndex++, categoryId);
            }
            if (startDateStr != null && !startDateStr.isEmpty()) {
                ps.setString(paramIndex++, startDateStr + " 00:00:00");
            }
            if (endDateStr != null && !endDateStr.isEmpty()) {
                ps.setString(paramIndex++, endDateStr + " 23:59:59");
            }
            
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

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
