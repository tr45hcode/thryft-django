package controller;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.bean.DatabaseConnection;

@WebServlet("/deleteUser")
public class DeleteUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify admin privileges
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("login.jsp?error=Unauthorized access");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("id"));
        
        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            conn.setAutoCommit(false); // Start transaction
            
            try {
                // 1. First delete from cart_items to maintain referential integrity
                try (PreparedStatement cartStmt = conn.prepareStatement(
                        "DELETE FROM cart_items WHERE user_id = ?")) {
                    cartStmt.setInt(1, userId);
                    cartStmt.executeUpdate();
                }

                // 2. Then delete the user
                try (PreparedStatement userStmt = conn.prepareStatement(
                        "DELETE FROM users WHERE id = ?")) {
                    userStmt.setInt(1, userId);
                    int rowsAffected = userStmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        conn.commit();
                        response.sendRedirect("userList?success=User deleted successfully");
                    } else {
                        conn.rollback();
                        response.sendRedirect("userList?error=User not found");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("userList?error=Error deleting user: " + e.getMessage());
        }
    }
}