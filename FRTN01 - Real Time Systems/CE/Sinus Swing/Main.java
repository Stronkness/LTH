import se.lth.control.*;
import javax.swing.*;

/** Main class used only for initialization */
public class Main {

  /** main method called when application starts */
  public static void main(String[] args) {
    final Opcom opcom;
    Sinus sinus;

    opcom = new Opcom();
    sinus = new Sinus();

    // Symmetric reference exchange
    opcom.setSinus(sinus);
    sinus.setOpcom(opcom);

    // Initialization of the Swing GUI
    // Should be done by the Swing thread
    Runnable initializeGUI = new Runnable() {
	public void run() {opcom.initializeGUI();}
    };
    try {
      SwingUtilities.invokeAndWait(initializeGUI);
    }
    catch (Exception x) {
      return;
    }

    // Starting of the two threads
    opcom.start();
    sinus.start();
  }
}
            
