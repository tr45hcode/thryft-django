package controller;

import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import model.bean.User;
import model.bean.Product;
import model.bean.CartItem; // ✅ You must create this class
import model.bean.DatabaseConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Connection conn = DatabaseConnection.initializeDatabase();

            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // (Add hashing later)
            
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id"); // get user ID from result set
                boolean isAdmin = rs.getBoolean("admin"); // Direct boolean check

                HttpSession session = request.getSession();
                session.setAttribute("userId", userId);
                session.setAttribute("username", rs.getString("username"));
                session.setAttribute("isAdmin", isAdmin);

                if (isAdmin) {
                    response.sendRedirect("admin.html");
                    return; // Stop further execution for admins
                }

                // ✅ Load user cart from DB
                Map<Integer, CartItem> cart = new HashMap<>();

                String cartSql = "SELECT * FROM cart_items WHERE user_id = ?";
                PreparedStatement cartStmt = conn.prepareStatement(cartSql);
                cartStmt.setInt(1, userId);
                ResultSet cartRs = cartStmt.executeQuery();

                while (cartRs.next()) {
                    int productId = cartRs.getInt("product_id");
                    int quantity = cartRs.getInt("quantity");

                    // Get product info
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM products WHERE id = ?");
                    ps.setInt(1, productId);
                    ResultSet prs = ps.executeQuery();
                    if (prs.next()) {
                        Product p = new Product();
                        p.setId(prs.getInt("ID"));
                        p.setName(prs.getString("NAME"));
                        p.setPrice(prs.getDouble("PRICE"));
                        p.setImagePath(prs.getString("IMAGE_PATH"));
                        p.setCategory(prs.getString("CATEGORY"));

                        CartItem item = new CartItem(p, quantity);
                        cart.put(productId, item);
                    }
                    prs.close();
                    ps.close();
                }

                session.setAttribute("cart", cart);

                response.sendRedirect("index"); // forward to index servlet
            } else {
                request.setAttribute("error", "Invalid username or password.");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }

            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
