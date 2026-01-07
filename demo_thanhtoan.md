
# Demo: Luồng xử lý Donation tích hợp VNPay (ngắn gọn, có file & hàm)

## 📋 Tổng quan

Hỗ trợ 2 loại donor:

1. Volunteer — đã đăng nhập (có `account_id`).

2. Guest — không cần đăng nhập, có thể ẩn danh.

Tài liệu này tóm tắt ngắn gọn từng bước xử lý, kèm file và hàm/method chính liên quan.

---

## 1) Volunteer — tóm tắt luồng (vắn nhưng chỉ rõ file/hàm)

- Màn hình form: `VolunteerDonateFormServlet.java` (`doGet`)

  - Kiểm tra session, lấy `account` và `accountId`.

  - Gọi `VolunteerDonationService.hasVolunteerDonated(volunteerId, eventId)` để kiểm tra.

  - Lấy `Event` bằng `VolunteerDonationService.getEventById(eventId)`.

  - Forward sang `/volunteer/payment_volunteer.jsp`.

- Khởi tạo thanh toán (submit form): `VolunteerPaymentDonationServlet.java` (`doPost`)

  - Xác thực session (account phải là volunteer).

  - Lấy thông tin user từ `UserDAO.getUserByAccountId(accountId)` (fullname, email, phone).

  - Validate amount: `VolunteerDonationService.validateDonationAmount(amount)`.

  - Tạo hoặc lấy `Donor`: gọi
    `VolunteerDonationService.createOrGetDonor(accountId, fullName, phone, email)` → trong service gọi `PaymentDonationDAO.createOrGetDonor(...)`.

  - Chuẩn bị params VNPay: sử dụng `PaymentConfig` helpers:

    - `PaymentConfig.getRandomNumber(...)` để tạo `vnp_TxnRef`;

    - `PaymentConfig.getIpAddress(request)` để lấy IP;

    - `PaymentConfig.getVolunteerDonationReturnUrl(request)` làm `vnp_ReturnUrl`.

  - Tạo chữ ký: `PaymentConfig.hmacSHA512(PaymentConfig.secretKey, hashData)` hoặc `PaymentConfig.hashAllFields(map)`.

  - Lưu record tạm `Payment_Donations` qua
    `VolunteerDonationService.createPaymentDonation(...)` → `PaymentDonationDAO.createPaymentDonation(...)`.

  - Lưu vài giá trị vào session (donor_id, event_id, txn_ref) và `response.sendRedirect(paymentUrl)`.

- Callback từ VNPay: `VolunteerPaymentDonationReturnServlet.java` (`doGet`)

  - Thu tất cả params từ VNPay (`vnp_TxnRef`, `vnp_Amount`, `vnp_ResponseCode`, `vnp_SecureHash`, ...).

  - Xóa các field hash, gọi `PaymentConfig.hashAllFields(fields)` để verify chữ ký so với `vnp_SecureHash`.

  - Lấy payment detail: `PaymentDonationDAO.getPaymentDonationByTxnRef(vnp_TxnRef)` (trả về donor info, event_id, v.v.).

  - Nếu chữ ký hợp lệ và `vnp_ResponseCode == "00"`:

    - `PaymentDonationDAO.updatePaymentDonation(...)` để đặt `payment_status='success'` và cập nhật thông tin bank/transaction.

    - Tạo bản ghi `Donations` bằng
      `PaymentDonationDAO.createDonation(eventId, volunteerId, donorId, amount, "success", "VNPay", vnp_TxnRef, note)`.

    - Gửi email cảm ơn nếu có email (gọi `EmailUtil` hoặc helper gửi mail trong project).

    - Tạo `Notification` cho organization (thông qua service/DAO tương ứng).

  - Nếu thất bại hoặc chữ ký không hợp lệ: `updatePaymentDonation(..., payment_status='failed')` và ghi log.

  - Xóa session data liên quan và redirect về `/VolunteerDonateServlet`.

- Lấy lịch sử: `VolunteerDonateServlet.java` (`doGet`)

  - Gọi `DisplayDonateService.getUserDonationsPaged(volunteerId, page, pageSize)` và
    `getTotalDonationsByVolunteer(volunteerId)`.

  - Forward sang `history_transaction_volunteer.jsp`.

---

## 2) Guest — tóm tắt luồng (file & hàm)

- Màn hình form: `GuestDonateFormServlet.java` (`doGet`)

  - Lấy `eventId`, gọi `GuestDonationService.getEventById(eventId)`.

  - Forward sang `/donate_form.jsp`.

- Khởi tạo thanh toán: `GuestPaymentDonationServlet.java` (`doPost`)

  - Lấy params form (`eventId`, `amount`, `isAnonymous`, `guestName`, `guestPhone`, `guestEmail`, `note`).

  - Validate amount: `GuestDonationService.validateDonationAmount(amount)`.

  - Validate guest info: `GuestDonationService.validateGuestInfo(isAnonymous, guestName, guestPhone, guestEmail)`.

  - Tạo hoặc lấy `Donor`:
    `GuestDonationService.createOrGetDonor(fullName, phone, email, isAnonymous)` → `PaymentDonationDAO.createOrGetDonor(...)`.

  - Chuẩn bị params VNPay (như volunteer), sử dụng
    `PaymentConfig.getGuestDonationReturnUrl(request)` cho `vnp_ReturnUrl`.

  - Tạo chữ ký với `PaymentConfig.hashAllFields(...)`/`hmacSHA512`.

  - Lưu `Payment_Donations` tạm bằng `PaymentDonationDAO.createPaymentDonation(...)`.

  - Lưu donor_id/event_id/txn_ref vào session và redirect tới `paymentUrl`.

