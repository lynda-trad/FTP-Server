package ftp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerCore extends Thread
{
    private Socket client = null;
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

    private void InitConnection()
    {
        send("220 Welcome.");
        System.out.print("Connection accepted");
    }

    public void run()
    {
        try
        {
            output = new PrintWriter(client.getOutputStream(), true);
			InitConnection();
			
            while (!ServerMain.quit)
            {
                input = new BufferedReader(new InputStreamReader(client.getInputStream()));

                String inString;
                while ((inString = input.readLine()) == null) ;
                System.out.println("Read command : " + inString+ '\n');

                Command.run(inString);
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