

import se.lth.control.realtime.ConditionVariable;
import se.lth.control.realtime.Semaphore;

public class RingBufferWithSemaphore {
	private final int bufSize;
	private Object[] elements;
	private int nextRead = 0;
	private int nextWrite = 0;
	private int curSize = 0;

	private Semaphore sem;
	private ConditionVariable nonEmpty;
	private ConditionVariable nonFull;

	public RingBufferWithSemaphore(int bufSize) {
		this.bufSize = bufSize;
		elements = new Object[bufSize];

		sem = new Semaphore(1);
		nonEmpty = new ConditionVariable(sem);
		nonFull = new ConditionVariable(sem);
	}

	public Object get() {
		sem.take();
		while (curSize == 0) {
			nonEmpty.cvWait();
		}
		Object ret = elements[nextRead];
		elements[nextRead] = null;
		nextRead = (nextRead + 1) % bufSize;
		curSize--;
		nonFull.cvNotifyAll();
		sem.give();
		return ret;
	}

	public void put(Object o) {
		sem.take();
		while (curSize == bufSize) {
			nonFull.cvWait();
		}
		elements[nextWrite] = o;
		nextWrite = (nextWrite + 1) % bufSize;
		curSize++;
		nonEmpty.cvNotifyAll();
		sem.give();
	}
}

