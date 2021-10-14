package result.marathon;

import result.Result;
import result.SorterFormatter;

public class MarathonSorterFormatter extends SorterFormatter {


    @Override
    protected String raceString(Result driverResult) {
        MarathonResult res = (MarathonResult) driverResult;
        return res.getTotal();
    }
}
