package result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.FileWrite;

import java.util.LinkedList;
import java.util.List;

public class TestFileWrite {
    FileWrite myWriter;
    List<String> result;

    @BeforeEach
    public void setUp(){
        result = new LinkedList<>();
        myWriter = new FileWrite();
    }

    @Test
    public void testFileWrite(){
        result.add("1; --:--:--; 12:00:00; 13:23:34");
        result.add("2; --:--:--; 12:01:00; 13:15:16");
        result.add("3; --:--:--; 12:02:00; 13:05:06");
        result.add("4; --:--:--; 12:03:00; 13:12:07");
        result.add("5; --:--:--; 12:04:00; 13:16:07");
        myWriter.write(result,"resultFile.txt");
    }
}
