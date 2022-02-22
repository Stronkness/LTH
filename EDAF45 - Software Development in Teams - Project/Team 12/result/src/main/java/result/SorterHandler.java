package result;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class SorterHandler<R extends Result> {

    private SorterFormatter sf;
    protected List<String> sortedResult;
    protected final String NON_VALID_RESULT = "--:--:--";
    protected String title;

    public SorterHandler(String title, SorterFormatter sf){
        this.title = title;

        this.sf = sf;
    }

    public List<String> sortAndFormat(List<R> results, Set<Map.Entry<String, List<String>>> classDetails){

        List<String> res = new ArrayList<>();
        List<R> classResults;

        for (Map.Entry<String, List<String>> classDetailsEntry : classDetails) {

            res.add(classDetailsEntry.getKey());

            res.add(title);
            classResults = new ArrayList<>();


//            Skulle kunna vara mer effektiv kod :)
            for(R tempResult: results) {
                if (classDetailsEntry.getValue().contains(tempResult.getNumber())) {
                    classResults.add(tempResult);
                }
            }

            List<List<Result>> separatedResults = separateValids(classResults);

            List<Result> valids = separatedResults.get(0);
            List<Result> nonValids = separatedResults.get(1);

            sort(valids);



        for(int i = 0; i<valids.size(); i++){
            res.add((i+1) + sf.formatDriver(valids.get(i)));
        }

        for(int i = 0; i<nonValids.size(); i++) {
            res.add(sf.formatDriver(nonValids.get(i)));
        }
    }
        return res;
    }

    //Returns a list of two lists, the first contains the valid results, the second contains the nonvalids.
    //todo make it work for different race types, move to subclass?
    protected abstract List<List<Result>> separateValids(List<R> total);

    protected abstract void sort(List<Result> results);


}