- Callback từ VNPay: `GuestPaymentDonationReturnServlet.java` (`doGet`)

  - Xử lý tương tự Volunteer: verify chữ ký, lấy payment detail via
    `PaymentDonationDAO.getPaymentDonationByTxnRef(...)`.

  - Nếu thành công:

    - `PaymentDonationDAO.updatePaymentDonation(..., payment_status='success')`;

    - `PaymentDonationDAO.createDonation(eventId, null (guest), donorId, amount, "success", "VNPay", vnp_TxnRef, note)`;

    - Gửi email cảm ơn **chỉ khi** guest không ẩn danh và có email;

    - Tạo `Notification` cho organization (sender/receiver xử lý như doc kiến nghị).

  - Nếu thất bại: update payment status = 'failed' và lưu record thất bại để tracking.

  - Xóa session và redirect về `/home` (guest flow).

---

## 3) DAO / Utils chính (chỉ tên hàm quan trọng)

- `PaymentDonationDAO`:

  - `createOrGetDonor(...)` — tạo hoặc trả về donor_id.

  - `createPaymentDonation(donorId, eventId, paymentTxnRef, amount, orderInfo, gateway)` —
    lưu Payment_Donations (pending).

  - `updatePaymentDonation(paymentTxnRef, bankCode, cardType, payDate, responseCode, transactionNo, transactionStatus, secureHash, paymentStatus)` —
    cập nhật khi callback.

  - `createDonation(eventId, volunteerId, donorId, amount, status, paymentMethod, paymentTxnRef, note)` —
    insert vào `Donations` và trả về donation_id.

  - `getPaymentDonationByTxnRef(paymentTxnRef)` — lấy chi tiết Payment_Donations kèm donor/event.

  - `getDonorEmail(donorId)` — lấy email để gửi mail.

- `DonationDAO`:

  - `getTotalDonationAmount()`

  - `getDonationHistoryByVolunteerId(volunteerId)`

  - `getDonationDetailById(donationId)`

- `VolunteerDonationDAO`:

  - `createDonation(...)` — dùng cho một số luồng nội bộ (pending insert)

- `PaymentConfig` (utils):

  - `hmacSHA512(key, data)` — tạo chữ ký HMAC SHA512.

  - `hashAllFields(Map<String,String>)` — sắp xếp + nối + trả về hash (dùng verify và tạo `vnp_SecureHash`).

  - `getGuestDonationReturnUrl(request)`, `getVolunteerDonationReturnUrl(request)` — tạo return URL động.

  - `getRandomNumber(len)`, `getIpAddress(request)` — helpers.

- `EmailUtil` / utils gửi mail: gọi hàm gửi mail (project có `utils.EmailUtil` được sử dụng trong servlets).

---

## 4) Lưu ý ngắn gọn (để dev nhanh nắm)

- VNPay amount: **nhân 100** (đơn vị VNPay là xu). Khi lưu `Donations.amount` cần chia 100.

- Chữ ký VNPay: luôn verify `vnp_SecureHash` trước khi tin callback.

- Payment_Donations.payment_status: dùng `pending` → `success`/`failed`.

- Trigger DB `trg_UpdateDonationTotals` cập nhật `Events.total_donation` — không cần update thủ công.

- Giữ `vnp_TmnCode` và `secretKey` an toàn (không commit vào repo trong production).

---

## 5) File để mở nhanh (quick links)

- Servlets:

  - `src/java/controller_volunteer/VolunteerDonateFormServlet.java`

  - `src/java/controller_volunteer/VolunteerPaymentDonationServlet.java`

  - `src/java/controller_volunteer/VolunteerPaymentDonationReturnServlet.java`

  - `src/java/controller_volunteer/VolunteerDonateServlet.java`

  - `src/java/controller_view/GuestDonateFormServlet.java`

  - `src/java/controller_view/GuestPaymentDonationServlet.java`

  - `src/java/controller_view/GuestPaymentDonationReturnServlet.java`

- DAO/Utils:

  - `src/java/dao/PaymentDonationDAO.java`

  - `src/java/dao/DonationDAO.java`

  - `src/java/dao/VolunteerDonationDAO.java`

  - `src/java/utils/PaymentConfig.java`

  - `src/java/utils/EmailUtil.java` (nếu có)

---

Tôi đã tạo file `demo_thanhtoan.md` trong gốc project. Muốn tôi:

- (A) Bổ sung ví dụ đoạn code ngắn cho phần verify chữ ký và tạo donation, hoặc

- (B) Commit các thay đổi (nếu bạn muốn), hoặc

- (C) Kiểm tra toàn repo còn comment tiếng Anh nào nữa và liệt kê.

Chọn A, B, hoặc C (hoặc yêu cầu khác).