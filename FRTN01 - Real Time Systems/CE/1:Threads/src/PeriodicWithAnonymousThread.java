public class PeriodicWithAnonymousThread extends Base {
    private int period;
    private Thread t;

    public PeriodicWithAnonymousThread(int per) {
        period = per;
        t = new Thread() {
            public void run() {
                try {
                    while (!Thread.interrupted()) {
                        System.out.print(period);
                        System.out.print(", ");
                        Thread.sleep(period);
                    }
                } catch (InterruptedException e) {
                    // Requested to stop
                }
                System.out.println("Thread stopped.");
            }
        };
    }

    public void start() {
        t.start();
    }

    public static void main(String[] args) {
        for (String arg : args) {
            PeriodicWithAnonymousThread p = new PeriodicWithAnonymousThread(Integer.parseInt(arg));
            p.start();
        }
    }
}