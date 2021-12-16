package scheduling.ViewControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduling.Main;
import scheduling.Model.DBModel;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * View Controller class for for logging into the program
 * @author Jason Philpy
 */
public class LoginViewController extends ViewController {

    /**
     * Login label
     */
    @FXML
    private Label loginLabel;

    /**
     * Login button
     */
    @FXML
    private Button loginBtn;

    /**
     * Label to display user's location
     */
    @FXML
    private Label locationLabel;

    /**
     * Username field label
     */
    @FXML
    private Label usernameLabel;

    /**
     * Password field label
     */
    @FXML
    private Label passwordLabel;

    /**
     * Username field
     */
    @FXML
    private TextField username;

    /**
     * Password field
     */
    @FXML
    private TextField password;

    /**
     * Label to display alerts and errors
     */
    @FXML
    private Label errorLabel;

    /**
     * Button to go back to previous scene
     */
    @FXML
    private Button exitBtn;

    /**
     * Sets field data and button actions using lambdas
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    public void initialize() {
        ResourceBundle loginRb = ResourceBundle.getBundle("scheduling.Properties.login", Locale.getDefault());
        loginLabel.setText(loginRb.getString("title"));
        loginBtn.setText(loginRb.getString("button"));
        String locationText = loginRb.getString("location") + ": " + ZoneId.systemDefault();
        locationLabel.setText(locationText);
        usernameLabel.setText(loginRb.getString("user"));
        passwordLabel.setText(loginRb.getString("password"));

        loginBtn.setOnAction(actionEvent -> {
            try {
                Main.setUser(DBModel.login(username.getText(), password.getText()));
                loadScene("MainMenu.fxml", 600);
            } catch (Exception e) {
                errorLabel.setText(e.getMessage());
            }
        });
        exitBtn.setOnAction(actionEvent -> exitToMenu());
    }
}
