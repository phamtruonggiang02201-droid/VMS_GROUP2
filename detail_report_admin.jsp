<%-- 
    Document   : detail_report_admin
    Created on : Nov 10, 2025, 4:12:45 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết báo cáo</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet">
    <head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <div class="main-content p-4">
                <div class="container-fluid">
                    <h3 class="fw-bold mb-4">Chi tiết báo cáo</h3>

                    <c:if test="${not empty report}">
                        <div class="row">
                            <!-- Bên trái: thông tin tổ chức gửi báo cáo -->
                            <div class="col-md-6 mb-4">
                                <div class="card shadow-sm border-0">
                                    <div class="card-header bg-primary text-white fw-bold">
                                        Thông tin báo cáo
                                    </div>
                                    <div class="card-body">
                                        <p><strong>ID báo cáo:</strong> ${report.id}</p>
                                        <p><strong>Tổ chức gửi:</strong> ${report.organizationName}</p>
                                        <p><strong>ID tổ chức:</strong> ${report.organizationId}</p>
                                        <p><strong>Lý do báo cáo:</strong></p>
                                        <p class="border rounded p-2 bg-light">${report.reason}</p>
                                        <p><strong>Ngày gửi:</strong>
                                            <fmt:formatDate value="${report.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                        </p>
                                        <p><strong>Trạng thái hiện tại:</strong>
                                            <span class="badge
                                                  <c:choose>
                                                      <c:when test="${report.status == 'pending'}">bg-warning text-dark</c:when>
                                                      <c:when test="${report.status == 'resolved'}">bg-success</c:when>
                                                      <c:when test="${report.status == 'rejected'}">bg-danger</c:when>
                                                      <c:otherwise>bg-secondary</c:otherwise>
                                                  </c:choose>">
                                                ${report.status}
                                            </span>
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <!-- Bên phải: thông tin volunteer bị báo cáo -->
                            <div class="col-md-6 mb-4">
                                <div class="card shadow-sm border-0">
                                    <div class="card-header bg-info text-white fw-bold">
                                        Thông tin tình nguyện viên bị báo cáo
                                    </div>
                                    <div class="card-body">
                                        <p><strong>Tên tình nguyện viên:</strong> ${report.volunteerName}</p>
                                        <p><strong>Tên tài khoản:</strong> ${report.username}</p>
                                        <p><strong>Điểm đánh giá:</strong>
                                            <c:forEach begin="1" end="5" var="i">
                                                <i class="bi
                                                   <c:if test='${i <= report.rating}'>bi-star-fill text-warning</c:if>
                                                   <c:if test='${i > report.rating}'>bi-star text-muted</c:if>"></i>
                                            </c:forEach>
                                            (${report.rating}/5)
                                        </p>
                                        <p><strong>Nội dung bình luận:</strong></p>
                                        <p class="border rounded p-2 bg-light">${report.comment}</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Form xử lý -->
                        <div class="mt-4 text-center">
                            <form method="post" action="${pageContext.request.contextPath}/AdminReportServlet" class="d-inline">
                                <input type="hidden" name="reportId" value="${report.id}" />
                                <input type="hidden" name="feedbackId" value="${report.feedbackId}" />

                                <c:choose>
                                    <c:when test="${report.status == 'pending'}">
                                        <button type="submit" name="action" value="approve" class="btn btn-success me-2">
                                            <i class="bi bi-check-circle"></i> Duyệt
                                        </button>

                                        <button type="submit" name="action" value="approve_and_lock" class="btn btn-warning me-2">
                                            <i class="bi bi-lock-fill"></i> Duyệt & Khóa tài khoản
                                        </button>

                                        <button type="submit" name="action" value="reject" class="btn btn-danger me-2">
                                            <i class="bi bi-x-circle"></i> Từ chối
                                        </button>
                                    </c:when>

                                    <c:otherwise>
                                        <div class="alert alert-success text-center mb-3">
                                            Báo cáo này đã được xử lý (<strong>${report.status}</strong>).
                                        </div>
                                    </c:otherwise>
                                </c:choose>

                                <a href="${pageContext.request.contextPath}/AdminReportServlet" class="btn btn-secondary">
                                    <i class="bi bi-arrow-left"></i> Quay lại danh sách
                                </a>
                            </form>
                        </div>
                    </c:if>

                    <c:if test="${empty report}">
                        <div class="alert alert-warning mt-4">Không tìm thấy thông tin báo cáo.</div>
                        <a href="${pageContext.request.contextPath}/AdminReportServlet" class="btn btn-secondary mt-2">
                            <i class="bi bi-arrow-left"></i> Quay lại danh sách
                        </a>
                    </c:if>
                </div>
            </div>

        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
