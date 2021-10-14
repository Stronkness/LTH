package result.varvlopp;

import result.DriverEntry;
import result.Matcher;
import result.TimeEntry;
import result.marathon.MarathonDriver;
import result.marathon.MarathonResult;
import result.marathon.MarathonResultRow;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LapMatcher extends Matcher<LapDriver, LapResult> {
    String endOfRace;

    public LapMatcher(String endOfRace){
        this.endOfRace = endOfRace;
    }

    public int maxLaps(){
        int max = 0;
        for(Map.Entry<String, LapDriver> drive: drivers.entrySet()){
            if(drive.getValue().getTotalLaps() > max){
                max = drive.getValue().getTotalLaps();
            }
        }
        return max;
    }

    @Override
    public void cleanUpDrivers(List<String> currentDrivers) {
        for(Map.Entry<String, LapDriver> drive : drivers.entrySet()){
            if(!currentDrivers.contains(drive.getKey())){
                LapDriver temp = new LapDriver(drive.getKey());
                temp.setStart(drive.getValue().getStart());
                for(LocalTime time :drive.getValue().getEnd()) {
                    temp.setEnd(time);
                }
                temp.setName("");
                driversNotRegistered.put(drive.getKey(), temp);
                drivers.remove(drive.getKey());
            }
        }
    }

    @Override
    protected LapDriver getDriver(String number) {
        LapDriver driver;
        if ((driver = drivers.get(number)) == null) {
            driver = new LapDriver(number);
            drivers.put(number, driver);
        }
        return driver;
    }

   @Override
    protected List<LapResult> makeResult(Map<String, LapDriver> map) {
            List<LapResult> results = new ArrayList<>();
            for (LapDriver driver : map.values()) {
                LapResult result = new LapResultRow(driver.getStartNumber(),
                        driver.getName(), driver.getStart(), driver.getEnd());
                result = findErrors(driver, result); // decorate result with errors
                results.add(result); // add decorated result to list
            }
            return results;

    }

}
