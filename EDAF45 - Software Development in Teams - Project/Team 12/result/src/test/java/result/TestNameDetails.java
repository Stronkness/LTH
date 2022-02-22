package result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.FileRead;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestNameDetails {
    private NameDetails nameDetails;
    private FileRead fileRead;

    @BeforeEach
    public void setUp(){
        nameDetails = new NameDetails();
        fileRead = new FileRead();
    }

    @Test
    public void testAddClassDetails() {
        nameDetails.addClassName("SENIOR");
        nameDetails.addClassName("SENIOR");
        assertEquals(1, nameDetails.getClassDetailsSet().size());
        nameDetails.addClassName("JUNIOR");
        assertEquals(2, nameDetails.getClassDetailsSet().size());
    }

    @Test
    public void testOrderOfClassDetails() {
        nameDetails.addClassName("SENIOR");
        nameDetails.addClassName("JUNIOR");
        nameDetails.addClassName("BABY");

        nameDetails.addNameDetails("1", "Anders", "SENIOR");
        nameDetails.addNameDetails("2", "Bengt", "JUNIOR");
        nameDetails.addNameDetails("3", "Bebis", "BABY");

        List<String> klasser = new ArrayList<>();
        klasser.add("SENIOR");
        klasser.add("JUNIOR");
        klasser.add("BABY");


        List<String> startnr = new ArrayList<>();
        startnr.add("1");
        startnr.add("2");
        startnr.add("3");
        Set<Map.Entry<String, List<String>>> setOfDetails = nameDetails.getClassDetailsSet();

        int x = 0;
        for(Map.Entry<String, List<String>> temp : setOfDetails) {
            assertEquals(klasser.get(x), temp.getKey());
            assertEquals(startnr.get(x), temp.getValue().get(0));
            x++;
        }
    }


    @Test
    public void testColumnNames(){
        fileRead.readNameFile(nameDetails, "../Acceptanstester/Maraton/acceptanstestM1_M2/namnfil.txt");
        assertEquals(2, nameDetails.getColumnNames().size());
    }

    @Test
    public void testListOfDrivers() {
        fileRead.readNameFile(nameDetails, "../Acceptanstester/Maraton/acceptanstestM1_M2/namnfil.txt");
        assertEquals(5, nameDetails.getDriverEntries().size());
    }

    @Test
    public void testOrderColumnNames() {
        fileRead.readNameFile(nameDetails, "../Acceptanstester/Maraton/acceptanstestM1_M2/namnfil.txt");
        List<String> list = nameDetails.getColumnNames();
        assertEquals("StartNr", list.get(0));
        assertEquals("Namn", list.get(1));
    }

    @Test
    public void testOrderListOfDrivers() {
        fileRead.readNameFile(nameDetails, "../Acceptanstester/Maraton/acceptanstestM1_M2/namnfil.txt");
        List<DriverEntry> list = nameDetails.getDriverEntries();
        List<DriverEntry> temp = new LinkedList<>();
        temp.add(new DriverEntry("1", "Anders Asson"));
        temp.add(new DriverEntry("2", "Bengt Bsson"));
        temp.add(new DriverEntry("3", "Chris Csson"));
        temp.add(new DriverEntry("4", "David Dsson"));
        temp.add(new DriverEntry("5", "Erik Esson"));
        for (int i = 0; i < temp.size(); i++) {
            assertEquals(temp.get(i).getNumber(), list.get(i).getNumber());
            assertEquals(temp.get(i).getName(), list.get(i).getName());
        }
    }

}
