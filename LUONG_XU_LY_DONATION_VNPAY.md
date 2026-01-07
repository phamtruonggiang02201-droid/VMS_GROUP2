# LUỒNG XỬ LÝ DONATION TÍCH HỢP VNPAY

## 📋 TỔNG QUAN HỆ THỐNG

Hệ thống hỗ trợ 2 loại người donate:
1. **Volunteer** (đã đăng nhập) - có account_id
2. **Guest** (khách vãng lai) - không cần đăng nhập, có thể ẩn danh

---

## 🔄 LUỒNG 1: VOLUNTEER DONATE

### Bước 1: Volunteer chọn donate cho sự kiện
**File:** `VolunteerDonateFormServlet.java`
- Volunteer đã đăng nhập vào hệ thống
- Click nút "Donate" trên trang chi tiết sự kiện
- Hệ thống kiểm tra:
  - Volunteer đã đăng nhập chưa?
  - Volunteer đã donate cho sự kiện này chưa? (mỗi volunteer chỉ donate 1 lần/sự kiện)
- Lấy thông tin volunteer từ bảng `Users` (full_name, email, phone)
- Forward đến trang `payment_volunteer.jsp`

### Bước 2: Volunteer nhập thông tin thanh toán
**File:** `payment_volunteer.jsp`
- Hiển thị form thanh toán với:
  - Thông tin sự kiện (readonly)
  - Thông tin volunteer (readonly - lấy từ profile)
  - Số tiền donate (input - tối thiểu 10,000 VND)
  - Ghi chú (optional)
  - Phương thức: VNPay (mặc định)
- Validate client-side:
  - Số tiền >= 10,000 VND
  - Số tiền phải là số hợp lệ
- Submit form đến `/volunteer-payment-donation`

### Bước 3: Khởi tạo giao dịch VNPay
**File:** `VolunteerPaymentDonationServlet.java` (doPost)

**3.1. Xác thực và lấy thông tin:**
```
- Kiểm tra session volunteer
- Lấy thông tin từ bảng Users (full_name, email, phone)
- Lấy parameters: eventId, amount, note
```

**3.2. Validate dữ liệu:**
```
- Validate amount >= 10,000 VND (qua VolunteerDonationService)
```

**3.3. Tạo/Lấy bản ghi Donor:**
```
- Gọi: donationService.createOrGetDonor(accountId, fullName, phone, email)
- Service gọi: PaymentDonationDAO.createOrGetDonor()
- Kiểm tra bảng Donors:
  - Nếu volunteer đã có donor record → trả về donor_id
  - Nếu chưa → INSERT INTO Donors (donor_type='volunteer', account_id, full_name, phone, email)
- Trả về: donor_id
```

**3.4. Xây dựng parameters VNPay:**
```
- vnp_Version = "2.1.0"
- vnp_Command = "pay"
- vnp_Amount = amount * 100 (chuyển sang xu)
- vnp_TxnRef = "DONATE{eventId}_{random8digits}" (mã giao dịch duy nhất)
- vnp_OrderInfo = "Donation for Event #{eventId} - {fullName}"
- vnp_ReturnUrl = động (lấy từ request để hoạt động trên mọi môi trường)
- vnp_CreateDate, vnp_ExpireDate (hết hạn sau 15 phút)
```

**3.5. Tạo chữ ký bảo mật:**
```
- Sắp xếp tất cả parameters theo alphabet
- URL encode từng parameter
- Nối thành chuỗi: "key1=value1&key2=value2&..."
- Tạo HMAC SHA512 với secretKey
- Thêm vnp_SecureHash vào URL
```

**3.6. Lưu bản ghi Payment_Donations:**
```
- INSERT INTO Payment_Donations:
  - donor_id
  - event_id
  - payment_txn_ref
  - payment_amount
  - order_info
  - payment_gateway = 'VNPay'
  - payment_status = 'pending'
```

**3.7. Lưu thông tin vào session:**
```
- donation_donor_id
- donation_event_id
- donation_note
- donation_txn_ref
```

**3.8. Redirect đến VNPay:**
```
- response.sendRedirect(paymentUrl)
- User được chuyển đến trang thanh toán VNPay
```

### Bước 4: User thanh toán trên VNPay
- User chọn phương thức thanh toán (ATM, Credit Card, Ví điện tử)
- Nhập thông tin thẻ/tài khoản
- Xác nhận OTP
- VNPay xử lý giao dịch

### Bước 5: VNPay callback về hệ thống
**File:** `VolunteerPaymentDonationReturnServlet.java` (doGet)

