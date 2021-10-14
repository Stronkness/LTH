package result.view;

import org.json.JSONObject;
import org.json.JSONPropertyName;
import org.json.JSONStringer;
import org.json.JSONTokener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class ResultView {

    private JFrame frame;
    private JTabbedPane tabPane;
    private final Font buttonTextFont = new Font("Arial", Font.BOLD, 18);
    private File configFile;

    public static void main(String[] args){
        ResultView view = new ResultView();
        view.initiateView();
    }
    public ResultView(){
        frame = new JFrame();
        tabPane = new JTabbedPane();
    }

    public void initiateView(){
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Konfiguration");
        JMenuItem configMenu = new JMenuItem("Importera");
        configMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int result = chooser.showOpenDialog(frame);
                if(result == JFileChooser.APPROVE_OPTION){
                    configFile = chooser.getSelectedFile();
                }
            }
        });
        menu.add(configMenu);
        bar.add(menu);
        frame.setJMenuBar(bar);
        tabPane.addTab("Maraton", MarathonOrLap(true));
        tabPane.addTab("Varvlopp", MarathonOrLap(false));
        frame.add(tabPane);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Dimension frameDim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setPreferredSize(new Dimension((int) (frameDim.getWidth()*0.6),(int) (frameDim.getHeight()*0.8)));
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel MarathonOrLap(boolean isMaraton){
        GridBagConstraints gcon = new GridBagConstraints();
        gcon.weightx = 1;
        gcon.weighty = 1;
        gcon.fill = GridBagConstraints.BOTH;

        JPanel mainPanel = new JPanel();

        JPanel panel = new JPanel();
        BorderLayout borderLayout = new BorderLayout();
        panel.setLayout(borderLayout);

        JPanel fileTitlePanel = new JPanel();
        GridBagLayout gb1 = new GridBagLayout();
        fileTitlePanel.setLayout(gb1);

        JLabel titleLabel = new JLabel("Titel");
        titleLabel.setFont(buttonTextFont);
        setGridBagConstraints(gcon,gb1,titleLabel,0,0,1,GridBagConstraints.REMAINDER,1,1);
        fileTitlePanel.add(titleLabel);

        JTextField titleField = new JTextField();
        titleField.setFont(buttonTextFont);
        titleField.setBorder(BorderFactory.createLineBorder(Color.black));
        titleField.setPreferredSize(new Dimension(30,33));
        setGridBagConstraints(gcon,gb1,titleField,0,1,1,GridBagConstraints.REMAINDER,1,1);
        fileTitlePanel.add(titleField);


        JLabel nameLabel = new JLabel("Namn");
        nameLabel.setFont(buttonTextFont);
        setGridBagConstraints(gcon, gb1,nameLabel,0,2,1,GridBagConstraints.REMAINDER,1,1);
        gb1.setConstraints(nameLabel,gcon);
        fileTitlePanel.add(nameLabel);

        JTextField nameField = new JTextField();
        disableTextField(nameField);
        nameField.setFont(buttonTextFont);
        nameField.setBorder(BorderFactory.createLineBorder(Color.black));
        setGridBagConstraints(gcon,gb1,nameField,0,3,1,2,1,1);
        fileTitlePanel.add(nameField);

        JButton nameButton = new JButton(new String("Välj fil...".getBytes(), StandardCharsets.UTF_8));
        nameButton.setFont(buttonTextFont);
        setGridBagConstraints(gcon,gb1,nameButton,2,3,1,GridBagConstraints.REMAINDER,1,0.5);
        fileTitlePanel.add(nameButton);



        JPanel startPanel = new JPanel();
        startPanel.setLayout(new GridLayout(0,4));
        JLabel startLabel = new JLabel("Start");
        startLabel.setFont(buttonTextFont);
        setGridBagConstraints(gcon,gb1,startLabel,0,4,1,GridBagConstraints.REMAINDER,1,1);
        fileTitlePanel.add(startLabel);

        JTextField masStartField = new JTextField("hh:mm:ss");
        addTextFieldHint(masStartField, "hh:mm:ss");
        masStartField.setHorizontalAlignment(JTextField.CENTER);
        masStartField.setFont(buttonTextFont);
        masStartField.setBorder(BorderFactory.createLineBorder(Color.black));

        JTextField startField = new JTextField();
        startField.setFont(buttonTextFont);
        startField.setBorder(BorderFactory.createLineBorder(Color.black));
        disableTextField(startField);
        setGridBagConstraints(gcon,gb1,startField,0,7,1,2,1,1);
        fileTitlePanel.add(startField);

        JButton startButton = new JButton(new String("Välj fil...".getBytes(), StandardCharsets.UTF_8));
        startButton.setFont(buttonTextFont);
        setGridBagConstraints(gcon,gb1,startButton,2,7,1,GridBagConstraints.REMAINDER,1,0.5);
        fileTitlePanel.add(startButton);

        JLabel massStartCheck = new JLabel("Masstart");
        massStartCheck.setFont(new Font("Arial", Font.BOLD, 13));
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(0,2));
        ButtonGroup sortGroup = new ButtonGroup();
        JCheckBox jaSort = new JCheckBox("Ja");
        jaSort.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(jaSort.isSelected()){
                    startField.setText("");
                    masStartField.setEnabled(true);
                    startButton.setEnabled(false);
                }
            }
        });
        jaSort.setFont(new Font("Arial", Font.BOLD, 13));
        JCheckBox nejSort = new JCheckBox("Nej");
        nejSort.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(nejSort.isSelected()){
                    masStartField.setForeground(Color.gray);
                    masStartField.setText("hh:mm:ss");
                    masStartField.setEnabled(false);
                    startButton.setEnabled(true);
                }
            }
        });
        nejSort.setSelected(true);
        nejSort.setFont(new Font("Arial", Font.BOLD, 13));
        sortGroup.add(jaSort);
        sortGroup.add(nejSort);
        checkboxPanel.add(jaSort);
        checkboxPanel.add(nejSort);

        startPanel.add(massStartCheck);
        startPanel.add(new JPanel());
        startPanel.add(new JPanel());
        startPanel.add(new JPanel());
        startPanel.add(checkboxPanel);
        startPanel.add(masStartField);
        startPanel.add(new JPanel());
        startPanel.add(new JPanel());

        setGridBagConstraints(gcon,gb1,startPanel,0,5,1,GridBagConstraints.REMAINDER,1,1);
        fileTitlePanel.add(startPanel);

        JPanel endOuterPanel = new JPanel();
        GridBagLayout gb3 = new GridBagLayout();
        endOuterPanel.setLayout(gb3);

        JPanel emptyPanel = new JPanel();
        setGridBagConstraints(gcon,gb3,emptyPanel, 0,0,1,GridBagConstraints.REMAINDER,1,1);
        endOuterPanel.add(emptyPanel);

        JLabel endLabel = new JLabel("Slut");
        endLabel.setFont(buttonTextFont);
        setGridBagConstraints(gcon,gb3,endLabel, 0,1,1,2,1,1);
        endOuterPanel.add(endLabel);

        JButton endButton = new JButton(new String("Välj fil...".getBytes(), StandardCharsets.UTF_8));
        endButton.setFont(buttonTextFont);
        setGridBagConstraints(gcon,gb3,endButton,2,1,1,1,0.5,0.52);
        endOuterPanel.add(endButton);

        JPanel endInnerPanel = new JPanel();
        endInnerPanel.setLayout(new GridLayout(0,1));
        setGridBagConstraints(gcon,gb3,endInnerPanel,0,2,GridBagConstraints.REMAINDER,GridBagConstraints.REMAINDER,1,1);
        endOuterPanel.add(endInnerPanel);

        endButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel endFilePanel = new JPanel();
                GridBagLayout gb2 = new GridBagLayout();
                endFilePanel.setLayout(gb2);

                JTextField endFileField = new JTextField();
                setGridBagConstraints(gcon,gb2,endFileField,0,0,3,2,1,1);
                disableTextField(endFileField);
                endFileField.setFont(buttonTextFont);
                endFileField.setPreferredSize(new Dimension(0,30));
                endFileField.setBorder(BorderFactory.createLineBorder(Color.black));
                endFilePanel.add(endFileField);

                JButton removeEndFileButton = new JButton("Ta Bort");
                removeEndFileButton.setFont(buttonTextFont);
                setGridBagConstraints(gcon,gb2,removeEndFileButton,2,0,GridBagConstraints.REMAINDER,1,1,0.1);
                endFilePanel.add(removeEndFileButton);

                JFileChooser chooser = new JFileChooser();
                int result = chooser.showOpenDialog(endFilePanel);
                if(result == JFileChooser.APPROVE_OPTION){
                    File selectedfile = chooser.getSelectedFile();
                    endFileField.setText(selectedfile.getPath());
                    endInnerPanel.add(endFilePanel);
                    endInnerPanel.updateUI();
                }

                removeEndFileButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        endInnerPanel.remove(endFilePanel);
                        endInnerPanel.updateUI();
                    }
                });
            }
        });

        JPanel defaultConfigs;
        if(isMaraton){
            defaultConfigs = MaratonConfigsPanel();

        }else{
            defaultConfigs = VarvloppConfigsPanel();
        }

        JPanel resultPanel = new JPanel();
        JPanel newResultPanel = new JPanel();
        JTextArea resultEditor = new JTextArea();
        JScrollPane resultEditorScroll = new JScrollPane(resultEditor,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JButton newResultButton = new JButton("Editera Resultat");

        Dimension resultDim = Toolkit.getDefaultToolkit().getScreenSize();
        resultEditorScroll.setPreferredSize(new Dimension(20,(int) (resultDim.getHeight() * 0.35)));
        resultPanel.setLayout(new BorderLayout());
        newResultPanel.setLayout(new GridLayout(0,3));
        resultEditor.setFont(buttonTextFont);
        resultEditor.setBorder(BorderFactory.createLineBorder(Color.black));
        newResultButton.setFont(buttonTextFont);

        newResultPanel.add(new JPanel());
        newResultPanel.add(newResultButton);
        newResultPanel.add(new JPanel());

        resultPanel.add(resultEditorScroll,BorderLayout.CENTER);
        resultPanel.add(newResultPanel,BorderLayout.PAGE_END);


        panel.add(fileTitlePanel, BorderLayout.PAGE_START);
        panel.add(endOuterPanel, BorderLayout.CENTER);
        panel.add(defaultConfigs,BorderLayout.PAGE_END);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(panel,BorderLayout.PAGE_START);
        mainPanel.add(resultPanel,BorderLayout.CENTER);

        return mainPanel;
    }

    private void setGridBagConstraints(GridBagConstraints gcon, GridBagLayout gb, JComponent component, int gridx, int gridy
            , int gridheight, int gridwidth, double weighty, double weightx) {
        gcon.gridx = gridx;
        gcon.gridy = gridy;
        gcon.gridheight = gridheight;
        gcon.gridwidth = gridwidth;
        gcon.weightx = weightx;
        gcon.weighty = weighty;
        gb.setConstraints(component,gcon);
    }

    private void disableTextField(JTextField nameField) {
        nameField.setEditable(false);
        nameField.setBackground(UIManager.getColor("TextField.background"));
    }


    private FocusListener getTextFieldFocusListener(JTextField textField, String defaultText){
        return new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if(textField.getText().equals(defaultText)){
                    textField.setText("");
                    textField.setForeground(new Color(50, 50, 50));
                }

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().length() == 0) {
                    textField.setText(defaultText);
                    textField.setForeground(new Color(150, 150, 150));
                }

            }
        };
    }

    private void addTextFieldHint(JTextField textField, String defaultString){
        textField.setForeground(Color.gray);
        textField.addFocusListener(getTextFieldFocusListener(textField,defaultString));
        textField.setFont(buttonTextFont);
        textField.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    private JPanel MaratonConfigsPanel(){
        JPanel defaultConfigs = new JPanel();
        defaultConfigs.setLayout(new GridLayout(0,3));
        JLabel minimiTid = new JLabel("Minimitid");
        minimiTid.setFont(buttonTextFont);
        JLabel sortering = new JLabel("Sortering");
        sortering.setFont(buttonTextFont);
        JLabel resultDest = new JLabel("Destination");
        resultDest.setFont(buttonTextFont);

        JTextField minimiTidText = new JTextField("00:15:00");
        addTextFieldHint(minimiTidText,"00:15:00");
        minimiTidText.setHorizontalAlignment(JTextField.CENTER);


        JTextField resultDestText = new JTextField("Resultat.txt");
        addTextFieldHint(resultDestText,"Resultat.txt");

        JButton resultButton = new JButton("Generera Resultat");
        resultButton.setFont(buttonTextFont);

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(0,2));
        ButtonGroup sortGroup = new ButtonGroup();
        JCheckBox jaSort = new JCheckBox("Ja");
        jaSort.setFont(buttonTextFont);
        JCheckBox nejSort = new JCheckBox("Nej");
        nejSort.setSelected(true);
        nejSort.setFont(buttonTextFont);
        sortGroup.add(jaSort);
        sortGroup.add(nejSort);
        checkboxPanel.add(jaSort);
        checkboxPanel.add(nejSort);

        defaultConfigs.add(minimiTid);
        defaultConfigs.add(sortering);
        defaultConfigs.add(resultDest);
        defaultConfigs.add(minimiTidText);
        defaultConfigs.add(checkboxPanel);
        defaultConfigs.add(resultDestText);
        defaultConfigs.add(new JPanel());
        defaultConfigs.add(new JPanel());
        defaultConfigs.add(resultButton);

        return defaultConfigs;
    }

    private JPanel VarvloppConfigsPanel(){
        JPanel defaultConfigs = new JPanel();
        defaultConfigs.setLayout(new GridLayout(0,4));
        JLabel minimiTid = new JLabel("Minimitid");
        minimiTid.setFont(buttonTextFont);
        JLabel endTime = new JLabel(new String("Måltid".getBytes(), StandardCharsets.UTF_8));
        endTime.setFont(buttonTextFont);
        JLabel sortering = new JLabel("Sortering");
        sortering.setFont(buttonTextFont);
        JLabel resultDest = new JLabel("Destination");
        resultDest.setFont(buttonTextFont);

        JTextField minimiTidText = new JTextField("00:15:00");
        addTextFieldHint(minimiTidText,"00:15:00");
        minimiTidText.setHorizontalAlignment(JTextField.CENTER);

        JTextField endTimeText = new JTextField("13:00:00");
        addTextFieldHint(endTimeText,"13:00:00");
        endTimeText.setHorizontalAlignment(JTextField.CENTER);

        JTextField resultDestText = new JTextField("Resultat.txt");
        addTextFieldHint(resultDestText,"Resultat.txt");

        JButton resultButton = new JButton("Generera Resultat");
        resultButton.setFont(buttonTextFont);

        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new GridLayout(0,2));
        ButtonGroup sortGroup = new ButtonGroup();
        JCheckBox jaSort = new JCheckBox("Ja");
        jaSort.setFont(buttonTextFont);
        JCheckBox nejSort = new JCheckBox("Nej");
        nejSort.setSelected(true);
        nejSort.setFont(buttonTextFont);
        sortGroup.add(jaSort);
        sortGroup.add(nejSort);
        checkboxPanel.add(jaSort);
        checkboxPanel.add(nejSort);

        defaultConfigs.add(minimiTid);
        defaultConfigs.add(endTime);
        defaultConfigs.add(sortering);
        defaultConfigs.add(resultDest);
        defaultConfigs.add(minimiTidText);
        defaultConfigs.add(endTimeText);
        defaultConfigs.add(checkboxPanel);
        defaultConfigs.add(resultDestText);
        defaultConfigs.add(new JPanel());
        defaultConfigs.add(new JPanel());
        defaultConfigs.add(new JPanel());
        defaultConfigs.add(resultButton);

        return defaultConfigs;
    }
}
