package util;

import result.DriverEntry;
import result.NameDetails;
import result.RegTime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;


interface Register {
    void reg(RegTime list, String first, String second);
}

public class FileRead {

    public void readFile(RegTime list, String stringPath, Register register) {
        Path path = Path.of(stringPath);
        try {
            BufferedReader read = Files.newBufferedReader(path);
            String temp;

            while((temp = read.readLine()) != null){
                String[] parts = temp.split("; ");
                register.reg(list, parts[0], parts[1]);
            }

            read.close();
        } catch (IOException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    public void readStartFile(RegTime list, String stringPath) {
        readFile(list, stringPath, ((regTime, first, second) -> regTime.putStart(first,second)));
    }

    public void readEndFile(RegTime list, String stringPath) {
        readFile(list, stringPath, ((regTime, first, second) -> regTime.putEnd(first,second)));
    }

    public void readNameFile(NameDetails nameDetails, String stringPath){
        Path path = Path.of(stringPath);
        try {
            BufferedReader read = Files.newBufferedReader(path);
            String temp;
            String className = "STANDARDKLASS";

            if ((temp = read.readLine()) != null) {
                nameDetails.addColumnNames(temp);
            }
            while((temp = read.readLine()) != null){
                if(isClassName(temp)){
                    className = temp;
                    nameDetails.addClassName(temp);
                }else{
                    String[] parts = temp.split("; ");
                    nameDetails.addNameDetails(parts[0],parts[1],className);
                }
            }
            read.close();
        } catch (IOException e) {
            System.out.println("File not found!");
            e.printStackTrace();
        }
    }

    private boolean isClassName(String row) {
        String[] parts = row.split("; ");
        if(parts.length == 2){
            return false;
        }else{
            return true;
        }
    }
}
