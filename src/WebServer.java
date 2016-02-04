package webserver;

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

		ThreadPool threadPool = new ThreadPool(ConfigurationObject.getMaxThreads());
		ServerListener webserver = new ServerListener(threadPool);

		webserver.start();
	}

}
