<%-- 
    Document   : send_all_notification_admin
    Created on : Nov 14, 2025, 1:29:55 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Gửi thông báo chung</title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="<%= request.getContextPath() %>/admin/css/admin.css" rel="stylesheet" />
    </head>
    <body>
        <div class="content-container">
            <!-- Sidebar -->
            <jsp:include page="layout_admin/sidebar_admin.jsp" />

            <!-- Main Content -->
            <div class="main-content p-4">

                <h2 class="mb-2">Gửi thông báo chung</h2>
                <div class="d-flex align-items-center mb-4">
                    <a href="<%= request.getContextPath() %>/AdminAccountServlet" class="btn btn-secondary me-3">
                        <i class="bi bi-arrow-left"></i> Quay lại
                    </a>
                </div>



                <!-- Hiển thị thông báo -->
                <c:if test="${param.msg == 'success'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="bi bi-check-circle me-2"></i>Đã gửi thông báo đến ${param.count} tài khoản thành công!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                <c:if test="${param.msg == 'error'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="bi bi-exclamation-circle me-2"></i>Gửi thông báo thất bại. Vui lòng thử lại!
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <!-- Card thông tin người nhận -->
                <div class="card mb-4">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0">Phạm vi gửi</h5>
                    </div>
                    <div class="card-body">
                        <div class="alert alert-warning mb-0">
                            <i class="bi bi-info-circle me-2"></i>
                            <strong>Lưu ý:</strong> Thông báo sẽ được gửi đến <strong>tất cả tài khoản</strong> trong hệ thống.
                        </div>
                    </div>
                </div>

                <!-- Form filter người nhận -->
                <div class="card mb-4">
                    <div class="card-header bg-secondary text-white">
                        <h5 class="mb-0">Lọc đối tượng nhận</h5>
                    </div>
                    <div class="card-body">
                        <form id="filterForm">
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-bold">Vai trò</label>
                                    <div>
                                        <div class="form-check form-check-inline">
                                            <input class="form-check-input" type="checkbox" id="roleAdmin" value="admin" checked>
                                            <label class="form-check-label" for="roleAdmin">Admin</label>
                                        </div>
                                        <div class="form-check form-check-inline">
                                            <input class="form-check-input" type="checkbox" id="roleOrg" value="organization" checked>
                                            <label class="form-check-label" for="roleOrg">Organization</label>
                                        </div>
                                        <div class="form-check form-check-inline">
                                            <input class="form-check-input" type="checkbox" id="roleVol" value="volunteer" checked>
                                            <label class="form-check-label" for="roleVol">Volunteer</label>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label fw-bold">Trạng thái</label>
                                    <div>
                                        <div class="form-check form-check-inline">
                                            <input class="form-check-input" type="radio" name="statusFilter" id="statusActive" value="active" checked>
                                            <label class="form-check-label" for="statusActive">Chỉ Active</label>
                                        </div>
                                        <div class="form-check form-check-inline">
                                            <input class="form-check-input" type="radio" name="statusFilter" id="statusAll" value="all">
                                            <label class="form-check-label" for="statusAll">Tất cả</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <!-- Form gửi thông báo -->
                <div class="card">
                    <div class="card-header bg-warning">
                        <h5 class="mb-0">Nội dung thông báo</h5>
                    </div>
                    <div class="card-body">
                        <form action="<%= request.getContextPath() %>/admin/AdminSendNotificationServlet" method="post" onsubmit="return confirmSend()">
                            <input type="hidden" name="action" value="sendAll">
                            <input type="hidden" name="roles" id="rolesInput">
                            <input type="hidden" name="status" id="statusInput">

                            <div class="mb-3">
                                <label for="message" class="form-label fw-bold">Nội dung thông báo <span class="text-danger">*</span></label>
                                <textarea class="form-control" id="message" name="message" rows="8" 
                                          placeholder="Nhập nội dung..." required></textarea>
                                <div class="form-text">Tối thiểu 10 ký tự, tối đa 1000 ký tự</div>
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-danger">
                                    <i class="bi bi-send-fill me-2"></i>Gửi thông báo
                                </button>
                                <a href="<%= request.getContextPath() %>/AdminAccountServlet" class="btn btn-secondary">
                                    <i class="bi bi-x-circle me-2"></i>Hủy
                                </a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                            // Xác nhận trước khi gửi
                            function confirmSend() {
                                const message = document.getElementById('message').value.trim();
                                if (message.length < 10) {
                                    alert('Nội dung thông báo phải có ít nhất 10 ký tự!');
                                    return false;
                                }

                                // Cập nhật hidden inputs với filter
                                const roles = [];
                                if (document.getElementById('roleAdmin').checked)
                                    roles.push('admin');
                                if (document.getElementById('roleOrg').checked)
                                    roles.push('organization');
                                if (document.getElementById('roleVol').checked)
                                    roles.push('volunteer');

                                if (roles.length === 0) {
                                    alert('Vui lòng chọn ít nhất một vai trò!');
                                    return false;
                                }

                                document.getElementById('rolesInput').value = roles.join(',');
                                document.getElementById('statusInput').value = document.querySelector('input[name="statusFilter"]:checked').value;

                                const count = document.getElementById('estimatedCount').textContent;
                                return confirm('Bạn có chắc chắn muốn gửi thông báo này đến ' + count + ' tài khoản?');
                            }

                            // Cập nhật số lượng ước tính khi thay đổi filter
                            function updateEstimate() {
                                const roles = [];
                                if (document.getElementById('roleAdmin').checked)
                                    roles.push('admin');
                                if (document.getElementById('roleOrg').checked)
                                    roles.push('organization');
                                if (document.getElementById('roleVol').checked)
                                    roles.push('volunteer');
                                const status = document.querySelector('input[name="statusFilter"]:checked').value;

                                // Gọi AJAX để lấy số lượng
                                fetch('<%= request.getContextPath() %>/admin/AdminSendNotificationServlet?action=countRecipients&roles=' + roles.join(',') + '&status=' + status)
                                        .then(response => response.json())
                                        .then(data => {
                                            document.getElementById('estimatedCount').textContent = data.count + ' tài khoản';
                                        })
                                        .catch(error => {
                                            document.getElementById('estimatedCount').textContent = 'Không thể tính';
                                        });
                            }

                            // Lắng nghe sự thay đổi filter
                            document.getElementById('roleAdmin').addEventListener('change', updateEstimate);
                            document.getElementById('roleOrg').addEventListener('change', updateEstimate);
                            document.getElementById('roleVol').addEventListener('change', updateEstimate);
                            document.getElementById('statusActive').addEventListener('change', updateEstimate);
                            document.getElementById('statusAll').addEventListener('change', updateEstimate);

                            // Tính toán ban đầu
                            updateEstimate();
        </script>
    </body>
</html>
