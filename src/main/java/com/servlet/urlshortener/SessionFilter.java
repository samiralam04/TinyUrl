package com.servlet.urlshortener;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class SessionFilter implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the current session
        HttpSession session = httpRequest.getSession(false);

        // Check if the request is for the login page or if there's a session
        String requestURI = httpRequest.getRequestURI();
        boolean isLoginPage = requestURI.endsWith("login.html");

        // If no session exists and the request is not for the login page, redirect to login page
        if (session == null || session.getAttribute("username") == null) {
            if (!isLoginPage) {
                httpResponse.sendRedirect("login.html"); // Redirect to login if not on login page
            } else {
                chain.doFilter(request, response); // Allow access to login page
            }
        } else {
            chain.doFilter(request, response); // Allow request to proceed if session exists
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if needed
    }

    public void destroy() {
        // Cleanup code, if needed
    }
}
