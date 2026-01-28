<%-- 
    Document   : userList
    Created on : Jul 6, 2025, 8:55:09 PM
    Author     : tr45h
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>User Management - Thryft Admin</title>
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

        .admin-nav a:hover {
            background-color: #ff6f61;
        }

        .admin-container {
            max-width: 1100px;
            margin: 30px auto;
            padding: 20px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }

        .user-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .user-table th {
            background-color: #2a2a2a;
            color: white;
            padding: 12px;
            text-align: left;
        }

        .user-table td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
        }

        .user-table tr:hover {
            background-color: #f1f1f1;
        }

        .admin-badge {
            background-color: #28a745;
            color: white;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 12px;
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

        .delete-btn {
            background-color: #dc3545;
        }

        .edit-btn {
            background-color: #ffc107;
            color: #212529;
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

        .table-actions {
            display: flex;
            gap: 5px;
        }

        .nav-active {
            background-color: #ff6f61;
        }
    </style>
    <script>
        function confirmDelete(username) {
            return confirm('Are you sure you want to permanently delete "' + username + '"?\nThis action cannot be undone.');
        }
    </script>
</head>
<body>
    <div class="admin-header">
        <h1>Thryft Admin Panel</h1>
    </div>

    <nav class="admin-nav">
        <ul>
            <li><a href="${pageContext.request.contextPath}/admin.html"><i class="fa fa-tachometer"></i> Dashboard</a></li>
            <li><a href="${pageContext.request.contextPath}/productList"><i class="fa fa-shopping-bag"></i> Products</a></li>
            <li><a href="${pageContext.request.contextPath}/userList" class="nav-active"><i class="fa fa-users"></i> Users</a></li>
            <li><a href="${pageContext.request.contextPath}/viewOrders"><i class="fa fa-list"></i> Orders</a></li>
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

        <h2><i class="fa fa-users"></i> User Management</h2>

        <table class="user-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Role</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${users}">
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.email}</td>
                        <td>
                            <c:if test="${user.admin}">
                                <span class="admin-badge"><i class="fa fa-shield"></i> Admin</span>
                            </c:if>
                            <c:if test="${not user.admin}">
                                <span><i class="fa fa-user"></i> User</span>
                            </c:if>
                        </td>
                        <td>
                            <div class="table-actions">
                                <a href="editUser?id=${user.id}" class="action-btn edit-btn">
                                    <i class="fa fa-edit"></i> Edit
                                </a>
                                <form action="deleteUser" method="POST" style="display:inline;">
                                    <input type="hidden" name="id" value="${user.id}">
                                    <button type="submit" class="action-btn delete-btn" 
                                            onclick="return confirmDelete('${user.username}')">
                                        <i class="fa fa-trash"></i> Delete
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>