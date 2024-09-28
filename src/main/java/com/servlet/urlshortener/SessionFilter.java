package com.servlet.urlshortener;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class SessionFilter implements Filter {

    public void init(FilterConfig config) throws ServletException {
        // Initialization code if needed
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the current session (do not create a new one if it doesn't exist)
        HttpSession session = httpRequest.getSession(false);

        // If no session exists or the user is not logged in, redirect to login page
        if (session == null || session.getAttribute("username") == null) {
            httpResponse.sendRedirect("login.html");
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {
        // Cleanup code if needed
    }
}
