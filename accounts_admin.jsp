<%-- 
    Document   : accounts_admin
    Created on : Sep 21, 2025, 9:34:56 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Quản lí tài khoản</title>
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
                <h1>Quản lí tài khoản</h1>               
                <c:if test="${param.msg == 'created_successfully'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert" id="create-success-alert">
                        Đã tạo tài khoản mới thành công.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.msg == 'lock_org_48h_error'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-circle me-2"></i>Không thể khóa tài khoản Organization! Tổ chức này có sự kiện đang active trong vòng 48 giờ tới. Admin không được khóa tài khoản Organization khi có sự kiện active trong 48h sắp tới.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <c:if test="${param.msg == 'lock_error'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-circle me-2"></i>Không thể khóa/mở khóa tài khoản. Vui lòng thử lại!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                <!-- Yêu cầu 1: Thêm nút Add Account + Filter + Search -->
                <div class="d-flex justify-content-between align-items-center mb-3 gap-2 flex-nowrap">
                    <!-- Filter + Search (cùng một form) -->
                    <form action="AdminAccountServlet" method="get" class="d-flex align-items-center gap-3 flex-nowrap flex-grow-1" style="flex: 1;">
                        <select name="role" class="form-select" style="min-width: 180px;">
                            <option value="">-- Vai trò --</option>
                            <option value="admin" ${'admin' == selectedRole ? 'selected' : ''}>Admin</option>
                            <option value="organization" ${'organization' == selectedRole ? 'selected' : ''}>Organization</option>
                            <option value="volunteer" ${'volunteer' == selectedRole ? 'selected' : ''}>Volunteer</option>
                        </select>

                        <select name="status" class="form-select" style="min-width: 180px;">
                            <option value="">-- Trạng thái --</option>
                            <option value="active" ${'active' == selectedStatus ? 'selected' : ''}>Active</option>
                            <option value="inactive" ${'inactive' == selectedStatus ? 'selected' : ''}>Inactive</option>
                        </select>

                        <input type="text" name="search" class="form-control flex-grow-1" placeholder="Tìm tài khoản..." value="${fn:escapeXml(searchText)}" />

                        <button type="submit" class="btn btn-danger d-inline-flex align-items-center gap-2 text-nowrap px-3" style="height: 40px;">
                            <i class="bi bi-filter"></i> Lọc
                        </button>
                        <a href="<%= request.getContextPath() %>/AdminAccountServlet" class="btn btn-secondary d-inline-flex align-items-center justify-content-center gap-2 text-nowrap px-4" style="min-width: 140px; height: 40px;">
                            <i class="bi bi-trash"></i> Khôi phục
                        </a>
                    </form>

                    <a href="<%= request.getContextPath() %>/admin/AdminAddAccountServlet" class="btn btn-primary">
                        <i class="bi bi-plus-circle"></i> Tạo tài khoản
                    </a>
                </div>
                <a href="<%= request.getContextPath() %>/admin/AdminSendNotificationServlet?action=all" class="btn btn-info text-black mb-4">
                    Gửi thông báo chung
                </a>


                <!-- Bảng dữ liệu -->
                <table class="table table-bordered table-hover">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Tài khoản</th>
                            <th>Vai trò</th>
                            <th>Trạng thái</th>
                            <th>Thao Tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="acc" items="${accounts}">
                            <tr>
                                <td>${acc.id}</td>
                                <td>${acc.username}</td>

                                <td>${acc.role}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${acc.status}">
                                            <span class="badge bg-success">
                                                <i class="bi bi-circle-fill me-1"></i> Hoạt động
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-danger">
                                                <i class="bi bi-circle-fill me-1"></i> Ngừng hoạt động
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="action-icons">
                                        <a class="btn btn-primary btn-sm btn-icon" title="Xem chi tiết"
                                           href="<%= request.getContextPath() %>/admin/detail_accounts_admin.jsp?id=${acc.id}">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                        <!-- Nút gửi thông báo cá nhân -->
                                        <a class="btn btn-warning btn-sm btn-icon" title="Gửi thông báo"
                                           href="<%= request.getContextPath() %>/admin/AdminSendNotificationServlet?action=individual&accountId=${acc.id}">
                                            <i class="bi bi-bell"></i>
                                        </a>
                                        <c:choose>
                                            <c:when test="${acc.role == 'admin'}">
                                                <span class="d-inline-block" tabindex="0" data-bs-toggle="tooltip" data-bs-title="Không thể khóa tài khoản quyền admin">
                                                    <button type="button" class="btn btn-secondary btn-sm btn-icon opacity-50" style="pointer-events: none;" disabled aria-disabled="true">
                                                        <i class="bi bi-lock"></i>
                                                    </button>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <c:url var="toggleUrl" value="/AdminAccountServlet">
                                                    <c:param name="action" value="toggle"/>
                                                    <c:param name="id" value="${acc.id}"/>
                                                    <c:if test="${not empty selectedRole}">
                                                        <c:param name="role" value="${selectedRole}"/>
                                                    </c:if>
                                                    <c:if test="${not empty selectedStatus}">
                                                        <c:param name="status" value="${selectedStatus}"/>
                                                    </c:if>
                                                    <c:if test="${not empty searchText}">
                                                        <c:param name="search" value="${searchText}"/>
                                                    </c:if>
                                                    <c:if test="${not empty currentPage}">
                                                        <c:param name="page" value="${currentPage}"/>
                                                    </c:if>
                                                </c:url>
                                                <a href="${toggleUrl}"
                                                   class="btn ${acc.status ? 'btn-danger' : 'btn-success'} btn-sm btn-icon" 
                                                   title="${acc.status ? 'Khóa tài khoản' : 'Mở khóa tài khoản'}">
                                                    <i class="bi ${acc.status ? 'bi-lock' : 'bi-unlock'}"></i>
                                                </a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- Pagination -->
                <div class="d-flex justify-content-between align-items-center">
                    <div class="text-muted small">
                        Trang ${currentPage} / ${totalPages} · Tổng: ${totalItems}
                    </div>
                    <nav aria-label="Account pagination">
                        <ul class="pagination mb-0">
                            <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                <c:url var="prevUrl" value="AdminAccountServlet">
                                    <c:param name="role" value="${selectedRole}" />
                                    <c:param name="status" value="${selectedStatus}" />
                                    <c:param name="search" value="${searchText}" />
                                    <c:param name="page" value="${currentPage - 1}" />
                                </c:url>
                                <a class="page-link" href="${prevUrl}">Trước</a>
                            </li>

                            <c:forEach var="p" begin="1" end="${totalPages}">
                                <c:url var="pUrl" value="AdminAccountServlet">
                                    <c:param name="role" value="${selectedRole}" />
                                    <c:param name="status" value="${selectedStatus}" />
                                    <c:param name="search" value="${searchText}" />
                                    <c:param name="page" value="${p}" />
                                </c:url>
                                <li class="page-item ${p == currentPage ? 'active' : ''}">
                                    <a class="page-link" href="${pUrl}">${p}</a>
                                </li>
                            </c:forEach>

                            <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                <c:url var="nextUrl" value="AdminAccountServlet">
                                    <c:param name="role" value="${selectedRole}" />
                                    <c:param name="status" value="${selectedStatus}" />
                                    <c:param name="search" value="${searchText}" />
                                    <c:param name="page" value="${currentPage + 1}" />
                                </c:url>
                                <a class="page-link" href="${nextUrl}">Sau</a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
