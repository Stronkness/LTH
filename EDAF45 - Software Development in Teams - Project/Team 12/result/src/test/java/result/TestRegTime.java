package result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class TestRegTime {
    private RegTime time;

    @BeforeEach
    public void setUp(){
        time = new RegTime();
    }

    @Test
    public void testStartTimes(){
        time.putStart("1", "12:00:00");
        time.putStart("2", "12:01:00");
        time.putStart("3", "12:02:00");
        assertEquals(3, time.startSize());
    }

    @Test
    public void testEndTimes(){
        time.putEnd("1", "12:00:00");
        time.putEnd("2", "12:01:00");
        time.putEnd("3", "12:02:00");
        assertEquals(3, time.endSize());
    }

    @Test
    public void testStartOrder(){
        time.putStart("1", "12:00:00");
        time.putStart("2", "12:01:00");
        time.putStart("3", "12:02:00");
        List<TimeEntry> start = time.getStartTimes();
        List<TimeEntry> temp = new LinkedList<TimeEntry>();
        temp.add(new TimeEntry("1", LocalTime.of(12,00,00,00)));
        temp.add(new TimeEntry("2", LocalTime.of(12,01,00,00)));
        temp.add(new TimeEntry("3", LocalTime.of(12,02,00,00)));
        for(int i = 0; i < start.size(); i++){
            assertTrue(start.get(i).equals(temp.get(i)));
        }
    }

    @Test
    public void testEndOrder(){
        List<TimeEntry> end = time.getEndTimes();
        time.putEnd("1", "12:00:00");
        time.putEnd("2", "12:01:00");
        time.putEnd("3", "12:02:00");
        List<TimeEntry> temp = new LinkedList<TimeEntry>();
        temp.add(new TimeEntry("1", LocalTime.of(12,00,00,00)));
        temp.add(new TimeEntry("2", LocalTime.of(12,01,00,00)));
        temp.add(new TimeEntry("3", LocalTime.of(12,02,00,00)));
        for(int i = 0; i < end.size(); i++){
            assertTrue(end.get(i).equals(temp.get(i)));
        }

    }
}
