/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Feedback;
import utils.DBContext;

/**
 * DAO cho việc quản lý Feedback của Volunteer
 */
public class VolunteerFeedbackDAO {

    private Connection conn;

    public VolunteerFeedbackDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kiểm tra volunteer đã feedback cho event này chưa
 
    public Feedback getFeedbackByEventAndVolunteer(int eventId, int volunteerId) {
        String sql = "SELECT f.id, f.event_id, f.volunteer_id, f.rating, f.comment, "
                + "f.feedback_date, f.status, "
                + "e.title AS event_title, "
                + "u_vol.full_name AS volunteer_name, "
                + "u_org.full_name AS organization_name "
                + "FROM Feedback f "
                + "JOIN Events e ON f.event_id = e.id "
                + "JOIN Users u_vol ON f.volunteer_id = u_vol.account_id "
                + "JOIN Users u_org ON e.organization_id = u_org.account_id "
                + "WHERE f.event_id = ? AND f.volunteer_id = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, volunteerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Feedback(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("volunteer_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getDate("feedback_date"),
                        rs.getString("status"),
                        rs.getString("event_title"),
                        rs.getString("volunteer_name"),
                        rs.getString("organization_name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy thông tin event + org + volunteer để hiển thị form (khi chưa
     * feedback)
     *
     * @return Feedback object chứa thông tin cơ bản để hiển thị form
     */
    public Feedback getEventInfoForFeedback(int eventId, int volunteerId) {
        String sql = "SELECT e.id AS event_id, "
                + "e.title AS event_title, "
                + "u_vol.full_name AS volunteer_name, "
                + "u_org.full_name AS organization_name "
                + "FROM Events e "
                + "JOIN Users u_org ON e.organization_id = u_org.account_id "
                + "CROSS JOIN Users u_vol "
                + "WHERE e.id = ? AND u_vol.account_id = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, volunteerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Feedback feedback = new Feedback();
                feedback.setEventId(rs.getInt("event_id"));
                feedback.setVolunteerId(volunteerId);
                feedback.setEventTitle(rs.getString("event_title"));
                feedback.setVolunteerName(rs.getString("volunteer_name"));
                feedback.setOrganizationName(rs.getString("organization_name"));
                return feedback;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tạo feedback mới (status mặc định = 'valid')
     *
     * @return true nếu tạo thành công
     */
    public boolean createFeedback(Feedback feedback) {
        String sql = "INSERT INTO Feedback (event_id, volunteer_id, rating, comment, status) "
                + "VALUES (?, ?, ?, ?, 'valid')";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, feedback.getEventId());
            ps.setInt(2, feedback.getVolunteerId());
            ps.setInt(3, feedback.getRating());
            ps.setString(4, feedback.getComment());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật feedback (chỉ update rating và comment)
     *
     * @return true nếu cập nhật thành công
     */
    public boolean updateFeedback(Feedback feedback) {
        String sql = "UPDATE Feedback SET rating = ?, comment = ? "
                + "WHERE event_id = ? AND volunteer_id = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, feedback.getRating());
            ps.setString(2, feedback.getComment());
            ps.setInt(3, feedback.getEventId());
            ps.setInt(4, feedback.getVolunteerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


     // Kiểm tra volunteer có đủ điều kiện feedback không Trả về mã lỗi cụ thể để
     // hiển thị thông báo phù hợp

     // @return Được phép feedback = 0 | Chưa đăng ký sự kiện = 1 | Đơn chưa được : 2
     // duyệt (pending/rejected) 3 = Sự kiện chưa bắt đầu
     
    public int checkFeedbackEligibility(int eventId, int volunteerId) {
        String sql = "SELECT ev.status, e.start_date "
                + "FROM Event_Volunteers ev "
                + "JOIN Events e ON ev.event_id = e.id "
                + "WHERE ev.event_id = ? AND ev.volunteer_id = ? "
                + "ORDER BY ev.apply_date DESC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, volunteerId);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return 1; // Chưa đăng ký sự kiện
            }

            String status = rs.getString("status");
            if (!"approved".equals(status)) {
                return 2; // Đơn chưa được duyệt
            }

            java.sql.Timestamp startDate = rs.getTimestamp("start_date");
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

            if (now.before(startDate)) {
                return 3; // Sự kiện chưa bắt đầu (now < start_date)
            }

            return 0; // Được phép feedback (sự kiện đã bắt đầu hoặc đang diễn ra)

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Lỗi hệ thống
        }
    }

    /**
     * Kiểm tra volunteer có đủ điều kiện feedback không (phương thức cũ - giữ
     * lại để tương thích) Logic mới: Cho phép feedback khi sự kiện ĐÃ BẮT ĐẦU
     *
     * @return true nếu đủ điều kiện
     */
    public boolean canFeedback(int eventId, int volunteerId) {
        return checkFeedbackEligibility(eventId, volunteerId) == 0;
    }

    /**
     * Lấy danh sách feedback valid của một event
     *
     * @param eventId ID của event
     * @return Danh sách feedback có status = 'valid'
     */
    public List<Feedback> getValidFeedbacksByEventId(int eventId) {
        List<Feedback> feedbacks = new ArrayList<>();
        String sql = "SELECT f.id, f.event_id, f.volunteer_id, f.rating, f.comment, "
                + "f.feedback_date, f.status, "
                + "e.title AS event_title, "
                + "u_vol.full_name AS volunteer_name, "
                + "u_org.full_name AS organization_name "
                + "FROM Feedback f "
                + "JOIN Events e ON f.event_id = e.id "
                + "JOIN Users u_vol ON f.volunteer_id = u_vol.account_id "
                + "JOIN Users u_org ON e.organization_id = u_org.account_id "
                + "WHERE f.event_id = ? AND f.status = 'valid' "
                + "ORDER BY f.feedback_date DESC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("volunteer_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("feedback_date"),
                        rs.getString("status"),
                        rs.getString("event_title"),
                        rs.getString("volunteer_name"),
                        rs.getString("organization_name")
                );
                feedbacks.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbacks;
    }
    
    /**
     * Lấy danh sách feedback với phân trang
     *
     * @param eventId ID của event
     * @param page Trang hiện tại
     * @param pageSize Số lượng feedback mỗi trang
     * @return Danh sách feedback có status = 'valid'
     */
    public List<Feedback> getValidFeedbacksByEventIdPaged(int eventId, int page, int pageSize) {
        List<Feedback> feedbacks = new ArrayList<>();
        int offset = (page - 1) * pageSize;
        
        String sql = "SELECT f.id, f.event_id, f.volunteer_id, f.rating, f.comment, "
                + "f.feedback_date, f.status, "
                + "e.title AS event_title, "
                + "u_vol.full_name AS volunteer_name, "
                + "u_org.full_name AS organization_name "
                + "FROM Feedback f "
                + "JOIN Events e ON f.event_id = e.id "
                + "JOIN Users u_vol ON f.volunteer_id = u_vol.account_id "
                + "JOIN Users u_org ON e.organization_id = u_org.account_id "
                + "WHERE f.event_id = ? AND f.status = 'valid' "
                + "ORDER BY f.feedback_date DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, offset);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Feedback feedback = new Feedback(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("volunteer_id"),
                        rs.getInt("rating"),
                        rs.getString("comment"),
                        rs.getTimestamp("feedback_date"),
                        rs.getString("status"),
                        rs.getString("event_title"),
                        rs.getString("volunteer_name"),
                        rs.getString("organization_name")
                );
                feedbacks.add(feedback);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbacks;
    }
    
    /**
     * Đếm tổng số feedback valid của event
     *
     * @param eventId ID của event
     * @return Tổng số feedback
     */
    public int countValidFeedbacksByEventId(int eventId) {
        String sql = "SELECT COUNT(*) FROM Feedback WHERE event_id = ? AND status = 'valid'";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
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
