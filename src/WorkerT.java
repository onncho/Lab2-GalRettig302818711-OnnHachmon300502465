
public class WorkerT extends Thread {
	
	SynchronizedQueueLL m_tasks;
	
	public WorkerT(SynchronizedQueueLL i_tasks) {
		m_tasks = i_tasks;
	}
	
	public void run() {
		Runnable taskToExecute;
		
		while(true) {
			
			// try to get a task from taskQueue, if the Queue is empty wait if not take it and remove it from the queue
			synchronized (m_tasks) {
				if (m_tasks.getCapacity() == 0) {
					try {
						m_tasks.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				taskToExecute = (Runnable) m_tasks.dequeue();
			}
			try {
				taskToExecute.run();
			} catch(Exception e) {
				System.err.println("ERROR: Thread Failed Running");
			}
		}
	}
}
