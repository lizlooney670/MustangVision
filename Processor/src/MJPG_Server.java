import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MJPG_Server 
{
	private OutputStream outputStream;
	private Socket socket;
	 
	/**
	 * 
	 * @param serverSocket Create a simple server socket with any open port number on your pc
	 * @param serverName The name of your server
	 */
	public MJPG_Server(ServerSocket serverSocket, String serverName)
	{
		try{
			this.socket = serverSocket.accept();
			outputStream = socket.getOutputStream();
			outputStream.write((
			      "HTTP/1.0 200 OK\r\n" +
			      "Server: "+ serverName +"\r\n" +
			      "Connection: close\r\n" +
			      "Max-Age: 0\r\n" +
			      "Expires: 0\r\n" +
			      "Cache-Control: no-cache, private\r\n" + 
			      "Pragma: no-cache\r\n" + 
			      "Content-Type: multipart/x-mixed-replace; " +
			      "boundary=--BoundaryString\r\n\r\n").getBytes());
		}catch(IOException e){}
	}
	
	public Socket getServerSocket()
	{
		return socket;
	}
	
	/**
	 * 
	 * @param data The byte array of the image that should be written to the MJPG stream
	 * 
	 * Place this method inside a while loop in your program with the new image to constantly write to the server and create a powerful MJPG stream
	 */
	public void writeToServer(byte[] data)
	{
		try{
	    outputStream.write((
		        "--BoundaryString\r\n" +
		        "Content-type: image/jpg\r\n" +
		        "Content-Length: " +
		        data.length +
		        "\r\n\r\n").getBytes());
		    outputStream.write(data);
		    outputStream.write("\r\n\r\n".getBytes());
		    outputStream.flush();
		}catch(IOException e){}
	}
	
}
