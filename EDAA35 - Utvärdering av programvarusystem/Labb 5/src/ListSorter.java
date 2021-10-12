import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ListSorter {
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
                sorter(temp);
                long timeEnd = System.nanoTime();
                long diff = timeEnd - timeStart;

                w.write(i + " " + diff + "\n");
            }

            w.close();

        }catch(Exception e){
            System.out.println("FAILURE");
        }
    }

    private static LinkedList<Integer> sorter(LinkedList<Integer> temp) {
        if (temp.size() <= 1) return temp;

        int mid = temp.size()/2;
        LinkedList<Integer> r = new LinkedList<Integer>(temp.subList(mid, temp.size()));
        LinkedList<Integer> l = new LinkedList<Integer>(temp.subList(0, mid));
        r = sorter(r); l = sorter(l);

        return mergeSort(r,l);
    }

    private static LinkedList<Integer> mergeSort(LinkedList<Integer> r, LinkedList<Integer> l) {
        LinkedList<Integer> resultSorted = new LinkedList<Integer>();
        while(r.size() > 0 && l.size() > 0){
            if(r.get(0) < l.get(0)) resultSorted.add(r.remove(0));
            else resultSorted.add(l.remove(0));
        }
        resultSorted.addAll(r);
        resultSorted.addAll(l);
        return resultSorted;
    }

}
