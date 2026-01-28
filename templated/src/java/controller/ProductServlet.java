/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.bean.Product;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author muham
 */
@WebServlet("/products")
public class ProductServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ArrayList<Product> allProducts = new ArrayList<>();

        try {
            Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Thryft", "app", "app");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCTS");

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("ID"));
                p.setName(rs.getString("NAME"));
                p.setPrice(rs.getDouble("PRICE"));
                p.setImagePath(rs.getString("IMAGE_PATH"));
                p.setCategory(rs.getString("CATEGORY"));
                allProducts.add(p);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("allProducts", allProducts);
        request.getRequestDispatcher("products.jsp").forward(request, response);
    }
}

