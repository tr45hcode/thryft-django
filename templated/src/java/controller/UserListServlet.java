package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import model.bean.User;

@WebServlet("/userList")
public class UserListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Verify admin privileges
        Boolean isAdmin = (Boolean) request.getSession().getAttribute("isAdmin");
        if (isAdmin == null || !isAdmin) {
            response.sendRedirect("login.jsp?error=Unauthorized access");
            return;
        }

        // Get all users using the model
        List<User> users = User.getAllUsers();
        request.setAttribute("users", users);
        request.getRequestDispatcher("userList.jsp").forward(request, response);
    }
}