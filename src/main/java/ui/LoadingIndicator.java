package util;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Reusable loading indicator overlay
 */
public class LoadingIndicator {

    /**
     * Create a loading overlay that can be added to any parent
     */
    public static StackPane createLoadingOverlay(String message) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(20, 20, 43, 0.85);");
        overlay.setVisible(false);
        overlay.setManaged(false);

        // Loading content
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(250);
        content.setMaxHeight(150);
        content.setStyle(
                "-fx-background-color: #1F1F33; " +
                        "-fx-background-radius: 18; " +
                        "-fx-padding: 30; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 20, 0, 0, 8);");

        // Spinner
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(50, 50);
        spinner.setStyle("-fx-accent: #7C3AED;");

        // Message
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        messageLabel.setWrapText(true);
        messageLabel.setAlignment(Pos.CENTER);

        content.getChildren().addAll(spinner, messageLabel);
        overlay.getChildren().add(content);

        return overlay;
    }

    /**
     * Show loading overlay with fade animation
     */
    public static void show(StackPane overlay) {
        overlay.setVisible(true);
        overlay.setManaged(true);
        overlay.toFront();

        FadeTransition fade = new FadeTransition(Duration.millis(200), overlay);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    /**
     * Hide loading overlay with fade animation
     */
    public static void hide(StackPane overlay) {
        FadeTransition fade = new FadeTransition(Duration.millis(200), overlay);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> {
            overlay.setVisible(false);
            overlay.setManaged(false);
        });
        fade.play();
    }

    /**
     * Update loading message
     */
    public static void updateMessage(StackPane overlay, String newMessage) {
        VBox content = (VBox) overlay.getChildren().get(0);
        Label messageLabel = (Label) content.getChildren().get(1);
        messageLabel.setText(newMessage);
    }
}