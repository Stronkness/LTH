import java.io.*;
import java.util.Collections;
import java.util.LinkedList;

public class Lab {
    public static void main(String[] args){
        try{
            LinkedList<Integer> list = new LinkedList<>();
            BufferedReader bw = new BufferedReader(new FileReader(args[0]));
            String line;
            while ((line = bw.readLine()) != null){
                list.add(Integer.parseInt(line));
            }

            BufferedWriter w = new BufferedWriter(new FileWriter(args[1]));
            w.write("Iterate " + "Time" + "\n");
            int N = Integer.parseInt(args[2]);

            for(int i = 1; i <= N; i++){
                LinkedList<Integer> temp = (LinkedList<Integer>) list.clone();
                long timeStart = System.nanoTime();
                Collections.sort(temp);
                long timeEnd = System.nanoTime();
                long diff = timeEnd - timeStart;

                w.write(i + " " + diff + "\n");
            }

            w.close();

        }catch(Exception e){
            System.out.println("FAILURE");
        }
    }
}
