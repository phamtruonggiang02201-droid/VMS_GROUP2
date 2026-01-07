<%@page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Welfare - Free Bootstrap 4 Template by Colorlib</title>
        <jsp:include page="/layout/header.jsp" />
    </head>
    <body>

        <!-- Hiển thị thông báo (unified message display) -->
        <c:if test="${not empty message or not empty sessionScope.message}">
            <c:set var="displayMessage" value="${not empty message ? message : sessionScope.message}" />
            <c:set var="displayType" value="${not empty messageType ? messageType : sessionScope.messageType}" />

            <div class="alert alert-${displayType} alert-dismissible fade show" role="alert"
                 style="position: fixed; top: 20px; left: 50%; transform: translateX(-50%); z-index: 9999; min-width: 400px; max-width: 600px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);">
                <c:choose>
                    <c:when test="${displayType == 'success'}">
                        <i class="bi bi-check-circle-fill"></i> <strong>Thành công!</strong>
                    </c:when>
                    <c:when test="${displayType == 'danger'}">
                        <i class="bi bi-exclamation-triangle-fill"></i> <strong>Lỗi!</strong>
                    </c:when>
                    <c:otherwise>
                        <strong>Thông báo:</strong>
                    </c:otherwise>
                </c:choose>
                <c:out value="${displayMessage}"/>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>

            <c:remove var="message" scope="session"/>
            <c:remove var="messageType" scope="session"/>

            <script>
                setTimeout(function () {
                    var alertEl = document.querySelector('.alert');
                    if (alertEl) {
                        var bsAlert = new bootstrap.Alert(alertEl);
                        bsAlert.close();
                    }
                }, 5000);
            </script>
        </c:if>
        <!-- Navbar -->
        <jsp:include page="/layout/navbar.jsp" />
        <!-- Background Images -->
        <jsp:include page="layout/background.jsp" />
        <!-- Modal Option -->
        <jsp:include page="/layout/modal_option.jsp" />
        <!-- Text Modal -->
        <jsp:include page="/layout/text_modal.jsp" />
        <!-- Donors -->
        <jsp:include page="/layout/donors.jsp" />
        <!-- Images Child -->
        <jsp:include page="/layout/images.jsp" />
        <!-- Blog -->
        <jsp:include page="/layout/blog.jsp" />
        <!-- Events -->
        <jsp:include page="/layout/events.jsp" />

        <jsp:include page="/layout/footer.jsp" />
        <jsp:include page="/layout/loader.jsp" />
    </body>
</html>
