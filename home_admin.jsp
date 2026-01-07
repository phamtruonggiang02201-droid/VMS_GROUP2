<%@page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/home_admin.css" rel="stylesheet" />
    </head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <div class="main-content" style="background-color:#f8f9fa; padding:20px;">
                <!-- Thanh tiêu đề -->
                <div class="topbar d-flex justify-content-between align-items-center mb-4 border-bottom pb-2">
                    <h4 class="fw-bold mb-0 text-primary">
                        <i class="bi bi-speedometer2"></i> Bảng quản trị
                    </h4>
                </div>

                <!-- Hàng thống kê 1 -->
                <div class="row g-3 mb-3">
                    <!-- Tổng tài khoản -->
                    <div class="col-md-4">
                        <div class="bg-warning rounded shadow p-3 text-start text-dark position-relative">
                            <h2 class="fw-bold">${totalAccounts}</h2>
                            <h5 class="">Tổng số người dùng</h5>
                            <i class="bi bi-person-plus-fill"
                               style="font-size: 5rem; position: absolute; bottom:5px; right: 10px; opacity: 0.15; color: black"></i>
                        </div>
                    </div>

                    <!-- Tổng sự kiện -->
                    <div class="col-md-4">
                        <div class="bg-primary rounded shadow p-3 text-start text-white position-relative">
                            <h2 class="fw-bold">${totalEventsActive}</h2>
                            <h5 class="">Tổng số sự kiện</h5>
                            <i class="bi bi-house-door"
                               style="font-size: 5rem; position: absolute; bottom:5px; right: 10px; opacity: 0.15; color: black"></i>
                        </div>
                    </div>

                    <!-- Tổng tiền donate -->
                    <div class="col-md-4">
                        <div class="bg-primary rounded shadow p-3 text-start text-white position-relative">
                            <h2 class="fw-bold">
                                <fmt:formatNumber value="${totalMoneyDonate}" type="number" pattern="#,###" /> VND
                            </h2>
                            <h5 class="">Tổng số tiền tài trợ</h5>
                            <i class="bi bi-cash"
                               style="font-size: 5rem; position: absolute; bottom:5px; right: 10px; opacity: 0.15; color: black"></i>
                        </div>
                    </div>
                </div>


                <!-- Biểu đồ tổng tiền .. Cái này bạn lấy theo mốc từ tháng mấy đến tháng mấy là được ,
                nó tính theo tổng donate của tất cả các sự kiện--> 
                <div class="card border rounded shadow-sm p-3 bg-white mb-4">
                    <h6 class="fw-bold text-primary mb-3">
                        <i class="bi bi-bar-chart-line"></i> Tổng số tiền theo tháng 
                    </h6>
                    <canvas id="donationChart" height="100"></canvas>
                </div>

                <!-- Hàng biểu đồ phụ -->
                <div class="row g-3">
                    <!-- Biểu đồ phân loại tài khoản -->
                    <div class="col-md-6">
                        <div class="card border rounded shadow-sm p-3 bg-white" style="height: 100%;">
                            <h6 class="fw-bold mb-3"><i class="bi bi-pie-chart"></i> Phân loại tài khoản</h6>
                            <canvas id="accountChart"></canvas>
                        </div>
                    </div>          
                    <div class="col-md-6">

                        <!-- Top 3 sự kiện donate -->
                        <div class="card border rounded shadow-sm p-3 bg-white mb-5">
                            <h6 class="fw-bold mb-3"><i class="bi bi-trophy"></i> Top 3 sự kiện được tài trợ nhiều nhất</h6>
                            <table class="table table-hover table-sm align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>STT</th>
                                        <th>Tên sự kiện</th>
                                        <th class="text-end">Tổng tiền (₫)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="e" items="${topEvents}" varStatus="status">
                                        <tr>
                                            <td>${status.index + 1}</td>
                                            <td>${e.title}</td>
                                            <td class="text-end"><fmt:formatNumber value="${e.totalDonation}" type="number" /></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>

                        <!-- Top 3 người donate nhiều nhất -->
                        <div class="card border rounded shadow-sm p-3 bg-white mt-3">
                            <h6 class="fw-bold mb-3"><i class="bi bi-trophy"></i> Top 3 tình nguyện viên tài trợ nhiều nhất</h6>
                            <table class="table table-hover table-sm align-middle">
                                <thead class="table-light">
                                    <tr>
                                        <th>STT</th>
                                        <th>Họ tên</th>
                                        <th class="text-end">Số tiền tài trợ (₫)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <!-- Hiển thị top 3 người tài trợ từ backend -->
                                    <c:forEach var="donor" items="${top3Donors}" varStatus="status">
                                        <tr>
                                            <td>${status.index + 1}</td>
                                            <td>${donor.volunteerFullName}</td>
                                            <td class="text-end"><fmt:formatNumber value="${donor.totalAmountDonated}" type="number" /></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Sự kiện sắp diễn ra -->
                <div class="card border rounded shadow-sm p-3 bg-white mt-4 mb-4">
                    <h6 class="fw-bold mb-3"><i class="bi bi-calendar-week"></i> Sự kiện sắp diễn ra</h6>
                    <table class="table table-striped table-sm table-bordered table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th>STT</th>
                                <th>Tên sự kiện</th>
                                <th>Ngày bắt đầu</th>
                                <th>Địa điểm</th>
                                <th>Tổ chức</th>
                                <th>Trạng thái</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="e" items="${eventsComing}" varStatus="status">
                                <tr>
                                    <td>${status.count}</td>
                                    <td>${e.title}</td>
                                    <td><fmt:formatDate value="${e.startDate}" pattern="yyyy-MM-dd"/></td>
                                    <td>${e.location}</td>
                                    <td>${e.organizationName}</td>
                                    <td><span class="badge bg-success">Sắp diễn ra</span></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>


            </div>
        </div>

        <!-- Chart.js script -->
        <script>
            // Biểu đồ tổng tiền donate theo tháng - Hiển thị 5 tháng gần nhất
            new Chart(document.getElementById("donationChart"), {
                type: 'bar',
                data: {
                    // Lấy các tháng từ backend (format: MM/yyyy)
                    labels: [<c:forEach var="entry" items="${donationByMonth}" varStatus="status">"Tháng ${entry.key}"<c:if test="${!status.last}">, </c:if></c:forEach>],
                            datasets: [{
                                    label: 'Tổng tiền (₫)',
                                    // Lấy tổng tiền từng tháng từ backend
                                    data: [<c:forEach var="entry" items="${donationByMonth}" varStatus="status">${entry.value}<c:if test="${!status.last}">, </c:if></c:forEach>],
                                    backgroundColor: ['#0d6efd', '#198754', '#dc3545', '#ffc107', '#0dcaf0']
                                }]
                },
                options: {responsive: true, plugins: {legend: {display: false}}}
            });

            // Biểu đồ phân loại tài khoản - Hiển thị active/inactive theo role
            new Chart(document.getElementById("accountChart"), {
                type: 'pie',
                data: {
                    labels: [
                        "Admin (Active)", "Admin (Inactive)",
                        "Tổ chức (Active)", "Tổ chức (Inactive)",
                        "Volunteer (Active)", "Volunteer (Inactive)"
                    ],
                    datasets: [{
                            // Lấy số lượng trực tiếp từ Map - default 0 nếu null
                            data: [
            <c:out value="${allAccountStats['admin_active']}" default="0" />,
            <c:out value="${allAccountStats['admin_inactive']}" default="0" />,
            <c:out value="${allAccountStats['organization_active']}" default="0" />,
            <c:out value="${allAccountStats['organization_inactive']}" default="0" />,
            <c:out value="${allAccountStats['volunteer_active']}" default="0" />,
            <c:out value="${allAccountStats['volunteer_inactive']}" default="0" />
                            ],
                            backgroundColor: ['#0d6efd', '#6c757d', '#20c997', '#adb5bd', '#ffc107', '#ced4da']
                        }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true
                }
            });
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
