import se.lth.control.*;
import se.lth.control.plot.*;
import java.util.*;

/** Sinus thread. Generates a sinewave and sends data points to 
    Opcom */
public class Sinus extends Thread {

  private Opcom opcom;
  private double realTime = 0.0;
  private double sinTime = 0.0; 
  private int period = 100;
  private static final double twoPI = 2 * Math.PI;
  private boolean doIt = true;

    // Internal Monitor class

  private Monitor mon = new Monitor();

  class Monitor {
    private double frequency = 0.0;
    private double amplitude = 0.0;

    public synchronized double getAmplitude() {
      return amplitude;  
    }
  
    public synchronized void setAmplitude(double amp) {
      amplitude = amp;
    }

    public synchronized double getFrequency() {
      return frequency;  
    }
  
    public synchronized void setFrequency(double freq) {
      frequency = freq;
    }
  }

  /** Sets up a reference to Opcom. Called from Main. */
  public void setOpcom(Opcom opc) {
    opcom = opc;
  }

  /** Method called by the run method to get the
      current amplitude */    
  public  double getAmplitude() {
      return mon.getAmplitude();
  }

  /** Method called by Opcom to set the current
      amplitude */
  public void setAmplitude(double amp) {
      mon.setAmplitude(amp);
  }

  /** Method called by the run method to get the
      current frequency */    
  public double getFrequency() {
      return mon.getFrequency();
  }

  /** Method called by Opcom to set the current
      frequency */
  public void setFrequency(double freq){
      mon.setFrequency(freq);
  }

  /** Run method */
  public void run() {
    double y, amp;
    long nextTime = System.currentTimeMillis();
    int sleepTime = 0;
    DoublePoint dp;
    // Set thread priority. The default priority is 5. The 
    // event-dispatching thread has priority 6. 
    setPriority(7);
    while (doIt) {
      // Get the amplitude
      amp = getAmplitude();

      // If the amplitude is > 0.9 the amplitude is forced
      // down to 0.1. The resetAmplitude in Opcom is called to
      // update the GUI. Called by another thread than the 
      // event-dispatching thread. In a real application this would
      // have been checked in the statechanged method of the object
      // listening for events from the slider. Here, however, we want to
      // show one modifies a Swing GUI
      // from another thread than the event-dispatching thread.
      if (amp > 0.9) {
	amp = 0.1;
	opcom.resetAmplitude(0.1);
      }
      y = amp * Math.sin(sinTime);

      // New object created to hold a new data point
      dp = new DoublePoint(realTime,y);

      // Sent to Buffer in Opcom
      opcom.putDataPoint(dp);

      // Complicated way of generating a sine wave. 
      // Do not try to understand
      realTime = realTime + ((double)period)/1000;
      sinTime = sinTime + getFrequency()*((double)period)/1000;
      while (sinTime > twoPI) {sinTime = sinTime - twoPI; }

      // Standard Java way of implementing a periodic thread
      nextTime = nextTime + period;
      sleepTime = (int) (nextTime - System.currentTimeMillis());
      if (sleepTime > 0) {
	try {
	  sleep(sleepTime);
	} catch (InterruptedException e) {
	}
      }
    }
  }

  /** Stop the thread */
  public void stopThread() {
    doIt = false;
  }
}



