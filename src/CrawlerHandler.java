import java.util.List;

public class CrawlerHandler {

	private ThreadPool Downloader;
	private ThreadPool Analyzer;
	private List<String> m_UrlList;
	
	public CrawlerHandler() {
		// TODO Auto-generated constructor stub
		Downloader = new ThreadPool(Integer.parseInt(ConfigurationObject.getMaxDownloaders()));
		Analyzer = new ThreadPool(Integer.parseInt(ConfigurationObject.getMaxAnalyzers()));
		
	}
}
