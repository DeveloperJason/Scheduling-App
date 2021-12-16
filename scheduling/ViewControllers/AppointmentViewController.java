package scheduling.ViewControllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.Main;
import scheduling.Model.Appointment;
import scheduling.Model.DBModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * View Controller for viewing and deleting appointments
 * @author Jason Philpy
 */
public class AppointmentViewController extends ApptTableViewController {

    /**
     * Appointment table view Location column
     */
    @FXML
    private TableColumn<Object, Object> apptLocationCol;

    /**
     * Appointment table view Contact column
     */
    @FXML
    private TableColumn<Object, Object> apptContactCol;

    /**
     * Appointment table view Type column
     */
    @FXML
    private TableColumn<Object, Object> apptTypeCol;

    /**
     * Appointment table view User ID column
     */
    @FXML
    private TableColumn<Object, Object> apptUserIDCol;

    /**
     * Radio to view by week
     */
    @FXML
    private RadioButton weekRadio;

    /**
     * Raid to view by month
     */
    @FXML
    private RadioButton monthRadio;

    /**
     * Previous week/month button
     */
    @FXML
    private Button prevBtn;

    /**
     * Next week/month button
     */
    @FXML
    private Button nextBtn;

    /**
     * Shows the filtering month/week
     */
    @FXML
    private Label weekMonthLabel;

    /**
     * Button to opens scene to add an appointment
     */
    @FXML
    private Button addBtn;

    /**
     * Button to open scene to update an appointment
     */
    @FXML
    private Button updateBtn;

    /**
     * Button to delete selected appointment
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
     * List of appointments to display
     */
    private ObservableList<Appointment> appointments;

    /**
     * Appointment selected to update or delete
     */
    private Appointment appointment;

    /**
     * Start date for appointments to display
     */
    private Date displayStartDate;

    /**
     * End date for appointments to display
     */
    private Date displayEndDate;

    /**
     * Showing week if true, showing month if false
     */
    private boolean showingWeek = true;

    /**
     * Formatter for standard date
     */
    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yy");

    /**
     * Formatter for month and year
     */
    SimpleDateFormat monthFormatter = new SimpleDateFormat("MMMMM, yyyy");

    /**
     * Sets field data and button actions using lambdas
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    @Override
    public void initialize() {
        super.initialize();
        displayStartDate = getStartOfWeekDate();
        displayEndDate = getEndOfWeekDate();
        String displayText = dateFormatter.format(displayStartDate) + "-" +
                dateFormatter.format(displayEndDate);
        weekMonthLabel.setText(displayText);
        apptLocationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        apptContactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        apptTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        apptUserIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        try {
            appointments = DBModel.getAllAppointments();
            updateTable();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
        deleteBtn.setOnAction(actionEvent -> deleteBtnAction());
        backBtn.setOnAction(actionEvent -> exitToMenu());
        prevBtn.setOnAction(actionEvent -> prevBtnAction());
        nextBtn.setOnAction(actionEvent -> nextBtnAction());
        monthRadio.setOnAction(actionEvent -> monthRadioAction());
        weekRadio.setOnAction(actionEvent -> weekRadioAction());
        addBtn.setOnAction(actionEvent -> loadScene("AddAppointment.fxml", 720));
        updateBtn.setOnAction(actionEvent -> updateBtnAction());
    }

    /**
     * Sets user data to selected appointment and loads add appointment scene
     */
    private void updateBtnAction() {
        appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        if (appointment != null) {
            Main.getPrimaryStage().setUserData(appointment);
            loadScene("AddAppointment.fxml", 720);
        } else {
            errorLabel.setText("Please select an appointment.");
        }
    }

    /**
     * Deletes selected appointment
     */
    private void deleteBtnAction() {
        appointment = appointmentTableView.getSelectionModel().getSelectedItem();
        if (appointment!= null) {
            try {
                DBModel.deleteAppointment(appointment);
                appointments.remove(appointment);
                updateTable();
                errorLabel.setText(appointment.getType() + " appointment " + appointment.getId() + " canceled.");
            } catch (Exception e) {
                errorLabel.setText("Could not remove: " + e.getMessage());
            }
        } else {
            errorLabel.setText("Please select a customer.");
        }
    }

