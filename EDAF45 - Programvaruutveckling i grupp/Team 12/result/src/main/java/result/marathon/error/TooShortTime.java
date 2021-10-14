package result.marathon.error;

import result.marathon.MarathonResult;

import java.util.List;

public class TooShortTime extends MarathonDecorator{
    private MarathonResult result;
    public TooShortTime(MarathonResult result) {
        super(result);
        this.result = result;
    }

    @Override
    public List<String> getErrors(){
        List<String> errors = result.getErrors();
        errors.add("Omöjlig totaltid?");
        return errors;
    }

}
