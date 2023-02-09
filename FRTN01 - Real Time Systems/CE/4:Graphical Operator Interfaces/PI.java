// PI class to be written by you
public class PI {
	// Current PI parameters
	private PIParameters p;

    private double I = 0, v = 0, e = 0;

    /**
     * Constructor for the class PI
     */
    public PI() {
        // PIParameters
        PIParameters param = new PIParameters();
        param.Beta = 1.0;
        param.H = 0.1;
        param.integratorOn = false;
        param.K = 1.0;
        param.Ti = 0.0;
        param.Tr = 10.0;

        setParameters(param);
        // PIGUI
        new PIGUI(this, param, "PI");
    }
	
	// Calculates the control signal v.
	// Called from BeamRegul.
	public synchronized double calculateOutput(double y, double yref) {
        e = yref - y;
        v = p.K * (p.Beta * yref - y) + I;
        return v;
    }
	
	// Updates the controller state.
	// Should use tracking-based anti-windup
	// Called from BeamRegul.
	public synchronized void updateState(double u) {
        if (p.integratorOn) {
            // Forward Euler approximation
            I += (p.K * p.H / p.Ti) * e + (p.H / p.Tr) * (u - v); 
        } else {
            I = 0;
        }
    }
	
	// Returns the sampling interval expressed as a long.
	// Note: Explicit type casting needed
	public synchronized long getHMillis() {
        return (long) (p.H * 1000);
    }
	
	// Sets the PIParameters.
	// Called from PIGUI.
	// Must clone newParameters.
	public synchronized void setParameters(PIParameters newParameters) {
        p = (PIParameters) newParameters.clone();
        if (!p.integratorOn) {
            I = 0;
        }
    }

    public static void main(String[] args) {
        new PI();
    }
}
