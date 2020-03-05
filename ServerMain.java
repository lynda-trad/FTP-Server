import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMain
{
    public static void StartServer(AtomicInteger numThreads, ArrayList<Thread> list, int serverPort)
    {
        try
        {
            ServerSocket socket = new ServerSocket(serverPort);
            System.out.println("Server listening on port " + serverPort);

            while (true)
            {
                Socket client = socket.accept();
                Thread thrd = new Thread(new ServerCore(client));
                list.add(thrd);
                thrd.start();
                numThreads.incrementAndGet();
                System.out.println("Thread " + numThreads.get() + " started.");
            }
		} 
		catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
	}
	
	public static void main(String[] args)
    {
        int ServerPort = 1212;
        AtomicInteger numThreads = new AtomicInteger(0);
        ArrayList<Thread> list   = new ArrayList<Thread>();

        StartServer(numThreads, list, ServerPort);
    }
}