// BallAndBeamRegul class to be written by you

import SimEnvironment.*;

public class BallAndBeamRegul extends Thread {

    // IO Declarations 
    private AnalogSource analogInAngle;
    private AnalogSource analogInPosition;
    private AnalogSink analogOut;
    private AnalogSink analogRef;

    private ReferenceGenerator refGen;
    private PID pid;
    private PI pi;

    // Limits
    private final double UMIN = -10, UMAX = 10;

	// Constructor
    public BallAndBeamRegul(ReferenceGenerator refgen, BallAndBeam bb, int priority) {
        // Code to initialize the IO
        analogInPosition = bb.getSource(0);
        analogInAngle = bb.getSource(1);
        analogOut = bb.getSink(0);
        analogRef = bb.getSink(1);

        //TODO C3.E8: Write your code here //
        refGen = refgen;
        setPriority(priority);
        pid = new PID("PID");
        pi = new PI("PI");
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
        //TODO C3.E8: Define help variables here //
        long t = System.currentTimeMillis();

        while (!interrupted()) {
            //TODO C3.E8: Write your code here //
            double y_1 = analogInPosition.get();
            double y_2 = analogInAngle.get();
            double ref = refGen.getRef();
            
            double u1;
            double u2;
            
            synchronized(pid){
                u1 = pid.calculateOutput(y_1, ref);
                u1 = limit(u1);
                pid.updateState(u1);
            }

            synchronized(pi){
               u2 = pi.calculateOutput(y_2, u1);
               u2 = limit(u2);
               analogOut.set(u2);
               pi.updateState(u2); 
            }

            analogRef.set(ref);

            t += pid.getHMillis();
            long duration = t - System.currentTimeMillis();
            if(duration > 0){
                try {
                    sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}
