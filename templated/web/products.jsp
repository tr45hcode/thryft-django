<%@ page import="java.util.*, model.bean.Product" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

  <head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <link href="https://fonts.googleapis.com/css?family=Poppins:100,200,300,400,500,600,700,800,900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <title>Thryft - Product Listing Page</title>


    <!-- Additional CSS Files -->
    <link rel="stylesheet" type="text/css" href="assets/css/bootstrap.min.css">

    <link rel="stylesheet" type="text/css" href="assets/css/font-awesome.css">

    <link rel="stylesheet" href="assets/css/templatemo-hexashop.css">

    <link rel="stylesheet" href="assets/css/owl-carousel.css">

    <link rel="stylesheet" href="assets/css/lightbox.css">
<!--

TemplateMo 571 Hexashop

https://templatemo.com/tm-571-hexashop

-->
    </head>
    
    <body>
    
    <!-- ***** Preloader Start ***** -->
    <div id="preloader">
        <div class="jumper">
            <div></div>
            <div></div>
            <div></div>
        </div>
    </div>  
    <!-- ***** Preloader End ***** -->
    
    
    <!-- ***** Header Area Start ***** -->
    <header class="header-area header-sticky">
        <div class="container">
            <div class="row">
                <div class="col-12">
                    <nav class="main-nav">
                        <!-- ***** Logo Start ***** -->
                        <a href="index.html" class="logo">
                            <img src="assets/images/trift.png">
                        </a>
                        <!-- ***** Logo End ***** -->
                        <!-- ***** Menu Start ***** -->
                        <ul class="nav">
                            <li><a href="index">Home</a></li>
                            <li><a href="index#men">Men's</a></li>
                            <li><a href="index#women">Women's</a></li>
                            <li><a href="products">Products</a></li>
                            <%
                                String user = (String) session.getAttribute("username");
                                if (user != null) {
                            %>
                                <li>
                                    <a href="cart">
                                        <i class='fas fa-cart-plus' style='font-size:18px; padding-bottom:0px; padding-top: 10px; margin-bottom: 0px;'></i>
                                    </a>
                                </li>
                                <li>
                                    <a href="dashboard.jsp">
                                        <i class="fa fa-user" style="font-size: 20px;"></i> <!-- Font Awesome Icon -->
                                    </a>
                                </li>
                            <%
                                } else {
                            %>
                                <li><a href="login.jsp">Login</a></li>
                            <%
                                }
                            %>
                        </ul>   
                        <!-- ***** Menu End ***** -->
                    </nav>
                </div>
            </div>
        </div>
    </header>
    <!-- ***** Header Area End ***** -->

    <!-- ***** Main Banner Area Start ***** -->
    <div class="page-heading" id="top">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="inner-content">
                        <h2>Check Our Products</h2>
                        <span>The best stuff from Thryft.</span>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- ***** Main Banner Area End ***** -->


    <!-- ***** Products Area Starts ***** -->
    <div id="toast" style="display:none; position:fixed; bottom:20px; right:20px; background-color:#333; color:#fff; padding:12px 20px; border-radius:6px; box-shadow:0 2px 10px rgba(0,0,0,0.2); z-index:9999;">
    ✅ Item added to cart!
    </div>

    <script>
      const urlParams = new URLSearchParams(window.location.search);
      if (urlParams.get("cartSuccess") === "true") {
        const toast = document.getElementById('toast');
        toast.style.display = 'block';
        toast.style.opacity = '1';

        setTimeout(() => {
          toast.style.transition = 'opacity 1s ease';
          toast.style.opacity = '0';
          setTimeout(() => toast.style.display = 'none', 1000);
        }, 3000);

        // Remove from URL
        const url = new URL(window.location);
        url.searchParams.delete("cartSuccess");
        window.history.replaceState({}, document.title, url);
      }
    </script>


    <section class="section" id="products">
        <div class="container">
            <div class="row">
                <div class="col-lg-12">
                    <div class="section-heading">
                        <h2>Our Latest Products</h2>
                        <span>Check out all of our products.</span>
                    </div>
                </div>
            </div>
        </div>
        <div class="container">
            <div class="row">
                <%
                    ArrayList<Product> allProducts = (ArrayList<Product>) request.getAttribute("allProducts");
                    Integer userId = (Integer) session.getAttribute("userId");
                    if (allProducts != null && !allProducts.isEmpty()) {
                        for (Product p : allProducts) {
                %>
                    <div class="col-lg-4">
                        <div class="item d-flex flex-column" style="height:100%">
                            <div class="thumb">
                                <img src="<%= p.getImagePath() %>" alt="<%= p.getName() %>">
                            </div>
                            <div class="down-content">
                                <h4><%= p.getName() %></h4>
                                <span>$<%= p.getPrice() %></span>
                                <!-- Add to Cart Button Below -->
                                <form method="post" action="<%= request.getContextPath() %>/cart" style="margin-top:0px; margin-bottom: 50px;">
                                    <input type="hidden" name="action" value="add"/>
                                    <input type="hidden" name="productId" value="<%= p.getId() %>">
                                    <input type="hidden" name="userId" value="<%= userId %>"> <div style="display: flex; align-items: center; justify-content:flex-end;">
                                        <input type="number" name="quantity" value="1" min="1" style="width: 60px;">
                                        <button type="submit" class="btn btn-sm btn-primary" style="margin-left: 10px; height:35px; background-color:black; border:black;">
                                            <i class="fa fa-shopping-cart"></i> Add to Cart
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>

                <%
                        }
                    } else {
                %>
                    <p>No products available.</p>
                <%
                    }
                %>
                <div class="col-lg-12">
                    <div class="pagination">
                        <ul>
                            <li>
                                <a href="#">1</a>
                            </li>
                            <li class="active">
                                <a href="#">2</a>
                            </li>
                            <li>
                                <a href="#">3</a>
                            </li>
                            <li>
                                <a href="#">4</a>
                            </li>
                            <li>
                                <a href="#">></a>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </section>
    <!-- ***** Products Area Ends ***** -->
    
    <!-- ***** Footer Start ***** -->
    <footer>
        <div class="container">
            <div class="row">
                <div class="col-lg-3">
                    <div class="first-item">
                        <div class="logo">
                            <img src="assets/images/rsz_trift.png" alt="hexashop ecommerce templatemo">
                        </div>
                        <ul>
                            <li><a href="#">16501 Collins Ave, Sunny Isles Beach, FL 33160, United States</a></li>
                            <li><a href="#">hexashop@company.com</a></li>
                            <li><a href="#">010-020-0340</a></li>
                        </ul>
                    </div>
                </div>
                <div class="col-lg-3">
                    <h4>Shopping &amp; Categories</h4>
                    <ul>
                        <li><a href="#">Men’s Shopping</a></li>
                        <li><a href="#">Women’s Shopping</a></li>
                        <li><a href="#">Kid's Shopping</a></li>
                    </ul>
                </div>
                <div class="col-lg-3">
                    <h4>Useful Links</h4>
                    <ul>
                        <li><a href="#">Homepage</a></li>
                        <li><a href="#">About Us</a></li>
                        <li><a href="#">Help</a></li>
                        <li><a href="#">Contact Us</a></li>
                    </ul>
                </div>
                <div class="col-lg-3">
                    <h4>Help &amp; Information</h4>
                    <ul>
                        <li><a href="#">Help</a></li>
                        <li><a href="#">FAQ's</a></li>
                        <li><a href="#">Shipping</a></li>
                        <li><a href="#">Tracking ID</a></li>
                    </ul>
                </div>
                <div class="col-lg-12">
                    <div class="under-footer">
                        <p>Copyright © 2022 HexaShop Co., Ltd. All Rights Reserved. 
                        
                        <br>Design: <a href="https://templatemo.com" target="_parent" title="free css templates">TemplateMo</a></p>
                        <ul>
                            <li><a href="#"><i class="fa fa-facebook"></i></a></li>
                            <li><a href="#"><i class="fa fa-twitter"></i></a></li>
                            <li><a href="#"><i class="fa fa-linkedin"></i></a></li>
                            <li><a href="#"><i class="fa fa-behance"></i></a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </footer>
    

    <!-- jQuery -->
    <script src="assets/js/jquery-2.1.0.min.js"></script>

    <!-- Bootstrap -->
    <script src="assets/js/popper.js"></script>
    <script src="assets/js/bootstrap.min.js"></script>

    <!-- Plugins -->
    <script src="assets/js/owl-carousel.js"></script>
    <script src="assets/js/accordions.js"></script>
    <script src="assets/js/datepicker.js"></script>
    <script src="assets/js/scrollreveal.min.js"></script>
    <script src="assets/js/waypoints.min.js"></script>
    <script src="assets/js/jquery.counterup.min.js"></script>
    <script src="assets/js/imgfix.min.js"></script> 
    <script src="assets/js/slick.js"></script> 
    <script src="assets/js/lightbox.js"></script> 
    <script src="assets/js/isotope.js"></script> 
    
    <!-- Global Init -->
    <script src="assets/js/custom.js"></script>

  </body>

</html>
<style>
    .thumb img {
    width: 340px;      /* Or whatever size fits your layout */
    height: 340px;
    object-fit: cover; /* Ensures image fills the box while maintaining crop */
    border-radius: 8px; /* Optional: rounded corners */
}
.down-content form {
    margin-top: 10px;
}

.down-content input[type="number"] {
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 5px;
    text-align: center;
}
.item {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.down-content {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}


</style>