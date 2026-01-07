<%-- 
    Document   : users_admin
    Created on : Sep 16, 2025, 9:30:33 PM
    Author     : Admin
--%>

<%@page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Quản lý người dùng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/user_admin.css" rel="stylesheet" />
    </head>
    <body>
        <div class="content-container">
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <div class="main-content">
                <!-- Header -->
                <h1 class="mb-4 text-black fw-bold">Quản Lý Người Dùng</h1>


                <!-- Filter + Search Card -->
                <div class="card shadow-sm border-0 mb-4">
                    <div class="card-body">
                        <div class="row g-3">
                            <!-- Filter Dropdown -->
                            <div class="col-lg-8 col-md-7">
                                <div class="dropdown d-inline-block me-2">
                                    <button class="btn btn-danger dropdown-toggle" type="button" id="filterDropdown" data-bs-toggle="dropdown" aria-expanded="false">
                                        <i class="bi bi-funnel me-1"></i>Bộ Lọc
                                    </button>
                                    <div class="dropdown-menu p-3 shadow" aria-labelledby="filterDropdown" style="min-width: 280px;">
                                        <form action="AdminUserServlet" method="get">
                                            <div class="mb-3">
                                                <label class="form-label small text-muted">Vai Trò</label>
                                                <select name="role" class="form-select form-select-sm">
                                                    <option value="">Tất cả vai trò</option>
                                                    <option value="admin" ${currentRole == 'admin' ? 'selected' : ''}>Quản Trị Viên</option>
                                                    <option value="organization" ${currentRole == 'organization' ? 'selected' : ''}>Người Tổ Chức</option>
                                                    <option value="volunteer" ${currentRole == 'volunteer' ? 'selected' : ''}>Tình Nguyện Viên</option>
                                                </select>
                                            </div>

                                            <div class="mb-3">
                                                <label class="form-label small text-muted">Giới Tính</label>
                                                <select name="gender" class="form-select form-select-sm">
                                                    <option value="">Tất cả giới tính</option>
                                                    <option value="male" ${currentGender == 'male' ? 'selected' : ''}>Nam</option>
                                                    <option value="female" ${currentGender == 'female' ? 'selected' : ''}>Nữ</option>
                                                </select>
                                            </div>

                                            <input type="hidden" name="search" value="${fn:escapeXml(currentSearch)}" />
                                            <input type="hidden" name="sort" value="${fn:escapeXml(currentSort)}" />

                                            <div class="d-flex gap-2">
                                                <button type="submit" class="btn btn-danger btn-sm flex-fill">
                                                    <i class="bi bi-check-circle me-1"></i>Áp dụng
                                                </button>
                                                <a href="AdminUserServlet" class="btn btn-secondary btn-sm flex-fill">
                                                    <i class="bi bi-arrow-clockwise me-1"></i>Đặt lại
                                                </a>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </div>

                            <!-- Search Form -->
                            <div class="col-lg-4 col-md-5">
                                <form action="AdminUserServlet" method="get" class="d-flex">
                                    <input type="text" name="search" class="form-control form-control-sm" placeholder="Tìm kiếm theo tên..." value="${fn:escapeXml(currentSearch)}" />
                                    <input type="hidden" name="role" value="${fn:escapeXml(currentRole)}" />
                                    <input type="hidden" name="sort" value="${fn:escapeXml(currentSort)}" />
                                    <input type="hidden" name="gender" value="${fn:escapeXml(currentGender)}" />
                                    <button type="submit" class="btn btn-primary btn-sm ms-2">
                                        <i class="bi bi-search"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- User Table Card -->
                <div class="card shadow-sm border-0 mb-4">
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle mb-0">
                                <thead class="table-light">
                                    <tr>
                                        <th class="py-3 ps-4">ID</th>
                                        <th class="py-3">Ảnh</th>
                                        <th class="py-3">Họ và Tên</th>
                                        <th class="py-3">Giới Tính</th>
                                        <th class="py-3">Vai Trò</th>
                                        <th class="py-3 text-center">Hành Động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:if test="${empty users}">
                                        <tr>
                                            <td colspan="6" class="text-center py-5">
                                                <i class="bi bi-inbox text-muted" style="font-size: 3rem;"></i>
                                                <p class="text-muted mt-2 mb-0">Không tìm thấy người dùng nào</p>
                                            </td>
                                        </tr>
                                    </c:if>

                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td class="ps-4 fw-medium">${user.id}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${not empty user.avatar && fn:contains(user.avatar, '://')}">
                                                        <img src="${user.avatar}" alt="avatar" class="rounded-circle" width="45" height="45" style="object-fit: cover; border: 2px solid #f0f0f0;"/>
                                                    </c:when>
                                                    <c:when test="${not empty user.avatar}">
                                                        <img src="${pageContext.request.contextPath}/UserAvatar?file=${user.avatar}" alt="avatar" class="rounded-circle" width="45" height="45" style="object-fit: cover; border: 2px solid #f0f0f0;"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="avatar" class="rounded-circle" width="45" height="45" style="object-fit: cover; border: 2px solid #f0f0f0;"/>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="fw-medium">${user.full_name}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${user.gender == 'male'}">
                                                        <i class="bi bi-gender-male text-primary"></i> Nam
                                                    </c:when>
                                                    <c:when test="${user.gender == 'female'}">
                                                        <i class="bi bi-gender-female text-danger"></i> Nữ
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted">N/A</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${user.account.role == 'admin'}">
                                                        <span class="badge bg-info text-dark">
                                                            Quản Trị Viên
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${user.account.role == 'organization'}">
                                                        <span class="badge bg-success text-white">
                                                            Tổ Chức Viên
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${user.account.role == 'volunteer'}">
                                                        <span class="badge bg-secondary">
                                                            Tình Nguyện Viên
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Unknown</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-center">
                                                <form action="AdminUserDetailServlet" method="get" style="display:inline;">
                                                    <input type="hidden" name="id" value="${user.id}">
                                                    <button type="submit" class="btn btn-primary btn-sm me-1" title="Xem Chi Tiết">
                                                        <i class="bi bi-eye"></i>
                                                    </button>
                                                </form>
                                                <form action="AdminUserEditServlet" method="get" style="display:inline;">
                                                    <input type="hidden" name="id" value="${user.id}">
                                                    <button type="submit" class="btn btn-warning btn-sm" title="Chỉnh Sửa">
                                                        <i class="bi bi-pencil-square"></i>
                                                    </button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Pagination Card -->
                <div class="card shadow-sm border-0">
                    <div class="card-body">
                        <div class="row align-items-center g-3">
                            <!-- Page Info -->
                            <div class="col-md-4 text-center text-md-start">
                                <p class="text-muted mb-0 small">
                                    Trang <strong class="text-primary">${currentPage}</strong> / <strong>${totalPages}</strong>
                                </p>
                            </div>

                            <!-- Pagination Controls -->
                            <div class="col-md-4">
                                <nav aria-label="User list pagination">
                                    <ul class="pagination pagination-sm justify-content-center mb-0">
                                        <!-- Previous -->
                                        <c:url var="prevUrl" value="AdminUserServlet">
                                            <c:param name="page" value="${currentPage - 1}" />
                                            <c:param name="role" value="${fn:escapeXml(currentRole)}" />
                                            <c:param name="search" value="${fn:escapeXml(currentSearch)}" />
                                            <c:param name="sort" value="${fn:escapeXml(currentSort)}" />
                                            <c:param name="gender" value="${fn:escapeXml(currentGender)}" />
                                        </c:url>
                                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                            <a class="page-link" href="${prevUrl}">
                                                <i class="bi bi-chevron-left"></i>
                                            </a>
                                        </li>

                                        <!-- Page Numbers -->
                                        <c:forEach var="i" begin="1" end="${totalPages}">
                                            <c:url var="pageUrl" value="AdminUserServlet">
                                                <c:param name="page" value="${i}" />
                                                <c:param name="role" value="${fn:escapeXml(currentRole)}" />
                                                <c:param name="search" value="${fn:escapeXml(currentSearch)}" />
                                                <c:param name="sort" value="${fn:escapeXml(currentSort)}" />
                                                <c:param name="gender" value="${fn:escapeXml(currentGender)}" />
                                            </c:url>
                                            <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                <a class="page-link" href="${pageUrl}">${i}</a>
                                            </li>
                                        </c:forEach>

                                        <!-- Next -->
                                        <c:url var="nextUrl" value="AdminUserServlet">
                                            <c:param name="page" value="${currentPage + 1}" />
                                            <c:param name="role" value="${fn:escapeXml(currentRole)}" />
                                            <c:param name="search" value="${fn:escapeXml(currentSearch)}" />
                                            <c:param name="sort" value="${fn:escapeXml(currentSort)}" />
                                            <c:param name="gender" value="${fn:escapeXml(currentGender)}" />
                                        </c:url>
                                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                            <a class="page-link" href="${nextUrl}">
                                                <i class="bi bi-chevron-right"></i>
                                            </a>
                                        </li>
                                    </ul>
                                </nav>
                            </div>

                            <!-- Go to Page -->
                            <div class="col-md-4">
                                <form action="AdminUserServlet" method="get" class="d-flex justify-content-center justify-content-md-end align-items-center gap-2">
                                    <label for="gotoPage" class="form-label mb-0 small text-muted text-nowrap">Đến trang:</label>
                                    <input type="number" id="gotoPage" name="page" min="1" max="${totalPages}" value="${currentPage}"
                                           class="form-control form-control-sm" style="width: 70px;">
                                    <input type="hidden" name="role" value="${fn:escapeXml(currentRole)}" />
                                    <input type="hidden" name="search" value="${fn:escapeXml(currentSearch)}" />
                                    <input type="hidden" name="sort" value="${fn:escapeXml(currentSort)}" />
                                    <input type="hidden" name="gender" value="${fn:escapeXml(currentGender)}" />
                                    <button type="submit" class="btn btn-primary btn-sm">
                                        <i class="bi bi-arrow-right"></i>
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>