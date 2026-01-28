<%-- 
    Document   : editUser
    Created on : Jul 6, 2025, 11:31:38 PM
    Author     : tr45h
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Edit User</title>
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
        input[type="email"] {
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
            background-color: #28a745;
            color: white;
        }
        .btn-primary:hover {
            background-color: #218838;
        }
        .btn-secondary {
            background-color: #6c757d;
            color: white;
            margin-right: 10px;
        }
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        .checkbox-group {
            margin: 15px 0;
        }

    </style>
</head>
<body>
    <div class="admin-header">
        <h1>Edit User</h1>
    </div>

    <div class="admin-container">


        <form action="editUser" method="POST">
            <input type="hidden" name="id" value="${user.id}">
            
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" value="${user.username}" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="${user.email}" required>
            </div>
            
            <div class="form-group">
                <label for="password">password</label>
                <input type="text" id="password" name="password" value="${user.password}" required>
            </div>
            
            <div class="checkbox-group">
                <input type="checkbox" id="admin" name="admin" ${user.admin ? 'checked' : ''}>
                <label for="admin">Administrator</label>
            </div>
            
            <div class="form-actions">
                <a href="userList" class="btn btn-secondary">Cancel</a>
                <button type="submit" class="btn btn-primary">Update User</button>
            </div>
        </form>
    </div>
    
    <!-- Font Awesome for icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</body>
</html>