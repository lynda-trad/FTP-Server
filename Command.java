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

    public static void CommandList()
    {
        send("150 File status okay; about to open data connection.", output);
        send("226-Options: -a -l", output);
        send("226 6 matches total", output);
    }

    public static String CommandBye()
    {
        ServerMain.bye = true;
        return "231 User is \"logged out\". Service terminated." ;
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
        
        else if (command[0].equals("BYE"))
        {
            return CommandBye();
        }

        else if (command[0].equals("LIST"))
        {
            CommandList();
        }
        
        else if (command[0].equals("RETR"))
        {
            
        }
        
        else if (command[0].equals("PWD"))
        {
            return "257 " + dir + " comment ";
        }
        
        else if (command[0].equals("TYPE") && command[1].equals("I"))
        {
            return "200 TYPE is now 8-bit binary.";
        }
        
        else if (command[0].equals("EPRT"))
        {
            return "200 Command okay.";
        }
        
        else if (command[0].equals("CWD")) // go into directory command[1]
        {
            dir = command[1];
            return "250 FTP file transfer started correctly.";
        }
/*
        else if (command[0].equals("PORT"))
        {
            CommandPort(command);
        }
*/
        return "502 Command not implemented.";
    }
}

// return "500 Last command line completely unrecognized.";
// return "504 Last command invalid, action not possible at this time.";