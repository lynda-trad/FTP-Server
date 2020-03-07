package ftp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class Command
{
    public static String cwd = System.getProperty("user.dir");

    private static String tempFilename = "";

    private static String username = "";

    public static void USER(String[] command) // Authentication username
    {
        username = command[1];

        if (username.equals("anonymous"))
        {
            ServerCore.send("230 User logged in, proceed.");
        }
        else
        {
            ServerCore.send("330 Enter password (may be sent with hide-your-input)");
        }
    }

    public static void PORT(String[] command) // Change of port
    {
        /*
        ServerMain.serverPort = Integer.parseInt(command[1]);
        ServerMain.changePort();
        */
        
        ServerCore.send("200 Last command received correctly.");
    }

    public static boolean checkPassword(String[] command) // Called by PASS()
    {
        //check if password command[1] is right for username

        return false;
    }

    public static void PASS(String[] command) // Authentication password
    {
        if(checkPassword(command))
        {
            ServerCore.send("230 User logged in, proceed.");
        }
        else
        {
            // ServerCore.send("430 Log-on time or tries exceeded, goodbye.");
            ServerCore.send("431 Log-on unsuccessful. User and/or password invalid.");
            // ServerCore.send("432 User not valid for this service.");
        }
    }

    public static void AUTH(String mecanism) // Authentication/Security Mechanism
    {
        ServerCore.send("504 Request denied for policy reasons.");
        /*
        ServerCore.send("234 Security data exchange complete.");
        ServerCore.send("534 Request denied for policy reasons.");
        */
    }
    
    public static void APPE(String filename) // Append file
    {
        File newF = new File(filename);
        try 
        {
			newF.createNewFile();
        } 
        catch (IOException e) 
        {
			e.printStackTrace();
            ServerCore.send("553 Service interrupted. Filename is incorrect.");
		}
        ServerCore.send("250 FTP file transfer started correctly.");
    }

    public static void DELE(String filename) // Delete file
    {
        File del = new File(filename);
        if(del.exists())
        {    
            del.delete();
            ServerCore.send("254 Delete completed."); 
        }
        else
            ServerCore.send("450 File not available.");
    }

    public static void RNFR(String pathname) // Rename file from
    {
        tempFilename = pathname;
        ServerCore.send("200 Waiting for RNTO command.");
    }

    public static void RNTO(String pathname) // Rename file to
    {
        File original = new File(tempFilename);

        if(original.exists())
        {
            File newF = new File(pathname);
            original.renameTo(newF);
            ServerCore.send("250 FTP file transfer started correctly.");
        }
        else
        {
            ServerCore.send("553 Service interrupted. Filename is incorrect.");
        }

        tempFilename = "";
    }

    public static void SIZE(String pathname) // Return the size of a file
    {
        File file = new File(pathname);

        if(file.exists())
        {
            ServerCore.send(pathname + " : " + String.valueOf(file.length()) + " bytes.");
            ServerCore.send("250 FTP file transfer started correctly.");
        }
        else
            ServerCore.send("553 Service interrupted. Filename is incorrect.");
    }

    public static void PWD() // Print working directory
    {
        ServerCore.send("257 " + cwd + " Current working directory.");
    }

    public static void CWD(String dir) // Change working directory
    {
        File d = new File(dir);
        if(d.exists())
        {
            cwd = dir;
            ServerCore.send("250 FTP file transfer started correctly.");
        }
        else
            ServerCore.send("553 Service interrupted. Pathname is incorrect.");
    }

    public static void CDUP() // Change to Parent Directory
    {
        File son = new File(cwd);
        if(son.exists())
        {
            cwd = son.getParent();
            ServerCore.send("200 Last command received correctly.");
        }
        else
            ServerCore.send("553 Service interrupted. Pathname is incorrect.");
    }

    public static void MKD(String newDir) // Create new directory
    {
        File newD = new File(cwd + '/' + newDir);
        newD.mkdir();
        ServerCore.send("257 Creating new directory");
    }

    public static void RMD(String rmDir) // Remove a directory
    {
        File dir = new File(rmDir);
        if(dir.exists())
        {
            dir.delete();
            ServerCore.send("250 FTP file transfer started correctly.");
        }
        else
            ServerCore.send("553 Service interrupted. Pathname is incorrect.");
    }

    public static void RMDA(String rmDira) // Remove a directory tree
    {
        File top = new File(rmDira);
        if(top.exists())
        {
            File[] fileList = top.listFiles();
            for(File files:fileList) 
            {
                files.delete();
            }
            top.delete();
            
            ServerCore.send("250 FTP file transfer started correctly.");
        }
        else
            ServerCore.send("553 Service interrupted. Pathname is incorrect.");
    }

    public static void sendFilesList(String pathname) // Called by LIST()
    {
        try 
        {
            File f = new File(pathname);
            if(f.exists())
            {
                File[] fileList = f.listFiles();

                for(File path:fileList) 
                {
                    ServerCore.send(cwd + '/' + path.getName());
                    System.out.println(cwd + '/' + path.getName());
                }

                ServerCore.send("226 Transfer complete.");
            }
            else
                ServerCore.send("450 File not available.");
        } 
        catch(Exception e) 
        {
            e.printStackTrace();
        }
    }

    public static void LIST() // Get a list of files and directories
    {
        ServerCore.send("150 Opening data canal.");
        sendFilesList(cwd);
    }

    public static void QUIT() // Quit
    {
        ServerMain.quit = true;
        ServerCore.send("221 Control canal closed by the service.");
    }

    public static void RETR(String filename) // Retrieve a file
    {
        ServerCore.send("125 Transfert starting.");
        /*
        return "150 File status reply.";

        // Ending
        return "226 Closing data canal.";
        return "250 File service ending.";

        // Errors
        return "425 Error while opening data canal.";
        return "426 Connection closed. Transfert interrupted.";
        return "451 Service interrupted. Local error.";
        */
    }
    
    public static void PASV() // Entering passive mode
    {
        ServerCore.send("227 Entering passive mode.");
    }

    public static void run(String commandString, PrintWriter output)
    {
        String[] command = commandString.split(" ");

        if (command[0].equals("USER"))
        {
            USER(command);
        }

        else if (command[0].equals("PASS"))
        {
            PASS(command);
        }
        
        else if(command[0].equals("AUTH"))
        {
            if(command.length > 1)
                AUTH(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if(command[0].equals("QUIT"))
        {
            QUIT();
        }

        else if (command[0].equals("LIST"))
        {
            LIST();
        }
        
        else if (command[0].equals("RETR")) //Retrieve a copy of the file
        {
            if(command.length > 1)
                RETR(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }
        
        else if (command[0].equals("TYPE") && command[1].equals("I"))
        {
            ServerCore.send("200 TYPE is now 8-bit binary.");
        }
   
        else if (command[0].equals("APPE")) // Append the file
        {
            if(command.length > 1)
                APPE(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if (command[0].equals("DELE")) // Delete the file.
        {
            if(command.length > 1)
                DELE(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if(command[0].equals("RNFR")) // Rename the file (from)
        {
            if(command.length > 1)
                RNFR(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if(command[0].equals("RNTO")) // Rename the file (to)
        {
            if(command.length > 1)
                RNTO(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if(command[0].equals("SIZE")) // Gets the file size
        {
            if(command.length > 1)
                SIZE(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if (command[0].equals("PWD")) // Print working directory.
        {
            PWD();
        }

        else if (command[0].equals("CWD")) // Change working directory.
        {
            if(command.length > 1)
                CWD(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if (command[0].equals("CDUP")) // Change to parent directory.
        {
            CDUP();
        }

        else if ( command[0].equals("MKD") || command[0].equals("XMKD") ) // create a directory
        {
            if(command.length > 1)
                MKD(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if ( command[0].equals("RMD") || command[0].equals("XRMD") ) // remove a directory
        {
            if(command.length > 1)
                RMD(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if (command[0].equals("RMDA")) // remove a directory tree
        {
            if(command.length > 1)
                RMDA(command[1]);
            else
                ServerCore.send("500 Invalid parameters");
        }

        else if(command[0].equals("PASV"))
        {
            PASV();
        }

        else if (command[0].equals("PORT")) //Specifies an address and port to which the server should connect.
        {
            PORT(command);
        }

/*
        else if (command[0].equals("EPRT")) //Specifies an extended address and port to which the server should connect.
        {
            ServerCore.send("200 Last command received correctly.");
        }
*/
        else
            ServerCore.send("502 Command not implemented.");
//          ServerCore.send("500 Last command line completely unrecognized.");
//          ServerCore.send("504 Last command invalid, action not possible at this time.");
    }
}