
public class Link {

	String extension;
	String linkType;
	int lengthInBytes;
	String m_address;
	
	
	public Link(String i_address, String i_extension, String i_linkType, String i_lengthInBytes){
		m_address = i_address;
		extension = i_extension;
		linkType = i_linkType;
		lengthInBytes = Integer.parseInt(i_lengthInBytes);
	}
	
	public String getExtension() {
		return extension;
	}


	public String getLinkType() {
		return linkType;
	}


	public int getLengthInBytes() {
		return lengthInBytes;
	}
	
	public String getLinkAdress(){
		return m_address;
	}
	
	
}
