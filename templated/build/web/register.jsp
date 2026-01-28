<%-- 
    Document   : register
    Created on : 14-Jun-2025, 17:10:30
    Author     : muham
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Register - HexaShop</title>
    <link rel="stylesheet" href="assets/css/style.css"> <!-- Adjust path as needed -->
    <link rel="stylesheet" href="assets/css/fontawesome.css">
    <link rel="stylesheet" href="assets/css/templatemo-hexashop.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <style>
        .register-container {
            max-width: 400px;
            margin: 100px auto;
            background: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0px 0px 10px rgba(0,0,0,0.1);
        }
        .register-container h2 {
            text-align: center;
            margin-bottom: 20px;
        }
        .register-container input[type="text"],
        .register-container input[type="email"],
        .register-container input[type="password"] {
            width: 100%;
            padding: 12px;
            margin: 8px 0;
            border: 1px solid #ccc;
            border-radius: 6px;
        }
        .register-container button {
            width: 100%;
            padding: 12px;
            background-color: #2a2a2a;
            color: #fff;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 10px;
        }
        .register-container button:hover {
            background-color: #ff6f61;
        }
    </style>
</head>
<body>

    <div class="register-container">
        <h2>Create Account</h2>
        <form action="register" method="post">
            <input type="text" name="username" placeholder="Username" required>
            <input type="email" name="email" placeholder="Email" required>
            <input type="password" name="password" placeholder="Password" required>
            <input type="password" name="confirm_password" placeholder="Confirm Password" required>
            <button type="submit">Register</button>
        </form>
        <p style="text-align:center; margin-top: 15px;">
            Already have an account? <a href="login.jsp">Login</a>
        </p>
    </div>

</body>
</html>

