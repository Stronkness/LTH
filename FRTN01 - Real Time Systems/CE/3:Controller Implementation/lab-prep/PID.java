// PID class to be written by you
public class PID {
	// Current PID parameters
	private PIDParameters p;

    private double I = 0; // Integral part of PID
    private double D = 0; // Derivative part of PID
    private double v = 0; // Computed control signal
    private double e = 0; // Error signal
    private double y = 0; // Measurement signal
    private double yOld = 0; // Old measurement signal
    private double ad; // Help variable for Derivative calculation
    private double bd; // Help variable for Derivative calculation
	

    /* 
    Used in PI: K = 0.5, Ti = 0.7, Tr = 10, Beta = 1, h = 0.02

    Inner controller: P controller with (K=0.5-10, H=0.02-0.1)
    Outer controller: PD controller with (K=(-0.2)-(-0.01), Td=0.5-4, H=same as for the inner controller, N=5-10).
    Reference Generator: (Amp=4.0, Period=20)
     */


	// Constructor
	public PID(String name) {
        p = new PIDParameters();
        p.Beta = 1;
		p.H = 0.100;
		p.integratorOn = false;
		p.K = -0.350;
		p.Ti = 0.0;
		p.Tr = 10.0;
        p.Td = 0.8;
        p.N = 5;
        ad = p.Td / (p.Td + (p.N*p.H));
        bd = ((p.K * p.Td * p.N)/(p.Td + p.N*p.H));
		new PIDGUI(this, p, name);
        setParameters(p);
        //TODO C3.E8: Write your code here //
    }
	
	// Calculates the control signal v.
	// Called from BeamRegul.
	public synchronized double calculateOutput(double y, double yref) {
        //TODO C3.E8: Write your code here //
        this.y = y;
        e = yref - y;
        D = ad*D - bd*(y - yOld);
        v = p.K*(p.Beta*yref - y) + I + D;
        return v;
    }
	
	// Updates the controller state.
	// Should use tracking-based anti-windup
	// Called from BeamRegul.
	public synchronized void updateState(double u) {
        //TODO C3.E8: Write your code here //
        if(p.integratorOn) I += (p.K*p.H/p.Ti)*e + (p.H/p.Tr)*(u - v);
        else I = 0.0;

        yOld = y;
    }
	
	// Returns the sampling interval expressed as a long.
	// Note: Explicit type casting needed
	public synchronized long getHMillis() {
        //TODO C3.E8: Write your code here //
        return (long)(p.H*1000.0);
    }
	
	// Sets the PIDParameters.
	// Called from PIDGUI.
	// Must clone newParameters.
	public synchronized void setParameters(PIDParameters newParameters) {
        //TODO C3.E8: Write your code here //
		this.p = (PIDParameters)newParameters.clone();
		if(!p.integratorOn) I = 0.0;

        // Required if we update parameters in GUI
		ad = p.Td/(p.Td + p.N*p.H);
		bd = ((p.K * p.Td * p.N)/(p.Td + p.N*p.H));
    }
}
