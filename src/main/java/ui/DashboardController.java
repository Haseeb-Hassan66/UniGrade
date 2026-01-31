package ui;

import javafx.scene.layout.BorderPane;

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
import util.ResultCalculator;
import java.util.ResourceBundle;
import java.text.MessageFormat;

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
            ResourceBundle messages = SceneManager.getBundle();
            String welcomeMsg = MessageFormat.format(messages.getString("dashboard.welcome"),
                    currentUser.getName(), currentUser.getDepartment());
            welcomeLabel.setText(welcomeMsg);
        }

        // Initialize DAO
        semesterDAO = new SemesterDAO();

        // Load semesters
        loadSemesters();

        // Calculate and display CGPA
        updateCGPA();

        // Style scrollpane when scene is ready
        javafx.application.Platform.runLater(() -> {
            if (semesterListContainer.getParent() instanceof javafx.scene.control.ScrollPane) {
                javafx.scene.control.ScrollPane scrollPane = (javafx.scene.control.ScrollPane) semesterListContainer
                        .getParent();
                util.UIUtil.styleScrollPane(scrollPane);
            }
        });
    }

    @FXML
    private void handleAddSemester() {
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/AddSemesterDialog.fxml");
            javafx.scene.Parent root = loader.load();

            AddSemesterDialogController dialogController = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(messages.getString("dialog.add_semester.title"));
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(addSemesterButton.getScene().getWindow());
            dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogController.setDialogStage(dialogStage);

            // Apply blur
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) addSemesterButton.getScene().getWindow();
            javafx.scene.Node ownerRoot = ownerStage.getScene().getRoot();
            util.UIUtil.applyModalBlur(ownerRoot);

            dialogStage.setOnHidden(e -> util.UIUtil.removeModalBlur(ownerRoot));

            dialogStage.showAndWait();

            String semesterName = dialogController.getSemesterName();

            if (semesterName != null) {
                if (semesterDAO.exists(currentUser.getId(), semesterName)) {
                    showError(messages.getString("semester.exists"));
                    return;
                }

                Semester semester = new Semester(currentUser.getId(), semesterName);
                int semesterId = semesterDAO.save(semester);

                if (semesterId != -1) {
                    semester.setId(semesterId);
                    String msg = MessageFormat.format(messages.getString("semester.created"), semesterName);
                    showInfo(msg);

                    loadSemesters();
                    updateCGPA();
                } else {
                    showError(messages.getString("semester.create.failed"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError(messages.getString("dialog.error.title") + ": " + e.getMessage());
        }
    }

    // ===== PHASE 7 PART 2: VIEW ACADEMIC REPORT =====
    @FXML
    private void handleViewReport() {
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/AcademicReport.fxml");
            javafx.scene.Parent root = loader.load();

            AcademicReportController controller = loader.getController();

            SceneManager.getRootLayout().setCenter(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError(messages.getString("report.view.failed") + ": " + e.getMessage());
        }
    }

    @FXML
    private void handleSettings() {
        SceneManager.loadCenter("Settings.fxml");
    }

    private void loadSemesters() {
        // Clear existing items
        semesterListContainer.getChildren().clear();

        // Get all semesters for user
        List<Semester> semesters = semesterDAO.getAllByUser(currentUser.getId());

        if (semesters.isEmpty()) {
            // Show empty state
            VBox emptyBox = createEmptyState();
            semesterListContainer.getChildren().add(emptyBox);
        } else {
            // Show semesters
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

        ResourceBundle messages = SceneManager.getBundle();

        Label emoji = new Label(messages.getString("dashboard.empty.emoji"));
        emoji.setStyle("-fx-font-size: 48px;");

        Label title = new Label(messages.getString("dashboard.empty.title"));
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #A5A3C7;");

        Label subtitle = new Label(messages.getString("dashboard.empty.subtitle"));
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

        // ===== PHASE 6: DISPLAY SEMESTER GPA =====
        Label gpaLabel = new Label(formatGPA(semester.getGpa()));
        gpaLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #A78BFA;");

        header.getChildren().addAll(nameLabel, spacer, gpaLabel);

        // Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        ResourceBundle messages = SceneManager.getBundle();

        Button viewButton = new Button(messages.getString("dashboard.btn.view_subjects"));
        viewButton.setStyle(
                "-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 6 16;");
        viewButton.setOnAction(e -> handleViewSemester(semester));

        Button deleteButton = new Button(messages.getString("dashboard.btn.delete"));
        deleteButton.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #F87171; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #F87171; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 6 16;");
        deleteButton.setOnAction(e -> handleDeleteSemester(semester));

        actions.getChildren().addAll(viewButton, deleteButton);

        card.getChildren().addAll(header, actions);
        return card;
    }

    private void handleViewSemester(Semester semester) {
        try {
            // Load Semester Detail screen
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/SemesterDetail.fxml");
            javafx.scene.Parent root = loader.load();

            // Get controller and pass semester data
            SemesterDetailController controller = loader.getController();
            controller.setSemester(semester);

            // Load into center
            SceneManager.getRootLayout().setCenter(root);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to open semester details: " + e.getMessage());
        }
    }

    private void handleDeleteSemester(Semester semester) {
        ResourceBundle messages = SceneManager.getBundle();
        String msg = MessageFormat.format(messages.getString("semester.delete.message"), semester.getSemesterName());

        boolean confirmed = util.DialogUtil.showConfirmation(
                (javafx.stage.Stage) welcomeLabel.getScene().getWindow(),
                messages.getString("semester.delete.title"),
                msg);

        if (confirmed) {
            semesterDAO.delete(semester.getId());
            showInfo(messages.getString("dashboard.semester.delete.success"));
            loadSemesters();
            updateCGPA();
        }
    }

    // ===== PHASE 6: CGPA CALCULATION =====
    private void updateCGPA() {
        Double cgpa = semesterDAO.getCGPA(currentUser.getId());

        if (cgpa != null) {
            cgpaLabel.setText(String.format("%.2f", cgpa));

            // Optional: Show grade classification
            String gradeClass = ResultCalculator.getGradeClass(cgpa);
            cgpaLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #A78BFA;");

            // You can add a tooltip or secondary label to show classification
            // For now, just display the number
        } else {
            ResourceBundle messages = SceneManager.getBundle();
            cgpaLabel.setText(messages.getString("dashboard.gpa.na"));
            cgpaLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #7D7A9C;");
        }
    }

    // Helper to format GPA display
    private String formatGPA(Double gpa) {
        ResourceBundle messages = SceneManager.getBundle();
        if (gpa != null) {
            return MessageFormat.format("{0}: {1}", messages.getString("report.table.gpa"), String.format("%.2f", gpa));
        } else {
            return messages.getString("report.table.gpa") + ": " + messages.getString("dashboard.gpa.na");
        }
    }

    private void showError(String message) {
        ResourceBundle messages = SceneManager.getBundle();
        util.DialogUtil.showError(
                (javafx.stage.Stage) welcomeLabel.getScene().getWindow(),
                messages.getString("dialog.error.title"),
                message);
    }

    private void showInfo(String message) {
        ResourceBundle messages = SceneManager.getBundle();
        util.DialogUtil.showInfo(
                (javafx.stage.Stage) welcomeLabel.getScene().getWindow(),
                messages.getString("dialog.success.title"),
                message);
    }
}