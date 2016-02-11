
import java.io.IOException;
import java.util.HashMap;

public class WebServer {

	public static void main(String[] args) throws IOException
	{
		HashMap<String, String> m_confList = new HashMap<>();
		m_confList = Utils.readConfFile();

		// create configuration object
		ConfigurationObject.getConfigurationObject().setup(m_confList);

		if(ConfigurationObject.getPortNumber() == null){
			throw new IOException("Problem in Reading The Config File");
		}
		
		/*
		// TODO: test for lab2
		//ThreadPool threadPool = new ThreadPool(ConfigurationObject.getMaxThreads());
		SynchronizedQueueLL taskQ = new SynchronizedQueueLL();
		ThreadPoolV1 webRequestsPool = new ThreadPoolV1(taskQ, ConfigurationObject.getMaxThreads());
		
		//ServerListener webserver = new ServerListener(threadPool);
		ServerListener webSrv = new ServerListener(webRequestsPool);

		//webserver.start();
		webSrv.start();
		*/
		
		String url = "http://www.faculty.idc.ac.il/smozes/index.html";//"http://www.play-hookey.com";
		
		Crawler myCrawler = new Crawler(url, false, false);
		myCrawler.startCrawling();
	}

}
