package result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.marathon.MarathonMatcher;
import result.marathon.MarathonResult;
import result.varvlopp.LapFormatter;
import result.varvlopp.LapMatcher;
import result.varvlopp.LapResult;
import result.varvlopp.LapSorter;
import util.FileRead;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMultipleEndFiles {

    private LapMatcher lapMatcher;
    private LapFormatter formatter;
    private List<DriverEntry> drivers;
    private RegTime times;
    private List<LapResult> result;
    private NameDetails nameDetails;
    private FileRead fileRead;

    @BeforeEach
    public void setup() {
        lapMatcher = new LapMatcher("20:00:00");
        formatter = new LapFormatter();
        drivers = new ArrayList<>();
        times = new RegTime();
        nameDetails = new NameDetails();
        fileRead = new FileRead();

    }


    @Test
    public void testSort() {

        fileRead.readStartFile(times, "../Acceptanstester/Varvlopp/acceptanstestV5/starttider.txt");
        fileRead.readEndFile(times, "../Acceptanstester/Varvlopp/acceptanstestV5/maltider2.txt");
        fileRead.readEndFile(times, "../Acceptanstester/Varvlopp/acceptanstestV5/maltider1.txt");
        times.sort();
        fileRead.readNameFile(nameDetails, "../Acceptanstester/Varvlopp/acceptanstestV5/namnfil.txt");

        lapMatcher.addStartTimes(times.getStartTimes());

        lapMatcher.addEndTimes(times.getEndTimes());

        lapMatcher.addDrivers(nameDetails.getDriverEntries());

        lapMatcher.cleanUpDrivers(nameDetails.getStartNumbers());


        result = lapMatcher.result();

        int max = lapMatcher.maxLaps();


        SorterHandler sorter = new LapSorter(max);
        List<String> sortedResults = sorter.sortAndFormat(result, nameDetails.getClassDetailsSet());

        String expectedResult1 = "SENIOR";
        String expectedResult2 = "Place; StartNr; Namn; #Varv; Totaltid; Varv1; Varv2; Varv3";
        String expectedResult4 = "2; 1; Anders Asson; 3; 01:23:34; 00:30:00; 00:25:00; 00:28:34";
        String expectedResult3 = "1; 2; Bengt Bsson; 3; 00:59:16; 00:21:00; 00:20:00; 00:18:16";
        String expectedResult5 = "JUNIOR";
        String expectedResult6 = "Place; StartNr; Namn; #Varv; Totaltid; Varv1; Varv2; Varv3";
        String expectedResult7 = "1; 101; Chris Csson; 3; 01:05:06; 00:22:00; 00:20:00; 00:23:06";
        String expectedResult8 = "2; 102; David Dsson; 3; 01:12:07; 00:23:00; 00:20:00; 00:29:07";
        String expectedResult9 = "; 103; Erik Esson; 2; 00:44:00; 00:24:00; 00:20:00; ";


        assertEquals(expectedResult1, sortedResults.get(0));
        assertEquals(expectedResult2, sortedResults.get(1));
        assertEquals(expectedResult3, sortedResults.get(2));
        assertEquals(expectedResult4, sortedResults.get(3));
        assertEquals(expectedResult5, sortedResults.get(4));
        assertEquals(expectedResult6, sortedResults.get(5));
        assertEquals(expectedResult7, sortedResults.get(6));
        assertEquals(expectedResult8, sortedResults.get(7));
        assertEquals(expectedResult9, sortedResults.get(8));


    }

    @Test
    public void testJSON() {
        fileRead.readStartFile(times, "../Acceptanstester/Varvlopp/acceptanstestV5/starttider.txt");

        String endFile = "../Acceptanstester/Varvlopp/acceptanstestV5/maltider2.txt;../Acceptanstester/Varvlopp/acceptanstestV5/maltider1.txt";
        String[] endFiles = endFile.split(";");
        for(int i = 0;i < endFiles.length; i++) {
            fileRead.readEndFile(times,endFiles[i]);
        }

        times.sort();
        fileRead.readNameFile(nameDetails, "../Acceptanstester/Varvlopp/acceptanstestV5/namnfil.txt");

        lapMatcher.addStartTimes(times.getStartTimes());

        lapMatcher.addEndTimes(times.getEndTimes());

        lapMatcher.addDrivers(nameDetails.getDriverEntries());

        lapMatcher.cleanUpDrivers(nameDetails.getStartNumbers());

        result = lapMatcher.result();

        int max = lapMatcher.maxLaps();


        SorterHandler sorter = new LapSorter(max);
        List<String> sortedResults = sorter.sortAndFormat(result, nameDetails.getClassDetailsSet());

        String expectedResult1 = "SENIOR";
        String expectedResult2 = "Place; StartNr; Namn; #Varv; Totaltid; Varv1; Varv2; Varv3";
        String expectedResult4 = "2; 1; Anders Asson; 3; 01:23:34; 00:30:00; 00:25:00; 00:28:34";
        String expectedResult3 = "1; 2; Bengt Bsson; 3; 00:59:16; 00:21:00; 00:20:00; 00:18:16";
        String expectedResult5 = "JUNIOR";
        String expectedResult6 = "Place; StartNr; Namn; #Varv; Totaltid; Varv1; Varv2; Varv3";
        String expectedResult7 = "1; 101; Chris Csson; 3; 01:05:06; 00:22:00; 00:20:00; 00:23:06";
        String expectedResult8 = "2; 102; David Dsson; 3; 01:12:07; 00:23:00; 00:20:00; 00:29:07";
        String expectedResult9 = "; 103; Erik Esson; 2; 00:44:00; 00:24:00; 00:20:00; ";


        assertEquals(expectedResult1, sortedResults.get(0));
        assertEquals(expectedResult2, sortedResults.get(1));
        assertEquals(expectedResult3, sortedResults.get(2));
        assertEquals(expectedResult4, sortedResults.get(3));
        assertEquals(expectedResult5, sortedResults.get(4));
        assertEquals(expectedResult6, sortedResults.get(5));
        assertEquals(expectedResult7, sortedResults.get(6));
        assertEquals(expectedResult8, sortedResults.get(7));
        assertEquals(expectedResult9, sortedResults.get(8));


}



}
