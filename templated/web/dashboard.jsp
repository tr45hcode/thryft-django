<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Dashboard - Thryft</title>
  <link rel="stylesheet" href="assets/css/style.css">
  <link rel="stylesheet" href="assets/css/fontawesome.css">
  <link rel="stylesheet" href="assets/css/templatemo-hexashop.css">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <style>
    body {
      margin: 0;
      font-family: Arial, sans-serif;
      background-color: #f9f9f9;
    }

    .dashboard-header {
      background-color: #2a2a2a;
      color: #fff;
      padding: 20px;
      text-align: center;
    }

    .dashboard-container {
      max-width: 1000px;
      margin: 30px auto;
      padding: 20px;
      background-color: #fff;
      border-radius: 10px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }

    .dashboard-welcome {
      text-align: center;
      margin-bottom: 30px;
    }

    .dashboard-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      gap: 20px;
    }

    .card {
      padding: 20px;
      background-color: #f1f1f1;
      border-radius: 10px;
      text-align: center;
      transition: 0.3s;
    }

    .card:hover {
      background-color: #ff6f61;
      color: white;
      cursor: pointer;
    }
    
    .logout-btn {
      background-color: #ff6f61;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 5px;
      cursor: pointer;
      margin-top: 20px;
    }
  </style>
</head>

<body>

  <div class="dashboard-header">
    <h1>Welcome to Thryft Dashboard</h1>
  </div>

  <div class="dashboard-container">
    <div class="dashboard-welcome">
      <h2>Hello, <%= session.getAttribute("username") != null ? session.getAttribute("username") : "User" %>!</h2>
      <p>Select an option below to get started.</p>
    </div>

    <div class="dashboard-grid">
      <div class="card" onclick="location.href='profile'">My Profile</div>
      <div class="card" onclick="location.href='orders'">Orders</div>
      <div class="card" onclick="location.href='cart'">Cart</div>
      <div class="card" onclick="location.href='logoutServlet'">Log Out</div>
    </div>
  </div>

</body>
</html>