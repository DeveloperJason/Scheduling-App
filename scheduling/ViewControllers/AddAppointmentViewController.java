package scheduling.ViewControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import scheduling.Main;
import scheduling.Model.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.Date;

/**
 * View Controller class for adding or updating an appointment
 * @author Jason Philpy
 */
public class AddAppointmentViewController extends ViewController {

    /**
     * View's main title
     */
    @FXML
    private Label mainTitle;

    /**
     * Field for appointment's ID
     */
    @FXML
    private TextField apptId;

    /**
     * Field for appointment's title
     */
    @FXML
    private TextField apptTitle;

    /**
     * Field for appointment's description
     */
    @FXML
    private TextField apptDesc;

    /**
     * Field for appointment's location
     */
    @FXML
    private TextField apptLocation;

    /**
     * Field for appointment's type
     */
    @FXML
    private TextField apptType;

    /**
     * Selector for appointment's contact
     */
    @FXML
    private ChoiceBox<Contact> apptContact;

    /**
     * Selector for appointment's customer
     */
    @FXML
    private ChoiceBox<Customer> apptCustomer;

    /**
     * Selector for appointment's user
     */
    @FXML
    private ChoiceBox<User> apptUser;

    /**
     * Picker for appointment's date
     */
    @FXML
    private DatePicker apptDate;

    /**
     * Selector for appointment's start hour
     */
    @FXML
    private ChoiceBox<Integer> apptStartHour;

    /**
     * Selector for appointment's start minute
     */
    @FXML
    private ChoiceBox<String> apptStartMinute;

    /**
     * Selector for appointment's end hour
     */
    @FXML
    private ChoiceBox<Integer> apptEndHour;

    /**
     * Selector for appointment's end minute
     */
    @FXML
    private ChoiceBox<String> apptEndMinute;

    /**
     * Radio for start AM
     */
    @FXML
    private RadioButton apptStartAM;

    /**
     * Radio for start PM
     */
    @FXML
    private RadioButton apptStartPM;

    /**
     * Radio for end AM
     */
    @FXML
    private RadioButton apptEndAM;

    /**
     * Radio for end PM
     */
    @FXML
    private RadioButton apptEndPM;

    /**
     * Button to confirm fields and either add or update appointment data
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
     * Appointment to be updated if true, added new if false
     */
    private boolean isUpdating = false;

    /**
     * Appointment to add or update
     */
    private Appointment appointment;

    /**
     * List of all contacts
     */
    private final ObservableList<Contact> contacts = DBModel.getAllContacts();

    /**
     * List of all customers
     */
    private final ObservableList<Customer> customers = DBModel.getAllCustomers();

    /**
     * List of all users
     */
    private final ObservableList<User> users = DBModel.getAllUsers();

