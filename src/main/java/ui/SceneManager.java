package ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import java.util.ResourceBundle;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;

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
            logError(e);
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

    private static void logError(Exception e) {
        try {
            String dir = System.getProperty("user.home") + File.separator + ".unigrade";
            new File(dir).mkdirs();
            File logFile = new File(dir, "error.log");
            try (PrintWriter pw = new PrintWriter(new FileWriter(logFile, true))) {
                pw.println("Error loading FXML:");
                e.printStackTrace(pw);
                pw.println("--------------------------------------------------");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}