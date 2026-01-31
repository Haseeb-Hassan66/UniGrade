package ui;

import dao.UserProfileDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.UserProfile;

public class EditProfileController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField departmentField;

    private Stage dialogStage;
    private UserProfile currentUser;
    private UserProfileDAO userDAO;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        userDAO = new UserProfileDAO();
        currentUser = userDAO.getUser();

        if (currentUser != null) {
            nameField.setText(currentUser.getName());
            departmentField.setText(currentUser.getDepartment());
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();

        java.util.ResourceBundle messages = SceneManager.getBundle();

        // Validation
        if (name.isEmpty()) {
            util.DialogUtil.showError(dialogStage, messages.getString("dialog.error.title"),
                    messages.getString("dialog.error.name_empty"));
            return;
        }

        if (department.isEmpty()) {
            util.DialogUtil.showError(dialogStage, messages.getString("dialog.error.title"),
                    messages.getString("dialog.error.department_empty"));
            return;
        }

        // Update user
        currentUser.setName(name);
        currentUser.setDepartment(department);
        userDAO.update(currentUser);

        saveClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}