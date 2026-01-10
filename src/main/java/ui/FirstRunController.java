package ui;

import java.net.URL;
import java.util.ResourceBundle;

import dao.UserProfileDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import model.UserProfile;

public class FirstRunController implements Initializable {

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> departmentCombo;

    @FXML
    private Button registerButton;

    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add departments
        departmentCombo.getItems().addAll(
                "Computer Science",
                "Electrical Engineering",
                "Mechanical Engineering",
                "Civil Engineering"
        );

        // Make ComboBox editable for prompt text
        departmentCombo.setEditable(true);
        departmentCombo.setValue(null); // show prompt initially

        // ---- PIXEL-PERFECT STYLING ----
        // Style the editor (input area)
        departmentCombo.getEditor().setStyle(
                "-fx-background-color: #2B2B44;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 10;"
                + "-fx-border-radius: 10;"
                + "-fx-padding: 0 12;"
                + "-fx-font-size: 14px;"
        );

        // Style dropdown items
        departmentCombo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item);
                        }
                        setStyle("-fx-background-color: #2B2B44; -fx-text-fill: white;");
                    }
                };
            }
        });

        // Style the ComboBox itself to match TextField and remove default arrow box
        departmentCombo.setStyle(
                "-fx-background-color: #2B2B44;"
                + "-fx-background-radius: 10;"
                + "-fx-border-radius: 10;"
                + "-fx-font-size: 14px;"
                + "-fx-text-fill: white;"
                + "-fx-padding: 0 12;"
                + "-fx-background-insets: 0;"
                + "-fx-arrow-button-visible: false;" // removes default arrow box styling
        );

        // Force the arrow button to blend with background
        departmentCombo.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null && departmentCombo.lookup(".arrow-button") != null) {
                Region arrow = (Region) departmentCombo.lookup(".arrow-button");
                arrow.setStyle("-fx-background-color: #2B2B44;");
            }
        });
    }

    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String dept = departmentCombo.getValue();

        // Input validation
        if (name.isEmpty()) {
            errorLabel.setText("Please enter your name!");
            return;
        }

        if (dept == null || dept.isEmpty()) {
            errorLabel.setText("Please select your department!");
            return;
        }

        // Save user to DB
        UserProfileDAO dao = new UserProfileDAO();
        UserProfile user = new UserProfile(0, name, dept, 0);  // universityId = 0 (not selected yet)
        dao.save(user);

        System.out.println("Registration successful for " + name);

        // Clear error if any
        errorLabel.setText("");

        // ✅ FIXED: Navigate to UniversitySelection (not Dashboard)
        SceneManager.loadCenter("UniversitySelection.fxml");
    }
}