    /**
     * Moves the appointments display back by 1 week or 1 month
     */
    private void prevBtnAction() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(displayStartDate);
        if (showingWeek) {
            cal.add(Calendar.DATE, -7);
            displayStartDate = cal.getTime();
            cal.setTime(displayEndDate);
            cal.add(Calendar.DATE, -7);
            displayEndDate = cal.getTime();
            weekMonthLabel.setText(dateFormatter.format(displayStartDate) + "-" +
                    dateFormatter.format(displayEndDate));
        } else {
            cal.add(Calendar.MONTH, -1);
            displayStartDate = cal.getTime();
            int lastDate = cal.getActualMaximum(Calendar.DATE);
            cal.set(Calendar.DATE, lastDate);
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            displayEndDate = cal.getTime();
            weekMonthLabel.setText(monthFormatter.format(displayStartDate));
        }
        updateTable();
    }

    /**
     * Moves the appointments display up by 1 week or 1 month
     */
    private void nextBtnAction() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(displayStartDate);
        if (showingWeek) {
            cal.add(Calendar.DATE, 7);
            displayStartDate = cal.getTime();
            cal.setTime(displayEndDate);
            cal.add(Calendar.DATE, 7);
            displayEndDate = cal.getTime();
            weekMonthLabel.setText(dateFormatter.format(displayStartDate) + "-" +
                    dateFormatter.format(displayEndDate));
        } else {
            cal.add(Calendar.MONTH, 1);
            displayStartDate = cal.getTime();
            int lastDate = cal.getActualMaximum(Calendar.DATE);
            cal.set(Calendar.DATE, lastDate);
            cal.set(Calendar.HOUR, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            displayEndDate = cal.getTime();
            weekMonthLabel.setText(monthFormatter.format(displayStartDate));
        }
        updateTable();
    }

    /**
     * Switches to display appointments by week
     */
    private void weekRadioAction() {
        showingWeek = true;
        monthRadio.setSelected(false);
        displayStartDate = getStartOfWeekDate();
        displayEndDate = getEndOfWeekDate();
        weekMonthLabel.setText(dateFormatter.format(displayStartDate) + "-" +
                dateFormatter.format(displayEndDate));
        updateTable();
    }

    /**
     * Switches to display appointments by month
     */
    private void monthRadioAction() {
        showingWeek = false;
        weekRadio.setSelected(false);
        displayStartDate = getStartOfMonthDate();
        displayEndDate = getEndOfMonthDate();
        weekMonthLabel.setText(monthFormatter.format(displayStartDate));
        updateTable();
    }

    /**
     * Filters appointments by the display week/month and sets the tables items
     */
    private void updateTable() {
        ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
        for (Appointment appt : appointments) {
            if (appt.getStart().after(displayStartDate) && appt.getStart().before(displayEndDate)) {
                filteredAppointments.add(appt);
            }
        }
        appointmentTableView.setItems(filteredAppointments);
    }

    /**
     * Gets the date of the beginning of the month
     * @return Date class of beginning of month
     */
    private Date getStartOfMonthDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return changeTimeOf(cal.getTime().toInstant(), "00:00:00");
    }

    /**
     * Gets the date of the end of the month
     * @return Date class of end of month
     */
    private Date getEndOfMonthDate() {
        Calendar cal = Calendar.getInstance();
        int lastDate = cal.getActualMaximum(Calendar.DATE);
        cal.set(Calendar.DATE, lastDate);
        return changeTimeOf(cal.getTime().toInstant(), "23:59:59");
    }

    /**
     * Gets the date of the beginning of the week
     * @return Date class of beginning of week
     */
    private Date getStartOfWeekDate() {
        LocalDateTime weekStartLDT = LocalDateTime.now(TimeZone.getDefault().toZoneId())
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        return changeTimeOf(weekStartLDT.toInstant(ZoneOffset.UTC), "00:00:00");
    }

    /**
     * Gets the date of the end of the week
     * @return Date class of end of the week
     */
    private Date getEndOfWeekDate() {
        LocalDateTime weekStartLDT = LocalDateTime.now(TimeZone.getDefault().toZoneId())
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        return changeTimeOf(weekStartLDT.toInstant(ZoneOffset.UTC), "23:59:59");
    }

    /**
     * Changes the time of a date to the specified time
     * @return Date class of the changed date/time
     * @param instant date that needs it's time changed
     * @param time string of time in format HH:mm:ss to change to
     */
    private Date changeTimeOf(Instant instant, String time) {
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        String weekStartDateString = formatterDate.format(Date.from(instant));
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(weekStartDateString + " " + time);
        } catch (ParseException e) {
            return Date.from(instant);
        }
    }
}
