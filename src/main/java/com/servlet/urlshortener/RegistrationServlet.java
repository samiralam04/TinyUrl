package com.servlet.urlshortener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class RegistrationServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/urlshortener", "postgres", "mark47");

            // Check if username already exists
            String checkQuery = "SELECT 1 FROM users WHERE username = ?";
            checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Username exists, show popup
                out.println("<script type='text/javascript'>");
                out.println("alert('Username already exists! Please choose a different one.');");
                out.println("window.location.href = 'register.html';");
                out.println("</script>");
            } else {
                // Insert new user
                String insertQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
                insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, username);
                insertStmt.setString(2, password); // Hash password in production

                int rowsInserted = insertStmt.executeUpdate();

                if (rowsInserted > 0) {
                    out.println("<script type='text/javascript'>");
                    out.println("alert('Registration successful! Redirecting to login page...');");
                    out.println("window.location.href = '/URL-shortener/login.html';");
                    out.println("</script>");
                } else {
                    out.println("<script type='text/javascript'>");
                    out.println("alert('Registration failed. Please try again.');");
                    out.println("window.location.href = 'register.html';");
                    out.println("</script>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.println("<script type='text/javascript'>");
            out.println("alert('An error occurred: " + e.getMessage().replace("'", "\\'") + "');");
            out.println("window.location.href = 'register.html';");
            out.println("</script>");
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
