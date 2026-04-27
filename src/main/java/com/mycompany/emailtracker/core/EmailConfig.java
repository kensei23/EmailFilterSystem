/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.emailtracker.core;
import java.io.FileInputStream;
import java.util.Properties;
/**
 *
 * @author aaron
 */
public class EmailConfig {
    public static final String HOST = "outlook.office365.com";
    public static final String PROTOCOL = "imaps";
    public static final int PORT = 993;
    public static String CLIENT_ID; 
    public static String AUTHORITY; 
    public static String SCOPE;
    
    static {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            prop.load(fis);
            CLIENT_ID = prop.getProperty("azure.client.id");
            AUTHORITY = prop.getProperty("azure.authority");
            SCOPE = prop.getProperty("azure.scope");
        } catch (Exception e) {
            System.err.println("CRITICAL: config.properties file not found!");
            System.err.println("Please copy config.example.properties to config.properties and add your keys.");
        }
    }
        
    private String username;
    private String password;
    
    
    public EmailConfig(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    
    public String getUsername(){
        return username;
    }
    public String getPassword(){
        return password;
    }
}
