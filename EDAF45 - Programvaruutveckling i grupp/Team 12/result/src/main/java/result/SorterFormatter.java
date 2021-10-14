package result;

public abstract class SorterFormatter implements ResultFormatter {


    public String formatDriver(Result driverResult) {
        StringBuilder sb = new StringBuilder();

        // before placement
        sb.append(SEP).append(" ");
        // start number
        sb.append(driverResult.getNumber()).append(SEP).append(" ");

        // driver name
        sb.append(driverResult.getName()).append(SEP).append(" ");

        sb.append(raceString(driverResult));

        return sb.toString();
    }

    protected abstract String raceString(Result driverResult);
}
