<%-- 
    Document   : login
    Created on : Sep 7, 2025, 4:12:36 PM
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
        <title>Đăng nhập</title>
    </head>
    <body>
        <form action="<%= request.getContextPath() %>/LoginServlet" method="post">
            <section class="vh-100" style="background-color: #9A616D;">
                <% 
                    String errorParam = request.getParameter("error");
                    String errorMessage = null;
                    
                    if ("account_locked".equals(errorParam)) {
                        errorMessage = "Tài khoản của bạn bị khóa";
                    } else if ("wrong_credentials".equals(errorParam)) {
                        errorMessage = "Sai tên đăng nhập hoặc mật khẩu";
                    } else if (errorParam != null) {
                        errorMessage = "Đăng nhập thất bại";
                    }
                %>
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
                                                <span class="h1 fw-bold mb-0">Đăng nhập tài khoản</span>
                                            </div>

                                            <% if (errorMessage != null) { %>
                                            <div class="alert alert-danger text-center" role="alert">
                                                <i class="bi bi-exclamation-triangle-fill"></i> <%= errorMessage %>
                                            </div>
                                            <% } %>

                                            <div class="form-outline mb-4">
                                                <label class="form-label" for="form2Example17">Tài khoản</label>
                                                <input type="text" name="username" id="form2Example17" class="form-control form-control-lg border rounded px-2 py-1" />

                                            </div>

                                            <div class="form-outline mb-4">
                                                <label class="form-label" for="form2Example27">Mật khẩu</label>
                                                <input type="password" name="password" id="form2Example27" class="form-control form-control-lg border rounded px-2 py-1" />

                                            </div>

                                            <div class="pt-1 mb-4">
                                                <button class="btn btn-danger btn-lg btn-block" type="submit">Đăng nhập</button>
                                            </div>

                                            <a class="small text-center" href="<%=request.getContextPath()%>/auth/reset_password.jsp">Quên mật khẩu?</a>
                                            <p class="mb-5 pb-lg-2" style="color: #393f81;">Bạn chưa có tài khoản? 
                                                <a class = "small text-danger " href="<%=request.getContextPath()%>/auth/register.jsp">Đăng kí ngay</a>
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
