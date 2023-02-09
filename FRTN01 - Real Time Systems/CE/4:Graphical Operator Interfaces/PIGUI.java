import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import se.lth.control.*;
import se.lth.control.plot.*;
import javax.swing.event.*;

public class PIGUI {
	private String name;
	private PI pi;
	private PIParameters params;
	private JPanel paramsLabelPanel = new JPanel();
	private JPanel paramsFieldPanel = new JPanel();
	private JPanel sliderFieldPanel = new JPanel();
	private BoxPanel paramsPanel = new BoxPanel(BoxPanel.HORIZONTAL);
	private DoubleField paramsKField = new DoubleField(6,3);
	private DoubleField paramsTiField = new DoubleField(6,3);
	private DoubleField paramsTrField = new DoubleField(6,3);
	private DoubleField paramsBetaField = new DoubleField(6,3);
	private DoubleField paramsHField = new DoubleField(6,3);
	private JButton paramsButton = new JButton("Apply");
	
	private BoxPanel k_panel = new BoxPanel(BoxPanel.HORIZONTAL);
	private JSlider k_slider = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);	
	private BoxPanel ti_panel = new BoxPanel(BoxPanel.HORIZONTAL);
	private JSlider ti_slider = new JSlider(JSlider.HORIZONTAL, -5, 5, 0);	
	private BoxPanel tr_panel = new BoxPanel(BoxPanel.HORIZONTAL);
	private JSlider tr_slider = new JSlider(JSlider.HORIZONTAL, -5, 5, 0);	
	private BoxPanel beta_panel = new BoxPanel(BoxPanel.HORIZONTAL);
	private JSlider beta_slider = new JSlider(JSlider.HORIZONTAL, -5, 5, 0);	
	private BoxPanel h_panel = new BoxPanel(BoxPanel.HORIZONTAL);
	private JSlider h_slider = new JSlider(JSlider.HORIZONTAL, 0, 1, 0);

	private PlotterPanel plotter = new PlotterPanel(1,4);

	public PIGUI(PI pCon, PIParameters p, String n) {
        //TODO C4.E5: Add code to make the PIGUI assimilate the Sinus example //
		name = n;
		pi = pCon;
		params = p;
		MainFrame.showLoading();
		paramsLabelPanel.setLayout(new GridLayout(0,1));
		paramsLabelPanel.add(new JLabel("K: "));
		paramsLabelPanel.add(new JLabel("Ti: "));
		paramsLabelPanel.add(new JLabel("Tr: "));
		paramsLabelPanel.add(new JLabel("Beta: "));
		paramsLabelPanel.add(new JLabel("h: "));

		plotter.setBorder(BorderFactory.createEtchedBorder());
		plotter.setYAxis(2, -1, 2, 2); 
		plotter.setXAxis(10, 5, 5); 
		plotter.setColor(1, Color.red);

		k_slider.setPaintTicks(true);
		k_slider.setMajorTickSpacing(2);
		k_slider.setMinorTickSpacing(1);
		k_slider.setLabelTable(k_slider.createStandardLabels(1));
		k_slider.setPaintLabels(true);
		k_slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(!k_slider.getValueIsAdjusting()){
					double k = k_slider.getValue();
					params.K = k;
					paramsButton.setEnabled(true);
					paramsKField.setValue(params.K);
				}
			}
		});
		
		ti_slider.setPaintTicks(true);
		ti_slider.setMajorTickSpacing(2);
		ti_slider.setMinorTickSpacing(1);
		ti_slider.setLabelTable(ti_slider.createStandardLabels(1));
		ti_slider.setPaintLabels(true);
		ti_slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(!ti_slider.getValueIsAdjusting()){
					double ti = ti_slider.getValue();
					params.Ti = ti;
					paramsButton.setEnabled(true);
					paramsTiField.setValue(params.Ti);
				}
			}
		});
		
		tr_slider.setPaintTicks(true);
		tr_slider.setMajorTickSpacing(2);
		tr_slider.setMinorTickSpacing(1);
		tr_slider.setLabelTable(tr_slider.createStandardLabels(1));
		tr_slider.setPaintLabels(true);
		tr_slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(!tr_slider.getValueIsAdjusting()){
					double tr = tr_slider.getValue();
					params.Tr = tr;
					paramsButton.setEnabled(true);
					paramsTrField.setValue(params.Tr);
				}
			}
		});
		
		beta_slider.setPaintTicks(true);
		beta_slider.setMajorTickSpacing(2);
		beta_slider.setMinorTickSpacing(1);
		beta_slider.setLabelTable(beta_slider.createStandardLabels(1));
		beta_slider.setPaintLabels(true);
		beta_slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(!beta_slider.getValueIsAdjusting()){
					double beta = beta_slider.getValue();
					params.Beta = beta;
					paramsButton.setEnabled(true);
					paramsBetaField.setValue(params.Beta);
				}
			}
		});
		
		h_slider.setPaintTicks(true);
		h_slider.setMajorTickSpacing(2);
		h_slider.setMinorTickSpacing(1);
		h_slider.setLabelTable(h_slider.createStandardLabels(1));
		h_slider.setPaintLabels(true);
		h_slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(!h_slider.getValueIsAdjusting()){
					double h = h_slider.getValue();
					params.H = h;
					paramsButton.setEnabled(true);
					paramsHField.setValue(params.H);
				}
			}
		});
		
		k_panel.add(k_slider);
		ti_panel.add(ti_slider);
		tr_panel.add(tr_slider);
		beta_panel.add(beta_slider);
		h_panel.add(h_slider);

		paramsKField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsKField.getValue();
				params.K = tempValue;
				paramsButton.setEnabled(true);
				k_slider.setValue((int)params.K);
			}
		});
		paramsTiField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsTiField.getValue();
				if (tempValue < 0.0) {
					paramsTiField.setValue(params.Ti);
					ti_slider.setValue((int)params.Ti);
				} else {
					params.Ti = tempValue;
					paramsButton.setEnabled(true);
					params.integratorOn = (params.Ti != 0.0);
					ti_slider.setValue((int)params.Ti);
				}
			}

		});
		paramsTrField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsTrField.getValue();
				if (tempValue < 0.0) {
					paramsTrField.setValue(params.Tr);
					tr_slider.setValue((int)params.Tr);
				} else {
					params.Tr = tempValue;
					paramsButton.setEnabled(true);
					tr_slider.setValue((int)params.Tr);
				}
			}
		});

		paramsBetaField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsBetaField.getValue();
				if (tempValue < 0.0 || tempValue > 1.0) {
					paramsBetaField.setValue(params.Beta);
					beta_slider.setValue((int)params.Beta);
				} else {
					params.Beta = tempValue;
					paramsButton.setEnabled(true);
					beta_slider.setValue((int)params.Beta);
				}
			}
		});

		paramsHField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double tempValue = paramsHField.getValue();
				if (tempValue < 0.0) {
					paramsHField.setValue(params.H);
					h_slider.setValue((int)params.H);
				} else {
					params.H = tempValue;
					paramsButton.setEnabled(true);
					h_slider.setValue((int)(params.H));
				}
			}
		});

		paramsFieldPanel.setLayout(new GridLayout(0,1));
		paramsFieldPanel.add(paramsKField); 
		paramsFieldPanel.add(paramsTiField);
		paramsFieldPanel.add(paramsTrField);
		paramsFieldPanel.add(paramsBetaField);
		paramsFieldPanel.add(paramsHField);
		sliderFieldPanel.setLayout(new GridLayout(0,1));
		sliderFieldPanel.add(k_panel); 
		sliderFieldPanel.add(ti_panel);
		sliderFieldPanel.add(tr_panel);
		sliderFieldPanel.add(beta_panel);
		sliderFieldPanel.add(h_panel);
		paramsPanel.add(paramsLabelPanel);
		paramsPanel.addGlue();
		paramsPanel.add(paramsFieldPanel);
		paramsPanel.addGlue();
		paramsPanel.add(sliderFieldPanel);
		paramsPanel.addGlue();
		paramsPanel.add(plotter, BorderLayout.EAST);
		paramsPanel.addFixed(10);

		paramsKField.setValue(p.K);
		paramsTiField.setValue(p.Ti);
		paramsTrField.setValue(p.Tr);
		paramsBetaField.setValue(p.Beta);
		paramsHField.setValue(p.H);
		k_slider.setValue((int)p.K);
		ti_slider.setValue((int)p.Ti);
		tr_slider.setValue((int)p.Tr);
		beta_slider.setValue((int)p.Beta);
		h_slider.setValue((int)p.H);

		BoxPanel paramButtonPanel = new BoxPanel(BoxPanel.VERTICAL);
		paramButtonPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		paramButtonPanel.addFixed(10);
		paramButtonPanel.add(paramsPanel);
		paramButtonPanel.addFixed(10);
		paramButtonPanel.add(paramsButton);

		paramsButton.setEnabled(false);
		paramsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pi.setParameters(params);
				paramsButton.setEnabled(false);}
		});
		MainFrame.setPanel(paramButtonPanel,name);

		plotter.start();
		plotter.putData(0.5,0.5);
	}
}