    /**
     * Sets field data and button actions using lambdas
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    public void initialize() {
        ObservableList<String> mins = FXCollections.observableArrayList();
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            if (i > 0 && i <= 12) {
                hours.add(i);
            }
            String stringNumber = String.valueOf(i);
            if (i < 10) {
                stringNumber = "0" + stringNumber;
            }
            mins.add(stringNumber);
        }
        apptStartHour.setItems(hours);
        apptStartHour.getSelectionModel().selectFirst();
        apptEndHour.setItems(hours);
        apptEndHour.getSelectionModel().selectFirst();
        apptStartMinute.setItems(mins);
        apptStartMinute.getSelectionModel().selectFirst();
        apptEndMinute.setItems(mins);
        apptEndMinute.getSelectionModel().selectFirst();
        apptContact.setConverter(Contact.contactStringConverter);
        apptContact.setItems(contacts);
        apptContact.getSelectionModel().selectFirst();
        apptCustomer.setConverter(Customer.customerStringConverter);
        apptCustomer.setItems(customers);
        apptCustomer.getSelectionModel().selectFirst();
        apptUser.setConverter(User.userStringConverter);
        apptUser.setItems(users);
        apptUser.getSelectionModel().selectFirst();
        apptDate.setValue(LocalDate.now());
        apptDate.setDayCellFactory(datePicker -> new DateCell() {
            @Override
            public void updateItem(LocalDate localDate, boolean b) {
                super.updateItem(localDate, b);
                setDisable(b || localDate.compareTo(LocalDate.now()) < 0);
            }
        });
        if (Main.getPrimaryStage().getUserData() != null) {
            convertToUpdating();
        }
        addBtn.setOnAction(actionEvent -> addBtnAction());
        backBtn.setOnAction(actionEvent -> exitToMenu());
        apptStartAM.setOnAction(actionEvent -> toggleAMPM(true, true));
        apptStartPM.setOnAction(actionEvent -> toggleAMPM(true, false));
        apptEndAM.setOnAction(actionEvent -> toggleAMPM(false, true));
        apptEndPM.setOnAction(actionEvent -> toggleAMPM(false, false));
    }

    /**
     * Switches view texts to updating and adds existing appointment data to fields
     */
    private void convertToUpdating() {
        SimpleDateFormat hourFormat = new SimpleDateFormat("h");
        hourFormat.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat minFormat = new SimpleDateFormat("mm");
        minFormat.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat ampmFormat = new SimpleDateFormat("a");
        ampmFormat.setTimeZone(TimeZone.getDefault());
        isUpdating = true;
        appointment = (Appointment) Main.getPrimaryStage().getUserData();
        mainTitle.setText("Update Appointment");
        addBtn.setText("Update");
        apptId.setText(String.valueOf(appointment.getId()));
        apptTitle.setText(appointment.getTitle());
        apptDesc.setText(appointment.getDescription());
        apptLocation.setText(appointment.getLocation());
        apptType.setText(appointment.getType());
        int contactIndex = 0;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).getId() == appointment.getContactID()) {
                contactIndex = i;
                break;
            }
        }
        apptContact.getSelectionModel().select(contactIndex);
        int customerIndex = 0;
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == appointment.getCustomerID()) {
                customerIndex = i;
                break;
            }
        }
        apptCustomer.getSelectionModel().select(customerIndex);
        int userIndex = 0;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == appointment.getUserID()) {
                userIndex = i;
                break;
            }
        }
        apptUser.getSelectionModel().select(userIndex);
        LocalDate localDate = LocalDate.ofInstant(appointment.getStart().toInstant(),
                TimeZone.getDefault().toZoneId());
        apptDate.setValue(localDate);
        Integer startHour = Integer.parseInt(hourFormat.format(appointment.getStart()));
        apptStartHour.getSelectionModel().select(startHour);
        String startMin = minFormat.format(appointment.getStart());
        apptStartMinute.getSelectionModel().select(startMin);
        String startAMPM = ampmFormat.format(appointment.getStart());
        if (startAMPM.equals("PM")) {
            apptStartAM.setSelected(false);
            apptStartPM.setSelected(true);
        }
        Integer endHour = Integer.parseInt(hourFormat.format(appointment.getEnd()));
        apptEndHour.getSelectionModel().select(endHour);
        String endMin = minFormat.format(appointment.getEnd());
        apptEndMinute.getSelectionModel().select(endMin);
        String endAMPM = ampmFormat.format(appointment.getEnd());
        if (endAMPM.equals("PM")) {
            apptEndAM.setSelected(false);
            apptEndPM.setSelected(true);
        }
    }

    /**
     * Validates field data and adds/updates the database with the appointment
     */
    private void addBtnAction() {
        errorLabel.setText("");
        SimpleDateFormat formatter = new SimpleDateFormat("M/d/yy h:mm a");
        formatter.setTimeZone(TimeZone.getDefault());
        String dateString = apptDate.getValue().format(DateTimeFormatter.ofPattern("M/d/yy"));
        String startHourString = apptStartHour.getValue().toString();
        String endHourString = apptEndHour.getValue().toString();
        String startMinString = apptStartMinute.getValue();
        String endMinString = apptEndMinute.getValue();
        String startAMPM = "AM";
        if (apptStartPM.isSelected()) {
            startAMPM = "PM";
        }
        String endAMPM = "AM";
        if (apptEndPM.isSelected()) {
            endAMPM = "PM";
        }
        String startDateString = dateString + " " + startHourString + ":" + startMinString + " " + startAMPM;
        String endDateString = dateString + " " + endHourString + ":" + endMinString + " " + endAMPM;
        try {
            validateAppointmentTime(formatter.parse(startDateString), formatter.parse(endDateString));
            if (isUpdating) {
                appointment.setTitle(apptTitle.getText());
                appointment.setDescription(apptDesc.getText());
                appointment.setLocation(apptLocation.getText());
                appointment.setType(apptType.getText());
                appointment.setContactID(apptContact.getValue().getId());
                appointment.setCustomerID(apptCustomer.getValue().getId());
                appointment.setUserID(apptUser.getValue().getId());
                try {
                    appointment.setStart(formatter.parse(startDateString));
                    appointment.setEnd(formatter.parse(endDateString));
                    DBModel.updateAppointment(appointment);
                    exitToMenu();
                } catch (ParseException e) {
                    errorLabel.setText("Couldn't add dates: " + e.getMessage());
                } catch (Exception e) {
                    errorLabel.setText("Could not update: " + e.getMessage());
                }
            } else {
                try {
                    appointment = new Appointment(0, apptTitle.getText(), apptDesc.getText(), apptLocation.getText(),
                            apptType.getText(), formatter.parse(startDateString), formatter.parse(endDateString),
                            apptCustomer.getValue().getId(), apptContact.getValue(), apptUser.getValue().getId());
                    DBModel.addAppointment(appointment);
                    exitToMenu();
                } catch (ParseException e) {
                    errorLabel.setText("Couldn't add dates: " + e.getMessage());
                } catch (Exception e) {
                    errorLabel.setText("Could not update: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    /**
     * Ensures that AM and PM are not both selected at the same time
     * @param isStart true if start AM/PM pair, false if end
     * @param isAm true if AM, false if PM
     */
    private void toggleAMPM(boolean isStart, boolean isAm) {
        if (isStart) {
            apptStartAM.setSelected(isAm);
            apptStartPM.setSelected(!isAm);
        } else {
            apptEndAM.setSelected(isAm);
            apptEndPM.setSelected(!isAm);
        }
    }

    /**
     * Checks that the selected appointment time is valid
     * @param start start time of appointment
     * @param end end time of appointment
     * @throws Exception if appointment time is not valid
     */
    private void validateAppointmentTime(Date start, Date end) throws Exception {
        if (start.before(end)) {
            if (validBusinessHours(start) && validBusinessHours(end)) {
                String startString = TimeFormatter.getTimeStringUTC(start);
                String endString = TimeFormatter.getTimeStringUTC(end);
                if (DBModel.hasOverlapAppointments(startString, endString)) {
                    throw new Exception("Appointment overlaps with an existing appointment.");
                }
            } else {
                throw new Exception("Appointment must be within business hours (8am-10pm EST).");
            }
        } else {
            throw new Exception("Start time must come before end time.");
        }
    }

    /**
     * Checks if the given date is within the business hours (8am-10pm EST)
     * @return true if within hours, false if outside
     * @param date date to validate
     */
    private boolean validBusinessHours(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
        format.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
        String officeStart = format.format(date) + " 8:00 AM";
        String officeEnd = format.format(date) + " 10:00 PM";
        Date officeStartDate = TimeFormatter.getESTDateFromString(officeStart);
        Date officeEndDate = TimeFormatter.getESTDateFromString(officeEnd);
        return date.compareTo(officeStartDate) >= 0 && date.compareTo(officeEndDate) <= 0;
    }


}
