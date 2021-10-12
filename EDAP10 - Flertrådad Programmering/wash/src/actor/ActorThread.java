package actor;

import java.util.LinkedList;
import java.util.Queue;

public class ActorThread<M> extends Thread {
	private Queue<M> q = new LinkedList<M>();
    /** Called by another thread, to send a message to this thread. */
    public synchronized void send(M message) {
        q.add(message);
        notifyAll();
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected synchronized M receive() throws InterruptedException {
    	while(q.isEmpty()) {
    		wait();
    	}
        return q.remove();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected synchronized M receiveWithTimeout(long timeout) throws InterruptedException {
    	M m;
    	long timetoWakeup = System.currentTimeMillis() + timeout;
    	
        while(q.isEmpty() && timetoWakeup > System.currentTimeMillis()) {
        	long dt = timetoWakeup - System.currentTimeMillis();
        	wait(dt);
        }
        
    	if(q.isEmpty()) {
    		m = null;
    	}else {
    		m = q.remove();
    	}
    	
        return m;
    }
}