package com.telaeris.oauthtest;

import com.telaeris.oauthtest.config.Configuration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static com.telaeris.oauthtest.AuthCallbackServer.AuthHandler.fetchTokens;

public class OAuthApp extends Application {
    public static VBox layout = new VBox(10);
    private final Button loginButton = new Button("Login with XPtrack");

    public static void main(String[] args) {
        String uri = (args.length > 0) ? args[0] : null;
        if (uri != null && SingleInstanceChecker.launchIfRunning(uri)) {
            System.exit(0);
        }
        SingleInstanceChecker.startServer();
        launch(args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SingleInstanceChecker.stopServer();
        }));
    }

    @Override
    public void start(Stage primaryStage) {
        AuthCallbackServer.startServer();
        loginButton.setOnAction(e -> openBrowserForAuthentication());
        layout.getChildren().add(loginButton);
        primaryStage.setTitle("OAuth Demo");
        primaryStage.setScene(new Scene(layout, 300, 250));
        primaryStage.show();
    }

    public static void processUri(String uri) {
        // Extract code from URI and use it
        String code = extractCodeFromUri(uri);
        if (code != null) {
            CompletableFuture<String> future = fetchTokens(code);
            future.thenAccept(response -> {
                ProfileManagement.fetchProfile();  // Assuming fetchProfile parses the response and updates UI
            }).exceptionally(e -> {
                // Log the exception and only send an error response if no response has been sent yet
                System.err.println("An error occurred, but response was already sent: " + e.getMessage());
                return null;
            });
        }
    }

    private static String extractCodeFromUri(String uri) {
        // Example URI parsing - implement according to your specific format
        return uri.substring(uri.indexOf("code=") + 5);
    }

    private void openBrowserForAuthentication() {
        String url = "http://new4.xptrack.local/oauth/authorize?response_type=code&client_id=" +
                Configuration.getClientId() + "&redirect_uri=" + Configuration.getRedirectUri() + "&scope=";
        try {
            Runtime.getRuntime().exec(new String[]{"/usr/bin/xdg-open", url});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}