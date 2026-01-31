package ui;

import dao.UniversityDAO;
import dao.UserProfileDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.University;
import model.UserProfile;
import java.util.ResourceBundle;
import java.text.MessageFormat;

public class SettingsController {

    @FXML
    private Button backButton;

    @FXML
    private Label nameLabel;

    @FXML
    private Label departmentLabel;

    @FXML
    private Label universityLabel;

    private UserProfile currentUser;
    private UserProfileDAO userDAO;
    private UniversityDAO universityDAO;

    @FXML
    public void initialize() {
        userDAO = new UserProfileDAO();
        universityDAO = new UniversityDAO();

        loadUserData();

        // Style scrollpane when scene is ready
        javafx.application.Platform.runLater(() -> {
            if (nameLabel.getParent().getParent().getParent() instanceof javafx.scene.control.ScrollPane) {
                javafx.scene.control.ScrollPane scrollPane = (javafx.scene.control.ScrollPane) nameLabel.getParent()
                        .getParent().getParent();
                util.UIUtil.styleScrollPane(scrollPane);
            }
        });
    }

    private void loadUserData() {
        currentUser = userDAO.getUser();

        if (currentUser != null) {
            // Profile info
            nameLabel.setText(currentUser.getName());
            departmentLabel.setText(currentUser.getDepartment());

            // University info
            University university = universityDAO.getById(currentUser.getUniversityId());
            if (university != null) {
                universityLabel.setText(university.getName());
            } else {
                ResourceBundle messages = SceneManager.getBundle();
                universityLabel.setText(messages.getString("settings.university.none"));
            }
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.loadCenter("Dashboard.fxml");
    }

    @FXML
    private void handleEditProfile() {
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/EditProfile.fxml");
            javafx.scene.Parent root = loader.load();

            EditProfileController controller = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(messages.getString("settings.profile.edit.title"));
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(backButton.getScene().getWindow());
            dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            // Apply blur
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) backButton.getScene().getWindow();
            javafx.scene.Node ownerRoot = ownerStage.getScene().getRoot();
            util.UIUtil.applyModalBlur(ownerRoot);

            dialogStage.setOnHidden(e -> util.UIUtil.removeModalBlur(ownerRoot));

            dialogStage.showAndWait();

            // If saved, reload data
            if (controller.isSaveClicked()) {
                loadUserData();
                util.DialogUtil.showInfo(
                        (javafx.stage.Stage) backButton.getScene().getWindow(),
                        messages.getString("dialog.success.title"),
                        messages.getString("settings.profile.success"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.error.title"),
                    "Failed to open profile editor: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangeUniversity() {
        ResourceBundle messages = SceneManager.getBundle();
        util.DialogUtil.showInfo(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                messages.getString("dialog.coming.soon"),
                "University switcher will be available in the next update!");
    }

    @FXML
    private void handleEditGradingPolicy() {
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/GradingPolicyEditor.fxml");
            javafx.scene.Parent root = loader.load();

            GradingPolicyEditorController controller = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(messages.getString("settings.grading.edit.title"));
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(backButton.getScene().getWindow());
            dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            // Apply blur
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) backButton.getScene().getWindow();
            javafx.scene.Node ownerRoot = ownerStage.getScene().getRoot();
            util.UIUtil.applyModalBlur(ownerRoot);

            dialogStage.setOnHidden(e -> util.UIUtil.removeModalBlur(ownerRoot));

            dialogStage.showAndWait();

            // If saved, show success
            if (controller.isSaveClicked()) {
                util.DialogUtil.showInfo(
                        (javafx.stage.Stage) backButton.getScene().getWindow(),
                        messages.getString("dialog.success.title"),
                        messages.getString("settings.policy.success"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.error.title"),
                    "Failed to open grading policy editor: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditAssessment() {
        ResourceBundle messages = SceneManager.getBundle();
        try {
            javafx.fxml.FXMLLoader loader = SceneManager.getLoader("/fxml/AssessmentPolicyEditor.fxml");
            javafx.scene.Parent root = loader.load();

            AssessmentPolicyEditorController controller = loader.getController();

            javafx.stage.Stage dialogStage = new javafx.stage.Stage();
            dialogStage.setTitle(messages.getString("settings.assessment.edit.title"));
            dialogStage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            dialogStage.initOwner(backButton.getScene().getWindow());
            dialogStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);

            // Apply blur
            javafx.stage.Stage ownerStage = (javafx.stage.Stage) backButton.getScene().getWindow();
            javafx.scene.Node ownerRoot = ownerStage.getScene().getRoot();
            util.UIUtil.applyModalBlur(ownerRoot);

            dialogStage.setOnHidden(e -> util.UIUtil.removeModalBlur(ownerRoot));

            dialogStage.showAndWait();

            // If saved, show success
            if (controller.isSaveClicked()) {
                util.DialogUtil.showInfo(
                        (javafx.stage.Stage) backButton.getScene().getWindow(),
                        messages.getString("dialog.success.title"),
                        messages.getString("settings.assessment.success"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            util.DialogUtil.showError(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.error.title"),
                    "Failed to open assessment policy editor: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetData() {
        ResourceBundle messages = SceneManager.getBundle();
        boolean confirmed = util.DialogUtil.showConfirmation(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                messages.getString("settings.reset.title"),
                messages.getString("settings.reset.message"));

        if (confirmed) {
            // TODO: Implement reset logic
            util.DialogUtil.showInfo(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    messages.getString("dialog.coming.soon"),
                    messages.getString("settings.reset.not_implemented"));
        }
    }
}