package result.marathon.error;

import result.marathon.MarathonResult;

import java.util.List;

/**
 * Decorator pattern to decorate marathon results with error messages.
 */
public abstract class MarathonDecorator implements MarathonResult {

    public static final String MISSING_TIME = "--:--:--";

    private MarathonResult result;

    public MarathonDecorator(MarathonResult result) {
        this.result = result;
    }

    @Override
    public String getNumber() {
        return result.getNumber();
    }

    @Override
    public String getName() {
        return result.getName();
    }

    @Override
    public String getStart() {
        return result.getStart();
    }

    @Override
    public String getEnd() {
        return result.getEnd();
    }

    @Override
    public String getTotal() {
        return result.getTotal();
    }

    @Override
    public List<String> getErrors() {
        return result.getErrors();
    }
}
