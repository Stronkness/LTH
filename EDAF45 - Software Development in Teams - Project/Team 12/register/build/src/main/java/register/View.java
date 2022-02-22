package register;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

public class View implements Observer {

    private BorderLayout layout;
    private JFrame frame;
    private JPanel regPanel;
    private DefaultListModel<String> participants;
    private JList reggedParticipants;
    private JScrollPane listedParticipants;
    private RegisterModelInterface model;
    private JTextField input;
    private JButton button;

    /*
    Initialiserar GUI for registeringsprogrammet
    */
    public View(){
        frame = new JFrame();
        regPanel = new JPanel();
        layout = new BorderLayout();
        participants = new DefaultListModel<>();
        reggedParticipants = new JList(participants);
        listedParticipants = new JScrollPane(reggedParticipants);
        model =  new RegisterModel("time-", this);
        input = new JTextField("Startnummer");;
        button = new JButton("Registrera");
    }
    /*
     * Vi måste göra testfall för registreringsprogrammet!
     *
     * Vi borde dela upp programmet i model / view.
     * Modellen kan testas med junit. Behöver man testa GUI:t? Kan man ens göra det?
     */
    public void register() {
        try {
            Integer.parseInt(input.getText());
            if(!isInteger(input.getText(), 10)){
                throw new Exception();
            }
            model.writeRegistration(input.getText());
            input.setText("");
        } catch (Exception e) {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String startnumber = "";

            while(!(isInteger(startnumber, 10))) {
                BufferedReader buff = new BufferedReader(
                        new InputStreamReader(
                                new ByteArrayInputStream("Skriv in startnummer för registrering ".getBytes()),
                                Charset.forName("UTF-8")));
                try {
                    startnumber = JOptionPane.showInputDialog(frame, buff.readLine() + time + "!");
                } catch (IOException ioException) {
                }
            }
            model.writeLateRegistration(startnumber, time);
            input.setText("");
        }
    }

    /**
     * Privat metod som används för att se om inputen är en integer
     */
    private boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        if(s.charAt(0) == '-') return false;
        for(int i = 0; i < s.length(); i++) {
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
        /*
        Initialiserar GUI for registeringsprogrammet
        */

    public void initiateView(){
        /*
        Lyssanar på om det registeringknappen och sätter tillbaka fokus på textrutan.
        */


        button.addActionListener(e -> {
            try{
                register();
                input.requestFocusInWindow();
            } catch (Exception e1) {
            }
            // Hur visar vi tiden i GUI:t!?
        });

         /*
        Lyssanar på om det det skrivs något i textrutan och sätter tillbaka fokus på textrutan när det händer.
        */

        input.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    register();
                    input.requestFocusInWindow();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

         /*
        När fokus återfås så sätts texten i textrutan till en tom sträng och när fokus tappas så sätts texten tillbaka
        till "Startnummer".
        */

        input.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                    input.setText("");
                    input.setForeground(new Color(50, 50, 50));
            }

            @Override
            public void focusLost(FocusEvent e) {
                    if (input.getText().length() == 0) {
                        input.setText("Startnummer");
                        input.setForeground(new Color(150, 150, 150));
                    }
                }
        });
            frame.add(regPanel);
            regPanel.setLayout(layout);
            regPanel.add(input,BorderLayout.LINE_START);
            regPanel.add(button, BorderLayout.CENTER);
            regPanel.add(listedParticipants, BorderLayout.PAGE_END);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            input.setPreferredSize(new Dimension(450,(int) (dim.getHeight()*0.2)));
            button.setPreferredSize(new Dimension(450,(int) (dim.getHeight()*0.2)));
            listedParticipants.setPreferredSize(new Dimension(450,(int) (dim.getHeight()*0.65)));
            input.setHorizontalAlignment(JTextField.CENTER);
            Font buttonText = new Font("Comic Sans MS", Font.BOLD, 70);
            input.setFont(buttonText);
            input.setForeground(Color.gray);
            button.setFont(buttonText);
            Font regText = new Font("Comic Sans MS", Font.BOLD, 35);
            listedParticipants.getViewport().getView().setFont(regText);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            listedParticipants.requestFocusInWindow();
            //frame.setPreferredSize(new Dimension(width,height));
            frame.setResizable(false);
            frame.setVisible(true);
    }

        /*
       Update metoden till observer interfacet.
       */
    @Override
    public void update(Observable o, Object arg) {
        participants.addElement((String) arg);
    }

    public RegisterModelInterface getRegisterModel(){
        return model;
    }

}
