/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.mycompany.emailtracker.core;
import java.util.List;
/**
 *
 * @author aaron
 */
public interface EmailService {
    void connect();
    List<EmailMessage> fetchEmails();
    void disconnect();
}
