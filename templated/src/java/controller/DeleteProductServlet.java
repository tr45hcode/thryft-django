package controller;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.bean.DatabaseConnection;

@WebServlet("/deleteProduct")
public class DeleteProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify admin privileges
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("productList?error=Unauthorized access");
            return;
        }

        int productId = Integer.parseInt(request.getParameter("id"));
        
        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            conn.setAutoCommit(false); // Start transaction
            
            try {
                // 1. Remove from all carts first
                try (PreparedStatement cartStmt = conn.prepareStatement(
                        "DELETE FROM cart_items WHERE product_id = ?")) {
                    cartStmt.setInt(1, productId);
                    cartStmt.executeUpdate();
                }

                // 2. Delete product images from filesystem (optional)
                String imagePath = null;
                try (PreparedStatement selectStmt = conn.prepareStatement(
                        "SELECT image_path FROM products WHERE id = ?")) {
                    selectStmt.setInt(1, productId);
                    ResultSet rs = selectStmt.executeQuery();
                    if (rs.next()) {
                        imagePath = rs.getString("image_path");
                    }
                }

                // 3. Delete the product
                try (PreparedStatement productStmt = conn.prepareStatement(
                        "DELETE FROM products WHERE id = ?")) {
                    productStmt.setInt(1, productId);
                    int rowsAffected = productStmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        // 4. Delete actual image file (optional)
                        if (imagePath != null && !imagePath.isEmpty()) {
                            String fullPath = getServletContext().getRealPath(imagePath);
                            System.out.println("debugline "+fullPath);
                            new java.io.File(fullPath).delete();
                        }
                        
                        conn.commit();
                        response.sendRedirect("productList?success=Product deleted successfully");
                    } else {
                        conn.rollback();
                        response.sendRedirect("productList?error=Product not found");
                    }
                }
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("productList?error=Error deleting product: " + e.getMessage());
        }
    }
}