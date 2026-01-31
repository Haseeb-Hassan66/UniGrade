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
import java.util.ResourceBundle;
import java.text.MessageFormat;

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

        // Set semester name
        semesterNameLabel.setText(currentSemester.getSemesterName());

        // Refresh GPA from database
        refreshGPA();

        // Load credit hours
        updateCreditHoursDisplay();

        // Load subjects
        loadSubjects();
    }

    // ===== PHASE 6: REFRESH GPA FROM DATABASE =====
    private void refreshGPA() {
        // Reload semester from database to get latest GPA
        Semester updatedSemester = semesterDAO.getById(currentSemester.getId());

        if (updatedSemester != null && updatedSemester.getGpa() != null) {
            currentSemester.setGpa(updatedSemester.getGpa());
            gpaLabel.setText(String.format("%.2f", updatedSemester.getGpa()));
            gpaLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #A78BFA;");
        } else {
            ResourceBundle messages = SceneManager.getBundle();
            gpaLabel.setText(messages.getString("dashboard.gpa.na"));
            gpaLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #7D7A9C;");
        }
    }

    private void updateCreditHoursDisplay() {
        int totalCredits = subjectDAO.getTotalCreditHours(currentSemester.getId());
        int theoryCredits = subjectDAO.getTheoryCreditHours(currentSemester.getId());
        int practicalCredits = subjectDAO.getPracticalCreditHours(currentSemester.getId());

        totalCreditsLabel.setText(String.valueOf(totalCredits));
        ResourceBundle messages = SceneManager.getBundle();
        creditsBreakdownLabel.setText(MessageFormat.format(messages.getString("semester.credits_breakdown"),
                theoryCredits, practicalCredits));
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

        ResourceBundle messages = SceneManager.getBundle();

        Label emoji = new Label(messages.getString("semester.empty.emoji"));
        emoji.setStyle("-fx-font-size: 48px;");

        Label title = new Label(messages.getString("semester.empty.title"));
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #A5A3C7;");

        Label subtitle = new Label(messages.getString("semester.empty.subtitle"));
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

        ResourceBundle messages = SceneManager.getBundle();

        String typeStr = subject.isHasPractical() ? messages.getString("subject.type.theory_practical")
                : messages.getString("subject.type.theory");
        Label creditsLabel = new Label(MessageFormat.format(messages.getString("subject.label.credits"),
                subject.getCreditHoursDisplay(), typeStr));
        creditsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #BDB7E2;");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        Label gradeLabel = new Label(
                MessageFormat.format(messages.getString("subject.label.grade"), subject.getGradeDisplay()));
        gradeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #A78BFA;");

        infoBox.getChildren().addAll(creditsLabel, spacer1, gradeLabel);

        // Action Buttons
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button viewMarksButton = new Button(messages.getString("subject.btn.view_marks"));
        viewMarksButton.setStyle(
                "-fx-background-color: #7C3AED; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 6 16;");
        viewMarksButton.setOnAction(e -> handleViewMarks(subject));

        Button editButton = new Button(messages.getString("subject.btn.edit"));
        editButton.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #A78BFA; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10; -fx-text-fill: #A78BFA; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 6 16;");
        editButton.setOnAction(e -> handleEditSubject(subject));

        Button deleteButton = new Button(messages.getString("subject.btn.delete"));
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
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/AddSubjectDialog.fxml");
            javafx.scene.Parent root = loader.load();

            AddSubjectDialogController dialogController = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(messages.getString("dialog.add_subject.title"));
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
                            messages.getString("dialog.success.title"),
                            MessageFormat.format(messages.getString("subject.created"), newSubject.getSubjectName()));

                    loadSubjects();
                    updateCreditHoursDisplay();
                } else {
                    util.DialogUtil.showError(
                            (javafx.stage.Stage) backButton.getScene().getWindow(),
                            messages.getString("dialog.error.title"),
                            messages.getString("subject.create.failed"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.error.title"),
                    "Failed to open dialog: " + e.getMessage());
        }
    }

    private void handleViewMarks(Subject subject) {
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/MarksEntry.fxml");
            javafx.scene.Parent root = loader.load();

            MarksEntryController controller = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(
                    MessageFormat.format(messages.getString("dialog.marks_entry.title"), subject.getSubjectName()));
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

                // ===== PHASE 6: REFRESH GPA AFTER MARKS ENTRY =====
                refreshGPA(); // Refresh GPA in real-time!

                // Refresh subjects and credit hours
                loadSubjects();
                updateCreditHoursDisplay();
            });

            dialogStage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.error.title"),
                    "Failed to open marks entry: " + e.getMessage());
        }
    }

    private void handleEditSubject(Subject subject) {
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/EditSubjectDialog.fxml");
            javafx.scene.Parent root = loader.load();

            EditSubjectDialogController dialogController = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(messages.getString("dialog.edit_subject.title"));
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
                        messages.getString("dialog.success.title"),
                        MessageFormat.format(messages.getString("subject.updated"), subject.getSubjectName()));

                loadSubjects();
                updateCreditHoursDisplay();
            }

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.error.title"),
                    "Failed to open dialog: " + e.getMessage());
        }
    }

    private void handleDeleteSubject(Subject subject) {
        ResourceBundle messages = SceneManager.getBundle();
        boolean confirmed = util.DialogUtil.showConfirmation(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                messages.getString("subject.delete.title"),
                MessageFormat.format(messages.getString("subject.delete.message"), subject.getSubjectName()));

        if (confirmed) {
            subjectDAO.delete(subject.getId());
            util.DialogUtil.showInfo(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.success.title"),
                    messages.getString("subject.deleted"));
            loadSubjects();
            updateCreditHoursDisplay();

            // ===== PHASE 6: REFRESH GPA AFTER DELETION =====
            refreshGPA(); // Recalculate GPA after subject deletion
        }
    }

    @FXML
    private void handleViewResult() {
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/SemesterResult.fxml");
            javafx.scene.Parent root = loader.load();

            SemesterResultController controller = loader.getController();
            controller.setSemester(currentSemester);

            SceneManager.getRootLayout().setCenter(root);

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.error.title"),
                    "Failed to open result view: " + e.getMessage());
        }
    }
}
