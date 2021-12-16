package scheduling.ViewControllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.Main;
import scheduling.Model.Customer;
import scheduling.Model.DBModel;

/**
 * View Controller class for viewing or deleting customers
 * @author Jason Philpy
 */
public class CustomerViewController extends ViewController {

    /**
     * Customer table view
     */
    @FXML
    private TableView<Customer> customerTableView;

    /**
     * Customer table view ID column
     */
    @FXML
    private TableColumn<Object, Object> customerIdCol;

    /**
     * Customer table view Name column
     */
    @FXML
    private TableColumn<Object, Object> customerNameCol;

    /**
     * Customer table view Address column
     */
    @FXML
    private TableColumn<Object, Object> customerAddressCol;

    /**
     * Customer table view Postal Code column
     */
    @FXML
    private TableColumn<Object, Object> customerPostalCol;

    /**
     * Customer table view Phone column
     */
    @FXML
    private TableColumn<Object, Object> customerPhoneCol;

    /**
     * Add customer button
     */
    @FXML
    private Button addBtn;

    /**
     * Update customer button
     */
    @FXML
    private Button updateBtn;

    /**
     * Delete customer button
     */
    @FXML
    private Button deleteBtn;

    /**
     * Button to go back to previous scene
     */
    @FXML
    private Button backBtn;

    /**
     * Label to display alerts and errors
     */
    @FXML
    private Label errorLabel;

    /**
     * List of customers to display
     */
    private ObservableList<Customer> customers;

    /**
     * Customer selected to update or delete
     */
    private Customer customer;

    /**
     * Sets field data and button actions using lambdas
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    public void initialize() {
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPostalCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        try {
            customers = DBModel.getAllCustomers();
            customerTableView.setItems(customers);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
        addBtn.setOnAction(actionEvent -> loadScene("AddCustomer.fxml", 600));
        updateBtn.setOnAction(actionEvent -> {
            customer = customerTableView.getSelectionModel().getSelectedItem();
            if (customer != null) {
                Main.getPrimaryStage().setUserData(customer);
                loadScene("AddCustomer.fxml", 600);
            } else {
                errorLabel.setText("Please select a customer.");
            }
        });
        deleteBtn.setOnAction(actionEvent -> {
            customer = customerTableView.getSelectionModel().getSelectedItem();
            if (customer != null) {
                try {
                    DBModel.deleteCustomer(customer);
                    customers.remove(customer);
                    customerTableView.refresh();
                    errorLabel.setText("Customer " + customer.getId() + " successfully deleted.");
                } catch (Exception e) {
                    errorLabel.setText("Could not remove: " + e.getMessage());
                }
            } else {
                errorLabel.setText("Please select a customer.");
            }
        });
        backBtn.setOnAction(actionEvent -> exitToMenu());
    }
}
