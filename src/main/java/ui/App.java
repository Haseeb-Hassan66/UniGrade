package ui;

import dao.UserProfileDAO;
import model.UserProfile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import util.DBInitializer;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Initialize DB
        DBInitializer.initialize();

        // Load root layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
        BorderPane root = loader.load();

        // Register root layout
        SceneManager.setRootLayout(root);

        // Create scene
        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("UniGrade");
        stage.show();

        // Decide first screen
        UserProfileDAO dao = new UserProfileDAO();
        if (dao.exists()) {
            UserProfile user = dao.getUser();

            // ← ADD THIS CHECK (for Phase 2)
            if (user.getUniversityId() == 0) {
                // No university selected yet → go to university selection
                SceneManager.loadCenter("UniversitySelection.fxml");
            } else {
                // University already selected → go to dashboard
                SceneManager.loadCenter("Dashboard.fxml");
            }
        } else {
            // First time user → registration
            SceneManager.loadCenter("FirstRun.fxml");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}