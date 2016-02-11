import java.io.IOException;
import java.net.URISyntaxException;

public class Downloader implements Runnable {

	String m_UrlToDownload;
	HTTPQuery m_QuerySite;
	ThreadPoolV1 m_threadPool;
	String[] m_DownloadedHtmlWithBody;
	AnalyzerTask m_AnalyzerTask;
	String m_PageSizeAndType;

	public Downloader(ThreadPoolV1 i_threadPool, String i_UrlToDownload) {
		m_UrlToDownload = i_UrlToDownload;
		m_threadPool = i_threadPool;
		m_QuerySite = new HTTPQuery();
	}

	@Override
	public void run() {
		try {

			if (!m_threadPool.containsUrlInList(m_UrlToDownload)) {
				
				m_threadPool.addToDownloadedList(m_UrlToDownload);
				
				m_DownloadedHtmlWithBody = m_QuerySite.sendHttpGetRequest(m_UrlToDownload);
				
				//TODO: Debug 
				System.out.println("site has been downloaded ****");

				if (m_DownloadedHtmlWithBody != null) {
					
					String body = m_DownloadedHtmlWithBody[1];

					m_PageSizeAndType = m_QuerySite.parseContentLengthFromHttpResponse(m_DownloadedHtmlWithBody[0]);

					m_AnalyzerTask = new AnalyzerTask(body, m_threadPool, m_UrlToDownload, m_PageSizeAndType);
					
					m_threadPool.putTaskInAnalyzersQueue((Runnable) m_AnalyzerTask);				
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}

}
