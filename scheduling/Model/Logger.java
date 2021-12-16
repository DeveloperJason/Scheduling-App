package scheduling.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class responsible for writing to a file
 * @author Jason Philpy
 */
public class Logger {

    /**
     * Creates login_activity.txt file if it doesn't exist, then writes a message to it
     * @param message message to write to file
     */
    public static void writeToActivityLog(String message) {
        File logFile = new File("login_activity.txt");
        try {
            if (logFile.createNewFile()) {
                System.out.println("Log file created.");
            }
            FileWriter writer = new FileWriter("login_activity.txt", true);
            writer.write(message);
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}
