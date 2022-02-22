package result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import result.marathon.MarathonMatcher;
import result.marathon.MarathonResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestAbstractMatcher {

    private MarathonMatcher matcher;
    private List<DriverEntry> drivers;
    private List<MarathonResult> result;

    @BeforeEach
    public void setup() {
        matcher = new MarathonMatcher();
        drivers = new ArrayList<>();
    }


    @Test
    public void testDriverNames() {

        String name1 = "Adam Asson";
        String name2 = "Bodil Bsson";
        String name3 = "Caesar Csson";
        drivers.add(new DriverEntry("01", name1));
        drivers.add(new DriverEntry("02", name2));
        drivers.add(new DriverEntry("03", name3));

        matcher.addDrivers(drivers);

        result = matcher.result();

        assertEquals(3, result.size());

        assertEquals(name1, result.get(0).getName());
        assertEquals(name2, result.get(1).getName());
        assertEquals(name3, result.get(2).getName());

    }

    @Test
    public void testUnorderedDriverNames() {

        String name1 = "Adam Asson";
        String name2 = "Bodil Bsson";
        String name3 = "Caesar Csson";
        drivers.add(new DriverEntry("02", name2));
        drivers.add(new DriverEntry("03", name3));
        drivers.add(new DriverEntry("01", name1));

        matcher.addDrivers(drivers);

        result = matcher.result();

        assertEquals(3, result.size());

        assertEquals(name1, result.get(0).getName());
        assertEquals(name2, result.get(1).getName());
        assertEquals(name3, result.get(2).getName());

    }

}
