package lift;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LiftMonitor {
	private int floor;
	private boolean moving;
	private int direction; ////+1 if lift is going up,-1 if going down
	private int[] waitEntry;
	private int[] waitExit;
	private int load; //number of passengers currently in the lift
	
	private LiftView view;
	
	public LiftMonitor(LiftView view) {
		this.view = view;
		waitEntry = new int[7];
		waitExit = new int[7];
	}
	public synchronized void setFloor(int floor) {
		this.floor = floor;
		notifyAll();
	}
	
	public boolean shouldOpenDoors(int floor) {
		return waitEntry[floor] > 0 || waitExit[floor] != 0;
	}
	
	public synchronized void leaveFloor() {
		while((waitEntry[floor] > 0 && load < 4) || waitExit[floor] != 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}
	
	public synchronized void waitForLift(Passenger pass) {
		waitEntry[pass.getStartFloor()]++;
		notifyAll();
		while(!(floor == pass.getStartFloor() && load < 4 && !moving)) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		pass.enterLift();
		view.showDebugInfo(waitEntry, waitExit);
		notifyAll();
		waitEntry[pass.getStartFloor()]--;
		load++;
	}
	public synchronized void waitForFloor(Passenger pass) {
		waitExit[pass.getDestinationFloor()]++;
		while(!(floor == pass.getDestinationFloor() && !moving)) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		pass.exitLift();
		view.showDebugInfo(waitEntry, waitExit);
		notifyAll();
		waitExit[pass.getDestinationFloor()]--;//
		load--;
	}
	public synchronized void setMoving(boolean moving) {
		this.moving = moving;
	}
	
	public synchronized boolean passengers() {
		for(int i = 0; i < 7; i++) {
			if(waitEntry[i] > 0 || waitExit[i] > 0) {
				return true;
			}
		}
		return false;
	}
	
	public synchronized int shouldLiftStay() {
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i < 7; i++) {
			if(waitEntry[i] > 0) {

				return i;
			}
		}
		return 0;
	}
}
