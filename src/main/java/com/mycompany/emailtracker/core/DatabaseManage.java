/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.emailtracker.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 *
 * @author aaron
 */
public class DatabaseManage {
    // Creates the db file inside project folder
    private static final String DB_URL = "jdbc:sqlite:emailtracker.db";
    
    // Connects to the database
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    // Method to build tables if this is the first use of app
    public static void initialiseDatabase(){
        
        // SQL command
        String createSettingsTable = "CREATE TABLE IF NOT EXISTS settings("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "email_address TEXT NOT NULL,"
                + "app_password TEXT NOT NULL"
                + ");";
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            
            // Execute the SQL statement
            stmt.execute(createSettingsTable);
            System.out.println("SQLite Database connected and ready to go!");
            
        } catch (SQLException e) {
            System.out.println("Failed to build database: " + e.getMessage());
        }
    }
    
    public static void saveCredentials(String email, String password){
        // Deletes previous credentials
        String clearSQL = "DELETE FROM settings";
        String insertSQL = "INSERT INTO settings(email_address, app_password) VALUES(?, ?)";
        
        try (Connection conn = connect();
             Statement clearStmt = conn.createStatement();
             PreparedStatement insertStmt = conn.prepareStatement(insertSQL)){
            
            // Wipe old data
            clearStmt.execute(clearSQL);
            
            // Insert new email + password
            insertStmt.setString(1, email);
            insertStmt.setString(2, password);
            insertStmt.executeUpdate();
            
            System.out.println("Information saved to database");
            
        } catch(SQLException e){
            System.out.println("Error saving credentials: " + e.getMessage());
        }
    }
    
    public static String[] getCredentials() {
        String selectSQL = "SELECT email_address, app_password FROM settings LIMIT 1";
        
        try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectSQL)) {
            
            // If row is found, return array with info
            if(rs.next()){
                String email = rs.getString("email_address");
                String password = rs.getString("app_password");
                return new String[]{email, password};
            }
        } catch(SQLException e){
            System.out.println("Error loading information: " + e.getMessage());
        }
        
        // Return null if DB is empty
        return null;
    }
}
