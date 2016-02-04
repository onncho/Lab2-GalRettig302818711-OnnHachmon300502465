import java.util.LinkedList;

public class SynchronizedQueueLL {
	
	private LinkedList<String> m_buffer;
	private int m_producers;
	
	public SynchronizedQueueLL() {
		// TODO Auto-generated constructor stub
		m_buffer = new LinkedList<String>();
		// TODO: Maybe to delete, depends if producers is needed...??
		m_producers = 0;
	}
	
	// dequeue operation
	public synchronized String dequeue() {
		
		// in case the queue is empty
		while (m_buffer.isEmpty())
		{
			if (m_buffer.isEmpty() && m_producers == 0)
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
		String res = m_buffer.getLast();
		m_buffer.removeLast();
		notifyAll();
		
		return res;
	}
	
	// queue operation
	public synchronized void enqueue(String i_item) {
		// add to queue
		m_buffer.addFirst(i_item);
		notifyAll();
	}
	
	// return the number of items in queue
	public synchronized int getCapacity() {
		return m_buffer.size();
	}
	
	
	
	
}
