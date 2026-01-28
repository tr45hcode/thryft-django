<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Payment Result</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 40px;
            background-color: #f9f9f9;
            text-align: center;
        }
        .message {
            padding: 20px;
            border-radius: 8px;
            display: inline-block;
            margin-top: 40px;
            font-size: 18px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
        }
        .failure {
            background-color: #f8d7da;
            color: #721c24;
        }
        .info {
            margin-top: 20px;
            font-size: 14px;
            color: #555;
        }
    </style>
</head>
<body>

<%
    String status = request.getParameter("status");
    String billcode = request.getParameter("billcode");
    String amount = request.getParameter("amount");
    String refno = request.getParameter("refno");
%>

<% if ("1".equals(status)) { %>
    <div class="message success">
        <h2>🎉 Payment Successful!</h2>
        <p>Thank you for your purchase.</p>
        <div class="info">
            <p><strong>Bill Code:</strong> <%= billcode %></p>
            <p><strong>Ref No:</strong> <%= refno %></p>
            <p><strong>Amount:</strong> RM <%= amount %></p>
        </div>
    </div>
<% } else if ("3".equals(status)) { %>
    <div class="message failure">
        <h2>❌ Payment Failed</h2>
        <p>We're sorry, your payment was unsuccessful.</p>
        <p>Please try again or contact support.</p>
    </div>
<% } else { %>
    <div class="message failure">
        <h2>❓ Payment Status Unknown</h2>
        <p>No valid payment information was received.</p>
    </div>
<% } %>

</body>
</html>
