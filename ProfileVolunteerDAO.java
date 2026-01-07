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
import model.ProfileVolunteer;
import utils.DBContext;

// DAO to fetch read-only volunteer profile for organization viewing.
public class ProfileVolunteerDAO {

    public ProfileVolunteer getProfileByAccountId(int accountId) {
        String sql = """
                SELECT 
                    a.id AS account_id,
                    u.full_name,
                    u.dob,
                    u.gender,
                    u.phone,
                    u.email,
                    u.address,
                    u.job_title,
                    u.bio,
                    u.avatar
                FROM Users u
                JOIN Accounts a ON u.account_id = a.id
                WHERE a.id = ?
                """;
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProfileVolunteer p = new ProfileVolunteer();
                    p.setId(rs.getInt("account_id"));
                    p.setImages(rs.getString("avatar"));
                    p.setFullName(rs.getString("full_name"));
                    java.sql.Date dob = rs.getDate("dob");
                    if (dob != null) {
                        p.setDob(new java.util.Date(dob.getTime()));
                    }
                    p.setGender(rs.getString("gender"));
                    p.setPhone(rs.getString("phone"));
                    p.setEmail(rs.getString("email"));
                    p.setAddress(rs.getString("address"));
                    p.setJobTitle(rs.getString("job_title"));
                    p.setBio(rs.getString("bio"));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ProfileVolunteer getDetailedProfileByAccountId(int accountId) {
        String sql = """
                SELECT 
                    a.id AS account_id,
                    u.full_name,
                    u.dob,
                    u.gender,
                    u.phone,
                    u.email,
                    u.address,
                    u.job_title,
                    u.bio,
                    u.avatar,
                    COALESCE(stats.total_events, 0) AS total_events,
                    COALESCE(don.total_donated, 0) AS total_donated,
                    last_event.title AS last_event_title,
                    last_event.org_name AS last_organization_name
                FROM Accounts a
                JOIN Users u ON u.account_id = a.id
                LEFT JOIN (
                    SELECT volunteer_id,
                           COUNT(DISTINCT event_id) AS total_events
                    FROM Event_Volunteers
                    WHERE status = 'approved'
                    GROUP BY volunteer_id
                ) stats ON stats.volunteer_id = a.id
                LEFT JOIN (
                    SELECT volunteer_id,
                           COALESCE(SUM(amount), 0) AS total_donated
                    FROM Donations
                    WHERE status = 'success'
                    GROUP BY volunteer_id
                ) don ON don.volunteer_id = a.id
                OUTER APPLY (
                    SELECT TOP 1 e.title AS title, acc.username AS org_name
                    FROM Event_Volunteers ev
                    JOIN Events e ON ev.event_id = e.id
                    JOIN Accounts acc ON e.organization_id = acc.id
                    WHERE ev.volunteer_id = a.id AND ev.status = 'approved'
                    ORDER BY ev.apply_date DESC
                ) last_event
                WHERE a.id = ?
                """;

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProfileVolunteer p = new ProfileVolunteer();
                    p.setId(rs.getInt("account_id"));
                    p.setImages(rs.getString("avatar"));
                    p.setFullName(rs.getString("full_name"));
                    java.sql.Date dob = rs.getDate("dob");
                    if (dob != null) {
                        p.setDob(new java.util.Date(dob.getTime()));
                    }
                    p.setGender(rs.getString("gender"));
                    p.setPhone(rs.getString("phone"));
                    p.setEmail(rs.getString("email"));
                    p.setAddress(rs.getString("address"));
                    p.setJobTitle(rs.getString("job_title"));
                    p.setBio(rs.getString("bio"));
                    p.setTotalEvents(rs.getInt("total_events"));
                    p.setTotalDonated(rs.getDouble("total_donated"));
                    p.setEventName(rs.getString("last_event_title"));
                    p.setOrganizationName(rs.getString("last_organization_name"));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateVolunteerProfile(int accountId, String avatarPath, String fullName, java.sql.Date dob, String gender, String phone, String email, String address, String jobTitle, String bio) {
        String sql = """
                UPDATE Users
                SET full_name = ?,
                    dob = ?,
                    gender = ?,
                    phone = ?,
                    email = ?,
                    address = ?,
                    job_title = ?,
                    bio = ?,
                    avatar = ?
                WHERE account_id = ?
                """;
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, fullName);
            if (dob != null) {
                ps.setDate(2, dob);
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, gender);
            ps.setString(4, phone);
            ps.setString(5, email);
            ps.setString(6, address);
            ps.setString(7, jobTitle);
            ps.setString(8, bio);
            if (avatarPath != null && !avatarPath.trim().isEmpty()) {
                ps.setString(9, avatarPath);
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
            }
            ps.setInt(10, accountId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Backward-compatible overload (without jobTitle & bio)
    public boolean updateVolunteerProfile(int accountId, String avatarPath, String fullName, java.sql.Date dob, String gender, String phone, String email, String address) {
        return updateVolunteerProfile(accountId, avatarPath, fullName, dob, gender, phone, email, address, null, null);
    }

    public boolean emailExistsForOther(String email, int accountId) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ? AND account_id <> ?";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean phoneExistsForOther(String phone, int accountId) {
        String sql = "SELECT COUNT(*) FROM Users WHERE phone = ? AND account_id <> ?";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setInt(2, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy hồ sơ TNV theo tài khoản nhưng chỉ trong phạm vi các sự kiện do tổ chức hiện tại tạo
    public ProfileVolunteer getProfileByAccountIdAndOrganization(int accountId, int organizationId) {
        String sql = """
                SELECT 
                    u.account_id AS account_id,
                    u.full_name,
                    u.dob,
                    u.gender,
                    u.phone,
                    u.email,
                    u.address,
                    u.avatar AS avatar,
                    -- sự kiện gần nhất của TNV trong tổ chức này
                    (
                        SELECT TOP 1 e2.title
                        FROM Event_Volunteers ev2
                        JOIN Events e2 ON ev2.event_id = e2.id
                        WHERE ev2.volunteer_id = u.account_id AND e2.organization_id = ?
                        ORDER BY ev2.apply_date DESC
                    ) AS event_name,
                    -- tên tổ chức (username)
                    (
                        SELECT o.username FROM Accounts o WHERE o.id = ?
                    ) AS organization_name,
                    -- tổng sự kiện TNV tham gia trong tổ chức này
                    (
                        SELECT COUNT(DISTINCT ev3.event_id)
                        FROM Event_Volunteers ev3
                        JOIN Events e3 ON ev3.event_id = e3.id
                        WHERE ev3.volunteer_id = u.account_id AND e3.organization_id = ?
                    ) AS total_events,
                    -- tổng donate trong tổ chức này (nếu có bảng donate thì cập nhật lại)
                    0.0 AS total_donated
                FROM Users u
                JOIN Accounts a ON u.account_id = a.id
                WHERE a.id = ?
                """;
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            // Thứ tự tham số theo query trên
            ps.setInt(1, organizationId);
            ps.setInt(2, organizationId);
            ps.setInt(3, organizationId);
            ps.setInt(4, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProfileVolunteer p = new ProfileVolunteer();
                    p.setId(rs.getInt("account_id"));
                    p.setImages(rs.getString("avatar"));
                    p.setFullName(rs.getString("full_name"));
                    java.sql.Date dob = rs.getDate("dob");
                    if (dob != null) {
                        p.setDob(new java.util.Date(dob.getTime()));
                    }
                    p.setGender(rs.getString("gender"));
                    p.setPhone(rs.getString("phone"));
                    p.setEmail(rs.getString("email"));
                    p.setAddress(rs.getString("address"));
                    p.setEventName(rs.getString("event_name"));
                    p.setOrganizationName(rs.getString("organization_name"));
                    p.setTotalEvents(rs.getInt("total_events"));
                    p.setTotalDonated(rs.getDouble("total_donated"));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tiêu đề sự kiện theo eventId nhưng phải thuộc tổ chức này
    public String getEventTitleForOrganization(int eventId, int organizationId) {
        String sql = "SELECT e.title FROM Events e WHERE e.id = ? AND e.organization_id = ?";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, eventId);
            ps.setInt(2, organizationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Danh sách tiêu đề sự kiện mà volunteer tham gia trong phạm vi 1 tổ chức
    public List<String> getEventTitlesForVolunteerInOrganization(int volunteerAccountId, int organizationId) {
        List<String> titles = new ArrayList<>();
        String sql = """
            SELECT e.title
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            WHERE ev.volunteer_id = ? AND e.organization_id = ?
            ORDER BY ev.apply_date DESC
        """;
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, volunteerAccountId);
            ps.setInt(2, organizationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    titles.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titles;
    }

    // Lấy danh sách hồ sơ TNV của một sự kiện cụ thể (có filter)
    public List<ProfileVolunteer> getProfilesByEvent(int organizationId, int eventId, String genderFilter, String nameQuery) {
        List<ProfileVolunteer> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT
                u.account_id AS account_id,
                u.full_name,
                u.dob,
                u.gender,
                u.phone,
                u.email,
                u.address,
                u.avatar AS images,
                e.title AS event_name,
                o.username AS organization_name
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            JOIN Accounts o ON e.organization_id = o.id
            JOIN Users u ON ev.volunteer_id = u.account_id
            WHERE e.organization_id = ? AND e.id = ? AND ev.status = 'approved'
        """;

        if (genderFilter != null && !genderFilter.equals("all") && !genderFilter.isEmpty()) {
            sql += " AND u.gender = ?";
        }
        if (nameQuery != null && !nameQuery.trim().isEmpty()) {
            sql += " AND u.full_name LIKE ?";
        }

        sql += " ORDER BY u.full_name";

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ps.setInt(2, eventId);
            int idx = 3;
            if (genderFilter != null && !genderFilter.equals("all") && !genderFilter.isEmpty()) {
                ps.setString(idx++, genderFilter);
            }
            if (nameQuery != null && !nameQuery.trim().isEmpty()) {
                ps.setString(idx++, "%" + nameQuery.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProfileVolunteer p = new ProfileVolunteer();
                    p.setId(rs.getInt("account_id"));
                    p.setImages(rs.getString("images"));
                    p.setFullName(rs.getString("full_name"));
                    java.sql.Date dob = rs.getDate("dob");
                    if (dob != null) {
                        p.setDob(new java.util.Date(dob.getTime()));
                    }
                    p.setGender(rs.getString("gender"));
                    p.setPhone(rs.getString("phone"));
                    p.setEmail(rs.getString("email"));
                    p.setAddress(rs.getString("address"));
                    p.setEventName(rs.getString("event_name"));
                    p.setOrganizationName(rs.getString("organization_name"));
                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy danh sách hồ sơ TNV của tất cả sự kiện thuộc 1 organization (có filter)
    public List<ProfileVolunteer> getProfilesByOrganization(int organizationId, String genderFilter, String nameQuery, String eventTitleQuery) {
        List<ProfileVolunteer> list = new ArrayList<>();
        String sql = """
            WITH ranked AS (
                SELECT 
                    u.account_id AS account_id,
                    u.full_name,
                    u.dob,
                    u.gender,
                    u.phone,
                    u.email,
                    u.address,
                    u.avatar AS images,
                    e.title AS event_name,
                    o.username AS organization_name,
                    ROW_NUMBER() OVER (PARTITION BY u.account_id ORDER BY ev.apply_date DESC) AS rn
                FROM Event_Volunteers ev
                JOIN Events e ON ev.event_id = e.id
                JOIN Accounts o ON e.organization_id = o.id
                JOIN Users u ON ev.volunteer_id = u.account_id
                WHERE e.organization_id = ?
        """;

        if (genderFilter != null && !genderFilter.equals("all") && !genderFilter.isEmpty()) {
            sql += " AND u.gender = ?";
        }
        if (nameQuery != null && !nameQuery.trim().isEmpty()) {
            sql += " AND u.full_name LIKE ?";
        }
        if (eventTitleQuery != null && !eventTitleQuery.trim().isEmpty()) {
            sql += " AND e.title LIKE ?";
        }

        sql += """
            )
            SELECT account_id, full_name, dob, gender, phone, email, address, images, event_name, organization_name
            FROM ranked
            WHERE rn = 1
        """;

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            int idx = 2;
            if (genderFilter != null && !genderFilter.equals("all") && !genderFilter.isEmpty()) {
                ps.setString(idx++, genderFilter);
            }
            if (nameQuery != null && !nameQuery.trim().isEmpty()) {
                ps.setString(idx++, "%" + nameQuery.trim() + "%");
            }
            if (eventTitleQuery != null && !eventTitleQuery.trim().isEmpty()) {
                ps.setString(idx++, "%" + eventTitleQuery.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProfileVolunteer p = new ProfileVolunteer();
                    p.setId(rs.getInt("account_id"));
                    p.setImages(rs.getString("images"));
                    p.setFullName(rs.getString("full_name"));
                    java.sql.Date dob = rs.getDate("dob");
                    if (dob != null) {
                        p.setDob(new java.util.Date(dob.getTime()));
                    }
                    p.setGender(rs.getString("gender"));
                    p.setPhone(rs.getString("phone"));
                    p.setEmail(rs.getString("email"));
                    p.setAddress(rs.getString("address"));
                    p.setEventName(rs.getString("event_name"));
                    p.setOrganizationName(rs.getString("organization_name"));
                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ---- Organization event applications (moved from OrganizationApplyDAO) ----
    // Lấy danh sách volunteer apply theo event (lọc theo status)
    public List<model.EventVolunteer> getEventVolunteersByEvent(int organizationId, int eventId, String statusFilter) {
        List<model.EventVolunteer> list = new ArrayList<>();
        String sql = """
            SELECT 
                ev.id,
                ev.event_id,
                ev.volunteer_id,
                ev.apply_date,
                ev.status,
                ev.note,
                o.username AS organizationName,
                c.name AS categoryName,
                u.full_name AS volunteerName
            FROM Event_Volunteers ev
            JOIN Events e ON ev.event_id = e.id
            JOIN Accounts o ON e.organization_id = o.id
            JOIN Categories c ON e.category_id = c.category_id
            JOIN Users u ON ev.volunteer_id = u.account_id
            WHERE e.organization_id = ?
              AND e.id = ?
        """;
        if (statusFilter != null && !"all".equals(statusFilter)) {
            sql += " AND ev.status = ?";
        }
        sql += " ORDER BY ev.apply_date DESC";

        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            ps.setInt(2, eventId);
            if (statusFilter != null && !"all".equals(statusFilter)) {
                ps.setString(3, statusFilter);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.EventVolunteer ev = new model.EventVolunteer(
                            rs.getInt("id"),
                            rs.getInt("event_id"),
                            rs.getInt("volunteer_id"),
                            rs.getTimestamp("apply_date"),
                            rs.getString("status"),
                            rs.getString("note"),
                            rs.getString("organizationName"),
                            rs.getString("categoryName"),
                            rs.getString("volunteerName")
                    );
                    list.add(ev);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateEventVolunteerStatus(int applyId, String status) {
        String sql = "UPDATE Event_Volunteers SET status = ? WHERE id = ?";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, applyId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEventVolunteerNote(int applyId, String note) {
        String sql = "UPDATE Event_Volunteers SET note = ? WHERE id = ?";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, note);
            ps.setInt(2, applyId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteEventVolunteer(int applyId, int eventId, int organizationId) {
        String sql = "DELETE ev FROM Event_Volunteers ev JOIN Events e ON ev.event_id = e.id WHERE ev.id = ? AND ev.event_id = ? AND e.organization_id = ?";
        try (Connection con = DBContext.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applyId);
            ps.setInt(2, eventId);
            ps.setInt(3, organizationId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
