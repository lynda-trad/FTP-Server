import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Input 
{	
	Protocol handler;
	InputStream in;
	
	public Input(InputStream in, Protocol handler) throws IOException 
	{
		this.in = in;
		this.handler = handler;
	}
	
	public void doRun() throws IOException 
	{
		String str, str2;
		ArrayList<String> userList;
		
		try (BufferedReader is = new BufferedReader(new InputStreamReader(in)))
		{
			boolean stop = false;
			while (!stop) 
			{
				String line = is.readLine();
				switch (line) 
				{
					// Controle d'acces
					case "USER":
						str = is.readLine();
						//handler.sendUsername(str);
					break;
						
					case "PASS":
						str = is.readLine();
						//handler.sendPass(str);
					break;

					case "CWD":
						str = is.readLine();
					break;

					case "QUIT":
						handler.sendQuit();
					break;

					// Parametres de transfert
					case "PORT":
						str = is.readLine();
						// serverCore
					break;
		
					case "PASV":
						str = is.readLine();
					break;

					case "STRU":
						str = is.readLine();
					break;

					case "MODE":
						str = is.readLine();
					break;

					// Service FTP
					case "RETR":
						str = is.readLine();
					break;

					case "RNFR":
						str = is.readLine();
					break;
					
					default:
						throw new ProtocolException("Invalid input");
				}
			}
		}
	}
}