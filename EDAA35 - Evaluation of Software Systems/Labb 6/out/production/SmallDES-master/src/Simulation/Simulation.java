package Simulation;

import java.util.Comparator; 
import java.util.PriorityQueue;

/**
 * Represents a simulation model. Handles an event queue and the global time and sends
 * events to processes. 
 * @author martinh
 *
 */
public class Simulation {
	
	private PriorityQueue<Event> queue;      	// the event queue
	private double time; 						// the global time
	private boolean progressBar = false;        // whether we want to show progr bar

	
	/**
 	 * Constructs a new simulation (with no progress bar)
 	 */
	public Simulation() {
		queue = new PriorityQueue<Event>(1000, new Comparator<Event>() {
			      public int compare(Event o1, Event o2) {
				     return (o1.getT() < o2.getT() ? -1 : (o1.getT() > o2.getT() ? 1 : 0));
			      }
		        });
		time = 0.0; 
	}
	
	/**
 	 * Constructs a new simulation
 	 * @param progressBar boolean if there should be a progress bar
 	 */
	public Simulation(boolean progressBar) {
		this();
		this.progressBar = progressBar;
	}	
	
	/**
 	 * Adds an event to the event queue
 	 * @param e the event that should be added
 	 */
	public void addEvent(Event e) {
		queue.add(e);
	}
	
	/**
 	 * Get current (simulated) time
 	 * @return current simulated time
 	 */
	public double getTime() {
		return time;
	}
	
	/**
 	 * Start simulation
 	 * @param simulationTime the time to be simulated
 	 */
	public void simulate(double simulationTime) {
		long startTime = System.currentTimeMillis();
		if (progressBar)
			new ProgressBar(this, simulationTime);
		while (time < simulationTime) {
			Event e = queue.poll();
			time = e.getT();
			e.getProcess().handleEvent(e);
		}
		if(progressBar)
			System.out.println();
		long endTime = System.currentTimeMillis();
		long measuredTime = endTime - startTime;
		System.out.println("*** Simulation done in " + measuredTime/1e3 + " seconds (real time) ***"); 
	}
		
};
