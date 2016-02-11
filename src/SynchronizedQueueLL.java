import java.util.LinkedList;

public class SynchronizedQueueLL {
	
	private LinkedList<Runnable> m_buffer;
	private int m_size;
	
	public SynchronizedQueueLL() {
		m_buffer = new LinkedList<Runnable>();
	}
	
	// dequeue operation
	public synchronized Runnable dequeue() {
		
		// in case the queue is empty
		while (m_buffer.isEmpty())
		{
			if (m_buffer.isEmpty() && m_size == 0)
			{
				return null;
			}
			try 
			{
				wait();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		// dequeue
		Runnable task = m_buffer.removeLast();
		notifyAll();
		
		return task;
	}
	
	// queue operation
	public synchronized void enqueue(Runnable i_item) {
		// add to queue
		m_buffer.addFirst(i_item);
		notifyAll();
	}
	
	// return the number of items in queue
	public synchronized int getCapacity() {
		return m_buffer.size();
	}
	
	public synchronized boolean isEmpty() {
		return m_buffer.isEmpty();
	}
	
	
	
	
}
