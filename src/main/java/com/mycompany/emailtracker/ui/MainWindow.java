package com.mycompany.emailtracker.ui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import com.mycompany.emailtracker.core.EmailMessage;
import com.mycompany.emailtracker.core.EmailService;
import com.mycompany.emailtracker.core.EmailServiceImplemented;
import com.mycompany.emailtracker.core.DatabaseManage; 
import com.mycompany.emailtracker.core.EmailConfig;
import com.mycompany.emailtracker.core.MLClient;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import java.util.List;
/**
 *
 * @author aaron
 */
public class MainWindow extends javax.swing.JFrame {
    
    // Add this ONE line here:
    private EmailService emailService;
    private List<EmailMessage> currentEmails;
    private javax.swing.table.TableRowSorter<DefaultTableModel> rowSorter;

    public MainWindow() {
        initComponents(); 
        
        DatabaseManage.initialiseDatabase();       
        String[] credentials = DatabaseManage.getCredentials();
        
        if(credentials == null){
            System.out.println("No user found. Login screen opening...");
            SettingsDialog loginScreen = new SettingsDialog(this);
            loginScreen.setVisible(true);
         
            if(!loginScreen.isSaved()){
                System.out.println("Login cancelled.");
                System.exit(0);
            }
            
            credentials = loginScreen.getSessionCredentials();
        }
        
        String dbEmail = credentials[0];
        String dbPassword = credentials[1];
        String dbProvider = credentials[2];
        
        System.out.println("Logging in as: " + dbEmail + " via " + dbProvider);
        
        EmailConfig emailConfig = new EmailConfig(dbEmail, dbPassword, dbProvider);
        emailService = new EmailServiceImplemented(emailConfig);
        
        setupTable();
        setupSearch();
        setupDropdown();
        
        // Get and show all emails when the UI is shown -- Updated with new login window
        refreshEmails(true);

    }
    
    private void setupTable() {
        String[] columns = {"Sender", "Subject", "Date", "Category"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tblEmails.setModel(model);

        rowSorter = new javax.swing.table.TableRowSorter<>(model);
        tblEmails.setRowSorter(rowSorter);

        // The Click Listener
        tblEmails.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && tblEmails.getSelectedRow() != -1) {
                
                // Get the row clicked on the screen
                int viewIndex = tblEmails.getSelectedRow();
                
                // Translate screen row to actual data row
                int modelIndex = tblEmails.convertRowIndexToModel(viewIndex);
                
                if (currentEmails != null && modelIndex < currentEmails.size()) {
                    EmailMessage clickedEmail = currentEmails.get(modelIndex);
                    String bodyText = clickedEmail.getContent();
                    
                    if (bodyText != null && bodyText.toLowerCase().contains("<html")) {
                        txtEmailBody.setContentType("text/html");
                    } else {
                        txtEmailBody.setContentType("text/plain");
                    }
                    
                    txtEmailBody.setEditable(false);
                    txtEmailBody.setText(bodyText);
                    txtEmailBody.setCaretPosition(0); 
                }
            }
        });
        }
    
    private void setupSearch() {
        // This listens to every keystroke in the text field
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });
    }
    
    private void setupDropdown() {
        // Clears default text NetBeans has generated
        categoryFilter.removeAllItems();
        
        categoryFilter.addItem("All Emails");
        categoryFilter.addItem("Action Needed");
        categoryFilter.addItem("Offer");
        categoryFilter.addItem("Rejection");
        categoryFilter.addItem("Confirmation");
        
        categoryFilter.addActionListener(e -> {
            String selectedCategory = (String) categoryFilter.getSelectedItem();
            
            if(selectedCategory == null || selectedCategory.equals("All Emails")){
                rowSorter.setRowFilter(null); // Show all emails
            } else {
                // Show rows where this category was assigned in Column 3
                rowSorter.setRowFilter(javax.swing.RowFilter.regexFilter("^" + selectedCategory + "$", 3));
            }
        });
    }

    // The actual filtering logic
    private void filterTable() {
        String text = txtSearch.getText();
        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null); // Show everything if box is empty
        } else {
            // (?i) makes the search case-insensitive
            rowSorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + text));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblEmails = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtEmailBody = new javax.swing.JEditorPane();
        txtSearch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        categoryFilter = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        refreshEmailsBtn = new javax.swing.JButton();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblEmails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(tblEmails);

        jScrollPane4.setViewportView(txtEmailBody);

        txtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSearchActionPerformed(evt);
            }
        });

        jLabel1.setText("Search");

        categoryFilter.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        categoryFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryFilterActionPerformed(evt);
            }
        });

        jButton1.setText("Draft AI Reply");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        refreshEmailsBtn.setText("Refresh Emails");
        refreshEmailsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshEmailsBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(refreshEmailsBtn)
                .addGap(102, 102, 102)
                .addComponent(jButton1)
                .addGap(93, 93, 93)
                .addComponent(categoryFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(41, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(categoryFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(refreshEmailsBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSearchActionPerformed

    private void categoryFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryFilterActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_categoryFilterActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Check which row the user clicked on
        int selectedRow = tblEmails.getSelectedRow();
        
        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select an email from the table first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Prevents filter bug
        int modelRow = tblEmails.convertRowIndexToModel(selectedRow);
        
        // Get email data from that row
        String subject = tblEmails.getValueAt(modelRow, 1).toString();
        String category = tblEmails.getValueAt(modelRow, 3).toString();
        
        if (category.equals("Rejection") || category.equals("Action Needed")){
            System.out.println("Asking AI to draft reply for: " + subject);
            
            String draft = MLClient.draftReply(subject, category);
            
            JTextArea textArea = new JTextArea(10, 40);
            textArea.setText(draft);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "AI Generated draft", JOptionPane.INFORMATION_MESSAGE);
            
        } else {
            JOptionPane.showMessageDialog(this, "No AI reply needed for category: " + category, "Info:", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void refreshEmailsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshEmailsBtnActionPerformed
        refreshEmails(false);
    }//GEN-LAST:event_refreshEmailsBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    
    // Added boolean to stop the repeated authentication
    private void refreshEmails(boolean isFirstLoad){
        System.out.println("Re-fetching emails");
        
        new Thread(() -> {
            try {
                
                if(isFirstLoad){
                    System.out.println("Authenticating connection");
                    emailService.connect();
                }
                
                currentEmails = emailService.fetchEmails();
                
                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel model = (DefaultTableModel) tblEmails.getModel();
                    model.setRowCount(0); // Clears old emails
                    
                    for (EmailMessage email: currentEmails) {
                         Object[] rowData = {
                             email.getSender(),
                             email.getSubject(),
                             email.getReceivedDate(),
                             email.getCategory()
                         };
                         model.addRow(rowData);
                    }
                    System.out.println("Refresh Complete");
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> categoryFilter;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton refreshEmailsBtn;
    private javax.swing.JTable tblEmails;
    private javax.swing.JEditorPane txtEmailBody;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
