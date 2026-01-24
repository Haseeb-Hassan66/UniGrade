package ui;

import dao.UserProfileDAO;
import model.UserProfile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.DBInitializer;

public class App extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // ===== PHASE 8: SHOW SPLASH SCREEN FIRST =====
        showSplashScreen();
    }

    private void showSplashScreen() throws Exception {
        // Load splash screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SplashScreen.fxml"));
        BorderPane splashRoot = loader.load(); // Changed from StackPane to BorderPane

        SplashScreenController controller = loader.getController();
        controller.setStage(primaryStage);

        // Create splash scene - SAME SIZE AS MAIN APP (900x600)
        Scene splashScene = new Scene(splashRoot, 900, 600);

        // Configure stage for splash
        primaryStage.initStyle(StageStyle.UNDECORATED); // No window decorations
        primaryStage.setScene(splashScene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();

        // Initialize database in background
        new Thread(() -> {
            try {
                DBInitializer.initialize();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // This method will be called by SplashScreenController after loading completes
    public static void loadMainApplication(Stage stage) {
        try {
            // Load root layout
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/RootLayout.fxml"));
            BorderPane root = loader.load();

            // Register root layout
            SceneManager.setRootLayout(root);

            // Create main scene
            Scene scene = new Scene(root, 900, 600);

            // Create NEW stage for main app (can't change style of existing stage)
            Stage mainStage = new Stage();
            mainStage.initStyle(StageStyle.DECORATED); // Window decorations
            mainStage.setScene(scene);
            mainStage.setTitle("UniGrade - Academic Performance Tracker");
            mainStage.centerOnScreen();

            // Set initial opacity to 0 for fade-in effect
            scene.getRoot().setOpacity(0.0);

            // Close splash stage and show main stage
            stage.close();
            mainStage.show();

            // Fade in the main app
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(500), scene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            // Decide first screen
            UserProfileDAO dao = new UserProfileDAO();
            if (dao.exists()) {
                UserProfile user = dao.getUser();

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}