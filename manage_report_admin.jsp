<%-- 
    Document   : manage_feedback_admin
    Created on : Nov 4, 2025, 11:09:09 PM
    Author     : Admin
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Kiểm duyệt nội dung</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet">
    </head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <div class="main-content p-4">
                <div class="container-fluid">
                    <h3 class="fw-bold mb-4">Kiểm duyệt nội dung</h3>

                    <!-- Success/Error Messages -->
                    <c:if test="${param.success == 'true'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="bi bi-check-circle-fill me-2"></i>Xử lý báo cáo thành công!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.error == 'true'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-triangle-fill me-2"></i>Có lỗi xảy ra. Vui lòng thử lại!
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Bộ lọc -->
                    <form method="get" action="${pageContext.request.contextPath}/AdminReportServlet" 
                          class="d-flex justify-content-between align-items-center mb-3 flex-wrap">
                        <div class="d-flex gap-2 align-items-center flex-wrap">
                            <!-- Trạng thái -->
                            <div class="form-group d-flex flex-column">
                                <label class="form-label fw-semibold">Trạng thái:</label>
                                <select name="status" class="form-select form-select-sm" style="width: 160px;">
                                    <option value="all" ${statusFilter == 'all' ? 'selected' : ''}>Tất cả</option>
                                    <option value="pending" ${statusFilter == 'pending' ? 'selected' : ''}>Chờ xử lý</option>
                                    <option value="resolved" ${statusFilter == 'resolved' ? 'selected' : ''}>Đã duyệt</option>
                                    <option value="rejected" ${statusFilter == 'rejected' ? 'selected' : ''}>Từ chối</option>
                                </select>
                            </div>

                            <!-- Sắp xếp -->
                            <div class="form-group d-flex flex-column">
                                <label class="form-label fw-semibold">Sắp xếp:</label>
                                <select name="sort" class="form-select form-select-sm" style="width: 160px;">
                                    <option value="newest" ${sortOrder == 'newest' ? 'selected' : ''}>Mới nhất</option>
                                    <option value="oldest" ${sortOrder == 'oldest' ? 'selected' : ''}>Cũ nhất</option>
                                </select>
                            </div>

                            <!-- Nút Lọc -->
                            <button type="submit" class="btn btn-primary btn-sm" style="min-width:110px; align-self:end;">
                                <i class="bi bi-search"></i> Lọc
                            </button>


                            <!-- Nút Reset -->
                            <a href="${pageContext.request.contextPath}/AdminReportServlet" 
                               class="btn btn-secondary btn-sm" style="min-width:110px; align-self:end;">
                                <i class="bi bi-arrow-counterclockwise"></i> Reset
                            </a>
                        </div>
                    </form>

                    <!-- Bảng dữ liệu -->
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover" style="table-layout: fixed; width: 100%;">
                            <thead class="table-secondary">
                                <tr>
                                    <th style="width:4%;">STT</th>
                                    <th style="width:20%;">Người gửi</th>
                                    <th style="width:15%;">Thời gian gửi</th>
                                    <th style="width:35%;">Lý do báo cáo</th>
                                    <th style="width:13%;">Trạng thái</th>
                                    <th style="width:13%;">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty reportList}">
                                        <tr>
                                            <td colspan="6" class="text-center py-4">
                                                <i class="bi bi-inbox fs-1 text-muted"></i>
                                                <p class="text-muted mt-2 mb-0">Không có báo cáo nào</p>
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="report" items="${reportList}" varStatus="loop">
                                            <tr>
                                                <td>${startRecord + loop.index}</td>
                                                <td>${report.organizationName}</td>
                                                <td>
                                                    <fmt:formatDate value="${report.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                                </td>
                                                <td>${report.reason}</td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${report.status == 'pending'}">
                                                            <span class="badge bg-warning text-dark">Chờ xử lý</span>
                                                        </c:when>
                                                        <c:when test="${report.status == 'resolved'}">
                                                            <span class="badge bg-success">Đã duyệt</span>
                                                        </c:when>
                                                        <c:when test="${report.status == 'rejected'}">
                                                            <span class="badge bg-danger">Từ chối</span>
                                                        </c:when>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/AdminReportServlet?action=detail&id=${report.id}" 
                                                       class="btn btn-primary btn-sm">
                                                        <i class="bi bi-eye"></i> Xem chi tiết
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
                    <c:if test="${not empty reportList}">
                        <div class="d-flex justify-content-between align-items-center mt-3">
                            <span>Hiển thị ${startRecord} - ${endRecord} trong tổng ${totalRecords} báo cáo</span>
                            <ul class="pagination pagination-sm mb-0">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" 
                                       href="${pageContext.request.contextPath}/AdminReportServlet?status=${statusFilter}&sort=${sortOrder}&page=${currentPage - 1}">
                                        Trước
                                    </a>
                                </li>
                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <c:if test="${i == currentPage || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                            <a class="page-link" 
                                               href="${pageContext.request.contextPath}/AdminReportServlet?status=${statusFilter}&sort=${sortOrder}&page=${i}">
                                                ${i}
                                            </a>
                                        </li>
                                    </c:if>
                                </c:forEach>
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" 
                                       href="${pageContext.request.contextPath}/AdminReportServlet?status=${statusFilter}&sort=${sortOrder}&page=${currentPage + 1}">
                                        Sau
                                    </a>
                                </li>
                            </ul>
                        </div>
                    </c:if>

                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>