import java.util.HashMap;
import java.util.LinkedList;

public class CrawlerReport {

	HashMap<String, LinkReport> m_reports;
	String m_startPageAddress;
	
	int m_numberOfImages;
	int m_numberOfVideos;
	int m_numberOfDocuments;
	
	int m_numberOfInternalLinks;
	int m_numberOfExternalLink;
	int m_totalNumberOfLinks;
	
	//sizes in are saved in int but represented in bytes
	int m_sizeOfAllImages;
	int m_sizeOfAllVideos;
	int m_sizeOfAllDocuments;
	

	int m_totalSizeOfAllLinks;//pages...
	
	int m_amountOfConnectedDomains;
	
	boolean v_robotsTxtRespected;
	LinkedList<String> m_openedPorts;// if was not requested will be null
	
	public CrawlerReport(boolean i_robotsTxtRespected){
		v_robotsTxtRespected = i_robotsTxtRespected;
		m_reports = new HashMap<>();
		
		m_numberOfImages = 0;
		m_numberOfVideos = 0;
		m_numberOfDocuments = 0;
		
		m_numberOfInternalLinks;
		m_numberOfExternalLink;
		m_totalNumberOfLinks;
		
		
	}
	
	public CrawlerReport(boolean i_robotsTxtRespected, LinkedList<String> i_openedPorts){}
	
	
}
