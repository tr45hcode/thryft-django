package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.bean.Product;
import model.bean.DatabaseConnection;


public class editProductServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int productId = Integer.parseInt(request.getParameter("id"));
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("productList?error=Unauthorized access");
            return;
        }
        
        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            String sql = "SELECT * FROM products WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                product.setCategory(rs.getString("category"));
                product.setImagePath(rs.getString("image_path"));
                
                request.setAttribute("product", product);
                request.getRequestDispatcher("editProduct.jsp").forward(request, response);
            } else {
                response.sendRedirect("productList?error=Product not found");
            }
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("productList?error=Database error");
        }
    }
}