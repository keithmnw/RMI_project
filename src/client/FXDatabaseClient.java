/*
Name: Joe Migwi
Index: 220835
Date: 22/05/2026
 */
package client;

import common.Student;
import common.StudentServiceImpl;
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
import server.StudentServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class FXDatabaseClient extends Application {

    private server.StudentServiceImpl remoteStub;
    private TableView<Student> studentTable;
    private ObservableList<Student> tableDataList;
    private Label statusLabel;
    private Button fetchButton;

    @Override
    public void init() {
        try {
            System.out.println("[DB CLIENT] Connecting to RMI Registry...");
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            this.remoteStub = (server.StudentServiceImpl) registry.lookup("Obj1");
            System.out.println("[DB CLIENT] Connected Successfully.");
        } catch (Exception e) {
            System.err.println("[DB CLIENT ERROR] Linking failure.");
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("RMI Enterprise Client - Student Records");

        studentTable = new TableView<>();
        tableDataList = FXCollections.observableArrayList();
        studentTable.setItems(tableDataList);

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

        TableColumn<Student, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setPrefWidth(180);

        studentTable.getColumns().addAll(idColumn, nameColumn, courseColumn, scoreColumn, emailColumn);
        studentTable.setPlaceholder(new Label("No records fetched yet. Click 'Fetch Records' to sync."));

        fetchButton = new Button("Fetch Live Records");
        fetchButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        fetchButton.setOnAction(event -> fetchDatabaseRecords());

        statusLabel = new Label("System Status: Ready");

        HBox controlPanel = new HBox(15);
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        controlPanel.getChildren().addAll(fetchButton, statusLabel);

        VBox rootLayout = new VBox(15);
        rootLayout.setPadding(new Insets(20));
        rootLayout.getChildren().addAll(controlPanel, studentTable);

        Scene scene = new Scene(rootLayout, 680, 450);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void fetchDatabaseRecords() {
        if (remoteStub == null) {
            statusLabel.setText("System Status: Connection Error.");
            return;
        }

        fetchButton.setDisable(true);
        statusLabel.setText("System Status: Synchronizing...");

        // Asynchronous background thread to keep UI fluid
        Thread networkWorker = new Thread(() -> {
            try {
                // 1. Fetch data from remote interface
                List<String[]> rawData = remoteStub.getStudents();
                List<Student> processedList = new ArrayList<>();

                // 2. Map the String array elements safely to Student objects
                for (String[] row : rawData) {
                    if (row.length >= 5) {
                        processedList.add(new Student(
                                Integer.parseInt(row[0].trim()),
                                row[1],
                                row[2],
                                Integer.parseInt(row[3].trim()),
                                row[4]
                        ));
                    }
                }

                // 3. Push data updates safely back onto the UI thread
                Platform.runLater(() -> {
                    tableDataList.clear();
                    tableDataList.addAll(processedList);
                    statusLabel.setText("System Status: Sync Successful (" + processedList.size() + " records updated).");
                    fetchButton.setDisable(false);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    statusLabel.setText("System Status: Synchronization aborted.");
                    fetchButton.setDisable(false);
                });
            }
        });

        networkWorker.setDaemon(true);
        networkWorker.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}