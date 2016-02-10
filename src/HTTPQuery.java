import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class HTTPQuery {
	
	final String _CRLF = "\r\n";
	
	private String[] sendHttpRequest(String target, String requestType) throws IOException, UnknownHostException{
		String res[] = new String[2];
		String response = "";
		boolean fetchContent = requestType.equals("GET");
		try {
			URL uri = new URL(target);
			
			String host = uri.getHost();
			String path = uri.getPath();
			path = path.equals("") ? "/" : path;
			
			String requestLine = requestType + " " + path + " " + "HTTP/1.0";
			String headers = "Host: " + host;
			
			
			String currentRecievedLine = "";
			
			Socket socket = new Socket(InetAddress.getByName(host), 80);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			if(!fetchContent){
				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				writer.write(requestLine);
				writer.write(_CRLF.toCharArray());
				writer.flush();

				writer.write(headers);
				writer.write(_CRLF.toCharArray());
				writer.flush();

				writer.write(_CRLF.toCharArray());
				writer.flush();

				while((currentRecievedLine = reader.readLine()) != null){
					response += currentRecievedLine + "\n";
				}
				System.out.println(response);

				res[0] = response;
				res[1] = "";
				
				reader.close();
				
			} else {
				res = readHttpResponse(socket);
			}
			writer.close();
			socket.close();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new UnknownHostException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException();
		}
		return res;
	}
	//TODO: check if we can make it to one method instead of 2
	public String[] readHttpResponse(Socket connection) throws IOException{
		String ContentLengthHeader = "Content-Length: ";
		int contentLength = -1;
		String m_FullRequest = "";
		char[] m_MsgBodyCharBuffer;
		StringBuilder m_MessageBodyBuilder;
		String m_messageBodyString = "";
		
		try {
			if (connection.isClosed()) {
				return null;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = reader.readLine();

			// Read Request According to Http Protocol
			while (line != null && !line.equals("")) {
				// Check For Request With A Body Message
				if (line.indexOf(ContentLengthHeader) > -1) {
					String bodyContentLengthAsString = line.substring(ContentLengthHeader.length());
					contentLength = Integer.parseInt(bodyContentLengthAsString);
				}
				m_FullRequest += (line + "\n");
				line = reader.readLine();
			}

			// Handle With Request that Contain Body Message
			if (contentLength > 0) {
				m_MsgBodyCharBuffer = new char[contentLength];
				reader.read(m_MsgBodyCharBuffer);
				m_MessageBodyBuilder = new StringBuilder();

				for (int i = 0; i < m_MsgBodyCharBuffer.length; i++) {
					m_MessageBodyBuilder.append(m_MsgBodyCharBuffer[i]);
				}
				m_messageBodyString = m_MessageBodyBuilder.toString();
			}
			
			reader.close();

		} catch (IOException e) {
			System.err.println("ERROR: IO Exception");
			throw new IOException();
		}
		
		return new String[]{m_FullRequest, m_messageBodyString};
	}
	
	
	
	private String parseHttpHeadResponse(String response){
		String[] responseLines = response.split("\n");
		String _contentLength = "Content-Length: ";
		String _contentType = "Content-Type: ";
		String _seperator = "#_#@#_#";
		
		String lengthValue = "";
		String typeValue = "";
		
		for(int i = 0; i < responseLines.length; i++){
			String line = responseLines[i];
			if(line.indexOf(_contentLength) > -1 && line.indexOf(" ") > -1){
				lengthValue = (line.split(" "))[1];
			}
			else if(line.indexOf(_contentType) > -1 && line.indexOf(" ") > -1){
				typeValue = (line.split(" "))[1];
			}
		}
		
		return lengthValue + _seperator + typeValue;
	}

	// <img src="www.ynet.co.il/image/logo_2.png ...>
	//
	//
	public String sendHttpHeadRequestAndGetTypeAndLengthFromResponse(String target) throws UnknownHostException, IOException{
		String response = sendHttpRequest(target, "HEAD")[0];
		String lengthAndType = parseHttpHeadResponse(response);
		return lengthAndType;
	}
	
	
	/**
	 * 
	 * @param target : link to communicate with
	 * @return Response
	 */
	public String sendHttpHeadRequest(String target) throws IOException, UnknownHostException{
		return (sendHttpRequest(target, "HEAD"))[0];
	}
	
	/**
	 * 
	 * @param target : link to communicate with
	 * @return String Array -> [Response, Response-Mesaage-Body]
	 */
	public String[] sendHttpGetRequest(String target) throws IOException, UnknownHostException{
		return sendHttpRequest(target, "GET");
	}

}
