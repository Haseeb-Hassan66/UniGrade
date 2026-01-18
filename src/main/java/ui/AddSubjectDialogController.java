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

public class AddSubjectDialogController {

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
    private Subject resultSubject = null;
    private int semesterId;

    @FXML
    public void initialize() {
        theoryCreditsCombo.getItems().addAll(1, 2, 3, 4, 5, 6);
        theoryCreditsCombo.setValue(3);

        practicalCreditsCombo.getItems().addAll(0, 1, 2, 3);
        practicalCreditsCombo.setValue(1);

        styleComboBox(theoryCreditsCombo);
        styleComboBox(practicalCreditsCombo);

        errorLabel.setText("");
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    @FXML
    private void handlePracticalToggle() {
        boolean hasPractical = hasPracticalCheckbox.isSelected();
        practicalCreditsBox.setManaged(hasPractical);
        practicalCreditsBox.setVisible(hasPractical);
    }

    @FXML
    private void handleCreate() {
        String name = subjectNameField.getText().trim();

        if (name.isEmpty()) {
            errorLabel.setText("Subject name cannot be empty!");
            return;
        }

        Integer theoryCredits = theoryCreditsCombo.getValue();
        if (theoryCredits == null) {
            errorLabel.setText("Please select theory credit hours!");
            return;
        }

        boolean hasPractical = hasPracticalCheckbox.isSelected();
        Integer practicalCredits = null;

        if (hasPractical) {
            practicalCredits = practicalCreditsCombo.getValue();
            if (practicalCredits == null) {
                errorLabel.setText("Please select practical credit hours!");
                return;
            }
        }

        resultSubject = new Subject(
                semesterId,
                name,
                hasPractical,
                theoryCredits,
                practicalCredits
        );

        dialogStage.close();
    }

    @FXML
    private void handleCancel() {
        resultSubject = null;
        dialogStage.close();
    }

    public Subject getResultSubject() {
        return resultSubject;
    }

    private <T> void styleComboBox(ComboBox<T> comboBox) {
        comboBox.setStyle(
                "-fx-background-color: #2B2B44;" +
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-font-size: 14px;" +
                "-fx-text-fill: white;"
        );

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