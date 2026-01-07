<%-- 
    Document   : reports_admin
    Created on : Sep 16, 2025, 10:20:42 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Thông báo - Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet">
        <style>
            .notification-item {
                border-left: 2px solid #0d6efd;
                transition: all 0.3s ease;
            }
            .notification-item:hover {
                background-color: #f8f9fa;
                transform: translateX(5px);
            }
            .notification-item.unread {
                background-color: #fff3cd;
                border-left-color: #dc3545;
            }
            .notification-badge {
                font-size: 0.75rem;
                padding: 0.25rem 0.5rem;
            }
        </style>
    </head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <div class="flex-grow-1 p-4">
                <div class="container-fluid border">
                    <!-- Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2 class="mb-0">Thông báo
                            <c:if test="${totalNotifications > 0}">
                                <span class="badge bg-warning">${totalNotifications}</span>
                            </c:if>
                        </h2>
                        <div class="d-flex gap-2">
                            <!-- Lọc sắp xếp -->
                            <form method="GET" action="<%= request.getContextPath() %>/AdminNotificationServlet" class="d-inline">
                                <input type="hidden" name="page" value="1">
                                <input type="hidden" name="startDate" value="${startDate}">
                                <input type="hidden" name="endDate" value="${endDate}">
                                <select name="sort" class="form-select form-select-sm" onchange="this.form.submit()">
                                    <option value="newest" ${sortOrder == 'newest' ? 'selected' : ''}>Mới nhất</option>
                                    <option value="oldest" ${sortOrder == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                                </select>
                            </form>

                            <!-- Đánh dấu tất cả -->
                            <c:if test="${not empty notifications}">
                                <form method="POST" action="<%= request.getContextPath() %>/AdminNotificationServlet" style="display: inline;">
                                    <input type="hidden" name="action" value="markAllRead">
                                    <button type="submit" class="btn btn-outline-primary btn-sm">
                                        <i class="bi bi-check-all"></i> Đọc tất cả
                                    </button>
                                </form>
                            </c:if>
                        </div>
                    </div>

                    <!-- Form lọc theo ngày -->
                    <div class="card mb-4 shadow-sm">
                        <div class="card-body">
                            <form method="GET" action="<%= request.getContextPath() %>/AdminNotificationServlet" class="row g-3 align-items-end">
                                <div class="col-md-4">
                                    <label for="startDate" class="form-label fw-bold">
                                        <i class="bi bi-calendar-event"></i> Từ ngày
                                    </label>
                                    <input type="date" class="form-control" id="startDate" name="startDate" value="${startDate}">
                                </div>
                                <div class="col-md-4">
                                    <label for="endDate" class="form-label fw-bold">
                                        <i class="bi bi-calendar-check"></i> Đến ngày
                                    </label>
                                    <input type="date" class="form-control" id="endDate" name="endDate" value="${endDate}">
                                </div>
                                <div class="col-md-4">
                                    <input type="hidden" name="page" value="1">
                                    <input type="hidden" name="sort" value="${sortOrder}">
                                    <button type="submit" class="btn btn-primary me-2">
                                        <i class="bi bi-funnel"></i> Lọc
                                    </button>
                                    <a href="<%= request.getContextPath() %>/AdminNotificationServlet" class="btn btn-secondary">
                                        <i class="bi bi-x-circle"></i> Xóa lọc
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Message alert -->
                    <c:if test="${not empty sessionScope.message}">
                        <div class="alert alert-success alert-dismissible fade show">
                            ${sessionScope.message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <c:remove var="message" scope="session"/>
                    </c:if>

                    <!-- Danh sách thông báo -->
                    <c:choose>
                        <c:when test="${empty notifications}">
                            <div class="text-center py-5">
                                <i class="bi bi-bell-slash" style="font-size: 4rem; color: #ccc;"></i>
                                <p class="mt-3 text-muted fs-5">Chưa có thông báo nào</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="list-group">
                                <c:forEach var="noti" items="${notifications}">
                                    <div class="list-group-item notification-item ${noti.isRead ? '' : 'unread'} mb-3 rounded shadow-sm">
                                        <div class="d-flex w-100 justify-content-between align-items-start">
                                            <div class="flex-grow-1">
                                                <!-- Icon theo type -->
                                                <c:choose>
                                                    <c:when test="${noti.type == 'reminder'}">
                                                        <span class="badge bg-warning notification-badge">
                                                            <i class="bi bi-clock"></i> Nhắc nhở
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${noti.type == 'apply'}">
                                                        <span class="badge bg-info notification-badge">
                                                            <i class="bi bi-file-earmark-check"></i> Đơn đăng ký
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${noti.type == 'donation'}">
                                                        <span class="badge bg-success notification-badge">
                                                            <i class="bi bi-cash-coin"></i> Quyên góp
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${noti.type == 'system'}">
                                                        <span class="badge bg-secondary notification-badge">
                                                            <i class="bi bi-gear"></i> Hệ thống
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${noti.type == 'report'}">
                                                        <span class="badge bg-danger notification-badge">
                                                            <i class="bi bi-flag"></i> Báo cáo
                                                        </span>
                                                    </c:when>
                                                </c:choose>

                                                <!-- Người gửi -->
                                                <p class="mb-2 mt-2">
                                                    <strong>Từ: </strong>
                                                    <c:choose>
                                                        <c:when test="${not empty noti.senderName}">
                                                            ${noti.senderName}
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted">Hệ thống</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </p>

                                                <!-- Nội dung -->
                                                <p class="mb-2 fw-bold">${noti.message}</p>

                                                <!-- Sự kiện liên quan -->
                                                <c:if test="${not empty noti.eventTitle}">
                                                    <p class="mb-2 text-primary">
                                                        <i class="bi bi-calendar-event"></i> 
                                                        <strong>Sự kiện:</strong> ${noti.eventTitle}
                                                    </p>
                                                </c:if>

                                                <!-- Thời gian -->
                                                <small class="text-muted">
                                                    <i class="bi bi-clock"></i>
                                                    <fmt:formatDate value="${noti.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                                </small>
                                            </div>

                                            <!-- Nút đánh dấu đã đọc -->
                                            <c:if test="${!noti.isRead}">
                                                <form method="POST" action="<%= request.getContextPath() %>/AdminNotificationServlet" class="ms-3">
                                                    <input type="hidden" name="action" value="markRead">
                                                    <input type="hidden" name="notificationId" value="${noti.id}">
                                                    <button type="submit" class="btn btn-sm btn-outline-success" title="Đánh dấu đã đọc">
                                                        <i class="bi bi-check2"></i>
                                                    </button>
                                                </form>
                                            </c:if>
                                            <c:if test="${noti.isRead}">
                                                <span class="badge bg-success ms-3">
                                                    <i class="bi bi-check-circle"></i> Đã đọc
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>

                            <!-- Phân trang -->
                            <c:if test="${totalPages > 1}">
                                <nav class="mt-1">
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="<%= request.getContextPath() %>/AdminNotificationServlet?page=${currentPage - 1}&sort=${sortOrder}&startDate=${startDate}&endDate=${endDate}">
                                                Trước
                                            </a>
                                        </li>

                                        <!-- Các số trang -->
                                        <c:forEach begin="1" end="${totalPages}" var="i">
                                            <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="<%= request.getContextPath() %>/AdminNotificationServlet?page=${i}&sort=${sortOrder}&startDate=${startDate}&endDate=${endDate}">
                                                        ${i}
                                                    </a>
                                                </li>
                                            </c:if>
                                            <c:if test="${i == 2 && currentPage > 4}">
                                                <li class="page-item disabled"><span class="page-link">...</span></li>
                                                </c:if>
                                                <c:if test="${i == totalPages - 1 && currentPage < totalPages - 3}">
                                                <li class="page-item disabled"><span class="page-link">...</span></li>
                                                </c:if>
                                            </c:forEach>

                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="<%= request.getContextPath() %>/AdminNotificationServlet?page=${currentPage + 1}&sort=${sortOrder}&startDate=${startDate}&endDate=${endDate}">
                                                Sau
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            </c:if>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>