package com.example.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:oracle:thin:@localhost:1521/orcl";
    private static final String DB_USER = "c##scott";
    private static final String DB_PASSWORD = "tiger";
    static {
        try{
            // Load the Oracle JDBC driver
            Class.forName("oracle.jdbc.OracleDriver");
        }
        catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC Driver not found");
            e.printStackTrace();
        }
    }
    /**
     * Establishes a connection to the Oracle database.
     * @return A valid Connection object.
     * @throws SQLException If a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    /**
     * Closes the given database connection.
     * @param connection The Connection object to close.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
