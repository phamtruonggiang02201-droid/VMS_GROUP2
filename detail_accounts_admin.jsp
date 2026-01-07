<%-- 
    Document   : detail_accounts_admin
    Purpose    : Hiển thị chi tiết tài khoản (dành cho Admin xem)
                 Sao chép bố cục từ profile_admin.jsp nhưng bỏ các phần: đổi mật khẩu, địa chỉ, giới thiệu
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.sql.*" %>
<%@ page import="utils.DBContext" %>
<%@ page import="dao.AccountDAO" %>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet" />
        <title>Chi tiết tài khoản</title>
    </head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <div class="main-content">
                <div class="main-content container pt-4">
                    <h1 class="text-center mb-4">Chi tiết tài khoản</h1>

                    <%
                        String idParam = request.getParameter("id");
                        Integer accountId = null;
                        try {
                            if (idParam != null) accountId = Integer.parseInt(idParam);
                        } catch (NumberFormatException ignore) {}

                        Account account = null;
                        String fullName = null;
                        String email = null;
                        String phone = null;
                        String avatar = null;
                        String gender = null;
                        Date dob = null;
                        if (accountId != null) {
                            AccountDAO dao = new AccountDAO();
                            account = dao.getAccountById(accountId);
                            try (Connection c = DBContext.getConnection();
                                 PreparedStatement ps = c.prepareStatement("SELECT full_name, email, phone, avatar, gender, dob FROM Users WHERE account_id = ?")) {
                                ps.setInt(1, accountId);
                                try (ResultSet rs = ps.executeQuery()) {
                                    if (rs.next()) {
                                        fullName = rs.getString("full_name");
                                        email = rs.getString("email");
                                        phone = rs.getString("phone");
                                        avatar = rs.getString("avatar");
                                        gender = rs.getString("gender");
                                        dob = rs.getDate("dob");
                                    }
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    %>

                    <div class="profile-card shadow-sm p-4 bg-white rounded">
                        <% if (account == null) { %>
                            <div class="alert alert-danger mb-0">Không tìm thấy tài khoản.</div>
                        <% } else { %>
                        <div class="row">
                            <!-- Cột trái: Avatar -->
                            <div class="col-md-3 border-end">
                                <div class="text-center mb-4">
                                    <%
                                        String avatarUrl;
                                        if (avatar != null && !avatar.isEmpty()) {
                                            // Sử dụng AvatarServlet để serve ảnh từ thư mục cố định
                                            avatarUrl = request.getContextPath() + "/avatar/" + avatar;
                                        } else {
                                            // Ảnh mặc định
                                            avatarUrl = "https://cdn-icons-png.flaticon.com/512/3135/3135715.png";
                                        }
                                    %>
                                    <img src="<%= avatarUrl %>"
                                         class="img-fluid rounded-circle border p-2"
                                         style="width: 150px; height: 150px; object-fit: cover;"
                                         alt="Avatar" />
                                </div>
                                
                            </div>

                            <!-- Cột phải: Thông tin tài khoản -->
                            <div class="col-md-9 ps-4">
                                <form>
                                    <h5 class="mb-3 fw-bold">Thông tin tài khoản</h5>
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label class="form-label">ID</label>
                                            <input type="text" class="form-control" value="<%= account.getId() %>" readonly />
                                        </div>
                                        

                                        <div class="col-md-6">
                                            <label class="form-label">Họ và tên</label>
                                            <input type="text" class="form-control" value="<%= fullName != null ? fullName : "" %>" readonly />
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Vai trò</label>
                                            <input type="text" class="form-control <%= "admin".equalsIgnoreCase(account.getRole()) ? "fw-bold text-danger" : "" %>" value="<%= account.getRole() %>" readonly />
                                        </div>

                                        <div class="col-md-6">
                                            <label class="form-label">Email</label>
                                            <input type="email" class="form-control" value="<%= email != null ? email : "" %>" readonly />
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label">Số điện thoại</label>
                                            <input type="text" class="form-control" value="<%= phone != null ? phone : "" %>" readonly />
                                        </div>

                                        

                                        <div class="col-md-6">
                                            <label class="form-label">Trạng thái</label>
                                            <input type="text" class="form-control" value="<%= account.isStatus() ? "Active" : "Inactive" %>" readonly />
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

