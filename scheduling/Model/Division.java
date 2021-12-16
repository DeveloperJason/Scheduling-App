package scheduling.Model;

import javafx.util.StringConverter;

/**
 * Division class representing data for a row in the first_level_divisions table
 * @author Jason Philpy
 */
public class Division {

    public static Division.DivisionStringConverter divisionStringConverter = new Division.DivisionStringConverter();
    private int id;
    private String name;
    private int countryID;

    public Division(int id, String name, int countryID) {
        this.id = id;
        this.name = name;
        this.countryID = countryID;
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

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
    public static class DivisionStringConverter extends StringConverter<Division> {
        @Override
        public String toString(Division division) {
            return division.getName();
        }

        @Override
        public Division fromString(String s) {
            return null;
        }
    }
}
