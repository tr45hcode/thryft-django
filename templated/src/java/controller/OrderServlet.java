package controller;

import model.bean.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        List<Map<String, Object>> orders = new ArrayList<>();

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            String sql = "SELECT o.id, o.quantity, o.total_price, o.status, o.created_at, p.name AS product_name " +
                         "FROM orders o JOIN products p ON o.product_id = p.id WHERE o.user_id = ? ORDER BY o.created_at DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("id", rs.getInt("id"));
                order.put("product_name", rs.getString("product_name"));
                order.put("quantity", rs.getInt("quantity"));
                order.put("total_price", rs.getDouble("total_price"));
                order.put("status", rs.getString("status"));
                order.put("created_at", rs.getTimestamp("created_at"));
                orders.add(order);
            }

            stmt.close();
            rs.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("orders", orders);
        request.getRequestDispatcher("orders.jsp").forward(request, response);
    }
}
