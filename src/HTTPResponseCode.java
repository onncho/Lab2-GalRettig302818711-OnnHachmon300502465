package webserver;

public enum HTTPResponseCode {
	
	OK("200 OK"),
	NOT_FOUND("404 Not Found"),
	NOT_IMPLEMENTED("501 Not Implemented"),
	BAD_REQUEST("400 Bad Request"),
	INTERNAL_SERVER_ERROR("500 Internal Server Error");

	private String displayName;

	HTTPResponseCode(String displayName) {
		this.displayName = displayName;
	}
	
	public static HTTPResponseCode getStatusCode(String t)
	{
		for (HTTPResponseCode res : values()) {
			if (res.equals(t))
			{
				return res;
			}
		}
		return null;
	}
	
	public String displayName() { return displayName; }
}

