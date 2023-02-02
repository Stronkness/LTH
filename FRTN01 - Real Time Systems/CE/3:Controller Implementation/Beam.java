import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import SimEnvironment.*;
/**
 * Beam är den virtuella processen för bommen utan kula. Följande beskrivning av processen har en insignal, en utsignal och ett tillstånd.
 * <pre>
 * Tillståndsvektor:
 *
 *                   ç(t) = | phi(t) |    (Vinkeln på bommen)
 *
 * Tillståndsekvationer:
 *
 *                  dç/dt = | 0  0  0  |*ç(t) + |  kPhi  |*u(t)     ( kPhi=4.4 )
 *
 *
 *                   y(t) = | 0  0  1  |        |phi(t)|
 *
 *
 * Definitionen på in- och utgångar är:
 *
 * Ingång 0: Styrsignal till processen.
 * Utgång 0: Vinkeln på bommen.
  * </pre>
 * Det finns även en animering implementerad till denna virtuella process. För att visa animering använd metoden getAnimationPanel i VirtualProcess och lägg in panel i ett fönster (se exempel 2 i <A HREF="../SimEnvironment/VirtualProcess.html">VirtualProcess</A>). Uppdateringen av animeringen sker automatiskt med en animeringstråd i Vir
tualProcess.<P>
 *
 * Created: Mon Nov  6 11:56:27 2000
 *
 * @author Jonas Ahnlund
 * @author Tord Bergquist
 * @author Mats Lindestam
 * @author Anders Martinsson
 * @version 1.0
 */

public class Beam extends VirtualProcess {
    
    private static final int stateNbr=1;  //number of states
    private static final int inputNbr=2;  //number of inputs
    private static final int outputNbr=1; //number of outputs

    private double kPhi=4.4; //process coefficient for angle

    private double scale = 100.0;
    private RoundRectangle2D box = new RoundRectangle2D.Double(100.0, 80.0, 100.0, 50.0, 10.0, 10.0);
    private Ellipse2D axis = new Ellipse2D.Double(144, 109.0, 12.0, 12.0);
    private Rectangle2D beam = new Rectangle2D.Double(50.0, 120.0, 200.0, 5.0);
    private boolean init = false;


    /**
     * Creates an instance of the virtual process Beam
     */
    public Beam() {
	super(stateNbr, inputNbr, outputNbr);
	Plotter plotter = new Plotter(3,100,10,-10);
	getSource(0).setPlotter(plotter,0);
	getSink(0).setPlotter(plotter,1);
	getSink(1).setPlotter(plotter,2);
	JFrame frame = new JFrame("Virtual Beam");
	frame.getContentPane().setLayout(new GridBagLayout());
        JPanel jp = getAnimationPanel();
        jp.setPreferredSize(new Dimension(300,200));
        frame.getContentPane().add(jp); // add animation panel
	frame.getContentPane().add(plotter.getPanel());
        frame.pack();
        frame.setVisible(true);
    }
    
    /**
     * Calculates new output signals for the virtual process Beam
     *
     * @param state process state
     * @param input process input
     * @return new outputs for the process
     */
    public double[] computeOutput(double[] state, double[] input) {
	double[] output = new double[outputNbr];
	output[0] = state[0];                          //update beam angle
	return output;
    }

    /**
     * Calculates new states for the virtual process Beam
     *
     * @param state process state
     * @param input process inputs
     * @param h time difference
     * @return new process state
     */
    public double[] updateState(double[] state, double[] input, double h) {
	double ulim;
	double[] newState = new double[stateNbr];

	//Euler forward approximation
	ulim = limit(input[0],-10,10);
	newState[0] = state[0] + kPhi*h*ulim;   //update beam angle
	return newState;
    }

    /**
     * Animation of virtual process Beam
     *
     * @param g2 drawing area
     * @param jp JPanel used
     * @param state process state
     * @param input process inputs
     * @param output process outputs
     */
    public void draw(Graphics2D g2, JPanel jp, double[] state, 
		     double[] input, double[] output) {
	if (!init) {
	    jp.addMouseListener(new MouseListener() {
		public void mouseClicked(MouseEvent e) {
		    resetProcess();
		    init = true;
		}
		public void mousePressed(MouseEvent e){}
		public void mouseEntered(MouseEvent e){}
		public void mouseReleased(MouseEvent e){}
		public void mouseExited(MouseEvent e){}

	    });
	}
	scale = Math.min(jp.getWidth()/300.0, jp.getHeight()/200.0);
	g2.scale(scale, scale);                       //scale the following animation
	g2.setColor(Color.red);
	g2.fill(box);                                 //animate background
	g2.setColor(Color.black);
	g2.drawString("Beam",122,95);
	g2.draw(box); 
	g2.rotate(-1*output[0]*Math.PI/40,150,115);   //rotate the following with the beam angle
	g2.setColor(Color.darkGray);
	g2.fill(axis);                                //animate axis
	g2.fill(beam);                                //animate beam
//	g2.setColor(Color.blue);
    }

    private double limit(double v, double min, double max) {
	if (v < min) {
	    v = min;
	} else {
	    if (v > max) {
		v = max;
	    }
	}
	return v;
    }



} // Beam




