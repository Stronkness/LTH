package result.marathon;

import result.Driver;
import result.DriverEntry;
import result.Matcher;
import result.TimeEntry;
import result.marathon.error.*;
import util.TimeUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarathonMatcher extends Matcher<MarathonDriver, MarathonResult> {
    public MarathonMatcher() {
        addErrors();
    }

    private void addErrors() {
        // Find missing start times
        errorFinders.add((driver, result) ->
                driver.getStart() == null ? new MissingStartTime(result) : result
        );
        // Find missing end times
        errorFinders.add((driver, result) ->
                driver.getEnd() == null ? new MissingEndTime(result) : result
        );

        errorFinders.add((driver, result) -> {
                    if (driver.getStart() != null && driver.getEnd() != null) {
                        String totalTime = TimeUtils.formatTime(Duration.between(driver.getStart(), driver.getEnd()));
                        return invalidTime(totalTime) ? new TooShortTime(result) : result;
                    }
                    return result;
                }
        );

        errorFinders.add((driver, result) -> {
            if(driver.getEndSize() > 1){
                return new MultipleTime(result, driver.getAllEnd(),"mål");
            }
            else{
                return result;
            }
        });

        errorFinders.add((driver, result) -> {
            if(driver.getStartSize() > 1){
                return new MultipleTime(result, driver.getAllStart(), "start");
            }
            else{
                return result;
            }
        });
    }

    @Override
    public void cleanUpDrivers(List<String> currentDrivers) {
        for(Map.Entry<String, MarathonDriver> drive : drivers.entrySet()){
            if(!currentDrivers.contains(drive.getKey())){
                MarathonDriver temp = new MarathonDriver(drive.getKey());
                temp.setStart(drive.getValue().getStart());
                temp.setEnd(drive.getValue().getEnd());
                temp.setName("");
                driversNotRegistered.put(drive.getKey(), temp);
                drivers.remove(drive.getKey());
            }
        }
    }

    @Override
    protected MarathonDriver getDriver(String number) {
        MarathonDriver driver;
        if ((driver = drivers.get(number)) == null) {
            driver = new MarathonDriver(number);
            drivers.put(number, driver);
        }
        return driver;
    }

    @Override
    protected List<MarathonResult> makeResult(Map<String, MarathonDriver> map) {
        List<MarathonResult> results = new ArrayList<>();
        for (MarathonDriver driver : map.values()) {
            MarathonResult result = new MarathonResultRow(driver.getStartNumber(),
                    driver.getName(), driver.getStart(), driver.getEnd());
            result = findErrors(driver, result); // decorate result with errors
            results.add(result); // add decorated result to list
        }
        return results;
    }
}