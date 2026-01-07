<%-- 
    Document   : detail_new_admin
    Created on : Nov 4, 2025, 11:26:39 PM
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
        <title>Chi tiết bài đăng</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet">
    </head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />
            
            <!-- Main Content -->
            <div class="main-content" style="background-color: #f8f9fa; padding: 20px;">
                <div class="container-fluid">
                    <!-- Header -->
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h3 class="fw-bold mb-0">Chi tiết bài đăng</h3>
                    </div>

                    <!-- Thông tin bài đăng -->
                    <div class="card mb-4">
                        <div class="card-body">
                            <!-- Tiêu đề -->
                            <h4 class="fw-bold text-primary mb-4">${news.title}</h4>

                            <!-- Thông tin cơ bản -->
                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">
                                            <i class="bi bi-building text-secondary"></i> Tổ chức:
                                        </label>
                                        <p class="form-control-plaintext bg-light border rounded p-2">
                                            ${news.organizationName}
                                        </p>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">
                                            <i class="bi bi-calendar text-secondary"></i> Ngày tạo:
                                        </label>
                                        <p class="form-control-plaintext bg-light border rounded p-2">
                                            <fmt:formatDate value="${news.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                        </p>
                                    </div>
                                </div>
                            </div>

                            <div class="row mb-4">
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">
                                            <i class="bi bi-info-circle text-secondary"></i> Trạng thái hiện tại:
                                        </label>
                                        <p class="form-control-plaintext">
                                            <c:choose>
                                                <c:when test="${news.status == 'pending'}">
                                                    <span class="badge bg-warning text-dark fs-6">
                                                        <i class="bi bi-clock"></i> Chờ duyệt
                                                    </span>
                                                </c:when>
                                                <c:when test="${news.status == 'published'}">
                                                    <span class="badge bg-success fs-6">
                                                        <i class="bi bi-check-circle"></i> Đã duyệt
                                                    </span>
                                                </c:when>
                                                <c:when test="${news.status == 'rejected'}">
                                                    <span class="badge bg-danger fs-6">
                                                        <i class="bi bi-x-circle"></i> Từ chối
                                                    </span>
                                                </c:when>
                                                <c:when test="${news.status == 'hidden'}">
                                                    <span class="badge bg-secondary fs-6">
                                                        <i class="bi bi-eye-slash"></i> Đã ẩn
                                                    </span>
                                                </c:when>
                                            </c:choose>
                                        </p>
                                    </div>
                                </div>
                                <c:if test="${news.updatedAt != null}">
                                    <div class="col-md-6">
                                        <div class="mb-3">
                                            <label class="form-label fw-bold">
                                                <i class="bi bi-clock-history text-secondary"></i> Cập nhật lần cuối:
                                            </label>
                                            <p class="form-control-plaintext bg-light border rounded p-2">
                                                <fmt:formatDate value="${news.updatedAt}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                            </p>
                                        </div>
                                    </div>
                                </c:if>
                            </div>

                            <hr>

                            <!-- Ảnh bài đăng -->
                            <c:if test="${not empty news.images}">
                                <div class="mb-4">
                                    <label class="form-label fw-bold">Ảnh tiêu đề:</label>
                                    <div class="text-center border rounded p-3 bg-light">
                                        <img src="<%= request.getContextPath() %>/viewImage?type=news&file=${news.images}" 
                                             alt="Ảnh bài đăng" 
                                             class="img-fluid rounded" 
                                             style="max-height: 300px; object-fit: cover;"
                                             onerror="this.style.display='none'; this.parentElement.innerHTML='<p class=text-muted>Không có ảnh</p>'">
                                    </div>
                                </div>
                            </c:if>

                            <!-- Nội dung bài đăng -->
                            <div class="mb-4">
                                <label class="form-label fw-bold">Nội dung:</label>
                                <div class="border rounded bg-white" style="min-height: 200px; white-space: pre-line;">
                                    <p style="margin-left: 5px"> ${news.content}</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Nút thao tác -->
                    <div class="card">
                        <div class="card-header bg-light">
                            <h5 class="mb-0"><i class="bi bi-gear"></i> Hành động</h5>
                        </div>
                        <div class="card-body">
                            <form method="post" action="AdminNewsServlet" onsubmit="return confirm('Bạn có chắc chắn muốn thực hiện hành động này?');">
                                <input type="hidden" name="newsId" value="${news.id}">
                                
                                <div class="d-flex gap-2 flex-wrap">
                                    <c:choose>
                                        <c:when test="${news.status == 'pending'}">
                                            <button type="submit" name="action" value="approve" class="btn btn-success">
                                                <i class="bi bi-check-circle"></i> Duyệt bài
                                            </button>
                                            <button type="submit" name="action" value="reject" class="btn btn-danger">
                                                <i class="bi bi-x-circle"></i> Từ chối
                                            </button>
                                        </c:when>
                                        
                                        <c:when test="${news.status == 'published'}">
                                            <button type="submit" name="action" value="hide" class="btn btn-warning">
                                                <i class="bi bi-eye-slash"></i> Ẩn bài
                                            </button>
                                        </c:when>
                                        
                                        <c:when test="${news.status == 'rejected'}">
                                            <button type="submit" name="action" value="approve" class="btn btn-success">
                                                <i class="bi bi-check-circle"></i> Duyệt lại
                                            </button>
                                        </c:when>
                                        
                                        <c:when test="${news.status == 'hidden'}">
                                            <button type="submit" name="action" value="publish" class="btn btn-primary">
                                                <i class="bi bi-eye"></i> Hiển thị lại
                                            </button>
                                        </c:when>
                                    </c:choose>
                                    
                                    <a href="<%= request.getContextPath() %>/AdminNewsServlet" class="btn btn-secondary">
                                        <i class="bi bi-arrow-left"></i> Quay lại
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>

                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>