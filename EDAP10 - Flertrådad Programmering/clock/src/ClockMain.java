import java.util.concurrent.Semaphore;
import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        
        Time time = new Time();
        Alarm alarm = new Alarm();
        out.displayTime(time.getHour(), time.getMinute(), time.getSecond());
        
        Thread t1 = new Thread(() -> 
        	{
        	while(true) {	
        	time.clockTicker(out);
        	}
        });
        
        Thread t2 = new Thread(() -> {
        	while(true) {
        		alarm.activeAlarm(out, time);
        	}
        });
        
        Semaphore sem = in.getSemaphore();
        
        t1.start();
        t2.start();
        while (true) {
        	sem.acquire();
            UserInput userInput = in.getUserInput();
            
            int choice = userInput.getChoice();
            int h = userInput.getHours();
            int m = userInput.getMinutes();
            int s = userInput.getSeconds();
            
            if(choice == 1) {
            time.setTime(h,m,s);
            }else if(choice == 2) {
            	time.alarmTime(h, m, s);
            }

            alarm.alarmSwitch(choice, out); 

          //  System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
        }
    }
}
