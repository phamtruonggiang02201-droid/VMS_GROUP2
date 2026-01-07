
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.Account;
import utils.DBContext;
import java.sql.*;
import java.util.List;
import utils.PasswordUtil;

/**
 *
 * @author Admin
 */
public class AccountDAO {

    private Connection conn;

    public AccountDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 1. Truy xuất thông tin theo ID
    public Account getAccountById(int id) {
        String sql = "SELECT id, username, password, role, status "
                + "FROM Accounts WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Trả về đối tượng Account với các giá trị tương ứng
                    return new Account(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getBoolean("status") // Gán giá trị status
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Nếu không tìm thấy account
    }

    // 1.1. Lấy ra danh sách các tài khoản - dùng để hiển thị dữ liệu 
    public List<Account> getAllAccounts() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT id, username, password, role, status FROM Accounts";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Account acc = new Account(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("status")
                );
                list.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }
    // check email đã tồn tại trong sql ? - dùng cho đăng kí tài khoản
    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Accounts WHERE username = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // đăng kí tài khoản -> sẽ tự động insert vào database
    public int insertAccount(Account account) {
        String sql = "INSERT INTO Accounts (username, password, role, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getRole());
            ps.setBoolean(4, account.isStatus());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Trả về account_id vừa tạo
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // -------- Quên mật khẩu -----------
    // lấy thông tin username + email . nếu đúng -> trả về Account
    public Account getAccountByUsernameAndEmail(String username, String email) {
        String sql = "SELECT a.id, a.username, a.password, a.role, a.status "
                + "FROM Accounts a JOIN Users u ON a.id = u.account_id "
                + "WHERE a.username = ? AND u.email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật mật khẩu mới được random (đã mã hóa) ghi mật khẩu random mới vào DB
    public boolean updatePasswordRandom(int accountId, String newPassword) {
        String sql = "UPDATE Accounts SET password = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, accountId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ----------------- Đổi mật khẩu -----------------
    // đổi mật khẩu của từng tài khoản
    public boolean updatePasswordByUser(int accountId, String newPassword) {
        String sql = "UPDATE Accounts SET password = ? WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("🔹 SQL: " + sql);
            System.out.println("🔹 newPassword = " + newPassword);
            System.out.println("🔹 accountId = " + accountId);

            ps.setString(1, newPassword);
            ps.setInt(2, accountId);

            int rowsAffected = ps.executeUpdate();
            System.out.println("🔹 Rows affected = " + rowsAffected);

            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Lấy hash mật khẩu hiện tại từ DB theo accountId
    public String getPasswordHashById(int accountId) {
        String sql = "SELECT password FROM Accounts WHERE id = ?";
        try (Connection conn = DBContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy danh sách accounts theo roles và status (dùng cho Admin gửi thông báo chung)
    public List<Account> getAccountsByRolesAndStatus(List<String> roles, String statusFilter) {
        List<Account> accounts = new ArrayList<>();
        
        // Build SQL động với IN clause
        StringBuilder sql = new StringBuilder("SELECT id, username, password, role, status FROM Accounts WHERE 1=1");
        
        // Filter theo roles
        if (roles != null && !roles.isEmpty()) {
            sql.append(" AND role IN (");
            for (int i = 0; i < roles.size(); i++) {
                sql.append("?");
                if (i < roles.size() - 1) sql.append(", ");
            }
            sql.append(")");
        }
        
        // Filter theo status
        if ("active".equals(statusFilter)) {
            sql.append(" AND status = 1");
        } else if ("inactive".equals(statusFilter)) {
            sql.append(" AND status = 0");
        }
        // "all" thì không filter status
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            // Set parameters cho roles
            int paramIndex = 1;
            if (roles != null && !roles.isEmpty()) {
                for (String role : roles) {
                    ps.setString(paramIndex++, role);
                }
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Account account = new Account(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getBoolean("status")
                    );
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accounts;
    }

}
