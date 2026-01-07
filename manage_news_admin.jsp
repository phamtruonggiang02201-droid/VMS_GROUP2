<%-- 
    Document   : manage_news_admin
    Created on : Sep 23, 2025, 8:56:21 PM
    Author     : Admin
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Kiểm duyệt nội dung</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet">
    </head>
    <body>
        <div class="d-flex" style="min-height: 100vh;">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <div class="flex-grow-1 p-4" style="background-color: #f8f9fa; min-height: 100vh;">
                <div class="container-fluid">
                    <h3 class="fw-bold mb-4">Quản lí bài đăng</h3>

                    <!-- Thông báo -->
                    <c:if test="${param.success != null}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle"></i> Cập nhật trạng thái thành công!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.error != null}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle"></i> Có lỗi xảy ra!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Bộ lọc -->
                    <form method="get" action="AdminNewsServlet" class="card mb-4">
                        <div class="card-body">
                            <div class="row g-3">
                                <!-- Sắp xếp -->
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold">Sắp xếp:</label>
                                    <select name="sort" class="form-select">
                                        <option value="newest" ${sortOrder == 'newest' ? 'selected' : ''}>Ngày mới nhất</option>
                                        <option value="oldest" ${sortOrder == 'oldest' ? 'selected' : ''}>Ngày cũ nhất</option>
                                    </select>
                                </div>

                                <!-- Trạng thái -->
                                <div class="col-md-3">
                                    <label class="form-label fw-semibold">Trạng thái:</label>
                                    <select name="status" class="form-select">
                                        <option value="all" ${statusFilter == 'all' ? 'selected' : ''}>Tất cả</option>
                                        <option value="pending" ${statusFilter == 'pending' ? 'selected' : ''}>Chờ duyệt</option>
                                        <option value="published" ${statusFilter == 'published' ? 'selected' : ''}>Đã duyệt</option>
                                        <option value="rejected" ${statusFilter == 'rejected' ? 'selected' : ''}>Từ chối</option>
                                        <option value="hidden" ${statusFilter == 'hidden' ? 'selected' : ''}>Đã ẩn</option>
                                    </select>
                                </div>

                                <!-- Nút -->
                                <div class="col-md-6 d-flex align-items-end gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-search"></i> Lọc
                                    </button>
                                    <a href="AdminNewsServlet" class="btn btn-secondary">
                                        <i class="bi bi-arrow-counterclockwise"></i> Reset
                                    </a>
                                </div>
                            </div>
                        </div>
                    </form>

                    <!-- Bảng dữ liệu -->
                    <div class="card">
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover align-middle">
                                    <thead class="table-light">
                                        <tr>
                                            <th style="width:5%;">STT</th>
                                            <th style="width:30%;">Tiêu đề</th>
                                            <th style="width:15%;">Tổ chức</th>
                                            <th style="width:18%;">Ngày tạo</th>
                                            <th style="width:12%;">Trạng thái</th>
                                            <th style="width:10%;">Thao tác</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:choose>
                                            <c:when test="${empty newsList}">
                                                <tr>
                                                    <td colspan="6" class="text-center text-muted py-4">
                                                        <i class="bi bi-inbox fs-1 d-block mb-2"></i>
                                                        Không có bài viết nào
                                                    </td>
                                                </tr>
                                            </c:when>
                                            <c:otherwise>
                                                <c:forEach var="news" items="${newsList}" varStatus="status">
                                                    <tr>
                                                        <td class="text-center">${startRecord + status.index}</td>
                                                        <td>
                                                            <div class="text-truncate" style="max-width: 300px;" title="${news.title}">
                                                                ${news.title}
                                                            </div>
                                                        </td>
                                                        <td>${news.organizationName}</td>
                                                        <td>
                                                            <fmt:formatDate value="${news.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${news.status == 'pending'}">
                                                                    <span class="badge bg-warning text-dark">
                                                                        <i class="bi bi-clock"></i> Chờ duyệt
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${news.status == 'published'}">
                                                                    <span class="badge bg-success">
                                                                        <i class="bi bi-check-circle"></i> Đã duyệt
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${news.status == 'rejected'}">
                                                                    <span class="badge bg-danger">
                                                                        <i class="bi bi-x-circle"></i> Từ chối
                                                                    </span>
                                                                </c:when>
                                                                <c:when test="${news.status == 'hidden'}">
                                                                    <span class="badge bg-secondary">
                                                                        <i class="bi bi-eye-slash"></i> Đã ẩn
                                                                    </span>
                                                                </c:when>
                                                            </c:choose>
                                                        </td>
                                                        <td class="text-center">
                                                            <a href="AdminNewsServlet?action=detail&id=${news.id}" 
                                                               class="btn btn-sm btn-info text-white">
                                                                <i class="bi bi-eye"></i> Xem
                                                            </a>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </c:otherwise>
                                        </c:choose>
                                    </tbody>
                                </table>
                            </div>

                            <!-- Phân trang -->
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <span class="text-muted">
                                    Hiển thị ${startRecord} - ${endRecord} trong tổng ${totalRecords} bài
                                </span>
                                <ul class="pagination pagination-sm mb-0">
                                    <!-- Nút Trước -->
                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                        <a class="page-link" href="AdminNewsServlet?page=${currentPage - 1}&status=${statusFilter}&sort=${sortOrder}">
                                            <i class="bi bi-chevron-left"></i> Trước
                                        </a>
                                    </li>

                                    <!-- Số trang -->
                                    <c:forEach var="i" begin="1" end="${totalPages}">
                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                            <a class="page-link" href="AdminNewsServlet?page=${i}&status=${statusFilter}&sort=${sortOrder}">
                                                ${i}
                                            </a>
                                        </li>
                                    </c:forEach>

                                    <!-- Nút Sau -->
                                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                        <a class="page-link" href="AdminNewsServlet?page=${currentPage + 1}&status=${statusFilter}&sort=${sortOrder}">
                                            Sau <i class="bi bi-chevron-right"></i>
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

