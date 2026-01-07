/*
 * A friendly reminder to drink enough water
 */

package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Account;
import model.User;
import utils.DBContext;

/**
 *
 * @author Mirinae
 */
public class OrganizationProfileDAO {
	private Connection conn;
    public OrganizationProfileDAO() {
        try {
			DBContext db = new DBContext();
			this.conn = db.getConnection(); // lấy connection từ DBContext
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public User getOrganizationProfile(int accountId) {
        String sql = """
            SELECT 
                u.id AS user_id,
                u.account_id,
                u.full_name,
                u.gender,
                u.phone,
                u.email,
                u.address,
                u.avatar,
                u.job_title,
                u.bio,
                u.dob,

                a.id AS acc_id,
                a.username,
                a.role
            FROM Users u
            INNER JOIN Accounts a ON u.account_id = a.id
            WHERE u.account_id = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    Account acc = new Account();
                    acc.setId(rs.getInt("acc_id"));
                    acc.setUsername(rs.getString("username"));
                    acc.setRole(rs.getString("role"));

                    return new User(
                        rs.getInt("user_id"),
                        rs.getInt("account_id"),
                        acc,
                        rs.getString("full_name"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("avatar"),
                        rs.getString("job_title"),
                        rs.getString("bio"),
                        rs.getDate("dob")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Update
    public User getByUserId(int id) {
        String sql = """
            SELECT id, account_id, full_name, gender, phone, email, address, avatar, job_title, bio, dob
            FROM Users
            WHERE id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getInt("account_id"),
                        rs.getString("full_name"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("avatar"),
                        rs.getString("job_title"),
                        rs.getString("bio"),
                        rs.getDate("dob") // may be null
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    // Update using Users.id only (id is unique; authorization handles ownership check)
    public boolean updateById(User u) {
        String sql = """
            UPDATE Users
            SET full_name = ?, gender = ?, phone = ?, email = ?, address = ?, avatar = ?, job_title = ?, bio = ?, dob = ?
            WHERE id = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getFull_name());
            ps.setString(2, u.getGender());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getAddress());
            ps.setString(6, u.getAvatar());
            ps.setString(7, u.getJob_title());
            ps.setString(8, u.getBio());
            ps.setDate(9, u.getDob() != null ? new java.sql.Date(u.getDob().getTime()) : null);
            ps.setInt(10, u.getId());
            int rows = ps.executeUpdate();
            System.out.println("Update rows affected: " + rows); // Log for debugging
            return rows >= 0; // Consider success even if 0 (no changes or matched but unchanged in some DBs)
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // Update text fields only (exclude avatar)
public boolean updateUserTextOnly(int id, String fullName, String phone, String email, String address, String jobTitle, String bio, java.sql.Date dob) {
    String sql = """
        UPDATE Users
        SET full_name = ?, phone = ?, email = ?, address = ?, job_title = ?, bio = ?, dob = ?
        WHERE id = ?
    """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, fullName);
        ps.setString(2, phone);
        ps.setString(3, email);
        ps.setString(4, address);
        ps.setString(5, jobTitle);
        ps.setString(6, bio);
        ps.setDate(7, dob);
        ps.setInt(8, id);
        int rows = ps.executeUpdate();
        System.out.println("Text update rows: " + rows);
        return rows > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

// Update avatar only
public boolean updateAvatar(int id, String avatar) {
    String sql = """
        UPDATE Users
        SET avatar = ?
        WHERE id = ?
    """;
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, avatar);
        ps.setInt(2, id);
        int rows = ps.executeUpdate();
        System.out.println("Avatar update rows: " + rows);
        return rows > 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

 // Check data uniqueness while updating
public boolean isPhoneUnique(String phone, int currentUserId) {
    String sql = "SELECT COUNT(*) FROM Users WHERE phone = ? AND id <> ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, phone);
        ps.setInt(2, currentUserId);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) == 0;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

public boolean isEmailUnique(String email, int currentUserId) {
    String sql = "SELECT COUNT(*) FROM Users WHERE email = ? AND id <> ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, email);
        ps.setInt(2, currentUserId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) == 0;
        }
    } catch (Exception ignored) {}
    return false;
}
}
