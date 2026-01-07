<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Donate to Event - Volunteer System</title>
    <%@ include file="layout/header.jsp" %>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
    <style>
        .donation-form-container {
            max-width: 700px;
            margin: 3rem auto;
            padding: 2rem;
        }
        .form-card {
            background: white;
            border-radius: 12px;
            padding: 2.5rem;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }
        .form-header {
            text-align: center;
            margin-bottom: 2rem;
        }
        .form-header h2 {
            color: #2c3e50;
            font-weight: bold;
        }
        .form-header p {
            color: #7f8c8d;
        }
        .anonymous-toggle {
            background: #f8f9fa;
            padding: 1rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
        }
        .donor-info-section {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
            margin-bottom: 1.5rem;
        }
        .required-note {
            font-size: 0.875rem;
            color: #6c757d;
            font-style: italic;
        }
        .form-control + div small {
            display: inline-block;
            margin-right: 10px;
        }
        .form-control + div small.text-danger {
            margin-left: 10px;
        }
    </style>
</head>
<body>
    <!-- Navbar -->
    <%@ include file="layout/navbar.jsp" %>

    <div class="donation-form-container">
        <div class="form-card">
            <div class="form-header">
                <h2><i class="bi bi-heart-fill text-danger"></i> Tài trợ sự kiện</h2>
                <p>Hãy ủng hộ các sự kiện tình nguyện của chúng tôi và tạo nên sự khác biệt!</p>
            </div>

            <!-- Error Message -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <form action="<%= request.getContextPath() %>/guest-payment-donation" method="post" id="donationForm">
                <input type="hidden" name="eventId" value="${param.eventId}">

                <!-- Event Information (if available) -->
                <c:if test="${not empty event}">
                    <div class="mb-4">
                        <label class="form-label fw-bold">Sự kiện</label>
                        <input type="text" class="form-control" value="${event.title}" readonly>
                    </div>
                </c:if>

                <!-- Donation Amount -->
                <div class="mb-4">
                    <label class="form-label fw-bold">Số tiền tài trợ (VND) <span class="text-danger">*</span></label>
                    <input type="number" class="form-control" name="amount" id="amount"
                           min="10000" step="1000" placeholder="Nhập số tiền..." required>
                    <div>
                        <small class="text-muted" id="amount-note">Số tiền tối thiểu: 10,000 VND</small>
                        <small class="text-danger fst-italic" id="amount-error"></small>
                    </div>
                </div>

                <!-- Anonymous Toggle -->
                <div class="anonymous-toggle">
                    <div class="form-check form-switch">
                        <input class="form-check-input" type="checkbox" id="isAnonymous" name="isAnonymous" 
                               value="true">
                        <label class="form-check-label fw-bold" for="isAnonymous">
                            <i class="bi bi-incognito"></i> Tài trợ ẩn danh
                        </label>
                    </div>
                    <small class="text-muted">Chọn mục này nếu bạn muốn ẩn danh</small>
                </div>

                <!-- Donor Information Section -->
                <div class="donor-info-section" id="donorInfoSection">
                    <h5 class="mb-3">Thông tin cá nhân</h5>
                    <p class="required-note">
                        <i class="bi bi-info-circle"></i> Vui lòng để lại thông tin: Tên, Số điện thoại hoặc Email
                    </p>

                    <div class="mb-3">
                        <label class="form-label">Họ và tên</label>
                        <input type="text" class="form-control" name="guestName" id="guestName" 
                               placeholder="Tên của bạn...">
                        <div>
                            <small class="text-muted" id="guestName-note"></small>
                            <small class="text-danger fst-italic" id="guestName-error"></small>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Số điện thoại</label>
                        <input type="tel" class="form-control" name="guestPhone" id="guestPhone" 
                               placeholder="0xxxxxxxxx" pattern="^0\d{9,10}$">
                        <div>
                            <small class="text-muted" id="guestPhone-note">Bao gồm 10-11 chữ số và bắt đầu là 0</small>
                            <small class="text-danger fst-italic" id="guestPhone-error"></small>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Email</label>
                        <input type="email" class="form-control" name="guestEmail" id="guestEmail" 
                               placeholder="Email của bạn...">
                        <div>
                            <small class="text-muted" id="guestEmail-note">Chúng tôi sẽ gửi email cảm ơn sau khi tài trợ thành công</small>
                            <small class="text-danger fst-italic" id="guestEmail-error"></small>
                        </div>
                    </div>
                    <small class="text-danger fst-italic" id="donor-info-error"></small>
                </div>

                <!-- Note -->
                <div class="mb-4">
                    <label class="form-label">Ghi chú</label>
                    <textarea class="form-control" name="note" rows="3" 
                              placeholder="Để lại lời nhắn gửi..."></textarea>
                </div>

                <!-- Submit Buttons -->
                <div class="d-flex gap-2">
                    <a href="<%= request.getContextPath() %>/GuessEventServlet" class="btn btn-secondary flex-fill">
                        <i class="bi bi-arrow-left"></i> Hủy
                    </a>
                    <button type="submit" class="btn btn-success flex-fill" id="submitBtn">
                        <i class="bi bi-credit-card"></i> Thanh toán
                    </button>
                </div>
            </form>
        </div>
    </div>

    <%@ include file="layout/footer.jsp" %>
    <%@ include file="layout/loader.jsp" %>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Get form elements
        const form = document.getElementById('donationForm');
        const amountInput = document.getElementById('amount');
        const isAnonymousCheckbox = document.getElementById('isAnonymous');
        const donorInfoSection = document.getElementById('donorInfoSection');
        const guestNameInput = document.getElementById('guestName');
        const guestPhoneInput = document.getElementById('guestPhone');
        const guestEmailInput = document.getElementById('guestEmail');
        const submitBtn = document.getElementById('submitBtn');

        // Error message elements
        const amountError = document.getElementById('amount-error');
        const guestNameError = document.getElementById('guestName-error');
        const guestPhoneError = document.getElementById('guestPhone-error');
        const guestEmailError = document.getElementById('guestEmail-error');
        const donorInfoError = document.getElementById('donor-info-error');

        // Validation state
        let validationState = {
            amount: false,
            donorInfo: true // true when anonymous or at least one field filled
        };

        // Track if fields have been touched (interacted with)
        let touchedFields = {
            amount: false,
            donorInfo: false
        };

        // Toggle donor info section
        function toggleDonorInfo() {
            const isAnonymous = isAnonymousCheckbox.checked;
            
            if (isAnonymous) {
                donorInfoSection.style.display = 'none';
                guestNameInput.value = '';
                guestPhoneInput.value = '';
                guestEmailInput.value = '';
                // Clear all donor info errors
                clearDonorInfoErrors();
                validationState.donorInfo = true;
            } else {
                donorInfoSection.style.display = 'block';
                validationState.donorInfo = false;
            }
            updateSubmitButton();
        }

        // Clear all donor info errors
        function clearDonorInfoErrors() {
            if (guestNameError) guestNameError.textContent = '';
            if (guestPhoneError) guestPhoneError.textContent = '';
            if (guestEmailError) guestEmailError.textContent = '';
            if (donorInfoError) donorInfoError.textContent = '';
            guestNameInput.classList.remove('is-invalid');
            guestPhoneInput.classList.remove('is-invalid');
            guestEmailInput.classList.remove('is-invalid');
        }

        // Validate amount real-time (always show error for invalid values)
        function validateAmount(showError = true) {
            const amountNote = document.getElementById('amount-note');
            const amountValue = amountInput.value ? amountInput.value.trim() : '';
            const amount = parseFloat(amountValue);

            // Check if empty
            if (!amountValue) {
                validationState.amount = false;
                if (showError && touchedFields.amount) {
                    amountInput.classList.add('is-invalid');
                    if (amountError) amountError.textContent = 'Trường này là bắt buộc';
                    if (amountNote) amountNote.style.display = 'none';
                } else {
                    // Clear error if not touched yet
                    amountInput.classList.remove('is-invalid');
                    if (amountError) amountError.textContent = '';
                    if (amountNote) amountNote.style.display = 'inline-block';
                }
                return false;
            }

            // If user has entered a value, ALWAYS validate it (don't wait for touched)
            if (isNaN(amount) || amount < 10000) {
                validationState.amount = false;
                if (showError) {
                    amountInput.classList.add('is-invalid');
                    if (amountError) amountError.textContent = 'Số tiền tối thiểu là 10,000 VND';
                    if (amountNote) amountNote.style.display = 'none';
                }
                return false;
            }

            // Valid amount
            amountInput.classList.remove('is-invalid');
            if (amountError) amountError.textContent = '';
            if (amountNote) amountNote.style.display = 'inline-block';
            validationState.amount = true;
            return true;
        }

        // Validate donor info (at least one field required when not anonymous)
        function validateDonorInfo(showError = true) {
            if (isAnonymousCheckbox.checked) {
                validationState.donorInfo = true;
                clearDonorInfoErrors();
                return true;
            }

            const name = (guestNameInput.value || '').trim();
            const phone = (guestPhoneInput.value || '').trim();
            const email = (guestEmailInput.value || '').trim();

            // Check if at least one field is filled
            if (!name && !phone && !email) {
                validationState.donorInfo = false;
                if (showError && touchedFields.donorInfo) {
                    if (donorInfoError) donorInfoError.textContent = 'Vui lòng điền ít nhất một trong các trường: Tên, Số điện thoại, hoặc Email';
                }
                return false;
            }

            // Clear "at least one" error
            if (donorInfoError) donorInfoError.textContent = '';
            validationState.donorInfo = true;

            // Validate phone format if provided
            const phoneNote = document.getElementById('guestPhone-note');
            if (phone) {
                const phoneOk = /^0\d{9,10}$/.test(phone);
                if (!phoneOk) {
                    guestPhoneInput.classList.add('is-invalid');
                    if (guestPhoneError) guestPhoneError.textContent = 'Số điện thoại phải gồm 10-11 chữ số và bắt đầu bằng 0';
                    if (phoneNote) phoneNote.style.display = 'none';
                } else {
                    guestPhoneInput.classList.remove('is-invalid');
                    if (guestPhoneError) guestPhoneError.textContent = '';
                    if (phoneNote) phoneNote.style.display = 'inline-block';
                }
            } else {
                guestPhoneInput.classList.remove('is-invalid');
                if (guestPhoneError) guestPhoneError.textContent = '';
                if (phoneNote) phoneNote.style.display = 'inline-block';
            }

            // Validate email format if provided
            const emailNote = document.getElementById('guestEmail-note');
            if (email) {
                const emailOk = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/.test(email);
                if (!emailOk) {
                    guestEmailInput.classList.add('is-invalid');
                    if (guestEmailError) guestEmailError.textContent = 'Email không đúng định dạng';
                    if (emailNote) emailNote.style.display = 'none';
                } else {
                    guestEmailInput.classList.remove('is-invalid');
                    if (guestEmailError) guestEmailError.textContent = '';
                    if (emailNote) emailNote.style.display = 'inline-block';
                }
            } else {
                guestEmailInput.classList.remove('is-invalid');
                if (guestEmailError) guestEmailError.textContent = '';
                if (emailNote) emailNote.style.display = 'inline-block';
            }

            return true;
        }

        // Update submit button state
        function updateSubmitButton() {
            const isValid = validationState.amount && validationState.donorInfo;
            if (submitBtn) {
                submitBtn.disabled = !isValid;
                if (!isValid) {
                    submitBtn.classList.add('disabled');
                } else {
                    submitBtn.classList.remove('disabled');
                }
            }
        }

        // Real-time validation for amount - validate immediately on any input
        amountInput.addEventListener('input', () => {
            // Mark as touched if user has entered anything
            if (amountInput.value && amountInput.value.trim() !== '') {
                touchedFields.amount = true;
            }
            validateAmount(true);
            updateSubmitButton();
        });

        amountInput.addEventListener('blur', () => {
            touchedFields.amount = true;
            validateAmount(true);
            updateSubmitButton();
        });

        // Prevent form submission with invalid amount using HTML5 validation
        amountInput.addEventListener('invalid', (e) => {
            e.preventDefault();
            touchedFields.amount = true;
            validateAmount(true);
            updateSubmitButton();
        });

        // Real-time validation for donor info fields
        guestNameInput.addEventListener('input', () => {
            touchedFields.donorInfo = true;
            validateDonorInfo(true);
            updateSubmitButton();
        });

        guestPhoneInput.addEventListener('input', () => {
            touchedFields.donorInfo = true;
            validateDonorInfo(true);
            updateSubmitButton();
        });

        guestEmailInput.addEventListener('input', () => {
            touchedFields.donorInfo = true;
            validateDonorInfo(true);
            updateSubmitButton();
        });

        // Validate on blur for immediate feedback
        guestNameInput.addEventListener('blur', () => {
            touchedFields.donorInfo = true;
            validateDonorInfo(true);
            updateSubmitButton();
        });

        guestPhoneInput.addEventListener('blur', () => {
            touchedFields.donorInfo = true;
            validateDonorInfo(true);
            updateSubmitButton();
        });

        guestEmailInput.addEventListener('blur', () => {
            touchedFields.donorInfo = true;
            validateDonorInfo(true);
            updateSubmitButton();
        });

        // Validate when anonymous checkbox changes
        isAnonymousCheckbox.addEventListener('change', () => {
            toggleDonorInfo();
            touchedFields.donorInfo = true;
            validateDonorInfo(true);
            updateSubmitButton();
        });

        // Form submit validation
        form.addEventListener('submit', function(e) {
            // Mark all fields as touched on submit
            touchedFields.amount = true;
            touchedFields.donorInfo = true;
            
            // Validate amount
            if (!validateAmount(true)) {
                e.preventDefault();
                return false;
            }

            // Validate donor info
            if (!validateDonorInfo(true)) {
                e.preventDefault();
                return false;
            }

            // Final check
            if (!validationState.amount || !validationState.donorInfo) {
                e.preventDefault();
                return false;
            }

            return true;
        });

        // Initial validation (without showing errors)
        validateAmount(false);
        validateDonorInfo(false);
        updateSubmitButton();
    </script>
</body>
</html>

