package webserver;
import java.util.HashMap;

public class Parser {

	protected static boolean checkIfImage(String URI){
		String[] acceptedExtensions = {".bmp", ".jpg", ".gif", ".png"};
		int minExtLength = 4;
		boolean imageFound = false;
		
		// if the uri length is less then 4 then it is not a file min length of extension is 4 without the file name
		if(URI.length() > minExtLength) {
			String tempExt = URI.substring(URI.length() - minExtLength);
			for(int i = 0; i < acceptedExtensions.length; i++){
				if(tempExt.equals(acceptedExtensions[i])){
					imageFound = true;
				}
			}
		}
		return imageFound;
	}
	
	protected static String getExtensionFromFileName(String fileName){
		
		if(fileName == null) { return null; }
		
		String extension = null;
		int indexOfExt = fileName.indexOf(".");
		
		if(!fileName.equals("/") && indexOfExt > -1){
			extension = fileName.substring(indexOfExt + 1);
		} else {
			extension = "html";
		}
		return extension;
	}

	
	protected static boolean checkIfRequestIsParsable(String i_fullRequest){
		boolean parsable = false;
		int indexOfNewLine = i_fullRequest.indexOf("\n");
		if(indexOfNewLine > -1){
			String firstLineSubString = i_fullRequest.substring(0, indexOfNewLine);
			if(firstLineSubString.indexOf(" ") > -1){
				if(firstLineSubString.split(" ").length == 3){
					parsable = true;
				}
			}
		}
		return parsable;
	}
	
	// check if the the request-line is acceptable , returns the the headers of the request-line splitted to spaces into array
	// or null if it cannot
	protected static String[] SplitRequestLineToHeadersIfAcceptable(String i_requestLine){
		String[] requestLineHeaders = null;
		if(i_requestLine.indexOf(" ") > -1){
			String[] requestLineHeadersTemp = i_requestLine.split(" ");
			if(requestLineHeadersTemp.length == 3){
				requestLineHeaders = requestLineHeadersTemp;
				requestLineHeaders[1] = getCorrectPagePath(requestLineHeaders[1]);
			}
		}
		return requestLineHeaders;
	}
	
	
	// check if the the request is split able and returns the the request splitted to lines in array
	// or null if it cannot
	protected static String[] SplitRequestToLinesIfAcceptable(String originalMessage){
		String[] requestSplitted = null;
		if(originalMessage.indexOf("\n") > -1){
			requestSplitted = originalMessage.split("\n");
		}
		return requestSplitted;
	}
	
	// check if the request method is accpeptable by the server, if not returns -1
	protected static HttpRequestType checkIfMethodAcceptable(String requestMethod){
		HttpRequestType returnValue = HttpRequestType.OTHER;
		switch (requestMethod) {
		case "GET":
			returnValue = HttpRequestType.GET;
			break;
		case "POST":
			returnValue = HttpRequestType.POST;
			break;
		case "HEAD":
			returnValue = HttpRequestType.HTTP_HEAD;
			break;
		case "TRACE":
			returnValue = HttpRequestType.TRACE;
			break;
		default:
			break;
		}
		return returnValue;
	}

	protected static HashMap<String, String> breakRequestStringToHeaders(String[] requestHeaders){
		HashMap<String, String> headers = new HashMap<>();
		
		for(int i = 1; i < requestHeaders.length; i++){
			int indexOfSeperator = requestHeaders[i].indexOf(": ");
			if(indexOfSeperator > -1){
				String[] keyAndValue = requestHeaders[i].split(": ");
				if(keyAndValue.length == 2){
					headers.put(keyAndValue[0], keyAndValue[1]);
				}
			}
		}
		return headers;
	}

	
	protected static void parsePostRequest(String messageBody, HashMap<String, String> params) {
		if(messageBody.length() == 0){
			params = null;
		}
		else {
			params = handleEncodedParams(messageBody);
		}	
	}
	
	protected static String[] parseGetRequest(String i_URI) {
		int indexOfSeperator = i_URI.indexOf("?");
		if(indexOfSeperator > -1){
			String[] URIandParams = i_URI.split("\\?");
			if(URIandParams.length == 2){
				return URIandParams;
			}		
		}
		return null;
	}
	
	protected static HashMap<String, String> handleEncodedParams(String paramsEncoded){
		String[] paramsTuples = breakEncodedParamsToTuples(paramsEncoded);
		return extractTupleParmas(paramsTuples);
	}
	
	private static String[] breakEncodedParamsToTuples(String paramsEncoded){
		String[] tuples = null;
		int paramsTupleSeperator = paramsEncoded.indexOf("&");
		if(paramsTupleSeperator > -1){
			tuples = paramsEncoded.split("&");
		} else {
			tuples = new String[]{paramsEncoded};
		}	
		return tuples;
	}
	
	private static HashMap<String,String> extractTupleParmas(String[] tuples){
		HashMap<String, String> params = new HashMap<>();
		for(int i = 0; i < tuples.length; i++){
			if(tuples[i].indexOf("=") != -1){
				String[] keyValueParams = tuples[i].split("=");
				params.put(keyValueParams[0], keyValueParams[1]);
			}
		}
		return params;
	}
	
	
	
	private static String getCorrectPagePath(String url){
		if(url.length() == 1 && url.indexOf("/") == 0){
			return "/index.html";
		}
		return url;
	}
	
	

	
}
