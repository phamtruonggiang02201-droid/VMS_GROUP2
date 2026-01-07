<%-- 
    Document   : news_detail
    Created on : Nov 10, 2025, 3:01:47 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>${news.title} - Chi tiết tin tức</title>
        <%@ include file="layout/header.jsp" %>
    </head>
    <body>

        <!-- Navbar -->
        <%@ include file="layout/navbar.jsp" %>

        <!-- Hero Section với ảnh -->
        <c:choose>
            <c:when test="${not empty news.images}">

            </c:when>
            <c:otherwise>
                <div class="hero-wrap" style="background-image: url('<%= request.getContextPath() %>/images/bg_2.jpg');" data-stellar-background-ratio="0.5">
                    <div class="overlay"></div>
                    <div class="container">
                        <div class="row no-gutters slider-text align-items-center justify-content-center" data-scrollax-parent="true">
                            <div class="col-md-9 ftco-animate text-center" data-scrollax=" properties: { translateY: '70%' }">
                                <p class="breadcrumbs" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">
                                    <span class="mr-2"><a href="<%= request.getContextPath() %>/">Home</a></span> 
                                    <span class="mr-2"><a href="<%= request.getContextPath() %>/GuessNewServlet">Tin tức</a></span>
                                    <span>Chi tiết</span>
                                </p>
                            </div>
                        </div>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Nội dung bài viết -->
        <section class="ftco-section">
            <div class="container">
                <div class="row">
                    <div class="col-lg-8 mx-auto ftco-animate">
                        <!-- Tiêu đề -->
                        <h2 class="mb-3 font-weight-bold">${news.title}</h2>

                        <!-- Meta info -->
                        <div class="meta mb-4 d-flex align-items-center">
                            <div>
                                <span class="icon-calendar mr-2"></span>
                                <strong>Ngày đăng:</strong> 
                                <fmt:formatDate value="${news.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                            </div>
                            <div class="ml-4">
                                <span class="icon-person mr-2"></span>
                                <strong>Người đăng:</strong> 
                                <span class="text-primary">${news.organizationName}</span>
                            </div>
                        </div>

                        <c:if test="${news.updatedAt != null}">
                            <div class="mb-4 text-muted">
                                <small>
                                    <em>Cập nhật lần cuối: 
                                        <fmt:formatDate value="${news.updatedAt}" pattern="dd/MM/yyyy HH:mm" />
                                    </em>
                                </small>
                            </div>
                        </c:if>

                        <hr class="mb-4">

                        <!-- Ảnh lớn (nếu có) -->
                        <c:if test="${not empty news.images}">
                            <div class="mb-4 text-center">
                                <img src="<%= request.getContextPath() %>/viewImage?type=news&file=${news.images}" 
                                     alt="${news.title}" 
                                     class="img-fluid rounded shadow"
                                     style="max-width: 100%; height: auto;">
                            </div>
                        </c:if>

                        <!-- Nội dung đầy đủ -->
                        <div class="content-text" style="line-height: 1.8; font-size: 1.1rem; white-space: pre-line;">
                            ${news.content}
                        </div>

                        <hr class="mt-5 mb-4">

                        <!-- Nút quay lại -->
                        <div class="text-center mt-4">
                            <a href="<%= request.getContextPath() %>/GuessNewServlet" class="btn btn-primary px-4 py-2">
                                <span class="icon-arrow-left mr-2"></span> Quay lại danh sách tin tức
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <!-- Share buttons (optional) -->
        <section class="ftco-section bg-light pt-4 pb-4">
            <div class="container">
                <div class="row">
                    <div class="col-lg-8 mx-auto text-center">
                        <h5 class="mb-3">Chia sẻ bài viết này</h5>
                        <div>
                            <a href="https://www.facebook.com/sharer/sharer.php?u=${pageContext.request.requestURL}" 
                               target="_blank" 
                               class="btn btn-primary btn-sm mr-2">
                                <span class="icon-facebook"></span> Facebook
                            </a>
                            <a href="https://twitter.com/intent/tweet?url=${pageContext.request.requestURL}&text=${news.title}" 
                               target="_blank" 
                               class="btn btn-info btn-sm mr-2">
                                <span class="icon-twitter"></span> Twitter
                            </a>
                            <button onclick="copyToClipboard()" class="btn btn-secondary btn-sm">
                                <span class="icon-link"></span> Copy Link
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <%@ include file="layout/footer.jsp" %>
        <%@ include file="layout/loader.jsp" %>

        <script>
            function copyToClipboard() {
                const url = window.location.href;
                navigator.clipboard.writeText(url).then(() => {
                    alert('Đã copy link bài viết!');
                });
            }
        </script>
    </body>
</html>