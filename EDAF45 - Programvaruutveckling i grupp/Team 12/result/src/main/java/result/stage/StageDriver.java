package result.stage;

import result.Driver;

import java.time.LocalTime;
import java.util.List;

public class StageDriver extends Driver {
    private String mc;
    private String club;

    public StageDriver(String startNumber) {
        super(startNumber);
    }

    public List<LocalTime> getStarts() {
        return start;
    }


    public List<LocalTime> getEnd() {
        return end;
    }

    public int totalStages(){
        return start.size();
    }
}
