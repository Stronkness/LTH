package hospital;

import Simulation.*;
import Simulation.Process;

/**
 * This class simulates a doctor at a hospital. 
 * @author martinh
 *
 */
public class Doctor extends Process{
	
	private String name;
	private Patient currentPatient;
	private Hospital h;
	
	/**
 	 * Creates a new Doctor
 	 * @param s the active simulation
 	 * @param h the hospital where the doctor is working
 	 * @param name the name of the doctor
 	 */
	public Doctor(Simulation s, Hospital h, String name) {
		super(s);
		this.name = name; 
		this.h = h;
	}
	
	public void handleEvent(Event e) {
		switch (e.type()) {
		case "newPatient": // from hospital
			currentPatient = ((HospitalEvent) e).getPatient();
			h.log("Patient " + currentPatient + " started treatment by " + this);
			this.setTimer(Rand.exp(1/Hospital.TREATMENT_TIME), new Event("patientTreated"));
			break;
		case "patientTreated" :  // my timer, i.e. patient treated
			sendMessage(h, new HospitalEvent("patientTreated", currentPatient, this));
			break;
		}
	}
	
	public String toString() {
		return name;
	}
	

}
