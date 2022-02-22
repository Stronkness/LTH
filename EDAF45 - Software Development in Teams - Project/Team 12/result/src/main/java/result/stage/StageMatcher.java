package result.stage;

import result.Driver;
import result.Matcher;
import result.Result;
import result.marathon.MarathonDriver;
import result.varvlopp.LapDriver;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StageMatcher extends Matcher<StageDriver, StageResult> {
    
    @Override
    protected Driver getDriver(String number) {
        StageDriver driver = drivers.get(number);
        if (driver == null) {
            driver = new StageDriver(number);
            drivers.put(number, driver);
        }
        return driver;
    }

    public int maxStages(){
        int max = 0;
        for(Map.Entry<String, StageDriver> drive: drivers.entrySet()){
            if(drive.getValue().totalStages() > max){
                max = drive.getValue().totalStages();
            }
        }
        return max;
    }
    
    //todo fix dry
    @Override
    public void cleanUpDrivers(List currentDrivers) {
        for(Map.Entry<String, StageDriver> drive : drivers.entrySet()){
            if(!currentDrivers.contains(drive.getKey())){
                StageDriver temp = new StageDriver(drive.getKey());

                for(LocalTime time :drive.getValue().getStarts()) {
                    temp.setStart(time);
                }               
                
                for(LocalTime time : drive.getValue().getEnd()) {
                    temp.setEnd(time);
                }
                
                temp.setName("");
                driversNotRegistered.put(drive.getKey(), temp);
                drivers.remove(drive.getKey());
            }
        }
    }
    
    @Override
    protected List<StageResult> makeResult(Map<String, StageDriver> map) {
        List<StageResult> results = new ArrayList<>();
        for (StageDriver driver : map.values()) {
            StageResult result = new StageResultRow(driver.getStartNumber(),
                    driver.getName(), driver.getStarts(), driver.getEnd());
            result = findErrors(driver, result); // decorate result with errors
            results.add(result); // add decorated result to list
        }
        
        return results;

    }

}
