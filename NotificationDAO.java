/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Notification;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class NotificationDAO {

    private Connection conn;

    public NotificationDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Insert 1 thông báo
    public boolean insertNotification(Notification notification) {
        String sql = "INSERT INTO Notifications (sender_id, receiver_id, message, type, event_id) "
                + "VALUES (?, ?, ?, ?, ?)";
        System.out.println(" [DAO] insertNotification called");
        System.out.println("   SQL: " + sql);
        System.out.println("   - sender_id: " + notification.getSenderId());
        System.out.println("   - receiver_id: " + notification.getReceiverId());
        System.out.println("   - message: " + notification.getMessage());
        System.out.println("   - type: " + notification.getType());
        System.out.println("   - event_id: " + notification.getEventId());
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, notification.getSenderId());
            ps.setInt(2, notification.getReceiverId());
            ps.setString(3, notification.getMessage());
            ps.setString(4, notification.getType());
            if (notification.getEventId() > 0) {
                ps.setInt(5, notification.getEventId());
            } else {
                ps.setNull(5, Types.INTEGER);  // Nếu = 0 thì set NULL
            }
            int rowsAffected = ps.executeUpdate();
            System.out.println(" [DAO] Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println(" [DAO] SQLException occurred!");
            System.out.println("   Error message: " + e.getMessage());
            System.out.println("   SQL State: " + e.getSQLState());
            System.out.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    // 2. Lấy danh sách thông báo theo receiverId (JOIN lấy thêm tên sender, event)
    public List<Notification> getNotificationsByReceiverId(int receiverId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT n.id, n.sender_id, n.receiver_id, n.message, n.type, "
                + "n.created_at, n.is_read, n.event_id, "
                + "us.full_name AS sender_name, "
                + "ur.full_name AS receiver_name, "
                + "e.title AS event_title "
                + "FROM Notifications n "
                + "LEFT JOIN Users us ON n.sender_id = us.account_id "
                + "LEFT JOIN Users ur ON n.receiver_id = ur.account_id "
                + "LEFT JOIN Events e ON n.event_id = e.id "
                + "WHERE n.receiver_id = ? "
                + "ORDER BY n.created_at DESC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, receiverId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setSenderId(rs.getInt("sender_id"));
                n.setReceiverId(rs.getInt("receiver_id"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                n.setIsRead(rs.getBoolean("is_read"));
                n.setEventId(rs.getInt("event_id"));
                n.setSenderName(rs.getString("sender_name"));
                n.setReceiverName(rs.getString("receiver_name"));
                n.setEventTitle(rs.getString("event_title"));

                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 3. Đánh dấu đã đọc
    public boolean markAsRead(int notificationId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, notificationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 4. Đếm số thông báo chưa đọc
    public int getUnreadCount(int receiverId) {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE receiver_id = ? AND is_read = 0";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, receiverId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 5. Đánh dấu tất cả là đã đọc (optional - tiện lợi)
    public boolean markAllAsRead(int receiverId) {
        String sql = "UPDATE Notifications SET is_read = 1 WHERE receiver_id = ? AND is_read = 0";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, receiverId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. Validate: Kiểm tra event chưa kết thúc
    public boolean isEventActive(int eventId) {
        String sql = "SELECT COUNT(*) FROM Events WHERE id = ? AND end_date >= GETDATE()";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 7. Validate: Kiểm tra event có volunteer approved không
    public boolean hasApprovedVolunteers(int eventId) {
        String sql = "SELECT COUNT(*) FROM Event_Volunteers WHERE event_id = ? AND status = 'approved'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 8. Lấy danh sách volunteerId đã approved của event (dùng cho gửi chung)
    public List<Integer> getApprovedVolunteerIds(int eventId) {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT volunteer_id FROM Event_Volunteers WHERE event_id = ? AND status = 'approved'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("volunteer_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 9. Validate: Kiểm tra volunteer cụ thể có approved trong event không
    public boolean isVolunteerApprovedInEvent(int eventId, int volunteerId) {
        String sql = "SELECT COUNT(*) FROM Event_Volunteers WHERE event_id = ? AND volunteer_id = ? AND status = 'approved'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, volunteerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 10. Validate: Kiểm tra thời gian gửi thông báo cuối cùng (phải cách nhau >= 8 tiếng)
//    public boolean canSendNotification(int senderId, int eventId) {
//        String sql = "SELECT TOP 1 created_at FROM Notifications "
//                + "WHERE sender_id = ? AND event_id = ? "
//                + "ORDER BY created_at DESC";
//        try {
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ps.setInt(1, senderId);
//            ps.setInt(2, eventId);
//            ResultSet rs = ps.executeQuery();
//
//            if (rs.next()) {
//                Timestamp lastSentTime = rs.getTimestamp("created_at");
//                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
//
//                // Tính khoảng cách thời gian (milliseconds)
//                long diffInMillis = currentTime.getTime() - lastSentTime.getTime();
//                long diffInHours = diffInMillis / (1000 * 60 * 60); // Convert to hours
//
//                // Phải cách nhau >= 8 tiếng
//                return diffInHours >= 8;
//            }
//
//            // Nếu chưa có thông báo nào → Cho phép gửi
//            return true;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

    // 11. Lấy thời gian còn lại cho đến khi có thể gửi thông báo tiếp theo (đơn vị: giờ)
    public long getHoursUntilNextNotification(int senderId, int eventId) {
        String sql = "SELECT TOP 1 created_at FROM Notifications "
                + "WHERE sender_id = ? AND event_id = ? "
                + "ORDER BY created_at DESC";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, senderId);
            ps.setInt(2, eventId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Timestamp lastSentTime = rs.getTimestamp("created_at");
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());

                long diffInMillis = currentTime.getTime() - lastSentTime.getTime();
                long diffInHours = diffInMillis / (1000 * 60 * 60);

                // Trả về số giờ còn lại
                long hoursRemaining = 8 - diffInHours;
                return hoursRemaining > 0 ? hoursRemaining : 0;
            }

            return 0; // Chưa có thông báo nào
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Thêm vào NotificationDAO , phân trang và sắp xếp
    public List<Notification> getNotificationsByReceiverIdPaginated(int receiverId, int page, int pageSize, String sortOrder) {
        List<Notification> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String orderBy = "DESC"; // Mới nhất
        if ("oldest".equals(sortOrder)) {
            orderBy = "ASC"; // Cũ nhất
        }

        String sql = "SELECT n.id, n.sender_id, n.receiver_id, n.message, n.type, "
                + "n.created_at, n.is_read, n.event_id, "
                + "us.full_name AS sender_name, "
                + "ur.full_name AS receiver_name, "
                + "e.title AS event_title "
                + "FROM Notifications n "
                + "LEFT JOIN Users us ON n.sender_id = us.account_id "
                + "LEFT JOIN Users ur ON n.receiver_id = ur.account_id "
                + "LEFT JOIN Events e ON n.event_id = e.id "
                + "WHERE n.receiver_id = ? "
                + "ORDER BY n.created_at " + orderBy + " "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, receiverId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setSenderId(rs.getInt("sender_id"));
                n.setReceiverId(rs.getInt("receiver_id"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                n.setIsRead(rs.getBoolean("is_read"));
                n.setEventId(rs.getInt("event_id"));
                n.setSenderName(rs.getString("sender_name"));
                n.setReceiverName(rs.getString("receiver_name"));
                n.setEventTitle(rs.getString("event_title"));
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Phân trang với lọc theo ngày tháng
    public List<Notification> getNotificationsByReceiverIdPaginatedWithDateFilter(int receiverId, int page, int pageSize, String sortOrder, String startDate, String endDate) {
        List<Notification> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        String orderBy = "DESC";
        if ("oldest".equals(sortOrder)) {
            orderBy = "ASC";
        }

        StringBuilder sql = new StringBuilder("SELECT n.id, n.sender_id, n.receiver_id, n.message, n.type, "
                + "n.created_at, n.is_read, n.event_id, "
                + "us.full_name AS sender_name, "
                + "ur.full_name AS receiver_name, "
                + "e.title AS event_title "
                + "FROM Notifications n "
                + "LEFT JOIN Users us ON n.sender_id = us.account_id "
                + "LEFT JOIN Users ur ON n.receiver_id = ur.account_id "
                + "LEFT JOIN Events e ON n.event_id = e.id "
                + "WHERE n.receiver_id = ? ");

        // Thêm điều kiện lọc ngày nếu có
        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();
        
        if (hasStartDate) {
            sql.append("AND n.created_at >= ? ");
        }
        if (hasEndDate) {
            sql.append("AND n.created_at <= ? ");
        }

        sql.append("ORDER BY n.created_at ").append(orderBy).append(" ")
           .append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            ps.setInt(paramIndex++, receiverId);
            
            if (hasStartDate) {
                ps.setString(paramIndex++, startDate + " 00:00:00");
            }
            if (hasEndDate) {
                ps.setString(paramIndex++, endDate + " 23:59:59");
            }
            
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);
            
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setSenderId(rs.getInt("sender_id"));
                n.setReceiverId(rs.getInt("receiver_id"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setCreatedAt(rs.getTimestamp("created_at"));
                n.setIsRead(rs.getBoolean("is_read"));
                n.setEventId(rs.getInt("event_id"));
                n.setSenderName(rs.getString("sender_name"));
                n.setReceiverName(rs.getString("receiver_name"));
                n.setEventTitle(rs.getString("event_title"));
                list.add(n);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số thông báo
    public int getTotalNotifications(int receiverId) {
        String sql = "SELECT COUNT(*) FROM Notifications WHERE receiver_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, receiverId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Đếm tổng số thông báo với lọc ngày
    public int getTotalNotificationsWithDateFilter(int receiverId, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Notifications WHERE receiver_id = ? ");
        
        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();
        
        if (hasStartDate) {
            sql.append("AND created_at >= ? ");
        }
        if (hasEndDate) {
            sql.append("AND created_at <= ? ");
        }
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            ps.setInt(paramIndex++, receiverId);
            
            if (hasStartDate) {
                ps.setString(paramIndex++, startDate + " 00:00:00");
            }
            if (hasEndDate) {
                ps.setString(paramIndex++, endDate + " 23:59:59");
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
