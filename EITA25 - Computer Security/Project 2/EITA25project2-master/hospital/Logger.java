package hospital;


import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    String fileName = "./log.txt";
    File file = new File(fileName);
    FileWriter fw;

    public void log(String entity, String entryName, String action) {
        try {
            fw = new FileWriter(file, true);
            String date = LocalDateTime
                                .now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
            StringBuilder sb = new StringBuilder();
            sb.append(
                "["
                + date 
                + "] " 
                + entity.toUpperCase() 
                + " " 
                + action.toUpperCase() 
                + " the entry of " 
                + entryName.toUpperCase() 
                + "\n");

            fw.append(sb.toString());
            fw.close();
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
