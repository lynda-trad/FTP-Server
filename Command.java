import java.io.PrintWriter;

public class Command
{
    public static String CommandUser(String[] command)
    {
        if (command[1].equals("anonymous"))
        {
            System.out.println("user anon : ok");
            return ("230 User logged in, proceed.");
        }
        return null;
    }

    public static void send(String str, PrintWriter output)
    {
        output.print(str + "\r\n");
        output.flush();
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

        }

        else if (command[0].equals("BYE"))
        {

        }

        else if (command[0].equals("LIST"))
        {
            send("150 File status okay; about to open data connection.", output);
            send("226-Options: -a -l", output);
            send("226 6 matches total", output);
        }
        
        else if (command[0].equals("RETR"))
        {

        }
        
        else if (command[0].equals("PWD"))
        {
            return "257 \"/home/projects/serveur-ftp\" comment ";
        }
        
        else if (command[0].equals("TYPE") && command[1].equals("I"))
        {
            return "200 TYPE is now 8-bit binary.";
        }
        
        else if (command[0].equals("EPRT"))
        {
            return "200 Command okay.";
        }
        
        return "502 Command not implemented.";
    }
}