<%-- 
    Document   : editProduct.jsp
    Created on : Jul 5, 2025, 11:19:07 PM
    Author     : tr45h
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Edit Product</title>
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
        }
        .admin-container {
            max-width: 600px;
            margin: 30px auto;
            padding: 30px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
        }
        input[type="text"], 
        input[type="number"],
        select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .form-actions {
            margin-top: 30px;
            text-align: right;
        }
        .btn {
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            transition: 0.3s;
        }
        .btn-primary {
            background-color: #ff6f61;
            color: white;
        }
        .btn-primary:hover {
            background-color: #e05a4e;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
            margin-right: 10px;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
        }
    </style>
</head>
<body>
    <div class="admin-header">
        <h1>Edit Product</h1>
    </div>

    <div class="admin-container">
        <form action="UpdateProductServlet" method="POST" enctype="multipart/form-data">
            <input type="hidden" name="id" value="${product.id}">
            
            <div class="form-group">
                <label for="name">Product Name</label>
                <input type="text" id="name" name="name" value="${product.name}" required>
            </div>
            
            <div class="form-group">
                <label for="price">Price</label>
                <input type="number" id="price" name="price" step="0.01" value="${product.price}" required>
            </div>
            
            <div class="form-group">
                <label for="category">Category</label>
                <select id="category" name="category" required>
                    <option value="">Select a category</option>
                    <option value="men" ${product.category == 'men' ? 'selected' : ''}>Men</option>
                    <option value="women" ${product.category == 'women' ? 'selected' : ''}>Women</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="image">Product Image</label>
                <input type="file" id="image" name="image">
                <c:if test="${not empty product.imagePath}">
                    <p>Current image: ${product.imagePath}</p>
                    <img src="${product.imagePath}" alt="Current product image" style="max-width: 100px; margin-top: 10px;">
                </c:if>
            </div>
            
            <div class="form-actions">
                <a href="productList" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary">Update Product</button>
            </div>
        </form>
    </div>
</body>
</html>