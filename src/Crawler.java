
public class Crawler {

	ThreadPoolV1 m_threadPool;
	SynchronizedQueueLL m_UrlsQueueToDownload;
	SynchronizedQueueLL m_HtmlsQueueToAnalyze;
	int m_NumOfDownloaders;
	int m_NumOfAnalyzers;
	String m_DomainToCrawl;
	// 
	
	public Crawler(String i_DomainToCrawl) {
		// TODO Auto-generated constructor stub
		m_NumOfDownloaders = Integer.parseInt(ConfigurationObject.getMaxDownloaders());
		m_NumOfAnalyzers = Integer.parseInt(ConfigurationObject.getMaxAnalyzers());
		m_UrlsQueueToDownload = new SynchronizedQueueLL();
		m_HtmlsQueueToAnalyze = new SynchronizedQueueLL();
		m_threadPool = new ThreadPoolV1(m_UrlsQueueToDownload, m_NumOfDownloaders, 
				m_HtmlsQueueToAnalyze, m_NumOfAnalyzers);
		
		//Runnable === Task (Downloading...)
		HTTPRequest run = new HTTPRequest("get", "body", 425);
		m_threadPool.putTaskInDownloaderQueue((Runnable) run);
	}
}
