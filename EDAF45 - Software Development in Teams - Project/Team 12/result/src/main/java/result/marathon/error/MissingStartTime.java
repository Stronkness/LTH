package result.marathon.error;

import result.marathon.MarathonResult;

public class MissingStartTime extends MarathonDecorator {

    public MissingStartTime(MarathonResult result) {
        super(result);
    }

    @Override
    public String getStart() {
        return "Start?";
    }

    @Override
    public String getTotal() {
        return MISSING_TIME;
    }
}
