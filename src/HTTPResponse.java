package webserver;
import java.io.File;
import java.util.HashMap;

public class HTTPResponse {
	HashMap<String, String> m_HttpRequestParams;
	String m_HttpVersion; 
	String m_ContentExtension;
	String m_RequestedPage;
	String m_RequestType;
	HTTPResponseCode m_ErrorsFoundInRequest;
	HTTPResponseCode m_responseStatusCode;

	int m_ContentLength; 
	String m_ContentType;
	String m_Response;
	String v_ContentType = "Content-Type: ";
	String v_ContentLength = "Content-Length: ";
	String v_TransferEncodingChunked = "Transfer-Encoding: chunked";
	String m_PathTofile;
	String m_messageBody;

	boolean v_fileIsExpected = true;
	boolean v_isChunked = false;

	final String SERVERS_DEFAULT_HTTP_VERSION = "HTTP/1.1";
	final String _CRLF = "\r\n";
	final String _SP = " ";
	byte[] templatedHTML;

	public HTTPResponse(HashMap<String, String> i_HttpRequest, HashMap<String,String> request_params) 
	{
		m_Response = "";
		m_responseStatusCode = HTTPResponseCode.OK;//
		m_ContentExtension = "";
		m_ContentType = "";
		if(request_params != null){
			this.m_HttpRequestParams = request_params;
		}

		if (i_HttpRequest.get("errors").equals(HTTPResponseCode.BAD_REQUEST.displayName())) 
		{
			m_ErrorsFoundInRequest = HTTPResponseCode.BAD_REQUEST;
			m_responseStatusCode = HTTPResponseCode.BAD_REQUEST;
		}
		else if(i_HttpRequest.get("errors").equals(HTTPResponseCode.NOT_IMPLEMENTED.displayName())){
			m_ErrorsFoundInRequest = HTTPResponseCode.NOT_IMPLEMENTED;
			m_responseStatusCode = HTTPResponseCode.NOT_IMPLEMENTED;
		}

		else 
		{
			m_HttpVersion = i_HttpRequest.get("HTTPVersion");
			m_RequestType = i_HttpRequest.get("RequestType");
			m_ContentExtension = i_HttpRequest.get("extension");
			m_ContentType = constructExtensionToContentType();

			if(i_HttpRequest.containsKey("chunked")){
				v_isChunked = i_HttpRequest.get("chunked").equals("yes");
			}

			if(m_RequestType.equals(HttpRequestType.TRACE.displayName())){
				if(i_HttpRequest.containsKey("originalRequest")){
					m_messageBody = i_HttpRequest.get("originalRequest");
					v_fileIsExpected = false;
				}
			}
			else {
				m_RequestedPage = i_HttpRequest.get("URI");
				
				if(m_RequestType.equals(HttpRequestType.HTTP_HEAD.displayName())){
					v_fileIsExpected = false;
				}
			}
		}
	}

	// The Method for clients of this object
	public String GenerateResponse() 
	{
		String result = constructResponse();
		return result;
	}

	private String constructResponse() 
	{
		// Construct the first status line
		boolean buildErroredResponse = m_responseStatusCode.equals(HTTPResponseCode.BAD_REQUEST) || 
				m_responseStatusCode.equals(HTTPResponseCode.NOT_IMPLEMENTED);

		if (buildErroredResponse) 
		{
			this.v_fileIsExpected = false;
			m_Response += SERVERS_DEFAULT_HTTP_VERSION + _SP + m_responseStatusCode.displayName();
			return m_Response;
		}
		else if(m_RequestType.equals(HttpRequestType.TRACE.displayName())){
			constructResponseCodeTrace();
			m_Response += (SERVERS_DEFAULT_HTTP_VERSION+ _SP + m_responseStatusCode.displayName() + 
					"\r\n" + m_ContentType + "\r\n" + 
					v_ContentLength + m_ContentLength +"\r\n\r\n" + m_messageBody);
		}
		else
		{
			constructResponseCode();
			m_Response += (SERVERS_DEFAULT_HTTP_VERSION + _SP +m_responseStatusCode.displayName() + _CRLF);

			if(v_isChunked){
				m_Response += m_ContentType + _CRLF + 
						v_TransferEncodingChunked + _CRLF + _CRLF;
			}
			else if(!m_responseStatusCode.equals(HTTPResponseCode.NOT_FOUND)){
				m_Response += m_ContentType + _CRLF + 
						v_ContentLength + m_ContentLength + _CRLF + _CRLF;
			}
		}
		return m_Response;
	}

	
	private void constructResponseCodeTrace() {

		if (m_RequestType.equals("Other")) 
		{
			m_responseStatusCode = HTTPResponseCode.NOT_IMPLEMENTED;
		}
		else if (!checkResourceTrace(m_messageBody)) 
		{
			if (!m_responseStatusCode.displayName().equals(HTTPResponseCode.INTERNAL_SERVER_ERROR.displayName())) {				
				m_responseStatusCode = HTTPResponseCode.NOT_FOUND;
			}
		}
		else
		{
			m_responseStatusCode = HTTPResponseCode.OK;
		}
	}

