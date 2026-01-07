/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import model.Account;
import model.User;
import java.sql.SQLException;

import utils.DBContext;

/**
 *
 * @author Admin
 */
public class UserDAO {

    private Connection conn;

    public UserDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Kiểm tra email đã tồn tại chưa
    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Kiểm tra phone đã tồn tại chưa
    public boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM Users WHERE phone = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//     3. Thêm mới user sau khi tạo account -- phần của An
    public boolean insertUserWithAccount(User user) {
        String sql = "INSERT INTO Users (account_id, full_name, dob, gender, phone, email, address, avatar, job_title, bio) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getAccount_id());
            ps.setString(2, user.getFull_name());
            if (user.getDob() != null) {
                ps.setDate(3, new java.sql.Date(user.getDob().getTime()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, user.getGender());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getAvatar());
            ps.setString(9, user.getJob_title());
            ps.setString(10, user.getBio());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Get user by account_id
    public User getUserByAccountId(int accountId) {
        String sql = "SELECT * FROM Users WHERE account_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setAccount_id(rs.getInt("account_id"));
                user.setFull_name(rs.getString("full_name"));
                user.setDob(rs.getDate("dob"));
                user.setGender(rs.getString("gender"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                user.setAvatar(rs.getString("avatar"));
                user.setJob_title(rs.getString("job_title"));
                user.setBio(rs.getString("bio"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
