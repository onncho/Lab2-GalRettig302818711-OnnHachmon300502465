import java.util.LinkedList;

public class LinkReport {
	
	LinkedList<Link> m_images;
	LinkedList<Link> m_videos;
	LinkedList<Link> m_htmls;
	LinkedList<Link> m_documents;
	
	int m_imagesTotalSize;
	int m_videosTotalSize;
	int m_documentsTotalSize;
	int m_pagesTotalSize;
	
	String m_pageAddress;
	int m_sizeOfPageInBytes;
	
	public LinkReport(String i_pageAddress, int i_size){
		m_pageAddress = i_pageAddress;
		m_sizeOfPageInBytes = i_size;
		
		m_images = new LinkedList<>();
		m_videos = new LinkedList<>();
		m_documents = new LinkedList<>();
		m_htmls = new LinkedList<>();
		
		m_imagesTotalSize = 0;
		m_videosTotalSize = 0;
		m_documentsTotalSize = 0;
		m_pagesTotalSize = 0;
		
	}
	
	public int getSizeOfPage(){
		return m_sizeOfPageInBytes;
	}
	
	private boolean listContains(LinkedList<Link> list, Link link){
		boolean notExists = true;
		int i = 0;
		while(i < list.size()) {
			if(list.get(i).getLinkAdress().equals(link.getLinkAdress())){
				notExists = false;
				break;
			}
			
			i++;
		}
		return !notExists;
	}
	public void addImageLink(Link link){
		boolean elementExists = listContains(m_images, link);
		if(!elementExists){
			m_images.push(link);
			m_imagesTotalSize += link.getLengthInBytes();
		}
	}
	public void addVideoLink(Link link){
		boolean elementExists = listContains(m_videos, link);
		if(!elementExists){
			m_videos.push(link);
			m_videosTotalSize += link.getLengthInBytes();
		}
	}
	public void addDocumentLink(Link link){
		boolean elementExists = listContains(m_documents, link);
		if(!elementExists){
			m_documents.push(link);
			m_documentsTotalSize += link.getLengthInBytes();
		}
	}
	
	public void addPageLink(Link link){
		boolean elementExists = listContains(m_htmls, link);
		if(!elementExists){
			m_htmls.push(link);
			m_pagesTotalSize += link.getLengthInBytes();
		}
	}


	public LinkedList<Link> getM_images() {
		return m_images;
	}


	public LinkedList<Link> getM_videos() {
		return m_videos;
	}


	public LinkedList<Link> getM_htmls() {
		return m_htmls;
	}


	public LinkedList<Link> getM_documents() {
		return m_documents;
	}


	public int getM_imagesTotalSize() {
		return m_imagesTotalSize;
	}


	public int getM_videosTotalSize() {
		return m_videosTotalSize;
	}


	public int getM_documentsTotalSize() {
		return m_documentsTotalSize;
	}


	public int getM_pagesTotalSize() {
		return m_pagesTotalSize;
	}


	public String getM_pageAddress() {
		return m_pageAddress;
	}
	
	
	

}