**5.1. Nhận parameters từ VNPay:**
```
- vnp_TxnRef (mã giao dịch)
- vnp_Amount (số tiền)
- vnp_ResponseCode (mã kết quả: 00=thành công)
- vnp_TransactionNo (mã giao dịch VNPay)
- vnp_BankCode, vnp_CardType, vnp_PayDate
- vnp_SecureHash (chữ ký để verify)
```

**5.2. Validate chữ ký:**
```
- Lấy tất cả parameters (trừ vnp_SecureHash)
- URL encode và sắp xếp
- Tạo hash bằng HMAC SHA512
- So sánh với vnp_SecureHash từ VNPay
- Nếu không khớp → Giao dịch có thể bị giả mạo
```

**5.3. Lấy thông tin từ session:**
```
- donation_donor_id
- donation_event_id
- donation_note
```

**5.4. Lấy chi tiết payment từ DB:**
```
- PaymentDonationDAO.getPaymentDonationByTxnRef(vnp_TxnRef)
- Trả về: donor info, event info, payment info
```

**5.5. Xử lý kết quả thanh toán:**

**Nếu THÀNH CÔNG (vnp_ResponseCode = "00"):**
```
a) Update Payment_Donations:
   - SET payment_status = 'success'
   - SET bank_code, card_type, pay_date, response_code, transaction_no, secure_hash

b) Tạo bản ghi Donations:
   - INSERT INTO Donations:
     - event_id
     - volunteer_id (account_id của volunteer)
     - donor_id
     - amount (chia 100 để về VND)
     - status = 'success'
     - payment_method = 'VNPay'
     - payment_txn_ref
     - note
   - Trigger tự động cập nhật total_donation trong bảng Events

c) Gửi email cảm ơn:
   - Lấy email từ bảng Donors/Users
   - Gửi email với chi tiết donation
   - Template HTML đẹp với thông tin đầy đủ

d) GỬI THÔNG BÁO CHO ORGANIZATION:
   - Lấy thông tin event và organization
   - Lấy tên volunteer từ Users.full_name
   - INSERT INTO Notifications:
     - sender_id = volunteer_id
     - receiver_id = organization_id
     - message = "{Tên volunteer} đã ủng hộ {số tiền} VNĐ cho sự kiện \"{tên sự kiện}\" của bạn"
     - type = 'donation'
     - event_id
```

**Nếu THẤT BẠI:**
```
a) Update Payment_Donations:
   - SET payment_status = 'failed'
   - SET các thông tin giao dịch

b) Tạo bản ghi Donations với status = 'failed'
   - Để tracking lịch sử giao dịch thất bại
```

**5.6. Xóa session data:**
```
- Xóa donation_donor_id, donation_event_id, donation_note, donation_txn_ref
```

**5.7. Redirect về trang lịch sử:**
```
- Nếu thành công: successMessage
- Nếu thất bại: errorMessage
- Redirect: /VolunteerDonateServlet
```

### Bước 6: Hiển thị lịch sử donation
**File:** `VolunteerDonateServlet.java`
- Lấy danh sách donations của volunteer
- Hỗ trợ filter theo:
  - Ngày (startDate, endDate)
  - Trạng thái (success, failed, pending)
- Phân trang (5 donations/trang)
- Hiển thị top 3 donors
- Forward đến `history_transaction_volunteer.jsp`

---

## 🔄 LUỒNG 2: GUEST DONATE

### Bước 1: Guest chọn donate cho sự kiện
**File:** `GuestDonateFormServlet.java`
- Guest KHÔNG cần đăng nhập
- Click nút "Donate" trên trang sự kiện công khai
- Lấy eventId từ URL parameter
- Lấy thông tin event từ DB
- Forward đến `donate_form.jsp`

### Bước 2: Guest nhập thông tin
**File:** `donate_form.jsp`

**Form bao gồm:**
```
1. Thông tin sự kiện (readonly)

2. Số tiền donate (required, min 10,000 VND)

3. Checkbox "Ẩn danh":
   - Nếu check → Không cần điền thông tin cá nhân
   - Nếu không check → Phải điền ít nhất 1 trong 3: Tên, SĐT, Email

4. Thông tin cá nhân (nếu không ẩn danh):
   - Họ và tên (optional)
   - Số điện thoại (optional, validate: 0xxxxxxxxx)
   - Email (optional, validate format)
   - Ít nhất 1 field phải có giá trị

5. Ghi chú (optional)
```

**Validation client-side:**
```javascript
- Số tiền >= 10,000 VND
- Nếu không ẩn danh:
  - Ít nhất 1 trong 3 field (name/phone/email) phải có giá trị
  - Phone: regex ^0\d{9,10}$
  - Email: regex email hợp lệ
- Real-time validation khi user nhập
- Disable submit button nếu invalid
```

