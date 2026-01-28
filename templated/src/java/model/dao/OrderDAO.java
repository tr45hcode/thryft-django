/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.dao;

/**
 *
 * @author tr45h
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private Connection getConnection() throws SQLException {
        // Replace with your actual database connection method
        String url = "jdbc:derby://localhost:1527/thryft";
        String username = "app";
        String password = "app";
        return DriverManager.getConnection(url, username, password);
    }

    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT id, user_id, total_price, status, created_at FROM orders";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getString("user_id"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at"));
                orders.add(order);
            }
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
    System.out.println("[DAO] Attempting to update order " + orderId + " to status " + status);
    
    String sql = "UPDATE orders SET status = ? WHERE id = ?";
    System.out.println("[DAO] SQL: " + sql);
    
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, status);
        stmt.setInt(2, orderId);
        
        System.out.println("[DAO] Parameters set: status=" + status + ", orderId=" + orderId);
        
        int rowsAffected = stmt.executeUpdate();
        System.out.println("[DAO] Rows affected: " + rowsAffected);
        
        return rowsAffected > 0;
    } catch (SQLException e) {
        System.out.println("[DAO ERROR] SQL Exception: " + e.getMessage());
        throw e;
    }
}

    // Order model class (can be in separate file)
    public static class Order {
        private int id;
        private String userId;
        private double totalPrice;
        private String status;
        private Timestamp createdAt;

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }
}