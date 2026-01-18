package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

public class SceneManager {

    public static BorderPane rootLayout;

    public static void setRootLayout(BorderPane root) {
        rootLayout = root;
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    public static void loadCenter(String fxmlFile) {
        try {
            if (rootLayout == null) {
                System.err.println("Error: rootLayout is null!");
                return;
            }
            Node node = FXMLLoader.load(SceneManager.class.getResource("/fxml/" + fxmlFile));
            rootLayout.setCenter(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}