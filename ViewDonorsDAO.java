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
import model.Event;
import utils.DBContext;

/**
 *
 * @author ADDMIN
 */
public class ViewDonorsDAO {

    private Connection conn;

    public ViewDonorsDAO() {
        try {
            this.conn = DBContext.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // lấy danh sách top 3 người donate nhiều nhất
    public List<Donation> getTop3UserDonation() {
        List<Donation> list = new ArrayList<>();

        String sql = """
                WITH TotalDonations AS (
                    SELECT 
                        d.volunteer_id,
                        SUM(d.amount) AS total_amount,
                        COUNT(DISTINCT d.event_id) AS events_count
                    FROM Donations d where d.status = 'success'
                    GROUP BY d.volunteer_id
                ),
                LatestDonation AS (
                    SELECT
                        d.id,
                        d.volunteer_id,
                        d.event_id,
                        d.amount AS donate_amount,
                        d.donate_date,
                        d.status AS donation_status,
                        d.payment_method,
                        d.payment_txn_ref,
                        d.note,
                        ROW_NUMBER() OVER (PARTITION BY d.volunteer_id ORDER BY d.donate_date DESC) AS rn
                    FROM Donations d
                )
                SELECT TOP 3
                    ld.id AS donation_id,
                    ld.volunteer_id,
                    ld.event_id,
                    u.full_name AS volunteer_name,
                    u.avatar AS volunteer_avatar,
                    a.username AS volunteer_username,
                    td.total_amount,
                    td.events_count,
                    e.title AS event_title,
                    ld.donate_amount,
                    ld.donate_date,
                    ld.donation_status,
                    ld.payment_method,
                    ld.payment_txn_ref,
                    ld.note
                FROM TotalDonations td
                JOIN LatestDonation ld 
                    ON td.volunteer_id = ld.volunteer_id AND ld.rn = 1  
                JOIN Events e ON ld.event_id = e.id
                JOIN Accounts a ON td.volunteer_id = a.id
                JOIN Users u ON a.id = u.account_id
                ORDER BY td.total_amount DESC;
             """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Donation d = new Donation(
                        rs.getInt("donation_id"), // donation_id
                        rs.getInt("event_id"), // eventId
                        rs.getInt("volunteer_id"), // volunteerId
                        rs.getDouble("donate_amount"), // amount
                        rs.getTimestamp("donate_date"),
                        rs.getString("donation_status"),
                        rs.getString("payment_method"),
                        rs.getString("payment_txn_ref"),
                        rs.getString("note"),
                        rs.getString("volunteer_username"),
                        rs.getString("volunteer_name"),
                        rs.getString("volunteer_avatar"),
                        rs.getString("event_title"),
                        rs.getDouble("total_amount"),
                        rs.getInt("events_count")
                );

                list.add(d);
            }
            System.out.println("==> getTop3UserDonation() trả về: " + list.size() + " bản ghi");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    // lấy danh sách cá nhân volunteer donate để xem lịch sử 
    // Thay thế phương thức getUserDonationsPaged() trong ViewDonorsDAO.java

    public List<Donation> getUserDonationsPaged(int volunteerId, int pageIndex, int pageSize) {
        List<Donation> list = new ArrayList<>();

        String sql = """
    SELECT
        d.id AS donation_id,
        d.volunteer_id,
        d.event_id,
        d.amount AS donate_amount,
        d.donate_date,
        d.status AS donation_status,
        d.payment_method,
        d.payment_txn_ref,
        d.note,
        u.full_name AS volunteer_name,
        u.avatar AS volunteer_avatar,
        a.username AS volunteer_username,
        e.title AS event_title,
        (SELECT SUM(amount) FROM Donations WHERE volunteer_id = ? AND status = 'success') AS total_amount,
        (SELECT COUNT(DISTINCT event_id) FROM Donations WHERE volunteer_id = ?) AS events_count
    FROM Donations d
    JOIN Events e ON d.event_id = e.id
    JOIN Accounts a ON d.volunteer_id = a.id
    JOIN Users u ON a.id = u.account_id
    WHERE d.volunteer_id = ?
    ORDER BY d.donate_date DESC
    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, volunteerId);  // cho subquery total_amount
            ps.setInt(2, volunteerId);  // cho subquery events_count
            ps.setInt(3, volunteerId);  // cho WHERE chính
            ps.setInt(4, (pageIndex - 1) * pageSize);  // OFFSET
            ps.setInt(5, pageSize);  // FETCH NEXT

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donation d = new Donation(
                            rs.getInt("donation_id"),
                            rs.getInt("event_id"),
                            rs.getInt("volunteer_id"),
                            rs.getDouble("donate_amount"),
                            rs.getTimestamp("donate_date"),
                            rs.getString("donation_status"),
                            rs.getString("payment_method"),
                            rs.getString("payment_txn_ref"),
                            rs.getString("note"),
                            rs.getString("volunteer_username"),
                            rs.getString("volunteer_name"),
                            rs.getString("volunteer_avatar"),
                            rs.getString("event_title"),
                            rs.getDouble("total_amount"),
                            rs.getInt("events_count")
                    );
                    list.add(d);
                }
            }

            System.out.println("==> getUserDonationsPaged() trả về: " + list.size() + " bản ghi (Page: " + pageIndex + ")");

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    // lấy danh sách những người donate + số sự kiện họ donate
    public List<Donation> getAllUserDonation() {
        List<Donation> list = new ArrayList<>();
        String sql = """
                      WITH TotalDonations AS (
                                         SELECT 
                                             d.volunteer_id,
                                             SUM(d.amount) AS total_amount,
                                             COUNT(DISTINCT d.event_id) AS events_count
                                         FROM Donations d where d.status = 'success'
                                         GROUP BY d.volunteer_id
                                     ),
                                     LatestDonation AS (
                                         SELECT
                                             d.id,
                                             d.volunteer_id,
                                             d.event_id,
                                             d.amount AS donate_amount,
                                             d.donate_date,
                                             d.status AS donation_status,
                                             d.payment_method,
                                             d.note,
                                             d.payment_txn_ref,
                                             ROW_NUMBER() OVER (PARTITION BY d.volunteer_id ORDER BY d.donate_date DESC) AS rn
                                         FROM Donations d
                                     )
                                     SELECT 
                                         ld.id AS donation_id,
                                         ld.volunteer_id,
                                         ld.event_id,
                                         u.full_name AS volunteer_name,
                                         u.avatar AS volunteer_avatar,
                                         a.username AS volunteer_username,
                                         td.total_amount,
                                         td.events_count,
                                         e.title AS event_title,       
                                         ld.donate_amount,
                                         ld.donate_date,
                                         ld.donation_status,
                                         ld.payment_method,
                                         ld.note,
                                         ld.payment_txn_ref
                                     FROM TotalDonations td
                                     JOIN LatestDonation ld 
                                         ON td.volunteer_id = ld.volunteer_id AND ld.rn = 1  
                                     JOIN Events e ON ld.event_id = e.id
                                     JOIN Accounts a ON td.volunteer_id = a.id
                                     JOIN Users u ON a.id = u.account_id
                                     ORDER BY td.total_amount DESC;
                     """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Donation d = new Donation(
                        rs.getInt("donation_id"), // donation_id
                        rs.getInt("event_id"), // eventId
                        rs.getInt("volunteer_id"), // volunteerId
                        rs.getDouble("donate_amount"), // amount
                        rs.getTimestamp("donate_date"),
                        rs.getString("donation_status"),
                        rs.getString("payment_method"),
                        rs.getString("payment_txn_ref"),
                        rs.getString("note"),
                        rs.getString("volunteer_username"),
                        rs.getString("volunteer_name"),
                        rs.getString("volunteer_avatar"),
                        rs.getString("event_title"),
                        rs.getDouble("total_amount"),
                        rs.getInt("events_count")
                );

                list.add(d);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // lấy danh sách các nhà donate + phân trang
    public List<Donation> getDonorsPaged(int offset, int limit) {
        List<Donation> list = new ArrayList<>();
        String sql = """
                      WITH TotalDonations AS (
                              SELECT 
                                  d.volunteer_id,
                                  SUM(d.amount) AS total_amount,
                                  COUNT(DISTINCT d.event_id) AS events_count
                              FROM Donations d 
                              WHERE d.status = 'success'
                              GROUP BY d.volunteer_id
                          ),
                          LatestDonation AS (
                              SELECT
                                  d.id,
                                  d.volunteer_id,
                                  d.event_id,
                                  d.amount AS donate_amount,
                                  d.donate_date,
                                  d.status AS donation_status,
                                  d.payment_method,
                                  d.payment_txn_ref,
                                  d.note,
                                  ROW_NUMBER() OVER (
                                      PARTITION BY d.volunteer_id 
                                      ORDER BY d.donate_date DESC
                                  ) AS rn
                              FROM Donations d
                              WHERE d.status = 'success'
                          )
                          SELECT 
                              ld.id AS donation_id,
                              ld.volunteer_id,
                              ld.event_id,
                              u.full_name AS volunteer_name,
                              u.avatar AS volunteer_avatar,
                              a.username AS volunteer_username,
                              td.total_amount,
                              td.events_count,
                              e.title AS event_title,       
                              ld.donate_amount,
                              ld.donate_date,
                              ld.donation_status,
                              ld.payment_method,
                              ld.payment_txn_ref,
                              ld.note
                          FROM TotalDonations td
                          JOIN LatestDonation ld 
                              ON td.volunteer_id = ld.volunteer_id AND ld.rn = 1  
                          JOIN Events e ON ld.event_id = e.id
                          JOIN Accounts a ON td.volunteer_id = a.id
                          JOIN Users u ON a.id = u.account_id
                          ORDER BY td.total_amount DESC
                          OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                     """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donation d = new Donation(
                            rs.getInt("donation_id"),
                            rs.getInt("event_id"),
                            rs.getInt("volunteer_id"),
                            rs.getDouble("donate_amount"),
                            rs.getTimestamp("donate_date"),
                            rs.getString("donation_status"),
                            rs.getString("payment_method"),
                            rs.getString("payment_txn_ref"),
                            rs.getString("note"),
                            rs.getString("volunteer_username"),
                            rs.getString("volunteer_name"),
                            rs.getString("volunteer_avatar"),
                            rs.getString("event_title"),
                            rs.getDouble("total_amount"),
                            rs.getInt("events_count")
                    );
                    list.add(d);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // tổng số đơn donate cho mỗi cá nhân volunteer để phân trang
    public int getTotalDonationsByVolunteer(int volunteerId) {
        String sql = "SELECT COUNT(*) FROM Donations WHERE volunteer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, volunteerId);
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

    //tính tổng số donors đã đăng để chia trang
    public int getTotalDonors() {
        String sql = "SELECT COUNT(*) FROM ( "
                + "  SELECT volunteer_id "
                + "  FROM Donations "
                + "  WHERE status = 'success' "
                + "  GROUP BY volunteer_id "
                + ") AS t";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Phân trang với filter theo ngày cho volunteer donation history
    public List<Donation> getUserDonationsPagedWithDateFilter(int volunteerId, int page, int pageSize, String startDate, String endDate) {
        List<Donation> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder("""
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
                a.username AS volunteer_username,
                u.full_name AS volunteer_full_name,
                e.title AS event_title
            FROM Donations d
            JOIN Accounts a ON d.volunteer_id = a.id
            JOIN Users u ON a.id = u.account_id
            JOIN Events e ON d.event_id = e.id
            WHERE d.volunteer_id = ?
        """);

        // Thêm điều kiện filter ngày
        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();

        if (hasStartDate) {
            sql.append(" AND d.donate_date >= ?");
        }
        if (hasEndDate) {
            sql.append(" AND d.donate_date <= ?");
        }

        sql.append(" ORDER BY d.donate_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, volunteerId);

            if (hasStartDate) {
                ps.setString(paramIndex++, startDate + " 00:00:00");
            }
            if (hasEndDate) {
                ps.setString(paramIndex++, endDate + " 23:59:59");
            }

            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donation d = new Donation(
                            rs.getInt("id"),
                            rs.getInt("event_id"),
                            rs.getInt("volunteer_id"),
                            rs.getDouble("amount"),
                            rs.getTimestamp("donate_date"),
                            rs.getString("status"),
                            rs.getString("payment_method"),
                            rs.getString("payment_txn_ref"),
                            rs.getString("note"),
                            rs.getString("volunteer_username"),
                            rs.getString("volunteer_full_name"),
                            rs.getString("event_title"),
                            0,
                            0
                    );
                    list.add(d);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số donations với filter ngày
    public int getTotalDonationsByVolunteerWithDateFilter(int volunteerId, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Donations WHERE volunteer_id = ?");

        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();

        if (hasStartDate) {
            sql.append(" AND donate_date >= ?");
        }
        if (hasEndDate) {
            sql.append(" AND donate_date <= ?");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, volunteerId);

            if (hasStartDate) {
                ps.setString(paramIndex++, startDate + " 00:00:00");
            }
            if (hasEndDate) {
                ps.setString(paramIndex++, endDate + " 23:59:59");
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

    // Lọc donations với filter đầy đụ (ngày + trạng thái)
    public List<Donation> getUserDonationsWithAllFilters(int volunteerId, int page, int pageSize,
            String startDate, String endDate, String status) {
        List<Donation> list = new ArrayList<>();
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder("""
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
                a.username AS volunteer_username,
                u.full_name AS volunteer_full_name,
                e.title AS event_title
            FROM Donations d
            JOIN Accounts a ON d.volunteer_id = a.id
            JOIN Users u ON a.id = u.account_id
            JOIN Events e ON d.event_id = e.id
            WHERE d.volunteer_id = ?
        """);

        // Thêm điều kiện filter ngày
        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty() && !"all".equals(status);

        if (hasStartDate) {
            sql.append(" AND d.donate_date >= ?");
        }
        if (hasEndDate) {
            sql.append(" AND d.donate_date <= ?");
        }
        if (hasStatus) {
            sql.append(" AND d.status = ?");
        }

        sql.append(" ORDER BY d.donate_date DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, volunteerId);

            if (hasStartDate) {
                ps.setString(paramIndex++, startDate + " 00:00:00");
            }
            if (hasEndDate) {
                ps.setString(paramIndex++, endDate + " 23:59:59");
            }
            if (hasStatus) {
                ps.setString(paramIndex++, status);
            }

            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Donation d = new Donation(
                            rs.getInt("id"),
                            rs.getInt("event_id"),
                            rs.getInt("volunteer_id"),
                            rs.getDouble("amount"),
                            rs.getTimestamp("donate_date"),
                            rs.getString("status"),
                            rs.getString("payment_method"),
                            rs.getString("payment_txn_ref"),
                            rs.getString("note"),
                            rs.getString("volunteer_username"),
                            rs.getString("volunteer_full_name"),
                            rs.getString("event_title"),
                            0,
                            0
                    );
                    list.add(d);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Đếm tổng số donations với filter đầy đủ
    public int getTotalDonationsWithAllFilters(int volunteerId, String startDate, String endDate, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Donations WHERE volunteer_id = ?");

        boolean hasStartDate = startDate != null && !startDate.trim().isEmpty();
        boolean hasEndDate = endDate != null && !endDate.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty() && !"all".equals(status);

        if (hasStartDate) {
            sql.append(" AND donate_date >= ?");
        }
        if (hasEndDate) {
            sql.append(" AND donate_date <= ?");
        }
        if (hasStatus) {
            sql.append(" AND status = ?");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, volunteerId);

            if (hasStartDate) {
                ps.setString(paramIndex++, startDate + " 00:00:00");
            }
            if (hasEndDate) {
                ps.setString(paramIndex++, endDate + " 23:59:59");
            }
            if (hasStatus) {
                ps.setString(paramIndex++, status);
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

    // Lấy chi tiết donation theo ID và volunteer ID (để đảm bảo volunteer chỉ xem được donation của mình)
    public Donation getDonationById(int donationId, int volunteerId) {
        String sql = """
            SELECT
                d.id AS donation_id,
                d.volunteer_id,
                d.event_id,
                d.amount AS donate_amount,
                d.donate_date,
                d.status AS donation_status,
                d.payment_method,
                d.payment_txn_ref,
                d.note,
                u.full_name AS volunteer_name,
                u.avatar AS volunteer_avatar,
                a.username AS volunteer_username,
                e.title AS event_title,
                u_org.full_name AS organization_name,
                u_org.email AS email_organization,
                u_org.phone AS phone_organization,
                (SELECT SUM(amount) FROM Donations WHERE volunteer_id = ? AND status = 'success') AS total_amount,
                (SELECT COUNT(DISTINCT event_id) FROM Donations WHERE volunteer_id = ?) AS events_count
            FROM Donations d
            JOIN Events e ON d.event_id = e.id
            JOIN Accounts a ON d.volunteer_id = a.id
            JOIN Users u ON a.id = u.account_id
            LEFT JOIN Accounts a_org ON e.organization_id = a_org.id
            LEFT JOIN Users u_org ON a_org.id = u_org.account_id
            WHERE d.id = ? AND d.volunteer_id = ?
        """;

        System.out.println("==> getDonationById() - DonationId: " + donationId + ", VolunteerId: " + volunteerId);

        // Sử dụng connection mới để đảm bảo connection không bị đóng
        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, volunteerId);  // cho subquery total_amount
            ps.setInt(2, volunteerId);  // cho subquery events_count
            ps.setInt(3, donationId);   // cho WHERE d.id = ?
            ps.setInt(4, volunteerId);  // cho WHERE d.volunteer_id = ?

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("==> getDonationById() - Tìm thấy donation!");
                    Donation donation = new Donation(
                            rs.getInt("donation_id"),
                            rs.getInt("event_id"),
                            rs.getInt("volunteer_id"),
                            rs.getDouble("donate_amount"),
                            rs.getTimestamp("donate_date"),
                            rs.getString("donation_status"),
                            rs.getString("payment_method"),
                            rs.getString("payment_txn_ref"),
                            rs.getString("note"),
                            rs.getString("volunteer_username"),
                            rs.getString("volunteer_name"),
                            rs.getString("volunteer_avatar"),
                            rs.getString("event_title"),
                            rs.getDouble("total_amount"),
                            rs.getInt("events_count")
                    );
                    // Set organization information
                    donation.setOrganizationName(rs.getString("organization_name"));
                    donation.setEmailOrganization(rs.getString("email_organization"));
                    donation.setPhoneOrganization(rs.getString("phone_organization"));
                    return donation;
                } else {
                    System.out.println("==> getDonationById() - KHÔNG tìm thấy donation!");
                }
            }
        } catch (Exception ex) {
            System.err.println("==> getDonationById() - Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    // Lấy chi tiết donation theo ID và event ID (for organization to view donations in their events)
    public Donation getDonationByIdForOrganization(int donationId, int eventId) {
        String sql = """
            SELECT
                d.id AS donation_id,
                d.volunteer_id,
                d.event_id,
                d.donor_id,
                d.amount AS donate_amount,
                d.donate_date,
                d.status AS donation_status,
                d.payment_method,
                d.payment_txn_ref,
                d.note,
                u.full_name AS volunteer_name,
                u.avatar AS volunteer_avatar,
                a.username AS volunteer_username,
                u.email AS volunteer_email,
                u.phone AS volunteer_phone,
                e.title AS event_title,
                dn.donor_type,
                dn.full_name AS donor_full_name,
                dn.phone AS donor_phone,
                dn.email AS donor_email,
                dn.is_anonymous
            FROM Donations d
            JOIN Events e ON d.event_id = e.id
            LEFT JOIN Accounts a ON d.volunteer_id = a.id
            LEFT JOIN Users u ON a.id = u.account_id
            LEFT JOIN Donors dn ON d.donor_id = dn.id
            WHERE d.id = ? AND d.event_id = ? AND d.status = 'success'
        """;

        System.out.println("==> getDonationByIdForOrganization() - DonationId: " + donationId + ", EventId: " + eventId);

        try (Connection connection = DBContext.getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, donationId);
            ps.setInt(2, eventId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("==> getDonationByIdForOrganization() - Tìm thấy donation!");
                    Donation donation = new Donation(
                            rs.getInt("donation_id"),
                            rs.getInt("event_id"),
                            rs.getInt("volunteer_id"),
                            rs.getDouble("donate_amount"),
                            rs.getTimestamp("donate_date"),
                            rs.getString("donation_status"),
                            rs.getString("payment_method"),
                            rs.getString("payment_txn_ref"),
                            rs.getString("note"),
                            rs.getString("volunteer_username"),
                            rs.getString("volunteer_name"),
                            rs.getString("volunteer_avatar"),
                            rs.getString("event_title"),
                            0, // total_amount not needed for this view
                            0 // events_count not needed for this view
                    );
                    donation.setVolunteerEmail(rs.getString("volunteer_email"));
                    donation.setVolunteerPhone(rs.getString("volunteer_phone"));
                    Object donorIdObj = rs.getObject("donor_id");
                    if (donorIdObj != null) {
                        donation.setDonorId(((Number) donorIdObj).intValue());
                    }
                    donation.setDonorType(rs.getString("donor_type"));
                    donation.setDonorFullName(rs.getString("donor_full_name"));
                    donation.setDonorPhone(rs.getString("donor_phone"));
                    donation.setDonorEmail(rs.getString("donor_email"));
                    Object anonymousObj = rs.getObject("is_anonymous");
                    if (anonymousObj != null) {
                        donation.setDonorAnonymous(rs.getBoolean("is_anonymous"));
                    }
                    return donation;
                } else {
                    System.out.println("==> getDonationByIdForOrganization() - KHÔNG tìm thấy donation!");
                }
            }
        } catch (Exception ex) {
            System.err.println("==> getDonationByIdForOrganization() - Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

}
