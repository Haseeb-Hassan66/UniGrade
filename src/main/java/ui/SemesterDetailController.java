package ui;

import java.util.List;

import dao.SemesterDAO;
import dao.SubjectDAO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Semester;
import model.Subject;

public class SemesterDetailController {

    @FXML
    private Button backButton;

    @FXML
    private Label semesterNameLabel;

    @FXML
    private Label gpaLabel;

    @FXML
    private Label totalCreditsLabel;

    @FXML
    private Label creditsBreakdownLabel;

    @FXML
    private Button addSubjectButton;

    @FXML
    private VBox subjectListContainer;

    private Semester currentSemester;
    private SubjectDAO subjectDAO;
    private SemesterDAO semesterDAO;

    @FXML
    public void initialize() {
        subjectDAO = new SubjectDAO();
        semesterDAO = new SemesterDAO();

        // Style scrollpane when scene is ready
        javafx.application.Platform.runLater(() -> {
            if (subjectListContainer.getParent() instanceof javafx.scene.control.ScrollPane) {
                javafx.scene.control.ScrollPane scrollPane = (javafx.scene.control.ScrollPane) subjectListContainer
                        .getParent();
                util.UIUtil.styleScrollPane(scrollPane);
            }
        });
    }

    public void setSemester(Semester semester) {
        this.currentSemester = semester;
        loadSemesterData();
    }

    private void loadSemesterData() {
        if (currentSemester == null)
            return;

        // Set semester name and GPA
        semesterNameLabel.setText(currentSemester.getSemesterName());
        gpaLabel.setText(String.format("%.2f", currentSemester.getGpa()));

        // Load credit hours
        updateCreditHoursDisplay();

        // Load subjects
        loadSubjects();
    }

    private void updateCreditHoursDisplay() {
        int totalCredits = subjectDAO.getTotalCreditHours(currentSemester.getId());
        int theoryCredits = subjectDAO.getTheoryCreditHours(currentSemester.getId());
        int practicalCredits = subjectDAO.getPracticalCreditHours(currentSemester.getId());

        totalCreditsLabel.setText(String.valueOf(totalCredits));
        creditsBreakdownLabel.setText("(" + theoryCredits + " Theory + " + practicalCredits + " Practical)");
    }

