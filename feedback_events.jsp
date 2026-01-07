<%-- 
    Document   : feedback_events
    Created on : Nov 11, 2025, 7:48:51 PM
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
        <title>Bình luận sự kiện</title>
        <%@ include file="layout/header.jsp" %>
    </head>
    <body>
        <!-- Navbar -->
        <%@ include file="layout/navbar.jsp" %>

        <div class="hero-wrap" style="background-image: url('images/background.jpg');" data-stellar-background-ratio="0.5">
            <div class="overlay"></div>
            <div class="container">
                <div class="row no-gutters slider-text align-items-center justify-content-center" data-scrollax-parent="true">
                    <div class="col-md-7 ftco-animate text-center" data-scrollax=" properties: { translateY: '70%' }">
                        <p class="breadcrumbs" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">
                            <span class="mr-2"><a href="<%= request.getContextPath() %>/VolunteerHomeServlet">Home</a></span> 
                            <span class="mr-2"><a href="<%= request.getContextPath() %>/<%= eventServlet %>">Sự kiện</a></span> 
                            <span>Bình luận</span>
                        </p>
                        <h1 class="mb-3 bread" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">
                            Bình luận sự kiện: ${event.title}
                        </h1>
                    </div>
                </div>
            </div>
        </div>

        <section class="ftco-section">
            <div class="container">
                <div class="row justify-content-center">
                    <div class="col-md-12">
                        <div class="card shadow-sm">
                            <div class="card-header bg-primary text-white">
                                <h4 class="mb-0">
                                    <i class="icon-comment"></i> Danh sách đánh giá
                                </h4>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${empty feedbacks}">
                                        <div class="alert alert-info text-center">
                                            <i class="icon-info-circle"></i> Chưa có đánh giá nào cho sự kiện này.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="table-responsive">
                                            <table class="table table-bordered table-hover table-striped">
                                                <thead class="table-dark">
                                                    <tr>
                                                        <th style="width: 5%;">STT</th>
                                                        <th style="width: 20%;">Tên người bình luận</th>
                                                        <th style="width: 40%;">Nội dung bình luận</th>
                                                        <th style="width: 15%;">Số sao đánh giá</th>
                                                        <th style="width: 20%;">Ngày đánh giá</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="feedback" items="${feedbacks}" varStatus="loop">
                                                        <tr>
                                                            <td class="text-center">${loop.index + 1}</td>
                                                            <td>
                                                                <strong>${feedback.volunteerName}</strong>
                                                            </td>
                                                            <td>
                                                                <p class="mb-0">${feedback.comment}</p>
                                                            </td>
                                                            <td class="text-center">
                                                                <c:forEach begin="1" end="5" var="i">
                                                                    <c:choose>
                                                                        <c:when test="${i <= feedback.rating}">
                                                                            <span class="icon-star text-warning"></span>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <span class="icon-star-o text-muted"></span>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>
                                                            </td>
                                                            <td>
                                                                <fmt:formatDate value="${feedback.feedbackDate}" pattern="dd/MM/yyyy HH:mm" />
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                        
                                        <!-- Phân trang - LUÔN HIỆN -->
                                        <c:if test="${totalPages >= 1}">
                                            <nav aria-label="Feedback pagination" class="mt-4">
                                                <ul class="pagination justify-content-center">
                                                    <!-- Nút Trước -->
                                                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                        <a class="page-link" href="?eventId=${eventId}&feedbackPage=${currentPage - 1}&page=${returnPage}">
                                                            &lt; Trước
                                                        </a>
                                                    </li>

                                                    <!-- Các số trang -->
                                                    <c:forEach var="i" begin="1" end="${totalPages}">
                                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                            <a class="page-link" href="?eventId=${eventId}&feedbackPage=${i}&page=${returnPage}">${i}</a>
                                                        </li>
                                                    </c:forEach>

                                                    <!-- Nút Sau -->
                                                    <li class="page-item ${currentPage >= totalPages ? 'disabled' : ''}">
                                                        <a class="page-link" href="?eventId=${eventId}&feedbackPage=${currentPage + 1}&page=${returnPage}">
                                                            Sau &gt;
                                                        </a>
                                                    </li>
                                                </ul>
                                            </nav>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>

                                <div class="mt-4 text-center">
                                    <c:choose>
                                        <c:when test="${not empty returnPage && returnPage > 1}">
                                            <a href="${pageContext.request.contextPath}/<%= eventServlet %>?page=${returnPage}" class="btn btn-secondary">
                                                <i class="icon-arrow-left"></i> Quay lại danh sách sự kiện
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/<%= eventServlet %>" class="btn btn-secondary">
                                                <i class="icon-arrow-left"></i> Quay lại danh sách sự kiện
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <%@ include file="layout/footer.jsp" %>
        <%@ include file="layout/loader.jsp" %>

    </body>
</html>
