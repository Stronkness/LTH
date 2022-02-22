package result.marathon.error;

import result.marathon.MarathonResult;
import util.TimeUtils;

import java.time.LocalTime;
import java.util.List;

public class MultipleTime extends MarathonDecorator{
    private MarathonResult result;
    private List<LocalTime> ends;
    private String type;

    public MultipleTime(MarathonResult result, List<LocalTime> ends, String type) {
        super(result);
        this.result = result;
        this.ends = ends;
        this.type = type;
    }

    @Override
    public List<String> getErrors(){
        List<String> errors = result.getErrors();
        StringBuilder sb = new StringBuilder();
        sb.append("Flera " + type + "tider?");

        for(var e: ends){
            sb.append("; ");
            sb.append(TimeUtils.formatTime(e));
        }
        errors.add(sb.toString());
        return errors;
    }

}
