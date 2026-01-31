package ui;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Subject;

public class EditSubjectDialogController {

    @FXML
    private TextField subjectNameField;

    @FXML
    private ComboBox<Integer> theoryCreditsCombo;

    @FXML
    private CheckBox hasPracticalCheckbox;

    @FXML
    private VBox practicalCreditsBox;

    @FXML
    private ComboBox<Integer> practicalCreditsCombo;

    @FXML
    private Label errorLabel;

    private Stage dialogStage;
    private Subject subject;
    private boolean saveClicked = false;

    @FXML
    public void initialize() {
        // Populate dropdowns
        theoryCreditsCombo.getItems().addAll(1, 2, 3, 4, 5, 6);
        practicalCreditsCombo.getItems().addAll(0, 1, 2, 3);

        // Style comboboxes
        styleComboBox(theoryCreditsCombo);
        styleComboBox(practicalCreditsCombo);

        // Clear error label
        errorLabel.setText("");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;

        // Populate fields with existing data
        subjectNameField.setText(subject.getSubjectName());
        theoryCreditsCombo.setValue(subject.getTheoryCreditHours());
        hasPracticalCheckbox.setSelected(subject.isHasPractical());

        // Show/hide practical fields based on current state
        if (subject.isHasPractical()) {
            practicalCreditsBox.setManaged(true);
            practicalCreditsBox.setVisible(true);
            if (subject.getPracticalCreditHours() != null) {
                practicalCreditsCombo.setValue(subject.getPracticalCreditHours());
            } else {
                practicalCreditsCombo.setValue(1); // Default
            }
        }
    }

    @FXML
    private void handlePracticalToggle() {
        boolean hasPractical = hasPracticalCheckbox.isSelected();
        practicalCreditsBox.setManaged(hasPractical);
        practicalCreditsBox.setVisible(hasPractical);

        if (hasPractical && practicalCreditsCombo.getValue() == null) {
            practicalCreditsCombo.setValue(1); // Set default
        }
    }

    @FXML
    private void handleSave() {
        String name = subjectNameField.getText().trim();

        java.util.ResourceBundle messages = SceneManager.getBundle();

        // Validation
        if (name.isEmpty()) {
            errorLabel.setText(messages.getString("subject.label.name.empty"));
            return;
        }

        Integer theoryCredits = theoryCreditsCombo.getValue();
        if (theoryCredits == null) {
            errorLabel.setText(messages.getString("subject.label.theory.empty"));
            return;
        }

        boolean hasPractical = hasPracticalCheckbox.isSelected();
        Integer practicalCredits = null;

        if (hasPractical) {
            practicalCredits = practicalCreditsCombo.getValue();
            if (practicalCredits == null) {
                errorLabel.setText(messages.getString("subject.label.practical.empty"));
                return;
            }
        }

        // Update subject object
        subject.setSubjectName(name);
        subject.setTheoryCreditHours(theoryCredits);
        subject.setHasPractical(hasPractical);
        subject.setPracticalCreditHours(practicalCredits);

        saveClicked = true;
        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        saveClicked = false;
        dialogStage.close();
    }

    public boolean isSaveClicked() {
        return saveClicked;
    }

    private <T> void styleComboBox(ComboBox<T> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: #2B2B44;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: white;");

        comboBox.setCellFactory(lv -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setStyle("-fx-background-color: #2B2B44; -fx-text-fill: white;");
            }
        });

        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setStyle("-fx-text-fill: white;");
            }
        });
    }
}