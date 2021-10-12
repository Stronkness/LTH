package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {

    WashingIO io;

    public SpinController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
    	boolean shouldSpin = false;
    	boolean spinRight = false;
        try {

            // ... TODO ...

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    System.out.println("got " + m);
                    if(m.getCommand() == WashingMessage.SPIN_OFF) {
                    	shouldSpin = false;
                    	io.setSpinMode(WashingMessage.SPIN_OFF);
                    	m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                    	
                    	
                    }else if(m.getCommand() == WashingMessage.SPIN_FAST) {
                    	shouldSpin = false;
                    	io.setSpinMode(io.SPIN_FAST);
                    	m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                    	
                    }else if(m.getCommand() == WashingMessage.SPIN_SLOW) {
                    	shouldSpin = true;
                    	m.getSender().send(new WashingMessage(this, WashingMessage.ACKNOWLEDGMENT));
                    }else {
                    	
                    }
                }
                
                if(shouldSpin) {
                	if(spinRight) {
                	io.setSpinMode(io.SPIN_RIGHT);
                	}else {
                		io.setSpinMode(io.SPIN_LEFT);
                	}
                	spinRight = !spinRight;
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
