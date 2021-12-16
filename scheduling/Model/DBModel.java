package scheduling.Model;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduling.Main;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class responsible for communication with the SQL database
 * @author Jason Philpy
 */
public class DBModel {

    /**
     * Attempts to login using given username and password.
     * @return a User class when successful
     * @param username to check against User_Name column in the users table
     * @param password to check against Password column in users table
     * @throws Exception if username does not exist, fields left blank, or incorrect password
     */
    public static User login(String username, String password) throws Exception {
        ResourceBundle loginRb = ResourceBundle.getBundle("scheduling.Properties.login", Locale.getDefault());
        String emptyErr = loginRb.getString("emptyErr");
        String failErr = loginRb.getString("failErr");
        String dbErr = loginRb.getString("dbErr");
        Date now = new Date();
        String timestamp = TimeFormatter.getTimeStringUTC(now);
        StringBuilder logMessage = new StringBuilder(timestamp);
        logMessage.append(": ");
        if (!username.equals("") && !password.equals("")) {
            String sql = "SELECT User_ID, User_Name, Password FROM users WHERE User_Name = ?";
            try {
                PreparedStatement ps = JDBC.connection.prepareStatement(sql);
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                String foundPw;
                if (rs.next()) {
                    foundPw = rs.getString("Password");
                    if (foundPw.equals("") || !foundPw.equals(password)) {
                        logMessage.append("Unsuccessful Login - User: ");
                        logMessage.append(username);
                        logMessage.append(" - Incorrect password.");
                        logMessage.append("\n");
                        Logger.writeToActivityLog(logMessage.toString());
                        throw new Exception(failErr);
                    } else {
                        logMessage.append("Successful Login - User: ");
                        logMessage.append(username);
                        logMessage.append("\n");
                        Logger.writeToActivityLog(logMessage.toString());
                        return new User(rs.getInt("User_ID"), rs.getString("User_Name"));
                    }
                } else {
                    logMessage.append("Unsuccessful Login - User: ");
                    logMessage.append(username);
                    logMessage.append(" - Username not found.");
                    logMessage.append("\n");
                    Logger.writeToActivityLog(logMessage.toString());
                    throw new Exception(failErr);
                }
            } catch (SQLException e) {
                logMessage.append("Unsuccessful Login - User: ");
                logMessage.append(username);
                logMessage.append(" - SQL Error");
                logMessage.append("\n");
                Logger.writeToActivityLog(logMessage.toString());
                throw new Exception(dbErr + " " + e.getLocalizedMessage());
            }
        } else {
            logMessage.append("Unsuccessful Login - Blank username or password");
            logMessage.append("\n");
            Logger.writeToActivityLog(logMessage.toString());
            throw new Exception(emptyErr);
        }
    }

