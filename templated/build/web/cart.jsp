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
    <script>
        function confirmDelete(productName) {
            return confirm('Are you sure you want to permanently delete "' + productName + '"?\nThis action cannot be undone.');
        }
    </script>
    <script>
        function updateQuantityAndRefresh(productId, userId, newQuantityStr) {
            let newQty = parseInt(newQuantityStr);
            let quantityInput = document.getElementById('quantity_' + productId);

            // Input validation: Ensure quantity is a valid number and at least 1
            // If newQty is 0, the server will remove the item, which is fine.
            if (isNaN(newQty) || newQty < 0) { // Allow 0 to enable deletion
                newQty = 1; // Default to 1 if invalid (but not for explicit 0)
                quantityInput.value = newQty; // Update input field to valid value
            }

            // ⭐⭐⭐ IMPORTANT: Adjust '/yourWebAppName' to your actual web application's context path if needed ⭐⭐⭐
            // If your app runs at http://localhost:8080/myproject/, use '/myproject/updateCartItem'
            // If your app runs at http://localhost:8080/, use '/updateCartItem'
            const servletUrl = '/updateCartItem'; 

            fetch(servletUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ 
                    productId: String(productId), // Send as string to match servlet's parsing
                    userId: String(userId),       // Send as string
                    newQuantity: newQty           // Send as number
                }),
            })
            .then(response => {
                // Check for HTTP errors (e.g., 401, 404, 500)
                if (!response.ok) {
                    // Try to parse error message from server if available, otherwise use default
                    return response.text().then(errorText => { // Read as text, might not be JSON
                        try {
                            const errorJson = JSON.parse(errorText); // Try to parse as JSON
                            throw new Error(errorJson.message || 'Server error: ' + response.statusText);
                        } catch (e) {
                            // If not JSON, just use the raw text or default message
                            throw new Error('Server error (' + response.status + '): ' + errorText || response.statusText);
                        }
                    });
                }
                return response.json(); // Expecting JSON response from backend
            })
            .then(data => {
                if (data.success) {
                    console.log("Cart updated successfully, refreshing page:", data.message);
                    window.location.reload(); // Refresh the entire page
                } else {
                    alert('Error updating cart: ' + data.message);
                    // No refresh on failure, user can see the error
                }
            })
            .catch(error => {
                console.error('There was a problem with the fetch operation:', error);
                alert('Failed to update cart. ' + error.message || 'Please try again.');
            });
        }
    </script>

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
                                    <a href="dashboard.jsp" title="User Dashboard">
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
    
    <!-- ***** Products Area Starts ***** -->
    <section class="section" id="products">
        <div class="container" style="margin-top:30px;">
            <div class="row">
                <div class="col-lg-12">
                    <div class="section-heading">
                        <h2>Your Cart</h2>
                    </div>
                </div>
            </div>
        </div>
        <div class="container">
            <%@ page import="model.bean.CartItem, model.bean.Product" %>
            <%
                List<CartItem> cartItems = (List<CartItem>) request.getAttribute("cartItems");
                Integer userId = (Integer) request.getAttribute("userId"); 
                double grandTotal = 0;
            %>

            <div class="row" style="" >
                <% if (cartItems == null || cartItems.isEmpty()) { %>
                    <div class="col-lg-12 text-center">
                        <h4>Your cart is empty.</h4>
                    </div>
                <% } else { 
                    for (CartItem item : cartItems) {
                        Product p = item.getProduct();
                        int qty = item.getQuantity();
                        double total = p.getPrice() * qty;
                        grandTotal += total;
                %>
                <div class="col-12 mb-4">
                    <div class="item d-flex align-items-center" style="border: 1px solid #ddd; border-radius: 8px; margin-top:0px; margin-bottom:0px; padding: 15px;">
                        <div class="thumb" style="flex: 0 0 120px; margin-right: 20px; overflow: hidden;">
                            <img src="<%= p.getImagePath() %>" alt="<%= p.getName() %>" style="width: 100%; height: 100%; object-fit: cover; border-radius: 6px;">
                        </div>
                        <div class="down-content" style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
                            <div style="flex-grow: 1;"> <h4 style="margin: 0;"><%= p.getName() %></h4>
                                <p style="margin: 5px 0;">Category: <%= p.getCategory() %></p>
                                <p style="margin: 0; display: flex; align-items: center; gap: 5px;">
                                    RM <%= String.format("%.2f", p.getPrice()) %> x 
                                    <div class="quantity-controls">
                                        <%-- Form for Decrement Quantity --%>
                                        <form action="<%= request.getContextPath() %>/cart" method="post">
                                            <input type="hidden" name="action" value="decrement"/>
                                            <input type="hidden" name="productId" value="<%= p.getId() %>"/>
                                            <input type="hidden" name="userId" value="<%= userId %>"/>
                                            <button type="submit">-</button>
                                        </form>

                                        <%-- Display current quantity --%>
                                        <span class="current-quantity">
                                            <%= qty %>
                                        </span>

                                        <%-- Form for Increment Quantity --%>
                                        <form action="<%= request.getContextPath() %>/cart" method="post">
                                            <input type="hidden" name="action" value="increment"/>
                                            <input type="hidden" name="productId" value="<%= p.getId() %>"/>
                                            <input type="hidden" name="userId" value="<%= userId %>"/>
                                            <button type="submit">+</button>
                                        </form>
                                    </div>
                                    = <strong>RM <span id="total_<%= p.getId() %>"><%= String.format("%.2f", total) %></span></strong>
                                </p>
                            </div>
                            <div>
                                <form action="removeCartItem" method="POST" style="display:inline;">
                                    <input type="hidden" name="id" value="<%= p.getId() %>">
                                    <button type="submit" class="delete-btn" 
                                            onclick="return confirmDelete('<%= p.getName() %>')">
                                        <i class="fa fa-trash"></i> Delete
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <% } } %>
                <%
                    String userAddress = (String) request.getAttribute("userAddress");
                %>
        </div>
                        <div class="cart-summary-row" style="margin-left:80px;">
                    <!-- Left side: Address -->
                    <div class="cart-summary-left">
                        <% if (userAddress != null && !userAddress.trim().isEmpty()) { %>
                            <p><strong>Delivery Address:</strong> <%= userAddress %></p>
                        <% } else { %>
                            <p><strong>Add a delivery address to continue with purchase</strong></p>
                        <% } %>
                    </div>

                    <!-- Right side: Total + Checkout -->
                    <div class="cart-summary-right" style="margin-right:80px;">
                        <h4>Total: RM <%= String.format("%.2f", grandTotal) %></h4>
                        <% if (userAddress != null && !userAddress.trim().isEmpty()) { %>
                            <form action="checkout" method="post">
                                <input type="hidden" name="amount" value="<%= Math.round(grandTotal * 100) %>">
                                <button type="submit" class="btn btn-primary">Checkout</button>
                            </form>
                        <% } %>
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
                            <img src="assets/images/rsz_trift.png" alt="Thryft Logo">
                        </div>
                        <ul>
                            <li><a href="#">Persiaran Multimedia, Seksyen 7, Jalan Plumbum 7/102, I-City, 40000 Shah Alam, Selangor</a></li>
                            <li><a href="#">thryfster@gmail.com</a></li>
                            <li><a href="#">013-332-7313</a></li>
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
                        <p>Copyright © 2022 Thryft Co., Ltd. All Rights Reserved. </p>
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
    width: 360px;      /* Or whatever size fits your layout */
    height: 360px;
    object-fit: cover; /* Ensures image fills the box while maintaining crop */
    border-radius: 8px; /* Optional: rounded corners */
}
.cart-summary-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap; /* So it wraps on smaller screens */
    margin-top: 30px;
}

