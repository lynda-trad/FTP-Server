import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMain
{
    public static int serverPort = 1212;

    private static AtomicInteger numThreads = new AtomicInteger(0);

    private static ArrayList<Thread> list   = new ArrayList<Thread>();

    public static boolean bye = false;

    public static void changePort()
    {
        StartServer(ServerMain.numThreads, ServerMain.list, ServerMain.serverPort);
    }

    public static void StartServer(AtomicInteger numThreads, ArrayList<Thread> list, int serverPort)
    {
        try
        {
            ServerSocket socket = new ServerSocket(serverPort);
            System.out.println("Server listening on port " + serverPort);

            while (!bye)
            {
                Socket client = socket.accept();
                Thread thrd = new Thread(new ServerCore(client));
                list.add(thrd);
                thrd.start();
                numThreads.incrementAndGet();
                System.out.println("Thread " + numThreads.get() + " started.");
            }

            socket.close();
		} 
		catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
	}
	
	public static void main(String[] args)
    {
        StartServer(ServerMain.numThreads, ServerMain.list, ServerMain.serverPort);
    }
}