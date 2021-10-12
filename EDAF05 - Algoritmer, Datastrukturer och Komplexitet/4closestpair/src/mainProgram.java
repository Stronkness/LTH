import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class mainProgram {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        int contestants = Integer.parseInt(scan.nextLine());

        //Two arraylist containing Px and Py, needed for the algorithm
        ArrayList<Point> px = new ArrayList<Point>();
        ArrayList<Point> py = new ArrayList<Point>();

        while(scan.hasNext()){
            String newLine = scan.nextLine();
            String[] parts = newLine.split(" ");
            Point point = null;
            if(parts.length == 3) point = new Point(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            else point = new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            //Point point = new Point(scan.nextInt(), scan.nextInt());
            px.add(point);
            py.add(point);
        }

        //Sorting from lowest to highest with respect of x and y coordinates
        px.sort((Point p1, Point p2) -> (p1.x - p2.x));
        py.sort((Point p1, Point p2) -> (p1.y - p2.y));

        System.out.printf(Locale.US, "%.6f", closestPair(px, py, contestants));
    }

    private static double closestPair(ArrayList<Point> px, ArrayList<Point> py, int contestants) {
        double min;
        int middle = (px.get(px.size() / 2)).x;

        if(contestants == 1) min = -1;
        if (contestants == 2) min = Math.hypot(px.get(0).x - px.get(1).x, px.get(0).y - px.get(1).y);
        else if (contestants == 3) {
            min = Math.hypot(px.get(0).x - px.get(1).x, px.get(0).y - px.get(1).y);
            if (Math.hypot(px.get(1).x - px.get(2).x, px.get(1).y - px.get(2).y) < min) min = Math.hypot(px.get(1).x - px.get(2).x, px.get(1).y - px.get(2).y);
            if (Math.hypot(px.get(2).x - px.get(0).x, px.get(2).y - px.get(0).y) < min) min = Math.hypot(px.get(2).x - px.get(0).x, px.get(2).y - px.get(0).y);
        }
        else {
            ArrayList<Point> lx = new ArrayList<Point>(contestants);
            ArrayList<Point> ly = new ArrayList<Point>(contestants);
            ArrayList<Point> rx = new ArrayList<Point>(contestants);
            ArrayList<Point> ry = new ArrayList<Point>(contestants);
            //Insertion to lx, rx
            for (int i = 0; i < contestants; i++) {
                if ((px.size() / 2) > i) lx.add(px.get(i));
                else rx.add(px.get(i));
            }
            //Insertion to ly, ry
            for(Point point : py) {
                if (point.x < middle) ly.add(point);
                else ry.add(point);
            }
            double min_left = closestPair(lx, ly, lx.size());
            double min_right = closestPair(rx, ry, rx.size());
            min = Math.min(min_left, min_right);
        }//SLUT AV ELSE

        ArrayList<Point> sy = new ArrayList<Point>();
        for (Point point : py){ if(Math.abs((point.x - middle)) < min) sy.add(point); }

        for(int i = 0; i < sy.size(); i++) {
            for(int j = i + 1; j < sy.size(); j++) {
                if(j > i + 15) break;
                if(Math.hypot(sy.get(i).x - sy.get(j).x, sy.get(i).y - sy.get(j).y) < min) min = Math.hypot(sy.get(i).x - sy.get(j).x, sy.get(i).y - sy.get(j).y);
            }
        }

        return min;

    }



    private static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
