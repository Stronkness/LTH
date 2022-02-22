package result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.stage.StageFormatter;
import result.stage.StageMatcher;
import result.stage.StageResult;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStageFormat {
    private List<DriverEntry> drivers;
    private StageFormatter formatter;
    private List<TimeEntry> startTimes;
    private List<TimeEntry> endTimes;
    private Matcher matcher;
    private List<StageResult> result;


    @BeforeEach
    public void setup() {
        matcher = new StageMatcher();
        drivers = new ArrayList<>();
        formatter = new StageFormatter();
        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();

        drivers.add(new DriverEntry("01", "Adam Asson"));
        drivers.add(new DriverEntry("02", "Bodil Bsson"));
        drivers.add(new DriverEntry("03", "Caesar Csson"));
        matcher.addDrivers(drivers);
    }

    @Test
    public void TestTwoStages() {
        startTimes.add(new TimeEntry("01", LocalTime.parse("12:00:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("13:00:00")));
        startTimes.add(new TimeEntry("01", LocalTime.parse("14:00:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("15:00:00")));

        startTimes.add(new TimeEntry("02", LocalTime.parse("12:01:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("13:01:00")));
        startTimes.add(new TimeEntry("02", LocalTime.parse("14:01:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("15:01:00")));

        startTimes.add(new TimeEntry("03", LocalTime.parse("12:02:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("13:02:00")));
        startTimes.add(new TimeEntry("03", LocalTime.parse("14:02:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("15:02:00")));

        matcher.addStartTimes(startTimes);
        matcher.addEndTimes(endTimes);

        int stages = ((StageMatcher) matcher).maxStages();
        formatter.setMaxStages(stages);

        result = matcher.result();
        //StartNr; Namn; Klubb; MC; Totaltid; #Etapper; Etapp1; Etapp2; ...; Start1; Mål1; Start2; Mål2; ...
        String expectedResult1 = "01; Adam Asson; 02:00:00; 2; 01:00:00; 01:00:00; 12:00:00; 13:00:00; 14:00:00; 15:00:00";
        String expectedResult2 = "02; Bodil Bsson; 02:00:00; 2; 01:00:00; 01:00:00; 12:01:00; 13:01:00; 14:01:00; 15:01:00";
        String expectedResult3 = "03; Caesar Csson; 02:00:00; 2; 01:00:00; 01:00:00; 12:02:00; 13:02:00; 14:02:00; 15:02:00";

        assertEquals(expectedResult1, formatter.formatDriver(result.get(0)));
        assertEquals(expectedResult2, formatter.formatDriver(result.get(1)));
        assertEquals(expectedResult3, formatter.formatDriver(result.get(2)));

    }


    @Test
    public void TestDifferentStages() {
        startTimes.add(new TimeEntry("01", LocalTime.parse("12:00:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("13:00:00")));
        startTimes.add(new TimeEntry("01", LocalTime.parse("14:00:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("15:00:00")));
        startTimes.add(new TimeEntry("01", LocalTime.parse("16:00:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("17:00:00")));

        startTimes.add(new TimeEntry("02", LocalTime.parse("12:01:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("13:01:00")));
        startTimes.add(new TimeEntry("02", LocalTime.parse("14:01:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("15:01:00")));
        startTimes.add(new TimeEntry("02", LocalTime.parse("16:01:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("17:01:00")));

        startTimes.add(new TimeEntry("03", LocalTime.parse("12:02:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("13:02:00")));
        startTimes.add(new TimeEntry("03", LocalTime.parse("14:02:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("15:02:00")));

        matcher.addStartTimes(startTimes);
        matcher.addEndTimes(endTimes);

        int stages = ((StageMatcher) matcher).maxStages();
        formatter.setMaxStages(stages);

        result = matcher.result();
        //StartNr; Namn; Klubb; MC; Totaltid; #Etapper; Etapp1; Etapp2; ...; Start1; Mål1; Start2; Mål2; ...
        String expectedResult1 = "01; Adam Asson; 03:00:00; 3; 01:00:00; 01:00:00; 01:00:00; 12:00:00; 13:00:00; 14:00:00; 15:00:00; 16:00:00; 17:00:00";
        String expectedResult2 = "02; Bodil Bsson; 03:00:00; 3; 01:00:00; 01:00:00; 01:00:00; 12:01:00; 13:01:00; 14:01:00; 15:01:00; 16:01:00; 17:01:00";
        String expectedResult3 = "03; Caesar Csson; 02:00:00; 2; 01:00:00; 01:00:00; ; 12:02:00; 13:02:00; 14:02:00; 15:02:00; ; ";

        assertEquals(expectedResult1, formatter.formatDriver(result.get(0)));
        assertEquals(expectedResult2, formatter.formatDriver(result.get(1)));
        assertEquals(expectedResult3, formatter.formatDriver(result.get(2)));

    }
}
