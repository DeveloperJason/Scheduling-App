package scheduling.ViewControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import scheduling.Model.*;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

/**
 * View Controller class for displaying various reports
 * @author Jason Philpy
 */
public class ReportsViewController extends ApptTableViewController {

    /**
     * Button to go back to previous scene
     */
    @FXML
    private Button backBtn;

    /**
     * Selector for picking report 1 year
     */
    @FXML
    private ChoiceBox<Integer> r1Year;

    /**
     * Selector for picking report 1 month
     */
    @FXML
    private ChoiceBox<String> r1Month;

    /**
     * Selector for picking report 1 type
     */
    @FXML
    private ChoiceBox<String> r1Type;

    /**
     * Button to execute report 1
     */
    @FXML
    private Button r1Run;

    /**
     * Label to display the results of report 1
     */
    @FXML
    private Label r1ReportLabel;

    /**
     * Selector for picking report 2 contact
     */
    @FXML
    private ChoiceBox<Contact> r2Contact;

    /**
     * Selector for picking report 3 country
     */
    @FXML
    private ChoiceBox<Country> r3Country;

    /**
     * Button to execute report 3
     */
    @FXML
    private Button r3Run;

    /**
     * Label to display the results of report 3
     */
    @FXML
    private Label r3ReportLabel;

    /**
     * Runs report setup functions
     */
    @Override
    public void initialize() {
        super.initialize();
        backBtn.setOnAction(actionEvent -> exitToMenu());
        r1Setup();
        r2Setup();
        r3Setup();
    }

    /**
     * Sets up report 1 buttons and fields using lambdas
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    private void r1Setup() {
        ObservableList<Integer> years = DBModel.getAllApptYears();
        r1Year.setItems(years);
        r1Year.getSelectionModel().selectFirst();
        ObservableList<String> months = FXCollections.observableArrayList();
        months.addAll(new DateFormatSymbols().getMonths());
        if (months.size() == 13) {
            months.remove(12);
        }
        r1Month.setItems(months);
        r1Month.getSelectionModel().selectFirst();
        ObservableList<String> types = DBModel.getAllApptTypes();
        r1Type.setItems(types);
        r1Type.getSelectionModel().selectFirst();
        r1Run.setOnAction(actionEvent -> {
            int year = r1Year.getValue();
            String month = r1Month.getValue();
            String type = r1Type.getValue();
            int apptsFound = DBModel.getCountOfAppointments(month, year, type);
            String reportData;
            if (apptsFound > 1) {
                reportData = "There are " + apptsFound + " " + type + " appointments in " + month + ", " + year + ".";
            } else if (apptsFound == 1) {
                reportData = "There is 1 " + type + " appointment in " + month + ", " + year + ".";
            } else {
                reportData = "There are no " + type + " appointments in " + month + ", " + year + ".";
            }
            r1ReportLabel.setText(reportData);
        });
    }

    /**
     * Sets up report 2 fields using lambdas
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    private void r2Setup() {
        ObservableList<Contact> contacts = DBModel.getAllContacts();
        r2Contact.setConverter(Contact.contactStringConverter);
        r2Contact.setItems(contacts);
        r2Contact.setOnAction(actionEvent -> {
            Contact contact = r2Contact.getValue();
            appointmentTableView.setItems(DBModel.getAppointmentsForContact(contact));
        });
    }

    /**
     * Sets up report 3 fields using lambdas
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    private void r3Setup() {
        ObservableList<Country> countries = DBModel.getAllCountries();
        r3Country.setConverter(Country.countryStringConverter);
        r3Country.setItems(countries);
        r3Country.getSelectionModel().selectFirst();
        r3Run.setOnAction(actionEvent -> {
            List<Customer> customers = DBModel.getCustomersByCountry(r3Country.getValue());
            List<Appointment> appointments = new ArrayList<>();
            for (Customer customer : customers) {
                appointments.addAll(DBModel.getAppointmentsForCustomer(customer));

            }
            String reportData = r3Country.getValue().getName() + " has " + customers.size() + " customer(s) with " +
                    appointments.size() + " appointment(s) in the system.";
            r3ReportLabel.setText(reportData);
        });
    }
}
