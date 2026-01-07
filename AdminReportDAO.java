/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Report;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class AdminReportDAO {

    private Connection connection;

    public AdminReportDAO() {
        try {
            connection = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy danh sách Reports có phân trang + filter + sort
    public List<Report> getAllReports(String statusFilter, String sortOrder, int page, int pageSize) {
        List<Report> list = new ArrayList<>();

        // Tính OFFSET
        int offset = (page - 1) * pageSize;

        // Build câu SQL với JOIN
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.id, r.feedback_id, r.organization_id, r.reason, r.status, r.created_at, ");
        sql.append("       u_org.full_name AS organization_name, ");
        sql.append("       a_vol.username AS volunteer_username, ");
        sql.append("       u_vol.full_name AS volunteer_name, ");
        sql.append("       f.rating, f.comment ");
        sql.append("FROM Reports r ");
        sql.append("JOIN Accounts a_org ON r.organization_id = a_org.id ");
        sql.append("JOIN Users u_org ON a_org.id = u_org.account_id ");
        sql.append("JOIN Feedback f ON r.feedback_id = f.id ");
        sql.append("JOIN Accounts a_vol ON f.volunteer_id = a_vol.id ");
        sql.append("JOIN Users u_vol ON a_vol.id = u_vol.account_id ");

        // Filter theo status
        if (statusFilter != null && !statusFilter.equals("all")) {
            sql.append("WHERE r.status = ? ");
        }

        // Sort theo thời gian
        if ("oldest".equals(sortOrder)) {
            sql.append("ORDER BY r.created_at ASC ");
        } else {
            sql.append("ORDER BY r.created_at DESC ");
        }

        // Phân trang (SQL Server)
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());

            int paramIndex = 1;

            // Set parameter cho filter
            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(paramIndex++, statusFilter);
            }

            // Set parameter cho phân trang
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Report report = new Report(
                        rs.getInt("id"),
                        rs.getInt("feedback_id"),
                        rs.getInt("organization_id"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getString("organization_name"),
                        rs.getString("volunteer_username"),
                        rs.getString("volunteer_name"),
                        rs.getString("comment"),
                        rs.getInt("rating")
                );
                list.add(report);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Đếm tổng số Reports (để tính số trang)
    public int getTotalReports(String statusFilter) {
        String sql = "SELECT COUNT(*) AS total FROM Reports";

        if (statusFilter != null && !statusFilter.equals("all")) {
            sql += " WHERE status = ?";
        }

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(1, statusFilter);
            }

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Lấy chi tiết 1 Report theo ID
    public Report getReportById(int id) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT r.id, r.feedback_id, r.organization_id, r.reason, r.status, r.created_at, ");
        sql.append("       u_org.full_name AS organization_name, ");
        sql.append("       a_vol.username AS volunteer_username, ");
        sql.append("       u_vol.full_name AS volunteer_name, ");
        sql.append("       f.rating, f.comment ");
        sql.append("FROM Reports r ");
        sql.append("JOIN Accounts a_org ON r.organization_id = a_org.id ");
        sql.append("JOIN Users u_org ON a_org.id = u_org.account_id ");
        sql.append("JOIN Feedback f ON r.feedback_id = f.id ");
        sql.append("JOIN Accounts a_vol ON f.volunteer_id = a_vol.id ");
        sql.append("JOIN Users u_vol ON a_vol.id = u_vol.account_id ");
        sql.append("WHERE r.id = ?");

        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Report(
                        rs.getInt("id"),
                        rs.getInt("feedback_id"),
                        rs.getInt("organization_id"),
                        rs.getString("reason"),
                        rs.getString("status"),
                        rs.getTimestamp("created_at"),
                        rs.getString("organization_name"),
                        rs.getString("volunteer_username"),
                        rs.getString("volunteer_name"),
                        rs.getString("comment"),
                        rs.getInt("rating")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Cập nhật trạng thái Report

    public boolean updateReportStatus(int id, String newStatus) {
        String sql = "UPDATE Reports SET status = ? WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Khóa tài khoản Volunteer (set status = 0)

    public boolean lockVolunteerAccount(int volunteerId) {
        String sql = "UPDATE Accounts SET status = 0 WHERE id = ? AND role = 'volunteer'";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, volunteerId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy volunteer_id từ feedback_id (để có thể khóa tài khoản)

    public int getVolunteerIdByFeedbackId(int feedbackId) {
        String sql = "SELECT volunteer_id FROM Feedback WHERE id = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, feedbackId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("volunteer_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
