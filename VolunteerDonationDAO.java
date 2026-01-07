/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import utils.DBContext;

/**
 *
 * @author Admin
 */
public class VolunteerDonationDAO {

    // Kết nối DB dùng cho các thao tác liên quan đến bảng `Donations` cho volunteer
    private Connection conn;

    public VolunteerDonationDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tạo một bản ghi donation mới cho volunteer.
    // - Ghi trạng thái ban đầu là 'pending' (chờ xác nhận/hoàn tất thanh toán).
    // - Hàm này thường được gọi khi volunteer muốn đóng góp trực tiếp (không qua cổng thanh toán),
    //   hoặc để khởi tạo donation trước khi cập nhật trạng thái sau khi xác nhận thanh toán.
    public boolean createDonation(int eventId, int volunteerId, double amount,
            String paymentMethod, String note) {
        String sql = "INSERT INTO Donations (event_id, volunteer_id, amount, donate_date, "
                + "status, payment_method, note) "
                + "VALUES (?, ?, ?, GETDATE(), 'pending', ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, eventId);
            ps.setInt(2, volunteerId);
            ps.setDouble(3, amount);
            ps.setString(4, paymentMethod);
            ps.setString(5, note);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




