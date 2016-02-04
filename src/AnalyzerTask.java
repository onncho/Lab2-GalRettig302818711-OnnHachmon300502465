import java.util.LinkedList;

public class AnalyzerTask {

	LinkedList<String> m_anchors;
	LinkedList<String> m_images;
	LinkedList<String> m_videos;
	LinkedList<String> m_docs;
	
	String m_htmlSourceCode;
	
	public AnalyzerTask(String i_htmlSourceCode){
		m_htmlSourceCode = i_htmlSourceCode;
		m_anchors = new LinkedList<>();
	}
	
	//TODO: temp run method until threads will be implemented
	public void tempRunMethod__ChangeWhenThreadsImp(){}
	
	private void getAllowedImagesFromSource(){
		String subjectTag = "<a";
		String propertyToSearchFor = "href=";
		
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
	
	
	private String removeQuoteCharFromString(String str){
		return str.substring(1, str.length());
	}

}
