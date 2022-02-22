package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {
	WashingIO io;
	double upperBound;
	double lowerBound;

    public TemperatureController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        boolean isOn = false;
        boolean reachedTemp = false;
        ActorThread<WashingMessage> sender = null;
		while(true) {
		    try {

                WashingMessage message = receiveWithTimeout(10000 / Settings.SPEEDUP);
                if(message != null) {
                	sender = message.getSender();
                	if(message.getCommand() == WashingMessage.TEMP_SET) {
	                	upperBound = message.getValue();
	                	lowerBound = upperBound - 2;
	                	isOn = true;
						reachedTemp = false;
                	}
                		else if(message.getCommand() == WashingMessage.TEMP_IDLE) {
						io.heat(false);
						reachedTemp = false;
						message.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
						isOn = false;
                			}else {
						
                		}
                }
                
                if(isOn) {
                	if(upperBound > io.getTemperature() && io.getTemperature() >= lowerBound && !reachedTemp) {
                		reachedTemp = true;
                		sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                	}
                	if(upperBound < (io.getTemperature() + 0.478)) {
                		io.heat(false);
                	}else if(lowerBound >= (io.getTemperature() - 0.0952)){
                		io.heat(true);
                	}else {
                		
                	}
                }
                
			} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
    }
}
