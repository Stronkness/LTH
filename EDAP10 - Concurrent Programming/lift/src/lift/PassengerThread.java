package lift;

public class PassengerThread extends Thread {
	LiftView view;
	LiftMonitor monitor;
	int counter;
	
	public PassengerThread(LiftView view, LiftMonitor monitor) {
		this.view = view;
		this.monitor = monitor;
	}
	
	public void run() {
		while(true) {
	        Passenger pass = view.createPassenger();
	        try {
				Thread.sleep((long)(Math.random() * 45000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		        pass.begin();
		        monitor.waitForLift(pass);
		        monitor.waitForFloor(pass);
		        pass.end();
			}
	}

}
