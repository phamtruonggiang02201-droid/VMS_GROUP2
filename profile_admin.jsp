<%-- 
    Document   : profile_admin
    Created on : Sep 30, 2025, 1:26:44 PM
    Author     : Admin
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Hồ Sơ Quản Trị Viên</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/profile_admin.css" rel="stylesheet" />
    </head>
    <body>
        <div class="content-container d-flex">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <div class="main-content flex-grow-1">
                <div class="container-detail shadow bg-white">
                    <div class="row g-0">
                        <!-- LEFT: Avatar -->
                        <div class="col-md-4 profile-left d-flex flex-column align-items-center justify-content-center text-center p-4">
                            <c:choose>
                                <c:when test="${not empty user.avatar && fn:contains(user.avatar, '://')}">
                                    <img src="${user.avatar}" alt="avatar" class="rounded-circle avatar-lg mb-3 border border-2 border-light shadow-sm" />
                                </c:when>

                                <c:when test="${not empty user.avatar}">
                                    <img src="${pageContext.request.contextPath}/UserAvatar?file=${user.avatar}" alt="avatar" class="rounded-circle avatar-lg mb-3 border border-2 border-light shadow-sm" />
                                </c:when>

                                <c:otherwise>
                                    <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="avatar" class="rounded-circle avatar-lg mb-3 border border-2 border-light shadow-sm" />
                                </c:otherwise>
                            </c:choose>
                            <div class="fw-semibold fs-5">${fn:escapeXml(user.full_name)}</div>
<!--                            <div class="text-muted small">@${user.account.username}</div>-->
                            <div class="mt-3 text-muted fst-italic">${fn:escapeXml(user.job_title)}</div>
                        </div>

                        <!-- RIGHT: Fields -->
                        <div class="col-md-8 p-4">
                            <div class="d-flex justify-content-between align-items-start mb-4">
                                <div>
                                    <h5 class="mb-0">Hồ Sơ Của Quản Trị Viên</h5>
                                    <small class="text-muted">Đang Xem Hồ Sơ Của <strong>${user.account.username}</strong>.</small>
                                </div>
                                <div>
                                    <a href="AdminProfileEditServlet?id=${user.id}" class="btn btn-sm btn-warning text-white">
                                        <i class="bi bi-pencil"></i> Chỉnh Sửa
                                    </a>
                                </div>
                            </div>

                            <form>
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label">ID</label>
                                        <input type="text" class="form-control form-control-sm" value="${user.id}" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Tài Khoản</label>
                                        <input type="text" class="form-control form-control-sm" value="${user.account.username}" readonly>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label">Giới Tính</label>
                                        <c:choose>
                                            <c:when test="${user.gender == 'male'}">
                                                <input type="text" class="form-control form-control-sm" value="Nam" readonly>
                                            </c:when>
                                            <c:when test="${user.gender == 'female'}">
                                                <input type="text" class="form-control form-control-sm" value="Nữ" readonly>
                                            </c:when>
                                            <c:otherwise>Unknown</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Ngày Sinh</label>
                                        <c:choose>
                                            <c:when test="${not empty user.dob}">
                                                <fmt:formatDate value="${user.dob}" pattern="dd-MM-yyyy" var="dobFmt" />
                                                <input type="text" class="form-control form-control-sm" value="${dobFmt}" readonly>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="text" class="form-control form-control-sm" value="N/A" readonly>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label">Địa Chỉ</label>
                                        <input type="text" class="form-control form-control-sm" value="${fn:escapeXml(user.address)}" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Điện Thoại</label>
                                        <input type="text" class="form-control form-control-sm" value="${user.phone}" readonly>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label">Email</label>
                                        <input type="email" class="form-control form-control-sm" value="${user.email}" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Vai Trò</label>
                                        <c:choose>
                                            <c:when test="${user.account.role == 'admin'}">
                                                <input type="text" class="form-control form-control-sm fw-bold text-danger" value="Quản Trị Viên" readonly>
                                            </c:when>
                                            <c:when test="${user.account.role == 'organization'}">
                                                <input type="text" class="form-control form-control-sm fw-bold text-danger" value="Người Tổ Chức" readonly>
                                            </c:when>
                                            <c:when test="${user.account.role == 'volunteer'}">
                                                <input type="text" class="form-control form-control-sm fw-bold text-danger" value="Tình Nguyện Viên" readonly>
                                            </c:when>
                                            <c:otherwise>Unknown</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Ngày Tạo Tài Khoản</label>
                                        <c:choose>
                                            <c:when test="${not empty user.account.createdAt}">
                                                <fmt:formatDate value="${user.account.createdAt}" pattern="HH:mm:ss / dd-MM-yyyy" var="acctCreated"/>
                                                <input type="text" class="form-control form-control-sm" value="${acctCreated}" readonly>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="text" class="form-control form-control-sm" value="N/A" readonly>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <div class="col-12">
                                        <label class="form-label">Giới Thiệu Bản Thân</label>
                                        <textarea class="form-control form-control-sm" style="resize:none;" rows="5" readonly>${fn:escapeXml(user.bio)}</textarea>
                                    </div>
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
