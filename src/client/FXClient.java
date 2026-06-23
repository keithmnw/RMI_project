/*
Name: Joe Migwi
Index: 220835
Date: 22/05/2026
 */
package client;

import common.StudentService;
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

    private StudentService remoteStub;
    private TextArea responseArea;

    @Override
    public void init() {
        try {
            System.out.println("[CLIENT] Connecting to RMI Registry...");
            // Connects locally for now; easily updated to a Radmin IP later
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.remoteStub = (StudentService) registry.lookup("Obj1");
            System.out.println("[CLIENT] Successfully linked to RMI Server Stub!");
        } catch (Exception e) {
            System.err.println("[CLIENT ERROR] Connection failed. Operating offline.");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("RMI Client - String Processor");

        Label instructionLabel = new Label("Enter text to process on the remote server:");
        TextField inputField = new TextField();
        inputField.setPromptText("Type something here...");

        Button processButton = new Button("Send to Server");
        processButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-weight: bold;");

        Label responseLabel = new Label("Server Response:");
        responseArea = new TextArea();
        responseArea.setEditable(false);
        responseArea.setWrapText(true);

        processButton.setOnAction(event -> {
            String inputText = inputField.getText().trim();
            if (inputText.isEmpty()) {
                responseArea.setText("Warning: Input field cannot be empty.");
                return;
            }
            sendTextToServer(inputText);
        });

        VBox mainLayout = new VBox(12);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_LEFT);
        mainLayout.getChildren().addAll(instructionLabel, inputField, processButton, responseLabel, responseArea);

        Scene scene = new Scene(mainLayout, 450, 350);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void sendTextToServer(String text) {
        if (remoteStub == null) {
            responseArea.setText("Error: Not connected to RMI server.");
            return;
        }

        try {
            responseArea.setText("Sending data stream to server...");
            // Calling John Keith's string handler method
            String result = remoteStub.echo(text);
            responseArea.setText(result);
        } catch (Exception e) {
            responseArea.setText("Network Exception:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}