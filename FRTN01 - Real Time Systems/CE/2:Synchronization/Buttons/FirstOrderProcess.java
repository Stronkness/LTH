import SimEnvironment.*;
import javax.swing.*;
import java.awt.*;

public class FirstOrderProcess extends VirtualProcess {
	private static final int stateNbr=1;  // number of states
	private static final int inputNbr=2;  // number of inputs
	private static final int outputNbr=1; // number of outputs
	
	public FirstOrderProcess() {
		super(stateNbr, inputNbr, outputNbr);
		Plotter plotter = new Plotter(3,100,2,-2);
		getSource(0).setPlotter(plotter,0);
		getSink(0).setPlotter(plotter,1);
		getSink(1).setPlotter(plotter,2);
		JFrame frame = new JFrame("Simulated First-Order Process");
		frame.getContentPane().setLayout(new FlowLayout());
		frame.getContentPane().add(getAnimationPanel());
		frame.getContentPane().add(plotter.getPanel());
		frame.pack();
		frame.setVisible(true);
	}
	
	public double[] computeOutput(double [] state, double[] input) {
		double[] output = new double[outputNbr];
		output[0] = state[0];
		return output;
	}
	
	public double[] updateState(double[] state, double[] input, double h) {
		double[] newState = new double[stateNbr];
		newState[0] = (1 - h)*state[0] + h*input[0];
		return newState;
	}
}

