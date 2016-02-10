import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;


public class AnalyzerTask implements Runnable {

	LinkedList<String> m_anchors;
	LinkedList<String> m_images;
	LinkedList<String> m_videos;
	LinkedList<String> m_docs;
	
	LinkedList<String> m_allowedImageExt;
	LinkedList<String> m_allowedVideoExt;
	LinkedList<String> m_allowedDocExt;
	
	HTTPQuery query = new HTTPQuery();
	
	ThreadPoolV1 m_threadPool;
	
	
	String m_htmlSourceCode;

	public AnalyzerTask(String i_htmlSourceCode, ThreadPoolV1 i_threadPool){
		m_htmlSourceCode = i_htmlSourceCode;
		m_threadPool = i_threadPool;
		
		m_allowedImageExt = (LinkedList<String>) ConfigurationObject.getImageExtensions();
		m_allowedVideoExt = (LinkedList<String>) ConfigurationObject.getVideoExtensions();
		m_allowedDocExt = (LinkedList<String>) ConfigurationObject.getDocumentExtensions();
		
		m_anchors = new LinkedList<>();
		m_images = new LinkedList<>();
		m_videos = new LinkedList<>();
		m_docs = new LinkedList<>();
	}

	//TODO: temp run method until threads will be implemented
	
	@Override
	public void run() {
		//getAllAnchorsFromSource();
		lookForAnchors();
		lookForImages();
		
		//////
		/////
		LinkedList<String> internalLinksToDownload = getInternalAnchors();
		for(int i = 0; i < internalLinksToDownload.size(); i++){
			Downloader downloader = new Downloader(m_threadPool, internalLinksToDownload.get(i));
			m_threadPool.putTaskInDownloaderQueue((Runnable) downloader);
		}
		
		
	}
	
	private LinkedList<String> getInternalAnchors() {
		// TODO Auto-generated method stub
		return null;
	}

	private void lookForImages(){
		getAllPropertiesValueByTag("<img", "src=");
	}
	
	private void lookForAnchors(){
		getAllPropertiesValueByTag("<a", "href=");
	}
	
	private void getAllPropertiesValueByTag(String subjectTag, String propertyToSearchFor){
		//LinkedList<String> list = new LinkedList<>();
		
		//String subjectTag = "<a";
		//String propertyToSearchFor = "href=";

		int currentIndex = m_htmlSourceCode.indexOf(subjectTag);

		// while there are still anchors from currentIndex to end of the string..
		while(currentIndex > -1){
			String link = null;
			char kindOfQuoteCharUsed;

			//indexes of the link itself aka -> <a href='www.someLink.com'
			int linkStartIndex, linkEndIndex;

			//inside an "<a" tag there is the "href=" property that holds the link address
			int hrefIndexInAnchor = m_htmlSourceCode.indexOf(propertyToSearchFor, currentIndex);

			linkStartIndex = (hrefIndexInAnchor + propertyToSearchFor.length());

			//can identifiy links with ' or " char, inorder to fecth it correctly 
			kindOfQuoteCharUsed = m_htmlSourceCode.charAt(linkStartIndex);

			//pointing to the closing quote char
			linkEndIndex = m_htmlSourceCode.indexOf(kindOfQuoteCharUsed, linkStartIndex + 1);

			if(linkStartIndex > -1 && linkEndIndex > -1){
				link = m_htmlSourceCode.substring(linkStartIndex, linkEndIndex);
				//list.push(this.removeQuoteCharFromString(link));
				populateCorrectList(this.removeQuoteCharFromString(link));
			}

			currentIndex = m_htmlSourceCode.indexOf(subjectTag, currentIndex + subjectTag.length());
		}
		
	}
	

	private void getAllAnchorsFromSource(){
		String subjectTag = "<a";
		String propertyToSearchFor = "href=";

		int currentIndex = m_htmlSourceCode.indexOf(subjectTag);

		// while there are still anchors from currentIndex to end of the string..
		while(currentIndex > -1){
			String link = null;
			char kindOfQuoteCharUsed;

			//indexes of the link itself aka -> <a href='www.someLink.com'
			int linkStartIndex, linkEndIndex;

			//inside an "<a" tag there is the "href=" property that holds the link address
			int hrefIndexInAnchor = m_htmlSourceCode.indexOf(propertyToSearchFor, currentIndex);

			linkStartIndex = (hrefIndexInAnchor + propertyToSearchFor.length());

			//can identifiy links with ' or " char, inorder to fecth it correctly 
			kindOfQuoteCharUsed = m_htmlSourceCode.charAt(linkStartIndex);

			//pointing to the closing quote char
			linkEndIndex = m_htmlSourceCode.indexOf(kindOfQuoteCharUsed, linkStartIndex + 1);

			if(linkStartIndex > -1 && linkEndIndex > -1){
				link = m_htmlSourceCode.substring(linkStartIndex, linkEndIndex);
				m_anchors.push(this.removeQuoteCharFromString(link));
			}

			currentIndex = m_htmlSourceCode.indexOf(subjectTag, currentIndex + subjectTag.length());
		}
	}
	
	
	private int populateCorrectList(String linkToMap){
		String ext = getExtensionFromString(linkToMap);
		int i = ext != null ? 0 : 3;//doesn't have an extension, mapping to anchors list stright away
		while(i < 4){
			if(i == 0){
				if(listContainsElement(m_allowedImageExt, ext)){
					m_images.push(linkToMap);
					break;
				}
			}
			else if(i == 1) {
				if(listContainsElement(m_allowedVideoExt, ext)){
					m_videos.push(linkToMap);
					break;
				}
			}
			else if(i == 2){
				if(listContainsElement(m_allowedDocExt, ext)){
					m_docs.push(linkToMap);
					break;
				}
			}
			else {
				m_anchors.push(linkToMap);
				break;
			}
			i++;
		}
		return i;
	}
	
	private String getExtensionFromString(String linkToMap) {
		String ext = null;
		int indexOfDotChar = linkToMap.indexOf(".");
		if(indexOfDotChar > -1){
			ext = linkToMap.substring(indexOfDotChar + 1);
		}
		return ext;
	}

	private boolean listContainsElement(LinkedList<String> set, String member){
		int i = 0;
		while(i < set.size()){
			if(set.get(i).equals(member)){
				return true;
			}
			i++;
		}
		return false;
	}
	
	private String removeQuoteCharFromString(String str){
		return str.substring(1, str.length());
	}
	
	private String fetchImageLength(){
		String link = m_images.pop();
		try {
			String response = query.sendHttpHeadRequest(link);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}



}



