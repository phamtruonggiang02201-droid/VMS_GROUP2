<%-- 
    Document   : send_notification_admin
    Created on : Nov 14, 2025, 1:29:34 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Gửi thông báo cá nhân</title>
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
            <div class="main-content p-4">
                <div class="d-flex align-items-center mb-4">
                    <a href="<%= request.getContextPath() %>/AdminAccountServlet" class="btn btn-secondary me-3">
                        <i class="bi bi-arrow-left"></i> Quay lại
                    </a>
                    <h2 class="mb-0">Gửi thông báo cá nhân</h2>
                </div>

                <!-- Hiển thị thông báo -->
                <c:if test="${param.msg == 'success'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle me-2"></i>Đã gửi thông báo thành công!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                <c:if test="${param.msg == 'error'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-circle me-2"></i>Gửi thông báo thất bại. Vui lòng thử lại!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Card thông tin người nhận -->
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0"><i class="bi bi-person-circle me-2"></i>Thông tin người nhận</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <p><strong>ID tài khoản:</strong> ${account.id}</p>
                                <p><strong>Tên đăng nhập:</strong> ${account.username}</p>
                            </div>
                            <div class="col-md-6">
                                <p><strong>Vai trò:</strong> <span class="badge bg-info">${account.role}</span></p>
                                <c:choose>
                                    <c:when test="${not empty user}">
                                        <p><strong>Họ tên:</strong> ${user.full_name}</p>
                                    </c:when>
                                    <c:otherwise>
                                        <p><strong>Họ tên:</strong> <span class="text-muted">(Chưa cập nhật)</span></p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Form gửi thông báo -->
                <div class="card">
                    <div class="card-header bg-warning">
                        <h5 class="mb-0"><i class="bi bi-envelope me-2"></i>Nội dung thông báo</h5>
                    </div>
                    <div class="card-body">
                        <form action="<%= request.getContextPath() %>/admin/AdminSendNotificationServlet" method="post">
                            <input type="hidden" name="action" value="sendIndividual">
                            <input type="hidden" name="receiverId" value="${account.id}">

                            <div class="mb-3">
                                <label for="message" class="form-label fw-bold">Nội dung thông báo <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="message" name="message" rows="6" 
                                          placeholder="Nhập nội dung thông báo..." required></textarea>
                                <div class="form-text">Tối thiểu 10 ký tự, tối đa 500 ký tự</div>
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-send me-2"></i>Gửi thông báo
                                </button>
                                <a href="<%= request.getContextPath() %>/AdminAccountServlet" class="btn btn-secondary">
                                    <i class="bi bi-x-circle me-2"></i>Hủy
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
