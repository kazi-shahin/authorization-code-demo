package com.telaeris.oauthtest;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SingleInstanceChecker {
    private static final int PORT = 9999; // Ensure this port is open and not used by other applications
    private static ServerSocket serverSocket;

    public static boolean launchIfRunning(String uri) {
        try {
            // Attempt to connect to the existing application instance
            Socket socket = new Socket("localhost", PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(uri);
            socket.close();
            return true; // Successfully sent to running instance
        } catch (IOException e) {
            // No running instance, continue to start a new one
            return false;
        }
    }

    public static void startServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            Thread serverThread = new Thread(() -> {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            OAuthApp.processUri(inputLine);
                        }
                        clientSocket.close();
                    } catch (IOException e) {
                        if (!serverSocket.isClosed()) {
                            System.out.println("Server accept error: " + e.getMessage());
                        }
                    }
                }
            });
            serverThread.setDaemon(true);
            serverThread.start();
        } catch (IOException e) {
            System.out.println("Port " + PORT + " is already in use. Another instance might be running.");
        }
    }


    public static void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
