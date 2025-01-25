package com.servlet.urlshortener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/urlshortener";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "mark47";

    // Load PostgreSQL JDBC Driver once when the class is loaded
    static {
        try {
            // Load the PostgreSQL driver class
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
        }
    }

    // Method to get a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Error while establishing connection to the database", e);
        }
    }
}
