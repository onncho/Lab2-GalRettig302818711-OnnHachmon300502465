
public class ThreadPoolV1 {
	
	// create collection of workers
	WorkerT[] m_DownloadersWorkersThreads;
	WorkerT[] m_AnalyzersWorkersThreads;
	
	// create task queue for downloaders 
	SynchronizedQueueLL m_UrlsToDownloadQueue;
	int m_NumOfDownloaders;
	
	private static int m_Counter;
	private boolean m_isFinished;
	
	// create task queue for analyzers
	SynchronizedQueueLL m_HtmlToAnalyzeQueue;
	int m_NumOfAnalyzers;
	
	public ThreadPoolV1(SynchronizedQueueLL i_UrlsToDownload, int i_NumOfDownloaders,
							 SynchronizedQueueLL i_HtmlsToAnalyze, int i_NumOfAnalyzers) {
		
		// Reference to the task queue
		m_UrlsToDownloadQueue = i_UrlsToDownload;
		m_HtmlToAnalyzeQueue = i_HtmlsToAnalyze;
		
		// create and start the workers to be ready to get tasks
		m_NumOfDownloaders = i_NumOfDownloaders;
		m_DownloadersWorkersThreads = new WorkerT[m_NumOfDownloaders];	
		
		for (WorkerT thread : m_DownloadersWorkersThreads) {
			thread = new WorkerT(m_UrlsToDownloadQueue);
			thread.start();
		}
		
		m_NumOfAnalyzers = i_NumOfAnalyzers;
		m_AnalyzersWorkersThreads = new WorkerT[m_NumOfAnalyzers];
		
		for (WorkerT worker : m_AnalyzersWorkersThreads) {
			worker = new WorkerT(m_HtmlToAnalyzeQueue);
			worker.start();
		}
		
		m_Counter = 0;
	}
	
	//TODO: test for handling webSRV regular Requests
	// TODO: maybe change names to generic names.  
	public ThreadPoolV1(SynchronizedQueueLL tasks, int numofthreads) {
		m_UrlsToDownloadQueue = tasks;
		
		// create and start the workers to be ready to get tasks
		m_NumOfDownloaders = numofthreads;
		m_DownloadersWorkersThreads = new WorkerT[m_NumOfDownloaders];		
		for (WorkerT thread : m_DownloadersWorkersThreads) {
			thread = new WorkerT(m_UrlsToDownloadQueue);
			thread.start();
		}
	}
	
	public void putTaskInDownloaderQueue(Runnable task) {
		synchronized (m_UrlsToDownloadQueue) {
			m_UrlsToDownloadQueue.enqueue(task);
			m_Counter++;
		}
	}
	
	// TODO: Check if needed...
	public void putTaskInAnalyzersQueue(Runnable task) {
		synchronized (m_HtmlToAnalyzeQueue) {
			m_HtmlToAnalyzeQueue.enqueue(task);
		}
	}
	
	public static synchronized void counterMinus() {
		m_Counter--;
	}
	
	public static synchronized int getCounter() {
		return m_Counter;
	}
	
	public synchronized boolean isFinished() {
		boolean res = false;
		int report = 0;
		if (m_Counter == report
				&& m_UrlsToDownloadQueue.isEmpty() 
				&& m_HtmlToAnalyzeQueue.isEmpty()) {
			res = true;
			
		}
		return res;
	}
	
}
