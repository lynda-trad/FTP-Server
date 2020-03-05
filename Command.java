import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;

public class Command
{
    public static String cwd = "/home/lynda/test";

    private static String tempFilename = "";

    private static String username = "";

    public static void CommandUser(String[] command) //	Authentication username
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

    public static void CommandPort(String[] command)
    {
        /*
        ServerMain.serverPort = Integer.parseInt(command[1]);
        ServerMain.changePort();
        */
        
        ServerCore.send("200 Last command received correctly.");
    }

    public static boolean checkPassword(String[] command)
    {
        //check if password command[1] is right for username

        return false;
    }

    public static void CommandPass(String[] command) // Authentication password
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

    public static void CommandAuth(String mecanism) // Authentication/Security Mechanism
    {
        ServerCore.send("504 Request denied for policy reasons.");
        /*
        ServerCore.send("234 Security data exchange complete.");
        ServerCore.send("534 Request denied for policy reasons.");
        */
    }
    
    public static void CommandAppe(String filename) // Append file
    {
        // append the file 
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

    public static void CommandDele(String filename) // Delete file
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

    public static void CommandRnfr(String pathname) // Rename file from
    {
        tempFilename = pathname;
    }

    public static void CommandRnto(String pathname) // Rename file to
    {
        //rename the file
        File original = new File(tempFilename);

        if(original.exists())
        {
            File newF = new File(pathname);
            original.renameTo(newF);
            ServerCore.send("250 FTP file transfer started correctly.");
        }
        else
            ServerCore.send("553 Service interrupted. Filename is incorrect.");
    }

    public static void CommandSize(String pathname) // Return the size of a file
    {
        File file = new File(pathname);
        File absolute = new File(file.getAbsolutePath());
        file.renameTo(absolute);

        if(file.exists())
        {    
            ServerCore.send(String.valueOf(file.length()));
            ServerCore.send("250 FTP file transfer started correctly.");
        }
        else
            ServerCore.send("553 Service interrupted. Filename is incorrect.");
    }

    public static void CommandPWD() // Print working directory
    {
        ServerCore.send("257 " + cwd + " Current working directory.");
    }

    public static void CommandCWD(String dir) // Change working directory
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

    public static void CommandCDUP() // Change to Parent Directory
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

    public static void CommandMKD(String newDir) // Create new directory
    {
        File newD = new File(cwd + '/' + newDir);
        newD.mkdir();
        ServerCore.send("257 Creating new directory");
    }

    public static void CommandRMD(String rmDir) // Remove a directory
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

    public static void CommandRMDA(String rmDira) // Remove a directory tree
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

    public static void sendFilesList(String pathname)
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

    public static void CommandList()
    {
        ServerCore.send("150 Opening data canal.");
        sendFilesList(cwd);
    }

    public static void CommandBye()
    {
        ServerMain.bye = true;
        ServerCore.send("231 User is \"logged out\". Service terminated.");
        //ServerCore.send("232 Logout command noted, will complete when transfer done.");
    }

    public static void CommandQuit()
    {
        ServerMain.bye = true;
        ServerCore.send("221 Control canal closed by the service.");
    }

    public static void CommandRetr(String filename)
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
    
    public static void CommandPASV()
    {
        ServerCore.send("227 Entering passive mode.");
    }

    public static void run(String commandString, PrintWriter output)
    {
        String[] command = commandString.split(" ");

        if (command[0].equals("USER"))
        {
            CommandUser(command);
        }

        else if (command[0].equals("PASS"))
        {
            CommandPass(command);
        }
        
        else if(command[0].equals("AUTH"))
        {
            CommandAuth(command[1]);
        }

        else if (command[0].equals("BYE"))
        {
            CommandBye();
        }

        else if(command[0].equals("QUIT"))
        {
            CommandQuit();            
        }

        else if (command[0].equals("LIST"))
        {
            CommandList();
        }
        
        else if (command[0].equals("RETR")) //Retrieve a copy of the file
        {
            CommandRetr(command[1]);
        }
        
        else if (command[0].equals("TYPE") && command[1].equals("I"))
        {
            ServerCore.send("200 TYPE is now 8-bit binary.");
        }
   
        else if (command[0].equals("APPE")) // Append the file
        {
            CommandAppe(command[1]);
        }

        else if (command[0].equals("DELE")) // Delete the file.
        {
            CommandDele(command[1]);
        }

        else if(command[0].equals("RNFR")) // Rename the file (from)
        {
            CommandRnfr(command[1]);
        }

        else if(command[0].equals("RNTO")) // Rename the file (to)
        {
            CommandRnto(command[1]);
        }

        else if(command[0].equals("SIZE"))
        {
            CommandSize(command[1]);
        }

        else if (command[0].equals("PWD")) // Print working directory.
        {
            CommandPWD();
        }

        else if (command[0].equals("CWD")) // Change working directory.
        {
            CommandCWD(command[1]);
        }

        else if (command[0].equals("CDUP")) // Change to parent directory.
        {
            CommandCDUP();
        }

        else if ( command[0].equals("MKD") || command[0].equals("XMKD") ) // create a directory
        {
            CommandMKD(command[1]);
        }

        else if ( command[0].equals("RMD") || command[0].equals("XRMD") ) // remove a directory
        {
            CommandRMD(command[1]);
        }

        else if (command[0].equals("RMDA")) // remove a directory tree
        {
            CommandRMDA(command[1]);
        }

        else if(command[0].equals("PASV"))
        {
            CommandPASV();
        }
        else if (command[0].equals("PORT")) //Specifies an address and port to which the server should connect.
        {
            CommandPort(command);
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