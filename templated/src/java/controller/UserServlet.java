package controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author tr45h
 */


import model.bean.User;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/profile")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Debug output
        System.out.println("Session ID: " + (session != null ? session.getId() : "null"));
        if (session != null) {
            System.out.println("Session contains userId: " + (session.getAttribute("userId") != null));
            System.out.println("Session contains username: " + (session.getAttribute("username") != null));
        }

        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            System.out.println("User not logged in - redirecting to login.jsp");
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Get user ID from session
            int userId = (Integer) session.getAttribute("userId");
            
            // Get full user details from database
            User currentUser = User.getUserById(userId);
            
            if (currentUser == null) {
                // User doesn't exist in DB anymore
                session.invalidate();
                response.sendRedirect("login.jsp");
                return;
            }

            // Set user data for the JSP
            request.setAttribute("user", currentUser);
            RequestDispatcher dispatcher = request.getRequestDispatcher("user.jsp");
            dispatcher.forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in UserServlet: " + e.getMessage());
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        int userId = (Integer) session.getAttribute("userId");
        
        try {
            if ("update".equals(action)) {
                updateUser(request, response, userId);
            } else if ("delete".equals(action)) {
                deleteUser(request, response, userId);
                session.invalidate();
                response.sendRedirect("index.jsp");
                return;
            }
            response.sendRedirect("profile");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response, int userId) throws Exception {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        
        User user = User.getUserById(userId);
        
        // Verify current password if changing password
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!currentPassword.equals(user.getPassword())) {
                request.setAttribute("error", "Current password is incorrect");
                doGet(request, response);
                return;
            }
        }
        
        user.setUsername(username);
        user.setEmail(email);
        user.setAddress(address);
        
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }
        
        user.update();
        
        // Update session username if it was changed
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("username", username);
        }
        
        request.setAttribute("success", "Profile updated successfully");
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response, int userId) throws Exception {
        User.deleteUser(userId);
    }
}