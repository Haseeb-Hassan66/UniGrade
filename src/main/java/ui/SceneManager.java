package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import java.util.ResourceBundle;

public class SceneManager {

    public static BorderPane rootLayout;
    private static ResourceBundle bundle = ResourceBundle.getBundle("Messages");

    public static void setRootLayout(BorderPane root) {
        rootLayout = root;
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    public static void loadCenter(String fxmlFile) {
        try {
            FXMLLoader loader = getLoader("/fxml/" + fxmlFile);
            Node node = loader.load();
            loadCenter(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCenter(Node node) {
        if (rootLayout == null) {
            System.err.println("Error: rootLayout is null!");
            return;
        }
        rootLayout.setCenter(node);
    }

    /**
     * Creates an FXMLLoader with the global resource bundle and correct class/path
     * context.
     * Use this for manual loading of dialogs or sub-components.
     */
    public static FXMLLoader getLoader(String fxmlPath) {
        return new FXMLLoader(SceneManager.class.getResource(fxmlPath), bundle);
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }
}