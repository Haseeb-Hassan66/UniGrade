package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CustomDialogController {

    @FXML
    private Label iconLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Label messageLabel;

    @FXML
    private HBox buttonBox;

    private Stage dialogStage;
    private boolean confirmed = false;
    private VBox dialogCard;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        
        // Clip to remove light corners
        if (titleLabel != null && titleLabel.getParent() != null) {
            javafx.scene.Node parent = titleLabel.getParent();
            while (parent != null && !(parent instanceof VBox && parent.getStyleClass().isEmpty())) {
                parent = parent.getParent();
            }
            if (parent instanceof VBox) {
                util.UIUtil.clipToRoundedRectangle((VBox) parent, 22);
            }
        }
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void setIcon(String icon) {
        if (icon != null && !icon.isEmpty()) {
            iconLabel.setText(icon);
            iconLabel.setManaged(true);
            iconLabel.setVisible(true);
        }
    }

    public void setButtons(String... buttonLabels) {
        buttonBox.getChildren().clear();

        for (int i = 0; i < buttonLabels.length; i++) {
            String label = buttonLabels[i];
            final int index = i;

            Button button = new Button(label);
            button.setPrefHeight(40);
            button.setPrefWidth(150);

            // Style based on button type
            if (label.equalsIgnoreCase("OK") || label.equalsIgnoreCase("Yes") || label.equalsIgnoreCase("Confirm")) {
                // Primary button (gradient)
                button.setStyle("-fx-background-color: linear-gradient(to right, #7C3AED, #5B21B6); " +
                              "-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; " +
                              "-fx-background-radius: 12;");
                button.setOnAction(e -> {
                    confirmed = true;
                    dialogStage.close();
                });
            } else {
                // Secondary button (outlined)
                button.setStyle("-fx-background-color: transparent; -fx-border-color: #7C3AED; " +
                              "-fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; " +
                              "-fx-text-fill: #A78BFA; -fx-font-size: 14px; -fx-font-weight: bold;");
                button.setOnAction(e -> {
                    confirmed = false;
                    dialogStage.close();
                });
            }

            buttonBox.getChildren().add(button);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}