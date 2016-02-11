import java.util.HashMap;
import java.util.LinkedList;

public class CrawlerReport {

	//Did the crawler respect robots.txt or not.

	//TODO: domains
	// Number of domains the crawled domain is connected to
	// The domains the crawled domain is connected to
	// If the linked domain has been crawled, the text should be a link to this domain’s page.
	// If requested, the opened ports.
	// Average RTT in milliseconds (Time passed from sending the HTTP request

	HashMap<String, LinkReport> m_ReportsDB;
	String m_startPageAddress;

	//Amounts
	int m_numberOfImages;
	int m_numberOfVideos;
	int m_numberOfDocuments;
	int m_numberOfInternalLinks;
	int m_numberOfExternalLink;
	
	//TODO: Why? internal + external ?
	int m_totalNumberOfLinks;

	//Sizes
	//sizes in are saved in int but represented in bytes
	int m_sizeOfAllImages;
	int m_sizeOfAllVideos;
	int m_sizeOfAllDocuments;
	int m_amountOfConnectedDomains;
	int m_totalSizeOfAllLinks;//pages..

	LinkedList<Link> m_images;
	LinkedList<Link> m_videos;
	LinkedList<Link> m_documents;

	LinkedList<Link> m_internalLinks;
	LinkedList<Link> m_externalLink;

	boolean v_robotsTxtRespected;
	LinkedList<String> m_openedPorts;// if was not requested will be null
	LinkedList<LinkReport> m_ReportsNotProcess;

	public CrawlerReport(boolean i_robotsTxtRespected, String i_startPageAddress){
		v_robotsTxtRespected = i_robotsTxtRespected;
		m_openedPorts = null; //aka not requested
		m_startPageAddress = i_startPageAddress;
		
		// Data Structure initialization
		m_ReportsDB = new HashMap<>();
		m_images = new LinkedList<>();
		m_videos = new LinkedList<>();
		m_documents = new LinkedList<>();
		m_internalLinks = new LinkedList<>();
		m_externalLink = new LinkedList<>();
		
		// Amounts initialization
		m_numberOfImages = 0;
		m_numberOfVideos = 0;
		m_numberOfDocuments = 0;
		m_numberOfInternalLinks = 0;
		m_numberOfExternalLink = 0;
		m_totalNumberOfLinks = 0;
		
		// size initialization
		//sizes in are saved in int but represented in bytes
		m_sizeOfAllImages = 0;
		m_sizeOfAllVideos = 0;
		m_sizeOfAllDocuments = 0;
		m_amountOfConnectedDomains = 0;
		m_totalSizeOfAllLinks = 0;//pages..
	}

	public CrawlerReport(boolean i_robotsTxtRespected, String i_startPageAddress, LinkedList<String> i_openedPorts){
		
		//TODO: Arrange Constructo
		// maybe to delete
		
		v_robotsTxtRespected = i_robotsTxtRespected;
		m_openedPorts = i_openedPorts; //aka not requested
		m_startPageAddress = i_startPageAddress;

		m_ReportsDB = new HashMap<>();

		m_images = new LinkedList<>();
		m_videos = new LinkedList<>();
		m_documents = new LinkedList<>();

		m_internalLinks = new LinkedList<>();
		m_externalLink = new LinkedList<>();

		m_totalNumberOfLinks = 0;
		m_totalSizeOfAllLinks = 0;

		//sizes in are saved in int but represented in bytes
		m_sizeOfAllImages = 0;
		m_sizeOfAllVideos = 0;
		m_sizeOfAllDocuments = 0;

		m_amountOfConnectedDomains = 0;
	}
	
	// TODO: check if needed becuase the threadPool does that
	public void addLinkReport(LinkReport linkReport){
		String address = linkReport.m_pageAddress;
		if(!m_ReportsDB.containsKey(address)){
			m_ReportsDB.put(address, linkReport);
		}
	}
	
	public void reportsToProcess(LinkedList<LinkReport> i_reportsNotProcess) {
		if (i_reportsNotProcess != null) {
			// filled with reports after the analyzer used the threadpool to insert reports
			m_ReportsNotProcess = i_reportsNotProcess;
			processReports();			
		}
	}

	private void processReports() {

		for(LinkReport report : m_ReportsNotProcess) {
			
			String domainKey = report.getM_pageAddress();

			//insert to DB
			if(!m_ReportsDB.containsKey(domainKey)) {
				m_ReportsDB.put(domainKey, report);

			}

			// sums objects for statistics
			
			//Amounts
			m_numberOfImages += report.amountOfImages();
			m_numberOfVideos += report.amountOfVideos();
			m_numberOfDocuments += report.amountOfDocuments();
			m_numberOfExternalLink += report.amountOfExternalPages();
			
			//Sizes
			m_sizeOfAllImages += report.getM_imagesTotalSize();
			m_sizeOfAllVideos += report.getM_videosTotalSize();
			m_sizeOfAllDocuments += report.getM_documentsTotalSize();
			
			// internal + external 
			// 
			// TODO: sum everything (size in bytes of pages)
			m_totalSizeOfAllLinks += report.getSizeOfThisPageInBytes();

		}
		
		// size of reports means numbers of internal pages downloads and analyze from the domain
		m_numberOfInternalLinks = m_ReportsNotProcess.size();

	}

}
