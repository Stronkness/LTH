import se.lth.control.*;

/** Dummy Regul class for exercise 4. Generates and sends sinewaves to OpCom
and replies with print-outs when the set methods are called. */
public class Regul extends Thread {
	public static final int OFF=0, BEAM=1, BALL=2;
	
	private PIParameters innerPar;
	private PIDParameters outerPar;
	private OpCom opcom;
	
	private int mode;
	
	private double amp = 0.5; // Amplitude of sinewaves
	private double freq = 1.0; // Frequency of sinewaves
	private double realTime = 0.0;
	private double sinTime = 0.0; // between 0 and 2*pi
	private static final double twoPI = 2 * Math.PI;
	
	private boolean doIt = true;
	
	/** Constructor. Sets initial values of the controller parameters and initial mode. */
	public Regul() {
		innerPar = new PIParameters();
		innerPar.K = 4.0;
		innerPar.Ti = 0.0;
		innerPar.Tr = 10.0;
		innerPar.Beta = 1.0;
		innerPar.H = 0.05;
		innerPar.integratorOn = false;
		
		outerPar = new PIDParameters();
		outerPar.K = -0.05;
		outerPar.Ti = 0.0;
		outerPar.Td = 2.0;
		outerPar.Tr = 10.0;
		outerPar.N = 10.0;
		outerPar.Beta = 1.0;
		outerPar.H = 0.05;
		outerPar.integratorOn = false;
		
		mode = OFF;
	}
	
	/** Sets up a reference to OpCom. Called from Main. */
	public void setOpCom(OpCom o) {
		opcom = o;
	}
	
	/** Run method. Sends data periodically to OpCom. */
	public void run() {
		final long h = 100; // period (ms)
		long duration;
		long t = System.currentTimeMillis();
		DoublePoint dp;
		PlotData pd;
		double y, r, u;
		
		setPriority(7);
		
		while (doIt) {
			y = amp * Math.sin(sinTime);
			r = amp * Math.cos(sinTime);
			u = amp * Math.sin(sinTime);
			
			pd = new PlotData();
			pd.y = y;
			pd.ref = r;
			pd.x = realTime;
			opcom.putMeasurementDataPoint(pd);
			
			dp = new DoublePoint(realTime,u);
			opcom.putControlDataPoint(dp);
			
			realTime += ((double) h)/1000.0;
			sinTime += freq*((double) h)/1000.0;
			while (sinTime > twoPI) {sinTime -= twoPI; }
			
			t += h;
			duration = (int) (t - System.currentTimeMillis());
			if (duration > 0) {
				try {
					sleep(duration);
				} catch (Exception e) {}
			}
		}
	}
	
	/** Stops the thread. */
	private void stopThread() {
		doIt = false;
	}
	
	/** Called by OpCom to set the parameter values of the inner loop. */
	public synchronized void setInnerParameters(PIParameters p) {
		System.out.println("Parameters changed for inner loop");
	}
	
	/** Called by OpCom during initialization to get the parameter values of the inner loop. */
	public synchronized PIParameters getInnerParameters() {
		return (PIParameters) innerPar.clone(); 
	}
	
	/** Called by OpCom to set the parameter values of the outer loop */
	public synchronized void setOuterParameters(PIDParameters p) {
		System.out.println("Parameters changed for outer loop");
	}
	
	/** Called by OpCom during initialization to get the parameter values of the outer loop. */
	public synchronized PIDParameters getOuterParameters() {
		return (PIDParameters) outerPar.clone(); 
	}
	
	/** Called by OpCom to turn off the controller. */
	public synchronized void setOFFMode() {
		System.out.println("Controller turned OFF");
	}
	
	/** Called by OpCom to set the Controller in BEAM mode. */
	public synchronized void setBEAMMode() {
		System.out.println("Controller in BEAM mode");
	}
	
	/** Called by OpCom to set the Controller in BALL mode. */
	public synchronized void setBALLMode() {
		System.out.println("Controller in BALL mode");
	}
	
	/** Called by OpCom during initialization to get the initail mode of the controller. */
	public synchronized int getMode() {
		return mode;
	}
	
	/** Called by OpCom when the Stop button is pressed. */
	public synchronized void shutDown() {
		stopThread();
	}
}
