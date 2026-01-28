package model.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.bean.Product;

public class ProductDAO {
    public static List<Product> getMenProducts() {
        List<Product> productList = new ArrayList<>();

        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            Connection conn = DriverManager.getConnection("jdbc:derby://localhost:1527/Thryft", "app", "app");

            String query = "SELECT * FROM products WHERE category = 'men'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("image_path"),
                    rs.getString("category")
                );
                productList.add(p);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productList;
    }
}
