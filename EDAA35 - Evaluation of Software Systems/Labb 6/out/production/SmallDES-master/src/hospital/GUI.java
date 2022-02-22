package hospital;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * A simple Swing GUI for the simulation
 * @author martinh
 *
 */
public class GUI {
	
	JTextField myText; 
	JProgressBar bar;
	
	/**
	 * Constructs a new GUI.
	 * @param h the simulated hospital
	 */
	public GUI(Hospital h) {
		JFrame window = new JFrame("Simulated hospital");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bar = new JProgressBar(0, 100);
		myText = new JTextField(20);
		myText.setEditable(false);
		new Timer(333, new ActionListener() {      
				public void actionPerformed(ActionEvent e) {
					myText.setText("" + h.nrJobs());
					bar.setValue((int) h.percentageDone());
				}
		}).start();		
		JButton button = new JButton("Stop simulation");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		JPanel myPanel = new JPanel(new BorderLayout());
		myPanel.add(myText, BorderLayout.EAST);
		JTextField nbrPatients = new JTextField("Number patients ");
		nbrPatients.setEditable(false);
		myPanel.add(nbrPatients, BorderLayout.WEST);
		myPanel.add(button, BorderLayout.SOUTH);
		myPanel.add(bar, BorderLayout.NORTH);
		window.add(myPanel);
		window.pack();
		window.setVisible(true);		
	}

}
