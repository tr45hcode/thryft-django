/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.bean.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.bean.DatabaseConnection;

/**
 *
 * @author muham
 */
@WebServlet("/removeCartItem")
public class removeCartItemServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int productId = Integer.parseInt(request.getParameter("id"));
        String redirectUrl = request.getHeader("referer"); // Gets the previous URL
        
        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            String sql = "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, productId);
                stmt.executeUpdate();
                
                // Redirect back to the same cart page
                response.sendRedirect(redirectUrl != null ? redirectUrl : "viewCart");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Redirect back with error message
            response.sendRedirect(redirectUrl != null ? redirectUrl + "?error=Error removing item" : "viewCart?error=Error removing item");
        }
    }
}
