package ui;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreenController {

    @FXML
    private HBox progressFill;

    @FXML
    private Label percentLabel;

    @FXML
    private Label loadingLabel;

    private Stage stage;

    @FXML
    public void initialize() {
        // Ensure progress starts at 0
        StackPane fillBar = (StackPane) progressFill.getChildren().get(0);
        fillBar.setPrefWidth(0);
        percentLabel.setText("0%");

        // Start loading
        startLoadingSequence();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void startLoadingSequence() {
        Task<Void> loadingTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String[] steps = {
                        "Initializing database...",
                        "Loading grading policies...",
                        "Checking user profile...",
                        "Preparing dashboard...",
                        "Finalizing setup..."
                };

                int totalSteps = steps.length;

                for (int i = 0; i < totalSteps; i++) {
                    final int currentStep = i;
                    final int progress = ((i + 1) * 100) / totalSteps;

                    Platform.runLater(() -> {
                        loadingLabel.setText(steps[currentStep]);
                        animateProgress(progress);
                    });

                    // Delays
                    if (i == 0) {
                        Thread.sleep(800);
                    } else if (i == totalSteps - 1) {
                        Thread.sleep(1000);
                    } else {
                        Thread.sleep(900);
                    }
                }

                return null;
            }
        };

        loadingTask.setOnSucceeded(event -> {
            Platform.runLater(() -> {
                loadingLabel.setText("Ready!");
                animateProgress(100);
            });

            PauseTransition pause = new PauseTransition(Duration.millis(800));
            pause.setOnFinished(e -> transitionToMainApp());
            pause.play();
        });

        loadingTask.setOnFailed(event -> {
            System.err.println("Loading failed: " + loadingTask.getException());
            loadingLabel.setText("Error loading application");
        });

        Thread loadingThread = new Thread(loadingTask);
        loadingThread.setDaemon(true);
        loadingThread.start();
    }

    private void animateProgress(int targetPercent) {
        StackPane fillBar = (StackPane) progressFill.getChildren().get(0);
        double targetWidth = (360.0 * targetPercent) / 100.0;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(fillBar.prefWidthProperty(), fillBar.getPrefWidth())),
                new KeyFrame(Duration.millis(700),
                        new KeyValue(fillBar.prefWidthProperty(), targetWidth)));

        timeline.play();
        percentLabel.setText(targetPercent + "%");
    }

    private void transitionToMainApp() {
        try {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), stage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);

            fadeOut.setOnFinished(event -> {
                App.loadMainApplication(stage);
            });

            fadeOut.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}