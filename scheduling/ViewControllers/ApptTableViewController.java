package scheduling.ViewControllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.Model.Appointment;
import scheduling.Model.TimeFormatter;
import java.util.Date;

/**
 * Base view controller class for a appointment table view
 * @author Jason Philpy
 */
public class ApptTableViewController extends ViewController {

    /**
     * Appointment table view
     */
    @FXML
    protected TableView<Appointment> appointmentTableView;

    /**
     * Appointment table view ID column
     */
    @FXML
    protected TableColumn<Object, Object> apptIdCol;

    /**
     * Appointment table view User ID column
     */
    @FXML
    protected TableColumn<Object, Object> apptTitleCol;

    /**
     * Appointment table view Description column
     */
    @FXML
    protected TableColumn<Object, Object> apptDescCol;

    /**
     * Appointment table view start date column
     */
    @FXML
    protected TableColumn<Appointment, Date> apptStartCol;

    /**
     * Appointment table view end date column
     */
    @FXML
    protected TableColumn<Appointment, Date> apptEndCol;

    /**
     * Appointment table view Customer ID column
     */
    @FXML
    protected TableColumn<Object, Object> apptCustIDCol;

    /**
     * Sets table view columns and uses lambdas for date column formatting
     * Lambda use here in order to format the time of the start and end time columns
     */
    public void initialize() {
        apptIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        apptTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        apptDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        apptStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        apptStartCol.setCellFactory(appointmentDateTableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean b) {
                super.updateItem(date, b);
                if (b) {
                    setText(null);
                } else {
                    this.setText(TimeFormatter.getTimeStringForDisplay(date));
                }
            }
        });
        apptEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        apptEndCol.setCellFactory(appointmentDateTableColumn -> new TableCell<>() {
            @Override
            protected void updateItem(Date date, boolean b) {
                super.updateItem(date, b);
                if (b) {
                    setText(null);
                } else {
                    this.setText(TimeFormatter.getTimeStringForDisplay(date));
                }
            }
        });
        apptCustIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
    }

}
