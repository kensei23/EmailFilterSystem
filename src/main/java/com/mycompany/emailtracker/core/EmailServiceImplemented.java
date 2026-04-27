/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.emailtracker.core;
import com.microsoft.aad.msal4j.*;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.URLName;
import jakarta.mail.Folder;
import java.net.URI;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.List;
/**
 *
 * @author aaron
 */
public class EmailServiceImplemented implements EmailService{
    private Store store;
    private EmailConfig emailConfig;
    
    
    public EmailServiceImplemented(EmailConfig emailConfig){
        this.emailConfig = emailConfig; 
    }
    
    
    @Override
    public void connect(){
        try {
            System.out.println("Attempting OAuth2 Login...");

            IAuthenticationResult result = getAuthResult();
            
            String authToken = result.accessToken();
            String email = result.account().username();
            
            System.out.println("Detected Login: " + email);
            
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", EmailConfig.HOST);
            props.put("mail.imaps.port", "993");
            props.put("mail.imaps.ssl.enable", "true");
            
            // Enable XOAUTH2
            props.put("mail.imaps.auth", "true");
            props.put("mail.imaps.auth.mechanisms", "XOAUTH2");
            props.put("mail.imaps.user", email);

            // Create Session
            Session session = Session.getInstance(props);
            session.setDebug(true); 

            store = session.getStore("imaps");
            store.connect(EmailConfig.HOST, email, authToken);

            System.out.println("Connected to email server successfully via OAuth2.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to connect: " + e.getMessage());
        }
    }
    
    private IAuthenticationResult getAuthResult() throws Exception {
        PublicClientApplication app = PublicClientApplication.builder(EmailConfig.CLIENT_ID)
                .authority(EmailConfig.AUTHORITY)
                .build();

        InteractiveRequestParameters parameters = InteractiveRequestParameters
                .builder(new URI("http://localhost")) // Must match Azure
                .scopes(Collections.singleton(EmailConfig.SCOPE))
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
        
        return future.get();
    }

    private String getTextFromMessage(jakarta.mail.Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        }
        else if (message.isMimeType("multipart/*")) {
            jakarta.mail.Multipart mimeMultipart = (jakarta.mail.Multipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }
    
    private String getTextFromMimeMultipart(jakarta.mail.Multipart mimeMultipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            jakarta.mail.BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                return result.append("\n").append(bodyPart.getContent()).toString();
            } else if (bodyPart.isMimeType("text/html")) {
                result.append("\n").append(bodyPart.getContent());
            } else if (bodyPart.getContent() instanceof jakarta.mail.Multipart) {
                result.append(getTextFromMimeMultipart((jakarta.mail.Multipart) bodyPart.getContent()));
            }
        }
        return result.toString();
    }
    
    @Override
    public List<EmailMessage> fetchEmails() {
        // Create an empty list to hold the emails
        List<EmailMessage> emailList = new java.util.ArrayList<>();

        try {
            if (store == null || !store.isConnected()) {
                connect();
            }

            var inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int totalMessages = inbox.getMessageCount();
            int limit = Math.min(10, totalMessages);
            
            jakarta.mail.Message[] messages = inbox.getMessages(totalMessages - limit + 1, totalMessages);

            // Loop backwards (Newest first)
            for (int i = messages.length - 1; i >= 0; i--) {
                jakarta.mail.Message msg = messages[i];
                
                // Extract data
                String sender = msg.getFrom()[0].toString();
                String subject = msg.getSubject();
                java.util.Date sentDate = msg.getSentDate();
                
                String content = getTextFromMessage(msg); 

                EmailMessage emailObj = new EmailMessage(sender, subject, content, sentDate);
                emailList.add(emailObj);
            }

            inbox.close(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Return the list to whoever called this method (the UI)
        return emailList;
    }
    
    @Override
    public void disconnect() {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
