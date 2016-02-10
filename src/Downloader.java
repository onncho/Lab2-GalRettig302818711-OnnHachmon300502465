import java.io.IOException;

public class Downloader implements Runnable {

	String m_UrlToDownload;
	HTTPQuery m_QuerySite;
	SynchronizedQueueLL m_HtmlsQueueToAnalyze;
	String[] m_DownloadedHtmlWithBody;
	AnalyzerTask m_AnalyzerTask;
	
	public Downloader(SynchronizedQueueLL i_HtmlsQueueToAnalyze, String i_UrlToDownload) {
		m_UrlToDownload = i_UrlToDownload;
		m_HtmlsQueueToAnalyze = i_HtmlsQueueToAnalyze;
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
				m_AnalyzerTask = new AnalyzerTask(body);
				m_HtmlsQueueToAnalyze.enqueue((Runnable) m_AnalyzerTask);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
	
}
