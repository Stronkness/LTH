package result.varvlopp;

import result.Result;

public interface LapResult extends Result {
    String getLap(int lap);
    int getNumberOfLaps();
    String getVarvning(int maxLaps);
}
