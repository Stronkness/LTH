public class Consumer extends Thread {
	private RingBuffer rb;
	
	public Consumer(RingBuffer r) {
		rb = r;
	}
	
	public void run() {
		try {
			Thread.sleep(10000);
			while(!Thread.interrupted()) {
				System.out.println("Consumer: Attempting to read a message");
				String msg = (String)rb.get();
				System.out.println("Consumer: Read \"" + msg + "\"");
			}
		} catch (InterruptedException e) {
			// Requested to stop
		}
		
		System.out.println("Consumer stopped.");
	}
}
