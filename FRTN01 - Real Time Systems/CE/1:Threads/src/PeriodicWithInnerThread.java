public class PeriodicWithInnerThread extends Base {
    private int period;
    private PeriodicThread t;

    public PeriodicWithInnerThread(int period){
        this.period = period;
        t = new PeriodicThread();
    }

    public void start(){
        t.start();
    }

    public class PeriodicThread extends Thread {
        public void run(){
            try {
                while(!Thread.interrupted()){
                    System.out.println(period);
                    System.out.println(", ");
                    Thread.sleep(period);
                }
                
            } catch (Exception e) {
                // TODO: handle exception
            }
            System.out.println("Thread stopped!");
        }
      
    }

    public static void main(String[] args){
        for(String arg : args){
            PeriodicWithInnerThread pt = new PeriodicWithInnerThread(Integer.parseInt(arg));      
            pt.start();
        }
    }
}
