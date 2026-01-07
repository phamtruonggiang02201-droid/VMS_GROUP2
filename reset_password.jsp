<%-- 
    Document   : reset_password
    Created on : Sep 7, 2025, 4:13:02 PM
    Author     : Admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />
        <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet" />
        <link href="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/7.1.0/mdb.min.css" rel="stylesheet"/>
        <title>Quên mật khẩu</title>
    </head>
    <body>
        <form action="<%= request.getContextPath() %>/ForgetPasswordServlet" method="post">
            <section class="vh-100" style="background-color: #9A616D;">

                <% String error = (String) request.getAttribute("error"); %>
                <% if (error != null) { %>
                <div class="error-message text-danger text-center"><%= error %></div>
                <% } %>

                <% String msg = (String) request.getAttribute("msg"); %>
                <% if (msg != null) { %>
                <div class="text-success text-center"><%= msg %></div>
                <% } %>


                <div class="container py-5 h-100">
                    <div class="row d-flex justify-content-center align-items-center h-100">
                        <div class="col col-xl-10">
                            <div class="card" style="border-radius: 1rem;">
                                <div class="row g-0">
                                    <div class="col-md-6 col-lg-5 d-none d-md-block">
                                        <img src="https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-login-form/img1.webp"
                                             alt="login form" class="img-fluid" style="border-radius: 1rem 0 0 1rem;" />
                                    </div>
                                    <div class="col-md-6 col-lg-7 d-flex align-items-center">
                                        <div class="card-body p-4 p-lg-5 text-black">

                                            <div class="d-flex align-items-center mb-3 pb-1">
                                                <i class="fas fa-cubes fa-2x me-3" style="color: #ff6219;"></i>
                                                <span class="h1 fw-bold mb-0">Quên mật khẩu</span>
                                            </div>

                                            <div class="form-outline mb-4">
                                                <label class="form-label" for="form2Example17">Email</label>
                                                <input type="email" name="email" id="form2Example17" class="form-control form-control-lg border rounded px-2 py-1" />

                                            </div>

                                            <div class="form-outline mb-4">
                                                <label class="form-label" for="form2Example17">Tài khoản</label>
                                                <input type="text" name="username" id="form2Example17" class="form-control form-control-lg border rounded px-2 py-1" />

                                            </div>


                                            <div class="pt-1 mb-4">
                                                <button class="btn btn-danger btn-lg btn-block" type="submit">Gửi</button>
                                            </div>

                                            <p class="mb-5 pb-lg-2" style="color: #393f81;">Bạn đã có tài khoản? 
                                                <a class = "small text-danger " href="<%= request.getContextPath() %>/LoginServlet">Đăng nhập</a>
                                            </p>

                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>
        </form>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/7.1.0/mdb.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
