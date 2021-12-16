package scheduling.ViewControllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import scheduling.Main;
import java.io.IOException;
import java.util.Objects;

/**
 * Base view controller class used for navigation
 * @author Jason Philpy
 */
public class ViewController {

    /**
     * Loads a new scene based on fxml file name and specified width
     * @param fxml file name of .fxml file in Views folder
     * @param width desired with of scene
     */
    protected void loadScene(String fxml, int width) {
        Main.getPrimaryStage().close();
        String resourceURL = "../Views/" + fxml;
        Parent root = null;
        try {
            if (getClass().getResource(resourceURL) != null) {
                root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(resourceURL)));
            } else {
                System.out.println("No resource URL Found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (root != null) {
            Main.getPrimaryStage().setScene(new Scene(root, width, 400));
            Main.getPrimaryStage().show();
        }
    }

    /**
     * Exits current scene or program (if on main menu)
     */
    protected void exitToMenu() {
        Main.getPrimaryStage().setUserData(null);
        if (this instanceof MainMenuViewController) {
            loadScene("Login.fxml", 600);
        } else if (this instanceof CustomerViewController || this instanceof ApptTableViewController) {
            loadScene("MainMenu.fxml", 600);
        } else if (this instanceof AddCustomerViewController) {
            loadScene("Customers.fxml", 600);
        } else if (this instanceof AddAppointmentViewController) {
            loadScene("Appointments.fxml", 1000);
        } else {
            System.exit(0);
        }
    }
}
