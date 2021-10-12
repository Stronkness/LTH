package train.simulation;

import java.util.LinkedList;
import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

public class TrainSimulation {
	
	public static void threadCode(TrainView view, Monitor mon) {
		Route route = view.loadRoute();
    	LinkedList<Segment> list = new LinkedList<>();
        for(int i = 0; i < 3; i++) {
            Segment first = route.next();
            mon.busy_free(first);
            first.enter();
        	list.add(first);
        }
        
        while(true) {
        	Segment first = route.next();
        	mon.busy_free(first);
        	first.enter();
        	list.addFirst(first);
        	Segment removed = list.removeLast();
        	removed.exit();
        	mon.free(removed);
        }    
	}

    public static void main(String[] args) {


        TrainView view = new TrainView();
        Monitor mon = new Monitor();
        
        for(int i = 0; i < 20; i++) {
            Thread t = new Thread(() ->{
                threadCode(view, mon);  
             });
            t.start();
        }
    }

}
