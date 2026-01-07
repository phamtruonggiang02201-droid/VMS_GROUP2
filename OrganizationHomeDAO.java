/*
 * A friendly reminder to drink enough water
 */
package dao;

import java.sql.Connection;
import utils.DBContext;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Event;
import model.ProfileVolunteer;

/**
 *
 * @author Mirinae
 */
public class OrganizationHomeDAO extends DBContext {

    private Connection connection;

    public OrganizationHomeDAO() {
        try {
            DBContext db = new DBContext();
            this.connection = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Get total events created by organization

    public int getTotalEventsByOrganization(int organizationId) {
        String sql = "SELECT COUNT(*) as total FROM Events WHERE organization_id = ? AND status IN ('active','inactive','closed')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get total volunteers across all organization events
    public int getTotalVolunteersByOrganization(int organizationId) {
        String sql = "SELECT COUNT(DISTINCT ev.volunteer_id) as total "
                + "FROM Event_Volunteers ev "
                + "INNER JOIN Events e ON ev.event_id = e.id "
                + "WHERE e.organization_id = ? AND ev.status = 'approved'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get total donations for all events by organization
    public double getTotalDonationsByOrganization(int organizationId) {
        String sql = "SELECT ISNULL(SUM(d.amount), 0) as total "
                + "FROM Donations d "
                + "INNER JOIN Events e ON d.event_id = e.id "
                + "WHERE e.organization_id = ? AND d.status = 'success'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Get top 3 events with most donations by organization
    public List<Event> getTop3EventsByDonation(int organizationId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT TOP 3 id, title, total_donation "
                + "FROM Events "
                + "WHERE organization_id = ? AND total_donation > 0 "
                + "ORDER BY total_donation DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setTitle(rs.getString("title"));
                event.setTotalDonation(rs.getDouble("total_donation"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // Get top 3 donor volunteers by organization (summing donations across all events)
    public List<ProfileVolunteer> getTop3DonorVolunteers(int organizationId) {
        List<ProfileVolunteer> donors = new ArrayList<>();
        String sql = "SELECT TOP 3 u.full_name, ISNULL(SUM(d.amount), 0) as total_donated "
                + "FROM Donations d "
                + "INNER JOIN Events e ON d.event_id = e.id "
                + "INNER JOIN Accounts a ON d.volunteer_id = a.id "
                + "INNER JOIN Users u ON a.id = u.account_id "
                + "WHERE e.organization_id = ? AND d.status = 'success' "
                + "GROUP BY u.full_name "
                + "ORDER BY total_donated DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ProfileVolunteer donor = new ProfileVolunteer();
                donor.setFullName(rs.getString("full_name"));
                donor.setTotalDonated(rs.getDouble("total_donated"));
                donors.add(donor);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return donors;
    }

    // Get upcoming events for organization
    public List<Event> getUpcomingEvents(int organizationId) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, title, start_date, end_date, location, status "
                + "FROM Events "
                + "WHERE organization_id = ? AND start_date > GETDATE() "
                + "AND status = 'active' "
                + "ORDER BY start_date ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setTitle(rs.getString("title"));
                event.setStartDate(rs.getTimestamp("start_date"));
                event.setEndDate(rs.getTimestamp("end_date"));
                event.setLocation(rs.getString("location"));
                event.setStatus(rs.getString("status"));
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    // Get monthly donation data for organization (last 6 months)
    public List<Double> getMonthlyDonations(int organizationId) {
        List<Double> donations = new ArrayList<>();
        String sql = "SELECT ISNULL(SUM(d.amount), 0) as monthly_total "
                + "FROM Donations d "
                + "INNER JOIN Events e ON d.event_id = e.id "
                + "WHERE e.organization_id = ? "
                + "AND d.donate_date >= DATEADD(MONTH, -5, DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0)) "
                + "GROUP BY YEAR(d.donate_date), MONTH(d.donate_date) "
                + "ORDER BY YEAR(d.donate_date), MONTH(d.donate_date)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                donations.add(rs.getDouble("monthly_total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Fill with zeros if less than 6 months of data
        while (donations.size() < 6) {
            donations.add(0, 0.0);
        }
        return donations;
    }
}
