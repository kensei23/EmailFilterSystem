/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.emailtracker.core;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author aaron
 */
public class MLClient {
    
    // Instance Variables
    private static final String API_URL = "http://127.0.0.1:8000/predict";
    private static final HttpClient client = HttpClient.newHttpClient();
    // Using Gson to translate what Java outputs into a JSON the Python client can read
    private static final Gson gson = new Gson();
    
    public static String categoriseEmail(String subject, String body){
        try{
            // Putting email contents into a Map
            Map<String, String> emailData = new HashMap<>();
            emailData.put("subject", subject != null ? subject : "");
            emailData.put("body", body != null ? body : "");
            
            // Converting the Map to a JSON 
            String jsonPayLoad = gson.toJson(emailData);
            
            //  Building a POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayLoad))
                    .build();
            
            // Send to Python, wait for response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Read Python response
            if (response.statusCode() == 200){
                JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
                return jsonResponse.get("category").getAsString();
            } else {
                System.out.println("Error from ML Server: " + response.statusCode());
                return "Uncategorised";
            }
        } catch (Exception e) {
            System.out.println("Failed to connect to ML Server.");
            return "Uncategorised";
        }
    }
}
