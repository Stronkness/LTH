import clock.io.ClockOutput;

public class Alarm {
	boolean alarmState = false;
	
	public Alarm() {
		
	}
	
	public void alarmSwitch(int choice, ClockOutput out) {
		if(choice == 3 && alarmState == false) {
            out.setAlarmIndicator(true);
            alarmState = true;
            	}else if(choice == 3 && alarmState == true){
            		out.setAlarmIndicator(false);
            		alarmState = false;
            }
		
		
	}
	
	public void activeAlarm(ClockOutput out, Time time){
        if(time.getAlarmHour() == time.getHour() && 
        		time.getAlarmMinute() == time.getMinute() &&
        			time.getAlarmSecond() == time.getSecond()
        				&& alarmState == true) {
        	for(int i = 0; i <= 20; i++) {
        			out.alarm();
        			try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        			
					if(alarmState == false) {
	        				return;
	        		}
        		}
        	}
       }
}
