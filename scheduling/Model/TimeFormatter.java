package scheduling.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

/**
 * Creates dates or formats them into strings based on local, UTC, or EST
 * @author Jason Philpy
 */
public class TimeFormatter  {

    private static final SimpleDateFormat formatter = new SimpleDateFormat();
    private static final String SERVER_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DISPLAY_FORMAT = "MM/dd/yy h:mm a";

    /**
     * Takes a date and provides a string in timestamp format in the UTC timezone
     * @param date Date to convert
     * @return String of time in timestamp format
     */
    public static String getTimeStringUTC(Date date) {
        formatter.applyPattern(SERVER_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    /**
     * Takes a date and provides a string in displayable format and in the local timezone
     * @param date Date to convert
     * @return string of date in readable format
     */
    public static String getTimeStringForDisplay(Date date) {
        formatter.applyPattern(DISPLAY_FORMAT);
        formatter.setTimeZone(TimeZone.getDefault());
        return formatter.format(date);
    }

    /**
     * Takes a string and converts it to a date in EST timezone
     * @param dateString date as string in format of MM/dd/yy h:mm a
     * @return the converted Date
     */
    public static Date getESTDateFromString(String dateString) {
        formatter.applyPattern(DISPLAY_FORMAT);
        formatter.setTimeZone(TimeZone.getTimeZone(ZoneId.of("America/New_York")));
        try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return new Date();
        }
    }

}
