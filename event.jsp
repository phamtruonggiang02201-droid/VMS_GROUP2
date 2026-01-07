<%-- 
    Document   : event
    Created on : Sep 16, 2025, 2:53:41 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@page import="model.Account"%>
<%
    // acc is already declared in navbar.jsp, so we just get it here for eventServlet calculation
    Account accForEvent = (Account) session.getAttribute("account");
    String eventServlet = (accForEvent != null && "volunteer".equals(accForEvent.getRole())) 
                          ? "VolunteerExploreEventServlet" 
                          : "GuessEventServlet";
%>
<!DOCTYPE html>
<html>
    <head>
        <title>Trang danh sách sự kiện</title>
        <%@ include file="layout/header.jsp" %>
    </head>
    <body>
        <!-- Navbar -->
        <%@ include file="layout/navbar.jsp" %>

        <!-- Success Message Alert -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="position-fixed w-100 d-flex justify-content-center" style="top: 80px; z-index: 9999; pointer-events: none;">
                <div class="alert alert-success alert-dismissible fade show shadow-lg" role="alert" id="successAlert" style="pointer-events: auto; min-width: 400px;">
                    <i class="bi bi-check-circle-fill me-2"></i>
                    <strong>${sessionScope.successMessage}</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </div>
            <c:remove var="successMessage" scope="session"/>
            <script>
                setTimeout(function () {
                    var alertElement = document.getElementById('successAlert');
                    if (alertElement) {
                        var bsAlert = new bootstrap.Alert(alertElement);
                        bsAlert.close();
                    }
                }, 3000);
            </script>
        </c:if>

        <div class="hero-wrap" style="background-image: url('images/background.jpg');" data-stellar-background-ratio="0.5">
            <div class="overlay"></div>
            <div class="container">
                <div class="row no-gutters slider-text align-items-center justify-content-center" data-scrollax-parent="true">
                    <div class="col-md-7 ftco-animate text-center" data-scrollax=" properties: { translateY: '70%' }">
                        <p class="breadcrumbs" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }"><span class="mr-2"><a href="<%= request.getContextPath() %>/VolunteerHomeServlet">Home</a></span> <span>Event</span></p>
                        <h1 class="mb-3 bread" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">Danh sách các sự kiện đang diễn ra</h1>
                    </div>
                </div>
            </div>
        </div>

        <section class="ftco-section">
            <h1 class="text-center mb-4">Danh sách các sự kiện đang diễn ra</h1>
            <div class="container">
                <!-- Form lọc -->
                <div class="row mb-4" id="filter-section">
                    <div class="col-12">
                        <form method="get" action="GuessEventServlet#filter-section" class="bg-light p-3 rounded">
                            <div class="row g-2">
                                <!-- Cột 1: Danh mục (trên) + Số lượng (dưới) -->
                                <div class="col-md-3">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold mb-1">Danh mục</label>
                                        <select name="category" class="form-select">
                                            <option value="all" ${selectedCategory == 'all' ? 'selected' : ''}>Tất cả</option>
                                            <c:forEach var="cat" items="${categories}">
                                                <option value="${cat.categoryId}" ${selectedCategory == cat.categoryId.toString() ? 'selected' : ''}>
                                                    ${cat.name}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    
                                    <div>
                                        <label class="form-label fw-bold mb-1">Số lượng</label>
                                        <select name="slotFilter" class="form-select">
                                            <option value="all" ${slotFilter == 'all' ? 'selected' : ''}>Tất cả</option>
                                            <option value="full" ${slotFilter == 'full' ? 'selected' : ''}>Đã đủ</option>
                                            <option value="available" ${slotFilter == 'available' ? 'selected' : ''}>Còn trống</option>
                                        </select>
                                    </div>
                                </div>

                                <!-- Cột 2: Từ ngày (trên) + Trạng thái Đăng ký (dưới) -->
                                <div class="col-md-3">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold mb-1">Từ ngày</label>
                                        <input type="date" name="startDate" class="form-control" value="${startDate}">
                                    </div>
                                    
                                    <div>
                                        <label class="form-label fw-bold mb-1">Trạng thái Đăng ký</label>
                                        <select name="applyStatusFilter" class="form-select">
                                            <option value="all" ${applyStatusFilter == 'all' ? 'selected' : ''}>Tất cả</option>
                                            <option value="applied" ${applyStatusFilter == 'applied' ? 'selected' : ''}>Đã đăng ký</option>
                                            <option value="rejected" ${applyStatusFilter == 'rejected' ? 'selected' : ''}>Bị từ chối</option>
                                        </select>
                                    </div>
                                </div>

                                <!-- Cột 3: Đến ngày (trên) + Trạng thái Donate (dưới) -->
                                <div class="col-md-3">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold mb-1">Đến ngày</label>
                                        <input type="date" name="endDate" class="form-control" value="${endDate}">
                                    </div>
                                    
                                    <div>
                                        <label class="form-label fw-bold mb-1">Trạng thái Donate</label>
                                        <select name="donateFilter" class="form-select">
                                            <option value="all" ${donateFilter == 'all' ? 'selected' : ''}>Tất cả</option>
                                            <option value="donated" ${donateFilter == 'donated' ? 'selected' : ''}>Đã ủng hộ</option>
                                            <option value="not_donated" ${donateFilter == 'not_donated' ? 'selected' : ''}>Chưa ủng hộ</option>
                                        </select>
                                    </div>
                                </div>

                                <!-- Cột 4: Sắp xếp theo (trên) + Nút lọc (dưới) -->
                                <div class="col-md-3">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold mb-1">Sắp xếp theo</label>
                                        <select name="sort" class="form-select">
                                            <option value="desc" ${sortOrder == 'desc' ? 'selected' : ''}>Mới nhất</option>
                                            <option value="asc" ${sortOrder == 'asc' ? 'selected' : ''}>Cũ nhất</option>
                                        </select>
                                    </div>
                                    
                                    <div class="d-flex align-items-end" style="height: 61px;">
                                        <button type="submit" class="btn btn-primary w-100">
                                            <i class="icon-filter"></i> Lọc
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="row">
                    <c:forEach var="e" items="${events}">
                        <div class="col-md-4 d-flex align-items-stretch mb-5">
                            <div class="blog-entry align-self-stretch h-100 w-100">
                                <a class="block-20" style="background-image: url('${pageContext.request.contextPath}/EventImage?file=${e.images}');"></a>                           
                                <div class="text p-4 d-block h-100">
                                    <div class="meta d-flex justify-content-end">
                                        <a href="${pageContext.request.contextPath}/ViewFeedbackEventsServlet?eventId=${e.id}&page=${currentPage}" class="meta-chat">
                                            <span class="icon-comment text-warning"></span> Bình luận
                                        </a>
                                    </div>
                                    <div class="meta mb-3">
                                        <div><strong>Người tổ chức:</strong> <b><i>${e.organizationName}</i></b></div>
                                    </div>

                                    <h3 class="heading mb-1"><a href="${pageContext.request.contextPath}/VolunteerApplyEventServlet?eventId=${e.id}">${e.title}</a></h3>
                                    <p class="text-muted mb-1"><i>Loại sự kiện: ${e.categoryName}</i></p>
                                    <p class="time-loc">
                                        <span class="mr-2"><i class="icon-clock-o"></i> Bắt đầu: 
                                            <fmt:formatDate value="${e.startDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </span><br/>
                                        <span class="mr-2"><i class="icon-clock-o"></i> Kết thúc: 
                                            <fmt:formatDate value="${e.endDate}" pattern="dd/MM/yyyy HH:mm" />
                                        </span><br/>
                                        <span><i class="icon-map-o"></i> Địa điểm : ${e.location}</span>
                                    </p>
                                    <p>${e.description}</p>

                                    <div class="d-flex justify-content-between mt-auto">
                                        <!-- Nút Tham gia sự kiện -->
                                        <p class="mb-0">
                                            <c:choose>
                                                <%-- 1. Full slot (ưu tiên cao nhất) --%>
                                                <c:when test="${e.isFull}">
                                                    <span class="text-secondary">
                                                        <i class="icon-users"></i> Đã đủ slot
                                                    </span>
                                                </c:when>

                                                <%-- 2. Đã đăng ký (pending hoặc approved) --%>
                                                <c:when test="${e.hasApplied}">
                                                    <span class="text-success">
                                                        <i class="icon-check"></i> Đã đăng ký
                                                    </span>
                                                </c:when>

                                                <%-- 3. Bị từ chối >= 3 lần --%>
                                                <c:when test="${e.rejectedCount >= 3}">
                                                    <span class="text-danger">
                                                        <i class="icon-ban"></i> Đã bị từ chối ${e.rejectedCount} lần
                                                    </span>
                                                </c:when>

                                                <%-- 4. Bị từ chối < 3 lần hoặc chưa đăng ký --%>
                                                <c:otherwise>
                                                    <c:if test="${e.rejectedCount > 0}">
                                                        <small class="text-warning d-block">
                                                            ️ Đơn trước bị từ chối (${e.rejectedCount}/3)
                                                        </small>
                                                    </c:if>
                                                    <a href="${pageContext.request.contextPath}/VolunteerApplyEventServlet?eventId=${e.id}">
                                                        ${e.rejectedCount > 0 ? 'Đăng ký lại' : 'Tham gia sự kiện'} <i class="ion-ios-arrow-forward"></i>
                                                    </a>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>

                                        <!-- Nút Ủng hộ / Donate -->
                                        <p class="mb-0">
                                            <c:choose>
                                                <c:when test="${e.hasDonated}">
                                                    <span class="text-success">
                                                        <i class="icon-check"></i> Đã ủng hộ
                                                    </span>
                                                </c:when>
                                                <c:otherwise>
                                                    <% if (acc != null && "volunteer".equals(acc.getRole())) { %>
                                                    <!-- Volunteer: dùng VolunteerDonateFormServlet -->
                                                    <a href="${pageContext.request.contextPath}/VolunteerDonateFormServlet?eventId=${e.id}">
                                                            Ủng hộ <i class="ion-ios-add-circle"></i>
                                                        </a>
                                                    <% } else { %>
                                                        <!-- Guest: dùng GuestDonateFormServlet -->
                                                        <a href="${pageContext.request.contextPath}/GuestDonateFormServlet?eventId=${e.id}">
                                                            Ủng hộ <i class="ion-ios-add-circle"></i>
                                                        </a>
                                                    <% } %>
                                                </c:otherwise>
                                            </c:choose>
                                        </p>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Phân trang -->
                <div class="row mt-5">
                    <div class="col text-center">
                        <div class="block-27">
                            <ul>
                                <!-- Nút Previous -->
                                <c:if test="${currentPage > 1}">
                                    <li><a href="<%= eventServlet %>?page=${currentPage - 1}">&lt;</a></li>
                                    </c:if>
                                    <c:if test="${currentPage == 1}">
                                    <li class="disabled"><span>&lt;</span></li>
                                    </c:if>

                                <!-- Danh sách số trang -->
                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <c:choose>
                                        <c:when test="${i == currentPage}">
                                            <li class="active"><span>${i}</span></li>
                                                </c:when>
                                                <c:otherwise>
                                            <li><a href="<%= eventServlet %>?page=${i}">${i}</a></li>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                <!-- Nút Next -->
                                <c:if test="${currentPage < totalPages}">
                                    <li><a href="<%= eventServlet %>?page=${currentPage + 1}">&gt;</a></li>
                                    </c:if>
                                    <c:if test="${currentPage == totalPages}">
                                    <li class="disabled"><span>&gt;</span></li>
                                    </c:if>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </section>


        <%@ include file="layout/footer.jsp" %>
        <%@ include file="layout/loader.jsp" %>

    </body>
</html>
