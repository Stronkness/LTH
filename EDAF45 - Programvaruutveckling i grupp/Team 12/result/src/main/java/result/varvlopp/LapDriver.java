package result.varvlopp;

import result.Driver;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LapDriver extends Driver {
    public LapDriver(String startNumber) {
        super(startNumber);
    }

    public List<LocalTime> getEnd() {
        return end;
    }

    public int getTotalLaps() {
        return end.size();
    }
}
