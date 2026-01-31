package ui;

import java.net.URL;
import java.util.List;

import java.util.ResourceBundle;
import java.text.MessageFormat;

import dao.UniversityDAO;
import dao.UserProfileDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import model.University;
import model.UserProfile;

public class UniversitySelectionController implements Initializable {

    @FXML
    private ComboBox<University> universityCombo;

    @FXML
    private Button nextButton;

    @FXML
    private Button createButton;

    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load universities from database
        UniversityDAO dao = new UniversityDAO();
        List<University> universities = dao.getAll();

        // Populate ComboBox
        universityCombo.getItems().addAll(universities);

        // Style ComboBox (matching FirstRun style)
        styleComboBox();
    }

    @FXML
    private void handleNext() {
        University selected = universityCombo.getValue();

        // Validation
        if (selected == null) {
            ResourceBundle messages = ResourceBundle.getBundle("Messages");
            errorLabel.setText(messages.getString("university.select.error"));
            return;
        }

        // Link university to user profile
        UserProfileDAO userDAO = new UserProfileDAO();
        UserProfile user = userDAO.getUser();

        if (user != null) {
            userDAO.updateUniversityId(user.getId(), selected.getId());
            ResourceBundle messages = ResourceBundle.getBundle("Messages");
            System.out.println(MessageFormat.format(messages.getString("university.linked"), selected.getName()));

            // Clear error
            errorLabel.setText("");

            // Navigate to dashboard
            SceneManager.loadCenter("Dashboard.fxml");
        } else {
            ResourceBundle messages = ResourceBundle.getBundle("Messages");
            errorLabel.setText(messages.getString("university.user.not.found"));
        }
    }

    @FXML
    private void handleCreateNew() {
        // Navigate to University Creation screen
        // TODO: Create UniversityCreation.fxml in next step
        ResourceBundle messages = ResourceBundle.getBundle("Messages");
        errorLabel.setText(messages.getString("university.create.soon"));
        // SceneManager.loadCenter("UniversityCreation.fxml");
    }

    private void styleComboBox() {
        // Style the editor (input area)
        universityCombo.getEditor().setStyle(
                "-fx-background-color: #2B2B44;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-padding: 0 12;" +
                        "-fx-font-size: 14px;");

        // Style dropdown items
        universityCombo.setCellFactory(new Callback<ListView<University>, ListCell<University>>() {
            @Override
            public ListCell<University> call(ListView<University> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(University item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName()); // Display university name
                        }
                        setStyle("-fx-background-color: #2B2B44; -fx-text-fill: white;");
                    }
                };
            }
        });

        // Style the ComboBox itself
        universityCombo.setStyle(
                "-fx-background-color: #2B2B44;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 0 12;" +
                        "-fx-background-insets: 0;");

        // Force the arrow button to blend with background
        universityCombo.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if (newSkin != null && universityCombo.lookup(".arrow-button") != null) {
                Region arrow = (Region) universityCombo.lookup(".arrow-button");
                arrow.setStyle("-fx-background-color: #2B2B44;");
            }
        });

        // Style the button cell (selected item display)
        universityCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(University item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
                setStyle("-fx-text-fill: white;");
            }
        });
    }
}