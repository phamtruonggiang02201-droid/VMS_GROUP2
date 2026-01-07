<%-- 
    Document   : donate
    Created on : Sep 16, 2025, 2:53:25 PM
    Author     : Admin
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Welfare - Free Bootstrap 4 Template by Colorlib</title>
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
                        <h1 class="mb-3 bread" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">Các nhà tài trợ</h1>
                    </div>
                </div>
            </div>
        </div>


        <section class="ftco-section bg-light">
            <h1 class="text-center mb-4">Các nhà tài trợ của chúng tôi</h1>
            <div class="container">
                <div class="row">
                    <c:forEach var="e" items="${allDonates}"  >
                        <div class="col-lg-4 d-flex mb-sm-4 ftco-animate">
                            <div class="staff">
                                <div class="d-flex mb-4">
                                    <c:choose>
                                        <c:when test="${not empty e.volunteerAvatar}">
                                            <div class="img" style="background-image: url(viewImage?type=avatar&file=${e.volunteerAvatar});"></div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="img" style="background-image: url(images/person_2.jpg);"></div>
                                        </c:otherwise>
                                    </c:choose>

                                    <div class="info ml-5">
                                        <h3><a>${e.volunteerFullName}</a></h3>
                                        <span class="position" style="color: black">
                                            Mã ID:  ${e.volunteerId}
                                        </span>
                                        <div class="text">
                                            <p>
                                                Đã tài trợ 
                                                <span>
                                                    <fmt:formatNumber value="${e.totalAmountDonated}" type="number" groupingUsed="true"/>
                                                </span>VNĐ 
                                                <br/>
                                                Cho <a href="<%=request.getContextPath()%>/volunteer/payment_volunteer.jsp">${e.numberOfEventsDonated}</a> sự kiện
                                            </p>
                                        </div>
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
                                    <li><a href="GuessDonateServlet?page=${currentPage - 1}">&lt;</a></li>
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
                                            <li><a href="GuessDonateServlet?page=${i}">${i}</a></li>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                <!-- Nút Next -->
                                <c:if test="${currentPage < totalPages}">
                                    <li><a href="GuessDonateServlet?page=${currentPage + 1}">&gt;</a></li>
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
