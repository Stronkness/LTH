import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleGUI {
  private String name;

  public SimpleGUI(String name){
    JFrame frame = new JFrame(name);
    JPanel pane = new JPanel();
    JButton button = new JButton("Click me!");
    JButton quit = new JButton("Quit");
    JLabel label = new JLabel(" ");

    button.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e){
            label.setText("Button was clicked");
            SwingUtilities.invokeLater(() -> {
                System.out.println(Thread.currentThread().getPriority());
            });
        }
    });

    quit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            System.out.println(Thread.currentThread().getPriority());
            System.exit(0);
        }
    });

    pane.setLayout(new BorderLayout());
    pane.add(button, BorderLayout.SOUTH);
    pane.add(quit, BorderLayout.NORTH);
    pane.add(label, BorderLayout.CENTER);

    frame.getContentPane().add(pane, BorderLayout.CENTER);

    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }  

  public static void main(String[] args){
    //new SimpleGUI("PID GUI");
    new PI();
  }
}