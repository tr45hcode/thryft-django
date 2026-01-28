// File: src/main/java/controller/UpdateCartItemServlet.java
package controller;

// NO Gson imports needed anymore

import model.bean.DatabaseConnection; // Your existing DatabaseConnection utility

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
// No need for HashMap or Map for the response if manually building string

@WebServlet("/updateCartItem") // This is the new URL pattern for your AJAX requests
public class updateCartItemServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String productIdStr = null;
        String userIdStr = null;
        String newQuantityStr = null; // Will read quantity as string first

        boolean success = false;
        String message = "Unknown error occurred."; // Default message

        try {
            // Read raw JSON payload from request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String jsonPayload = sb.toString();

            // --- Manual JSON Parsing ---
            // This is a very basic string parsing. It's brittle to variations in whitespace
            // or order of elements in the JSON.
            // Example JSON: {"productId":"123", "userId":"456", "newQuantity":10}

            // Extract productId
            int productIdStart = jsonPayload.indexOf("\"productId\":\"") + "\"productId\":\"".length();
            int productIdEnd = jsonPayload.indexOf("\"", productIdStart);
            if (productIdStart > -1 && productIdEnd > productIdStart) {
                productIdStr = jsonPayload.substring(productIdStart, productIdEnd);
            }

            // Extract userId
            int userIdStart = jsonPayload.indexOf("\"userId\":\"") + "\"userId\":\"".length();
            int userIdEnd = jsonPayload.indexOf("\"", userIdStart);
            if (userIdStart > -1 && userIdEnd > userIdStart) {
                userIdStr = jsonPayload.substring(userIdStart, userIdEnd);
            }

            // Extract newQuantity
            int quantityStart = jsonPayload.indexOf("\"newQuantity\":") + "\"newQuantity\":".length();
            int quantityEnd = jsonPayload.indexOf("}", quantityStart); // Assumes quantity is last
            if (quantityEnd == -1) { // If not last, find comma or end of object
                 quantityEnd = jsonPayload.indexOf(",", quantityStart);
                 if (quantityEnd == -1) { // If no comma, try finding closing brace
                     quantityEnd = jsonPayload.indexOf("}", quantityStart);
                 }
            }
            if (quantityStart > -1 && quantityEnd > quantityStart) {
                newQuantityStr = jsonPayload.substring(quantityStart, quantityEnd).trim();
            }

            // --- End Manual JSON Parsing ---

            // Convert to appropriate integer types
            int productId = (productIdStr != null && !productIdStr.isEmpty()) ? Integer.parseInt(productIdStr) : 0;
            int userId = (userIdStr != null && !userIdStr.isEmpty()) ? Integer.parseInt(userIdStr) : 0;
            int newQuantity = (newQuantityStr != null && !newQuantityStr.isEmpty()) ? Integer.parseInt(newQuantityStr) : 0;

            // --- CRUCIAL SECURITY CHECK ---
            HttpSession session = request.getSession();
            Integer sessionUserId = (Integer) session.getAttribute("userId");

            if (sessionUserId == null || sessionUserId != userId) {
                success = false;
                message = "Unauthorized access or session expired. Please log in again.";
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // HTTP 401
                // No Gson here, manually build JSON for this early exit
                out.print("{\"success\":false, \"message\":\"Unauthorized access or session expired. Please log in again.\"}");
                return;
            }
            // --- END SECURITY CHECK ---

            Connection conn = null;

            try {
                conn = DatabaseConnection.initializeDatabase(); // Get your database connection

                if (newQuantity <= 0) {
                    // If quantity is 0 or less, remove the item from the cart
                    String deleteSql = "DELETE FROM cart_items WHERE user_id=? AND product_id=?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, userId);
                        deleteStmt.setInt(2, productId);
                        boolean deleted = deleteStmt.executeUpdate() > 0;
                        success = deleted;
                        message = deleted ? "Item successfully removed from cart." : "Item not found in cart for removal.";
                    }
                } else {
                    // Update the quantity of the existing item
                    String updateSql = "UPDATE cart_items SET quantity=? WHERE user_id=? AND product_id=?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, newQuantity);
                        updateStmt.setInt(2, userId);
                        updateStmt.setInt(3, productId);
                        boolean updatedRows = updateStmt.executeUpdate() > 0;
                        success = updatedRows;
                        message = updatedRows ? "Quantity updated successfully." : "Item not found in cart for update (consider adding it).";
                    }
                }

            } catch (SQLException e) {
                success = false;
                message = "Database error during update: " + e.getMessage();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // HTTP 500
                e.printStackTrace(); // Log the exception for debugging
            } finally {
                if (conn != null) {
                    try {
                        conn.close(); // Close the database connection
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            success = false;
            message = "Error parsing request or server issue: " + e.getMessage();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // HTTP 400 for bad input
            e.printStackTrace(); // Log the exception
        } finally {
            // Manually build the JSON response string
            out.print("{\"success\":" + success + ", \"message\":\"" + escapeJson(message) + "\"}");
            out.flush();
        }
    }

    // Helper method to escape special characters in JSON string values
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\\r")
                   .replace("\t", "\\t");
    }
}