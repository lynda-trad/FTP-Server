import java.io.PrintWriter;

public class Command
{
    public static String dir = "\"/home/lynda/projects/serveur-ftp\"";

    public static void send(String str, PrintWriter output)
    {
        output.print(str + "\r\n");
        output.flush();
    }

    public static String CommandUser(String[] command)
    {
        if (command[1].equals("anonymous"))
        {
            return "230 User logged in, proceed.";
        }
        else
        {
            return "330 Enter password (may be sent with hide-your-input)";
        }
    }

    public static String CommandPort(String[] command)
    {
        ServerMain.ServerPort = Integer.parseInt(command[1]);
        ServerMain.changePort();

        return "200 Last command received correctly.";
    }

    public static boolean checkPassword(String[] command)
    {
        //check if password command[1] is right for the user command[0]
        return false;
    }

    public static String CommandPass(String[] command)
    {
        if( checkPassword(command) )
        {
            return "230 User logged in, proceed.";
        }
        else
        {
            // return "430 Log-on time or tries exceeded, goodbye.";
            return "431 Log-on unsuccessful. User and/or password invalid.";
            //return "432 User not valid for this service.";
        }
    }
    
    public static String CommandAppe(String filename)
    {
        // append the file 
        return "250 FTP file transfer started correctly.";
    }

    public static String CommandDele(String filename)
    {
        // delete the file 
        return "254 Delete completed."; 
    }

    public static String CommandMKD(String newDir)
    {
        //create newDir
        return "257 Creating new directory";
    }

    public static String CommandRMD(String rmDir)
    {
        //remove rmDir
        return "250 FTP file transfer started correctly.";
    }

    public static void CommandList()
    {
        send("150 File status okay; about to open data connection.", output);
        send("226-Options: -a -l", output);
        send("226 6 matches total", output);
    }

    public static String CommandBye()
    {
        ServerMain.bye = true;
        return "231 User is \"logged out\". Service terminated.";
        //return "232 Logout command noted, will complete when transfer done.";
    }

    public static String run(String commandString, PrintWriter output)
    {
        String[] command = commandString.split(" ");

        if (command[0].equals("USER"))
        {
            return CommandUser(command);
        }

        else if (command[0].equals("PASS"))
        {
            return CommandPass(command);
        }
        
        else if (command[0].equals("BYE") || command[0].equals("QUIT"))
        {
            return CommandBye();
        }

        else if (command[0].equals("LIST"))
        {
            CommandList();
        }
        
        else if (command[0].equals("RETR")) //Retrieve a copy of the file
        {
            
        }
        
        else if (command[0].equals("PWD")) // Print working directory.
        {
            return "257 " + cwd + " comment ";
        }
        
        else if (command[0].equals("TYPE") && command[1].equals("I"))
        {
            return "200 TYPE is now 8-bit binary.";
        }
   
        else if (command[0].equals("APPE")) // Append the file
        {
            CommandAppe(command[1]);
        }

        else if (command[0].equals("DELE")) // Delete the file.
        {
            CommandDele(command[1]);
        }

        else if (command[0].equals("CWD")) // Change working directory.
        {
            cwd = command[1];
            return "250 FTP file transfer started correctly.";
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
            String rmDir = command[1];
            //remove rmDir
            return "250 FTP file transfer started correctly.";
        }

/*
        else if (command[0].equals("PORT")) //Specifies an address and port to which the server should connect.
        {
            CommandPort(command);
        }

        else if (command[0].equals("EPRT")) //Specifies an extended address and port to which the server should connect.
        {
            return "200 Command okay.";
        }
        */

        return "502 Command not implemented.";
    }
}
// return "500 Last command line completely unrecognized.";
// return "504 Last command invalid, action not possible at this time.";