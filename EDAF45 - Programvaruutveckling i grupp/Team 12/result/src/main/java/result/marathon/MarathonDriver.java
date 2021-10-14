package result.marathon;

import result.Driver;
import result.TimeEntry;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Marathon implementation of the Driver class.
 */
public class MarathonDriver extends Driver {
    public MarathonDriver(String startNumber) {
        super(startNumber);
    }

    public int getEndSize() {
        return end.size();
    }

    public int getStartSize() {
        return start.size();
    }

    public LocalTime getEnd() {
        if (!end.isEmpty()) {
            return end.get(0);
        }
         else {
            return null;
        }
    }

    public List<LocalTime> getAllStart() {
        List<LocalTime> tmp = start.subList(1, start.size());
        return tmp;
    }

    public List<LocalTime> getAllEnd() {
        List<LocalTime> tmp = end.subList(1, end.size());
        return tmp;
    }

}
