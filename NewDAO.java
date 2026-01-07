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
import model.New;
import utils.DBContext;

/**
 *
 * @author ADDMIN
 */
public class NewDAO {

    private Connection conn;

    public NewDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // lấy danh sách tất cả các Bài viết đang xuất bản
    public List<New> getAllPostNews() {
        List<New> list = new ArrayList<>();
        String sql = """
                        SELECT 
                            n.id,
                            n.title,
                            n.content,
                            n.images,
                            n.created_at,
                            n.updated_at,
                            n.organization_id,
                            n.status,
                            u.full_name AS organization_name
                        FROM News AS n
                        JOIN Users AS u 
                            ON n.organization_id = u.id
                        WHERE n.status = 'published'
                        ORDER BY n.created_at DESC;
                     """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                New e = new New(
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
                list.add(e);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // lấy danh sách top 3 bài viết mới nhất
    public List<New> getTop3PostNews() {
        List<New> list = new ArrayList<>();
        String sql = """
                        SELECT TOP 3 
                            n.id,
                            n.title,
                            n.content,
                            n.images,
                            n.created_at,
                            n.updated_at,
                            n.organization_id,
                            n.status,
                            u.full_name AS organization_name
                        FROM News AS n
                        JOIN Users AS u 
                            ON n.organization_id = u.id
                        WHERE n.status = 'published'
                        ORDER BY n.created_at DESC;
                     """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                New e = new New(
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
                list.add(e);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<New> getPublishNewsPaged(int offset, int limit) {
        List<New> list = new ArrayList<>();
        String sql = """
                    SELECT n.*, 
                           u.full_name AS organization_name
                    FROM News n
                    JOIN Accounts a ON n.organization_id = a.id
                    JOIN Users u ON a.id = u.account_id
                    WHERE n.status = 'published'
                    ORDER BY n.created_at DESC
                    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    New n = new New(
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
                    list.add(n);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    //tính tổng News đã đăng để chia trang
    public int getTotalPublishNews() {
        String sql = "SELECT COUNT(*) FROM News WHERE status = 'published'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    // Filter news by date and time with pagination
    public List<New> getNewsByDateTimeRangePaged(String startDateTime, String endDateTime, int offset, int limit) {
        List<New> list = new ArrayList<>();
        String sql = """
                    SELECT 
                        n.id,
                        n.title,
                        n.content,
                        n.images,
                        n.created_at,
                        n.updated_at,
                        n.organization_id,
                        n.status,
                        u.full_name AS organization_name
                    FROM News AS n
                    JOIN Users AS u 
                        ON n.organization_id = u.id
                    WHERE n.created_at >= ?
                      AND n.created_at <= ?
                      AND n.status = 'published'
                    ORDER BY n.created_at DESC
                    OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                    """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDateTime);
            ps.setString(2, endDateTime);
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    New e = new New(
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
                    list.add(e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // Count news in date-time range for pagination
    public int countNewsByDateTimeRange(String startDateTime, String endDateTime) {
        String sql = """
                    SELECT COUNT(*) 
                    FROM News 
                    WHERE created_at >= ?
                      AND created_at <= ?
                      AND status = 'published'
                    """;
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, startDateTime);
            ps.setString(2, endDateTime);
            
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
}
