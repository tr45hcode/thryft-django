package controller;

import model.bean.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

@WebServlet("/paymentCallback")
@MultipartConfig
public class PaymentCallbackServlet extends HttpServlet {

    private Integer getUserIdFromBillCode(String billCode) {
        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            String sql = "SELECT user_id FROM pending_payments WHERE bill_code = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, billCode);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== Toyyibpay Callback Triggered ===");

        Map<String, String> formData = new HashMap<>();

        // Parse multipart form data
        for (Part part : request.getParts()) {
            String fieldName = part.getName();
            InputStream is = part.getInputStream();
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String value = s.hasNext() ? s.next() : "";
            formData.put(fieldName, value);
            System.out.println(fieldName + ": " + value);
        }

        String billCode = formData.get("billcode");
        String status = formData.get("status");

        System.out.println("Parsed billCode: " + billCode);
        System.out.println("Parsed status: " + status);

        if ("1".equals(status) && billCode != null) {
            Integer userId = getUserIdFromBillCode(billCode);
            System.out.println("Resolved userId from DB: " + userId);

            if (userId != null) {
                try (Connection conn = DatabaseConnection.initializeDatabase()) {

                    String sql = "SELECT product_id, quantity FROM cart_items WHERE user_id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        int productId = rs.getInt("product_id");
                        int qty = rs.getInt("quantity");

                        PreparedStatement prodStmt = conn.prepareStatement("SELECT price FROM products WHERE id = ?");
                        prodStmt.setInt(1, productId);
                        ResultSet prodRs = prodStmt.executeQuery();

                        if (prodRs.next()) {
                            double price = prodRs.getDouble("price");
                            double totalPrice = price * qty;

                            PreparedStatement insertOrder = conn.prepareStatement(
                                "INSERT INTO orders (bill_code, user_id, product_id, quantity, total_price, status) VALUES (?, ?, ?, ?, ?, ?)"
                            );
                            insertOrder.setString(1, billCode);
                            insertOrder.setInt(2, userId);
                            insertOrder.setInt(3, productId);
                            insertOrder.setInt(4, qty);
                            insertOrder.setDouble(5, totalPrice);
                            insertOrder.setString(6, "Paid");
                            insertOrder.executeUpdate();
                            insertOrder.close();
                        }

                        prodRs.close();
                        prodStmt.close();
                    }

                    rs.close();
                    stmt.close();

                    PreparedStatement deleteCart = conn.prepareStatement("DELETE FROM cart_items WHERE user_id = ?");
                    deleteCart.setInt(1, userId);
                    deleteCart.executeUpdate();
                    deleteCart.close();

                    PreparedStatement deletePending = conn.prepareStatement("DELETE FROM pending_payments WHERE bill_code = ?");
                    deletePending.setString(1, billCode);
                    deletePending.executeUpdate();
                    deletePending.close();

                    System.out.println("✅ Orders added and cart cleared for user " + userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("❌ User not found for billcode: " + billCode);
            }
        } else {
            System.out.println("❌ Callback failed or status != 1");
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