### Bước 3: Khởi tạo giao dịch VNPay
**File:** `GuestPaymentDonationServlet.java` (doPost)

**3.1. Lấy và validate parameters:**
```
- eventId, amount, note
- isAnonymous (checkbox)
- guestName, guestPhone, guestEmail (nếu không ẩn danh)
```

**3.2. Validate dữ liệu:**
```
- Validate amount >= 10,000 VND
- Validate guest info:
  - Nếu không ẩn danh → ít nhất 1 field phải có
  - Validate phone format
  - Validate email format
```

**3.3. Tạo bản ghi Donor:**
```
- Gọi: donationService.createOrGetDonor(fullName, phone, email, isAnonymous)
- INSERT INTO Donors:
  - donor_type = 'guest'
  - account_id = NULL (guest không có account)
  - full_name = guestName (hoặc NULL nếu ẩn danh)
  - phone = guestPhone (hoặc NULL nếu ẩn danh)
  - email = guestEmail (hoặc NULL nếu ẩn danh)
  - is_anonymous = true/false
- Trả về: donor_id
```

**3.4. Xây dựng parameters VNPay:**
```
- Tương tự volunteer
- vnp_TxnRef = "DONATE{eventId}_{random8digits}"
- vnp_OrderInfo = "Donation for Event #{eventId} - {donorName hoặc 'Anonymous Donor'}"
- vnp_ReturnUrl = getGuestDonationReturnUrl() (khác với volunteer)
```

**3.5. Tạo chữ ký và lưu Payment_Donations:**
```
- Tương tự volunteer
- Lưu vào session: donor_id, event_id, note, txn_ref
```

**3.6. Redirect đến VNPay**

### Bước 4: Guest thanh toán trên VNPay
- Tương tự volunteer

### Bước 5: VNPay callback về hệ thống
**File:** `GuestPaymentDonationReturnServlet.java` (doGet)

**Xử lý tương tự VolunteerPaymentDonationReturnServlet, khác biệt:**

**5.1. Tạo bản ghi Donations:**
```
- INSERT INTO Donations:
  - event_id
  - volunteer_id = NULL (guest không có account)
  - donor_id
  - amount
  - status = 'success'/'failed'
  - payment_method = 'VNPay'
  - payment_txn_ref
  - note
```

**5.2. Gửi email cảm ơn:**
```
- Chỉ gửi nếu:
  - Guest không ẩn danh
  - Guest có cung cấp email
```

**5.3. GỬI THÔNG BÁO CHO ORGANIZATION:**
```
- Lấy tên donor:
  - Nếu có donorFullName → dùng tên đó
  - Nếu không → "Một nhà hảo tâm"
- INSERT INTO Notifications:
  - sender_id = organization_id (vì guest không có account_id, dùng org_id để tránh FK constraint)
  - receiver_id = organization_id
  - message = "{Tên guest hoặc 'Một nhà hảo tâm'} đã ủng hộ {số tiền} VNĐ cho sự kiện \"{tên}\" của bạn"
  - type = 'donation'
  - event_id
```

**5.4. Redirect về trang chủ:**
```
- Redirect: /home (khác với volunteer)
- Hiển thị message thành công/thất bại
```

---

## 📊 CÁC BẢNG DATABASE LIÊN QUAN

### 1. Donors
```sql
- id (PK)
- donor_type ('volunteer' | 'guest')
- account_id (FK → Accounts, NULL cho guest)
- full_name
- phone
- email
- is_anonymous (BIT)
- created_at
```

### 2. Payment_Donations
```sql
- payment_id (PK)
- donation_id (FK → Donations, NULL ban đầu)
- donor_id (FK → Donors)
- event_id (FK → Events)
- payment_txn_ref (UNIQUE)
- payment_amount (BIGINT - đơn vị xu)
- payment_gateway ('VNPay')
- bank_code, card_type, pay_date
- response_code, transaction_no, transaction_status
- secure_hash
- payment_status ('pending' | 'success' | 'failed')
- created_at, updated_at
```

### 3. Donations
```sql
- id (PK)
- event_id (FK → Events)
- volunteer_id (FK → Accounts, NULL cho guest)
- donor_id (FK → Donors)
- amount (DECIMAL)
- donate_date
- status ('success' | 'failed' | 'cancelled')
- payment_method ('VNPay')
- payment_txn_ref
- note
```

### 4. Events
```sql
- id (PK)
- ...
- total_donation (DECIMAL) - Tự động cập nhật bởi trigger
```

### 5. Notifications
```sql
- id (PK)
- sender_id (FK → Accounts, NULL cho guest)
- receiver_id (FK → Accounts)
- message
- type ('donation' | 'apply' | ...)
- event_id (FK → Events)
- created_at
- is_read (BIT)
```

