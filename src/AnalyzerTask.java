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

public class AnalyzerTask {

	LinkedList<String> m_anchors;
	LinkedList<String> m_images;
	LinkedList<String> m_videos;
	LinkedList<String> m_docs;
	
	LinkedList<String> m_allowedImageExt;
	LinkedList<String> m_allowedVideoExt;
	LinkedList<String> m_allowedDocExt;
	
	String m_htmlSourceCode;

	public AnalyzerTask(String i_htmlSourceCode, LinkedList<String> allowedImageExt, LinkedList<String> allowedVideoExt, LinkedList<String> allowedDocExt){
		m_htmlSourceCode = i_htmlSourceCode;
		
		m_allowedImageExt = allowedImageExt;
		m_allowedVideoExt = allowedVideoExt;
		m_allowedDocExt = allowedDocExt;
		
		m_anchors = new LinkedList<>();
		m_images = new LinkedList<>();
		m_videos = new LinkedList<>();
		m_docs = new LinkedList<>();
	}

	//TODO: temp run method until threads will be implemented
	public void tempRunMethod__ChangeWhenThreadsImp(){
		//getAllAnchorsFromSource();
		lookForAnchors();
		lookForImages();
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
	
	
	
	public static class linkEstimator{
		final String _CRLF = "\r\n";
		
		public String[] sendHttpRequest(String target, String requestType){
			String res[] = new String[2];
			String response = "";
			boolean fetchContent = requestType.equals("GET");
			try {
				URL uri = new URL(target);
				
				String host = uri.getHost();
				String path = uri.getPath();
				path = path.equals("") ? "/" : path;
				
				String requestLine = requestType + " " + path + " " + "HTTP/1.0";
				String headers = "Host: " + host;
				
				
				String currentRecievedLine = "";
				
				Socket socket = new Socket(InetAddress.getByName(host), 80);
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				if(!fetchContent){
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					writer.write(requestLine);
					writer.write(_CRLF.toCharArray());
					writer.flush();

					writer.write(headers);
					writer.write(_CRLF.toCharArray());
					writer.flush();

					writer.write(_CRLF.toCharArray());
					writer.flush();

					while((currentRecievedLine = reader.readLine()) != null){
						response += currentRecievedLine + "\n";
					}
					System.out.println(response);

					res[0] = response;
					res[1] = "";
					
					reader.close();
					
				} else {
					res = readHttpResponse(socket);
				}
				writer.close();
				socket.close();
				
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return res;
		}
		
		public static String[] readHttpResponse(Socket connection) {
			String ContentLengthHeader = "Content-Length: ";
			int contentLength = -1;
			String m_FullRequest = "";
			char[] m_MsgBodyCharBuffer;
			StringBuilder m_MessageBodyBuilder;
			String m_messageBodyString = "";
			
			try {
				if (connection.isClosed()) {
					return null;
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line = reader.readLine();

				// Read Request According to Http Protocol
				while (line != null && !line.equals("")) {
					// Check For Request With A Body Message
					if (line.indexOf(ContentLengthHeader) > -1) {
						String bodyContentLengthAsString = line.substring(ContentLengthHeader.length());
						contentLength = Integer.parseInt(bodyContentLengthAsString);
					}
					m_FullRequest += (line + "\n");
					line = reader.readLine();
				}

				// Handle With Request that Contain Body Message
				if (contentLength > 0) {
					m_MsgBodyCharBuffer = new char[contentLength];
					reader.read(m_MsgBodyCharBuffer);
					m_MessageBodyBuilder = new StringBuilder();

					for (int i = 0; i < m_MsgBodyCharBuffer.length; i++) {
						m_MessageBodyBuilder.append(m_MsgBodyCharBuffer[i]);
					}
					m_messageBodyString = m_MessageBodyBuilder.toString();
				}

				// TRACE: Request Headers
				//System.out.println(m_FullRequest);
				
				reader.close();

			} catch (IOException e) {
				System.err.println("ERROR: IO Exception");
			}
			
			return new String[]{m_FullRequest, m_messageBodyString};
		}

		
		public String sendHttpHeadRequest(String target){
			return (sendHttpRequest(target, "HEAD"))[0];
		}
		
		public String[] sendHttpGetRequest(String target){
			return sendHttpRequest(target, "GET");
		}
	}

}



