package result;

import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Combines information about drivers (name, start times, end times) to create a Result.
 * Sub classes for different types of races.
 *
 * @param <D> Type of Driver class, e.g. MarathonDriver
 * @param <R> Type of Result class, e.g. MarathonResult
 */
public abstract class Matcher<D extends Driver, R extends Result> {
    protected Map<String, D> driversNotRegistered;
    protected Map<String, D> drivers;
    protected List<ErrorFinder<D, R>> errorFinders = new ArrayList<>();
    protected String minTime;

    public Matcher() {
        this.minTime = "00:15:00";
        this.drivers = new TreeMap<>();
        this.driversNotRegistered = new TreeMap<>();
    }

    /**
     * Computes a result row for each driver.
     * @return List of results, i.e. one Result for each Driver.
     */
    public List<R> result() {
        return makeResult(drivers);
    }

    public List<R> resultNotRegistered() {
        return makeResult(driversNotRegistered);
    }

    protected abstract List<R> makeResult(Map<String, D> map);

    public void addStartTimes(List<TimeEntry> newTimes) {
        for (TimeEntry entry : newTimes) {
            getDriver(entry.getNumber()).setStart(entry.getTime());
        }
    }

    protected abstract Driver getDriver(String number);

    public void addEndTimes(List<TimeEntry> newTimes) {
        for (TimeEntry entry : newTimes) {
            getDriver(entry.getNumber()).setEnd(entry.getTime());
        }
    }

    public void addDrivers(List<DriverEntry> newDrivers) {
        for (DriverEntry entry : newDrivers) {
            getDriver(entry.getNumber()).setName(entry.getName());
        }
    }

    public abstract void cleanUpDrivers(List<String> currentDrivers);

    protected R findErrors(D driver, R result) {
        R decoratedResult = result;
        for (ErrorFinder<D, R> ef : errorFinders) {
            decoratedResult = ef.apply(driver, decoratedResult);
        }
        return decoratedResult;
    }

    protected boolean invalidTime(String time) {
        int h = Integer.parseInt(time.substring(0,2));
        int m = Integer.parseInt(time.substring(3,5));
        int s = Integer.parseInt(time.substring(6,8));
        int minH = Integer.parseInt(minTime.substring(0,2));
        int minM = Integer.parseInt(minTime.substring(3,5));
        int minS = Integer.parseInt(minTime.substring(6,8));

        return (minH > h || minM > m && minH >= h || minS > s && minM >= m && minH >= h);
    }

    public void setMinTime(String minTime){
        this.minTime = minTime;
    }
}
