/*
 * A friendly reminder to drink enough water
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import utils.DBContext;
import model.New;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Mirinae
 */
public class OrganizationNewsManagementDAO {

    private Connection conn;

    public OrganizationNewsManagementDAO() {
        try {
            DBContext db = new DBContext();
            this.conn = db.getConnection(); // lấy connection từ DBContext
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<New> getAllNews() {
        List<New> list = new ArrayList<>();
        String sql = """
        SELECT 
            n.id,
            n.title,
            n.content,
            n.images,
            n.created_at AS createdAt,
            n.updated_at AS updatedAt,
            n.organization_id AS organizationId,
            n.status,
            u.full_name AS organizationName
        FROM News AS n
        JOIN Users AS u 
            ON n.organization_id = u.id
        ORDER BY n.created_at DESC
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                New e = new New(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("images"),
                        rs.getTimestamp("createdAt"),
                        rs.getTimestamp("updatedAt"),
                        rs.getInt("organizationId"),
                        rs.getString("status"),
                        rs.getString("organizationName")
                );
                list.add(e);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public int getTotalNewsCount() {
        String sql = "SELECT COUNT(*) FROM News";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<New> getAllNewsWithPagination(int page, int pageSize) {
        List<New> list = new ArrayList<>();
        String sql = "SELECT id, title, images, status \n"
                + "FROM News\n"
                + "ORDER BY u.id\n"
                + "OFFSET ? ROWS\n"
                + "FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int offset = (page - 1) * pageSize;
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                New news = new New();
                news.setId(rs.getInt("id"));
                news.setTitle(rs.getString("title"));
                news.setImages(rs.getString("images"));
                news.setStatus(rs.getString("status"));

                list.add(news);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<New> getNewsWithFiltersAndPagination(int page, int pageSize, int organizationId, String status, String search) throws SQLException {
        List<New> list = new ArrayList<>();
        if (page < 1) {
            page = 1;
        }
        int offset = (page - 1) * pageSize;

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT n.id, n.title, n.content, n.images, ")
                .append("n.created_at AS createdAt, n.updated_at AS updatedAt, ")
                .append("n.organization_id AS organizationId, n.status, ")
                .append("u.full_name AS organizationName ")
                .append("FROM News AS n ")
                .append("LEFT JOIN Users AS u ON n.organization_id = u.id ");

        // Build WHERE: organization filter is mandatory
        List<String> whereClauses = new ArrayList<>();
        whereClauses.add("n.organization_id = ?");

        if (status != null && !status.trim().isEmpty()) {
            whereClauses.add("n.status = ?");
        }
        if (search != null && !search.trim().isEmpty()) {
            whereClauses.add("n.title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ?");
        }

        sql.append("WHERE ").append(String.join(" AND ", whereClauses)).append(" ");
        sql.append("ORDER BY n.created_at DESC ");
        sql.append("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            // set organizationId first
            ps.setInt(idx++, organizationId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx++, status.trim());
            }
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(idx++, "%" + search.trim() + "%");
            }

            ps.setInt(idx++, offset);
            ps.setInt(idx, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    New news = new New();
                    news.setId(rs.getInt("id"));
                    news.setTitle(rs.getString("title"));
                    news.setContent(rs.getString("content"));
                    news.setImages(rs.getString("images"));
                    news.setCreatedAt(rs.getTimestamp("createdAt"));
                    news.setUpdatedAt(rs.getTimestamp("updatedAt"));
                    news.setOrganizationId(rs.getInt("organizationId"));
                    news.setStatus(rs.getString("status"));
                    news.setOrganizationName(rs.getString("organizationName"));
                    list.add(news);
                }
            }
        }

        return list;
    }

    public int getFilteredNewsCount(int organizationId, String status, String search) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM News n ");

        List<String> whereClauses = new ArrayList<>();
        whereClauses.add("n.organization_id = ?");

        if (status != null && !status.trim().isEmpty()) {
            whereClauses.add("n.status = ?");
        }
        if (search != null && !search.trim().isEmpty()) {
            whereClauses.add("n.title COLLATE SQL_Latin1_General_CP1_CI_AI LIKE ?");
        }

        sql.append("WHERE ").append(String.join(" AND ", whereClauses));

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, organizationId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx++, status.trim());
            }
            if (search != null && !search.trim().isEmpty()) {
                ps.setString(idx++, "%" + search.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    //Detail
    public New getNewsDetailById(int id, int organizationId) {

        String sql = """
                    SELECT
                        n.id,
                        n.title,
                        n.content,
                        n.images,
                        n.created_at AS createdAt,
                        n.updated_at AS updatedAt,
                        n.organization_id AS organizationId,
                        n.status,
                        u.full_name AS organizationName
                    FROM News AS n
                    JOIN Users AS u
                        ON n.organization_id = u.id
                    WHERE n.id = ? AND n.organization_id = ?;
                 """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, organizationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new New(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("images"),
                            rs.getTimestamp("createdAt"),
                            rs.getTimestamp("updatedAt"),
                            rs.getInt("organizationId"),
                            rs.getString("status"),
                            rs.getString("organizationName"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Update
    public boolean updateNews(int id, int organizationId, String title, String content, String images, String status) {
        String sql = """
        UPDATE News
        SET title = ?, content = ?, images = ?, status = ?, updated_at = GETDATE()
        WHERE id = ? AND organization_id = ?
				""";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, images);
            ps.setString(4, status);
            ps.setInt(5, id);
            ps.setInt(6, organizationId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateNewsWithImage(int id, int organizationId, String title, String content, String status, String imageFileName) {
        String sql = """
        UPDATE News
        SET title = ?, content = ?, status = ?, images = ?, updated_at = GETDATE()
        WHERE id = ? AND organization_id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, status);
            ps.setString(4, imageFileName);
            ps.setInt(5, id);
            ps.setInt(6, organizationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Create
    public int insertNews(int organizationId, String title, String content, String imageFileName) {
        String sql = """
        INSERT INTO News (title, content, images, created_at, updated_at, organization_id)
        VALUES (?, ?, ?, GETDATE(), GETDATE(), ?)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, imageFileName);
            ps.setInt(4, organizationId);

            int affected = ps.executeUpdate();
            if (affected == 0) {
                return -1;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    //Delete 
    public boolean deleteNewsByIdAndOrgId(int id, int organizationId) {
        String sql = "DELETE FROM News WHERE id = ? AND organization_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, organizationId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
