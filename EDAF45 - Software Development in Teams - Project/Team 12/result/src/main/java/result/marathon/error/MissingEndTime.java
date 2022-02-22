package result.marathon.error;

import result.marathon.MarathonResult;

public class MissingEndTime extends MarathonDecorator {

    public MissingEndTime(MarathonResult result) {
        super(result);
    }

    @Override
    public String getEnd() {
        return "Slut?";
    }

    @Override
    public String getTotal() {
        return MISSING_TIME;
    }
}
