package hospital;

import java.util.LinkedList;

import Simulation.*;
import Simulation.Process;

/**
 * This class simulates a hospital. It has a pool of doctors, and a waiting room 
 * with patients. 
 * @author martinh
 *
 */
public class Hospital extends Process {

	/**
 	 * The main function of the program
 	 * @param args args
 	 */
	public static void main(String[] args) {
		Simulation s = new Simulation(false);
		Hospital h = new Hospital(s);
		new Generator(s, h);
		Sampler sampler = new Sampler(s, h);
		new GUI(h);
		s.simulate(SIMULATION_TIME);
		sampler.done();
		System.exit(0);
	}
	
	public static double INTER_ARRIVAL_TIME = 6;
	public static double TREATMENT_TIME = 6;
	public static int NR_DOCTORS = 1;
	public static double SAMPLE_INTERVAL = 60;
	public static double SIMULATION_TIME = 3e8;
	
	private static boolean LOGGING_OM = false;  // true -> print events on std out. 

	private Simulation s;

	private LinkedList<Doctor> doctorPool = new LinkedList<Doctor>();  
	private LinkedList<Patient> waitRoom = new LinkedList<Patient>();
	
	/**
 	 * Constructs a new hospital
 	 * @param s the active simulation
 	 */
	public Hospital(Simulation s) {
		super(s);
		this.s = s;
		for (int i = 0; i<NR_DOCTORS; i++)
			doctorPool.add(new Doctor(s, this, "DR"+(i+1)));
	}
		                                                 	
	public void handleEvent(Event e) {
		Patient p;
		Doctor d;
		switch (e.type()) {
		case "newPatient":  // from generator
			p = ((HospitalEvent) e).getPatient();
			if (!doctorPool.isEmpty()) {
				sendMessage(doctorPool.removeFirst(), e); // "newPatient" to a doctor
			} else {
				waitRoom.add(p);
				log("Patient " + p + " to waitroom");
			}
			break;
		case "patientTreated": // from a doctor
			p = ((HospitalEvent) e).getPatient();
			d = ((HospitalEvent) e).getDoctor();
			log("Patient " + p + " treated by " + d);
			if (!waitRoom.isEmpty()) {
				sendMessage(d, new HospitalEvent("newPatient", waitRoom.removeFirst())); // give the doctor a new patient
			} else 
				doctorPool.add(d);
			break;
		}
	}

	/**
 	 * Returns the number of patients at the hospital, both the ones treated by doctors, 
 	 * and the ones waiting in the waiting room. 
 	 * @return the number of patients at the hospital
 	 */
	public int nrJobs() {
		int nrDoctorsWorking = NR_DOCTORS - doctorPool.size();
		return waitRoom.size() + nrDoctorsWorking;   // queue length + nr occupied servers
	}
	
	/**
 	 * Prints a log-message on std out if loggingOn == true
 	 * @param text the text to print
 	 */
	public void log(String text) {
		if (LOGGING_OM) 
			System.out.println(text + " at " + s.getTime());
	}
	
	/**
	 * Calculates the percentage done as the current simulation time divided 
	 * by the total time to simulate.
	 * @return percentage done (0-100)
	 */
	public double percentageDone() {
		return 100 * s.getTime() / SIMULATION_TIME;
	}
	
}
