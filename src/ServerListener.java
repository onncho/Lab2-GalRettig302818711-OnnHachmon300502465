package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener {
	
	private ThreadPool m_threadPool;
	
	public ServerListener(ThreadPool i_ThreadPool)
	{
		this.m_threadPool = i_ThreadPool;
	}
	
	public void start() throws IOException
	{	
		String portString = ConfigurationObject.getPortNumber();
	
		if (portString.equals(null) || portString.equals("")) { 
			System.err.println("Port Number Is Not Valid, Please Check The Conf File");
			System.exit(1);
		}
		
		int port = Integer.parseInt(portString);
		
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(port);
		
		System.out.println("start listeneing on port: " + port + "\n");
		
		while (true) {
			Socket connection = serverSocket.accept();
			HandleRequest handleRequest = new HandleRequest(connection);
			this.m_threadPool.push(handleRequest);
		}
	}
}
