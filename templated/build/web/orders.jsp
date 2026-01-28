<%@ page import="java.util.*, java.text.SimpleDateFormat" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Your Orders - Thryft</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- CSS Files -->
    <link rel="stylesheet" href="assets/css/bootstrap.min.css">
    <link rel="stylesheet" href="assets/css/font-awesome.css">
    <link rel="stylesheet" href="assets/css/templatemo-hexashop.css">

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding-top: 60px;
        }

        .orders-container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 40px 30px;
            background-color: #fff;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.05);
            position: relative;
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            font-weight: 600;
            color: #333;
        }

        .table {
            background-color: #fff;
        }

        .thead-light th {
            background-color: #ff6f61;
            color: white;
        }

        .btn-back {
            position: absolute;
            top: 20px;
            right: 30px;
        }

        .btn-secondary {
            background-color: #6c757d;
            color: white;
            padding: 8px 16px;
            border-radius: 6px;
            text-decoration: none;
            transition: 0.3s ease;
            font-weight: 500;
        }

        .btn-secondary:hover {
            background-color: #5a6268;
        }

        .alert-info {
            background-color: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
            padding: 15px;
            text-align: center;
            border-radius: 5px;
        }
    </style>
</head>
<body>

<div class="container orders-container">
    <div class="btn-back">
        <a href="dashboard.jsp" class="btn btn-secondary">
            Back to Dashboard <i class="fa fa-arrow-right"></i> 
        </a>
    </div>

    <h2>Your Orders</h2>

    <%
        List<Map<String, Object>> orders = (List<Map<String, Object>>) request.getAttribute("orders");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm a");
    %>

    <%
        if (orders == null || orders.isEmpty()) {
    %>
        <div class="alert alert-info">You have no orders yet.</div>
    <%
        } else {
    %>
        <table class="table table-bordered">
            <thead class="thead-light">
            <tr>
                <th>#</th>
                <th>Product</th>
                <th>Quantity</th>
                <th>Total Price (RM)</th>
                <th>Status</th>
                <th>Ordered At</th>
            </tr>
            </thead>
            <tbody>
            <% for (Map<String, Object> order : orders) { %>
                <tr>
                    <td><%= order.get("id") %></td>
                    <td><%= order.get("product_name") %></td>
                    <td><%= order.get("quantity") %></td>
                    <td><%= String.format("%.2f", order.get("total_price")) %></td>
                    <td><%= order.get("status") %></td>
                    <td><%= sdf.format(order.get("created_at")) %></td>
                </tr>
            <% } %>
            </tbody>
        </table>
    <%
        }
    %>
</div>

</body>
</html>