---

## 🔐 BẢO MẬT VÀ VALIDATE

### 1. Chữ ký VNPay (vnp_SecureHash)
```
- Mục đích: Đảm bảo dữ liệu không bị giả mạo
- Thuật toán: HMAC SHA512
- Secret Key: Lấy từ VNPay Merchant Portal
- Validate: So sánh hash tạo ra với hash từ VNPay
```

### 2. Validate số tiền
```
- Client-side: JavaScript real-time
- Server-side: Service layer
- Minimum: 10,000 VND
```

### 3. Validate thông tin guest
```
- Nếu không ẩn danh: ít nhất 1 field
- Phone: 10-11 số, bắt đầu bằng 0
- Email: format hợp lệ
```

### 4. Session security
```
- Lưu donor_id, event_id, note trong session
- Xóa sau khi xử lý xong
- Timeout: 15 phút (theo VNPay expiry)
```

---

## 📧 EMAIL VÀ NOTIFICATION

### 1. Email cảm ơn donor
```
- Gửi đến: email của donor (nếu có)
- Template: HTML đẹp với thông tin đầy đủ
- Nội dung:
  - Tên sự kiện
  - Số tiền
  - Mã giao dịch
  - Phương thức thanh toán
  - Lời cảm ơn
```

### 2. Notification cho Organization
```
- Hiển thị trong hệ thống
- Real-time (nếu org đang online)
- Lưu vào DB để xem lại sau
- Nội dung: "{Tên} đã ủng hộ {số tiền} cho sự kiện của bạn"
```

---

## 🔄 TRIGGER TỰ ĐỘNG

### Trigger: trg_UpdateDonationTotals
```sql
-- Tự động cập nhật total_donation trong Events
-- Khi INSERT/UPDATE/DELETE trong Donations với status='success'

CREATE TRIGGER trg_UpdateDonationTotals
ON Donations
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    -- Cập nhật tổng donation cho event
    UPDATE Events
    SET total_donation = (
        SELECT ISNULL(SUM(amount), 0)
        FROM Donations
        WHERE event_id = Events.id
        AND status = 'success'
    )
    WHERE id IN (
        SELECT DISTINCT event_id FROM inserted
        UNION
        SELECT DISTINCT event_id FROM deleted
    )
END
```

---

## 🎯 ĐIỂM KHÁC BIỆT VOLUNTEER VS GUEST

| Tiêu chí | Volunteer | Guest |
|----------|-----------|-------|
| Đăng nhập | Bắt buộc | Không cần |
| Thông tin | Lấy từ profile | Nhập thủ công |
| Ẩn danh | Không | Có thể |
| volunteer_id trong Donations | account_id | NULL |
| sender_id trong Notifications | volunteer_id | organization_id |
| Return URL | /VolunteerDonateServlet | /home |
| Email | Luôn gửi (nếu có) | Chỉ gửi nếu không ẩn danh |

---

## 🚀 FLOW DIAGRAM

```
VOLUNTEER FLOW:
User → Login → Event Detail → Donate Button → Payment Form (auto-fill info)
→ Submit → Create Donor → Create Payment_Donations → Redirect VNPay
→ User Pay → VNPay Callback → Validate → Update Payment → Create Donation
→ Send Email → Send Notification to Org → Redirect History

GUEST FLOW:
User → Event Detail → Donate Button → Donation Form (manual input)
→ Choose Anonymous? → Submit → Create Donor → Create Payment_Donations
→ Redirect VNPay → User Pay → VNPay Callback → Validate → Update Payment
→ Create Donation → Send Email (if not anonymous) → Send Notification to Org
→ Redirect Home
```

---

## 📝 LƯU Ý QUAN TRỌNG

1. **VNPay Sandbox vs Production:**
   - Sandbox: Test với thẻ test
   - Production: Cần đăng ký merchant thật

2. **Return URL động:**
   - Lấy từ request để hoạt động trên mọi môi trường
   - Localhost, IP, Domain đều OK

3. **Trigger tự động:**
   - total_donation tự cập nhật
   - Không cần code thủ công

4. **Foreign Key Constraint:**
   - Guest donation: sender_id = organization_id (workaround)
   - Hoặc cho phép sender_id NULL trong DB

5. **Transaction timeout:**
   - VNPay: 15 phút
   - Session: Nên match với VNPay

---

## 🎉 KẾT LUẬN

Hệ thống donation đã được tích hợp đầy đủ với VNPay, hỗ trợ cả volunteer và guest, 
có validation chặt chẽ, bảo mật tốt, và trải nghiệm người dùng mượt mà!
