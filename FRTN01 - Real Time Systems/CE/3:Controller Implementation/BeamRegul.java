import SimEnvironment.*;

// BeamRegul class to be written by you
public class BeamRegul extends Thread {

    private ReferenceGenerator refGen;
    private PI controller;

    private final double UMIN = -10, UMAX = 10;

	// IO interface declarations
	private AnalogSource analogIn;
	private AnalogSink analogOut;
	private AnalogSink analogRef;
	
	public BeamRegul(ReferenceGenerator refgen, Beam beam, int priority) {
		// Code to initialize the IO
		analogIn = beam.getSource(0);
		analogOut = beam.getSink(0);
		analogRef = beam.getSink(1);

        //TODO C3.E3: Write your code here //
        refGen = refgen;
        setPriority(priority);
        controller = new PI("PI");
	}

    /**
     * Method limit:
     *
     * @param:
     *     u (double): The signal to saturate
     * @return:
     *     double: the saturated value
     */
    private double limit(double u) {
        if (u < this.UMIN) 
            return this.UMIN;
        else if (u > this.UMAX) 
            return this.UMAX;
        else 
            return u;
    }
	
	public void run() {

        //TODO C3.E3: Write your code here and help variables here //
        long t = System.currentTimeMillis();

        while (!interrupted()) {
            // Read inputs
            double y = analogIn.get();
            //TODO C3.E3: Write your code here //
            double ref = refGen.getRef();

            synchronized(controller){
                // Compute control signal
                double u = controller.calculateOutput(y, ref);
                u = limit(u);
                // Set output
                analogOut.set(u);
    
                // Update state
                controller.updateState(u);
            }
            
            // Set reference in gui
            analogRef.set(ref);
            
            //TODO C3.E3: Write code for control computation here //
            //TODO C3.E3: Write code to make run method periodic here //
            t += controller.getHMillis();
            long duration = t - System.currentTimeMillis();
            if (duration > 0){
                try {
                    sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
	}
}
