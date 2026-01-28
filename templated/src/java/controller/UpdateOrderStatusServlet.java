/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dao.OrderDAO;

/**
 *
 * @author tr45h
 */
@WebServlet("/updateOrderStatus")
public class UpdateOrderStatusServlet extends HttpServlet {
    
    private OrderDAO orderDao;

    @Override
    public void init() throws ServletException {
        super.init();
        orderDao = new OrderDAO();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("start here");
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            //System.out.println("debugline"+orderId);
            String status = request.getParameter("status_" + orderId);
            System.out.println(status);
            
            // Update only the status field
            boolean success = orderDao.updateOrderStatus(orderId, status);
            
            if (success) {
                request.getSession().setAttribute("message", "Order status updated to " + status);
            } else {
                request.getSession().setAttribute("error", "Failed to update order status");
            }
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "Invalid order ID");
        } catch (Exception e) {
            request.getSession().setAttribute("error", "Error updating order: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/viewOrders");
    }
}