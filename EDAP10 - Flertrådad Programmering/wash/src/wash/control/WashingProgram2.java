package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

/**
 * Program 3 for washing machine. This also serves as an example of how washing
 * programs can be structured.
 * 
 * This short program stops all regulation of temperature and water levels,
 * stops the barrel from spinning, and drains the machine of water.
 * 
 * It can be used after an emergency stop (program 0) or a power failure.
 */
public class WashingProgram2 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    
    public WashingProgram2(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {
        	// Lock the hatch
        	io.lock(true);
        	water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
        	receive();
        	temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 40));
        	receive();
        	// Instruct SpinController to rotate barrel slowly, back and forth
        	// Expect an acknowledgment in response.
        	System.out.println("setting SPIN_SLOW...");
        	spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
        	WashingMessage ack1 = receive();
        	System.out.println("washing program 2 got " + ack1);
        	
        	
        	// Spin for five simulated minutes (one minute == 60000 milliseconds)
        	Thread.sleep(20 * 60000 / Settings.SPEEDUP);


        	temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
        	System.out.println("washing program 2 got " + receive());
        	spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
        	System.out.println("washing program 2 got " + receive());
        	water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
        	System.out.println("washing program 2 got " + receive());
        	water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
        	System.out.println("washing program 2 got " + receive());
        	temp.send(new WashingMessage(this, WashingMessage.TEMP_SET, 60));
        	System.out.println("washing program 2 got " + receive());
        	
        	System.out.println("setting SPIN_SLOW...");
        	spin.send(new WashingMessage(this, WashingMessage.SPIN_SLOW));
        	WashingMessage ack3 = receive();
        	System.out.println("washing program 2 got " + ack3);
        	
        	
        	// Spin for five simulated minutes (one minute == 60000 milliseconds)
        	Thread.sleep(30 * 60000 / Settings.SPEEDUP);
        	
        	temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
        	receive();
        	for(int i = 0; i < 5; i++) {
            	water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
            	receive();
            	water.send(new WashingMessage(this, WashingMessage.WATER_FILL, 10));
            	receive();
            	Thread.sleep(2 * 60000 / Settings.SPEEDUP);
        	}
        	
        	water.send(new WashingMessage(this, WashingMessage.WATER_DRAIN));
        	receive();
        	spin.send(new WashingMessage(this, WashingMessage.SPIN_FAST));
        	receive();
        	
        	Thread.sleep(5 * 60000 / Settings.SPEEDUP);
        	
        	// Instruct SpinController to stop spin barrel spin.
        	// Expect an acknowledgment in response.
        	System.out.println("setting SPIN_OFF...");
        	spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
        	WashingMessage ack2 = receive();
        	System.out.println("washing program 2 got " + ack2);


        	// Now that the barrel has stopped, it is safe to open the hatch
        	io.lock(false);
        } catch (InterruptedException e) {
            
            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, WashingMessage.TEMP_IDLE));
            water.send(new WashingMessage(this, WashingMessage.WATER_IDLE));
            spin.send(new WashingMessage(this, WashingMessage.SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}

