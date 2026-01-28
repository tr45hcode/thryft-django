<%-- 
    Document   : login
    Created on : 14-Jun-2025, 17:08:44
    Author     : muham
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login - HexaShop</title>
    <link rel="stylesheet" href="assets/css/style.css"> <!-- Adjust path as needed -->
    <link rel="stylesheet" href="assets/css/fontawesome.css">
    <link rel="stylesheet" href="assets/css/templatemo-hexashop.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        .login-container {
            max-width: 400px;
            margin: 100px auto;
            background: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0px 0px 10px rgba(0,0,0,0.1);
        }
        .login-container h2 {
            text-align: center;
            margin-bottom: 20px;
        }
        .login-container input[type="text"],
        .login-container input[type="password"] {
            width: 100%;
            padding: 12px;
            margin: 8px 0;
            border: 1px solid #ccc;
            border-radius: 6px;
        }
        .login-container button {
            width: 100%;
            padding: 12px;
            background-color: #2a2a2a;
            color: #fff;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 10px;
        }
        .login-container button:hover {
            background-color: #ff6f61;
        }
    </style>
</head>
<body>

    <div class="login-container">
        <h2>Login</h2>
        <form action="login" method="post"> <!-- change here -->
            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Log In</button>
        </form>
           <p style="color:red; text-align:center;">
            <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
        </p>
        <p style="text-align:center; margin-top: 15px;">
            Don't have an account? <a href="register.jsp">Register here</a>
        </p>
    </div>


</body>
</html>