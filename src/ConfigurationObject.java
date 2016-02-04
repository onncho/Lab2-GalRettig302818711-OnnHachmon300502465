package webserver;

import java.util.HashMap;

// SingleTone Class
public class ConfigurationObject {

	String m_defaultPage;
	private static String m_Port;
	private static String m_MaxThreads;
	private static String m_defaultPageFullUrl;
	private static String m_rootFolder;
	
	private static ConfigurationObject m_Configuration = new ConfigurationObject();
	
	private ConfigurationObject() {
		
	}
	
	public static ConfigurationObject getConfigurationObject() {
		return m_Configuration;
	}
	
	public void setup(HashMap<String, String> i_confList) {
		m_Port = i_confList.get("port");
		m_rootFolder = i_confList.get("root");
		m_defaultPage = i_confList.get("defaultPage");
		m_MaxThreads = i_confList.get("maxThreads");
		m_defaultPageFullUrl = m_rootFolder + "/" + m_defaultPage;
	}
	
	public static String getRoot()
	{
		return m_rootFolder;
	}
	
	public static String getDefaultPage()
	{
		return m_defaultPageFullUrl;
	}
	
	public static String getPortNumber()
	{
		return m_Port;
	}
	
	public static int getMaxThreads()
	{
		return Integer.parseInt(m_MaxThreads);	
	}
}
