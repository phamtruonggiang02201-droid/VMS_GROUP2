/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.New;
import utils.DBContext;
public class AdminNewsDAO {
    private Connection connection;
    
    public AdminNewsDAO() {
        try {
            connection = new DBContext().getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Lấy danh sách News có phân trang + filter + sort
    public List<New> getAllNews(String statusFilter, String sortOrder, int page, int pageSize) {
        List<New> list = new ArrayList<>();
        
        // Tính OFFSET
        int offset = (page - 1) * pageSize;
        
        // Build câu SQL
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT n.id, n.title, n.content, n.images, ");
        sql.append("       n.created_at, n.updated_at, n.organization_id, n.status, ");
        sql.append("       u.full_name AS organization_name ");
        sql.append("FROM News n ");
        sql.append("JOIN Accounts a ON n.organization_id = a.id ");
        sql.append("JOIN Users u ON a.id = u.account_id ");
        
        // Filter theo status
        if (statusFilter != null && !statusFilter.equals("all")) {
            sql.append("WHERE n.status = ? ");
        }
        
        // Sort
        if ("oldest".equals(sortOrder)) {
            sql.append("ORDER BY n.created_at ASC ");
        } else {
            sql.append("ORDER BY n.created_at DESC ");
        }
        
        // Phân trang (SQL Server)
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql.toString());
            
            int paramIndex = 1;
            
            // Set parameter cho filter
            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(paramIndex++, statusFilter);
            }
            
            // Set parameter cho phân trang
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, pageSize);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                New news = new New(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("images"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    rs.getInt("organization_id"),
                    rs.getString("status"),
                    rs.getString("organization_name")
                );
                list.add(news);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }
    

     // Đếm tổng số bài News (để tính số trang)

    public int getTotalNews(String statusFilter) {
        String sql = "SELECT COUNT(*) AS total FROM News";
        
        if (statusFilter != null && !statusFilter.equals("all")) {
            sql += " WHERE status = ?";
        }
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            
            if (statusFilter != null && !statusFilter.equals("all")) {
                ps.setString(1, statusFilter);
            }
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    

     // Lấy chi tiết 1 bài News theo ID

    public New getNewsById(int id) {
        String sql = "SELECT n.id, n.title, n.content, n.images, " +
                     "       n.created_at, n.updated_at, n.organization_id, n.status, " +
                     "       u.full_name AS organization_name " +
                     "FROM News n " +
                     "JOIN Accounts a ON n.organization_id = a.id " +
                     "JOIN Users u ON a.id = u.account_id " +
                     "WHERE n.id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return new New(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("images"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    rs.getInt("organization_id"),
                    rs.getString("status"),
                    rs.getString("organization_name")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    

     // Cập nhật trạng thái News (duyệt/từ chối/ẩn/hiện)

    public boolean updateNewsStatus(int id, String newStatus) {
        String sql = "UPDATE News SET status = ?, updated_at = GETDATE() WHERE id = ?";
        
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
