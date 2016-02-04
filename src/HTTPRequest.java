package webserver;
import java.util.HashMap;

public class HTTPRequest {

	// GET or POST or ... 
	HttpRequestType m_RequestType;
	// Requested Page (/ or /index.html etc.)
	String m_RequestedPage;
	// Is Image if the requested page has an extension of an image (jpg, bmp, gif...)
	Boolean v_IsImage;
	// Content Length that is written in the request
	int m_contentLength;
	// Referrer header
	String m_ReferrerHeader;
	// User Agent
	String m_UserAgent;
	//Parameters – the parameters in the request (I used java.util.HashMap<String,String> to hold the parameters).
	String m_HTTPver;
	HashMap<String, String> m_HttpRequestParams = new HashMap<>();
	HashMap<String, String> m_requestHeaders = new HashMap<>();
	String m_originalRequest;
	String m_httpMessageBody;
	int m_errorCodeIfOccurred = -1;//-1 not occurred , 4 bad-request , 5 not implemented


	public HTTPRequest(String io_originalRequest, String io_httpMessageBody, int contentLength){
		m_originalRequest = io_originalRequest;
		m_httpMessageBody = io_httpMessageBody;

		// Parse the Raw Request and check it
		if(!Parser.checkIfRequestIsParsable(io_originalRequest)){

			m_errorCodeIfOccurred = 4;
			this.m_requestHeaders.put("errors", this.mapErrorValueInRequestToResponseType().displayName());
		} else {

			String[] requestAsArray = Parser.SplitRequestToLinesIfAcceptable(m_originalRequest);
			String[] requestLine = Parser.SplitRequestLineToHeadersIfAcceptable(requestAsArray[0]);


			this.m_RequestedPage = requestLine[1];
			this.m_HTTPver = requestLine[2];
			this.m_RequestType = checkIfMethodAcceptable(requestLine[0]);//has to be last

			if(!HttpRequestType.OTHER.displayName().equals(this.m_RequestType.displayName())){
				//can fully parse headers
				this.handleRequestHeaders(requestAsArray);

			} else {
				m_errorCodeIfOccurred = 5;
				this.m_requestHeaders.put("errors", this.mapErrorValueInRequestToResponseType().displayName());
			}
		}
	}

	private void handleRequestHeaders(String[] request){
		this.m_requestHeaders = Parser.breakRequestStringToHeaders(request);
		this.v_IsImage = Parser.checkIfImage(m_RequestedPage);

		if(this.m_requestHeaders.containsKey("Referer")){
			this.m_ReferrerHeader = this.m_requestHeaders.get("Referer");
		}
		if(this.m_requestHeaders.containsKey("User-Agent")){
			this.m_UserAgent = this.m_requestHeaders.get("User-Agent");
		}
		m_requestHeaders.put("URI", this.m_RequestedPage);
		m_requestHeaders.put("HTTPVersion", this.m_HTTPver);
		m_requestHeaders.put("RequestType", m_RequestType.displayName());
		m_requestHeaders.put("errors", this.mapErrorValueInRequestToResponseType().displayName());

		if(m_RequestType.displayName().equals(HttpRequestType.TRACE.displayName())){
			if(m_httpMessageBody != null){
				m_requestHeaders.put("originalRequest", m_originalRequest+"\r\n"+m_httpMessageBody+"\r\n");
			} else {
				m_requestHeaders.put("originalRequest", m_originalRequest);
			}
			
			m_requestHeaders.put("extension", "trace");
		} else {
			handleFileExtension();
		}
	}

	private void handleFileExtension(){
		String extension = Parser.getExtensionFromFileName(m_RequestedPage);

		if(extension == null){
			handleFileExtensionErrors();
		} else {
			if(m_RequestType.displayName().equals(HttpRequestType.TRACE.displayName())){
				m_requestHeaders.put("extension", "trace");
			} else {
				m_requestHeaders.put("extension", extension);
			}
		}
	}

	private void handleFileExtensionErrors(){
		if(m_requestHeaders.get("errors").equals("none")){
			HTTPResponseCode BadReqcode = HTTPResponseCode.BAD_REQUEST;
			m_requestHeaders.put("errors", BadReqcode.displayName());
		}
	}

	// check if the request method is acceptable by the server, if not returns -1
	private HttpRequestType checkIfMethodAcceptable(String requestMethod){
		HttpRequestType returnValue = HttpRequestType.OTHER;
		switch (requestMethod) {
		case "GET":
			returnValue = HttpRequestType.GET;
			handleGetRequest();
			break;
		case "POST":
			returnValue = HttpRequestType.POST;
			handlePostRequest();
			break;
		case "HEAD":
			returnValue = HttpRequestType.HTTP_HEAD;
			handleHeadRequest();
			break;
		case "TRACE":
			returnValue = HttpRequestType.TRACE;
			break;
		default:
			break;
		}

		return returnValue;
	}

	private void handleGetRequest(){

		// Seperate the parmas in the request if there are many
		if(m_RequestedPage.indexOf("?") != -1){
			String[] pageBrokenToQuery = Parser.parseGetRequest(m_RequestedPage);

			if(pageBrokenToQuery == null) {
				m_errorCodeIfOccurred = 4;
			} else {
				m_RequestedPage = pageBrokenToQuery[0];
				m_HttpRequestParams = Parser.handleEncodedParams(pageBrokenToQuery[1]);
			}
		}
	}

	private void handlePostRequest(){
		if(this.m_httpMessageBody != null){
			if(this.m_httpMessageBody.length() > 0){
				m_HttpRequestParams = Parser.handleEncodedParams(m_httpMessageBody);
				if(m_HttpRequestParams == null){
					m_errorCodeIfOccurred = 4;// was suppose to provide parameters
				}
			}
		}
	}

	private void handleHeadRequest(){
		//same as get only not providing the message body
		handleGetRequest();
	}

	
	public HashMap<String, String> getMap (){
		return this.m_requestHeaders;
	}

	public boolean ImplementedMethod(){
		if(m_RequestType.displayName().equals(HttpRequestType.GET.displayName()) ||
				m_RequestType.displayName().equals(HttpRequestType.POST.displayName()) || 
				m_RequestType.displayName().equals(HttpRequestType.HTTP_HEAD.displayName()) ||
				m_RequestType.displayName().equals(HttpRequestType.TRACE.displayName())){
			return true;
		}
		return false;
	}

	public HTTPResponseCode mapErrorValueInRequestToResponseType(){
		HTTPResponseCode code = HTTPResponseCode.OK;
		switch (this.m_errorCodeIfOccurred) {
		case -1:
			code = HTTPResponseCode.OK;
			break;
		case 4:
			code = HTTPResponseCode.BAD_REQUEST;
			break;
		case 5:
			code = HTTPResponseCode.NOT_IMPLEMENTED;
			break;

		default:
			code = HTTPResponseCode.BAD_REQUEST;
			break;
		}
		return code;
	}
}
