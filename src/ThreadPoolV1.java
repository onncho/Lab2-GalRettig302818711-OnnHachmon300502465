import java.util.LinkedList;

public class ThreadPoolV1 {
	
	// create collection of workers
	WorkerT[] m_DownloadersWorkersThreads;
	WorkerT[] m_AnalyzersWorkersThreads;
	
	// create task queue for downloaders 
	SynchronizedQueueLL m_UrlsToDownloadQueue;
	int m_NumOfDownloaders;
	
	private static int m_DownloaderCounter;
	//TODO: to delelte
	private boolean m_isFinished;
	
	// create task queue for analyzers
	SynchronizedQueueLL m_HtmlToAnalyzeQueue;
	int m_NumOfAnalyzers;
	int m_ReportsCounter;
	
	LinkedList<String> m_DownloadedLinks;
	LinkedList<String> m_AnalyzedNonInternalLinks;//external/internal imgs/videos/docs and external pages
	
	LinkedList<LinkReport> m_reports;
	
	public ThreadPoolV1(SynchronizedQueueLL i_UrlsToDownload, int i_NumOfDownloaders,
							 SynchronizedQueueLL i_HtmlsToAnalyze, int i_NumOfAnalyzers) {
		
		// Reference to the task queue
		m_UrlsToDownloadQueue = i_UrlsToDownload;
		m_HtmlToAnalyzeQueue = i_HtmlsToAnalyze;
		
		m_DownloadedLinks = new LinkedList<>();
		m_AnalyzedNonInternalLinks = new LinkedList<>();
		
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
		
		m_DownloaderCounter = 0;
		m_ReportsCounter = 0;
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
			m_DownloaderCounter++;
		}
	}
	
	// TODO: Check if needed...
	public void putTaskInAnalyzersQueue(Runnable task) {
		synchronized (m_HtmlToAnalyzeQueue) {
			m_HtmlToAnalyzeQueue.enqueue(task);
		}
	}
	
	public void setRefernceToReports(LinkedList<LinkReport> i_reports) {
		try {
			m_reports = i_reports;			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized void counterMinus() {
		m_DownloaderCounter--;
	}
	
	public static synchronized int getCounter() {
		return m_DownloaderCounter;
	}
	
	public synchronized void reportCounterPlus() {
		m_ReportsCounter++;
	}
	
	public synchronized void addReportAndCheckIfFinished(LinkReport i_report) {
			
		// add report after analysis to the list
		if (i_report != null) {
			LinkReport report = i_report;
			m_reports.addLast(report);			
		}
		
		// check if all tasks are finished
		if (m_DownloaderCounter == m_ReportsCounter
				&& m_UrlsToDownloadQueue.isEmpty() 
				&& m_HtmlToAnalyzeQueue.isEmpty()) {
			m_reports.notify();
		}	
	}
	
	public synchronized void addToDownloadedList(String i_urlToDowbload) {
		if (i_urlToDowbload != null) {
			m_DownloadedLinks.addLast(i_urlToDowbload);			
		}
	}
	
	public synchronized boolean containsUrlInList(String i_Url) {
		boolean res = false;
		
		if (!m_DownloadedLinks.contains(i_Url)) {
			res = true;
		}
	
		return res;	
	}
	
	public synchronized void addToAnalyzedNonInternalLinks(String i_nonInternalLink) {
		if (i_nonInternalLink != null) {
			m_AnalyzedNonInternalLinks.addLast(i_nonInternalLink);			
		}
	}
	
	public synchronized boolean containsUrlInAnalyzedNonInternalLinksList(String i_Url) {
		boolean res = false;
		
		if (!m_AnalyzedNonInternalLinks.contains(i_Url)) {
			res = true;
		}
	
		return res;	
	}
	
}
