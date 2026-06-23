/*
Name: Joe Migwi
Index: 220835
Date: 22/05/2026
 */
package rmi;

import common.StudentService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class FXDatabaseClient extends Application {

    private StudentService remoteStub;
    private TableView<Student> studentTable;
    private ObservableList<Student> tableDataList;
    private Label statusLabel;
    private Button fetchButton;

    @Override
    public void init() {
        // Safe baseline initialization for our RMI Registry proxy attachment
        try {
            System.out.println("Initializing Database Client Network Interface...");
            // Connection will look locally for now; switch to Radmin IP later
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.remoteStub = (StudentService) registry.lookup("Obj1");
            System.out.println("Database Client connected to Remote Registry Successfully.");
        } catch (Exception e) {
            System.err.println("Database Client network linking failure.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("RMI Distributed Enterprise Client - Student Records");

        // 1. Initialize TableView and Columns
        studentTable = new TableView<>();
        tableDataList = FXCollections.observableArrayList();
        studentTable.setItems(tableDataList);

        // Define Table Columns matching fields inside Student.java exactly
        TableColumn<Student, Integer> idColumn = new TableColumn<>("Student ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(90);

        TableColumn<Student, String> nameColumn = new TableColumn<>("Full Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(140);

        TableColumn<Student, String> courseColumn = new TableColumn<>("Course / Major");
        courseColumn.setCellValueFactory(new PropertyValueFactory<>("course"));
        courseColumn.setPrefWidth(150);

        TableColumn<Student, Integer> scoreColumn = new TableColumn<>("Exam Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreColumn.setPrefWidth(90);

        TableColumn<Student, String> emailColumn = new TableColumn<>("Institutional Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(180);

        // Bind columns to the table container
        studentTable.getColumns().addAll(idColumn, nameColumn, courseColumn, scoreColumn, emailColumn);
        studentTable.setPlaceholder(new Label("No records fetched yet. Click 'Fetch Records' to sync."));

        // 2. Control Layout Components
        fetchButton = new Button("Fetch Live Records");
        fetchButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15 8 15;");

        statusLabel = new Label("System Status: Ready");
        statusLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #555555;");

        // Set action binding for data synchronization
        fetchButton.setOnAction(event -> fetchDatabaseRecords());

        // Header controls layout
        HBox controlPanel = new HBox(15);
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        controlPanel.getChildren().addAll(fetchButton, statusLabel);

        // 3. Assemble Core Container Layout
        VBox rootLayout = new VBox(15);
        rootLayout.setPadding(new Insets(20));
        rootLayout.getChildren().addAll(controlPanel, studentTable);

        // Present Scene View
        Scene scene = new Scene(rootLayout, 680, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Executes the remote database fetch request asynchronously on a separate thread
     * to keep our JavaFX interface fully responsive.
     */
    private void fetchDatabaseRecords() {
        if (remoteStub == null) {
            statusLabel.setText("System Status: Connection Error (No RMI Server found).");
            showAlert("Connection Failed", "Unable to reach RMI Server.", "Please ensure your teammate's registry server is running.");
            return;
        }

        // Disable control button to avoid spamming network connection requests
        fetchButton.setDisable(true);
        statusLabel.setText("System Status: Synchronizing with remote database...");

        // Spin up a worker thread to handle the network payload stream
        Thread networkWorker = new Thread(() -> {
            try {
                // Trigger the Remote Method Call (Question 4)
                List<Student> fetchedList = remoteStub.getStudentData();

                // Update UI elements safely back inside the primary JavaFX Application Thread
                Platform.runLater(() -> {
                    tableDataList.clear();
                    if (fetchedList != null && !fetchedList.isEmpty()) {
                        tableDataList.addAll(fetchedList);
                        statusLabel.setText("System Status: Sync Successful (" + fetchedList.size() + " records updated).");
                    } else {
                        statusLabel.setText("System Status: Complete. Remote table contains no data rows.");
                    }
                    fetchButton.setDisable(false);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("System Status: Synchronization aborted due to network failure.");
                    fetchButton.setDisable(false);
                    showAlert("RMI Transaction Failure", "Database fetch operation failed.", e.getMessage());
                });
            }
        });

        // Fire worker thread
        networkWorker.setDaemon(true);
        networkWorker.start();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}