package controller;

import model.bean.CartItem;
import model.bean.DatabaseConnection;
import model.bean.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    // Handles Add to Cart OR Quantity Update
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        // String address = (String) session.getAttribute("userAddress"); // Not used here, can remove if always null

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // --- NEW: Get the 'action' parameter to differentiate requests ---
        String action = request.getParameter("action");

        // Common parameters, will be parsed safely
        String productIdParam = request.getParameter("productId");
        String formUserIdParam = request.getParameter("userId"); // userId from the form

        int productId = 0;
        int formUserId = 0;

        try {
            // Safely parse productId from request
            if (productIdParam != null && !productIdParam.isEmpty()) {
                productId = Integer.parseInt(productIdParam);
            } else {
                // If productId is missing, it's an invalid request for this servlet's logic
                throw new IllegalArgumentException("Product ID is missing.");
            }

            // Safely parse userId from form and perform security check
            if (formUserIdParam != null && !formUserIdParam.isEmpty()) {
                formUserId = Integer.parseInt(formUserIdParam);
            } else {
                throw new IllegalArgumentException("User ID from form is missing.");
            }

            // --- CRITICAL SECURITY CHECK ---
            // Ensure userId from session matches userId from the form data
            if (!userId.equals(formUserId)) { // Use .equals() for Integer object comparison
                System.err.println("Security alert: Mismatched user IDs. Session: " + userId + ", Form: " + formUserId);
                response.sendRedirect(request.getContextPath() + "/cart?error=unauthorized");
                return;
            }
            // --- END SECURITY CHECK ---

            Connection conn = null; // Declare here to be accessible in finally
            try {
                conn = DatabaseConnection.initializeDatabase(); // Get your database connection

                if ("increment".equals(action)) {
                    // Logic for incrementing quantity
                    String updateSql = "UPDATE cart_items SET quantity = quantity + 1 WHERE user_id=? AND product_id=?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, userId);
                        updateStmt.setInt(2, productId);
                        updateStmt.executeUpdate();
                    }
                    response.sendRedirect(request.getContextPath() + "/cart"); // Redirect to refresh cart page
                    return; // Stop processing
                } else if ("decrement".equals(action)) {
                    // Logic for decrementing quantity
                    String checkQtySql = "SELECT quantity FROM cart_items WHERE user_id=? AND product_id=?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkQtySql)) {
                        checkStmt.setInt(1, userId);
                        checkStmt.setInt(2, productId);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next()) {
                                int currentQuantity = rs.getInt("quantity");
                                if (currentQuantity > 1) { // Only decrement if quantity is more than 1
                                    String updateSql = "UPDATE cart_items SET quantity = quantity - 1 WHERE user_id=? AND product_id=?";
                                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                        updateStmt.setInt(1, userId);
                                        updateStmt.setInt(2, productId);
                                        updateStmt.executeUpdate();
                                    }
                                } else { // If quantity is 1 and user tries to decrement, remove the item
                                    String deleteSql = "DELETE FROM cart_items WHERE user_id=? AND product_id=?";
                                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                                        deleteStmt.setInt(1, userId);
                                        deleteStmt.setInt(2, productId);
                                        deleteStmt.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                    response.sendRedirect(request.getContextPath() + "/cart"); // Redirect to refresh cart page
                    return; // Stop processing
                } else if ("add".equals(action)) { // This specifically handles add-to-cart from products page
                    // This block expects a 'quantity' parameter
                    String quantityParam = request.getParameter("quantity");
                    int quantity = 0; // Default to 0, or handle as error if not provided for 'add'

                    if (quantityParam != null && !quantityParam.isEmpty()) {
                        quantity = Integer.parseInt(quantityParam);
                    } else {
                        throw new IllegalArgumentException("Quantity is missing for 'add to cart' action.");
                    }

                    // Check if item already in cart
                    String checkSql = "SELECT quantity FROM cart_items WHERE user_id=? AND product_id=?";
                    try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                        checkStmt.setInt(1, userId);
                        checkStmt.setInt(2, productId);
                        try (ResultSet rs = checkStmt.executeQuery()) {
                            if (rs.next()) {
                                // Update quantity if item exists
                                int existingQty = rs.getInt("quantity");
                                String updateSql = "UPDATE cart_items SET quantity=? WHERE user_id=? AND product_id=?";
                                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                    updateStmt.setInt(1, existingQty + quantity);
                                    updateStmt.setInt(2, userId);
                                    updateStmt.setInt(3, productId);
                                    updateStmt.executeUpdate();
                                }
                            } else {
                                // Insert new item if it doesn't exist
                                String insertSql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
                                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                    insertStmt.setInt(1, userId);
                                    insertStmt.setInt(2, productId);
                                    insertStmt.setInt(3, quantity);
                                    insertStmt.executeUpdate();
                                }
                            }
                        }
                    }
                    // Redirect to products page after adding item
                    response.sendRedirect(request.getContextPath() + "/products?cartSuccess=true");
                    return; // Stop processing
                } else {
                    // This 'else' catches any other unknown 'action' values or cases where 'action' is null
                    System.err.println("Unknown action requested: " + action);
                    response.sendRedirect(request.getContextPath() + "/cart?error=unknownAction");
                    return;
                }
            } finally {
                // Ensure connection is closed even if an exception occurs
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("NumberFormatException: Invalid parameter format. " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cart?error=invalidInput");
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: Missing or invalid parameter. " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/cart?error=missingData");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred in doPost: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
            response.sendRedirect(request.getContextPath() + "/cart?error=internalError");
        }
    }

    // Display Cart Page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<CartItem> cartItems = new ArrayList<>();
        String userAddress = null;

        Connection conn = null; // Declare conn here for finally block
        try {
            conn = DatabaseConnection.initializeDatabase();

            // Get cart items
            String sql = "SELECT ci.quantity, p.* FROM cart_items ci JOIN products p ON ci.product_id = p.id WHERE ci.user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql);) { // Use try-with-resources
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery();) { // Use try-with-resources
                    while (rs.next()) {
                        Product p = new Product();
                        p.setId(rs.getInt("ID"));
                        p.setName(rs.getString("NAME"));
                        p.setPrice(rs.getDouble("PRICE"));
                        p.setImagePath(rs.getString("IMAGE_PATH"));
                        p.setCategory(rs.getString("CATEGORY"));

                        int quantity = rs.getInt("quantity");

                        cartItems.add(new CartItem(p, quantity));
                    }
                }
            }

            // Get user address
            String addressSql = "SELECT address FROM users WHERE id = ?";
            try (PreparedStatement addressStmt = conn.prepareStatement(addressSql);) { // Use try-with-resources
                addressStmt.setInt(1, userId);
                try (ResultSet addrRs = addressStmt.executeQuery();) { // Use try-with-resources
                    if (addrRs.next()) {
                        userAddress = addrRs.getString("address");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Optional: Set an error message to display on the JSP
            request.setAttribute("errorMessage", "Error loading cart items: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        // --- NEW: Handle error messages passed via URL parameters ---
        String errorParam = request.getParameter("error");
        if ("unauthorized".equals(errorParam)) {
            request.setAttribute("errorMessage", "Security alert: Unauthorized access attempt. Please log in.");
        } else if ("invalidInput".equals(errorParam)) {
            request.setAttribute("errorMessage", "Invalid data for cart update. Please try again.");
        } else if ("missingData".equals(errorParam)) {
            request.setAttribute("errorMessage", "Missing required information for cart update.");
        } else if ("internalError".equals(errorParam)) {
            request.setAttribute("errorMessage", "An unexpected server error occurred during cart update.");
        } else if ("databaseError".equals(errorParam)) { // Added for more specific DB errors
            request.setAttribute("errorMessage", "A database error occurred during cart update.");
        } else if ("unknownAction".equals(errorParam)) {
            request.setAttribute("errorMessage", "Unknown action requested.");
        }


        // Pass values to JSP
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("userAddress", userAddress);
        request.setAttribute("userId", userId); // Still passing userId to JSP for forms

        // Forward to cart.jsp
        request.getRequestDispatcher("cart.jsp").forward(request, response);
    }
}