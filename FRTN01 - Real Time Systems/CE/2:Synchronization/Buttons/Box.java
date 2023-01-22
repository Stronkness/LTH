import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import SimEnvironment.*;

public class Box {
	private JFrame frame = new JFrame("Button Box");
	private DigitalButtonSink onOutputInput, offOutputInput;
	private DigitalButtonSource incInput, decInput;
	private JPanel mainPanel;
	private JPanel onPanel, offPanel;
	private JPanel incInputPanel, decInputPanel;
	
	public Box() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1,4));
		onOutputInput = new DigitalButtonSink(1,"On");
		onPanel = onOutputInput.getPanel();
		offOutputInput = new DigitalButtonSink(2,"Off");
		offPanel = offOutputInput.getPanel();
		mainPanel.add(onPanel);
		mainPanel.add(offPanel);
		incInput = new DigitalButtonSource(3,"Inc");
		incInputPanel = incInput.getPanel();
		decInput = new DigitalButtonSource(4,"Dec");
		decInputPanel = decInput.getPanel();
		mainPanel.add(incInputPanel);
		mainPanel.add(decInputPanel);
		
		frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		frame.addWindowListener(new WindowAdapter() {
			public void  windowClosing(WindowEvent e) {System.exit(0);}});
		frame.pack();
		frame.setVisible(true);
	}
	
	public DigitalButtonIn getOnButtonInput() {
		return onOutputInput;
	}
	
	public DigitalButtonOut getOnButtonLamp() {
		return onOutputInput;
	}
	
	public DigitalButtonIn getOffButtonInput() {
		return offOutputInput;
	}
	
	public DigitalButtonOut getOffButtonLamp() {
		return offOutputInput;
	}
	
	public DigitalButtonIn getIncButtonInput() {
		return incInput;
	}
	
	public DigitalButtonIn getDecButtonInput() {
		return decInput;
	}
	
	public static void main(String[] args) {
		new Box();
	}
}
