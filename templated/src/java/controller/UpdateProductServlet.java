package controller;

import java.io.*;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.bean.DatabaseConnection;

@WebServlet("/updateProduct")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
public class UpdateProductServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int productId = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        double price = Double.parseDouble(request.getParameter("price"));
        String category = request.getParameter("category");
        Part filePart = request.getPart("image");

        String imagePath = null;

        // Check if a new file was uploaded
        if (filePart != null && filePart.getSize() > 0) {
            // Correct path to deployed assets/images
            String uploadDir = getServletContext().getRealPath("/assets/images");
            System.out.println("debugLine "+uploadDir);
            File uploadFolder = new File(uploadDir);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // Sanitize filename
            String rawFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String safeFileName = rawFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
            String fileName = System.currentTimeMillis() + "_" + safeFileName;

            // Absolute save path
            String savePath = uploadDir + File.separator + fileName;

            // Write manually to avoid GlassFish issues
            try (InputStream fileContent = filePart.getInputStream();
                 FileOutputStream outStream = new FileOutputStream(new File(savePath))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileContent.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }

            // Path to store in DB
            imagePath = "assets/images/" + fileName;
        }

        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            String sql;
            PreparedStatement stmt;

            if (imagePath != null) {
                // Update all fields including image
                sql = "UPDATE products SET name=?, price=?, category=?, image_path=? WHERE id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setString(3, category);
                stmt.setString(4, imagePath);
                stmt.setInt(5, productId);
            } else {
                // Update only fields without image
                sql = "UPDATE products SET name=?, price=?, category=? WHERE id=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setString(3, category);
                stmt.setInt(4, productId);
            }

            int rowsAffected = stmt.executeUpdate();
            stmt.close();

            if (rowsAffected > 0) {
                response.sendRedirect("productList?success=Product updated successfully");
            } else {
                response.sendRedirect("productList?error=Failed to update product");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("productList?error=Database error");
        }
    }
}
