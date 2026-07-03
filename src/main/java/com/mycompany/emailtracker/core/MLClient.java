/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.emailtracker.core;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.OutputStream;
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
    
    public static String draftReply(String emailText, String category) {
        try {
            URL url = new URL("http://localhost:8000/api/draft-reply");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            
            // Clean Headers
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            // Strip out internal quotes and newlines so the JSON doesn't break
            String safeEmail = emailText.replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
            
            // Manually combining it together
            String jsonString = "{\"email_body\": \"" + safeEmail + "\", \"category\": \"" + category + "\"}";

            // Send it to Python
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response back
            if (conn.getResponseCode() == 200) {
                Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8");
                String responseStr = scanner.useDelimiter("\\A").next();
                scanner.close();

                JsonObject responseJson = JsonParser.parseString(responseStr).getAsJsonObject();
                return responseJson.get("draft").getAsString();
                
            } else {
                Scanner scanner = new Scanner(conn.getErrorStream(), "UTF-8");
                String errorStr = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                System.out.println("PYTHON ERROR DETAILS: " + errorStr); 
                return "Error " + conn.getResponseCode() + ": " + errorStr; 
            }

        } catch (Exception e) {
            return "Connection Error: " + e.getMessage();
        }
    }
}
