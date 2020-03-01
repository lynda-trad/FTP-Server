import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerCore extends Thread
{	
	private int port;
	ServerSocket ss;
	private boolean stop = false;
	private IChatLogger logger = null;
	
	public ServerCore(int port) throws IOException 
	{
		this.port = port;
		logger = new TextChatLogger();
		logger.systemMessage("Server started...");
		this.start();
	}
	
	public void run() 
	{
		try (ServerSocket ss = new ServerSocket(port)) 
		{
			ss.setSoTimeout(1000);
			while (!stop) 
			{
				try 
				{
					Socket s = ss.accept();
					logger.clientConnected(s.toString());
					new Thread(new HandleClient(s, logger)).start();
				} 
				catch (SocketTimeoutException ex) 
				{
				}
			}
		} 
		catch (IOException e) 
		{
			System.out.println("Could not bind port " + port);
			//logger.getLogger(ServerCore.class.getName()).log(Level.SEVERE, null, e);
		}
	}
	
	public synchronized void finish() 
	{
		Model.clearAll();
		stop = true;
	}
}
