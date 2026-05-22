package rmi;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FXClient extends Application {

    // The remote interface stub used to call methods on the server
    private MyInterface remoteStub;
    private TextArea responseArea;

    @Override
    public void init() {
        // We initialize the connection here to prevent the UI from freezing during boot
        try {
            System.out.println("Connecting to RMI Registry...");

            // Note: Right now this looks locally. Later, you'll change "localhost" to the Radmin IP.
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Look up the remote object registered under the name "Obj1"
            this.remoteStub = (MyInterface) registry.lookup("Obj1");
            System.out.println("Successfully linked to RMI Server Stub!");
        } catch (Exception e) {
            System.err.println("RMI Connection failed. Operating in offline/testing mode.");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("RMI Distributed Client - String Processor");

        // UI Components
        Label instructionLabel = new Label("Enter text to process on the remote server:");
        TextField inputField = new TextField();
        inputField.setPromptText("Type something here...");

        Button processButton = new Button("Send to Server");
        processButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-weight: bold;");

        Label responseLabel = new Label("Server Response:");
        responseArea = new TextArea();
        responseArea.setEditable(false);
        responseArea.setWrapText(true);

        // Define Button Action Execution
        processButton.setOnAction(event -> {
            String inputText = inputField.getText().trim();
            if (inputText.isEmpty()) {
                responseArea.setText("Warning: Input field cannot be empty.");
                return;
            }

            // Route execution to our RMI wrapper method
            sendTextToServer(inputText);
        });

        // Main Layout Container Setup
        VBox mainLayout = new VBox(12);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_LEFT);
        mainLayout.getChildren().addAll(
                instructionLabel,
                inputField,
                processButton,
                responseLabel,
                responseArea
        );

        // Scene Presentation Window Configuration
        Scene scene = new Scene(mainLayout, 450, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Helper method to safely pass UI strings across the network to the RMI server.
     */
    private void sendTextToServer(String text) {
        if (remoteStub == null) {
            responseArea.setText("Error: Not connected to RMI server.\n" +
                    "Your input was: \"" + text + "\"\n\n" +
                    "(This is expected because the server isn't running yet!)");
            return;
        }

        try {
            responseArea.setText("Sending data stream to server...");

            // Execute the remote method via our interface contract (Question 3)
            String result = remoteStub.processInput(text);

            // Display the processed result back inside the client window
            responseArea.setText(result);
        } catch (Exception e) {
            responseArea.setText("Network Exception occurred while communicating with server:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Standard launching engine for JavaFX applications
        launch(args);
    }
}