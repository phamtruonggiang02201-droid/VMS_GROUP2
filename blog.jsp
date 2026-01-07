<%-- 
    Document   : blog
    Created on : Sep 16, 2025, 2:53:15 PM
    Author     : Admin
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Trang tin tức</title>
        <%@ include file="layout/header.jsp" %>
        <style>
            .datetime-filter-form {
                background: #f8f9fa;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
            .datetime-input-group {
                display: flex;
                align-items: center;
                gap: 10px;
                flex-wrap: wrap;
            }
            .datetime-input-group label {
                font-weight: bold;
                margin-bottom: 0;
            }
            .datetime-input-group input[type="datetime-local"] {
                padding: 8px 12px;
                border: 1px solid #ced4da;
                border-radius: 5px;
                font-size: 14px;
            }
            .filter-buttons {
                display: flex;
                gap: 10px;
                margin-top: 15px;
            }
            @media (max-width: 768px) {
                .datetime-input-group {
                    flex-direction: column;
                    align-items: stretch;
                }
                .datetime-input-group input[type="datetime-local"] {
                    width: 100%;
                }
            }
        </style>
    </head>
    <body>

        <!-- Navbar -->
        <%@ include file="layout/navbar.jsp" %>

        <div class="hero-wrap" style="background-image: url('images/background.jpg');" data-stellar-background-ratio="0.5">
            <div class="overlay"></div>
            <div class="container">
                <div class="row no-gutters slider-text align-items-center justify-content-center" data-scrollax-parent="true">
                    <div class="col-md-7 ftco-animate text-center" data-scrollax=" properties: { translateY: '70%' }">
                        <h1 class="mb-3 bread" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">Trang tin tức</h1>
                    </div>
                </div>
            </div>
        </div>

        <section class="ftco-section">
            <div class="container">
                <h1 class="text-center mb-4">Các tin tức của sự kiện</h1>
                
                <!-- Date-Time Range Filter Section -->
                <div class="row justify-content-center mb-4">
                    <div class="col-md-10">
                        <div class="datetime-filter-form">
                            <form action="GuessNewServlet" method="get" id="filterForm">
                                <div class="row">
                                    <div class="col-md-5">
                                        <label for="startDateTime">Từ ngày giờ:</label>
                                        <input type="datetime-local" 
                                               class="form-control" 
                                               id="startDateTime" 
                                               name="startDateTime" 
                                               value="${startDateTime}"
                                               max="<%= new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date()) %>"
                                               required>
                                    </div>
                                    <div class="col-md-5">
                                        <label for="endDateTime">Đến ngày giờ:</label>
                                        <input type="datetime-local" 
                                               class="form-control" 
                                               id="endDateTime" 
                                               name="endDateTime" 
                                               value="${endDateTime}"
                                               max="<%= new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm").format(new java.util.Date()) %>"
                                               required>
                                    </div>
                                    <div class="col-md-2 d-flex align-items-end">
                                        <button type="submit" class="btn btn-primary btn-block">
                                            <i class="fa fa-filter"></i> Lọc
                                        </button>
                                    </div>
                                </div>
                                
                                <c:if test="${isFiltered}">
                                    <div class="text-center mt-3">
                                        <a href="GuessNewServlet" class="btn btn-secondary">
                                            <i class="fa fa-times"></i> Xóa bộ lọc
                                        </a>
                                    </div>
                                </c:if>
                            </form>
                            
                            <!-- Error/Warning Messages -->
                            <c:if test="${not empty errorMessage}">
                                <div class="alert alert-danger mt-3 mb-0">
                                    <i class="fa fa-exclamation-circle"></i> <strong>Lỗi:</strong> ${errorMessage}
                                </div>
                            </c:if>
                            
                            <c:if test="${not empty warningMessage}">
                                <div class="alert alert-warning mt-3 mb-0">
                                    <i class="fa fa-exclamation-triangle"></i> <strong>Cảnh báo:</strong> ${warningMessage}
                                </div>
                            </c:if>
                            
                            <c:if test="${isFiltered and empty errorMessage}">
                                <div class="alert alert-success mt-3 mb-0">
                                    <i class="fa fa-check-circle"></i> 
                                    Tìm thấy <strong>${totalNews}</strong> tin tức 
                                    (Trang ${currentPage}/${totalPages})
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
                

                <div class="row d-flex">
                    <c:choose>
                        <c:when test="${empty allNews}">
                            <div class="col-12 text-center py-5">
                                <div class="alert alert-info">
                                    <i class="fa fa-info-circle"></i> 
                                    <c:choose>
                                        <c:when test="${isFiltered}">
                                            Không có tin tức nào trong khoảng thời gian đã chọn.
                                        </c:when>
                                        <c:otherwise>
                                            Chưa có tin tức nào.
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="e" items="${allNews}">
                                <div class="col-md-4 d-flex ftco-animate">
                                    <div class="blog-entry align-self-stretch w-100">
                                        <!-- Ảnh thumbnail -->
                                        <c:choose>
                                            <c:when test="${not empty e.images}">
                                                <a href="ViewNewsDetailServlet?id=${e.id}" class="block-20 d-block" 
                                                   style="background-image: url('<%= request.getContextPath() %>/viewImage?type=news&file=${e.images}'); min-height: 250px;">
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="ViewNewsDetailServlet?id=${e.id}" class="block-20 d-block" 
                                                   style="background-image: url('<%= request.getContextPath() %>/images/no-image.jpg'); min-height: 250px;">
                                                </a>
                                            </c:otherwise>
                                        </c:choose>

                                        <!-- Nội dung -->
                                        <div class="text p-4 d-flex flex-column">
                                            <div class="meta mb-3">
                                                <div class="text-truncate">
                                                    <strong class="text-success">Ngày đăng:</strong>
                                                    <fmt:formatDate value="${e.createdAt}" pattern="dd/MM/yyyy HH:mm" />
                                                    <br/>
                                                    <span class="text-primary">• ${e.organizationName}</span>
                                                </div>
                                            </div>

                                            <h3 class="heading mt-3 text-truncate" style="max-height: 3.6em; overflow: hidden;">
                                                <a href="ViewNewsDetailServlet?id=${e.id}" title="${e.title}">${e.title}</a>
                                            </h3>

                                            <!-- Nội dung giới hạn -->
                                            <p class="flex-grow-1 text-truncate" style="max-height: 4.5em; overflow: hidden;">
                                                <c:choose>
                                                    <c:when test="${fn:length(e.content) > 150}">
                                                        ${fn:substring(e.content, 0, 150)}...
                                                    </c:when>
                                                    <c:otherwise>
                                                        ${e.content}
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>

                                            <a href="ViewNewsDetailServlet?id=${e.id}" class="btn btn-primary btn-sm mt-auto">
                                                Đọc thêm
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Phân trang (works for both normal and filtered views) -->
                <c:if test="${totalPages > 1}">
                    <div class="row mt-5">
                        <div class="col text-center">
                            <div class="block-27">
                                <ul>
                                    <!-- Nút Previous -->
                                    <c:if test="${currentPage > 1}">
                                        <li>
                                            <a href="GuessNewServlet?page=${currentPage - 1}<c:if test='${isFiltered}'>&startDateTime=${startDateTime}&endDateTime=${endDateTime}</c:if>">&lt;</a>
                                        </li>
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
                                                <li>
                                                    <a href="GuessNewServlet?page=${i}<c:if test='${isFiltered}'>&startDateTime=${startDateTime}&endDateTime=${endDateTime}</c:if>">${i}</a>
                                                </li>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>

                                    <!-- Nút Next -->
                                    <c:if test="${currentPage < totalPages}">
                                        <li>
                                            <a href="GuessNewServlet?page=${currentPage + 1}<c:if test='${isFiltered}'>&startDateTime=${startDateTime}&endDateTime=${endDateTime}</c:if>">&gt;</a>
                                        </li>
                                    </c:if>
                                    <c:if test="${currentPage == totalPages}">
                                        <li class="disabled"><span>&gt;</span></li>
                                    </c:if>
                                </ul>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>
        </section>

        <script>
            // Client-side validation
            document.getElementById('filterForm').addEventListener('submit', function(e) {
                const startDateTime = document.getElementById('startDateTime').value;
                const endDateTime = document.getElementById('endDateTime').value;
                
                if (startDateTime && endDateTime) {
                    const startDate = new Date(startDateTime);
                    const endDate = new Date(endDateTime);
                    
                    if (startDate > endDate) {
                        e.preventDefault();
                        alert('Ngày giờ bắt đầu không thể sau ngày giờ kết thúc!');
                        return false;
                    }
                }
            });
            
            // Set max attribute dynamically to current date-time
            window.addEventListener('load', function() {
                const now = new Date();
                const year = now.getFullYear();
                const month = String(now.getMonth() + 1).padStart(2, '0');
                const day = String(now.getDate()).padStart(2, '0');
                const hours = String(now.getHours()).padStart(2, '0');
                const minutes = String(now.getMinutes()).padStart(2, '0');
                const maxDateTime = year + '-' + month + '-' + day + 'T' + hours + ':' + minutes;
                
                document.getElementById('startDateTime').setAttribute('max', maxDateTime);
                document.getElementById('endDateTime').setAttribute('max', maxDateTime);
            });
        </script>

        <%@ include file="layout/footer.jsp" %>
        <%@ include file="layout/loader.jsp" %>

    </body>
</html>