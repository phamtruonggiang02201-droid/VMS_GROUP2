/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class StatisticsDAO {

    private Connection conn;

    public StatisticsDAO() {
        try {
            DBContext db = new DBContext();

            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //1 tổng số sự kiện 
    public int getTotalEvents() {
        String sql = "SELECT COUNT(*) AS total FROM Events";
        
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }

    //1.1 tổng số sự kiện đang diễn ra
    public int getTotalEventsActive() {
        String sql = "SELECT COUNT(*) AS total FROM Events where status = 'active'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    //1.2 tổng số sự kiện đã kết thúc
    public int getTotalEventsInactive() {
        String sql = "SELECT COUNT(*) AS total FROM Events where status = 'inactive'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    
}
