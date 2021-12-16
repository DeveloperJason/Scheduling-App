package scheduling.ViewControllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import scheduling.Model.Appointment;
import scheduling.Model.DBModel;
import scheduling.Model.TimeFormatter;
import java.util.List;

/**
 * View Controller class navigating to parts of the program as well as appointment notifications
 * @author Jason Philpy
 */
public class MainMenuViewController extends ViewController {

    /**
     * Button to go back to previous scene
     */
    @FXML
    private Button exitBtn;

    /**
     * Button to go to the customers scene
     */
    @FXML
    private Button customersBtn;

    /**
     * Button to go to the appointments scene
     */
    @FXML
    private Button appointmentsBtn;

    /**
     * Button to go to the reports scene
     */
    @FXML
    private Button reportsBtn;

    /**
     * Label to display current and upcoming appointments
     */
    @FXML
    private Label apptNotify;

    /**
     * Sets field data and button actions using lambdas.  Checks for upcoming appointments and adds them to a apptNotify
     * label.
     * All .setOnAction methods use lambdas to remove the override handle function and replace it with a single line.
     */
    public void initialize() {
        exitBtn.setOnAction(actionEvent -> exitToMenu());
        customersBtn.setOnAction(actionEvent -> loadScene("Customers.fxml", 600));
        appointmentsBtn.setOnAction(actionEvent -> loadScene("Appointments.fxml", 1000));
        reportsBtn.setOnAction(actionEvent -> loadScene("Reports.fxml", 700));
        Appointment currentAppt = DBModel.getCurrentAppointment();
        List<Appointment> soonAppts = DBModel.getSoonAppointments();
        StringBuilder appointmentNotice = new StringBuilder();
        if (currentAppt != null) {
            appointmentNotice.append("NOW: Appointment ")
                    .append(currentAppt.getId())
                    .append(" from ").append(TimeFormatter.getTimeStringForDisplay(currentAppt.getStart()))
                    .append(" to ").append(TimeFormatter.getTimeStringForDisplay(currentAppt.getEnd()))
                    .append("\n\n");
        }
        if (soonAppts.size() > 0) {
            for (Appointment appt : soonAppts) {
                appointmentNotice.append("SOON: Appointment ")
                        .append(appt.getId())
                        .append(" from ").append(TimeFormatter.getTimeStringForDisplay(appt.getStart()))
                        .append(" to ").append(TimeFormatter.getTimeStringForDisplay(appt.getEnd()))
                        .append("\n\n");
            }
        }
        if (appointmentNotice.toString().equals("")) {
            appointmentNotice.append("There are no upcoming appointments.");
        }
        apptNotify.setText(appointmentNotice.toString());
    }
}
