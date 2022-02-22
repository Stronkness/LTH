package result.stage;

import result.Result;

public interface StageResult extends Result {

    int getNumberOfStages();
    String getStage(int stage);
    String getStart(int stage);
    String getEnd(int stage);
}
