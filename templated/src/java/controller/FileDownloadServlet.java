/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.File; // ✅ This one
import java.io.PrintWriter;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.bean.DatabaseConnection;

import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
/**
 *
 * @author tr45h
 */
public class FileDownloadServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    
    // Use ONLY the correct base path (the latter half from your error message)
    String correctBasePath = "C:/Users/xvent/OneDrive/Documents/NetBeansProjects/thryft-main/build/web";
    
    // Create uploads directory path
    String uploadDir = correctBasePath + "/uploads";
    File uploadFolder = new File(uploadDir);
    
    // Create directory if it doesn't exist
    if (!uploadFolder.exists()) {
        boolean created = uploadFolder.mkdirs();
        if (!created) {
            response.sendRedirect("addProduct.jsp?error=Could not create upload directory");
            return;
        }
    }

    // Process file upload
    Part filePart = request.getPart("image");
    if (filePart == null || filePart.getSize() == 0) {
        response.sendRedirect("addProduct.jsp?error=No file uploaded");
        return;
    }

    try {
        // Sanitize filename
        String fileName = System.currentTimeMillis() + "_" + 
                        filePart.getSubmittedFileName()
                               .replace(" ", "_")
                               .replaceAll("[^a-zA-Z0-9._-]", "");
        
        // Complete save path
        String savePath = uploadDir + "/" + fileName;
        
        // Debug output
        System.out.println("Attempting to save to: " + savePath);
        
        // Write the file
        filePart.write(savePath);
        
        // Verify file was created
        File savedFile = new File(savePath);
        if (!savedFile.exists()) {
            response.sendRedirect("addProduct.jsp?error=File failed to save");
            return;
        }

        // Web-accessible path (relative to web root)
        String imagePath = "uploads/" + fileName;

        // Save to database
        try (Connection conn = DatabaseConnection.initializeDatabase()) {
            String sql = "INSERT INTO products (name, price, category, image_path) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, request.getParameter("name"));
            stmt.setDouble(2, Double.parseDouble(request.getParameter("price")));
            stmt.setString(3, request.getParameter("category"));
            stmt.setString(4, imagePath);
            
            stmt.executeUpdate();
            response.sendRedirect("productList?success=Product added successfully");
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect("addProduct.jsp?error=Error: " + e.getMessage());
    }
}
}