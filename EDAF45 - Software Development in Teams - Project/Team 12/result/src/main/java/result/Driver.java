package result;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for raw data read from files, pertaining to a specific driver.
 * Sub classes for different driver types, e.g. MarathonDriver.
 */
public abstract class Driver {

    protected final String startNumber;
    private String name;
    protected List<LocalTime> start, end;

    public Driver(String startNumber) {
        this.startNumber = startNumber;
        start = new ArrayList<>();
        end = new ArrayList<>();
    }

    public void setStart(LocalTime newStart) {
        start.add(newStart);
    }

    
    public LocalTime getStart() {
        if (!start.isEmpty()) {
            return start.get(0);
        }
        else {
            return null;
        }
    }
    
    

    public void setEnd(LocalTime newEnd) {
        end.add(newEnd);
    }

    public String getStartNumber() {
        return startNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
