import java.util.Scanner;

public class PeriodicWithScreen extends Thread{
    private int pi;
    private Screen screen;

    public PeriodicWithScreen(int period, Screen screen){
        pi = period;
        this.screen = screen;
    }

    public void run(){
        System.out.println(getPriority());
        setPriority(7);
        System.out.println(getPriority());
        try {
            while(!Thread.interrupted()){
                screen.writePeriod(pi);
                Thread.sleep(pi);
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        System.out.println("Thread stopped!");
    }

    public static void main(String[] args) {
        Screen screen = new Screen();
        for (String arg : args){
            new PeriodicWithScreen(Integer.parseInt(arg), screen).start();
        }

        System.out.println(Thread.activeCount());
    }
}
