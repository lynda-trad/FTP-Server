import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EventListener;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServerCore extends Thread
{
    private Socket         client = null;
    private static PrintWriter    output;
    private static BufferedReader input;

    public ServerCore(Socket client)
    {
        this.client = client;
    }

    public static void send(String str)
    {
        output.print(str + "\r\n");
        output.flush();
    }

    public static void sendFilesList(String pathname)
    {
        //send("-rwxr-xr-x 1 100 100 14757 a.out\r\n");
        try (Stream<Path> walk = Files.walk(Paths.get(pathname)))
        {
            List<String> files = walk.filter(Files::isRegularFile)
            .map(x -> x.toString()).collect(Collectors.toList());
            
            if(files.size() > 0)
            {    for(int i = 0 ; i < files.size() - 1; ++i)
                    send(files.get(i));
            }

            List<String> dir = walk.filter(Files::isDirectory)
            .map(x -> x.toString()).collect(Collectors.toList());
            
            if(dir.size() > 0)
            {    for(int i = 0 ; i < dir.size(); ++i)
                    send(dir.get(i));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void InitConnection()
    {
        output.print("220 Welcome.\r\n");
        output.flush();
        System.out.print("Connection accepted");
    }

    public void run()
    {
        try
        {
            output = new PrintWriter(client.getOutputStream(), true);
			InitConnection();
			
            while (!ServerMain.bye) 
            {
                input = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String inString;
                while ((inString = input.readLine()) == null) ;
                System.out.println("Read command : " + inString+ '\n');

                Command.run(inString, output);
                /*
                String outString = Command.run(inString, output);
                System.out.println("Server sending:" + outString + '\n');

                output.print(outString + "\r\n");
                output.flush();
                */
            }

		} 
		catch (IOException e)
        {
            e.printStackTrace();
		} 
		finally
        {
            try
            {
                client.close();
			} 
			catch (IOException e)
            {
                e.printStackTrace();
            }
			System.out.println("Client thread closed.");
        }
    }
}