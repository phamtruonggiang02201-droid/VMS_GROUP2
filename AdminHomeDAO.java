/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Account;
import model.Event;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class AdminHomeDAO {

    private Connection conn;

    public AdminHomeDAO() {
        try {
            DBContext db = new DBContext();

            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // lấy tổng số tài khoản
    public int getTotalAccount() {
        String sql = "SELECT COUNT(*) AS total FROM Accounts";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // lấy tổng số tiền Donate
    public double getTotalMoneyDonate() {
        String sql = " SELECT \n"
                + "    SUM(amount) AS total_success_amount\n"
                + "FROM Donations\n"
                + "WHERE status = 'success';";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_success_amount");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // lấy top 3 sự kiện nhiều có tổng tiền donate nhiều nhất
    public List<Event> getTop3EventsMoneyDonate() {
        List<Event> list = new ArrayList<>();
        String sql = """ 
                     SELECT TOP 3 e.id, e.title, e.total_donation, u.full_name
                     FROM events e
                     JOIN [Users] u ON e.organization_id = u.id
                     ORDER BY e.total_donation DESC;
                     """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setTitle(rs.getString("title"));
                event.setTotalDonation(rs.getDouble("total_donation"));
                event.setOrganizationName(rs.getString("full_name"));
                list.add(event);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<Event> getTop3EventsComing() {
        List<Event> list = new ArrayList<>();
        String sql = """
        SELECT TOP 3 e.id, e.title, e.start_date, e.location, u.full_name AS organization_name
        FROM Events e
        JOIN Users u ON e.organization_id = u.id
        WHERE e.start_date > GETDATE()
        ORDER BY e.start_date ASC;
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Event event = new Event();
                event.setId(rs.getInt("id"));
                event.setTitle(rs.getString("title"));
                event.setStartDate(rs.getDate("start_date"));
                event.setLocation(rs.getString("location"));
                event.setOrganizationName(rs.getString("organization_name"));
                list.add(event);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    public Map<String, Integer> getAccountStatistics() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT role, COUNT(*) AS total FROM Accounts GROUP BY role";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                stats.put(rs.getString("role"), rs.getInt("total"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stats;
    }

    // Lấy tổng tiền donate theo 5 tháng gần nhất (status = success)
    // Luôn hiển thị đủ 5 tháng, nếu tháng nào không có donate thì = 0
    public Map<String, Double> getDonationByMonth() {
        Map<String, Double> monthlyDonations = new HashMap<>();
        String sql = """
            WITH Last5Months AS (
                -- Tạo bảng 5 tháng gần nhất
                SELECT FORMAT(DATEADD(MONTH, -n.number, GETDATE()), 'MM/yyyy') AS month_year,
                       YEAR(DATEADD(MONTH, -n.number, GETDATE())) AS year_val,
                       MONTH(DATEADD(MONTH, -n.number, GETDATE())) AS month_val
                FROM (SELECT 0 AS number UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) n
            )
            SELECT 
                m.month_year,
                ISNULL(SUM(d.amount), 0) AS total_amount
            FROM Last5Months m
            LEFT JOIN Donations d ON FORMAT(d.donate_date, 'MM/yyyy') = m.month_year 
                AND d.status = 'success'
            GROUP BY m.month_year, m.year_val, m.month_val
            ORDER BY m.year_val DESC, m.month_val DESC
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                monthlyDonations.put(rs.getString("month_year"), rs.getDouble("total_amount"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return monthlyDonations;
    }

    // Lấy top 3 người tài trợ nhiều nhất (tổng tiền donate)
    public List<model.Donation> getTop3Donors() {
        List<model.Donation> list = new ArrayList<>();
        String sql = """
            SELECT TOP 3
                d.volunteer_id,
                u.full_name AS volunteer_name,
                u.avatar AS volunteer_avatar,
                SUM(d.amount) AS total_donated
            FROM Donations d
            JOIN Accounts a ON d.volunteer_id = a.id
            JOIN Users u ON a.id = u.account_id
            WHERE d.status = 'success'
            GROUP BY d.volunteer_id, u.full_name, u.avatar
            ORDER BY total_donated DESC
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.Donation donor = new model.Donation();
                donor.setVolunteerId(rs.getInt("volunteer_id"));
                donor.setVolunteerFullName(rs.getString("volunteer_name"));
                donor.setVolunteerAvatar(rs.getString("volunteer_avatar"));
                donor.setTotalAmountDonated(rs.getDouble("total_donated"));
                list.add(donor);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Thống kê tài khoản theo role + status (active/inactive)
    public Map<String, Integer> getAllAccountStats() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = """
            SELECT 
                role,
                SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) AS active_count,
                SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) AS inactive_count,
                COUNT(*) AS total_count
            FROM Accounts
            GROUP BY role
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String role = rs.getString("role");
                stats.put(role + "_active", rs.getInt("active_count"));
                stats.put(role + "_inactive", rs.getInt("inactive_count"));
                stats.put(role + "_total", rs.getInt("total_count"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stats;
    }
}
