<%-- 
    Document   : events_admin
    Created on : Sep 16, 2025, 9:25:20 PM
    Author     : Admin
--%>

<%@page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>Quản lý sự kiện</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet" />
        <style>
            .filter-bar .form-select {
                height: 31px !important;
                line-height: 1.5;
                padding: 0.25rem 0.5rem;
                box-sizing: border-box;
            }
            .filter-bar .btn {
                height: 31px !important;
                line-height: 1.5;
                display: inline-flex;
                align-items: center;
                padding: 0.25rem 0.5rem;
                box-sizing: border-box;
                white-space: nowrap;
            }
            .filter-bar .form-group {
                display: flex;
                flex-direction: column;
            }
            .filter-bar .form-group.d-flex {
                flex-direction: row !important;
                align-items: flex-end;
            }
            /* Giảm kích thước button trong bảng */
            .table .btn-sm {
                padding: 0.25rem 0.5rem;
                font-size: 0.85rem;
                line-height: 1.4;
            }
            .table .btn-sm i {
                font-size: 0.9rem;
            }
        </style>
    </head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <div class="main-content">
                <h2>Quản lý sự kiện</h1>

                    <c:if test="${param.lockError == 'event_ended'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-circle me-2"></i>Không thể khóa sự kiện! Sự kiện này đã kết thúc (end_date đã qua). Không thể thay đổi trạng thái sự kiện đã kết thúc.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <c:if test="${param.unlockError == 'event_ended'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-circle me-2"></i>Không thể mở khóa sự kiện! Sự kiện này đã kết thúc (end_date đã qua). Không thể thay đổi trạng thái sự kiện đã kết thúc.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Success/Error Messages -->
                    <c:if test="${param.lockSuccess == 'true'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert" id="lock-success-alert">
                            <i class="bi bi-check-circle me-2"></i>Khóa sự kiện thành công!
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.lockError == '24h_restriction'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-circle me-2"></i>Không thể khóa sự kiện! Sự kiện này sẽ diễn ra trong vòng 24 giờ tới. Admin không được khóa sự kiện trước 24h diễn ra sự kiện.
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.lockError == 'general' || param.lockError == 'true'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-circle me-2"></i>Có lỗi xảy ra khi khóa sự kiện!
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.unlockSuccess == 'true'}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert" id="unlock-success-alert">
                            <i class="bi bi-check-circle me-2"></i>Mở khóa sự kiện thành công!
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>
                    <c:if test="${param.unlockError == 'true'}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="bi bi-exclamation-circle me-2"></i>Có lỗi xảy ra khi mở khóa sự kiện!
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <!-- Filter Section -->
                    <div class="filter-bar mb-4 d-flex flex-wrap justify-content-between align-items-end gap-3">
                        <form action="AdminEventsServlet" method="get" class="d-flex flex-wrap gap-2 align-items-end" style="flex: 1;">
                            <!-- Status Filter -->
                            <div class="form-group" style="min-width: 150px;">
                                <label for="status" class="form-label small mb-1">Trạng thái:</label>
                                <select name="status" id="status" class="form-select form-select-sm">
                                    <option value="">Tất cả</option>
                                    <option value="active" ${currentStatus == 'active' ? 'selected' : ''}>Active</option>
                                    <option value="inactive" ${currentStatus == 'inactive' ? 'selected' : ''}>Inactive</option>
                                    <option value="closed" ${currentStatus == 'closed' ? 'selected' : ''}>Closed</option>
                                </select>
                            </div>

                            <!-- Category Filter -->
                            <div class="form-group" style="min-width: 150px;">
                                <label for="category" class="form-label small mb-1">Loại sự kiện:</label>
                                <select name="category" id="category" class="form-select form-select-sm">
                                    <option value="">Tất cả</option>
                                    <c:forEach var="cat" items="${categories}">
                                        <option value="${cat.name}" ${currentCategory == cat.name ? 'selected' : ''}>${cat.name}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Visibility Filter -->
                            <div class="form-group" style="min-width: 150px;">
                                <label for="visibility" class="form-label small mb-1">Chế độ:</label>
                                <select name="visibility" id="visibility" class="form-select form-select-sm">
                                    <option value="">Tất cả</option>
                                    <option value="public" ${currentVisibility == 'public' ? 'selected' : ''}>Public</option>
                                    <option value="private" ${currentVisibility == 'private' ? 'selected' : ''}>Private</option>
                                </select>
                            </div>

                            <div class="form-group d-flex flex-row gap-2" style="flex-wrap: nowrap;">
                                <button type="submit" class="btn btn-primary btn-sm">
                                    <i class="bi bi-funnel me-1"></i>Lọc
                                </button>
                                <a href="AdminEventsServlet" class="btn btn-secondary btn-sm">
                                    <i class="bi bi-arrow-counterclockwise me-1"></i>Đặt lại
                                </a>
                            </div>
                            <input type="hidden" name="page" value="1" />
                        </form>
                    </div>

                    <!-- Events Table -->
                    <div class="table-responsive shadow-sm mb-0">
                        <table class="table table-striped table-hover align-middle mb-0">
                            <thead class="table-dark">
                                <tr>
                                    <th style="width: 50px;">STT</th>
                                    <th>Tên sự kiện</th>
                                    <th>Loại sự kiện</th>
                                    <th>Chế độ</th>
                                    <th class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:if test="${empty events}">
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-4">
                                            <i class="bi bi-inbox me-2"></i>Không có sự kiện nào
                                        </td>
                                    </tr>
                                </c:if>

                                <c:forEach var="event" items="${events}" varStatus="loop">
                                    <tr class="bg-white">
                                        <td>${loop.index + 1}</td>
                                        <td>
                                            <strong>${fn:escapeXml(event.title)}</strong>
                                            <br>
                                            <small class="text-muted">
                                                <c:choose>
                                                    <c:when test="${event.status == 'active'}">
                                                        <span class="badge bg-success">Active</span>
                                                    </c:when>
                                                    <c:when test="${event.status == 'inactive'}">
                                                        <span class="badge bg-secondary">Inactive</span>
                                                    </c:when>
                                                    <c:when test="${event.status == 'closed'}">
                                                        <span class="badge bg-dark">Closed</span>
                                                    </c:when>
                                                </c:choose>
                                            </small>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty event.categoryName}">
                                                    ${fn:escapeXml(event.categoryName)}
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted">Chưa phân loại</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${event.visibility == 'public'}">
                                                    <span class="badge bg-info">Public</span>
                                                </c:when>
                                                <c:when test="${event.visibility == 'private'}">
                                                    <span class="badge bg-warning text-dark">Private</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td class="text-center">
                                            <form action="AdminEventDetailServlet" method="get" style="display:inline;">
                                                <input type="hidden" name="id" value="${event.id}">
                                                <button type="submit" class="btn btn-primary btn-sm me-1" title="Xem chi tiết">
                                                    <i class="bi bi-eye"></i> Xem chi tiết
                                                </button>
                                            </form>
                                            <c:if test="${event.status != 'inactive'}">
                                                <form action="AdminEventLockServlet" method="post" style="display:inline;" 
                                                      onsubmit="return confirm('Bạn có chắc chắn muốn khóa sự kiện này?');">
                                                    <input type="hidden" name="id" value="${event.id}">
                                                    <input type="hidden" name="status" value="${currentStatus}">
                                                    <input type="hidden" name="category" value="${currentCategory}">
                                                    <input type="hidden" name="visibility" value="${currentVisibility}">
                                                    <input type="hidden" name="page" value="${currentPage}">
                                                    <button type="submit" class="btn btn-danger btn-sm" title="Khóa sự kiện">
                                                        <i class="bi bi-lock"></i> Khóa
                                                    </button>
                                                </form>
                                            </c:if>
                                            <c:if test="${event.status == 'inactive'}">
                                                <form action="AdminEventUnlockServlet" method="post" style="display:inline;" 
                                                      onsubmit="return confirm('Bạn có chắc chắn muốn mở khóa sự kiện này?');">
                                                    <input type="hidden" name="id" value="${event.id}">
                                                    <input type="hidden" name="status" value="${currentStatus}">
                                                    <input type="hidden" name="category" value="${currentCategory}">
                                                    <input type="hidden" name="visibility" value="${currentVisibility}">
                                                    <input type="hidden" name="page" value="${currentPage}">
                                                    <button type="submit" class="btn btn-success btn-sm" title="Mở khóa sự kiện">
                                                        <i class="bi bi-unlock"></i> Mở khóa
                                                    </button>
                                                </form>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <!-- Pagination -->
                    <div class="d-flex justify-content-between align-items-center">
                        <div class="text-muted small">
                            Trang ${currentPage} / ${totalPages} · Tổng: ${totalItems}
                        </div>
                        <nav aria-label="Event pagination">
                            <ul class="pagination mb-0">
                                <li class="page-item ${currentPage <= 1 ? 'disabled' : ''}">
                                    <c:url var="prevUrl" value="AdminEventsServlet">
                                        <c:param name="status" value="${currentStatus}" />
                                        <c:param name="category" value="${currentCategory}" />
                                        <c:param name="visibility" value="${currentVisibility}" />
                                        <c:param name="page" value="${currentPage - 1}" />
                                    </c:url>
                                    <a class="page-link" href="${prevUrl}">Trước</a>
                                </li>

                                <c:forEach var="p" begin="1" end="${totalPages}">
                                    <c:url var="pUrl" value="AdminEventsServlet">
                                        <c:param name="status" value="${currentStatus}" />
                                        <c:param name="category" value="${currentCategory}" />
                                        <c:param name="visibility" value="${currentVisibility}" />
                                        <c:param name="page" value="${p}" />
                                    </c:url>
                                    <li class="page-item ${p == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="${pUrl}">${p}</a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                    <c:url var="nextUrl" value="AdminEventsServlet">
                                        <c:param name="status" value="${currentStatus}" />
                                        <c:param name="category" value="${currentCategory}" />
                                        <c:param name="visibility" value="${currentVisibility}" />
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
        <script>
                                                      // Tự động ẩn alert thành công sau 3 giây
                                                      const lockSuccessAlert = document.getElementById('lock-success-alert');
                                                      if (lockSuccessAlert) {
                                                          setTimeout(() => {
                                                              const alert = new bootstrap.Alert(lockSuccessAlert);
                                                              alert.close();
                                                          }, 3000);
                                                      }

                                                      const unlockSuccessAlert = document.getElementById('unlock-success-alert');
                                                      if (unlockSuccessAlert) {
                                                          setTimeout(() => {
                                                              const alert = new bootstrap.Alert(unlockSuccessAlert);
                                                              alert.close();
                                                          }, 3000);
                                                      }
        </script>
    </body>
</html>
