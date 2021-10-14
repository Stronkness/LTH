package util;

import java.io.File;
import java.io.FileWriter;   // Import the FileWriter class
import java.util.List;
import result.ResultFormatter;
import result.marathon.MarathonFormatter;
import result.marathon.MarathonMatcher;
import result.marathon.MarathonResult;
public class  FileWrite{

    public FileWrite() {
    }

    public void write(List<String> result, String path) {
        try {
            File temp = new File(path);
            temp.createNewFile();
            FileWriter myWriter = new FileWriter(temp);
            for(String resultRow: result) {
                myWriter.write(resultRow + "\n");
            }
            myWriter.close();
        }
        catch(Exception e) {
            System.out.println("Could not write to file!");
        }
    }
}
