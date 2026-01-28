<%-- 
    Document   : orderList
    Created on : Jul 10, 2025, 9:49:58 PM
    Author     : tr45h
    ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Order Management - Thryft Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/font-awesome.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/templatemo-hexashop.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background-color: #f5f5f5;
        }

        .admin-header {
            background-color: #2a2a2a;
            color: #fff;
            padding: 20px;
            text-align: center;
            position: sticky;
            top: 0;
            z-index: 1000;
            box-shadow: 0 2px 5px rgba(0,0,0,0.2);
        }

        .admin-nav {
            background-color: #333;
            padding: 10px 0;
        }

        .admin-nav ul {
            list-style: none;
            padding: 0;
            margin: 0;
            display: flex;
            justify-content: center;
        }

        .admin-nav li {
            margin: 0 15px;
        }

        .admin-nav a {
            color: white;
            text-decoration: none;
            font-weight: 500;
            padding: 5px 10px;
            border-radius: 4px;
            transition: all 0.3s ease;
        }

        .admin-nav a:hover, .nav-active {
            background-color: #ff6f61;
        }

        .admin-container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .order-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .order-table th {
            background-color: #2a2a2a;
            color: white;
            padding: 12px;
            text-align: left;
        }

        .order-table td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }

        .order-table tr:hover {
            background-color: #f1f1f1;
        }

        .order-image {
            max-width: 80px;
            height: auto;
            border-radius: 4px;
        }

        .status-select {
            padding: 6px;
            border-radius: 4px;
            border: 1px solid #ddd;
            width: 100%;
        }

        .action-btn {
            display: inline-block;
            padding: 6px 12px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-right: 5px;
            font-size: 14px;
            border: none;
            cursor: pointer;
            transition: all 0.3s;
        }

        .action-btn:hover {
            opacity: 0.9;
            transform: translateY(-1px);
        }

        .save-btn {
            background-color: #28a745;
        }

        .view-btn {
            background-color: #6c757d;
        }

        .form-row {
            display: flex;
            gap: 5px;
        }

        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }

        .alert-success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .alert-danger {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
    </style>
</head>
<body>
    <div class="admin-header">
        <h1>Thryft Admin Panel</h1>
    </div>

    <nav class="admin-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin.html"><i class="fa fa-tachometer"></i> Dashboard</a></li>
            <li><a href="${pageContext.request.contextPath}/productList"><i class="fa fa-shopping-bag"></i> Products</a></li>
            <li><a href="${pageContext.request.contextPath}/userList"><i class="fa fa-users"></i> Users</a></li>
            <li><a href="${pageContext.request.contextPath}/viewOrders" class="nav-active"><i class="fa fa-list"></i> Orders</a></li>
            <li><a href="${pageContext.request.contextPath}/logoutServlet"><i class="fa fa-sign-out"></i> Logout</a></li>
        </ul>
    </nav>

    <div class="admin-container">
        <c:if test="${not empty param.success}">
            <div class="alert alert-success">
                <i class="fa fa-check-circle"></i> ${param.success}
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">
                <i class="fa fa-exclamation-circle"></i> ${param.error}
            </div>
        </c:if>

        <h2><i class="fa fa-list"></i> Order Management</h2>

        <form action="updateOrderStatus" method="POST">
            <table class="order-table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Bill Code</th>
                        <th>Customer</th>
                        <th>Product</th>
                        <th>Quantity</th>
                        <th>Unit Price</th>
                        <th>Total Price</th>
                        <th>Status</th>
                        <th>Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="order" items="${orders}">
                        <tr class="status-${order.status.toLowerCase()}">
                            <td>${order.id}</td>
                            <td>${order.billCode}</td>
                            <td>
                                ${order.username}<br>
                                <small>${order.userEmail}</small>
                            </td>
                            <td>
                                <img src="${order.productImage}" alt="${order.productName}" class="order-image"><br>
                                ${order.productName}
                            </td>
                            <td>${order.quantity}</td>
                            <td><fmt:formatNumber value="${order.productPrice}" type="currency"/></td>
                            <td><fmt:formatNumber value="${order.totalPrice}" type="currency"/></td>
                            <td>
                                <select name="status_${order.id}" class="status-select">
                                    <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                    <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                </select>
                            </td>
                            <td><fmt:formatDate value="${order.createdAt}" pattern="MMM dd, yyyy HH:mm"/></td>
                            <td>
                                <div class="form-row">
                                    <button type="submit" name="orderId" value="${order.id}" class="action-btn save-btn">
                                        <i class="fa fa-save"></i> Save
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </form>
    </div>
</body>
</html>