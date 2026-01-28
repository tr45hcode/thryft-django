package model.bean;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private boolean admin;
    private String address;

    // Constructors
    public User() {}

    public User(String username, String email, String password, String address) {
        this.username = username;
        this.email = email;
        this.password = password;  // No hashing
        this.admin = false;
        this.address = address;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { 
        this.password = password;  // No hashing
    }
    
    public boolean isAdmin() { return admin; }
    public void setAdmin(boolean admin) { this.admin = admin; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // Database operations
    public boolean save() {
        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            String sql = "INSERT INTO users (username, email, password, admin, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setBoolean(4, admin);
            stmt.setString(5, address);

            int rowsInserted = stmt.executeUpdate();
            conn.close();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update() {
        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            String sql = "UPDATE users SET username = ?, email = ?, password = ?, admin = ?, address = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setBoolean(4, admin);
            stmt.setString(5, address);
            stmt.setInt(6, id);

            int rowsUpdated = stmt.executeUpdate();
            conn.close();
            return rowsUpdated > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteUser(int id) {
        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            String sql = "DELETE FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            conn.close();
            return rowsDeleted > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User getUserById(int id) {
        User user = null;
        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAdmin(rs.getBoolean("admin"));
                user.setAddress(rs.getString("address"));
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User getUserByUsername(String username) {
        User user = null;
        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAdmin(rs.getBoolean("admin"));
                user.setAddress(rs.getString("address"));
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.initializeDatabase();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, username, email, admin, address FROM users");

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setAdmin(rs.getBoolean("admin"));
                user.setAddress(rs.getString("address"));
                users.add(user);
            }
            
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

    public static User login(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {  // Direct string comparison
            return user;
        }
        return null;
    }
}