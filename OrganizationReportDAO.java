package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utils.DBContext;

public class OrganizationReportDAO {

    public boolean insertPendingReport(int feedbackId, int organizationAccountId, String reason) {
        String sql = "INSERT INTO Reports (feedback_id, organization_id, reason, status) VALUES (?, ?, ?, 'pending')";
        try (Connection con = DBContext.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, feedbackId);
            ps.setInt(2, organizationAccountId);
            ps.setString(3, reason);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting pending report", e);
        }
    }
}

// ok em


