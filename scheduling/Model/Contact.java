package scheduling.Model;

import javafx.util.StringConverter;

/**
 * Contact class representing data for a row in the contacts table
 * @author Jason Philpy
 */
public class Contact {
    public static Contact.ContactStringConverter contactStringConverter = new Contact.ContactStringConverter();
    private int id;
    private String name;
    private String email;

    public Contact(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static class ContactStringConverter extends StringConverter<Contact> {
        @Override
        public String toString(Contact contact) {
            return contact.getName();
        }

        @Override
        public Contact fromString(String s) {
            return null;
        }
    }
}
