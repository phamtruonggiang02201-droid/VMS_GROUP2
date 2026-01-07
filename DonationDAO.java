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
import model.Donation;
import utils.DBContext;

/**
 *
 * @author ADMIN
 */
public class DonationDAO {

    private Connection conn;

    public DonationDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Truy vấn tổng số tiền đã donate thành công (status = 'success').
    // Dùng để hiển thị tổng tiền quyên góp trên giao diện hoặc báo cáo.
    public double getTotalDonationAmount() {
        double total = 0;
        String sql = "select SUM(amount) as total_success from Donations where status = 'success'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

    // Lấy lịch sử donation của volunteer (dành cho tổ chức/quan sát viên).
    // Trả về danh sách `Donation` kèm thông tin event, username và fullname của volunteer.
    public List<Donation> getDonationHistoryByVolunteerId(int volunteerId) {
        List<Donation> list = new ArrayList<>();

        String sql = """
            SELECT
                d.id,
                d.event_id,
                d.volunteer_id,
                d.amount,
                d.donate_date,
                d.status,
                d.payment_method,
                d.payment_txn_ref,
                d.note,
                a.username AS volunteerUsername,
                u.full_name AS volunteerFullName,
                e.title AS eventTitle
            FROM Donations d
            LEFT JOIN Accounts a ON d.volunteer_id = a.id
            LEFT JOIN Users u ON a.id = u.account_id
            JOIN Events e ON d.event_id = e.id
            WHERE d.volunteer_id = ?
            ORDER BY d.donate_date DESC
        """;

        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Donation donation = new Donation(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("volunteer_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("donate_date") != null ? new java.util.Date(rs.getTimestamp("donate_date").getTime()) : null,
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("payment_txn_ref"),
                        rs.getString("note"),
                        rs.getString("volunteerUsername"),
                        rs.getString("volunteerFullName"),
                        rs.getString("eventTitle"),
                        0, // totalAmountDonated, có thể tính thêm nếu muốn
                        0 // numberOfEventsDonated, có thể tính thêm nếu muốn
                );
                list.add(donation);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Lấy chi tiết donation theo ID (bao gồm thông tin event, volunteer và organization).
    // Thường dùng để hiện chi tiết sau khi thanh toán hoàn tất hoặc trong trang quản lý.
    public Donation getDonationDetailById(int donationId) {
        String sql = """
            SELECT 
                d.id,
                d.event_id,
                d.volunteer_id,
                d.amount,
                d.donate_date,
                d.status,
                d.payment_method,
                d.qr_code,
                d.note,
                a_vol.username AS volunteerUsername,
                u_vol.full_name AS volunteerFullName,
                e.title AS eventTitle,
                e.description AS eventDescription,
                e.location AS eventLocation,
                e.start_date AS eventStartDate,
                e.end_date AS eventEndDate,
                c.name AS categoryName,
                a_org.username AS organizationUsername,
                u_org.full_name AS organizationName,
                u_org.email AS emailOrganization,
                u_org.phone AS phoneOrganization
            FROM Donations d
            JOIN Accounts a_vol ON d.volunteer_id = a_vol.id
            JOIN Users u_vol ON a_vol.id = u_vol.account_id
            JOIN Events e ON d.event_id = e.id
            LEFT JOIN Categories c ON e.category_id = c.category_id
            JOIN Accounts a_org ON e.organization_id = a_org.id
            JOIN Users u_org ON a_org.id = u_org.account_id
            WHERE d.id = ?
        """;

        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, donationId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Donation donation = new Donation(
                        rs.getInt("id"),
                        rs.getInt("event_id"),
                        rs.getInt("volunteer_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("donate_date") != null ? new java.util.Date(rs.getTimestamp("donate_date").getTime()) : null,
                        rs.getString("status"),
                        rs.getString("payment_method"),
                        rs.getString("qr_code"),
                        rs.getString("note"),
                        rs.getString("volunteerUsername"),
                        rs.getString("volunteerFullName"),
                        rs.getString("eventTitle"),
                        0,
                        0
                );

                // Set thông tin organization
                donation.setOrganizationName(rs.getString("organizationName"));
                donation.setEmailOrganization(rs.getString("emailOrganization"));
                donation.setPhoneOrganization(rs.getString("phoneOrganization"));

                return donation;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // lấy danh sách tổng số lượt đăng ký tham gia
    public int getTotalApply() {
        int total = 0;
        String sql = "SELECT COUNT(*) AS total_approved_volunteers\n"
                + "FROM Event_Volunteers\n"
                + "WHERE status = 'approved'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }

}
