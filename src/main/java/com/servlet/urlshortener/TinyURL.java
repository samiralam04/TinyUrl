package com.servlet.urlshortener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.HashMap;

public class TinyURL extends HttpServlet {

    private static final String BASE62 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int BASE = 62;
    private static long counter = 1;

    private static HashMap<String, String> urlMap = new HashMap<>();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String originalUrl = request.getParameter("url");
            if (originalUrl == null || originalUrl.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL parameter is missing");
                return;
            }

            // Generate Tiny URL
            String tinyUrl = generateTinyUrl();
            urlMap.put(tinyUrl, originalUrl);

            // Set attributes to forward data to the JSP
            request.setAttribute("originalUrl", originalUrl);
            request.setAttribute("tinyUrl", tinyUrl);

            // Forward to JSP for rendering
            RequestDispatcher dispatcher = request.getRequestDispatcher("result.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred.");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String tinyUrl = request.getPathInfo();
            if (tinyUrl == null || tinyUrl.length() <= 1) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Tiny URL");
                return;
            }

            tinyUrl = tinyUrl.substring(1);
            String originalUrl = urlMap.get(tinyUrl);

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
