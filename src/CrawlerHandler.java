import java.util.List;

public class CrawlerHandler {

	private ThreadPool Downloader;
	private ThreadPool Analyzer;
	private List<String> m_UrlList;
	private boolean isRunning = true;
	private String m_DomainURL;
	
	private SynchronizedQueueLL PendingUrl2Downloads;
	private SynchronizedQueueLL PendingHTML2Analyze;
	
	public CrawlerHandler(String i_URL) {
		// TODO Auto-generated constructor stub
		Downloader = new ThreadPool(Integer.parseInt(ConfigurationObject.getMaxDownloaders()));
		Analyzer = new ThreadPool(Integer.parseInt(ConfigurationObject.getMaxAnalyzers()));
		m_DomainURL = i_URL;
		PendingUrl2Downloads = new SynchronizedQueueLL();
		PendingUrl2Downloads.enqueue(m_DomainURL);
		PendingHTML2Analyze = new SynchronizedQueueLL();
	}
	
	// Crawler start -> queues created -> first domain url configured -> send to Downloader 
}
