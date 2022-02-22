package Simulation;

/**
 * An abstract class that represents a process in a simulation model. 
 * All classes that extends the class must implement method handleEvent
 * @author martinh
 *
 */
public abstract class Process {
	
	protected Simulation s;	
	
	/** 
 	 * Constructs a new process
 	 * @param s the active simulation
 	 */
	public Process (Simulation s) {
		this.s = s;
	}
		
	/**
 	 * Abstract method that describes the behavior of the process when 
 	 * an event is received  
 	 * @param e the received event
 	 */
	public abstract void handleEvent(Event e);
		
	/**
 	 * sets a timer by sending an event to itself after a specified time 
 	 * @param duration time until the event should be received
 	 * @param e the event that is sent
 	 */
	protected void setTimer(double duration, Event e) {
		e.setProcess(this);
		e.setTime(duration + s.getTime());
		s.addEvent(e);
	}
	
	/**
 	 * Sends an event to another process
 	 * @param p the process that should receive the event
 	 * @param e the event that should be sent
 	 */
	protected void sendMessage(Process p, Event e) {
		e.setProcess(p);
		e.setTime(s.getTime());
		s.addEvent(e);
	}
	

}