.cart-summary-left {
    text-align: left;
    flex: 1;
}

.cart-summary-right {
    text-align: right;
    flex: 1;
}

</style>
<style>
    .delete-btn {
        background-color: #ff4444; /* Red color */
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 3px;
        cursor: pointer;
        font-size: 14px;
        transition: all 0.3s ease;
        display: inline-flex;
        align-items: center;
        gap: 6px;
    }
    
    .delete-btn:hover {
        background-color: #cc0000; /* Darker red on hover */
        transform: translateY(-1px);
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .delete-btn:active {
        transform: translateY(0);
        box-shadow: none;
    }
    
    .delete-btn i {
        font-size: 14px;
    }
</style>
<style>
    .quantity-controls {
        display: flex;
        align-items: center;
        gap: 5px; /* Space between buttons and quantity */
    }

    .quantity-controls form {
        margin: 0; /* Remove default form margins */
        padding: 0;
        line-height: 1; /* Helps with vertical alignment of button content */
    }

    .quantity-controls button {
        background-color: #f0f0f0; /* Light gray background */
        border: 1px solid #ccc;
        color: #333;
        cursor: pointer;
        font-size: 0.9em; /* Smaller font */
        width: 20px; /* Fixed width */
        height: 20px; /* Fixed height for square shape */
        display: flex;
        justify-content: center; /* Center content horizontally */
        align-items: center; /* Center content vertically */
        border-radius: 4px; /* Slightly rounded corners */
        padding: 0; /* Remove default button padding */
        box-shadow: 0 1px 2px rgba(0,0,0,0.1); /* Subtle shadow */
        transition: background-color 0.2s ease, box-shadow 0.2s ease; /* Smooth hover effect */
    }

    .quantity-controls button:hover {
        background-color: #e2e6ea; /* Darker on hover */
        box-shadow: 0 2px 4px rgba(0,0,0,0.15); /* Slightly more prominent shadow on hover */
    }

    .current-quantity {
        font-size: 1em; /* Standard font size for quantity */
        font-weight: bold;
        color: #333;
        min-width: 20px; /* Ensure space for quantity */
        text-align: center;
        padding: 0 2px; /* Small padding left/right */
    }
</style>