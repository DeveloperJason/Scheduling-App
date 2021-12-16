package scheduling;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduling.Model.User;
import java.net.URL;

/**
 * Main application class
 * @author Jason Philpy
 */
public class Main extends Application {

    /**
     * For tracking apps stage
     */
    private static Stage primaryStage;
    private static User user;

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getResource("Views/Login.fxml");
        if (url != null) {
            Parent root = FXMLLoader.load(url);
            primaryStage.setTitle("Scheduling Program");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.setResizable(false);
            primaryStage.show();
            Main.primaryStage = primaryStage;
        }
    }

    /**
     * Access to primary stage
     * @return the primaryStage
     */
    public static Stage getPrimaryStage() {
        return Main.primaryStage;
    }

    /**
     * Sets user information after successful login
     * @param user information to set
     */
    public static void setUser(User user) {
        Main.user = user;
    }

    /**
     * Returns current user's information
     * @return set user's class
     */
    public static User getUser() {
        return Main.user;
    }

    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}
