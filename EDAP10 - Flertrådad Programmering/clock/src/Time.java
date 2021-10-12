import java.time.LocalDateTime;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import clock.io.ClockOutput;

public class Time {
	private int hour;
	private int minute;
	private int second;
    public LocalDateTime now = LocalDateTime.now();
    private int alarmHour;
    private int alarmMinute;
    private int alarmSecond;
    private Lock mutex = new ReentrantLock();
    long t0 = System.currentTimeMillis();
	int counter = 0;
	
	public Time() {
		mutex.lock();
		hour = now.getHour();
		minute = now.getMinute();
		second = now.getSecond();
		mutex.unlock();
	}
	
	public void alarmTime(int aHour, int aMinute, int aSecond) {
		mutex.lock();
		alarmHour = aHour;
		alarmMinute = aMinute;
		alarmSecond = aSecond;
		mutex.unlock();
	}
	
	public void clockTicker(ClockOutput out) {
		try {
			long t = System.currentTimeMillis();
			Thread.sleep((counter+1)*1000-(t-t0));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mutex.lock();
		second++;
		
		if(second > 59) {
			second = 0;
			minute++;
		}
		if(minute > 59) {
			minute = 0;
			hour++;
		}
		if(hour > 23) {
			hour = 0;
		}
		out.displayTime(hour, minute, second);
		counter++;
		mutex.unlock();

	}
	
	public int getHour() {
		mutex.lock();
		int h = hour;
		mutex.unlock();
		return h;
	}
	public int getMinute() {
		mutex.lock();
		int m = minute;
		mutex.unlock();
		return m;
	}
	public int getSecond() {
		mutex.lock();
		int s = second;
		mutex.unlock();
		return s;
	}
	public void setTime(int hour, int minute, int second) {
		mutex.lock();
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		mutex.unlock();
	}
	
	
	
	public int getAlarmHour() {
		mutex.lock();
		int ah = alarmHour;
		mutex.unlock();
		return ah;
	}
	public int getAlarmMinute() {
		mutex.lock();
		int am = alarmMinute;
		mutex.unlock();
		return am;
	}
	public int getAlarmSecond() {
		mutex.lock();
		int as = alarmSecond;
		mutex.unlock();
		return as;
	}
	public void setAlarmHour(int aHour) {
		mutex.lock();
		alarmHour = aHour;
		mutex.unlock();
	}
	public void setAlarmMinute(int aMinute) {
		mutex.lock();
		alarmMinute = aMinute;
		mutex.unlock();
	}
	public void setAlarmSecond(int aSecond) {
		mutex.lock();
		alarmSecond = aSecond;
		mutex.unlock();
	}
}
