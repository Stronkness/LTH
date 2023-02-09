import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import se.lth.control.*;
import se.lth.control.plot.*;
import java.util.*;

/** Class for creating and maintaining the GUI. Contains a plotter
 * panel with an internal thread taking care of the plotting. */
public class Opcom {
  private Sinus sinus; // Reference to sine wave generator
  // Declaration of main frame.
  private JFrame frame;
  // Declaration of panels
  private BoxPanel sliderPanel, ampPanel, freqPanel;
  private JPanel guiPanel;
  // Declaration of sliders
  private JSlider ampSlider, freqSlider;
  // Declaration of text labels
  private JLabel ampLabel, freqLabel;

  private PlotterPanel plotter; // Plotter panel


  public Opcom() {
    plotter = new PlotterPanel(1, 4); // One channel, priority 4
  }

  /** Starts the thread in the plotter panel */
  public void start() {
    changeAmplitude(); // Send over the initial amplitude to sinus
    changeFrequency(); // Send over the initial frequency to sinus
    plotter.start();
  }

  /** Sets up a reference to sinus. Called from Main. */
  public void setSinus(Sinus sin) {
    sinus = sin;
  }

  /** Create the GUI. Called from Main. */
  public void initializeGUI() {
    // Create new main window
    frame = new JFrame("SinusDemo");

    // Create all the panels
    // Create the main panel that will become the content pane of frame
    guiPanel = new JPanel();
    guiPanel.setLayout(new BorderLayout());
    // Create the slider panel that will hold the ampPanel 
    // and the freqPanel
    sliderPanel = new BoxPanel(BoxPanel.HORIZONTAL);
    // Create the amp panel that will hold a slider and a label
    ampPanel = new BoxPanel(BoxPanel.VERTICAL);
    // Create the freq panel that will hold a slider and a label
    freqPanel = new BoxPanel(BoxPanel.VERTICAL);

    // Create a vertical sliders. Default range is 0 to 100
    ampSlider = new JSlider(JSlider.VERTICAL);
    ampSlider.setPaintTicks(true);
    ampSlider.setMajorTickSpacing(10);
    ampSlider.setMinorTickSpacing(5);
    ampSlider.setLabelTable(ampSlider.createStandardLabels(10));
    ampSlider.setPaintLabels(true);
    // Add an anonymous ChangeListener to the slider
    ampSlider.addChangeListener(new ChangeListener(){
	public void stateChanged(ChangeEvent e) {changeAmplitude();}
      });
    // Create a vertical slider. Default range is 0 to 100.
    freqSlider = new JSlider(JSlider.VERTICAL);
    freqSlider.setPaintTicks(true);
    freqSlider.setMajorTickSpacing(10);
    freqSlider.setMinorTickSpacing(5);
    freqSlider.setLabelTable(ampSlider.createStandardLabels(10));
    freqSlider.setPaintLabels(true);  
    // Add an anonymous ChangeListener to the slider
    freqSlider.addChangeListener(new ChangeListener() { 
	public void stateChanged(ChangeEvent e) {changeFrequency();}
      });

    // Create a label
    ampLabel = new JLabel("Amp");
    ampLabel.setAlignmentX(0.5F);
    // Build up ampPanel
    ampPanel.add(ampLabel);
    ampPanel.addFixed(10);
    ampPanel.add(ampSlider);

    // Create a label
    freqLabel = new JLabel("Freq");
    freqLabel.setAlignmentX(0.5F);
    // Build up freqPanel
    freqPanel.add(freqLabel);
    freqPanel.addFixed(10);
    freqPanel.add(freqSlider);

    // Build up sliderPanel
    sliderPanel.add(ampPanel);
    sliderPanel.addGlue();
    sliderPanel.add(freqPanel);

    // Set the y-axis and x-axis of the plotter panel: range, bottom,
    // number of divisions for tickmarks number of divisions for the
    // grid
    plotter.setBorder(BorderFactory.createEtchedBorder());
    plotter.setYAxis(2, -1, 2, 2); 
    plotter.setXAxis(10, 5, 5); 
    plotter.setColor(1, Color.red);

    // Build up guiPanel
    guiPanel.add(sliderPanel ,BorderLayout.WEST);
    guiPanel.add(plotter ,BorderLayout.CENTER);

    // Set guiPanel to be the content pane of frame
    frame.setContentPane(guiPanel);

    // Add a WindowListener that exists the system if the main
    // window is closed
    frame.addWindowListener(new WindowAdapter() {
	public void windowClosing(WindowEvent e) {System.exit(0);}
      });
    // Pack the components of the window. The size is calculated.
    frame.pack();

    // Code to position the window at the center of the screen
    Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension fd = frame.getSize();
    frame.setLocation((sd.width - fd.width)/2, (sd.height - fd.height)/2);

    // Make the window visible
    frame.setVisible(true);
  }

  /** Updates the amplitude in sinus. Events are not handled while the
      slider is being moved. */
  private void changeAmplitude() {
    if (!ampSlider.getValueIsAdjusting()) {
      // Scale from 0-100 to 0-1
      double amplitude = 0.01 * ampSlider.getValue(); 
      sinus.setAmplitude(amplitude);
    }
  }

  /** Resets the amplitude to 0.1. Called from Sinus. 
      InvokeLater must be used since this is not the 
      event-dispatching thread */
  public void resetAmplitude(final double amp) { // final important

    // New runnable is created to update ampSlider
    Runnable updateSlider = new Runnable() {
	public void run() {
	  int temp = (int)(amp * 100);
	  ampSlider.setValue(temp);
	};
      };
    // Runnable sent to event-dispatching thread for execution
    SwingUtilities.invokeLater(updateSlider);
  }

  /**  Updates the amplitude in sinus. Events are not handled while the
       slider is being moved. */
  private void changeFrequency() {
    if (!freqSlider.getValueIsAdjusting()) {
      double log = 0.01 * freqSlider.getValue();
      double frequency = 0.5 * Math.pow(10, log);
      sinus.setFrequency(frequency);
    }
  }

  /** Sends a new data point to the plotter panel. Called from
   * Sinus. */
  public void putDataPoint(DoublePoint dp) {
    plotter.putData(dp.x, dp.y);
  }


  public static void main(String[] argv) {

    Opcom o = new Opcom();
    o.initializeGUI();
  }

}




