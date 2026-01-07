<%-- 
    Document   : add_account_admin
    Created on : Oct 15, 2025, 10:30:00 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8" buffer="32kb"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Tạo tài khoản mới</title>
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
                <h1 class="mb-4">
                    <i class="bi bi-person-plus-fill me-2"></i>
                    Tạo tài khoản mới
                </h1>
                
                <c:if test="${param.msg == 'error_username_exists'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${param.msg == 'error_validation'}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        Vui lòng điền đầy đủ thông tin bắt buộc.
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>
                
                <c:if test="${param.msg == 'success'}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        Tạo tài khoản thành công!
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <div class="card">
                    <div class="card-body">
                        <form action="<%= request.getContextPath() %>/admin/AdminAddAccountServlet" method="post" enctype="multipart/form-data" id="createAccountForm" novalidate>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="username" class="form-label">
                                        <i class="bi bi-person me-1"></i>
                                        Tên đăng nhập <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="username" name="username" 
                                           placeholder="Nhập tên đăng nhập" required maxlength="50">
                                    <small class="text-danger fst-italic" id="username-error"></small>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="role" class="form-label">
                                        <i class="bi bi-shield-check me-1"></i>
                                        Vai trò <span class="text-danger">*</span>
                                    </label>
                                    <!-- Fix cứng vai trò là Organization -->
                                    <select class="form-select" id="role" name="role" required disabled style="background-color: #e9ecef;">
                                        <option value="organization" selected>Tổ chức (Organization)</option>
                                    </select>
                                    <!-- Hidden field để gửi giá trị role khi form submit -->
                                    <input type="hidden" name="role" value="organization" />
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="password" class="form-label">
                                        <i class="bi bi-lock me-1"></i>
                                        Mật khẩu <span class="text-danger">*</span>
                                    </label>
                                    <input type="password" class="form-control" id="password" name="password" 
                                           placeholder="Nhập mật khẩu" required minlength="3">
                                    <!-- <small class="text-muted">Mật khẩu tối thiểu 3 ký tự</small> -->
                                    <small class="text-danger fst-italic" id="password-error"></small>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="confirm_password" class="form-label">
                                        <i class="bi bi-lock-fill me-1"></i>
                                        Xác nhận mật khẩu <span class="text-danger">*</span>
                                    </label>
                                    <input type="password" class="form-control" id="confirm_password" 
                                           placeholder="Nhập lại mật khẩu" required minlength="3">
                                    <small class="text-danger fst-italic" id="password-match-error"></small>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="full_name" class="form-label">
                                        <i class="bi bi-person-vcard me-1"></i>
                                        Họ và tên <span class="text-danger">*</span>
                                    </label>
                                    <input type="text" class="form-control" id="full_name" name="full_name" 
                                           placeholder="Nhập họ và tên" required maxlength="100">
                                    <small class="text-danger fst-italic" id="full-name-error"></small>
                                </div>

                                <div class="col-md-6 mb-3">
                                    <label for="email" class="form-label">
                                        <i class="bi bi-envelope me-1"></i>
                                        Email <span class="text-danger">*</span>
                                    </label>
                                    <input type="email" class="form-control" id="email" name="email" 
                                           placeholder="example@email.com" required maxlength="100"
                                           pattern="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$" title="Email không đúng định dạng (ví dụ: name@example.com)">
                                    <small class="text-danger fst-italic" id="email-error"></small>
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-4 mb-3">
                                    <label for="phone" class="form-label">
                                        <i class="bi bi-telephone me-1"></i>
                                        Số điện thoại
                                    </label>
                                    <input type="text" class="form-control" id="phone" name="phone" 
                                           placeholder="0123456789" maxlength="11" inputmode="numeric" pattern="^\d{10,11}$" title="Số điện thoại phải gồm 10-11 chữ số" autocomplete="tel">
                                    <small class="text-danger fst-italic" id="phone-error"></small>
                                </div>

                                <div class="col-md-4 mb-3">
                                    <label for="gender" class="form-label">
                                        <i class="bi bi-gender-ambiguous me-1"></i>
                                        Giới tính
                                    </label>
                                    <select class="form-select" id="gender" name="gender">
                                        <option value="">-- Chọn giới tính --</option>
                                        <option value="male">Nam</option>
                                        <option value="female">Nữ</option>
                                    </select>
                                </div>

                                <div class="col-md-4 mb-3">
                                    <label for="status" class="form-label">
                                        <i class="bi bi-toggle-on me-1"></i>
                                        Trạng thái <span class="text-danger">*</span>
                                    </label>
                                    <select class="form-select" id="status" name="status" required>
                                        <option value="active" selected>Hoạt động (Active)</option>
                                        <option value="inactive">Khóa (Inactive)</option>
                                    </select>
                                    <small class="text-danger fst-italic" id="status-error"></small>
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="avatar" class="form-label">
                                        <i class="bi bi-image me-1"></i>
                                        Ảnh đại diện (Avatar)
                                    </label>
                                    <input type="file" class="form-control" id="avatar" name="avatar" 
                                           accept="image/*" onchange="previewAvatar(this)">
                                    <div class="mt-2" id="avatar-preview" style="display: none;">
                                        <img id="preview-img" src="" alt="Preview" style="max-width: 150px; max-height: 150px; border-radius: 8px;">
                                    </div>
                                    <small class="text-muted">Chỉ chấp nhận file ảnh <= 2MB</small>
                                </div>

                                <div class="col-md-6">
                                    <label for="address" class="form-label">
                                        <i class="bi bi-geo-alt me-1"></i>
                                        Địa chỉ
                                    </label>
                                    <input type="text" class="form-control" id="address" name="address" 
                                           placeholder="Nhập địa chỉ" maxlength="255">
                                </div>
                            </div>

                            <div class="row mb-3">
                                <div class="col-md-6">
                                    <label for="job_title" class="form-label">
                                        <i class="bi bi-briefcase me-1"></i>
                                        Chức vụ/Nghề nghiệp
                                    </label>
                                    <input type="text" class="form-control" id="job_title" name="job_title" 
                                           placeholder="Nhập chức vụ" maxlength="100">
                                </div>

                                <div class="col-md-6">
                                    <label for="dob" class="form-label">
                                        <i class="bi bi-calendar me-1"></i>
                                        Ngày sinh
                                    </label>
                                    <input type="date" class="form-control" id="dob" name="dob">
                                    <small class="text-danger fst-italic" id="dob-error"></small>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="bio" class="form-label">
                                    <i class="bi bi-file-text me-1"></i>
                                    Giới thiệu
                                </label>
                                <textarea class="form-control" id="bio" name="bio" rows="3" 
                                          placeholder="Nhập mô tả ngắn về tổ chức/tình nguyện viên" maxlength="1000"></textarea>
                                <small class="text-muted">Tối đa 1000 ký tự</small>
                            </div>

                            <div class="d-flex justify-content-end gap-2 mt-4">
                                <a href="<%= request.getContextPath() %>/AdminAccountServlet" class="btn btn-secondary">
                                    <i class="bi bi-x-circle me-1"></i>
                                    Hủy
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="bi bi-check-circle me-1"></i>
                                    Tạo tài khoản
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            const ctx = '<%= request.getContextPath() %>';
            console.log('Context path:', ctx);

            // Test API immediately on page load
            window.addEventListener('load', async () => {
                try {
                    const testUrl = ctx + '/admin/check-unique?type=username&value=admin';
                    console.log('Testing API with URL:', testUrl);
                    const response = await fetch(testUrl);
                    console.log('API test response status:', response.status);
                    const data = await response.json();
                    console.log('API test response data:', data);
                } catch (error) {
                    console.error('API test failed:', error);
                }
            });
            // Validate password match
            const form = document.getElementById('createAccountForm');
            const password = document.getElementById('password');
            const confirmPassword = document.getElementById('confirm_password');
            const errorMsg = document.getElementById('password-match-error');
            if (errorMsg) { errorMsg.classList.add('text-danger', 'fst-italic'); }
            const phoneInput = document.getElementById('phone');
            const emailInput = document.getElementById('email');
            const dobInput = document.getElementById('dob');
            const usernameInput = document.getElementById('username');
            const usernameError = document.getElementById('username-error');
            const passwordError = document.getElementById('password-error');
            const fullNameInput = document.getElementById('full_name');
            const fullNameError = document.getElementById('full-name-error');
            const statusSelect = document.getElementById('status');
            const statusError = document.getElementById('status-error');
            const submitBtn = document.querySelector('#createAccountForm button[type="submit"]');

            let uniqueState = { username: true, email: true, phone: true };

            async function checkUnique(type, value) {
                if (!value || value.trim().length === 0) return { exists: false };
                try {
                    const encodedType = encodeURIComponent(type);
                    const encodedValue = encodeURIComponent(value.trim());
                    const url = ctx + '/admin/check-unique?type=' + encodedType + '&value=' + encodedValue;
                    console.log('Calling checkUnique API:', url);
                    const res = await fetch(url, { method: 'GET', headers: { 'Accept': 'application/json' } });
                    console.log('Response status:', res.status);
                    if (!res.ok) return { exists: false };
                    return await res.json();
                } catch (e) {
                    console.error('checkUnique error:', e);
                    return { exists: false };
                }
            }

            function updateSubmitDisabled() {
                const anyInvalid =
                    password.classList.contains('is-invalid') ||
                    confirmPassword.classList.contains('is-invalid') ||
                    usernameInput.classList.contains('is-invalid') ||
                    emailInput.classList.contains('is-invalid') ||
                    phoneInput.classList.contains('is-invalid') ||
                    !uniqueState.username ||
                    !uniqueState.email ||
                    !uniqueState.phone;
                if (submitBtn) submitBtn.disabled = anyInvalid;
            }

            function validatePassword() {
                const passVal = (password.value || '');
                const confirmVal = (confirmPassword.value || '');
                if (confirmVal.length === 0) {
                    errorMsg.textContent = 'Trường này là bắt buộc';
                    confirmPassword.classList.add('is-invalid');
                    return false;
                }
                if (passVal !== confirmVal) {
                    errorMsg.textContent = 'Mật khẩu không khớp';
                    confirmPassword.classList.add('is-invalid');
                    return false;
                }
                errorMsg.textContent = '';
                confirmPassword.classList.remove('is-invalid');
                return true;
            }

            confirmPassword.addEventListener('keyup', validatePassword);
            password.addEventListener('keyup', validatePassword);

            form.addEventListener('submit', function(e) {
                // Password check
                if (!validatePassword()) {
                    e.preventDefault();
                    return false;
                }

                // Required fields check
                let requiredOk = true;
                function markRequired(el, errEl) {
                    if (!el) return;
                    const value = (el.value || '').trim();
                    if (value.length === 0) {
                        requiredOk = false;
                        el.classList.add('is-invalid');
                        if (errEl) errEl.textContent = 'Trường này là bắt buộc';
                    } else {
                        el.classList.remove('is-invalid');
                        if (errEl) errEl.textContent = '';
                    }
                }

                markRequired(usernameInput, usernameError);
                markRequired(password, passwordError);
                // Enforce password min length 3
                if ((password.value || '').length > 0 && password.value.length < 3) {
                    requiredOk = false;
                    password.classList.add('is-invalid');
                    if (passwordError) passwordError.textContent = 'Mật khẩu tối thiểu 3 ký tự';
                }
                markRequired(fullNameInput, fullNameError);
                if (statusSelect) {
                    if (!statusSelect.value) {
                        requiredOk = false;
                        statusSelect.classList.add('is-invalid');
                        if (statusError) statusError.textContent = 'Trường này là bắt buộc';
                    } else {
                        statusSelect.classList.remove('is-invalid');
                        if (statusError) statusError.textContent = '';
                    }
                }

                if (!requiredOk) {
                    e.preventDefault();
                    return false;
                }

                // Optional phone: if provided, must be 10-11 digits
                const phone = phoneInput.value.trim();
                const phoneError = document.getElementById('phone-error');
                if (phone.length > 0) {
                    const phoneOk = /^\d{10,11}$/.test(phone);
                    if (!phoneOk) {
                        e.preventDefault();
                        phoneInput.classList.add('is-invalid');
                        phoneInput.setCustomValidity('Số điện thoại phải gồm 10-11 chữ số');
                        if (phoneError) phoneError.textContent = 'Số điện thoại phải gồm 10-11 chữ số';
                        return false;
                    } else {
                        phoneInput.classList.remove('is-invalid');
                        phoneInput.setCustomValidity('');
                        if (phoneError) phoneError.textContent = '';
                    }
                } else {
                    phoneInput.classList.remove('is-invalid');
                    phoneInput.setCustomValidity('');
                    if (phoneError) phoneError.textContent = '';
                }

                // Email pattern check (HTML pattern also enforces)
                const emailVal = emailInput.value.trim();
                const emailError = document.getElementById('email-error');
                const emailOk = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(emailVal);
                if (!emailOk) {
                    e.preventDefault();
                    emailInput.classList.add('is-invalid');
                    emailInput.setCustomValidity('Email không đúng định dạng');
                    if (emailError) emailError.textContent = 'Email không đúng định dạng';
                    return false;
                } else {
                    emailInput.classList.remove('is-invalid');
                    emailInput.setCustomValidity('');
                    if (emailError) emailError.textContent = '';
                }

                // DOB must not be in the future
                if (dobInput.value) {
                    const dobError = document.getElementById('dob-error');
                    const today = new Date();
                    today.setHours(0,0,0,0);
                    const dobDate = new Date(dobInput.value);
                    if (dobDate > today) {
                        e.preventDefault();
                        dobInput.classList.add('is-invalid');
                        dobInput.setCustomValidity('Ngày sinh không được vượt quá ngày hiện tại');
                        if (dobError) dobError.textContent = 'Ngày sinh không được vượt quá ngày hiện tại';
                        return false;
                    } else {
                        dobInput.classList.remove('is-invalid');
                        dobInput.setCustomValidity('');
                        if (dobError) dobError.textContent = '';
                    }
                }
            });

            // Live required validation for generic fields
            function validateRequiredLive(el, errEl) {
                if (!el) return;
                const handler = () => {
                    const val = (el.value || '').trim();
                    if (val.length === 0) {
                        el.classList.add('is-invalid');
                        if (errEl) errEl.textContent = 'Trường này là bắt buộc';
                    } else {
                        el.classList.remove('is-invalid');
                        if (errEl) errEl.textContent = '';
                    }
                };
                el.addEventListener('input', handler);
                el.addEventListener('change', handler);
            }

            validateRequiredLive(usernameInput, usernameError);
            validateRequiredLive(password, passwordError);
            validateRequiredLive(fullNameInput, fullNameError);
            // Live password length validation
            if (password) {
                password.addEventListener('input', () => {
                    const val = password.value || '';
                    if (val.length > 0 && val.length < 3) {
                        password.classList.add('is-invalid');
                        if (passwordError) passwordError.textContent = 'Mật khẩu tối thiểu 3 ký tự';
                    } else {
                        password.classList.remove('is-invalid');
                        if (passwordError) passwordError.textContent = '';
                    }
                });
            }
            // Live phone validation (optional) with debounce
            if (phoneInput) {
                const phoneError = document.getElementById('phone-error');
                let phoneCheckTimeout = null;

                // Check phone uniqueness with debounce
                async function checkPhoneUnique(val) {
                    console.log('Checking phone uniqueness for:', val);
                    try {
                        const resp = await checkUnique('phone', val);
                        console.log('Phone check response:', resp);

                        if (resp && resp.exists === true) {
                            phoneInput.classList.add('is-invalid');
                            if (phoneError) phoneError.textContent = 'Số điện thoại đã tồn tại';
                            uniqueState.phone = false;
                        } else {
                            // Only remove invalid if format is also ok
                            if (/^\d{10,11}$/.test(val)) {
                                phoneInput.classList.remove('is-invalid');
                                if (phoneError) phoneError.textContent = '';
                                uniqueState.phone = true;
                            }
                        }
                        updateSubmitDisabled();
                    } catch (error) {
                        console.error('Error checking phone:', error);
                    }
                }

                phoneInput.addEventListener('input', () => {
                    const val = (phoneInput.value || '').trim();

                    // Clear previous timeout
                    if (phoneCheckTimeout) {
                        clearTimeout(phoneCheckTimeout);
                    }

                    // Empty value is ok (optional field)
                    if (val.length === 0) {
                        phoneInput.classList.remove('is-invalid');
                        phoneInput.setCustomValidity('');
                        if (phoneError) phoneError.textContent = '';
                        uniqueState.phone = true;
                        updateSubmitDisabled();
                        return;
                    }

                    // Check format first
                    const formatOk = /^\d{10,11}$/.test(val);
                    if (!formatOk) {
                        phoneInput.classList.add('is-invalid');
                        phoneInput.setCustomValidity('Số điện thoại phải gồm 10-11 chữ số');
                        if (phoneError) phoneError.textContent = 'Số điện thoại phải gồm 10-11 chữ số';
                        uniqueState.phone = false;
                        updateSubmitDisabled();
                        return;
                    }

                    // Format is ok, clear format error
                    phoneInput.setCustomValidity('');

                    // Check uniqueness after 800ms delay (debounce)
                    phoneCheckTimeout = setTimeout(() => {
                        checkPhoneUnique(val);
                    }, 800);
                });

                // Also check on blur for immediate feedback when leaving field
                phoneInput.addEventListener('blur', async () => {
                    const val = (phoneInput.value || '').trim();
                    if (val.length === 0) {
                        uniqueState.phone = true;
                        updateSubmitDisabled();
                        return;
                    }

                    // If format is ok, check uniqueness immediately
                    if (/^\d{10,11}$/.test(val)) {
                        // Clear any pending timeout
                        if (phoneCheckTimeout) {
                            clearTimeout(phoneCheckTimeout);
                        }
                        await checkPhoneUnique(val);
                    }
                });
            }

            // Live email validation (required + pattern) with debounce
            if (emailInput) {
                const emailError = document.getElementById('email-error');
                let emailCheckTimeout = null;

                // Check email uniqueness with debounce
                async function checkEmailUnique(val) {
                    console.log('Checking email uniqueness for:', val);
                    try {
                        const resp = await checkUnique('email', val);
                        console.log('Email check response:', resp);

                        if (resp && resp.exists === true) {
                            emailInput.classList.add('is-invalid');
                            if (emailError) emailError.textContent = 'Email đã tồn tại';
                            uniqueState.email = false;
                        } else {
                            // Only remove invalid if format is also ok
                            const formatOk = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(val);
                            if (formatOk) {
                                emailInput.classList.remove('is-invalid');
                                if (emailError) emailError.textContent = '';
                                uniqueState.email = true;
                            }
                        }
                        updateSubmitDisabled();
                    } catch (error) {
                        console.error('Error checking email:', error);
                    }
                }

                emailInput.addEventListener('input', () => {
                    const val = (emailInput.value || '').trim();

                    // Clear previous timeout
                    if (emailCheckTimeout) {
                        clearTimeout(emailCheckTimeout);
                    }

                    // Required field
                    if (val.length === 0) {
                        emailInput.classList.add('is-invalid');
                        emailInput.setCustomValidity('Trường này là bắt buộc');
                        if (emailError) emailError.textContent = 'Trường này là bắt buộc';
                        uniqueState.email = false;
                        updateSubmitDisabled();
                        return;
                    }

                    // Check format
                    const formatOk = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(val);
                    if (!formatOk) {
                        emailInput.classList.add('is-invalid');
                        emailInput.setCustomValidity('Email không đúng định dạng');
                        if (emailError) emailError.textContent = 'Email không đúng định dạng';
                        uniqueState.email = false;
                        updateSubmitDisabled();
                        return;
                    }

                    // Format is ok, clear format error
                    emailInput.setCustomValidity('');

                    // Check uniqueness after 800ms delay (debounce)
                    emailCheckTimeout = setTimeout(() => {
                        checkEmailUnique(val);
                    }, 800);
                });

                // Also check on blur for immediate feedback
                emailInput.addEventListener('blur', async () => {
                    const val = (emailInput.value || '').trim();
                    if (val.length === 0) {
                        uniqueState.email = false;
                        updateSubmitDisabled();
                        return;
                    }

                    const formatOk = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(val);
                    if (formatOk) {
                        // Clear any pending timeout
                        if (emailCheckTimeout) {
                            clearTimeout(emailCheckTimeout);
                        }
                        await checkEmailUnique(val);
                    } else {
                        uniqueState.email = false;
                        updateSubmitDisabled();
                    }
                });
            }
            if (statusSelect) {
                statusSelect.addEventListener('change', () => {
                    statusSelect.classList.remove('is-invalid');
                    if (statusError) statusError.textContent = '';
                });
                // Also validate required live for select
                statusSelect.addEventListener('input', () => {
                    if (!statusSelect.value) {
                        statusSelect.classList.add('is-invalid');
                        if (statusError) statusError.textContent = 'Trường này là bắt buộc';
                    } else {
                        statusSelect.classList.remove('is-invalid');
                        if (statusError) statusError.textContent = '';
                    }
                });
            }

            // Live DOB validation to prevent future date input
            if (dobInput) {
                const dobError = document.getElementById('dob-error');
                const validateDobLive = () => {
                    if (!dobInput.value) {
                        dobInput.classList.remove('is-invalid');
                        if (dobError) dobError.textContent = '';
                        return;
                    }
                    const today = new Date();
                    today.setHours(0,0,0,0);
                    const dobDate = new Date(dobInput.value);
                    if (dobDate > today) {
                        dobInput.classList.add('is-invalid');
                        if (dobError) dobError.textContent = 'Ngày sinh không được vượt quá ngày hiện tại';
                    } else {
                        dobInput.classList.remove('is-invalid');
                        if (dobError) dobError.textContent = '';
                    }
                };
                dobInput.addEventListener('change', validateDobLive);
                dobInput.addEventListener('input', validateDobLive);
            }

            // Preview avatar
            function previewAvatar(input) {
                const preview = document.getElementById('avatar-preview');
                const previewImg = document.getElementById('preview-img');
                if (input.files && input.files[0]) {
                    const reader = new FileReader();
                    reader.onload = function(e) {
                        previewImg.src = e.target.result;
                        preview.style.display = 'block';
                    }
                    reader.readAsDataURL(input.files[0]);
                } else {
                    preview.style.display = 'none';
                }
            }

            // Auto dismiss success alert after 3 seconds
            const successAlert = document.querySelector('.alert-success');
            if (successAlert) {
                setTimeout(() => {
                    const alert = new bootstrap.Alert(successAlert);
                    alert.close();
                }, 3000);
            }

            // Set max attribute for dob to today to prevent future picks in UI
            (function setDobMaxToday(){
                if (!dobInput) return;
                const today = new Date();
                const yyyy = today.getFullYear();
                const mm = String(today.getMonth() + 1).padStart(2, '0');
                const dd = String(today.getDate()).padStart(2, '0');
                dobInput.max = yyyy + '-' + mm + '-' + dd;
            })();

            // Username live unique check with debounce
            if (usernameInput) {
                let usernameCheckTimeout = null;

                // Check username uniqueness with debounce
                async function checkUsernameUnique(val) {
                    console.log('Checking username uniqueness for:', val);
                    try {
                        const resp = await checkUnique('username', val);
                        console.log('Username check response:', resp);

                        if (resp && resp.exists === true) {
                            usernameInput.classList.add('is-invalid');
                            if (usernameError) usernameError.textContent = 'Tên đăng nhập đã tồn tại';
                            uniqueState.username = false;
                        } else {
                            usernameInput.classList.remove('is-invalid');
                            if (usernameError) usernameError.textContent = '';
                            uniqueState.username = true;
                        }
                        updateSubmitDisabled();
                    } catch (error) {
                        console.error('Error checking username:', error);
                    }
                }

                usernameInput.addEventListener('input', () => {
                    const val = (usernameInput.value || '').trim();

                    // Clear previous timeout
                    if (usernameCheckTimeout) {
                        clearTimeout(usernameCheckTimeout);
                    }

                    // Required field
                    if (val.length === 0) {
                        usernameInput.classList.add('is-invalid');
                        if (usernameError) usernameError.textContent = 'Trường này là bắt buộc';
                        uniqueState.username = false;
                        updateSubmitDisabled();
                        return;
                    }

                    // Clear required error if has value
                    usernameInput.classList.remove('is-invalid');
                    if (usernameError) usernameError.textContent = '';

                    // Check uniqueness after 800ms delay (debounce)
                    usernameCheckTimeout = setTimeout(() => {
                        checkUsernameUnique(val);
                    }, 800);
                });

                // Also check on blur for immediate feedback
                usernameInput.addEventListener('blur', async () => {
                    const val = (usernameInput.value || '').trim();
                    if (val.length === 0) {
                        uniqueState.username = false;
                        if (usernameError) usernameError.textContent = 'Trường này là bắt buộc';
                        updateSubmitDisabled();
                        return;
                    }

                    // Clear any pending timeout
                    if (usernameCheckTimeout) {
                        clearTimeout(usernameCheckTimeout);
                    }
                    await checkUsernameUnique(val);
                });
            }
        </script>
    </body>
</html>

