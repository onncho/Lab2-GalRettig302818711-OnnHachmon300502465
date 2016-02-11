import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

/////////
//TODO: GAL -> at the moment verifing only if link already exists in this current analysis process and not in the ENTIRE crawling process
////////
public class AnalyzerTask implements Runnable {

	LinkedList<String> m_externalAnchors;
	LinkedList<String> m_internalAnchors;
	LinkedList<String> m_images;
	LinkedList<String> m_videos;
	LinkedList<String> m_docs;

	LinkedList<String> m_allowedImageExt;
	LinkedList<String> m_allowedVideoExt;
	LinkedList<String> m_allowedDocExt;

	HTTPQuery query = new HTTPQuery();

	ThreadPoolV1 m_threadPool;

	String m_htmlSourceCode;
	URI m_uri;
	String m_pageAddress;
	String m_sizeAndTypeOfPage;

	LinkReport m_report;

	public AnalyzerTask(String i_htmlSourceCode, ThreadPoolV1 i_threadPool, String i_pageAddress, String i_lengthAndType) throws URISyntaxException{
		m_threadPool = i_threadPool;
		m_htmlSourceCode = i_htmlSourceCode.toLowerCase();
		m_pageAddress = i_pageAddress;
		m_sizeAndTypeOfPage = i_lengthAndType;


		if(m_pageAddress.toLowerCase().indexOf("http://") != 0 && m_pageAddress.toLowerCase().indexOf("https://") != 0){
			m_pageAddress = "http://" + m_pageAddress;
		}

		m_uri = new URI(i_pageAddress);

		m_allowedImageExt = (LinkedList<String>) ConfigurationObject.getImageExtensions();
		m_allowedVideoExt = (LinkedList<String>) ConfigurationObject.getVideoExtensions();
		m_allowedDocExt = (LinkedList<String>) ConfigurationObject.getDocumentExtensions();

		m_externalAnchors = new LinkedList<>();
		m_internalAnchors = new LinkedList<>();
		m_images = new LinkedList<>();
		m_videos = new LinkedList<>();
		m_docs = new LinkedList<>();

		m_report = createReport();
	}

	@Override
	public void run() {
		lookForAnchorsAndPopulate();
		lookForImagesAndPopulate();

		fetchResourcesFounedAndAddToReport();
		
		// send all internal link to downloader queue
		LinkedList<String> internalLinksToDownload = getInternalAnchors();
		for(int i = 0; i < internalLinksToDownload.size(); i++){
			System.out.println(String.format("Sending to downloader: %S", internalLinksToDownload.get(i)));
			Downloader downloader = new Downloader(m_threadPool, internalLinksToDownload.get(i));
			m_threadPool.putTaskInDownloaderQueue((Runnable) downloader);
		}
		
		m_threadPool.addReportAndCheckIfFinished(m_report, m_report.m_pageAddress);
	}

	private LinkedList<String> getInternalAnchors() {
		return m_internalAnchors;
	}

	private void lookForImagesAndPopulate(){
		getAllPropertiesValueByTagAndPopulateLists("<img", "src=");
	}

	private void lookForAnchorsAndPopulate(){
		getAllPropertiesValueByTagAndPopulateLists("<a", "href=");
	}

