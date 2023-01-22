public class RingBuffer {
	private final int bufSize;
	private Object[] elements;
	private int nextRead = 0;
	private int nextWrite = 0;
	private int curSize = 0;

	public RingBuffer(int bufSize) {
		this.bufSize = bufSize;
		elements = new Object[bufSize];
	}

	public synchronized Object get() throws InterruptedException {
		while (curSize == 0) {
			wait();
		}
		Object ret = elements[nextRead];
		elements[nextRead] = null;
		nextRead = (nextRead + 1) % bufSize;
		curSize--;
		notifyAll();
		return ret;
	}

	public synchronized void put(Object o) throws InterruptedException {
		while (curSize == bufSize) {
			wait();
		}
		elements[nextWrite] = o;
		nextWrite = (nextWrite + 1) % bufSize;
		curSize++;
		notifyAll();
	}
}