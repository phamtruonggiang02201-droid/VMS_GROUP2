
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Attendance;
import utils.DBContext;

public class AttendanceDAO {

    private Connection connection;

    public AttendanceDAO() {
        try {
            connection = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    public List<Attendance> getAttendanceHistoryByVolunteerId(int volunteerId) {
        List<Attendance> list = new ArrayList<>();

        String sql = """
            SELECT 
                ev.volunteer_id,
                v.full_name AS volunteerName,
                COALESCE(a.status, 'pending') AS status,
                e.title AS eventTitle,
                o.full_name AS organizationName,
                e.start_date,
                e.end_date
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            JOIN Users v ON ev.volunteer_id = v.account_id
            JOIN Users o ON e.organization_id = o.account_id
            LEFT JOIN Attendance a ON a.event_id = ev.event_id AND a.volunteer_id = ev.volunteer_id
            WHERE ev.volunteer_id = ? AND ev.status = 'approved'
            ORDER BY e.start_date DESC
        """;

        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Attendance att = new Attendance(
                        rs.getInt("volunteer_id"),
                        rs.getString("volunteerName"),
                        rs.getString("status"),
                        rs.getString("eventTitle"),
                        rs.getString("organizationName"),
                        new java.util.Date(rs.getTimestamp("start_date").getTime()),
                        new java.util.Date(rs.getTimestamp("end_date").getTime())
                );
                list.add(att);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Phân trang với filter status
    public List<Attendance> getAttendanceHistoryPaginated(int volunteerId, int page, int pageSize, String statusFilter) {
        List<Attendance> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        StringBuilder sql = new StringBuilder("""
            SELECT 
                ev.volunteer_id,
                v.full_name AS volunteerName,
                COALESCE(a.status, 'pending') AS status,
                e.title AS eventTitle,
                o.full_name AS organizationName,
                e.start_date,
                e.end_date
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            JOIN Users v ON ev.volunteer_id = v.account_id
            JOIN Users o ON e.organization_id = o.account_id
            LEFT JOIN Attendance a ON a.event_id = ev.event_id AND a.volunteer_id = ev.volunteer_id
            WHERE ev.volunteer_id = ? AND ev.status = 'approved'
        """);
        
        // Thêm filter status nếu có
        if (statusFilter != null && !statusFilter.equals("all")) {
            sql.append(" AND COALESCE(a.status, 'pending') = ?");
        }
        
        sql.append(" ORDER BY e.start_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            ps.setInt(paramIndex++, volunteerId);
            
            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(paramIndex++, statusFilter);
            }
            
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Attendance att = new Attendance(
                        rs.getInt("volunteer_id"),
                        rs.getString("volunteerName"),
                        rs.getString("status"),
                        rs.getString("eventTitle"),
                        rs.getString("organizationName"),
                        new java.util.Date(rs.getTimestamp("start_date").getTime()),
                        new java.util.Date(rs.getTimestamp("end_date").getTime())
                );
                list.add(att);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // Đếm tổng số attendance với filter
    public int getTotalAttendanceByVolunteer(int volunteerId, String statusFilter) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            LEFT JOIN Attendance a ON a.event_id = ev.event_id AND a.volunteer_id = ev.volunteer_id
            WHERE ev.volunteer_id = ? AND ev.status = 'approved'
        """);
        
        if (statusFilter != null && !statusFilter.equals("all")) {
            sql.append(" AND COALESCE(a.status, 'pending') = ?");
        }
        
        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            ps.setInt(paramIndex++, volunteerId);
            
            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(paramIndex++, statusFilter);
            }
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    // Lấy danh sách volunteer đã được approved để điểm danh Có thể filter theo
    // status (pending/present/absent)
    public List<Attendance> getVolunteersForAttendance(int eventId, String statusFilter) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT "
                + "    acc.id AS volunteer_id, "
                + "    u.full_name AS volunteer_name, "
                + "    u.email, "
                + "    u.phone, "
                + "    COALESCE(a.status, 'pending') AS attendance_status "
                + "FROM Event_Volunteers ev "
                + "JOIN Accounts acc ON ev.volunteer_id = acc.id "
                + "JOIN Users u ON acc.id = u.account_id "
                + "LEFT JOIN Attendance a "
                + "    ON a.event_id = ev.event_id "
                + "   AND a.volunteer_id = ev.volunteer_id "
                + "WHERE ev.event_id = ? "
                + "  AND ev.status = 'approved'";

        // Thêm filter nếu không phải "all"
        if (statusFilter != null && !statusFilter.equals("all")) {
            sql += " AND COALESCE(a.status, 'pending') = ?";
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, eventId);

            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(2, statusFilter);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Attendance att = new Attendance(
                        eventId,
                        rs.getInt("volunteer_id"),
                        rs.getString("volunteer_name"),
                        rs.getString("attendance_status"),
                        rs.getString("email"),
                        rs.getString("phone")
                );
                list.add(att);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Cập nhật trạng thái điểm danh
    // Nếu chưa có record thì INSERT, có rồi thì UPDATE
    public boolean updateAttendanceStatus(int eventId, int volunteerId, String status) {
        // Kiểm tra xem đã có record chưa
        String checkSql = "SELECT 1 FROM Attendance WHERE event_id = ? AND volunteer_id = ?";
        String insertSql = "INSERT INTO Attendance (event_id, volunteer_id, status) VALUES (?, ?, ?)";
        String updateSql = "UPDATE Attendance SET status = ? WHERE event_id = ? AND volunteer_id = ?";

        try {
            PreparedStatement checkPs = connection.prepareStatement(checkSql);
            checkPs.setInt(1, eventId);
            checkPs.setInt(2, volunteerId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // Đã có => UPDATE
                PreparedStatement updatePs = connection.prepareStatement(updateSql);
                updatePs.setString(1, status);
                updatePs.setInt(2, eventId);
                updatePs.setInt(3, volunteerId);
                return updatePs.executeUpdate() > 0;
            } else {
                // Chưa có => INSERT
                PreparedStatement insertPs = connection.prepareStatement(insertSql);
                insertPs.setInt(1, eventId);
                insertPs.setInt(2, volunteerId);
                insertPs.setString(3, status);
                return insertPs.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Validate 1: Kiểm tra sự kiện đã bắt đầu chưa (start_date <= ngày giờ hiện tại)
    public boolean isEventStarted(int eventId) {
        String sql = "SELECT COUNT(*) FROM Events WHERE id = ? AND start_date <= GETDATE()";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
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
    
    // Validate 2: Kiểm tra sự kiện đã kết thúc quá 24h chưa
    public boolean isEventEndedOver24Hours(int eventId) {
        String sql = "SELECT COUNT(*) FROM Events WHERE id = ? AND DATEDIFF(HOUR, end_date, GETDATE()) > 24";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
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
    
    // Tự động mark absent cho sự kiện đã kết thúc (dùng chung cho cả Organization & Volunteer)
    // Có thể truyền eventId (1 event) hoặc null (tất cả events của volunteer)
    public int autoMarkAbsentForEndedEvents(Integer eventId, Integer volunteerId) {
        int totalUpdated = 0;
        
        // Bước 1: INSERT record 'absent' cho các sự kiện đã kết thúc mà chưa có trong Attendance
        StringBuilder insertSql = new StringBuilder("""
            INSERT INTO Attendance (event_id, volunteer_id, status)
            SELECT ev.event_id, ev.volunteer_id, 'absent'
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            WHERE ev.status = 'approved'
              AND e.end_date < GETDATE()
              AND NOT EXISTS (
                  SELECT 1 FROM Attendance a 
                  WHERE a.event_id = ev.event_id 
                    AND a.volunteer_id = ev.volunteer_id
              )
        """);
        
        // Bước 2: UPDATE các record 'pending' thành 'absent' cho sự kiện đã kết thúc
        StringBuilder updateSql = new StringBuilder("""
            UPDATE Attendance 
            SET status = 'absent' 
            WHERE status = 'pending' 
              AND event_id IN (
                  SELECT e.id 
                  FROM Events e 
                  WHERE e.end_date < GETDATE()
              )
        """);
        
        try {
            // Thêm điều kiện filter theo eventId hoặc volunteerId
            if (eventId != null) {
                insertSql.append(" AND ev.event_id = ?");
                updateSql.append(" AND event_id = ?");
            }
            if (volunteerId != null) {
                insertSql.append(" AND ev.volunteer_id = ?");
                updateSql.append(" AND volunteer_id = ?");
            }
            
            // Thực hiện INSERT
            PreparedStatement psInsert = connection.prepareStatement(insertSql.toString());
            int paramIndex = 1;
            if (eventId != null) psInsert.setInt(paramIndex++, eventId);
            if (volunteerId != null) psInsert.setInt(paramIndex++, volunteerId);
            totalUpdated += psInsert.executeUpdate();
            
            // Thực hiện UPDATE
            PreparedStatement psUpdate = connection.prepareStatement(updateSql.toString());
            paramIndex = 1;
            if (eventId != null) psUpdate.setInt(paramIndex++, eventId);
            if (volunteerId != null) psUpdate.setInt(paramIndex++, volunteerId);
            totalUpdated += psUpdate.executeUpdate();
            
            return totalUpdated;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
