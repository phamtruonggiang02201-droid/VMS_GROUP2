<%-- 
    Document   : edit_user
    Created on : 10 Oct 2025, 06:07:50
    Author     : Mirinesa
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Chỉnh Sửa Thông Tin Của ${user.account.username}</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/user_admin.css" rel="stylesheet" />
    </head>

    <body>
        <div class="content-container">
            <jsp:include page="layout_admin/sidebar_admin.jsp" />
            <div class="edit-wrapper">
                <!-- Start of unified form -->
                <form action="AdminUserEditServlet" method="post" enctype="multipart/form-data" class="d-flex flex-wrap gap-3 w-100">

                    <!-- Left: Avatar section -->
                    <div class="profile-side">
                        <c:choose>
                            <c:when test="${not empty user.avatar && fn:contains(user.avatar, '://')}">
                                <img src="${user.avatar}" alt="avatar" class="rounded-circle avatar-lg mb-3 border border-2 border-light shadow-sm"/>
                            </c:when>

                            <c:when test="${not empty user.avatar}">
                                <img src="${pageContext.request.contextPath}/UserAvatar?file=${user.avatar}" alt="avatar" class="rounded-circle avatar-lg mb-3 border border-2 border-light shadow-sm" id="avatarPreview"/>
                            </c:when>

                            <c:otherwise>
                                <img src="https://cdn-icons-png.flaticon.com/512/3135/3135715.png" alt="avatar" class="rounded-circle avatar-lg mb-3 border border-2 border-light shadow-sm" />
                            </c:otherwise>
                        </c:choose>
                        <h5>${user.full_name}</h5>
                        <small>@${user.account.username}</small>

                        <div class="upload-section">
                            <label class="form-label">Đổi Ảnh</label>
                            <input type="file" name="avatar" id="avatarInput" class="form-control form-control-sm mt-1" />
                            <c:if test="${not empty errors['avatar']}">
                                <div class="text-danger small mt-1">${errors['avatar']}</div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Right: User form section -->
                    <div class="edit-form-card flex-grow-1">
                        <h5><i class="bi bi-pencil-square me-2"></i>Chỉnh Sửa Chi Tiết Của Người Dùng ${user.account.username}.</h5>

                        <input type="hidden" name="id" value="${user.id}">
                        <input type="hidden" name="username" value="${user.account.username}">

                        <div class="row g-3">
                            <div class="col-md-6 position-relative">
                                <label class="form-label">Họ Tên <span class="form-error">${errors['full_name']}</span></label>
                                <input type="text" name="full_name" class="form-control form-control-sm"
                                       value="${not empty param.full_name ? param.full_name : user.full_name}">
                            </div>

                            <div class="col-md-6 position-relative">
                                <label class="form-label">Nghề Nghiệp <span class="form-error">${errors['job_title']}</span></label>
                                <input type="text" name="job_title" class="form-control form-control-sm"
                                       value="${not empty param.job_title ? param.job_title : user.job_title}">
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Giới Tính</label>
                                <select name="gender" class="form-select form-select-sm">
                                    <option value="male" ${user.gender == 'male' ? 'selected' : ''}>Nam</option>
                                    <option value="female" ${user.gender == 'female' ? 'selected' : ''}>Nữ</option>
                                </select>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Ngày Sinh <span class="form-error">${errors['dob']}</span></label>
                                <input type="date" name="dob" class="form-control form-control-sm"
                                       value="${not empty param.dob ? param.dob : user.dob}">
                            </div>

                            <div class="col-md-6 position-relative">
                                <label class="form-label">Địa Chỉ <span class="form-error">${errors['address']}</span></label>
                                <input type="text" name="address" class="form-control form-control-sm"
                                       value="${not empty param.address ? param.address : user.address}">
                            </div>

                            <div class="col-md-6 position-relative">
                                <label class="form-label">Điện Thoại <span class="form-error">${errors['phone']}</span></label>
                                <input type="text" name="phone" class="form-control form-control-sm"
                                       value="${not empty param.phone ? param.phone : user.phone}">
                            </div>

                            <div class="col-md-12 position-relative">
                                <label class="form-label">Email <span class="form-error">${errors['email']}</span></label>
                                <input type="email" name="email" class="form-control form-control-sm"
                                       value="${not empty param.email ? param.email : user.email}">
                            </div>
                        </div>

                        <div class="form-section">
                            <label class="form-label">Giới Thiệu Bản Thân </label>
                            <textarea class="form-control form-control-sm" name="bio" rows="6" style="resize:none;">${user.bio}</textarea>
                        </div>

                        <div class="action-buttons">
                            <a href="AdminUserServlet" class="btn btn-secondary btn-sm btn-rounded px-3">
                                <i class="bi bi-arrow-left"></i> Trở Về Danh Sách
                            </a>
                            <a href="AdminUserDetailServlet?id=${user.id}" class="btn btn-danger btn-sm btn-rounded px-3">
                                ✕ Hủy
                            </a>
                            <button type="submit" class="btn btn-success btn-sm btn-rounded px-3 ms-2">
                                ✓ Lưu Thay Đổi
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="<%= request.getContextPath() %>/admin/js/live_avatar_preview.js"></script>
    </body>
</html>
