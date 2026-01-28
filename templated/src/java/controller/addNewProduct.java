package controller;

import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import model.bean.*;

@WebServlet("/addProduct")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1MB
    maxFileSize = 1024 * 1024 * 10,     // 10MB
    maxRequestSize = 1024 * 1024 * 50   // 50MB
)
public class addNewProduct extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    // Get the correct base path (use the latter half of the path

    // Process file upload
    String imagePath = null;
    Part filePart = request.getPart("image");
    if (filePart != null && filePart.getSize() > 0) {
        // Create uploads folder outside of WAR
        String uploadDir = getServletContext().getRealPath("/assets/images");
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // Sanitize filename
        String rawFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String safeFileName = rawFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        String fileName = safeFileName;

        // Absolute save path
        String savePath = uploadDir + File.separator + fileName;

        // Write manually to avoid GlassFish confusion
        try (InputStream fileContent = filePart.getInputStream();
             FileOutputStream outStream = new FileOutputStream(new File(savePath))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileContent.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }

        // The path you save in DB for web access (e.g., if your app maps /uploads/** to C:/thryft-uploads)
        imagePath = "assets/images/" + fileName;

    }

    // Rest of your code to save product to database...
    try (Connection conn = DatabaseConnection.initializeDatabase()) {
        String sql = "INSERT INTO products (name, price, category, image_path) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, request.getParameter("name"));
        stmt.setDouble(2, Double.parseDouble(request.getParameter("price")));
        stmt.setString(3, request.getParameter("category"));
        stmt.setString(4, imagePath);
        
        stmt.executeUpdate();
        response.sendRedirect("productList?success=Product added successfully");
    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect("addProduct.jsp?error=Error: " + e.getMessage());
    }
}
}