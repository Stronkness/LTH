package result;

/**
 * Raw data read from name file, i.e. one start number and one driver name.
 */
public class DriverEntry {

    private final String number;
    private final String name;

    public DriverEntry(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

}
