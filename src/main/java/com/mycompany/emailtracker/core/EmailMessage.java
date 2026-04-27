/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.emailtracker.core;

/**
 *
 * @author aaron
 */

import java.util.Date;

public class EmailMessage {
    private String sender;
    private String subject;
    private String content;
    private Date receivedDate;

    public EmailMessage(String sender, String subject, String content, Date receivedDate) {
        this.sender = sender;
        this.subject = subject;
        this.content = content;
        this.receivedDate = receivedDate;
    }

    // Getters
    public String getSender() { return sender; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public Date getReceivedDate() { return receivedDate; }
    
    // toString for easy debugging later
    @Override
    public String toString() {
        return sender + ": " + subject;
    }
}
