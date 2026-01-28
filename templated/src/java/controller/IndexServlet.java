package controller;

import model.bean.Product;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ArrayList<Product> menProducts = new ArrayList<>();
        ArrayList<Product> womenProducts = new ArrayList<>();

        try {
            // Connect to Derby
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/Thryft", "app", "app");

            Statement stmt = conn.createStatement();

            // ✅ Query men products
            ResultSet rsMen = stmt.executeQuery("SELECT * FROM PRODUCTS WHERE LOWER(CATEGORY) = 'men'");
            while (rsMen.next()) {
                Product p = new Product();
                p.setId(rsMen.getInt("ID"));
                p.setName(rsMen.getString("NAME"));
                p.setPrice(rsMen.getDouble("PRICE"));
                p.setImagePath(rsMen.getString("IMAGE_PATH"));
                p.setCategory(rsMen.getString("CATEGORY"));

                menProducts.add(p);
                System.out.println("[DEBUG] MEN: " + p.getName());
            }

            // ✅ Query women products
            ResultSet rsWomen = stmt.executeQuery("SELECT * FROM PRODUCTS WHERE LOWER(CATEGORY) = 'women'");
            while (rsWomen.next()) {
                Product p = new Product();
                p.setId(rsWomen.getInt("ID"));
                p.setName(rsWomen.getString("NAME"));
                p.setPrice(rsWomen.getDouble("PRICE"));
                p.setImagePath(rsWomen.getString("IMAGE_PATH"));
                p.setCategory(rsWomen.getString("CATEGORY"));

                womenProducts.add(p);
                System.out.println("[DEBUG] WOMEN: " + p.getName());
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Pass both lists to JSP
        request.setAttribute("menProducts", menProducts);
        request.setAttribute("womenProducts", womenProducts);
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
