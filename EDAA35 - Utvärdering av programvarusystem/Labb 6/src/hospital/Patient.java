package hospital;

/**
 * A class that represents a patient at the hospital. 
 * @author martinh
 *
 */
public class Patient {
	
	private String name;
	
    /**
    * Constructs a new patient
    * @param name the patient's name
 	*/
	public Patient(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}

}
