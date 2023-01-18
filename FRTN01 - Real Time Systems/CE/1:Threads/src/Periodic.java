import java.util.Scanner;

public class Periodic extends Thread{
    private int pi;

    public Periodic(int period){
        pi = period;
    }

    public void run(){
        System.out.println(getPriority());
        setPriority(7);
        System.out.println(getPriority());
        try {
            while(!Thread.interrupted()){
                System.out.println(pi);
                System.out.println(", ");
                Thread.sleep(pi);
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
        System.out.println("Thread stopped!");
    }

    public static void main(String[] args) {
        for (String arg : args){
            new Periodic(Integer.parseInt(arg)).start();
        }

        System.out.println(Thread.activeCount());
    }
}
