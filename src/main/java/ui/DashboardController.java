package ui;

import java.util.List;
import java.util.Optional;

import dao.SemesterDAO;
import dao.UserProfileDAO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Semester;
import model.UserProfile;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label cgpaLabel;

    @FXML
    private VBox semesterListContainer;

    @FXML
    private VBox emptyState;

    @FXML
    private Button addSemesterButton;

    private UserProfile currentUser;
    private SemesterDAO semesterDAO;

    @FXML
    public void initialize() {
        // Load user profile
        UserProfileDAO userDAO = new UserProfileDAO();
        currentUser = userDAO.getUser();
        
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + " from " + currentUser.getDepartment() + "!");
        }

        // Initialize DAO
        semesterDAO = new SemesterDAO();

        // Load semesters
        loadSemesters();

        // Calculate and display CGPA (placeholder for now)
        updateCGPA();
    }

    @FXML
    private void handleAddSemester() {
        // Create dialog for semester name input
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Semester");
        dialog.setHeaderText("Create New Semester");
        dialog.setContentText("Enter semester name:");

        // Show dialog and wait for result
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(semesterName -> {
            String trimmedName = semesterName.trim();
            
            // Validation
            if (trimmedName.isEmpty()) {
                showError("Semester name cannot be empty!");
                return;
            }

            // Check if semester already exists
            if (semesterDAO.exists(currentUser.getId(), trimmedName)) {
                showError("A semester with this name already exists!");
                return;
            }

            // Create and save semester
            Semester semester = new Semester(currentUser.getId(), trimmedName);
            int semesterId = semesterDAO.save(semester);

            if (semesterId != -1) {
                semester.setId(semesterId);
                showInfo("Semester '" + trimmedName + "' created successfully!");
                
                // Reload semesters
                loadSemesters();
            } else {
                showError("Failed to create semester. Please try again.");
            }
        });
    }

    private void loadSemesters() {
        // Clear existing items (except empty state)
        semesterListContainer.getChildren().clear();

        // Get all semesters for user
        List<Semester> semesters = semesterDAO.getAllByUser(currentUser.getId());

        if (semesters.isEmpty()) {
            // Show empty state
            VBox emptyBox = createEmptyState();
            semesterListContainer.getChildren().add(emptyBox);
        } else {
            // Hide empty state and show semesters
            for (Semester semester : semesters) {
                VBox semesterCard = createSemesterCard(semester);
                semesterListContainer.getChildren().add(semesterCard);
            }
        }
    }

    private VBox createEmptyState() {
        VBox emptyBox = new VBox(15);
        emptyBox.setAlignment(Pos.CENTER);
        emptyBox.setPrefHeight(200);
        emptyBox.setStyle("-fx-background-color: #1F1F33; -fx-padding: 40; -fx-background-radius: 18;");
        
        DropShadow shadow = new DropShadow();
        shadow.setRadius(15);
        shadow.setOffsetY(6);
        shadow.setColor(Color.rgb(0, 0, 0, 0.27));
        emptyBox.setEffect(shadow);

        Label emoji = new Label("📚");
        emoji.setStyle("-fx-font-size: 48px;");

        Label title = new Label("No semesters yet!");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #A5A3C7;");

        Label subtitle = new Label("Click 'Add Semester' to get started");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7D7A9C;");

        emptyBox.getChildren().addAll(emoji, title, subtitle);
        return emptyBox;
    }

    private VBox createSemesterCard(Semester semester) {
        VBox card = new VBox(12);
        card.setStyle("-fx-background-color: #1F1F33; -fx-padding: 20; -fx-background-radius: 16;");
        
        DropShadow shadow = new DropShadow();
        shadow.setRadius(15);
        shadow.setOffsetY(6);
        shadow.setColor(Color.rgb(0, 0, 0, 0.27));
        card.setEffect(shadow);

        // Header: Semester Name + GPA
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(semester.getSemesterName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label gpaLabel = new Label(String.format("GPA: %.2f", semester.getGpa()));
        gpaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #A78BFA;");

        header.getChildren().addAll(nameLabel, spacer, gpaLabel);

        // Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("View Subjects");
        viewButton.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 6 16;");
        viewButton.setOnAction(e -> handleViewSemester(semester));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: transparent; -fx-border-color: #F87171; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #F87171; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 6 16;");
        deleteButton.setOnAction(e -> handleDeleteSemester(semester));

        actions.getChildren().addAll(viewButton, deleteButton);

        card.getChildren().addAll(header, actions);
        return card;
    }

    private void handleViewSemester(Semester semester) {
        // TODO: Navigate to semester view (Phase 4)
        showInfo("Semester view coming in Phase 4: Subject Management!");
    }

    private void handleDeleteSemester(Semester semester) {
        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Semester");
        alert.setHeaderText("Delete " + semester.getSemesterName() + "?");
        alert.setContentText("This will delete all subjects and marks in this semester. This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            semesterDAO.delete(semester.getId());
            showInfo("Semester deleted successfully!");
            loadSemesters();
            updateCGPA();
        }
    }

    private void updateCGPA() {
        // TODO: Calculate actual CGPA in Phase 7 (GPA Engine)
        // For now, show 0.00
        cgpaLabel.setText("0.00");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}