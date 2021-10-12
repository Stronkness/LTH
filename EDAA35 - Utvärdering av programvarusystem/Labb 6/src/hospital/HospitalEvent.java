package hospital;

import Simulation.*;

/**
 * Event extended with "parameters" Doctor and Patient
 */
public class HospitalEvent extends Event{
	
	private Patient p = null;
	private Doctor d = null;
	
	public HospitalEvent(String eventType, Patient p) {
		super(eventType);
		this.p = p;
	}

	public HospitalEvent(String eventType, Patient p, Doctor d) {
		super(eventType);
		this.p = p;
		this.d = d;
	}

	
	public Patient getPatient() {
		return p;
	}
	
	public Doctor getDoctor() {
		return d;
	}
}
