package ui;

import dao.UniversityDAO;
import dao.UserProfileDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.University;
import model.UserProfile;
import util.RecalculationService;

import java.util.List;

public class UniversitySwitcherController {

    @FXML
    private Label currentUniversityLabel;

    @FXML
    private VBox universityListContainer;

    @FXML
    private Button saveButton;

    private Stage dialogStage;
    private UserProfile currentUser;
    private UserProfileDAO userDAO;
    private UniversityDAO universityDAO;
    private boolean saveClicked = false;

    // Track current and selected university IDs
    private int currentUniversityId = -1;
    private int selectedUniversityId = -1;

    // Track the currently highlighted button
    private Button selectedButton = null;

    @FXML
    public void initialize() {
        userDAO = new UserProfileDAO();
        universityDAO = new UniversityDAO();
        currentUser = userDAO.getUser();

        if (currentUser != null) {
            currentUniversityId = currentUser.getUniversityId();
            loadCurrentUniversity();
            loadUniversityList();
        }
    }

    private void loadCurrentUniversity() {
        University current = universityDAO.getById(currentUniversityId);
        if (current != null) {
            currentUniversityLabel.setText(current.getName());
        } else {
            currentUniversityLabel.setText("None");
        }
    }

    private void loadUniversityList() {
        List<University> universities = universityDAO.getAll();
        universityListContainer.getChildren().clear();

        for (University uni : universities) {
            Button btn = createUniversityButton(uni);
            universityListContainer.getChildren().add(btn);
        }
    }

    private Button createUniversityButton(University uni) {
        // Outer HBox to hold icon + name
        HBox content = new HBox(12);
        content.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label icon = new Label(uni.getId() == currentUniversityId ? "✓" : "🏛️");
        icon.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
        icon.setPrefWidth(24);

        Label name = new Label(uni.getName());
        name.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        HBox.setHgrow(name, Priority.ALWAYS);

        // "Current" badge if this is the active university
        Label badge = new Label("Current");
        badge.setStyle(
                "-fx-background-color: #7C3AED; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 10px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 3 8; " +
                        "-fx-background-radius: 10;");
        badge.setVisible(uni.getId() == currentUniversityId);
        badge.setManaged(uni.getId() == currentUniversityId);

        content.getChildren().addAll(icon, name, badge);

        // Wrap in a Button-styled VBox for clickable row
        Button btn = new Button();
        btn.setGraphic(content);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        btn.setPrefHeight(48);

        // Set initial style
        if (uni.getId() == currentUniversityId) {
            btn.setStyle(
                    "-fx-background-color: #2D2D44; " +
                            "-fx-border-color: #7C3AED; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10; " +
                            "-fx-cursor: default; " +
                            "-fx-padding: 0 16;");
        } else {
            btn.setStyle(
                    "-fx-background-color: #2D2D44; " +
                            "-fx-border-color: transparent; " +
                            "-fx-border-width: 2; " +
                            "-fx-border-radius: 10; " +
                            "-fx-background-radius: 10; " +
                            "-fx-cursor: hand; " +
                            "-fx-padding: 0 16;");
        }

        // Click handler — skip if this is already the current university
        btn.setOnAction(e -> {
            if (uni.getId() == currentUniversityId)
                return;
            selectUniversity(uni, btn, icon);
        });

        return btn;
    }

    private void selectUniversity(University uni, Button clickedBtn, Label icon) {
        // Reset previously selected button
        if (selectedButton != null && selectedButton != clickedBtn) {
            // Check if previously selected was current university
            resetButtonStyle(selectedButton, false);
            // Reset icon on the previously selected button
            HBox prevContent = (HBox) selectedButton.getGraphic();
            ((Label) prevContent.getChildren().get(0)).setText("🏛️");
        }

        // Highlight newly selected
        selectedButton = clickedBtn;
        selectedUniversityId = uni.getId();
        icon.setText("✓");

        clickedBtn.setStyle(
                "-fx-background-color: #3D3D5C; " +
                        "-fx-border-color: #7C3AED; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: hand; " +
                        "-fx-padding: 0 16;");

        // Enable save button
        saveButton.setDisable(false);
    }

    private void resetButtonStyle(Button btn, boolean isCurrent) {
        btn.setStyle(
                "-fx-background-color: #2D2D44; " +
                        "-fx-border-color: " + (isCurrent ? "#7C3AED" : "transparent") + "; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 10; " +
                        "-fx-background-radius: 10; " +
                        "-fx-cursor: " + (isCurrent ? "default" : "hand") + "; " +
                        "-fx-padding: 0 16;");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        if (selectedUniversityId == -1 || selectedUniversityId == currentUniversityId) {
            util.DialogUtil.showError(dialogStage, "Validation Error",
                    "Please select a different university.");
            return;
        }

        // Get new university name for confirmation message
        University newUni = universityDAO.getById(selectedUniversityId);
        String newName = (newUni != null) ? newUni.getName() : "Selected University";

        // Confirm with user
        boolean confirmed = util.DialogUtil.showConfirmation(dialogStage,
                "⚠️ Confirm University Change",
                "Switching to: " + newName + "\n\n" +
                        "This will recalculate all your existing grades and GPAs\n" +
                        "using " + newName + "'s grading policy.\n\n" +
                        "Do you want to continue?");

        if (!confirmed)
            return;

        // Update university on user profile
        currentUser.setUniversityId(selectedUniversityId);
        userDAO.update(currentUser);

        // Trigger recalculation with new university's policy
        RecalculationService service = new RecalculationService();
        RecalculationService.RecalculationResult result = service.recalculateAll(currentUser.getId(),
                selectedUniversityId);

        System.out.println("University switched to: " + newName + " | Recalculation: " + result);

        saveClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}