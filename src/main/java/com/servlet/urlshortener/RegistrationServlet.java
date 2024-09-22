package com.servlet.urlshortener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegistrationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the form parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Establish a connection to the database
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/urlshortener", "postgres", "mark47");

            // Insert the user into the database
            String query = "INSERT INTO urlUser (username, password) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);  // In production, hash the password for security

            int rowsInserted = stmt.executeUpdate();

            // Respond to the user
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            if (rowsInserted > 0) {
                // Show success popup and redirect to login
                out.println("<script type='text/javascript'>");
                out.println("alert('Registration successful! Redirecting to login page...');");
                out.println("window.location.href = 'login.html';");
                out.println("</script>");
            } else {
                // Show failure message
                out.println("<script type='text/javascript'>");
                out.println("alert('Registration failed. Please try again.');");
                out.println("window.location.href = 'register.html';");
                out.println("</script>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during registration.");
        }
    }
}
