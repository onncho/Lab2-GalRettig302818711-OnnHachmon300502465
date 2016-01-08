import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		
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
	}

}