    /**
     * Pulls a list of all customers in the customers table
     * @return a list of customers
     */
    public static ObservableList<Customer> getAllCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String sql = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postal = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");
                Division division = getDivision(divisionID);
                if (division != null) {
                    Country country = getCountry(division.getCountryID());
                    if (country != null) {
                        Customer customer = new Customer(id, name, address, postal, phone, division, country);
                        customers.add(customer);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customers;
    }

    /**
     * Pulls all customers whose country matches the param
     * @return a list of customers
     * @param country to filter customers
     */
    public static List<Customer> getCustomersByCountry(Country country) {
        List<Customer> customers = new ArrayList<>();
        List<Integer> divisions = getListOfDivisions(country);
        StringBuilder divisionsSB = new StringBuilder("(");
        for (int i = 0; i < divisions.size(); i++) {
            divisionsSB.append(divisions.get(i));
            if (i != divisions.size() - 1) {
                divisionsSB.append(", ");
            } else {
                divisionsSB.append(")");
            }
        }
        String sql = "SELECT Customer_ID, Customer_Name, Address, Postal_Code, Phone, Division_ID FROM customers WHERE " +
                "Division_ID IN " + divisionsSB;
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("Customer_ID");
                String name = rs.getString("Customer_Name");
                String address = rs.getString("Address");
                String postal = rs.getString("Postal_Code");
                String phone = rs.getString("Phone");
                int divisionID = rs.getInt("Division_ID");
                Division division = getDivision(divisionID);
                if (division != null) {
                    Customer customer = new Customer(id, name, address, postal, phone, division, country);
                    customers.add(customer);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return customers;
    }

    /**
     * Pulls a list of all divisions (by id) located in a country
     * @return a list of Division_IDs (integers)
     * @param country to filter Division_IDs from the first_level_divisions table
     */
    private static List<Integer> getListOfDivisions(Country country) {
        List<Integer> divisions = new ArrayList<>();
        String sql = "SELECT Division_ID FROM first_level_divisions WHERE Country_ID = ?";
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, country.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                divisions.add(rs.getInt("Division_ID"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return divisions;
    }

    /**
     * Gets full division data from provided Division_ID
     * @return a completed Division class
     * @param id Division_ID to pull remainder of division data
     */
    private static Division getDivision(int id) {
        String sql = "SELECT Division, Country_ID FROM first_level_divisions WHERE Division_ID = ?";
        try { PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String divisionName = rs.getString("Division");
                int countryID = rs.getInt("Country_ID");
                return new Division(id, divisionName, countryID);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Pulls a list of all divisions and returns as a hashmap for quick access
     * @return a HashMap with integer keys and a list of Divisions
     */
    public static HashMap<Integer, ObservableList<Division>> getAllDivisions() {
        HashMap<Integer, ObservableList<Division>> divisions = new HashMap<>();
        String sql = "SELECT Division_ID, Division, Country_ID FROM first_level_divisions";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                int divID = rs.getInt("Division_ID");
                String div = rs.getString("Division");
                int couID = rs.getInt("Country_ID");
                Division division = new Division(divID, div, couID);
                ObservableList<Division> countryDivs = divisions.get(couID);
                if (countryDivs == null) {
                    countryDivs = FXCollections.observableArrayList();
                    countryDivs.add(division);
                    divisions.put(couID, countryDivs);
                } else {
                    countryDivs.add(division);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return divisions;
    }

    /**
     * Gets a country class based on a given id
     * @return a Country
     * @param id Country_ID from countries table
     */
    static Country getCountry(int id) {
        String sql = "SELECT Country FROM countries WHERE Country_ID = ?";
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String countryName = rs.getString("Country");
                return new Country(id, countryName);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Pulls a list of all countries from the countries table
     * @return a list of countries
     */
    public static ObservableList<Country> getAllCountries() {
        ObservableList<Country> countries = FXCollections.observableArrayList();
        String sql = "SELECT Country_ID, Country FROM countries";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                int couID = rs.getInt("Country_ID");
                String cou = rs.getString("Country");
                Country country = new Country(couID, cou);
                countries.add(country);
            }
        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
        }
        return countries;
    }

    /**
     * Adds a customer to the customer table
     * @param customer Customer class with all relevant table data
     * @throws Exception for SQL error
     */
    public static void addCustomer(Customer customer) throws Exception {
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, " +
                "Last_Update, Last_Updated_By, Division_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customer.getName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getPostalCode());
        ps.setString(4, customer.getPhone());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date now = new Date();
        ps.setString(5, formatter.format(now));
        ps.setString(6, Main.getUser().getUsername());
        ps.setString(7, formatter.format(now));
        ps.setString(8, Main.getUser().getUsername());
        ps.setInt(9, customer.getDivision().getId());
        ps.executeUpdate();
    }

    /**
     * Updates a customer to the customer table
     * @param customer Customer class with all relevant table data
     * @throws Exception for SQL error
     */
    public static void updateCustomer(Customer customer) throws Exception {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, " +
                "Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customer.getName());
        ps.setString(2, customer.getAddress());
        ps.setString(3, customer.getPostalCode());
        ps.setString(4, customer.getPhone());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date now = new Date();
        ps.setString(5, formatter.format(now));
        ps.setString(6, Main.getUser().getUsername());
        ps.setInt(7, customer.getDivision().getId());
        ps.setInt(8, customer.getId());
        ps.executeUpdate();
    }

    /**
     * Deletes a customer to the customer table
     * @param customer Customer class with all relevant table data
     * @throws Exception for SQL error or if customer has existing appointments
     */
    public static void deleteCustomer(Customer customer) throws Exception {
        if (getCountOfAppointments(customer) == 0) {
            String sql = "DELETE FROM customers WHERE Customer_ID = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, customer.getId());
            ps.executeUpdate();
        } else {
            throw new Exception("You must remove customer's appointments first.");
        }
    }

    /**
     * Adds an appointment to the appointments table
     * @param appt Appointment class with all relevant table data
     * @throws Exception for SQL error
     */
    public static void addAppointment(Appointment appt) throws Exception {
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, " +
                "Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, appt.getTitle());
        ps.setString(2, appt.getDescription());
        ps.setString(3, appt.getLocation());
        ps.setString(4, appt.getType());
        ps.setString(5, TimeFormatter.getTimeStringUTC(appt.getStart()));
        ps.setString(6, TimeFormatter.getTimeStringUTC(appt.getEnd()));
        Date now = new Date();
        ps.setString(7, TimeFormatter.getTimeStringUTC(now));
        ps.setString(8, Main.getUser().getUsername());
        ps.setString(9, TimeFormatter.getTimeStringUTC(now));
        ps.setString(10, Main.getUser().getUsername());
        ps.setInt(11, appt.getCustomerID());
        ps.setInt(12, appt.getUserID());
        ps.setInt(13, appt.getContactID());
        ps.executeUpdate();
    }

    /**
     * Updates an appointment to the appointments table
     * @param appt Appointment class with all relevant table data
     * @throws Exception for SQL error
     */
    public static void updateAppointment(Appointment appt) throws Exception {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, " +
                "End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, appt.getTitle());
        ps.setString(2, appt.getDescription());
        ps.setString(3, appt.getLocation());
        ps.setString(4, appt.getType());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        ps.setString(5, formatter.format(appt.getStart()));
        ps.setString(6, formatter.format(appt.getEnd()));
        Date now = new Date();
        ps.setString(7, formatter.format(now));
        ps.setString(8, Main.getUser().getUsername());
        ps.setInt(9, appt.getCustomerID());
        ps.setInt(10, appt.getUserID());
        ps.setInt(11, appt.getContactID());
        ps.setInt(12, appt.getId());
        ps.executeUpdate();
    }

    /**
     * Deletes an appointment to the appointments table
     * @param appointment Appointment class with all relevant table data
     * @throws Exception for SQL error
     */
    public static void deleteAppointment(Appointment appointment) throws Exception {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointment.getId());
        ps.executeUpdate();
    }

    /**
     * Pulls a list of all appointments in the appointments table
     * @return List of appointments
     */
    public static ObservableList<Appointment> getAllAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, " +
                "User_ID, Contact_ID FROM appointments";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date start = formatter.parse(rs.getString("Start"));
                Date end = formatter.parse(rs.getString("End"));
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");
                Contact contact = getContact(contactID);
                if (contact != null) {
                    Appointment appointment = new Appointment(id, title, description, location, type, start, end, customerID, contact, userID);
                    appointments.add(appointment);
                }
            }
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    /**
     * Determines if an appointments start date and end date conflicts with
     * any other appointments in the appointments table
     * @return true if there's an overlap, and false if not
     * @param startDate Appointment start time in format yyyy-MM-dd HH:mm:ss
     * @param endDate Appointment end time in format yyyy-MM-dd HH:mm:ss
     */
    public static boolean hasOverlapAppointments(String startDate, String endDate) {
        String sql = "SELECT COUNT(*) as total FROM appointments WHERE End > '" + startDate + "' AND Start < '" + endDate + "'";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            if (rs.next()) {
                int count = rs.getInt("total");
                return count > 0;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Pulls a list of all appointments associated with a specific contact
     * @return a list of appointments
     * @param contact Contact class contained in appointments
     */
    public static ObservableList<Appointment> getAppointmentsForContact(Contact contact) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, " +
                "User_ID FROM appointments WHERE Contact_ID = ?";
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, contact.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date start = formatter.parse(rs.getString("Start"));
                Date end = formatter.parse(rs.getString("End"));
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                Appointment appointment = new Appointment(id, title, description, location, type, start, end, customerID, contact, userID);
                appointments.add(appointment);
            }
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    /**
     * Pulls a list of all appointments associated with a specific customer
     * @return a list of appointments
     * @param customer Customer class contained in appointments
     */
    public static List<Appointment> getAppointmentsForCustomer(Customer customer) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Contact_ID, " +
                "User_ID FROM appointments WHERE Customer_ID = ?";
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, customer.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date start = formatter.parse(rs.getString("Start"));
                Date end = formatter.parse(rs.getString("End"));
                int contactID = rs.getInt("Contact_ID");
                Contact contact = getContact(contactID);
                int userID = rs.getInt("User_ID");
                if (contact != null) {
                    Appointment appointment = new Appointment(id, title, description, location, type, start, end, customer.getId(), contact, userID);
                    appointments.add(appointment);
                }
            }
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    /**
     * Attempts to get an appointments in progress
     * @return an appointment if one is in progress, null if not
     */
    public static Appointment getCurrentAppointment() {
        String now = TimeFormatter.getTimeStringUTC(new Date());
        String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, " +
                "User_ID, Contact_ID FROM appointments WHERE Start <= '" + now + "' AND End > '" + now + "'";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            if (rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date start = formatter.parse(rs.getString("Start"));
                Date end = formatter.parse(rs.getString("End"));
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");
                Contact contact = getContact(contactID);
                if (contact != null) {
                    return new Appointment(id, title, description, location, type, start, end, customerID, contact, userID);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Pulls a list of appointments starting within 15 minutes
     * @return a list of appointments
     */
    public static List<Appointment> getSoonAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        Date now = new Date();
        Date soon = new Date(now.getTime() + (15 * 60 * 1000));
        String nowString = TimeFormatter.getTimeStringUTC(now);
        String soonString = TimeFormatter.getTimeStringUTC(soon);
        String sql = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, Customer_ID, " +
                "User_ID, Contact_ID FROM appointments WHERE Start <= '" + soonString + "' AND Start > '" + nowString + "'";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("Appointment_ID");
                String title = rs.getString("Title");
                String description = rs.getString("Description");
                String location = rs.getString("Location");
                String type = rs.getString("Type");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date start = formatter.parse(rs.getString("Start"));
                Date end = formatter.parse(rs.getString("End"));
                int customerID = rs.getInt("Customer_ID");
                int userID = rs.getInt("User_ID");
                int contactID = rs.getInt("Contact_ID");
                Contact contact = getContact(contactID);
                if (contact != null) {
                    Appointment appointment = new Appointment(id, title, description, location, type, start, end, customerID, contact, userID);
                    appointments.add(appointment);
                }
            }
        } catch (SQLException | ParseException e) {
            System.out.println(e.getMessage());
        }
        return appointments;
    }

    /**
     * Gets a contact based on a Contact_ID from the contacts table
     * @return a list of appointments
     * @param id Contact_ID to pull remainder of contact data
     */
    static Contact getContact(int id) {
        String sql = "SELECT Contact_Name, Email FROM contacts WHERE Contact_ID = ?";
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String name = rs.getString("Contact_Name");
                String email = rs.getString("Email");
                return new Contact(id, name, email);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Pulls a list of all contacts from the contacts table
     * @return a list of contacts
     */
    public static ObservableList<Contact> getAllContacts() {
        ObservableList<Contact> contacts = FXCollections.observableArrayList();
        String sql = "SELECT Contact_ID, Contact_Name, Email FROM contacts";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("Contact_ID");
                String name = rs.getString("Contact_Name");
                String email = rs.getString("Email");
                contacts.add(new Contact(id, name, email));
            }
        } catch (SQLException e) {
            return contacts;
        }
        return contacts;
    }

    /**
     * Pulls a list of distinct appointment types
     * @return a list appointment types
     */
    public static ObservableList<String> getAllApptTypes() {
        ObservableList<String> types = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT Type FROM appointments";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                types.add(rs.getString("Type"));
            }
        } catch (SQLException e) {
            return types;
        }
        return types;
    }

    /**
     * Pulls a list of distinct years which contains appointments
     * @return a list of years
     */
    public static ObservableList<Integer> getAllApptYears() {
        ObservableList<Integer> years = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT YEAR(Start) AS Year FROM appointments";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                years.add(rs.getInt("Year"));
            }
        } catch (SQLException e) {
            return years;
        }
        return years;
    }

    /**
     * Gets number of appointments in a specific month, year, and type
     * @return number of appointments matching params
     * @param month month of appointments
     * @param year year of appointments
     * @param type appointments of this type
     */
    public static int getCountOfAppointments(String month, int year, String type) {
        SimpleDateFormat monthTextFormat = new SimpleDateFormat("MMMM");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(monthTextFormat.parse(month));
            SimpleDateFormat monthNumberFormat = new SimpleDateFormat("M");
            int monthNumber = Integer.parseInt(monthNumberFormat.format(cal.getTime()));
            String sql = "SELECT COUNT(*) AS Total FROM appointments WHERE Type = ? AND YEAR(Start) = ?" +
                    " AND MONTH(Start) = ?";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setString(1, type);
            ps.setInt(2, year);
            ps.setInt(3, monthNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("Total");
            } else {
                return 0;
            }
        } catch (SQLException | ParseException e) {
            return 0;
        }
    }

    /**
     * Pulls a list of all users from users table
     * @return a list users
     */
    public static ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT User_ID, User_Name FROM users";
        try {
            ResultSet rs = JDBC.connection.createStatement().executeQuery(sql);
            while (rs.next()) {
                int id = rs.getInt("User_ID");
                String name = rs.getString("User_Name");
                users.add(new User(id, name));
            }
        } catch (SQLException e) {
            return users;
        }
        return users;
    }

    /**
     * Gets number of appointments associated with a customer
     * @return number of appointments
     * @param customer Customer class to pull count of appointments
     */
    static int getCountOfAppointments(Customer customer) {
        String sql = "SELECT COUNT(*) AS total FROM appointments WHERE Customer_ID = ?";
        try {
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, customer.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

}
