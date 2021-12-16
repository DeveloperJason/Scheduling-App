package scheduling.Model;

import javafx.util.StringConverter;

/**
 * Country class representing data for a row in the countries table
 * @author Jason Philpy
 */
public class Country {

    public static CountryStringConverter countryStringConverter = new CountryStringConverter();
    private int id;
    private String name;

    public Country(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class CountryStringConverter extends StringConverter<Country> {
        @Override
        public String toString(Country country) {
            return country.getName();
        }

        @Override
        public Country fromString(String s) {
            return null;
        }
    }
}
