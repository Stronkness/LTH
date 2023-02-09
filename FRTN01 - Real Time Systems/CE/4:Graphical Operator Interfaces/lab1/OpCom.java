import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import se.lth.control.*;
import se.lth.control.plot.*;

/** Class that creates and maintains a GUI for the Ball and Beam process.
Uses two internal threads to update plotters */
public class OpCom {
	public static final int OFF = 0, BEAM = 1, BALL = 2;

	private Regul regul;
	private PIParameters innerPar;
	private PIDParameters outerPar;
	private int mode;

	private PlotterPanel measurementPlotter; // has internal thread
	private PlotterPanel controlPlotter; // has internal thread

	private JPanel text = new JPanel();
	private JLabel guilabel = new JLabel("Ball and Beam GUI");

	// Declarartion of main frame.
	private JFrame frame;

	// Declarartion of panels.
	private BoxPanel guiPanel, plotterPanel, innerParPanel, outerParPanel, buttonPanel;
	private JPanel innerParLabelPanel, innerParFieldPanel, outerParLabelPanel, outerParFieldPanel, leftPanel;

	// Declaration of components.
	private DoubleField innerParKField = new DoubleField(5,3);
	private DoubleField innerParTiField = new DoubleField(5,3);
	private DoubleField innerParTrField = new DoubleField(5,3);
	private DoubleField innerParBetaField = new DoubleField(5,3);
	private DoubleField innerParHField = new DoubleField(5,3);
	private JButton innerApplyButton;

	private DoubleField outerParKField = new DoubleField(5,3);
	private DoubleField outerParTiField = new DoubleField(5,3);
	private DoubleField outerParTdField = new DoubleField(5,3);
	private DoubleField outerParTrField = new DoubleField(5,3);
	private DoubleField outerParNField = new DoubleField(5,3);
	private DoubleField outerParBetaField = new DoubleField(5,3);
	private DoubleField outerParHField = new DoubleField(5,3);
	private JButton outerApplyButton;

	private JRadioButton offModeButton;
	private JRadioButton beamModeButton;
	private JRadioButton ballModeButton;
	private JButton stopButton;

	private double range = 10.0; // Range of time axis
	private int divTicks = 5;    // Number of ticks on time axis
	private int divGrid = 5;     // Number of grids on time axis

	private boolean hChanged = false; 

	/** Constructor. Creates the plotter panels. */
	public OpCom() {
		measurementPlotter = new PlotterPanel(2, 4); // Two channels
		controlPlotter = new PlotterPanel(1, 4);
	}

	/** Starts the threads. */
	public void start() {
		measurementPlotter.start();
		controlPlotter.start();
	}

	/** Stops the threads. */
	public void stopThread() {
		measurementPlotter.stopThread();
		controlPlotter.stopThread();
	}

	/** Sets up a reference to Regul. Called by Main. */
	public void setRegul(Regul r) {
		regul = r;
	}

