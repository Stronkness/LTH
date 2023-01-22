public class PeriodicStaticLock extends Thread {
	private int period;
	private static Object lock = new Object();
	
	public PeriodicStaticLock(int p) {
		period = p;	
	}
	
	public void run() {
		// Uncomment to print out default priority
		//System.out.println("Priority = " + getPriority());
		
		// Uncomment to set priority
		//setPriority(getPriority() + 1);
		
		try {
			while (!Thread.interrupted()) {
				synchronized (lock) {
					System.out.print(period);
					System.out.print(", ");
				}
				sleep(period);
			}
		} catch (InterruptedException e) {
			// Requested to stop
		}
		System.out.println("Thread stopped.");
	}
	
	public static void main(String[] argv) {
		for (int i = 0; i < argv.length; i++) {
			PeriodicStaticLock m = new PeriodicStaticLock(Integer.parseInt(argv[i]));
			m.start();
		}
	}
}
