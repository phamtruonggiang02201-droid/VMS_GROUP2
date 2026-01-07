<%-- 
    Document   : about
    Created on : Sep 16, 2025, 2:52:24 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <title>Welfare - Free Bootstrap 4 Template by Colorlib</title>
        <%@ include file="layout/header.jsp" %>
    </head>
    <body>

        <!-- Navbar -->
        <%@ include file="layout/navbar.jsp" %>
        <!-- END nav -->

        <div class="hero-wrap" style="background-image: url('images/background.jpg');" data-stellar-background-ratio="0.5">
            <div class="overlay"></div>
            <div class="container">
                <div class="row no-gutters slider-text align-items-center justify-content-center" data-scrollax-parent="true">
                    <div class="col-md-7 ftco-animate text-center" data-scrollax=" properties: { translateY: '70%' }">
                        <h1 class="mb-3 bread" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">Giới thiệu</h1>
                    </div>
                </div>
            </div>
        </div>


        <section class="ftco-section">
            <div class="container">
                <div class="row d-flex">
                    <div class="col-md-6 d-flex ftco-animate">
                        <div class="img img-about align-self-stretch" style="background-image: url(images/bg_3.jpg); width: 100%;"></div>
                    </div>
                    <div class="col-md-6 pl-md-5 ftco-animate">
                        <h2 class="mb-4">Chào mừng đến với Hệ thống Quản lý Tình nguyện viên</h2>
                        <p>
                            Hệ thống được xây dựng nhằm kết nối các tổ chức từ thiện với những tình nguyện viên nhiệt huyết, 
                            tạo cầu nối giúp cộng đồng tham gia vào các hoạt động xã hội ý nghĩa. 
                            Chúng tôi cung cấp nền tảng quản lý toàn diện từ đăng ký sự kiện, điểm danh, 
                            đến quyên góp và báo cáo minh bạch.
                        </p>
                        <p>
                            Với giao diện thân thiện và dễ sử dụng, tình nguyện viên có thể dễ dàng tìm kiếm 
                            các sự kiện phù hợp với lịch trình của mình, đăng ký tham gia chỉ với vài thao tác đơn giản. 
                            Các tổ chức có thể quản lý danh sách tình nguyện viên, duyệt đơn đăng ký, 
                            điểm danh và theo dõi nguồn tài trợ một cách hiệu quả. 
                            Hệ thống còn hỗ trợ thông báo tự động, giúp kết nối giữa tổ chức và tình nguyện viên 
                            luôn nhanh chóng và kịp thời.
                        </p>
                        <p>
                            Chúng tôi tin rằng mỗi hành động nhỏ đều có ý nghĩa lớn. 
                            Hãy cùng chúng tôi lan toa yêu thương và xây dựng cộng đồng tốt đẹp hơn. 
                            Tham gia ngay hôm nay để trở thành một phần của những thay đổi tích cực!
                        </p>
                    </div>
                </div>
            </div>
        </section>



        <%@ include file="layout/footer.jsp" %>
        <%@ include file="layout/loader.jsp" %>

    </body>
</html>
