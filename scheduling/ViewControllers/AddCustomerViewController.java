package scheduling.ViewControllers;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import scheduling.Main;
import scheduling.Model.Country;
import scheduling.Model.Customer;
import scheduling.Model.DBModel;
import scheduling.Model.Division;
import java.util.HashMap;

/**
 * View Controller class for adding or updating a customer
 * @author Jason Philpy
 */
public class AddCustomerViewController extends ViewController {

    /**
     * Field for customer's ID
     */
    @FXML
    private TextField cusId;

    /**
     * Field for customer's name
     */
    @FXML
    private TextField cusName;

    /**
     * Field for customer's address
     */
    @FXML
    private TextField cusAddress;

    /**
     * Selector for customer's division
     */
    @FXML
    private ChoiceBox<Division> cusDivision;

    /**
     * Selector for customer's country
     */
    @FXML
    private ChoiceBox<Country> cusCountry;

    /**
     * Field for customer's postal code
     */
    @FXML
    private TextField cusPostal;

    /**
     * Field for customer's phone number
     */
    @FXML
    private TextField cusPhone;

    /**
     * Button to confirm fields and either add or update customer data
     */
    @FXML
    private Button addBtn;

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
     * View's main title
     */
    @FXML
    private Label mainTitle;

    /**
     * Customer to add or update
     */
    private Customer customer;

    /**
     * List of all countries
     */
    private final ObservableList<Country> countries = DBModel.getAllCountries();

    /**
     *  HashMap of all divisions
     */
    private final HashMap<Integer, ObservableList<Division>> divisionsMap = DBModel.getAllDivisions();

    /**
     * List of all divisions
     */
    private ObservableList<Division> divisions;

    /**
     * Customer to be updated if true, added new if false
     */
    private boolean isUpdating = false;

    /**
     * Sets field data and button actions using lambdas
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    public void initialize() {
        divisions = divisionsMap.get(countries.get(0).getId());
        cusCountry.setConverter(Country.countryStringConverter);
        cusDivision.setConverter(Division.divisionStringConverter);
        cusDivision.setItems(divisions);
        cusDivision.getSelectionModel().selectFirst();
        cusCountry.setItems(countries);
        cusCountry.getSelectionModel().selectFirst();
        if (Main.getPrimaryStage().getUserData() != null) {
            convertToUpdating();
        }
        backBtn.setOnAction(actionEvent -> exitToMenu());
        cusCountry.setOnAction(actionEvent -> countrySelectAction());
        addBtn.setOnAction(actionEvent -> addBtnAction());
    }

    /**
     * Switches view texts to updating and adds existing customer data to fields
     */
    private void convertToUpdating() {
        isUpdating = true;
        customer = (Customer) Main.getPrimaryStage().getUserData();
        addBtn.setText("Update");
        mainTitle.setText("Update Customer");
        cusId.setText(String.valueOf(customer.getId()));
        cusName.setText(customer.getName());
        cusAddress.setText(customer.getAddress());
        cusPhone.setText(customer.getPhone());
        cusPostal.setText(customer.getPostalCode());
        divisions = divisionsMap.get(customer.getCountry().getId());
        cusDivision.setItems(divisions);
        cusDivision.getSelectionModel().select(getIndexFromDivisions(customer.getDivision().getId()));
        cusCountry.getSelectionModel().select(getIndexFromCountries(customer.getCountry().getId()));
    }

    /**
     * Validates field data and adds/updates the database with the customer
     */
    private void addBtnAction() {
        if (isUpdating) {
            customer.setName(cusName.getText());
            customer.setAddress(cusAddress.getText());
            customer.setPostalCode(cusPostal.getText());
            customer.setPhone(cusPhone.getText());
            customer.setDivision(cusDivision.getSelectionModel().getSelectedItem());
            customer.setCountry(cusCountry.getSelectionModel().getSelectedItem());
            try {
                DBModel.updateCustomer(customer);
                exitToMenu();
            } catch (Exception e) {
                errorLabel.setText("Could not update: " + e.getMessage());
            }
        } else {
            customer = new Customer(0, cusName.getText(), cusAddress.getText(), cusPostal.getText(),
                    cusPhone.getText(), cusDivision.getSelectionModel().getSelectedItem(),
                    cusCountry.getSelectionModel().getSelectedItem());
            try {
                DBModel.addCustomer(customer);
                exitToMenu();
            } catch (Exception e) {
                errorLabel.setText("Could not add: " + e.getMessage());
            }
        }
    }

    /**
     * Sets the division ChoiceBox based on the selected country
     */
    private void countrySelectAction() {
        Country selectedCountry = cusCountry.getSelectionModel().getSelectedItem();
        divisions = divisionsMap.get(selectedCountry.getId());
        cusDivision.setItems(divisions);
        cusDivision.getSelectionModel().selectFirst();
    }

    /**
     * Gets the index of the division using it's ID
     * @return division index
     * @param id Division_ID to check for
     */
    private int getIndexFromDivisions(int id) {
        for (int i = 0; i < divisions.size(); i++) {
            if (divisions.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Gets the index of the country using it's ID
     * @return country's index
     * @param id Country_ID to check for
     */
    private int getIndexFromCountries(int id) {
        for (int i = 0; i < countries.size(); i++) {
            if (countries.get(i).getId() == id) {
                return i;
            }
        }
        return 0;
    }
}
