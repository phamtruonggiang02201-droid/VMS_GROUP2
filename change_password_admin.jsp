<%-- 
    Document   : change_password_admin
    Created on : Sep 16, 2025, 10:21:04 PM
    Author     : Admin
--%>

<%@page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet" />
    </head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <!-- Main Content -->
            <div class="main-content">
                <div class="container mt-5">
                    <h2 class="mb-4 text-center">Trang đổi mật khẩu</h2>

                    <% String error = (String) request.getAttribute("error"); %>
                    <% String success = (String) request.getAttribute("success"); %>

                    <% if (error != null) { %>
                    <div class="alert alert-danger"><%= error %></div>
                    <% } %>

<!--                hiển thị thông báo thành công-->
                    <% if (success != null) { %>
                    <div class="alert alert-success"><%= success %></div>
                    <script>
                        setTimeout(function () {
                            window.location.href = '<%= request.getContextPath() %>/auth/login.jsp';
                        }, 2000);
                    </script>
                    <style>
                        form {
                            display: none;
                        }
                    </style>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/ChangePasswordServlet" method="post" class="col-md-6 offset-md-3">
                        <div class="mb-3">
                            <label for="currentPassword" class="form-label">Mật khẩu hiện tại</label>
                            <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                        </div>

                        <div class="mb-3">
                            <label for="newPassword" class="form-label">Mật khẩu mới</label>
                            <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                        </div>

                        <div class="mb-3">
                            <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                            <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                        </div>

                        <div class="d-flex justify-content-end">
                            <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

