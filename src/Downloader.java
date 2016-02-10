import java.io.IOException;

public class Downloader implements Runnable {

	String m_UrlToDownload;
	HTTPQuery m_QuerySite;
	ThreadPoolV1 m_threadPool;
	String[] m_DownloadedHtmlWithBody;
	AnalyzerTask m_AnalyzerTask;
	
	public Downloader(ThreadPoolV1 i_threadPool, String i_UrlToDownload) {
		m_UrlToDownload = i_UrlToDownload;
		m_threadPool = i_threadPool;
		m_QuerySite = new HTTPQuery();
	}
	
	@Override
	public void run() {
		
		// TODO: download page
		try {
			m_DownloadedHtmlWithBody = m_QuerySite.sendHttpGetRequest(m_UrlToDownload);
			
			// TODO: check if the download succeed
			if (m_DownloadedHtmlWithBody != null) {
				String body = m_DownloadedHtmlWithBody[1];
				m_AnalyzerTask = new AnalyzerTask(body, m_threadPool);
				m_threadPool.putTaskInAnalyzersQueue((Runnable) m_AnalyzerTask);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
}
