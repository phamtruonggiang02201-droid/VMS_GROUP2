/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import model.Account;
import utils.DBContext;

/**
 *
 * @author Admin
 */
// DAO dành riêng cho quản lí tài khoản bởi Admin
public class AdminAccountDAO {
    
    private Connection conn;
    
    public AdminAccountDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 1. Cập nhật trạng thái khóa/mở tài khoản
    public boolean updateAccountStatus(int id, boolean status) {
        String sql = "UPDATE Accounts SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, status);
            ps.setInt(2, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // 2. Tìm kiếm + lọc tài khoản theo role, status, search (username)
    public List<Account> findAccounts(String role, Boolean status, String search) {
        List<Account> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, username, password, role, status, created_at FROM Accounts WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (role != null && !role.isEmpty()) {
            sql.append(" AND role = ?");
            params.add(role);
        }
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (username LIKE ?)");
            params.add("%" + search + "%");
        }
        sql.append(" ORDER BY id ASC");
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) {
                    ps.setString(i + 1, (String) p);
                } else if (p instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) p);
                } else if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setObject(i + 1, p);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Account(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getBoolean("status"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 3. Đếm tổng số bản ghi theo cùng tiêu chí lọc
    public int countAccounts(String role, Boolean status, String search) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM Accounts WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (role != null && !role.isEmpty()) {
            sql.append(" AND role = ?");
            params.add(role);
        }
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (username LIKE ?)");
            params.add("%" + search + "%");
        }
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) {
                    ps.setString(i + 1, (String) p);
                } else if (p instanceof Boolean) {
                    ps.setBoolean(i + 1, (Boolean) p);
                } else if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setObject(i + 1, p);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // 4. Lấy danh sách theo trang (offset/limit) cùng tiêu chí lọc hiện tại
    public List<Account> findAccountsPaged(String role, Boolean status, String search, int offset, int limit) {
        List<Account> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id, username, password, role, status, created_at FROM Accounts WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (role != null && !role.isEmpty()) {
            sql.append(" AND role = ?");
            params.add(role);
        }
        if (status != null) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (search != null && !search.isEmpty()) {
            sql.append(" AND (username LIKE ?)");
            params.add("%" + search + "%");
        }
        
        sql.append(" ORDER BY id ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                if (p instanceof String) {
                    ps.setString(idx++, (String) p);
                } else if (p instanceof Boolean) {
                    ps.setBoolean(idx++, (Boolean) p);
                } else if (p instanceof Integer) {
                    ps.setInt(idx++, (Integer) p);
                } else {
                    ps.setObject(idx++, p);
                }
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new Account(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role"),
                            rs.getBoolean("status"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // 5. Tạo tài khoản mới và trả về ID
    public int insertAccountAndGetId(String username, String password, String role, boolean status) {
        String sql = "INSERT INTO Accounts (username, password, role, status, created_at) OUTPUT INSERTED.id VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.setBoolean(4, status);
            // Lưu thời gian tạo với giờ, phút, giây
            ps.setTimestamp(5, new java.sql.Timestamp(new Date().getTime()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Overload method without return value
    public boolean insertAccount(String username, String password, String role, boolean status) {
        return insertAccountAndGetId(username, password, role, status) > 0;
    }
    
    // 6. Kiểm tra username đã tồn tại chưa
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Accounts WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // 7. Lấy ID tài khoản vừa tạo (sau khi INSERT)
    public int getLastInsertedAccountId() {
        String sql = "SELECT SCOPE_IDENTITY()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // 7b. Kiểm tra Organization có sự kiện active trong 48h tới không
    public boolean hasActiveEventsWithin48Hours(int organizationId) {
        String sql = """
            SELECT COUNT(*) 
            FROM Events 
            WHERE organization_id = ? 
            AND status = 'active' 
            AND start_date > GETDATE() 
            AND start_date <= DATEADD(HOUR, 48, GETDATE())
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, organizationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
}