	private boolean checkResourceTrace(String i_messageBody) {

		if(i_messageBody != null) 
		{
			m_ContentLength = (int) i_messageBody.length();
			return true;

		} 
		else {
			m_responseStatusCode = HTTPResponseCode.INTERNAL_SERVER_ERROR;
			return false;
		} 

	}

	
	private void constructResponseCode() {

		if (m_RequestType.equals("Other")) 
		{
			m_responseStatusCode = HTTPResponseCode.NOT_IMPLEMENTED;
		}
		else if (!checkResource(m_RequestedPage)) 
		{
			if (!m_responseStatusCode.equals(HTTPResponseCode.INTERNAL_SERVER_ERROR)) {				
				m_responseStatusCode = HTTPResponseCode.NOT_FOUND;
			}
		}
		else
		{
			m_responseStatusCode = HTTPResponseCode.OK;
		}
	}

	
	private boolean checkResource(String i_RequestedPage) {

		String pathname = ConfigurationObject.getRoot() + i_RequestedPage;

		pathname = pathname.replace('/', '\\');

		System.out.println(pathname);

		File file = new File(pathname);

		try 
		{
			if (file.exists())
			{
				if(!v_isChunked){
					if(file.getName().equals("params_info.html")){
						templatedHTML = HTMLTemplater.templateHTML(file,m_HttpRequestParams);
						m_ContentLength = (int) templatedHTML.length;
					} else {
						m_ContentLength = (int) file.length();
					}

				} else if(file.getName().equals("params_info.html") && v_isChunked){

					//if for a weird reason the client want the page in chunks we still need to template it
					templatedHTML = HTMLTemplater.templateHTML(file,m_HttpRequestParams);
				}

				m_PathTofile = pathname;
				return true;
			}
		} 
		catch (Exception e) {
			m_responseStatusCode = HTTPResponseCode.INTERNAL_SERVER_ERROR;
			return false;
		} 
		return false;
	}

	// The Web Server Support more Content Extension then the lab requires, can add more... 
	private String constructExtensionToContentType()
	{
		switch (m_ContentExtension) {
		case "html"	: 
			m_ContentType = v_ContentType + "text/html";
			break;
		case "css"	: 
			m_ContentType = v_ContentType + "text/css";
		case "jpg"	: 
			m_ContentType = v_ContentType + "image/jpeg";
			break;
		case "htm"	: 
			m_ContentType = v_ContentType + "text/html";
			break;
		case "jpeg"	: 
			m_ContentType = v_ContentType + "image/jpeg";
			break;
		case "js"	: 
			m_ContentType = v_ContentType + "application/javascript";
			break;
		case "bmp"	: 
			m_ContentType = v_ContentType + "image/bmp";
			break;
		case "gif"	: 
			m_ContentType = v_ContentType + "image/gif";
		case "txt"	: 
			m_ContentType = v_ContentType + "text/plain";
			break;
		case "trace" : 
			m_ContentType = v_ContentType + "message/http";
			break;
		default		:
			m_ContentType = v_ContentType + "application/octet-stream";
			break;
		}
		return m_ContentType;
	}

	public String getPathToFile() {

		
		if(m_responseStatusCode.equals(HTTPResponseCode.NOT_FOUND) || m_responseStatusCode.equals(HTTPResponseCode.NOT_IMPLEMENTED) ||!this.v_fileIsExpected){
			return null;
		}
		
		if (m_responseStatusCode.equals(HTTPResponseCode.NOT_FOUND)) {
			return ConfigurationObject.getRoot() + "/" + "404Error.html";
		} else if (m_responseStatusCode.equals(HTTPResponseCode.NOT_IMPLEMENTED)) {
			return ConfigurationObject.getRoot() + "/" + "501Error.html";
		} else if (!this.v_fileIsExpected) {
			return null;
		}

		if (m_PathTofile.equals("/")) {
			return ConfigurationObject.getRoot() + "/" + "index.html";
		}
		return m_PathTofile;
	}

	public boolean fileIsExpected(){
		return v_fileIsExpected;
	}
}
