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
    private JLabel lblPassword;
    private JComboBox<String> providerDropdown;
    private boolean isSaved = false;
    
    public SettingsDialog(JFrame parent){
        super(parent, "Welcome Setup", true);
        setupUI();
    }
    
    private void setupUI(){
        setSize(350, 200);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        inputPanel.add(new JLabel("EmailProvider:"));
        String[] providers = {"Outlook", "iCloud"};
        providerDropdown = new JComboBox<>(providers);
        inputPanel.add(providerDropdown);
        
        inputPanel.add(new JLabel("Email Address:"));
        txtEmail = new JTextField();
        inputPanel.add(txtEmail);
        
        lblPassword = new JLabel("App Password:");
        txtPassword = new JPasswordField();
        // Initially hidden (as Outlook doesn't require an app password for OAuth)
        lblPassword.setVisible(false);
        txtPassword.setVisible(false);
        inputPanel.add(lblPassword);
        inputPanel.add(txtPassword);

        // Changes visibility based on which provider is selected   
        providerDropdown.addActionListener(e -> {
            String selected = (String) providerDropdown.getSelectedItem();
            if("Outlook".equalsIgnoreCase(selected)){
                lblPassword.setVisible(false);
                txtPassword.setVisible(false);
            } else {
                lblPassword.setVisible(true);
                txtPassword.setVisible(true);
            }
            
            inputPanel.revalidate();
            inputPanel.repaint();
        });
        
        providerDropdown.setSelectedItem("Outlook");
        
        add(inputPanel, BorderLayout.CENTER);
        
        JButton btnSave = new JButton("Login/Save");
        btnSave.addActionListener(e -> saveAction());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void saveAction(){
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String provider = (String) providerDropdown.getSelectedItem();
        
        // 3 Stages of validation based on which provider is being used and what info is needed
        if(email.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter your email address", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(!"Outlook".equals(provider) && password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Please enter your App password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if("Outlook".equals(provider)){
            password = "OAUTH_LOGIN";
        }
        
        DatabaseManage.saveCredentials(email, password, provider);
        isSaved = true;
        dispose();
    }
    
    public boolean isSaved(){
        return isSaved;
    }
}
