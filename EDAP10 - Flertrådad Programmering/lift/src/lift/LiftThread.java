package lift;

public class LiftThread extends Thread{
	LiftView view;
	LiftMonitor monitor;
	int counter = 0;
	boolean goingUp = true;
	
	public LiftThread(LiftView view, LiftMonitor monitor) {
		this.view = view;
		this.monitor = monitor;
	}
	
	public void run() {
		monitor.leaveFloor();
		int startFloor = monitor.shouldLiftStay();
		monitor.setMoving(true);
        view.moveLift(0, startFloor - 1);
		counter = startFloor - 1;
		while(true) {
			if(counter == 6) {
				goingUp = false;
			}else if (counter == 0){
				goingUp = true;
			}
			monitor.setMoving(true);
			if(goingUp) {
		        view.moveLift(counter, counter + 1);
		        counter++;
			}else {
		        view.moveLift(counter, counter - 1);
		        counter--;
			}
			monitor.setFloor(counter);
			monitor.setMoving(false);
			if(monitor.shouldOpenDoors(counter)) {
				view.openDoors(counter);
		        monitor.leaveFloor();
		        view.closeDoors();
			}
			
			
		}
	}
	
}
