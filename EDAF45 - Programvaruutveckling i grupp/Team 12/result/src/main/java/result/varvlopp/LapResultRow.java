package result.varvlopp;

import result.ResultRow;
import result.marathon.MarathonResult;
import util.TimeUtils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LapResultRow extends ResultRow implements LapResult {
    private LocalTime start;
    private List<LocalTime> ends;

    public LapResultRow(String number, String name, LocalTime start, List<LocalTime> end) {
        super(number, name);
        this.start = start;
        this.ends = end;
    }

    public String getStart() {
        return TimeUtils.formatTime(start);
    }

    public String getEnd() {
        return TimeUtils.formatTime(ends.get(ends.size() - 1));
    }

    public String getVarvning(int maxLaps) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumberOfLaps()-1; i++){
            String time = getStart();
            for(int j = 0; j <= i; j++){
                time = addStrings(time, getLap(j));
            }
            sb.append(time).append(";").append(" ");
        }
        int colons = maxLaps - getNumberOfLaps();
        for(int i = 0; i < colons; i++){
            sb.append(" ").append(";").append(" ");
        }
        return sb.toString();
    }

    public String addStrings(String s1, String s2) {
        int total = Integer.parseInt(s1.substring(0,2))*3600 + Integer.parseInt(s2.substring(0,2))*3600;
        total += Integer.parseInt(s1.substring(3,5))*60 + Integer.parseInt(s2.substring(3,5))*60;
        total += Integer.parseInt(s1.substring(6,8)) + Integer.parseInt(s2.substring(6,8));

        int h = total/3600;
        int m = (total - h*3600)/60;
        int s = total -h*3600 - m*60;

        String sH = Integer.toString(h);
        String sM = Integer.toString(m);
        String sS = Integer.toString(s);

        if(sH.length() == 0) sH = "00";
        else if(sH.length() == 1) sH = "0" + sH;

        if(sM.length() == 0) sM = "00";
        else if(sM.length() == 1) sM = "0" + sM;

        if(sS.length() == 0) sS = "00";
        else if(sS.length() == 1) sS = "0" + sS;

        return sH + ":"+sM + ":"+sS;
    }


    //Räknas på ett annat sätt
    @Override
    public String getTotal() {
        return TimeUtils.formatTime(Duration.between(start, ends.get(getNumberOfLaps()-1)));
    }

    @Override
    public String getLap(int lap) {
        if (lap == 0){
            return TimeUtils.formatTime(Duration.between(start, ends.get(0)));
        }else if(lap >= getNumberOfLaps()){
            return "";
        }
        else{
            return TimeUtils.formatTime(Duration.between(ends.get(lap-1), ends.get(lap)));
        }
    }

    @Override
    public int getNumberOfLaps() {
        return ends.size();
    }



}
