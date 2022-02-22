package train.simulation;

import java.util.HashSet;
import train.model.Segment;

public class MonitorTesting {
	private HashSet<Segment> hash = new HashSet<>();
	
	
	public MonitorTesting() {
		
	}
	
	
	
	public synchronized void busy_free(Segment s) {
		while(hash.contains(s)) { //blocked
			try {
				System.out.println("wait " + s);
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