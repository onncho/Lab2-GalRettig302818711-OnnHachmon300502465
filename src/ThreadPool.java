import java.util.LinkedList;

// 2 Classes that works together to construct running thread pool 
public class ThreadPool {

	private WorkerThread[] m_threads;
	private LinkedList<Runnable> m_taskQueue;
	
	private SynchronizedQueue<Runnable> m_QueueProducer;
	private SynchronizedQueue<Runnable> m_QueueConsumer;

	public ThreadPool(int i_numberOfThreads) {

		m_taskQueue = new LinkedList<Runnable>();
		m_threads = new WorkerThread[i_numberOfThreads];
		
		m_QueueProducer = new SynchronizedQueue<Runnable>(i_numberOfThreads);
		m_QueueConsumer = new SynchronizedQueue<Runnable>(i_numberOfThreads);

		for (int i = 0; i < m_threads.length; i++) {
			m_threads[i] = new WorkerThread();
			m_threads[i].start();
		}
	}

	public void push(Runnable r) {
		synchronized (m_taskQueue) {
			m_taskQueue.addLast(r);
			m_taskQueue.notify();
		}
	}

	public class WorkerThread extends Thread {
		
		public void run() {
			Runnable runnableObject;
			
			while (true) {
				
				synchronized (m_QueueProducer) {
					while (m_QueueProducer.getSize() == 0) {
						try {
							m_QueueProducer.wait();
							//m_taskQueue.wait();
						} catch (InterruptedException e) {
							System.err.println("ERROR: Threads Failed to Wait");
						}
					}
					runnableObject = (Runnable) m_taskQueue.removeFirst();
					
				}
				try {
					runnableObject.run();
				} catch (Exception e) {
					System.err.println("ERROR: Thread Failed Running");
				}
			}
		}
	}
}
