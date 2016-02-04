import java.io.IOException;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) throws IOException {
		/*
		int startPort = 80;
		int endPort = 100;
		
		String target = "194.153.101.3";
		
		String target2 = "http://www.idc.ac.il";
		
		
		ArrayList<Integer> result = new ArrayList<Integer>();
		
		//result = PortScanner.runScan(target, startPort, endPort);
		result = PortScanner.runScan(target2, startPort, endPort);
		
		for (Integer port : result) {
			System.out.println("Port Open: " + port + " is Open");
		}
		 */
		
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
