public class PeriodicRunnable extends Base implements Runnable {
    private int pi;

    public PeriodicRunnable(int period){
        pi = period;
    }

    public void run(){
        System.out.println(Thread.currentThread().getPriority());
        Thread.currentThread().setPriority(7);
        System.out.println(Thread.currentThread().getPriority());
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
            PeriodicRunnable p = new PeriodicRunnable(Integer.parseInt(arg));
            Thread m = new Thread(p);
            m.start();
        }

        System.out.println(Thread.activeCount());
    }

}
