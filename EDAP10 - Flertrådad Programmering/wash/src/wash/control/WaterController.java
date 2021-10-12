package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class WaterController extends ActorThread<WashingMessage> {

	WashingIO io;

    public WaterController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        try {

            // ... TODO ...


            double waterLevel = 0;
            boolean shouldFill = false;
            boolean shouldDrain = false;
            boolean hasSentMessage = false;
            ActorThread<WashingMessage> sender = null;
            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(5000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                	sender = m.getSender();
                    System.out.println("got " + m);
                    if(m.getCommand() == WashingMessage.WATER_DRAIN) {
                    	io.fill(false);
                    	io.drain(true);
                    	shouldFill = false;
                    	shouldDrain = true;
            			hasSentMessage = false;
                    }else if(m.getCommand() == WashingMessage.WATER_FILL) {
                    	io.drain(false);
                    	shouldDrain = false;
                    	waterLevel = m.getValue();
                    	shouldFill = true;
            			hasSentMessage = false;
                    }else {
                    	io.drain(false);
                    	io.fill(false);
                    }
                }
                
                if(shouldFill) {
                	if(io.getWaterLevel() < waterLevel) {
                		io.fill(true);
                	}else {
                		io.fill(false);                		
                		if(!hasSentMessage) {
                			sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                			hasSentMessage = true;
                		}
                	}
                }else if(shouldDrain) {
                	if(io.getWaterLevel() > 0) {
                		io.drain(true);
                	}else {
                		if(!hasSentMessage) {
                			sender.send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                			hasSentMessage = true;
                		}
                	}
                }
                
                // ... TODO ...
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
