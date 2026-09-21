/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.emailtracker.core;
import com.mycompany.emailtracker.ui.MainWindow;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author aaron
 */
public class EmailTracker {

    public static void main(String[] args) {
        JWindow splash = createSplash();
        splash.setVisible(true);

        new Thread(() -> {
            boolean serviceStarted = PythonServiceManager.startService();

            SwingUtilities.invokeLater(() -> {
                splash.dispose();

                if (!serviceStarted) {
                    JOptionPane.showMessageDialog(null,
                            "Could not start the Python ML service.\n"
                            + "Make sure Python is installed and python_api/requirements.txt dependencies are set up.\n"
                            + "Email classification and AI replies won't work until this is running.",
                            "ML Service Unavailable", JOptionPane.WARNING_MESSAGE);
                }

                MainWindow window = new MainWindow();
                window.setVisible(true);
            });
        }).start();
    }

    private static JWindow createSplash() {
        JWindow splash = new JWindow();
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel label = new JLabel("Starting AI classification service...", SwingConstants.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        panel.add(label, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        splash.getContentPane().add(panel);
        splash.setSize(320, 100);
        splash.setLocationRelativeTo(null);

        return splash;
    }
}
