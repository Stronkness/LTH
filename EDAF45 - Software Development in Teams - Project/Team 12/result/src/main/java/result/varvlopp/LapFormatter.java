package result.varvlopp;

import result.ResultFormatter;
import result.marathon.MarathonResult;
import util.TimeUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalAmount;
import java.util.Date;

public class LapFormatter implements ResultFormatter<LapResult> {
    private int maxLaps;

    public void setMaxLaps(int laps){
        maxLaps = laps;
    }
    @Override
    public String formatDriver(LapResult driverResult) {
        StringBuilder sb = new StringBuilder();

        // start number
        sb.append(driverResult.getNumber()).append(SEP).append(" ");

        // driver name
        sb.append(driverResult.getName()).append(SEP).append(" ");

        sb.append(driverResult.getNumberOfLaps()).append(SEP).append(" ");

        sb.append(driverResult.getTotal()).append(SEP).append(" ");

        for(int i = 0 ; i < maxLaps; i++ ) {
            sb.append(driverResult.getLap(i)).append(SEP).append(" ");
        }

        // start time
        sb.append(driverResult.getStart()).append(SEP).append(" ");


        //Laps
        sb.append(driverResult.getVarvning(maxLaps));

        sb.append(driverResult.getEnd()).append(SEP).append(" ");

        // errors in the extra error column
        boolean addComma = false;
        for (String e : driverResult.getErrors()) {
            if (addComma) {
                sb.append(", ");
            } else {
                addComma = true;
            }
            sb.append(e);
        }

        return sb.toString();
    }
}
