package com.servlet.urlshortener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import com.servlet.urlshortener.DBUtil;

import java.sql.*;

public class TinyURL extends HttpServlet {

    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE = 62;
    private static long counter = 1;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection connection = DBUtil.getConnection()) {
            String originalUrl = request.getParameter("url");
            if (originalUrl == null || originalUrl.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL parameter is missing");
                return;
            }

            // Check if URL already exists
            String tinyUrl = null;
            String checkQuery = "SELECT tiny_url FROM url_mapping WHERE original_url = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, originalUrl);
                try (ResultSet resultSet = checkStmt.executeQuery()) {
                    if (resultSet.next()) {
                        tinyUrl = resultSet.getString("tiny_url");
                    }
                }
            }

            // Generate new tiny URL if not found
            if (tinyUrl == null) {
                tinyUrl = generateTinyUrl();
                String insertQuery = "INSERT INTO url_mapping (original_url, tiny_url) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, originalUrl);
                    insertStmt.setString(2, tinyUrl);
                    insertStmt.executeUpdate();
                }
            }

            // Forward to JSP for rendering
            request.setAttribute("originalUrl", originalUrl);
            request.setAttribute("tinyUrl", tinyUrl);
            RequestDispatcher dispatcher = request.getRequestDispatcher("result.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection connection = DBUtil.getConnection()) {
            String tinyUrl = request.getPathInfo();
            if (tinyUrl == null || tinyUrl.length() <= 1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Tiny URL");
                return;
            }

            tinyUrl = tinyUrl.substring(1); // Remove leading '/'
            String originalUrl = null;
            String query = "SELECT original_url FROM url_mapping WHERE tiny_url = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, tinyUrl);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        originalUrl = resultSet.getString("original_url");
                    }
                }
            }

            if (originalUrl != null) {
                response.sendRedirect(originalUrl);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tiny URL not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred.");
        }
    }

    private String generateTinyUrl() {
        StringBuilder tinyUrl = new StringBuilder();
        long tempCounter = counter++;
        for (int i = 0; i < 5; i++) {
            tinyUrl.insert(0, BASE62.charAt((int) (tempCounter % BASE)));
            tempCounter /= BASE;
        }
        return tinyUrl.toString();
    }
}
