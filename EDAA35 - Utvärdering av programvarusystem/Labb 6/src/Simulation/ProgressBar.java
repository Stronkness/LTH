package Simulation;

/**
 * A simple "progress bar" in ascii format on std out showing that the simulation 
 * is progressing
 * @author martinh
 *
 */
public class ProgressBar extends Process {
	
	private static int nrMarks = 40;
	private double timeBetweenMarks;

	/**
 	 * Constructs a progress bar
 	 * @param s the active simulation
 	 * @param simTime how long the simulation should last (in simulated time)
 	 */
	public ProgressBar(Simulation s, double simTime) {
		super(s);
		timeBetweenMarks = simTime/nrMarks;
		this.setTimer(timeBetweenMarks, new Event());
	}

	public void handleEvent(Event e) {
		System.out.print("x");
		this.setTimer(timeBetweenMarks, new Event()); 
	}

}
