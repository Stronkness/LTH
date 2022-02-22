package register;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;

/**
 * Sköter registreringen
 */
public class RegisterModel extends Observable implements RegisterModelInterface  {

    Path path;
    String formatedTime;

    /**
     * Skapar en konstruktor som tar in pathString och en observer som sätter pathingen och använder den lokala tiden
     * @param pathString path till filerna
     * @param o Observer som håller koll på registreringsprogrammet
     */
    //TODO improve: what to do if the file already exists?
    public RegisterModel(String pathString,Observer o) {
        addObserver(o);
        path = Paths.get(pathString +
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH.mm.ss")) + ".txt");
        if(!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * Skriver registreringen till registerprogrammet
     * @param startNo Numret för deltagaren
     */
    @Override
    public void writeRegistration(String startNo) {
        formatedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        writeToFile(startNo);

    }

    /**
     * Skriver en sen registrering till registerprogrammet
     * @param startNo Numret för deltagaren
     * @param time Sparad tid utifall det blir sen registrering eller felaktig startnummer skrivs in (lokal tid i GUI)
     */
    public void writeLateRegistration(String startNo, String time) {
        formatedTime = time;
        writeToFile(startNo);
    }

    /**
     * Tar numret för deltagaren och bygger en sträng med den + den lokala tiden som skriver till en fil
     * @param startNo numret på deltagaren
     */
    private void writeToFile(String startNo) {
        String string = startNo + "; " + formatedTime;
        try {
            Files.write(path, Collections.singleton(string), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            setChanged();
            notifyObservers(string);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

}