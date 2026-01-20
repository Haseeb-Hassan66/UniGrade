package util;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class UIUtil {

    /**
     * Apply dark modern scrollbar styling to ScrollPane
     */
    public static void styleScrollPane(ScrollPane scrollPane) {
        // Make ScrollPane transparent
        scrollPane.setStyle(
                "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent;");

        // Style the scrollbar through CSS (unavoidable for scrollbars)
        scrollPane.lookup(".scroll-bar:vertical .thumb");

        // Apply custom scrollbar styling
        String scrollbarStyle = ".scroll-bar:vertical {" +
                "    -fx-background-color: transparent;" +
                "}" +
                ".scroll-bar:vertical .track {" +
                "    -fx-background-color: rgba(43, 43, 68, 0.3);" +
                "    -fx-background-radius: 5;" +
                "}" +
                ".scroll-bar:vertical .thumb {" +
                "    -fx-background-color: rgba(124, 58, 237, 0.6);" +
                "    -fx-background-radius: 5;" +
                "}" +
                ".scroll-bar:vertical .thumb:hover {" +
                "    -fx-background-color: rgba(124, 58, 237, 0.8);" +
                "}" +
                ".scroll-bar:vertical .increment-button, " +
                ".scroll-bar:vertical .decrement-button {" +
                "    -fx-background-color: transparent;" +
                "    -fx-padding: 0;" +
                "}" +
                ".scroll-bar:horizontal {" +
                "    -fx-background-color: transparent;" +
                "}" +
                ".scroll-bar:horizontal .track {" +
                "    -fx-background-color: rgba(43, 43, 68, 0.3);" +
                "    -fx-background-radius: 5;" +
                "}" +
                ".scroll-bar:horizontal .thumb {" +
                "    -fx-background-color: rgba(124, 58, 237, 0.6);" +
                "    -fx-background-radius: 5;" +
                "}" +
                ".scroll-bar:horizontal .thumb:hover {" +
                "    -fx-background-color: rgba(124, 58, 237, 0.8);" +
                "}" +
                ".scroll-bar:horizontal .increment-button, " +
                ".scroll-bar:horizontal .decrement-button {" +
                "    -fx-background-color: transparent;" +
                "    -fx-padding: 0;" +
                "}";

        // Apply to the scene
        if (scrollPane.getScene() != null && scrollPane.getScene().getRoot() != null) {
            scrollPane.getScene().getRoot().setStyle(scrollPane.getScene().getRoot().getStyle() + scrollbarStyle);
        } else {
            // Apply when scene is attached
            scrollPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && newScene.getRoot() != null) {
                    newScene.getRoot().setStyle(newScene.getRoot().getStyle() + scrollbarStyle);
                }
            });
        }
    }

    /**
     * Create a blurred backdrop overlay
     * Place this behind dialogs for modern blur effect
     */
    public static StackPane createBlurredBackdrop(Region contentToBlur) {
        StackPane backdrop = new StackPane();
        backdrop.setStyle("-fx-background-color: rgba(20, 20, 35, 0.7);");

        // Create blur effect
        GaussianBlur blur = new GaussianBlur(10);

        // Apply blur to the backdrop
        backdrop.setEffect(blur);

        // Make it fill the entire area
        backdrop.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        backdrop.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        return backdrop;
    }

    /**
     * Apply enhanced shadow to dialog cards
     */
    public static javafx.scene.effect.DropShadow createDialogShadow() {
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setRadius(40);
        shadow.setOffsetY(15);
        shadow.setColor(Color.rgb(0, 0, 0, 0.5)); // Stronger shadow
        shadow.setSpread(0.2);
        return shadow;
    }

    /**
     * Clip a region to rounded rectangle to remove light corners
     */
    public static void clipToRoundedRectangle(Region region, double radius) {
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.setArcWidth(radius * 2);
        clip.setArcHeight(radius * 2);

        clip.widthProperty().bind(region.widthProperty());
        clip.heightProperty().bind(region.heightProperty());

        region.setClip(clip);
    }

    /**
     * Apply subtle border to dialog for better visibility
     */
    public static String getDialogBorderStyle() {
        return "-fx-border-color: rgba(124, 58, 237, 0.3); " +
                "-fx-border-width: 1; " +
                "-fx-border-radius: 22;";
    }

    /**
     * Apply standard modal blur and darken effect to a root node
     */
    public static void applyModalBlur(Node root) {
        if (root == null)
            return;

        // Create darker overlay effect
        javafx.scene.effect.ColorAdjust darken = new javafx.scene.effect.ColorAdjust();
        darken.setBrightness(-0.3); // Make it darker

        // Apply blur
        javafx.scene.effect.GaussianBlur blur = new javafx.scene.effect.GaussianBlur(10);
        darken.setInput(blur);

        // Apply combined effect
        root.setEffect(darken);
    }

    /**
     * Remove modal blur effect from a root node
     */
    public static void removeModalBlur(Node root) {
        if (root == null)
            return;
        root.setEffect(null);
    }
}