package Simulation;

/**
 * Event in simulation model
 * @author martinh
 *
 */
public class Event {
	
	private double t;
	private Process p;
	private String eventType;
	
	/**
 	 * Constructs a new event
 	 * @param eventType a string that describes the type of event
 	 */
	public Event(String eventType) {
		t = 0.0;
		this.eventType = eventType;
	}

	/**
 	 * Constructs an event with event type null
 	 */
	public Event() {
		this(null);
	}

	/**
 	 * set the time when the event should be handled
 	 * @param t the time when the event should be handled
 	 */
	public void setTime(double t) {
		this.t = t;
	}

	/**
 	 * Returns the time when the event should be handled
 	 * @return the time when the event should be handled
 	 */
	public double getT() {
		return t;
	}
	
	/**
 	 * Set which process should handle the event
 	 * @param p the process that should handle the event
 	 */
	public void setProcess(Process p) {
		this.p = p;
	}
	
	/**
 	 * Get which process should handle the event
 	 * @return the process that should handle the event
 	 */
	public Process getProcess() {
		return p;
	}
	
	/**
 	 * Returns the event type
 	 * @return the event type
 	 */
	public String type() {
		return eventType;
	}
	
	public String toString() {
		return "E:" + t + "  ";
	}

}
