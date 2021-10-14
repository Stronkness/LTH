package result;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Raw data read from a time file, i.e. one start number and one clock time.
 */
public class TimeEntry {
    private final String number;
    private final LocalTime time;


    public TimeEntry(final String number, final LocalTime time) {
        this.number = number;
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(TimeEntry.class)) {
            return ((TimeEntry) obj).number.equals(number);
        }
        return false;
    }

    @Override
    public String toString() {
        return number + "; " + time.format(DateTimeFormatter.ofPattern("HH.mm.ss"));
    }

    public String getNumber() {
        return number;
    }

    public LocalTime getTime() {
        return time;
    }

}
