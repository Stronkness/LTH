package result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import util.FileRead;
import static org.junit.jupiter.api.Assertions.*;

import javax.annotation.processing.Filer;
import java.io.IOException;

public class TestFileRead {
    private RegTime time;
    private FileRead read;

    @BeforeEach
    public void setUp(){
        time = new RegTime();
        read = new FileRead();
    }

    @Test
    public void readStartFile() {
        read.readStartFile(time, "../Acceptanstester/Maraton/acceptanstestM1/starttider.txt");
        assertEquals(5, time.startSize());
    }

    @Test
    public void readEndFile(){
        read.readEndFile(time, "../Acceptanstester/Maraton/acceptanstestM1/maltider.txt");
        assertEquals(5, time.endSize());
    }

}
