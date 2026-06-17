/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.emailtracker.ui;

import javax.swing.*;
import java.awt.*;
import com.mycompany.emailtracker.core.DatabaseManage; 
import com.mycompany.emailtracker.core.EmailConfig;
/**
 *
 * @author aaron
 */
public class SettingsDialog extends JDialog {
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private boolean isSaved = false;
    
    public SettingsDialog(JFrame parent){
        super(parent, "Welcome Setup", true);
        setupUI();
    }
    
    private void setupUI(){
        setSize(350, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 20));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        inputPanel.add(new JLabel("Outlook Email:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);
        
        inputPanel.add(new JLabel("App Password:"));
        txtPassword = new JPasswordField();
        inputPanel.add(txtPassword);
        
        add(inputPanel, BorderLayout.CENTER);
        
        JButton btnSave = new JButton("Save and Continue");
        btnSave.addActionListener(e -> saveAction());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void saveAction(){
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        
        if(email.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please fill in both fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DatabaseManage.saveCredentials(email, password);
        isSaved = true;
        dispose();
    }
    
    public boolean isSaved(){
        return isSaved;
    }
}
