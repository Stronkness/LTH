package result.marathon;

import result.Result;
import result.SorterHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MarathonSorterHandler extends SorterHandler<MarathonResult> {

    public MarathonSorterHandler() {
        super("Place; StartNbr; Namn; Totaltid; ", new MarathonSorterFormatter());
    }

    protected void sort(List<Result> results) {
        results.sort(Comparator.comparing(r -> ((MarathonResult) r).getTotal()));
    }

    @Override
    protected List<List<Result>> separateValids(List<MarathonResult> total) {
        {
            List<Result> valids = new ArrayList<>();
            List<Result> nonValids = new ArrayList<>();

            List<List<Result>> res = new ArrayList<>();


            for(MarathonResult result : total){

                if(NON_VALID_RESULT.equals(result.getTotal())) {
                    nonValids.add(result);
                } else {
                    valids.add(result);
                }
            }

            res.add(valids);
            res.add(nonValids);

            return res;
        }
    }
}
