package result;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class RegTime {
    private List<TimeEntry> start, end;

    public RegTime(){
        start = new LinkedList<>();
        end = new LinkedList<>();
    }

    private void putTime(TimeEntry entry, List<TimeEntry> list) {
        list.add(entry);
    }
    public void putStart(String number, String time){
        putTime(createTimeEntry(number, time), start);
    }

    public void putEnd(String number, String time){
        putTime(createTimeEntry(number, time), end);
    }

    public List<TimeEntry> getStartTimes(){
        return start;
    }

    public List<TimeEntry> getEndTimes(){
        return end;
    }

    public int startSize(){
        return start.size();
    }

    public int endSize(){
        return end.size();
    }

    public void sort() {
        end.sort(Comparator.comparing(timeEntry -> timeEntry.getTime()));
    }

    private TimeEntry createTimeEntry(String number, String time){
        String[] parts = time.split(":");
        TimeEntry entry = new TimeEntry(number, LocalTime.of(numberCleanup(parts[0]), numberCleanup(parts[1]), numberCleanup(parts[2])));
        return entry;
    }

    private int numberCleanup(String time){
        if(time.charAt(0) == '0'){
            return Integer.parseInt(Character.toString(time.charAt(1)));
        }
        return Integer.parseInt(time);
    }

}
