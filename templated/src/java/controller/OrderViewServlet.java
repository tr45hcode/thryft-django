package controller;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.bean.Order;
import model.bean.DatabaseConnection;

@WebServlet("/viewOrders")
public class OrderViewServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify admin privileges
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("login.jsp?error=Unauthorized access");
            return;
        }

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            String sql = "SELECT o.*, p.name as product_name, p.image_path as product_image, " +
                         "p.price as product_price, u.username, u.email " +
                         "FROM orders o " +
                         "JOIN products p ON o.product_id = p.id " +
                         "JOIN users u ON o.user_id = u.id " +
                         "ORDER BY o.created_at DESC";
            
            List<Order> orders = new ArrayList<>();
            
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
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
                    
                    orders.add(order);
                }
            }
            
            request.setAttribute("orders", orders);
            request.getRequestDispatcher("orderList.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin.jsp?error=Database error: " + e.getMessage());
        }
    }
}