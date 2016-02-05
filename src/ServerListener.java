

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerListener {
	
	private ThreadPool m_threadPool;
	
	// TODO: test for lab 2
	private ThreadPoolV1 m_pool;
	
	public ServerListener(ThreadPool i_ThreadPool)
	{
		this.m_threadPool = i_ThreadPool;
	}
	
	public ServerListener(ThreadPoolV1 i_pool) {
		m_pool = i_pool;
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
			//TODO: test for lab2
			//this.m_threadPool.push(handleRequest);
			m_pool.putTaskInDownloaderQueue(handleRequest);
		}
	}
}
