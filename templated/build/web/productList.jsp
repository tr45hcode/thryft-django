<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Product List - Thryft Admin</title>
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

        .product-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 30px;
        }

        .product-table th {
            background-color: #2a2a2a;
            color: white;
            padding: 15px;
            text-align: left;
        }

        .product-table td {
            padding: 12px 15px;
            border-bottom: 1px solid #ddd;
        }

        .product-table tr:hover {
            background-color: #f1f1f1;
        }

        .product-table tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        .product-image {
            max-width: 80px;
            height: auto;
            border-radius: 4px;
        }

        .action-btn {
            display: inline-block;
            padding: 8px 15px;
            background-color: #ff6f61;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: 0.3s;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }

        .action-btn:hover {
            background-color: #e05a4e;
        }

        .table-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }

        .add-product-btn {
            padding: 10px 20px;
            background-color: #2a2a2a;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: 0.3s;
        }

        .add-product-btn:hover {
            background-color: #ff6f61;
        }

        .alert {
            padding: 10px 15px;
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
        .nav-active {
            background-color: #ff6f61;
        }
    </style>
    <script>
        function confirmDelete(productName) {
            return confirm('Are you sure you want to permanently delete "' + productName + '"?\nThis action cannot be undone.');
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
            <li><a href="${pageContext.request.contextPath}/productList" class="nav-active"><i class="fa fa-shopping-bag"></i> Products</a></li>
            <li><a href="${pageContext.request.contextPath}/userList"><i class="fa fa-users"></i> Users</a></li>
            <li><a href="${pageContext.request.contextPath}/viewOrders"><i class="fa fa-list"></i> Orders</a></li>
            <li><a href="${pageContext.request.contextPath}/logoutServlet"><i class="fa fa-sign-out"></i> Logout</a></li>
        </ul>
    </nav>

    <div class="admin-container">
        <c:if test="${not empty param.success}">
            <div class="alert alert-success">${param.success}</div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="alert alert-danger">${param.error}</div>
        </c:if>

        <div class="table-header">
            <h2>Product Management</h2>
            <a href="addProduct.jsp" class="add-product-btn"><i class="fa fa-plus"></i> Add New Product</a>
        </div>

        <table class="product-table">
            <thead>
                <tr>
                    <th>Image</th>
                    <th>Name</th>
                    <th>Price</th>
                    <th>Category</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="product" items="${products}">
                    <tr>
                        <td><img src="${product.imagePath}" alt="${product.name}" class="product-image"></td>
                        <td>${product.name}</td>
                        <td>RM${product.price}</td>
                        <td>${product.category}</td>
                        <td>
                            <a href="editProduct?id=${product.id}" class="action-btn"><i class="fa fa-edit"></i> Edit</a>
                            <form action="deleteProduct" method="POST" style="display:inline;">
                                <input type="hidden" name="id" value="${product.id}">
                                <button type="submit" class="action-btn" 
                                        onclick="return confirmDelete('${product.name}')">
                                    <i class="fa fa-trash"></i> Delete
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>