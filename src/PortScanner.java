import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class PortScanner {
	
	
	public static ArrayList<Integer> runScan(String i_Target, int i_StartPort, int i_EndPort) {
		
		ArrayList<Integer> openPorts = new ArrayList<>();
		String regexDefinition = "^.[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}/..[0-9]{1,3}";
		String regexHttp = "http://";
		String targetParsed = null;
		
		// if target is ip version 4
		if (i_Target.matches(regexDefinition) == false) {			
			
			i_Target = i_Target.replaceFirst(regexHttp, "");
			
			InetAddress address;
			
			try {
				address = InetAddress.getByName(i_Target);
				targetParsed = address.getHostAddress();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	
		for (int port = i_StartPort; port < i_EndPort; port++) {
			
			try 
			{	
				Socket socket = new Socket();
				
				if (targetParsed != null) {					
					socket.connect(new InetSocketAddress(targetParsed, port), 1000);
				}
				
				openPorts.add(port);
				socket.close();
			} 
			catch (Exception e) 
			{
				System.out.println("Port : " + port + " is Closed");
			}
			
		}
		
		return openPorts;
		
	}
	
}
