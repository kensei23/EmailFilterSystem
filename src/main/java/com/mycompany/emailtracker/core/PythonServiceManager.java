package com.mycompany.emailtracker.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
/**
 *
 * @author aaron
 */
public class PythonServiceManager {
    
    private static final String HEALTH_URL = "http://127.0.0.1:8000/health";
    private static final String PYTHON_API_DIR = "python_api";
    private static final int MAX_WAIT_SECONDS = 30;

    private static Process pythonProcess;

    public static boolean startService() {
        try {
            System.out.println("Starting Python ML service...");

            ProcessBuilder builder = new ProcessBuilder(
                    getPythonExecutable(), "-m", "uvicorn", "main:app",
                    "--host", "127.0.0.1", "--port", "8000"
            );
            builder.directory(new File(PYTHON_API_DIR));
            builder.redirectErrorStream(true);

            pythonProcess = builder.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(pythonProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Python] " + line);
                    }
                } catch (Exception e) {
                    // Stream closes when the process ends
                }
            }).start();

            Runtime.getRuntime().addShutdownHook(new Thread(PythonServiceManager::stopService));

            return waitForServiceReady();

        } catch (Exception e) {
            System.err.println("Failed to start Python service: " + e.getMessage());
            System.err.println("Make sure Python is installed and python_api/requirements.txt dependencies are set up.");
            return false;
        }
    }

    private static boolean waitForServiceReady() {
        System.out.println("Waiting for ML model to finish training...");

        for (int i = 0; i < MAX_WAIT_SECONDS; i++) {
            if (isServiceUp()) {
                System.out.println("Python ML service is ready.");
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        System.err.println("Timed out waiting for Python service to start.");
        return false;
    }

    private static boolean isServiceUp() {
        try {
            URL url = new URI(HEALTH_URL).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(500);
            conn.setReadTimeout(500);
            return conn.getResponseCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public static void stopService() {
        if (pythonProcess != null && pythonProcess.isAlive()) {
            System.out.println("Shutting down Python ML service...");
            pythonProcess.destroy();
        }
    }

    private static String getPythonExecutable() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win") ? "python" : "python3";
    }

}
