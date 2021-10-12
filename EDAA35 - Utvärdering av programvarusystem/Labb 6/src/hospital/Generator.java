package hospital;

import Simulation.Event;
import Simulation.Process;
import Simulation.Rand;
import Simulation.Simulation;

/**
 * This class simulates a "generator" that generates patients to a hospital. new
 * patients are generated at random times and then sent to the hospital.  
 * @author martinh
 *
 */
public class Generator extends Process {
	
	private Hospital h;
	int n = 0;           // nr generated patients
	
	/**
 	 * Constructs a new generator. 
 	 * @param s the active simulation
 	 * @param h the hospital where the patients should be treated 
 	 */
	public Generator(Simulation s, Hospital h) {
		super(s);
		this.h = h;
		this.setTimer(Rand.exp(1/Hospital.INTER_ARRIVAL_TIME), new Event());  // start the generator
	}

	public void handleEvent(Event e) {
		n++;
		sendMessage(h, new HospitalEvent("newPatient", new Patient("P"+n)));
		this.setTimer(Rand.exp(1/Hospital.INTER_ARRIVAL_TIME), new Event());		
	}
}
