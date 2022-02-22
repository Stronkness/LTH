package result.stage;

import result.ResultFormatter;

public class StageFormatter implements ResultFormatter<StageResult> {
    private int stages;

    public void setMaxStages(int stages){
        this.stages = stages;
    }

    @Override
    public String formatDriver(StageResult driverResult) {
        StringBuilder sb = new StringBuilder();

        // start number
        sb.append(driverResult.getNumber()).append(SEP).append(" ");

        // driver name
        sb.append(driverResult.getName()).append(SEP).append(" ");

        //for namedetail in namedetails { sb.append(namedetail) }

        sb.append(driverResult.getTotal()).append(SEP).append(" ");

        sb.append(driverResult.getNumberOfStages());

        for(int i = 0 ; i < stages; i++ ) {
            sb.append(SEP).append(" ").append(driverResult.getStage(i));
        }

        // start time

        for(int i = 0; i < stages; i++) {
            sb.append(SEP).append(" ").append(driverResult.getStart(i));

            sb.append(SEP).append(" ").append(driverResult.getEnd(i));
        }


        // errors in the extra error column
        /*boolean addComma = false;
        for (String e : driverResult.getErrors()) {
            if (addComma) {
                sb.append(", ");
            } else {
                addComma = true;
            }
            sb.append(e);
        }*/

        return sb.toString();
    }
}
