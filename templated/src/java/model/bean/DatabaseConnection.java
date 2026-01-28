package model.bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection initializeDatabase() throws SQLException, ClassNotFoundException {
        // Example for MySQL – adjust for your DB
        String dbDriver = "org.apache.derby.jdbc.ClientDriver";
        String dbURL = "jdbc:derby://localhost:1527/Thryft;create=true"; 
        String dbUsername = "app"; // default for Derby
        String dbPassword = "app"; // default for Derby

        Class.forName(dbDriver);
        return DriverManager.getConnection(dbURL, dbUsername, dbPassword);
    }
}
