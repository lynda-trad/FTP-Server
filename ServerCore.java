import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerCore extends Thread
{
    private Socket         client = null;
    private PrintWriter    output;
    private BufferedReader input;

    public ServerCore(Socket client)
    {
        this.client = client;
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
			
            while (true) //temporary
            {
                input = new BufferedReader(new InputStreamReader(client.getInputStream()));
                //System.out.print("Reader and writer created. ");

                String inString;
                while ((inString = input.readLine()) == null) ;
                System.out.println("Read command : " + inString+ '\n');

                String outString = Command.run(inString, output);
                System.out.println("Server sending:" + outString + '\n');

                output.print(outString + "\r\n");
                output.flush();
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