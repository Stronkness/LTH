package result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.marathon.MarathonFormatter;
import result.marathon.MarathonMatcher;
import result.marathon.MarathonResult;
import result.marathon.MarathonSorterHandler;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMarathonFormatterAndSorter {

    private MarathonMatcher marathonMatcher;
    private MarathonFormatter formatter;
    private List<DriverEntry> drivers;
    private List<TimeEntry> startTimes;
    private List<TimeEntry> endTimes;
    private List<MarathonResult> result;
    private NameDetails nameDetails;

    @BeforeEach
    public void setup() {
        marathonMatcher = new MarathonMatcher();
        formatter = new MarathonFormatter();
        drivers = new ArrayList<>();
        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();
        nameDetails = new NameDetails();
    }

    @Test
    public void testMissingStart() {
        drivers.add(new DriverEntry("01", "Adam Asson"));
        drivers.add(new DriverEntry("02", "Bodil Bsson"));
        drivers.add(new DriverEntry("03", "Caesar Csson"));
        startTimes.add(new TimeEntry("01", LocalTime.parse("12:00:00")));
        // missing start time
        //startTimes.add(new TimeEntry("02", LocalTime.parse("12:01:00")));
        startTimes.add(new TimeEntry("03", LocalTime.parse("12:02:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("13:00:00")));

        marathonMatcher.addDrivers(drivers);
        marathonMatcher.addStartTimes(startTimes);
        marathonMatcher.addEndTimes(endTimes);

        result = marathonMatcher.result();

        assertEquals(3, result.size());

        String expectedResult1 = "01; Adam Asson; 01:00:00; 12:00:00; 13:00:00; ";
        String expectedResult2 = "02; Bodil Bsson; --:--:--; Start?; 13:00:00; ";
        String expectedResult3 = "03; Caesar Csson; 00:58:00; 12:02:00; 13:00:00; ";

        assertEquals(expectedResult1, formatter.formatDriver(result.get(0)));
        assertEquals(expectedResult2, formatter.formatDriver(result.get(1)));
        assertEquals(expectedResult3, formatter.formatDriver(result.get(2)));

    }

    @Test
    public void testClassName() {
        drivers.add(new DriverEntry("01", "Adam Asson"));
        drivers.add(new DriverEntry("02", "Bodil Bsson"));
        drivers.add(new DriverEntry("03", "Caesar Csson"));

        startTimes.add(new TimeEntry("01", LocalTime.parse("12:00:00")));
        startTimes.add(new TimeEntry("02", LocalTime.parse("12:02:00")));
        startTimes.add(new TimeEntry("03", LocalTime.parse("12:02:00")));



        endTimes.add(new TimeEntry("01", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("13:00:00")));

        marathonMatcher.addDrivers(drivers);
        marathonMatcher.addStartTimes(startTimes);
        marathonMatcher.addEndTimes(endTimes);

        result = marathonMatcher.result();

        assertEquals(3, result.size());

        String expectedResult1 = "01; Adam Asson; 01:00:00; 12:00:00; 13:00:00; ";
        String expectedResult2 = "02; Bodil Bsson; 00:58:00; 12:02:00; 13:00:00; ";
        String expectedResult3 = "03; Caesar Csson; 00:58:00; 12:02:00; 13:00:00; ";

        assertEquals(expectedResult1, formatter.formatDriver(result.get(0)));
        assertEquals(expectedResult2, formatter.formatDriver(result.get(1)));
        assertEquals(expectedResult3, formatter.formatDriver(result.get(2)));

    }

    @Test
    public void testMissingEnd() {
        drivers.add(new DriverEntry("01", "Adam Asson"));
        drivers.add(new DriverEntry("02", "Bodil Bsson"));
        drivers.add(new DriverEntry("03", "Caesar Csson"));
        startTimes.add(new TimeEntry("01", LocalTime.parse("12:00:00")));
        startTimes.add(new TimeEntry("02", LocalTime.parse("12:01:00")));
        startTimes.add(new TimeEntry("03", LocalTime.parse("12:02:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("13:00:00")));
        // missing end time
        // endTimes.add(new TimeEntry("02", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("13:00:00")));

        marathonMatcher.addDrivers(drivers);
        marathonMatcher.addStartTimes(startTimes);
        marathonMatcher.addEndTimes(endTimes);

        result = marathonMatcher.result();

        assertEquals(3, result.size());

        String expectedResult1 = "01; Adam Asson; 01:00:00; 12:00:00; 13:00:00; ";
        String expectedResult2 = "02; Bodil Bsson; --:--:--; 12:01:00; Slut?; ";
        String expectedResult3 = "03; Caesar Csson; 00:58:00; 12:02:00; 13:00:00; ";

        assertEquals(expectedResult1, formatter.formatDriver(result.get(0)));
        assertEquals(expectedResult2, formatter.formatDriver(result.get(1)));
        assertEquals(expectedResult3, formatter.formatDriver(result.get(2)));

    }

    @Test
    public void testMultipleMissingTimes() {
        drivers.add(new DriverEntry("01", "Adam Asson"));
        drivers.add(new DriverEntry("02", "Bodil Bsson"));
        drivers.add(new DriverEntry("03", "Caesar Csson"));
//        startTimes.add(new TimeEntry("01", LocalTime.parse("12:00:00")));
//        startTimes.add(new TimeEntry("02", LocalTime.parse("12:01:00")));
        startTimes.add(new TimeEntry("03", LocalTime.parse("12:02:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("13:00:00")));
        // missing end time
        // endTimes.add(new TimeEntry("02", LocalTime.parse("13:00:00")));
//        endTimes.add(new TimeEntry("03", LocalTime.parse("13:00:00")));

        marathonMatcher.addDrivers(drivers);
        marathonMatcher.addStartTimes(startTimes);
        marathonMatcher.addEndTimes(endTimes);

        result = marathonMatcher.result();

        assertEquals(3, result.size());

        String expectedResult1 = "01; Adam Asson; --:--:--; Start?; 13:00:00; ";
        String expectedResult2 = "02; Bodil Bsson; --:--:--; Start?; Slut?; ";
        String expectedResult3 = "03; Caesar Csson; --:--:--; 12:02:00; Slut?; ";

        assertEquals(expectedResult1, formatter.formatDriver(result.get(0)));
        assertEquals(expectedResult2, formatter.formatDriver(result.get(1)));
        assertEquals(expectedResult3, formatter.formatDriver(result.get(2)));

    }


    @Test
    public void testSortCompleteList(){
        nameDetails.addClassName("Junior");
        nameDetails.addClassName("Senior");
        nameDetails.addNameDetails("01", "Adam Asson","Junior");
        nameDetails.addNameDetails("02", "Bodil Bsson","Senior");
        nameDetails.addNameDetails("03", "Caesar Csson", "Junior");
        nameDetails.addNameDetails("04", "David Dsson","Senior");
        startTimes.add(new TimeEntry("01", LocalTime.parse("12:00:00")));
        startTimes.add(new TimeEntry("02", LocalTime.parse("12:01:00")));
        startTimes.add(new TimeEntry("03", LocalTime.parse("12:02:00")));
        startTimes.add(new TimeEntry("04", LocalTime.parse("12:03:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("04", LocalTime.parse("13:00:00")));


        marathonMatcher.addDrivers(nameDetails.getDriverEntries());
        marathonMatcher.addStartTimes(startTimes);
        marathonMatcher.addEndTimes(endTimes);

        result = marathonMatcher.result();

        SorterHandler sorter = new MarathonSorterHandler();
        List<String> sortedResults = sorter.sortAndFormat(result,nameDetails.getClassDetailsSet());

        String expectedResult0 = "Junior";
        String expectedTitle = "Place; StartNbr; Namn; Totaltid; ";
        String expectedResult1 = "1; 03; Caesar Csson; 00:58:00";
        String expectedResult2 = "2; 01; Adam Asson; 01:00:00";
        String expectedResult01 = "Senior";
        String expectedResult3 = "1; 04; David Dsson; 00:57:00";
        String expectedResult4 = "2; 02; Bodil Bsson; 00:59:00";

        assertEquals(expectedResult0, sortedResults.get(0));
        assertEquals(expectedTitle, sortedResults.get(1));
        assertEquals(expectedResult1, sortedResults.get(2));
        assertEquals(expectedResult2, sortedResults.get(3));
        assertEquals(expectedResult01, sortedResults.get(4));
        assertEquals(expectedTitle, sortedResults.get(5));
        assertEquals(expectedResult3, sortedResults.get(6));
        assertEquals(expectedResult4, sortedResults.get(7));


    }

    @Test
    public void testSortIncompleteList(){
        nameDetails.addClassName("Junior");
        nameDetails.addClassName("Senior");
        nameDetails.addNameDetails("01", "Adam Asson","Junior");
        nameDetails.addNameDetails("02", "Bodil Bsson","Senior");
        nameDetails.addNameDetails("03", "Caesar Csson", "Junior");
        nameDetails.addNameDetails("04", "David Dsson","Senior");
        nameDetails.addNameDetails("05", "Erik Esson", "Junior");
        startTimes.add(new TimeEntry("01", LocalTime.parse("12:00:00")));
        startTimes.add(new TimeEntry("02", LocalTime.parse("12:01:00")));
        startTimes.add(new TimeEntry("03", LocalTime.parse("12:02:00")));
        startTimes.add(new TimeEntry("04", LocalTime.parse("12:03:00")));
        startTimes.add(new TimeEntry("05", LocalTime.parse("12:04:00")));
        endTimes.add(new TimeEntry("01", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("02", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("03", LocalTime.parse("13:00:00")));
        endTimes.add(new TimeEntry("04", LocalTime.parse("13:00:00")));


        marathonMatcher.addDrivers(nameDetails.getDriverEntries());
        marathonMatcher.addStartTimes(startTimes);
        marathonMatcher.addEndTimes(endTimes);

        result = marathonMatcher.result();

        SorterHandler sorter = new MarathonSorterHandler();

        List<String> sortedResults = sorter.sortAndFormat(result,nameDetails.getClassDetailsSet());

        String expectedResult1 = "1; 03; Caesar Csson; 00:58:00";
        String expectedResult2 = "2; 01; Adam Asson; 01:00:00";
        String expectedResult3 = "; 05; Erik Esson; --:--:--";
        String expectedResult4 = "1; 04; David Dsson; 00:57:00";
        String expectedResult5 = "2; 02; Bodil Bsson; 00:59:00";

        //ignoring classes

        assertEquals(expectedResult1, sortedResults.get(2));
        assertEquals(expectedResult2, sortedResults.get(3));
        assertEquals(expectedResult3, sortedResults.get(4));
        assertEquals(expectedResult4, sortedResults.get(7));
        assertEquals(expectedResult5, sortedResults.get(8));
    }

}