    private void loadSubjects() {
        subjectListContainer.getChildren().clear();

        List<Subject> subjects = subjectDAO.getAllBySemester(currentSemester.getId());

        if (subjects.isEmpty()) {
            // Show empty state
            VBox emptyBox = createEmptyState();
            subjectListContainer.getChildren().add(emptyBox);
        } else {
            // Show subjects
            for (Subject subject : subjects) {
                VBox subjectCard = createSubjectCard(subject);
                subjectListContainer.getChildren().add(subjectCard);
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

        Label emoji = new Label("📖");
        emoji.setStyle("-fx-font-size: 48px;");

        Label title = new Label("No subjects yet!");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #A5A3C7;");

        Label subtitle = new Label("Click 'Add Subject' to get started");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7D7A9C;");

        emptyBox.getChildren().addAll(emoji, title, subtitle);
        return emptyBox;
    }

    private VBox createSubjectCard(Subject subject) {
        VBox card = new VBox(12);

        // Dynamic Accent Color based on grading status
        String accentColor = subject.isGraded() ? "#10B981" : "#F59E0B"; // Green (Complete) vs Orange (Incomplete)

        card.setStyle("-fx-background-color: #1F1F33; -fx-padding: 20; -fx-background-radius: 16; " +
                "-fx-border-color: transparent transparent transparent " + accentColor + "; " +
                "-fx-border-width: 0 0 0 6;");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(15);
        shadow.setOffsetY(6);
        shadow.setColor(Color.rgb(0, 0, 0, 0.27));
        card.setEffect(shadow);

        // Header: Subject Name
        Label nameLabel = new Label(subject.getSubjectName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Credits + Grade Info
        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        Label creditsLabel = new Label("Credits: " + subject.getCreditHoursDisplay() +
                (subject.isHasPractical() ? " (Theory + Practical)" : " (Theory Only)"));
        creditsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #BDB7E2;");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label gradeLabel = new Label("Grade: " + subject.getGradeDisplay());
        gradeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #A78BFA;");

        infoBox.getChildren().addAll(creditsLabel, spacer1, gradeLabel);

        // Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button viewMarksButton = new Button("View Marks");
        viewMarksButton.setStyle(
                "-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 6 16;");
        viewMarksButton.setOnAction(e -> handleViewMarks(subject));

        Button editButton = new Button("Edit");
        editButton.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #A78BFA; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #A78BFA; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 6 16;");
        editButton.setOnAction(e -> handleEditSubject(subject));

        Button deleteButton = new Button("Delete");
        deleteButton.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #F87171; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #F87171; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 6 16;");
        deleteButton.setOnAction(e -> handleDeleteSubject(subject));

        actions.getChildren().addAll(viewMarksButton, editButton, deleteButton);

        card.getChildren().addAll(nameLabel, infoBox, actions);
        return card;
    }

    @FXML
    private void handleBack() {
        SceneManager.loadCenter("Dashboard.fxml");
    }

    @FXML
    private void handleAddSubject() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/AddSubjectDialog.fxml"));
            javafx.scene.Parent root = loader.load();

            AddSubjectDialogController dialogController = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Add Subject");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(backButton.getScene().getWindow());
            dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogController.setDialogStage(dialogStage);
            dialogController.setSemesterId(currentSemester.getId());

            // Apply blur using shared utility
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) backButton.getScene().getWindow();
            javafx.scene.Node ownerRoot = ownerStage.getScene().getRoot();
            util.UIUtil.applyModalBlur(ownerRoot);

            dialogStage.setOnHidden(e -> util.UIUtil.removeModalBlur(ownerRoot));

            dialogStage.showAndWait();

            Subject newSubject = dialogController.getResultSubject();

            if (newSubject != null) {
                int subjectId = subjectDAO.save(newSubject);

                if (subjectId != -1) {
                    newSubject.setId(subjectId);
                    util.DialogUtil.showInfo(
                            (javafx.stage.Stage) backButton.getScene().getWindow(),
                            "Success",
                            "Subject '" + newSubject.getSubjectName() + "' created successfully!");

                    loadSubjects();
                    updateCreditHoursDisplay();
                } else {
                    util.DialogUtil.showError(
                            (javafx.stage.Stage) backButton.getScene().getWindow(),
                            "Error",
                            "Failed to create subject. Please try again.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    "Error",
                    "Failed to open dialog: " + e.getMessage());
        }
    }

    private void handleViewMarks(Subject subject) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/MarksEntry.fxml"));
            javafx.scene.Parent root = loader.load();

            MarksEntryController controller = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Marks Entry - " + subject.getSubjectName());
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(backButton.getScene().getWindow());
            dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);

            controller.setDialogStage(dialogStage);
            controller.setSubject(subject);

            // Apply blur
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) backButton.getScene().getWindow();
            javafx.scene.Node ownerRoot = ownerStage.getScene().getRoot();
            util.UIUtil.applyModalBlur(ownerRoot);

            dialogStage.setOnHidden(e -> {
                util.UIUtil.removeModalBlur(ownerRoot);
                // Refresh to show updated grades
                loadSubjects();
                updateCreditHoursDisplay();
            });

            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    "Error",
                    "Failed to open marks entry: " + e.getMessage());
        }
    }

    private void handleEditSubject(Subject subject) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/EditSubjectDialog.fxml"));
            javafx.scene.Parent root = loader.load();

            EditSubjectDialogController dialogController = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle("Edit Subject");
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(backButton.getScene().getWindow());
            dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogController.setDialogStage(dialogStage);
            dialogController.setSubject(subject);

            // Apply blur using shared utility
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) backButton.getScene().getWindow();
            javafx.scene.Node ownerRoot = ownerStage.getScene().getRoot();
            util.UIUtil.applyModalBlur(ownerRoot);

            dialogStage.setOnHidden(e -> util.UIUtil.removeModalBlur(ownerRoot));

            dialogStage.showAndWait();

            if (dialogController.isSaveClicked()) {
                subjectDAO.update(subject);

                util.DialogUtil.showInfo(
                        (javafx.stage.Stage) backButton.getScene().getWindow(),
                        "Success",
                        "Subject '" + subject.getSubjectName() + "' updated successfully!");

                loadSubjects();
                updateCreditHoursDisplay();
            }

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    "Error",
                    "Failed to open dialog: " + e.getMessage());
        }
    }

    private void handleDeleteSubject(Subject subject) {
        boolean confirmed = util.DialogUtil.showConfirmation(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                "Delete Subject",
                "Are you sure you want to delete '" + subject.getSubjectName() + "'?\n\n" +
                        "This will delete all marks for this subject.\nThis action cannot be undone.");

        if (confirmed) {
            subjectDAO.delete(subject.getId());
            util.DialogUtil.showInfo(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    "Success",
                    "Subject deleted successfully!");
            loadSubjects();
            updateCreditHoursDisplay();
        }
    }
}