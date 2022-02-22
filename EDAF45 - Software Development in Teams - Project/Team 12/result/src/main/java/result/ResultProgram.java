package result;

import result.marathon.MarathonFormatter;
import result.marathon.MarathonMatcher;
import result.marathon.MarathonSorterHandler;
import result.stage.StageFormatter;
import result.stage.StageMatcher;
import result.varvlopp.LapFormatter;
import result.varvlopp.LapMatcher;
import result.varvlopp.LapSorter;
import util.FileRead;
import util.FileWrite;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.json.*;

/**
 * The main program. TODO!
 */
public class ResultProgram {


    public static void main(String[] args) throws IOException {

        String configFile = new String(Files.readAllBytes(Paths.get("configfile.json")));
        JSONObject obj = new JSONObject(configFile);
        String raceType = obj.getString("RaceType");

        JSONObject raceConfigObject = obj.getJSONObject(raceType);

        RegTime regTime = new RegTime();
        FileRead fileread = new FileRead();

        NameDetails nameDetails = new NameDetails();
        fileread.readNameFile(nameDetails, raceConfigObject.getString("NameFile"));

        boolean masstart = raceConfigObject.getBoolean("Masstart");
        if(masstart){
            List<String> list = nameDetails.getStartNumbers();
            for(String s: list) {
                regTime.putStart(s, raceConfigObject.getString("Masstart Tid"));
            }
        } else {
            fileread.readStartFile(regTime, raceConfigObject.getString("StartTimes"));
        }

        JSONArray array = raceConfigObject.getJSONArray("EndTimes");
        for(Object endFiles : array){
            fileread.readEndFile(regTime,(String) endFiles);
        }

        regTime.sort();

        Handler handler;

        switch (raceType){
            case "Maraton":
                handler = new MarathonHandler();
                break;
            case "Varvlopp":
                handler = new LapHandler(raceConfigObject.getString("Måltid"));
                break;
            default: handler = null;
        }


        handler.loadTitle(raceConfigObject.getString("Title"));

        System.out.println(raceConfigObject.getString("Minimitid"));

        handler.setMinTime(raceConfigObject.getString("Minimitid"));
        handler.setUpMatcher(regTime, nameDetails);



        boolean sortedResult = raceConfigObject.getBoolean("ResultSorted");

        List<String> result;
        if(sortedResult){
            result = handler.sorted();
        } else {
            result = handler.generateResult();
        }
        
        result.add(raceConfigObject.getString("ResultFormat"));

        FileWrite fileWrite = new FileWrite();
        fileWrite.write(result, raceConfigObject.getString("ResultDest"));

        System.out.println("This is the result program!" + "\n Result is in " + raceConfigObject.getString("ResultDest"));
    }


}

abstract class Handler {

    protected Matcher m;
    protected ResultFormatter formatter;
    protected SorterHandler sorter;
    private String title;
    Set<Map.Entry<String, List<String>>> classDetails;
    private boolean ready;
    private List<Result> results;

    public Handler() {
        ready = false;

    }
    public void setMinTime(String minTime) {
        m.setMinTime(minTime);
    }

    public void setUpMatcher(RegTime time, NameDetails details) {
        addTimes(time);
        addDrivers(details);
        classDetails = details.getClassDetailsSet();
        ready = true;
        results = m.result();
        prepare();

    }

    public void loadTitle(String title) {
        this.title = title;
    }

    public List<String> sorted() {
      return sorter.sortAndFormat(results, classDetails);
    }

    public List<String> generateResult() {

        List<String> stringResult = new ArrayList<>();

        if(ready) {
            List<Result> results = m.result();
            stringResult.add(title + "\n");

            for (Map.Entry<String, List<String>> classDetailsEntry : classDetails) {

                stringResult.add(classDetailsEntry.getKey());

                stringResult.add(rowHeader());

                for (Result tempResult : results) {
                    if (classDetailsEntry.getValue().contains(tempResult.getNumber())) {
                        String resultRow = formatter.formatDriver(tempResult);
                        stringResult.add(resultRow);
                    }
                }
            }

            List<Result> noRegisteredNames = m.resultNotRegistered();

            if (!noRegisteredNames.isEmpty()) {
                stringResult.add("Icke existerande startnummer");
                for (Result temp : noRegisteredNames) {
                    String resultRow = formatter.formatDriver(temp);
                    stringResult.add(resultRow);
                }
            }
        }

        return stringResult;
    }

    protected abstract String rowHeader();

    private void addTimes(RegTime time) {
        m.addStartTimes(time.getStartTimes());
        m.addEndTimes(time.getEndTimes());
    }

    private void addDrivers(NameDetails details){
        m.addDrivers(details.getDriverEntries());
        m.cleanUpDrivers(details.getStartNumbers());
    }

    protected abstract void prepare();

}

class MarathonHandler extends Handler {

    protected MarathonHandler() {
        m = new MarathonMatcher();
        formatter = new MarathonFormatter();
        sorter = new MarathonSorterHandler();

    }

    protected void prepare() {}

    @Override
    protected String rowHeader() {
        return "StartNbr; Namn; Totaltid; Start; Mål";
    }
}

class LapHandler extends Handler {
    private int maxNbrOfLaps;

    protected LapHandler(String endOfRace) {
        m = new LapMatcher(endOfRace); //todo change, create setTimeRestrictions method in abstract class Matcher and change Lap constructor
        formatter = new LapFormatter();
        maxNbrOfLaps = 0;
    }

    protected void prepare() {
        maxNbrOfLaps = ((LapMatcher) m).maxLaps();
        sorter = new LapSorter(maxNbrOfLaps);
        ((LapFormatter) formatter).setMaxLaps(maxNbrOfLaps);
    }


    @Override
    protected String rowHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("StartNbr; Namn; #Varv; Totaltid; ");
        for (int i = 1; i <= maxNbrOfLaps; i++) {
            sb.append("Varv" + i + "; ");
        }

        sb.append("Start; ");

        for (int i = 1; i < maxNbrOfLaps; i++) {
            sb.append("Varvning" + i + "; ");
        }

        sb.append("Mål");
        return sb.toString();
    }
}

class StageHandler extends Handler {

    private int maxNumberOfStages;

    //todo sorter
    public StageHandler() {
        m = new StageMatcher();
        formatter = new StageFormatter();
        maxNumberOfStages = 0;
    }
    
    @Override
    protected String rowHeader() {
        StringBuilder sb = new StringBuilder();

        //for(i in namedetails) {sb.append(i)}
        sb.append("StartNbr; Namn; #Etapp; Totaltid; ");
        for (int i = 1; i <= maxNumberOfStages; i++) {
            sb.append("Etapp" + i + "; ");
        }
        

        for (int i = 1; i < maxNumberOfStages; i++) {
            sb.append("Start" + i + "; ");
            sb.append("Mål" + i + "; ");
        }

        return sb.toString();
    }

    //todo could be abstracted numberRestrictions
    @Override
    protected void prepare() {
        maxNumberOfStages = ((StageMatcher) m).maxStages();

        ((StageFormatter) formatter).setMaxStages(maxNumberOfStages);
    }
}





