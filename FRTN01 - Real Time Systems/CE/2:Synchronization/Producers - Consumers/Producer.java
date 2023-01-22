public class Producer extends Thread {
	private RingBuffer rb;
	
	public Producer(RingBuffer r) {
		rb = r;
	}
	
	public void run() {
		int i = 0;
		try {
			while(!Thread.interrupted()) {
				++i;
				System.out.println("Producer: Attempting to write msg nbr " + i);
				rb.put("Msg nbr " + i);
				System.out.println("Producer: Msg nbr " + i + " written");
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// Thread interrupted
		}
		
		System.out.println("Producer stopped.");
	}
}
