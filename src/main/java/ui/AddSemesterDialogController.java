package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddSemesterDialogController {

    @FXML
    private TextField semesterNameField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;

    private String semesterName = null;
    private Stage dialogStage;

    @FXML
    public void initialize() {
        // Clear error label initially
        errorLabel.setText("");

        // Focus on text field when dialog opens
        semesterNameField.requestFocus();

        // Allow Enter key to submit
        semesterNameField.setOnAction(e -> handleCreate());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @FXML
    private void handleCreate() {
        String input = semesterNameField.getText().trim();

        // Validation
        if (input.isEmpty()) {
            errorLabel.setText("Semester name cannot be empty!");
            return;
        }

        // Store the name and close dialog
        semesterName = input;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        semesterName = null;
        dialogStage.close();
    }

    // Getter for the semester name
    public String getSemesterName() {
        return semesterName;
    }
}
