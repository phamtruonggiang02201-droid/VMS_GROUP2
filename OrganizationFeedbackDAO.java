package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Feedback;
import utils.DBContext;

public class OrganizationFeedbackDAO {

    public List<Feedback> findByOrganization(int organizationId, Integer eventId, Integer rating, String status, String eventTitleQuery) {
        List<Feedback> results = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT f.id, f.event_id, f.volunteer_id, f.rating, f.comment, f.feedback_date, f.status, ");
        sql.append("       e.title AS event_title, u.full_name AS volunteer_name, ou.full_name AS organization_name ");
        sql.append("FROM Feedback f ");
        sql.append("JOIN Events e ON e.id = f.event_id ");
        sql.append("JOIN Accounts va ON va.id = f.volunteer_id ");
        sql.append("JOIN Users u ON u.account_id = va.id ");
        sql.append("JOIN Accounts oa ON oa.id = e.organization_id ");
        sql.append("JOIN Users ou ON ou.account_id = oa.id ");
        sql.append("WHERE e.organization_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(organizationId);

        if (eventId != null) {
            sql.append(" AND f.event_id = ? ");
            params.add(eventId);
        }

        if (rating != null) {
            sql.append(" AND f.rating = ? ");
            params.add(rating);
        }
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND LOWER(f.status) = LOWER(?) ");
            params.add(status.trim());
        }
        if (eventTitleQuery != null && !eventTitleQuery.trim().isEmpty()) {
            sql.append(" AND e.title LIKE ? ");
            params.add("%" + eventTitleQuery.trim() + "%");
        }

        sql.append(" ORDER BY f.feedback_date DESC, f.id DESC");

        try (Connection con = DBContext.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Feedback f = new Feedback(
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
                    results.add(f);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying feedbacks", e);
        }

        return results;
    }

    public Feedback findByIdWithJoin(int feedbackId) {
        String sql = "SELECT f.id, f.event_id, f.volunteer_id, f.rating, f.comment, f.feedback_date, f.status, "
                   + "e.title AS event_title, u.full_name AS volunteer_name, ou.full_name AS organization_name "
                   + "FROM Feedback f "
                   + "JOIN Events e ON e.id = f.event_id "
                   + "JOIN Accounts va ON va.id = f.volunteer_id "
                   + "JOIN Users u ON u.account_id = va.id "
                   + "JOIN Accounts oa ON oa.id = e.organization_id "
                   + "JOIN Users ou ON ou.account_id = oa.id "
                   + "WHERE f.id = ?";

        try (Connection con = DBContext.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Feedback(
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
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching feedback by id", e);
        }
        return null;
    }

    public boolean updateFeedbackStatus(int feedbackId, String status) {
        String sql = "UPDATE Feedback SET status = ? WHERE id = ?";
        try (Connection con = DBContext.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, feedbackId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating feedback status", e);
        }
    }
}