	private void getAllPropertiesValueByTagAndPopulateLists(String subjectTag, String propertyToSearchFor){
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
				//m_anchors.push(linkToMap);
				populateAnchors(linkToMap);
				break;
			}
			i++;
		}
		return i;
	}

	// TODO: rejecting any line formatted without "http"/s "/" 
	private String reformatAnchorLink(String link){
		String linkLowered = link.toLowerCase();
		String verifiedLink;
		if(linkLowered.indexOf("/") == 0) {
			verifiedLink = "http://" + m_uri.getPath() + link.toLowerCase();
		} else {
			//verifiedLink = (linkLowered.indexOf("http://") == 0 || linkLowered.indexOf("https://") == 0) ?  linkLowered : "http://" + linkLowered;
			if(linkLowered.indexOf("http://") != 0 && linkLowered.indexOf("https://") != 0){
				verifiedLink = (linkLowered.indexOf(m_uri.getHost()) == -1) ? "http://" + m_uri.getHost() + "/" +linkLowered : "http://" + linkLowered; 
			} else {
				verifiedLink = linkLowered;
			}
		}
		return verifiedLink;
	}


	/**
	 * @param link -> anchor to be added to the external or internal lists if doesn't already exists
	 * @return true on success
	 */
	private boolean populateAnchors(String link){

		String formattedLink = reformatAnchorLink(link);
		URI linkURI;
		boolean inserted = false;
		try {
			linkURI = new URI(formattedLink);
			if(linkURI.getHost().equals(m_uri.getHost())){
				m_internalAnchors.push(formattedLink);
				inserted = pushIfNotExists(m_internalAnchors, formattedLink);
			} else {
				inserted = pushIfNotExists(m_externalAnchors, formattedLink);
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return inserted;
	}

	/**
	 * 
	 * @param LinkedList<String> set -> list to push element to
	 * @param member -> string to push if not already in list
	 * @return true if member was added , false otherwise
	 */
	private boolean pushIfNotExists(LinkedList<String> set, String member){
		boolean exists = listContainsElement(set, member);
		if(!exists){
			set.push(member);
		}
		return !exists;
	}

	private String getExtensionFromString(String linkToMap) {
		String ext = null;
		int indexOfDotChar = linkToMap.lastIndexOf(".");
		if(indexOfDotChar > -1){
			ext = linkToMap.substring(indexOfDotChar + 1);
		}
		return ext;
	}

	private boolean listContainsElement(LinkedList<String> set, String member){
		int i = 0;
		while(i < set.size()){
			if(set.get(i).trim().equals(member)){
				return true;
			}
			i++;
		}
		return false;
	}

	private String removeQuoteCharFromString(String str){
		return str.substring(1, str.length());
	}

	private void fetchResourcesFounedAndAddToReport(){
		fetchAllFromList(m_images, 0);
		fetchAllFromList(m_videos, 1);
		fetchAllFromList(m_docs, 2);
		fetchAllFromList(m_externalAnchors, 3);
		fetchFromInternalLinks();

	}


	private void fetchFromInternalLinks(){
		for(int i = 0; i < m_internalAnchors.size(); i++){
			String address = m_internalAnchors.get(i);
			Link link = new Link(address, "" , "", "0");
			m_report.addInternalPageLink(link);

		}
	}

	/**
	 * @TODO pages in links, are going to be downloaded anyway and will have own reports
	 * @param list to pop link from
	 * @param listIdentifier - 0 image , 1 videos , 2 documents, 3 external pages
	 */
	private void fetchAllFromList(LinkedList<String> list, int listIdentifier) {
		for(int i = 0; i < list.size(); i++){
			String address = list.get(i);
			String extension = getExtensionFromString(address);

			if(address != null && extension != null){
				Link link = createLink(address, extension);

				if(link != null){
					if(i == 0){
						m_report.addImageLink(link);
					}
					else if(i == 1){
						m_report.addVideoLink(link);
					}
					else if(i == 2){
						m_report.addDocumentLink(link);
					}
					else if(i == 3){
						m_report.addExternalPageLink(link);
					}


				} else {
					System.out.println("failed on fetching image -> link = " + address);
				}

			} else {
				System.out.println("fetching an image failed on getting address or extension on index = " + i);
			}

		}
	}

	private Link createLink(String linkAddress, String extension){
		Link link = null;
		try {
			if(m_threadPool.containsUrlInAnalyzedNonInternalLinksList(linkAddress)){
				String typeAndLength[] = query.sendHttpHeadRequestAndGetTypeAndLengthFromResponse(linkAddress).split("#_#@#_#");

				m_threadPool.addToAnalyzedNonInternalLinks(linkAddress);

				String type = typeAndLength[0];
				String length = typeAndLength[1];
				link = new Link(linkAddress, extension, type, length);
			} else {
				System.out.println("skipped a link since it already appeared in urls list : " + linkAddress);
			}
		} catch (IOException e) {
			System.out.println("EXCEPTION ON AnalyzerTask->CreateLink() with linkAddress = " + linkAddress);
			e.printStackTrace();
		}

		return link;
	}


	private LinkReport createReport(){
		String size = m_sizeAndTypeOfPage.split("#_#@#_#")[1];
		int sizeInBytes = Integer.parseInt(size);
		LinkReport report = new LinkReport(m_pageAddress, sizeInBytes);
		return report;
	}


}



