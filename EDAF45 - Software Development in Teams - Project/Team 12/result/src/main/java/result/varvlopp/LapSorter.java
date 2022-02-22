package result.varvlopp;

import result.Result;
import result.SorterFormatter;
import result.SorterHandler;
import result.marathon.MarathonResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LapSorter extends SorterHandler<LapResult> {
    private int totalNumberOfLaps;

    public LapSorter(int maxNumberOfLaps) {
        super("", new LapSorterFormatter(maxNumberOfLaps));
        totalNumberOfLaps = maxNumberOfLaps;
        loadTitle();
    }

    private void loadTitle() {
        StringBuilder sb = new StringBuilder();

        sb.append("Place; StartNr; Namn; #Varv; Totaltid");
        for(int i = 1; i<=totalNumberOfLaps; i++){

            sb.append("; ");
            sb.append("Varv" + i);

        }

        title = sb.toString();
    }

    @Override
    protected List<List<Result>> separateValids(List<LapResult> total) {

            List<Result> valids = new ArrayList<>();
            List<Result> nonValids = new ArrayList<>();

            List<List<Result>> res = new ArrayList<>();


            for(LapResult result : total){

                if(NON_VALID_RESULT.equals(result.getTotal()) || result.getNumberOfLaps() != totalNumberOfLaps) {
                    nonValids.add(result);
                } else {

                    valids.add(result);
                }

            }

            res.add(valids);
            res.add(nonValids);

            return res;

    }

    //unnecessary subclass method? if all races sort by getTotal
    @Override
    protected void sort(List<Result> results) {
        results.sort(Comparator.comparing(r -> ((LapResult) r).getTotal()));
    }


}

class LapSorterFormatter extends SorterFormatter {

    private int totalNrOfLaps;

    public LapSorterFormatter(int totalNrOfLaps){
        this.totalNrOfLaps = totalNrOfLaps;
    }

    @Override
    protected String raceString(Result driverResult) {

        LapResult result = (LapResult) driverResult;

        StringBuilder sb = new StringBuilder();

        sb.append(result.getNumberOfLaps()).append(SEP).append(" ");

        sb.append(result.getTotal());

        for(int i=0;i<totalNrOfLaps;i++){
            sb.append(SEP);
            sb.append(" ");
            sb.append(result.getLap(i));
        }

        return sb.toString();
    }
}
