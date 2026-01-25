package ui;

import dao.UniversityDAO;
import dao.UserProfileDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.University;
import model.UserProfile;

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
                universityLabel.setText("No University Selected");
            }
        }
    }

    @FXML
    private void handleBack() {
        SceneManager.loadCenter("Dashboard.fxml");
    }

    @FXML
    private void handleEditProfile() {
        util.DialogUtil.showInfo(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                "Coming Soon",
                "Profile editor will be available in the next update!");
    }

    @FXML
    private void handleChangeUniversity() {
        util.DialogUtil.showInfo(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                "Coming Soon",
                "University switcher will be available in the next update!");
    }

    @FXML
    private void handleEditGradingPolicy() {
        util.DialogUtil.showInfo(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                "Coming Soon",
                "Grading policy editor will be available in the next update!");
    }

    @FXML
    private void handleEditAssessment() {
        util.DialogUtil.showInfo(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                "Coming Soon",
                "Assessment policy editor will be available in the next update!");
    }

    @FXML
    private void handleResetData() {
        boolean confirmed = util.DialogUtil.showConfirmation(
                (javafx.stage.Stage) backButton.getScene().getWindow(),
                "⚠️ Reset All Data",
                "This will permanently delete ALL your data including:\n" +
                        "• All semesters\n" +
                        "• All subjects\n" +
                        "• All marks\n" +
                        "• Your profile\n\n" +
                        "This action CANNOT be undone!\n\n" +
                        "Are you absolutely sure?");

        if (confirmed) {
            // TODO: Implement reset logic
            util.DialogUtil.showInfo(
                    (javafx.stage.Stage) backButton.getScene().getWindow(),
                    "Not Implemented",
                    "Reset functionality will be implemented soon.");
        }
    }
}