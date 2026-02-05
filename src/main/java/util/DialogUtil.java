package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ui.CustomDialogController;
import ui.SceneManager;

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
            FXMLLoader loader = SceneManager.getLoader("/fxml/CustomDialog.fxml");
            Parent root = loader.load();

            CustomDialogController controller = loader.getController();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(owner);
            dialogStage.initStyle(StageStyle.TRANSPARENT); // Changed to TRANSPARENT for blur effect

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

            // Add darker overlay and blur to owner window
            if (owner != null && owner.getScene() != null) {
                javafx.scene.Node ownerRoot = owner.getScene().getRoot();

                // Apply blur using shared utility
                util.UIUtil.applyModalBlur(ownerRoot);

                // Restore when dialog closes
                dialogStage.setOnHidden(e -> util.UIUtil.removeModalBlur(ownerRoot));
            }

            dialogStage.showAndWait();

            return controller.isConfirmed();

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Show a dialog explaining what to do when university is not listed.
     * 
     * @param owner The parent window
     * @return true if user wants to auto-select General, false otherwise
     */
    public static boolean showUniversityNotListedDialog(Stage owner) {
        String message = "No problem! Here's what you can do:" +
                "\n\n" +
                "1. Select 'General/Standard (Customizable)' for now\n" +
                "2. After setup, go to Settings\n" +
                "3. Edit grading policies to match your university\n" +
                "4. Edit assessment components as needed" +
                "\n\n" +
                "Would you like me to select 'General/Standard' for you?";

        return showDialog(owner, "🎓 University Not Listed", message, "ℹ️",
                "No, I'll Choose", "Yes, Select It For Me");
    }
}