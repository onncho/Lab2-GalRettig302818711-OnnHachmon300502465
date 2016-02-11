import java.util.HashMap;
import java.util.LinkedList;

public class CrawlerReport {

	HashMap<String, LinkReport> m_reports;
	String m_startPageAddress;
	
	/*int m_numberOfImages;
	int m_numberOfVideos;
	int m_numberOfDocuments;
	
	int m_numberOfInternalLinks;
	int m_numberOfExternalLink;*/
	int m_totalNumberOfLinks;
	
	LinkedList<Link> m_images;
	LinkedList<Link> m_videos;
	LinkedList<Link> m_documents;
	
	LinkedList<Link> m_internalLinks;
	LinkedList<Link> m_externalLink;
	int m_totalSizeOfAllLinks;//pages...
	
	//sizes in are saved in int but represented in bytes
	int m_sizeOfAllImages;
	int m_sizeOfAllVideos;
	int m_sizeOfAllDocuments;
	
	int m_amountOfConnectedDomains;
	
	boolean v_robotsTxtRespected;
	LinkedList<String> m_openedPorts;// if was not requested will be null
	LinkedList<LinkReport> m_ReportsNotProcess;
	
	public CrawlerReport(boolean i_robotsTxtRespected, String i_startPageAddress){
		v_robotsTxtRespected = i_robotsTxtRespected;
		m_openedPorts = null; //aka not requested
		m_startPageAddress = i_startPageAddress;
		
		m_reports = new HashMap<>();
		
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
	
	public CrawlerReport(boolean i_robotsTxtRespected, String i_startPageAddress, LinkedList<String> i_openedPorts){
		v_robotsTxtRespected = i_robotsTxtRespected;
		m_openedPorts = i_openedPorts; //aka not requested
		m_startPageAddress = i_startPageAddress;
		
		m_reports = new HashMap<>();
		
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
	
	public void addLinkReport(LinkReport linkReport){
		String address = linkReport.m_pageAddress;
		if(!m_reports.containsKey(address)){
			m_reports.put(address, linkReport);
			
		}
	}
	
	public void reportsToProcess(LinkedList<LinkReport> i_reportsNotProcess) {
	
		if (i_reportsNotProcess != null) {			
			m_ReportsNotProcess = i_reportsNotProcess;
		}
	}
}