import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private Socket s;
	private OutputStream os;
	private InputStream is;

	public Server(int portNumber) throws IOException
	{
		ServerSocket serverSocket = new ServerSocket(portNumber); // can be reused.
		s = serverSocket.accept();
		os = s.getOutputStream();
		is = s.getInputStream();
	}
	
	public void writeData(String input) throws IOException
	{
		os.write(input.getBytes());
	}
	
	public byte[] readData(int value) throws IOException
	{
		byte[] b = new byte[120];
		is.read(b);
		return b;
	}
	
	public boolean isConnected()
	{
		return s.isConnected();
	}
}
