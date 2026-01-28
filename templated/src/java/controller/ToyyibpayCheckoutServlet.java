package controller;

import model.bean.DatabaseConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

@WebServlet("/checkout")
public class ToyyibpayCheckoutServlet extends HttpServlet {

    private static final String TOYYIBPAY_SANDBOX_URL = "https://dev.toyyibpay.com/index.php/api/createBill";
    private static final String SANDBOX_CATEGORY_CODE = "w407vxao";
    private static final String SANDBOX_SECRET_KEY = "aly5tpgl-jbug-0vvx-21li-xr88qezxyhtu";

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");

        if (userId == null || username == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String amountParam = request.getParameter("amount");
        if (amountParam == null || !amountParam.matches("\\d+")) {
            response.sendError(400, "Invalid or missing amount parameter.");
            return;
        }

        String userEmail = null;

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            PreparedStatement userStmt = conn.prepareStatement("SELECT email FROM users WHERE id = ?");
            userStmt.setInt(1, userId);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                userEmail = userRs.getString("email");
            }
            userRs.close();
            userStmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("cart.jsp");
            return;
        }

        Map<String, String> data = new LinkedHashMap<>();
        data.put("userSecretKey", SANDBOX_SECRET_KEY);
        data.put("categoryCode", SANDBOX_CATEGORY_CODE);
        data.put("billName", "HexaShop Order");
        data.put("billDescription", "Order from HexaShop e-commerce system");
        data.put("billPriceSetting", "1");
        data.put("billPayorInfo", "1");
        data.put("billAmount", amountParam);
        data.put("billReturnUrl", "https://22c28c0127f0.ngrok-free.app/Thryft/index");
        data.put("billCallbackUrl", "https://22c28c0127f0.ngrok-free.app/Thryft/paymentCallback");
        String externalRef = "UID-" + userId + "-" + System.currentTimeMillis();
        data.put("billExternalReferenceNo", externalRef); 
        data.put("billTo", username);
        data.put("billEmail", userEmail != null ? userEmail : "unknown@example.com");
        data.put("billPhone", "0123456789");
        data.put("billPaymentChannel", "0");

        StringJoiner sj = new StringJoiner("&");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        System.out.println("===== Toyyibpay DEBUG POST =====");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        System.out.println("Encoded Body: " + sj.toString());
        System.out.println("================================");

        URL url = new URL(TOYYIBPAY_SANDBOX_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        try (OutputStream os = conn.getOutputStream()) {
            os.write(sj.toString().getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int status = conn.getResponseCode();
        InputStream responseStream = (status < 400) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder responseStr = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(responseStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                responseStr.append(line);
            }
        }

        System.out.println("Toyyibpay Response Code: " + status);
        System.out.println("Toyyibpay Raw Response: " + responseStr.toString());

        String raw = responseStr.toString();
        String billCode = null;
        int index = raw.indexOf("BillCode\":\"");
        if (index != -1) {
            int start = index + 11;
            int end = raw.indexOf("\"", start);
            billCode = raw.substring(start, end);
        }

        if (billCode != null) {
            try (Connection conn2 = DatabaseConnection.initializeDatabase()) {
                String insertPending = "INSERT INTO pending_payments (bill_code, user_id) VALUES (?, ?)";
                PreparedStatement insertStmt = conn2.prepareStatement(insertPending);
                insertStmt.setString(1, billCode);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();
                insertStmt.close();
            } catch (Exception e) {
                e.printStackTrace(); // Optional: Log this somewhere
            }

            session.setAttribute("lastBillCode", billCode);
            response.sendRedirect("https://dev.toyyibpay.com/" + billCode);
        } else {
            response.getWriter().println("Failed to get bill code. Raw Response: " + raw);
        }
    }
}
