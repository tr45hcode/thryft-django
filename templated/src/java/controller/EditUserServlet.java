package controller;

import java.io.IOException;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.bean.User;
import model.bean.DatabaseConnection;

@WebServlet("/editUser")
public class EditUserServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify admin privileges
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("login.jsp?error=Unauthorized access");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("id"));
        
        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            String sql = "SELECT id, username, email, password,admin FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAdmin(rs.getBoolean("admin"));
                
                request.setAttribute("user", user);
                request.getRequestDispatcher("editUser.jsp").forward(request, response);
            } else {
                response.sendRedirect("userList?error=User not found");
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("userList?error=Database error");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Verify admin privileges
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("login.jsp?error=Unauthorized access");
            return;
        }

        int userId = Integer.parseInt(request.getParameter("id"));
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        boolean isAdminUpdated = request.getParameter("admin") != null;

        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            // Fixed SQL - added comma after password
            String sql = "UPDATE users SET username = ?, email = ?, password = ?, admin = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Set parameters in the correct order
            stmt.setString(1, username);       // username
            stmt.setString(2, email);          // email
            stmt.setString(3, password);       // password
            stmt.setBoolean(4, isAdminUpdated); // admin
            stmt.setInt(5, userId);            // id

            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            conn.close();

            if (rowsAffected > 0) {
                response.sendRedirect("userList?success=User updated successfully");
            } else {
                response.sendRedirect("userList?error=Failed to update user");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("userList?error=Error updating user: " + e.getMessage());
        }
    }
}