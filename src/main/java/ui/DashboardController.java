package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import dao.UserProfileDAO;
import model.UserProfile;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        UserProfileDAO dao = new UserProfileDAO();
        UserProfile user = dao.getUser();
        if(user != null) {
            // Show name AND department
            welcomeLabel.setText("Welcome, " + user.getName() + " from " + user.getDepartment() + "!");
        }
    }
}