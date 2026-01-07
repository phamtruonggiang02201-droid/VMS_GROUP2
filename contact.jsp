<%-- 
    Document   : contact
    Created on : Sep 16, 2025, 2:53:20 PM
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

        <div class="hero-wrap" style="background-image: url('images/bg_2.jpg');" data-stellar-background-ratio="0.5">
            <div class="overlay"></div>
            <div class="container">
                <div class="row no-gutters slider-text align-items-center justify-content-center" data-scrollax-parent="true">
                    <div class="col-md-7 ftco-animate text-center" data-scrollax=" properties: { translateY: '70%' }">
                        <p class="breadcrumbs" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">
                            <span class="mr-2">
                                <a href="index.html">Trang chủ</a>
                            </span> 
                            <span>
                                <a href="contact.jsp">Liên hệ</a>
                            </span>
                        </p>
                        <h1 class="mb-3 bread" data-scrollax="properties: { translateY: '30%', opacity: 1.6 }">Liên hệ chúng tôi</h1>
                    </div>
                </div>
            </div>
        </div>


        <section class="ftco-section contact-section ftco-degree-bg">
            <div class="container">
                <div class="row d-flex mb-5 contact-info">
                    <div class="col-md-12 mb-4">
                        <h2 class="h4">Thông tin liên hệ</h2>
                    </div>
                    <div class="w-100"></div>
                    <div class="col-md-3">
                        <p><span>Address:</span> 198 West 21th Street, Suite 721 New York NY 10016</p>
                    </div>
                    <div class="col-md-3">
                        <p><span>Phone:</span> <a href="tel://1234567920">+ 1235 2355 98</a></p>
                    </div>
                    <div class="col-md-3">
                        <p><span>Email:</span> <a href="mailto:info@yoursite.com">info@yoursite.com</a></p>
                    </div>
                    <div class="col-md-3">
                        <p><span>Website</span> <a href="#">yoursite.com</a></p>
                    </div>
                </div>
                <div class="row block-9">
                    <div class="col-md-6 pr-md-5">
                        <h4 class="mb-4">Do you have any questions?</h4>
                        <form action="#">
                            <div class="form-group">
                                <input type="text" class="form-control" placeholder="Your Name">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" placeholder="Your Email">
                            </div>
                            <div class="form-group">
                                <input type="text" class="form-control" placeholder="Subject">
                            </div>
                            <div class="form-group">
                                <textarea name="" id="" cols="30" rows="7" class="form-control" placeholder="Message"></textarea>
                            </div>
                            <div class="form-group">
                                <input type="submit" value="Send Message" class="btn btn-primary py-3 px-5">
                            </div>
                        </form>

                    </div>

                    <div class="col-md-6" id="map"></div>
                </div>
            </div>
        </section>

        <%@ include file="layout/footer.jsp" %>
        <%@ include file="layout/loader.jsp" %>

    </body>
</html>
