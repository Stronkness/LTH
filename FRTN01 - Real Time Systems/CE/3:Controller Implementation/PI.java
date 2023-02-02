// PI class to be written by you
public class PI {
	// Current PI parameters
	private PIParameters p;

    private double I = 0; // Integral part of controller
    private double e = 0; // Error signal
    private double v = 0; // Output from controller
	
	// Constructor
	public PI(String name) {
		p = new PIParameters();
		p.Beta = 1.0;
		p.H = 0.02;
		p.integratorOn = false;
		p.K = 0.5;
		p.Ti = 0.7;
		p.Tr = 10.0;
		new PIGUI(this, p, name);
        //TODO C3.E2: Write your code here //
    }
	
	// Calculates the control signal v.
	// Called from BeamRegul.
	public synchronized double calculateOutput(double y, double yref) {
		this.e = yref - y;
		this.v = p.K * (p.Beta * yref - y) + I;
        //TODO C3.E2: Write your code here //
        return this.v;
    }
	
	// Updates the controller state.
	// Should use tracking-based anti-windup
	// Called from BeamRegul.
	public synchronized void updateState(double u) {
		if(p.integratorOn) I += (p.K*p.H/p.Ti)*e + (p.H/p.Tr)*(u - v);
        else I = 0.0;
		//TODO C3.E2: Write your code here //
    }
	
	// Returns the sampling interval expressed as a long.
	// Note: Explicit type casting needed
	public synchronized long getHMillis() {
        //TODO C3.E2: Write your code here //
        return (long)(p.H*1000.0);
    }
	
	// Sets the PIParameters.
	// Called from PIGUI.
	// Must clone newParameters.
	public synchronized void setParameters(PIParameters newParameters) {
		p = (PIParameters)newParameters.clone();
		if(!p.integratorOn) I = 0.0;
        //TODO C3.E2: Write your code here //
    }
}
