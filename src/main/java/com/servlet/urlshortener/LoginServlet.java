package com.servlet.urlshortener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the form parameters
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Establish a connection to the database
        try {
            // Load PostgreSQL JDBC Driver
            Class.forName("org.postgresql.Driver");

            Connection conn = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/urlshortener", "postgres", "mark47");

            // Query to check if the user exists
            String query = "SELECT * FROM urlUser WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);  // In production, compare hashed passwords

            ResultSet rs = stmt.executeQuery();

            // Response to the user
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            if (rs.next()) {
                // User authenticated, create a session
                HttpSession session = request.getSession();
                session.setAttribute("username", username);

                // Send JavaScript for success popup and redirection
                out.println("<script type='text/javascript'>");
                out.println("alert('Login successful! Redirecting to URL Shortener...');");
                out.println("window.location.href = '/index.html';");
                out.println("</script>");
            } else {
                // Send JavaScript for failure popup and redirection
                out.println("<script type='text/javascript'>");
                out.println("alert('Invalid username or password. Please try again.');");
                out.println("window.location.href = 'login.html';");
                out.println("</script>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during login.");
        }
    }
}
