import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

// SingleTone Class
public class ConfigurationObject {

	String m_defaultPage;
	private static String m_Port;
	private static String m_MaxThreads;
	private static String m_defaultPageFullUrl;
	private static String m_rootFolder;
	
	//Added For Lab2
	private static String m_MaxDownloaders;
	private static String m_MaxAnalyzers;
	private static List<String> m_imageExtensions;
	private static List<String> m_videoExtensions;
	private static List<String> m_documentExtensions;
	
	
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
		
		m_MaxDownloaders = i_confList.get("maxDownloader");
		m_MaxAnalyzers = i_confList.get("maxAnalyzer");
		m_imageExtensions = parseStringToList(i_confList.get("imageExtensionsNotParsed"));
		m_videoExtensions = parseStringToList(i_confList.get("videoExtensionsNotParsed"));
		m_documentExtensions = parseStringToList(i_confList.get("documentExtensionsNotParsed"));
		
	}
	
	private List<String> parseStringToList(String i_stringToParse) {
		String[] values = i_stringToParse.split(",");	
		List<String> res = new LinkedList<String>();
		
		for(String val : values)
		{
			if (val != null) {
				res.add(val);				
			}
		}
		
		return res;
	}

	public static String getMaxDownloaders() {
		return m_MaxDownloaders;
	}

	public static String getMaxAnalyzers() {
		return m_MaxAnalyzers;
	}

	public static List<String> getImageExtensions() {
		return m_imageExtensions;
	}

	public static List<String> getVideoExtensions() {
		return m_videoExtensions;
	}

	public static List<String> getDocumentExtensions() {
		return m_documentExtensions;
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
