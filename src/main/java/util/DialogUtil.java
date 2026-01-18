package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.CustomDialogController;

public class DialogUtil {

    /**
     * Show an error dialog
     */
    public static void showError(Stage owner, String title, String message) {
        showDialog(owner, title, message, "❌", "OK");
    }

    /**
     * Show an info/success dialog
     */
    public static void showInfo(Stage owner, String title, String message) {
        showDialog(owner, title, message, "✅", "OK");
    }

    /**
     * Show a warning dialog
     */
    public static void showWarning(Stage owner, String title, String message) {
        showDialog(owner, title, message, "⚠️", "OK");
    }

    /**
     * Show a confirmation dialog (Yes/No)
     * Returns true if user clicked Yes/Confirm
     */
    public static boolean showConfirmation(Stage owner, String title, String message) {
        return showDialog(owner, title, message, "❓", "Cancel", "Confirm");
    }

    /**
     * Generic dialog method with blur backdrop
     */
    private static boolean showDialog(Stage owner, String title, String message, String icon, String... buttons) {
        try {
            FXMLLoader loader = new FXMLLoader(
                DialogUtil.class.getResource("/fxml/CustomDialog.fxml")
            );
            Parent root = loader.load();

            CustomDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.initStyle(StageStyle.TRANSPARENT);  // Changed to TRANSPARENT for blur effect
            
            // Create scene with transparent background
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            controller.setDialogStage(dialogStage);
            controller.setTitle(title);
            controller.setMessage(message);
            controller.setIcon(icon);
            controller.setButtons(buttons);

            // Apply backdrop blur effect to owner window
            if (owner != null && owner.getScene() != null) {
                javafx.scene.Node ownerRoot = owner.getScene().getRoot();
                javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(8);
                
                // Store original effect
                javafx.scene.effect.Effect originalEffect = ownerRoot.getEffect();
                
                // Apply blur when dialog opens
                ownerRoot.setEffect(blur);
                
                // Remove blur when dialog closes
                dialogStage.setOnHidden(e -> ownerRoot.setEffect(originalEffect));
            }

            dialogStage.showAndWait();

            return controller.isConfirmed();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}