	/** Creates the GUI. Called from Main. */
	public void initializeGUI() {

        //TODO C4.E7: Study the code and make sure you understand it //
        //TODO C4.E8: Change the code in some way and make sure that you understand why the GUI has changed //
		guilabel.setFont(new Font("Serif", Font.PLAIN, 20));
		text.add(guilabel);

		// Create main frame.
		frame = new JFrame("Ball and Beam GUI");

		// Create a panel for the two plotters.
		plotterPanel = new BoxPanel(BoxPanel.VERTICAL);
		// Create plot components and axes, add to plotterPanel.
		measurementPlotter.setYAxis(2, -1, 2, 2);
		measurementPlotter.setXAxis(range, divTicks, divGrid);
		measurementPlotter.setTitle("Set-point and measured variable");
		plotterPanel.add(measurementPlotter);
		plotterPanel.addFixed(10);
		controlPlotter.setYAxis(2, -1, 2, 2);
		controlPlotter.setXAxis(range, divTicks, divGrid);
		controlPlotter.setTitle("Control");
		plotterPanel.add(controlPlotter);

		// Get initail parameters from Regul
		innerPar = regul.getInnerParameters();
		outerPar = regul.getOuterParameters();

		// Create panels for the parameter fields and labels, add labels and fields 
		innerParPanel = new BoxPanel(BoxPanel.HORIZONTAL);
		innerParLabelPanel = new JPanel();
		innerParLabelPanel.setLayout(new GridLayout(0,1));
		innerParLabelPanel.add(new JLabel("K: "));
		innerParLabelPanel.add(new JLabel("Ti: "));
		innerParLabelPanel.add(new JLabel("Tr: "));
		innerParLabelPanel.add(new JLabel("Beta: "));
		innerParLabelPanel.add(new JLabel("h: "));
		innerParFieldPanel = new JPanel();
		innerParFieldPanel.setLayout(new GridLayout(0,1));
		innerParFieldPanel.add(innerParKField); 
		innerParFieldPanel.add(innerParTiField);
		innerParFieldPanel.add(innerParTrField);
		innerParFieldPanel.add(innerParBetaField);
		innerParFieldPanel.add(innerParHField);

		// Set initial parameter values of the fields
		innerParKField.setValue(innerPar.K);
		innerParTiField.setValue(innerPar.Ti);
		innerParTrField.setValue(innerPar.Tr);
		innerParBetaField.setValue(innerPar.Beta);
		innerParHField.setValue(innerPar.H);

		// Add action listeners to the fields
		innerParKField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				innerPar.K = innerParKField.getValue();
				innerApplyButton.setEnabled(true);
			}
		});
		innerParTiField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				innerPar.Ti = innerParTiField.getValue();
				if (innerPar.Ti==0.0) {
					innerPar.integratorOn = false;
				}
				else {
					innerPar.integratorOn = true;
				}
				innerApplyButton.setEnabled(true);
			}
		});
		innerParTrField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				innerPar.Tr = innerParTrField.getValue();
				innerApplyButton.setEnabled(true);
			}
		});
		innerParBetaField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				innerPar.Beta = innerParBetaField.getValue();
				innerApplyButton.setEnabled(true);
			}
		});
		innerParHField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				innerPar.H = innerParHField.getValue();
				outerPar.H = innerPar.H;
				outerParHField.setValue(innerPar.H);
				innerApplyButton.setEnabled(true);
				hChanged = true;
			}
		});

		// Add label and field panels to parameter panel
		innerParPanel.add(innerParLabelPanel);
		innerParPanel.addGlue();
		innerParPanel.add(innerParFieldPanel);
		innerParPanel.addFixed(10);

		// Create apply button and action listener.
		innerApplyButton = new JButton("Apply");
		innerApplyButton.setEnabled(false);
		innerApplyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regul.setInnerParameters(innerPar);
				if (hChanged) {
					regul.setOuterParameters(outerPar);
				}	
				hChanged = false;
				innerApplyButton.setEnabled(false);
			}
		});

		// Create panel with border to hold apply button and parameter panel
		BoxPanel innerParButtonPanel = new BoxPanel(BoxPanel.VERTICAL);
		innerParButtonPanel.setBorder(BorderFactory.createTitledBorder("Inner Parameters"));
		innerParButtonPanel.addFixed(10);
		innerParButtonPanel.add(innerParPanel);
		innerParButtonPanel.addFixed(10);
		innerParButtonPanel.add(innerApplyButton);

		// The same as above for the outer parameters
		outerParPanel = new BoxPanel(BoxPanel.HORIZONTAL);
		outerParLabelPanel = new JPanel();
		outerParLabelPanel.setLayout(new GridLayout(0,1));
		outerParLabelPanel.add(new JLabel("K: "));
		outerParLabelPanel.add(new JLabel("Ti: "));
		outerParLabelPanel.add(new JLabel("Td: "));
		outerParLabelPanel.add(new JLabel("N: "));
		outerParLabelPanel.add(new JLabel("Tr: "));
		outerParLabelPanel.add(new JLabel("Beta: "));
		outerParLabelPanel.add(new JLabel("h: "));

		outerParFieldPanel = new JPanel();
		outerParFieldPanel.setLayout(new GridLayout(0,1));
		outerParFieldPanel.add(outerParKField); 
		outerParFieldPanel.add(outerParTiField);
		outerParFieldPanel.add(outerParTdField);
		outerParFieldPanel.add(outerParNField);
		outerParFieldPanel.add(outerParTrField);
		outerParFieldPanel.add(outerParBetaField);
		outerParFieldPanel.add(outerParHField);
		outerParKField.setValue(outerPar.K);
		outerParTiField.setValue(outerPar.Ti);
		outerParTdField.setValue(outerPar.Td);
		outerParNField.setValue(outerPar.N);
		outerParTrField.setValue(outerPar.Tr);
		outerParBetaField.setValue(outerPar.Beta);
		outerParHField.setValue(outerPar.H);
		outerParKField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outerPar.K = outerParKField.getValue();
				outerApplyButton.setEnabled(true);
			}
		});
		outerParTiField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outerPar.Ti = outerParTiField.getValue();
				if (outerPar.Ti==0.0) {
					outerPar.integratorOn = false;
				}
				else {
					outerPar.integratorOn = true;
				}
				outerApplyButton.setEnabled(true);
			}
		});
		outerParTdField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outerPar.Td = outerParTdField.getValue();
				outerApplyButton.setEnabled(true);
			}
		});
		outerParNField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outerPar.N = outerParNField.getValue();
				outerApplyButton.setEnabled(true);
			}
		});
		outerParTrField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outerPar.Tr = outerParTrField.getValue();
				outerApplyButton.setEnabled(true);
			}
		});
		outerParBetaField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outerPar.Beta = outerParBetaField.getValue();
				outerApplyButton.setEnabled(true);
			}
		});
		outerParHField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outerPar.H = outerParHField.getValue();
				innerPar.H = outerPar.H;
				innerParHField.setValue(outerPar.H);
				outerApplyButton.setEnabled(true);
				hChanged = true;
			}
		});

		outerParPanel.add(outerParLabelPanel);
		outerParPanel.addGlue();
		outerParPanel.add(outerParFieldPanel);
		outerParPanel.addFixed(10);

		outerApplyButton = new JButton("Apply");
		outerApplyButton.setEnabled(false);
		outerApplyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regul.setOuterParameters(outerPar);
				if (hChanged) {
					regul.setInnerParameters(innerPar);
				}	
				hChanged = false;
				outerApplyButton.setEnabled(false);
			}
		});

		BoxPanel outerParButtonPanel = new BoxPanel(BoxPanel.VERTICAL);
		outerParButtonPanel.setBorder(BorderFactory.createTitledBorder("Outer Parameters"));
		outerParButtonPanel.addFixed(10);
		outerParButtonPanel.add(outerParPanel);
		outerParButtonPanel.addFixed(10);
		outerParButtonPanel.add(outerApplyButton);

		// Create panel for the buttons.
		buttonPanel = new BoxPanel(BoxPanel.VERTICAL);
		// Create the buttons.
		offModeButton = new JRadioButton("OFF");
		beamModeButton = new JRadioButton("BEAM");
		ballModeButton = new JRadioButton("BALL");
		stopButton = new JButton("STOP");
		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(offModeButton);
		group.add(beamModeButton);
		group.add(ballModeButton);
		// Button action listeners.
		offModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regul.setOFFMode();
			}
		});
		beamModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regul.setBEAMMode();
			}
		});
		ballModeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regul.setBALLMode();
			}
		});
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regul.shutDown();
				stopThread();
				System.exit(0);
			}
		});

		// Add buttons to button panel.
		buttonPanel.add(offModeButton);
		buttonPanel.add(beamModeButton);
		buttonPanel.add(ballModeButton);
		buttonPanel.add(stopButton);

		// Select initial mode.
		mode = regul.getMode();
		switch (mode) {
		case OFF:
			offModeButton.setSelected(true);
			break;
		case BEAM:
			beamModeButton.setSelected(true);
			break;
		case BALL:
			ballModeButton.setSelected(true);
			break;
		}

		// Create panel holding everything but the plotters.
		leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(innerParButtonPanel, BorderLayout.WEST);
		leftPanel.add(outerParButtonPanel, BorderLayout.EAST);
		leftPanel.add(buttonPanel, BorderLayout.SOUTH);

		// Create panel for the entire GUI.
		guiPanel = new BoxPanel(BoxPanel.HORIZONTAL);
		guiPanel.add(plotterPanel);
		guiPanel.addGlue();
		guiPanel.add(leftPanel);

		// WindowListener that exits the system if the main window is closed.
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				regul.shutDown();
				stopThread();
				System.exit(0);
			}
		});

		// Set guiPanel to be content pane of the frame.
		frame.getContentPane().add(guiPanel, BorderLayout.CENTER);
		frame.add(text, BorderLayout.NORTH);

		// Pack the components of the window.
		frame.pack();

		// Position the main window at the screen center.
		Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension fd = frame.getSize();
		frame.setLocation((sd.width-fd.width)/2, (sd.height-fd.height)/2);

		// Make the window visible.
		frame.setVisible(true);
	}

	/** Called by Regul to put a control signal data point in the buffer. */
	public synchronized void putControlDataPoint(DoublePoint dp) {
		double x = dp.x;
		double y = dp.y;
		controlPlotter.putData(x, y);
	}

	/** Called by Regul to put a measurement data point in the buffer. */
	public synchronized void putMeasurementDataPoint(PlotData pd) {
		double x = pd.x;
		double ref = pd.ref;
		double y = pd.y;
		measurementPlotter.putData(x, ref, y);
	}
}
