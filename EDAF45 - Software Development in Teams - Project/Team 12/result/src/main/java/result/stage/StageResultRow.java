package result.stage;

import result.ResultRow;
import result.TimeEntry;
import util.TimeUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StageResultRow extends ResultRow implements StageResult{

    List<LocalTime> starts;
    List<LocalTime> ends;

    public StageResultRow(String startNumber, String name, List<LocalTime> starts, List<LocalTime> end) {
        super(startNumber, name);
        this.starts = starts;
        this.ends = end;
    }


    @Override
    public List<String> getErrors() {
        return null;
    }



    public String getStart(int stage) {
        if(stage >= starts.size()) return "";

        return TimeUtils.formatTime(starts.get(stage));
    }

    public String getEnd(int stage) {
        if(stage >= ends.size()) return "";

        return TimeUtils.formatTime(ends.get(stage));
    }

    @Override
    public String getEnd() {
        return getEnd(ends.size()-1);
    }

    public String getStart() {
        return getStart(0);
    }



    @Override
    public String getTotal() {
        Duration sum = Duration.ZERO;

        for (int i = 0; i < getNumberOfStages(); i++) {

            Duration duration = durationBetween(i);
            sum = sum.plus(duration);

        }

        return TimeUtils.formatTime(sum);
    }

    public String getStage(int stage) {
        if(stage >= getNumberOfStages()){
            return "";
        }
        else{
            return TimeUtils.formatTime(durationBetween(stage));
        }
    }

    private Duration durationBetween(int stage){
        return Duration.between(starts.get(stage), ends.get(stage));
    }

    //todo could be incorrect
    @Override
    public int getNumberOfStages() {
        return Math.max(ends.size(), starts.size());
    }


}
