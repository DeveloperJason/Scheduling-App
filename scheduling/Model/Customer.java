package scheduling.Model;

import javafx.util.StringConverter;

/**
 * Customer class representing data for a row in the customers table
 * @author Jason Philpy
 */
public class Customer {
    public static Customer.CustomerStringConverter customerStringConverter = new Customer.CustomerStringConverter();
    private int id;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private Division division;
    private Country country;

    public Customer(int id, String name, String address, String postalCode, String phone, Division division, Country country) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.division = division;
        this.country = country;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public static class CustomerStringConverter extends StringConverter<Customer> {
        @Override
        public String toString(Customer customer) {
            return customer.getName();
        }

        @Override
        public Customer fromString(String s) {
            return null;
        }
    }
}
