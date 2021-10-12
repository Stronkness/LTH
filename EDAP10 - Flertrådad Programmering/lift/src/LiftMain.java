
import lift.LiftMonitor;
import lift.LiftThread;
import lift.LiftView;
import lift.Passenger;
import lift.PassengerThread;

public class LiftMain {

    public static void main(String[] args) {

        LiftView  view = new LiftView();//
        LiftMonitor monitor = new LiftMonitor(view);
        for(int i = 0; i < 20; i++) {
        	PassengerThread passengerThread = new PassengerThread(view, monitor);
        	passengerThread.start();
        }
                      // walk in (from left)
        LiftThread liftThread = new LiftThread(view, monitor);
        liftThread.start();
//        pass.enterLift();                    // step inside
//        pass.exitLift();                     // leave lift
//        pass.end();                          // walk out (to the right)
    }
}
