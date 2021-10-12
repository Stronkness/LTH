package train.simulation;

import java.util.HashSet;
import train.model.Segment;

public class Monitor {
	private HashSet<Segment> hash = new HashSet<>();
	
	
	public Monitor() {
		
	}
	
	
	
	public synchronized void busy_free(Segment s) {
		while(hash.contains(s)) { //blocked
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		hash.add(s); //busy
	}
	
	public synchronized void free(Segment s) {
		hash.remove(s); //free
		notifyAll();
	}
	
}