/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.emailtracker.core;
import com.mycompany.emailtracker.ui.MainWindow;

/**
 *
 * @author aaron
 */
public class EmailTracker {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Create the window and make it visible
                MainWindow window = new MainWindow();
                window.setVisible(true);
            }
        });
    }
}
