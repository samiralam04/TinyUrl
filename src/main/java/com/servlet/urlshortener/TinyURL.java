package com.servlet.urlshortener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class TinyURL extends HttpServlet {

    // Base62 character set
    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE = 62;
    private static long counter = 1; // Global counter to increment for each new URL

    // In-memory map to store the short URL mappings (for quick testing without database)
    private static HashMap<String, String> urlMap = new HashMap<>();

    // Handle POST requests for shortening a URL

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get the original URL from the request
            String originalUrl = request.getParameter("url");

            if (originalUrl == null || originalUrl.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL parameter is missing");
                return;
            }

            // Generate the Tiny URL
            String tinyUrl = generateTinyUrl();

            // Store the mapping in-memory (for quick testing)
            urlMap.put(tinyUrl, originalUrl);

            // Return the shortened URL to the client
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h3>Original URL: " + originalUrl + "</h3>");
            out.println("<h3>Shortened URL: <a href='/tiny/" + tinyUrl + "'>/tiny/" + tinyUrl + "</a></h3>");
            out.println("<a href='index.html'>Home</a>");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while generating the Tiny URL.");
        }
    }

    // Handle GET requests for redirecting to the original URL

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Get the tiny URL path from the request (e.g., "/tiny/abcde")
            String tinyUrl = request.getPathInfo();

            // Log the path info for debugging
            System.out.println("Path Info: " + tinyUrl);

            // Check if the tiny URL is provided
            if (tinyUrl == null || tinyUrl.length() <= 1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Tiny URL");
                return;
            }

            // Remove the leading "/" from the tiny URL path
            tinyUrl = tinyUrl.substring(1);

            // Lookup the original URL from the in-memory map
            String originalUrl = urlMap.get(tinyUrl);

            // Redirect to the original URL if found, otherwise return a 404 error
            if (originalUrl != null) {
                response.sendRedirect(originalUrl);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tiny URL not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request.");
        }
    }

    // Method to generate a 5-character Tiny URL
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
