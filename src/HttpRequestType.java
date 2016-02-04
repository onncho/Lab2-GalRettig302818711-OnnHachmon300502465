package webserver;

public enum HttpRequestType {
	GET("GET"),
	POST("POST"),
	HTTP_HEAD("HEAD"),
	TRACE("TRACE"),
	OTHER("Other");

	private String displayName;

	HttpRequestType(String displayName) {
		this.displayName = displayName;
	}

	public String displayName() { return displayName; }
}
