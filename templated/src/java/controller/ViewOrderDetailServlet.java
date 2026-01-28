package controller;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.bean.Order;
import model.bean.DatabaseConnection;

@WebServlet("/viewOrderDetails")
public class ViewOrderDetailServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify admin privileges
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("login.jsp?error=Unauthorized access");
            return;
        }

        int orderId = Integer.parseInt(request.getParameter("id"));
        
        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            String sql = "SELECT o.*, p.name as product_name, p.image_path as product_image, " +
                         "p.price as product_price, p.description as product_description, " +
                         "u.username, u.email, u.id as user_id " +
                         "FROM orders o " +
                         "JOIN products p ON o.product_id = p.id " +
                         "JOIN users u ON o.user_id = u.id " +
                         "WHERE o.id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, orderId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Order order = new Order();
                        order.setId(rs.getInt("id"));
                        order.setBillCode(rs.getString("bill_code"));
                        order.setUserId(rs.getInt("user_id"));
                        order.setProductId(rs.getInt("product_id"));
                        order.setQuantity(rs.getInt("quantity"));
                        order.setTotalPrice(rs.getDouble("total_price"));
                        order.setStatus(rs.getString("status"));
                        order.setCreatedAt(rs.getTimestamp("created_at"));
                        
                        // Product details
                        order.setProductName(rs.getString("product_name"));
                        order.setProductImage(rs.getString("product_image"));
                        order.setProductPrice(rs.getDouble("product_price"));
                        
                        // User details
                        order.setUsername(rs.getString("username"));
                        order.setUserEmail(rs.getString("email"));
                        
                        request.setAttribute("order", order);
                        request.getRequestDispatcher("orderDetails.jsp").forward(request, response);
                    } else {
                        response.sendRedirect("viewOrders?error=Order not found");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("viewOrders?error=Error viewing order: " + e.getMessage());
        }
    }
}