package register;

/**
 * Interface som används för RegisterModel
 */
public interface RegisterModelInterface {
    /**
     * Skriver registreringen till registerprogrammet
     * @param startNo Numret för deltagaren
     */
    public void writeRegistration(String startNo);

    /**
     * Skriver en sen registrering till registerprogrammet
     * @param startNo Numret för deltagaren
     * @param time Sparad tid utifall det blir sen registrering eller felaktig startnummer skrivs in
     */
    public void writeLateRegistration(String startNo, String time);
}


