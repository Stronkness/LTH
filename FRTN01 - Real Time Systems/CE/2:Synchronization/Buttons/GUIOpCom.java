import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class GUIOpCom {
	private Regul regul;
	private JSlider slider;
	
	public GUIOpCom(Regul r, int p) {
		regul = r;
		JFrame frame = new JFrame("K");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new FlowLayout());
		slider = new JSlider(JSlider.VERTICAL, 0, 10, 1);
		slider.setPaintTicks(true);
		slider.setMajorTickSpacing(2);
		slider.setMinorTickSpacing(1);
		slider.setLabelTable(slider.createStandardLabels(10));
		slider.setPaintLabels(true);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				changeGain();
			}
		});
		mainPanel.add(slider);
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		frame.pack();
		frame.setVisible(true);
	}
	
	private void changeGain() {
		if (!slider.getValueIsAdjusting()) {
			regul.setK(slider.getValue());
		}
	}
	
	public void start() {
		changeGain();
	}
}
