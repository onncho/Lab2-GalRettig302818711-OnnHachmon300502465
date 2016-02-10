import java.util.LinkedList;

public class LinkReport {
	
	LinkedList<Link> m_images;
	LinkedList<Link> m_videos;
	LinkedList<Link> m_htmls;
	LinkedList<Link> m_documents;
	
	String m_imagesTotalSize;
	String m_videosTotalSize;
	String m_documentsTotalSize;
	
	String m_domain;
	
	public LinkReport(String domain){
		m_domain = domain;
		
		m_images = new LinkedList<>();
		m_videos = new LinkedList<>();
		m_documents = new LinkedList<>();
		m_htmls = new LinkedList<>();
	}

}
