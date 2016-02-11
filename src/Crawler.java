import java.util.LinkedList;

public class Crawler {

	ThreadPoolV1 m_threadPool;
	SynchronizedQueueLL m_UrlsQueueToDownload;
	SynchronizedQueueLL m_HtmlsQueueToAnalyze;
	int m_NumOfDownloaders;
	int m_NumOfAnalyzers;
	String m_DomainToCrawl;
	Downloader m_Downloader;
	PortScanner m_PortScanner;
	
	LinkedList<LinkReport> m_Reports;
	CrawlerReport m_FinalReport;
	boolean m_RobotsChecked;
	boolean m_PortScannerChecked;
	
	boolean m_CrawlingRunning;

	// TODO: Need to receive parameters 
	public Crawler(String i_DomainToCrawl, boolean i_RobotsChecked, boolean i_PortScanChecked) {
		m_DomainToCrawl = i_DomainToCrawl;
		m_NumOfDownloaders = Integer.parseInt(ConfigurationObject.getMaxDownloaders());
		m_NumOfAnalyzers = Integer.parseInt(ConfigurationObject.getMaxAnalyzers());
		m_UrlsQueueToDownload = new SynchronizedQueueLL();
		m_HtmlsQueueToAnalyze = new SynchronizedQueueLL();
		m_threadPool = new ThreadPoolV1(m_UrlsQueueToDownload, m_NumOfDownloaders, 
				m_HtmlsQueueToAnalyze, m_NumOfAnalyzers);
		
		// transfer the reference of the reports to the threadpool
		m_Reports = new LinkedList<>();
		m_threadPool.setRefernceToReports(m_Reports);
		m_RobotsChecked = i_RobotsChecked;
		m_PortScannerChecked = i_PortScanChecked;
		m_CrawlingRunning = true;
		
		// start
		//startCrawling();
	}

	public void startCrawling() {
		
		// if port scan V -> DO First
		if (m_PortScannerChecked) {
			//TODO: change ports
			m_PortScanner = new PortScanner(m_DomainToCrawl, 80, 200);
		}
		
		// if robots -> do something
		if (m_RobotsChecked) {
			//TODO: add robots
		}
		
		m_Downloader = new Downloader(m_threadPool, m_DomainToCrawl);
		m_threadPool.putTaskInDownloaderQueue((Runnable) m_Downloader);

		// check every 2000 msec if the crawling still running
		while (m_CrawlingRunning) {
			try {
				Thread.sleep(2000);
				m_CrawlingRunning = !m_threadPool.isFinished();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
				
		// analysis ends 
		createFinalReport();
		System.out.println("Exit Crawler Class");
	}

	private void createFinalReport() {
		// reports before process
		m_FinalReport = new CrawlerReport(false, m_DomainToCrawl);
		m_FinalReport.reportsToProcess(m_Reports);
		
		// Show Results on Html
	}

}
