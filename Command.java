package ftp;

import java.io.*;

import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class Command
{
    public static String cwd = System.getProperty("user.dir");

    private static String tempFilename = "";

    private static String username = "";

    private static String downloadFolder = "";

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

    public static void ACCT()
    {
        ServerCore.send("230 User logged in, proceed.");
    }

    public static boolean checkPassword(String[] command) // Called by PASS()
    {
        //checks if password command[1] is right for username

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

    public static void AUTH(String mecanism) // Authentication / Security Mechanism
    {
        ServerCore.send("504 Request denied for policy reasons.");
        /*
        ServerCore.send("234 Security data exchange complete.");
        ServerCore.send("534 Request denied for policy reasons.");
        */
    }

    public static void ADAT() // Authentication / Security Data
    {
        ServerCore.send("503 Request denied for policy reasons.");
        /*
        ServerCore.send("235 Security data exchange complete.");
        ServerCore.send("535 Request denied for policy reasons.");
        */
    }

    public static void PORT(String[] command) // Change of port
    {
        /*
        ServerMain.serverPort = Integer.parseInt(command[1]);
        ServerMain.changePort();
        */

        ServerCore.send("200 Last command received correctly.");
    }

    public static void PASV() // Entering passive mode
    {
        ServerCore.send("227 Entering passive mode.");
    }

    public static void QUIT() // Quit
    {
        ServerMain.quit = true;
        ServerCore.send("221 Control canal closed by the service.");
        ServerCore.send("You can close this window.");
    }

    private static String fileRights(File f)
    {
        String rights = "";
        Path path = Paths.get(f.getAbsolutePath());

        try
        {
            if(f.isFile())
                rights += "-";
            else
                rights += "d";

            PosixFileAttributeView posixView = Files.getFileAttributeView(path, PosixFileAttributeView.class);

            PosixFileAttributes attributes = posixView.readAttributes();
            Set<PosixFilePermission> permissions = attributes.permissions();

            rights += PosixFilePermissions.toString(permissions);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return rights;
    }

    private static String sendInfo(File f) // Called by sendLIST
    {
        String info = "";
        Path path = Paths.get(f.getAbsolutePath());

        try
        {
            //rights
            info += fileRights(f);

            // links
            info += " " + Files.getAttribute(path, "unix:nlink");

            // user
            UserPrincipal owner = Files.getOwner(path);
            info = info + " " + owner ;

            // group owner
            GroupPrincipal group = Files.readAttributes(f.toPath(), PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).group();
            info += " " + group ;

            //size
            info += " " + String.valueOf(f.length());

            //date
            info += " " + new Date(f.lastModified());

            //pathname
            info += " " + f.getAbsolutePath();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return info;
    }

    private static void sendLIST(String pathname) // Called by LIST()
    {
        try
        {
            File f = new File(pathname);
            if(f.exists() && !f.isFile())
            {
                File[] fileList = f.listFiles();

                for(File path:fileList)
                {
                    ServerCore.send(sendInfo(path));
                }

                ServerCore.send("226 Transfer complete.");
            }
            else if(f.exists() && f.isFile())
            {
                ServerCore.send(sendInfo(f));
            }
            else
                ServerCore.send("450 File not available.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void LIST(String pathname) // Returns information of a file or directory if specified, else information of the current working directory is returned.
    {
        ServerCore.send("150 Opening data canal.");
        sendLIST(pathname);
    }

    private static void sendNLST(String pathname) // Called by NLST()
    {
        try
        {
            File f = new File(pathname);
            if(f.exists())
            {
                File[] fileList = f.listFiles();

                for(File path:fileList)
                {
                    if (path.isFile())
                    {
                        ServerCore.send(pathname + '/' + path.getName());
                    }
                }

                ServerCore.send("226 Transfer complete.");
            }
            else
                ServerCore.send("450 Path does not exist.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void NLST(String pathname) // Get a list of file names in a specified directory.
    {
        ServerCore.send("150 Opening data canal.");
        sendNLST(pathname);
    }

    public static void MLSD(String pathname) // Lists the contents of a directory if a directory is named.
    {
        //ServerCore.send("150 initial");

        LIST(pathname);

        //ServerCore.send("226 final");
    }

    public static void RETR(String filename) // Retrieve a file
    {
        ServerCore.send("125 Transfert starting.");

        String[] path = filename.split("/");
        String file = path[path.length - 1];

        File source = new File(filename);
        if(source.isFile())
        {
            try
            {
                InputStream src = new BufferedInputStream(new FileInputStream(filename));

                String download = downloadFolder + '/' + file;
                Path dest = Paths.get(download);

                try
                {
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    ServerCore.send("425 Error while opening data canal : could not copy to destination folder.");
                }
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                ServerCore.send("425 Error while opening data canal : source does not exist.");
            }
        }
        else
        {
            ServerCore.send("425 Error while opening data canal : source is not a file.");
        }

        ServerCore.send("226 Closing data canal.");
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
            if(del.isFile())
            {
                del.delete();
                ServerCore.send("254 Delete completed.");
            }
            else
                ServerCore.send("450 File not available.");
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

    public static void MDTM(String pathname) // Get a file's last modified date and time
    {
        File file = new File(pathname);

        if(file.exists())
        {
            Path p = Paths.get(pathname);
            try
            {
                ServerCore.send("213 " + Files.getLastModifiedTime(p));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
            ServerCore.send("500 Invalid parameters.");
    }

    public static void MFCT(String[] command) // Modify a file or folder's creation date and time
    {
        String date = command[1];
        String pathname = command[2];

        File file = new File(pathname);

        if(file.exists() && date.length() == 14)
        {
            int year   = Integer.valueOf(date.charAt(3) + date.charAt(2) + date.charAt(1) + date.charAt(0)) - 86;
            int month  = Integer.valueOf(date.substring(4,5) + date.substring(3,4))  + 1;
            int day    = Integer.valueOf(date.substring(6,7) + date.substring(5,6))  - 2;
            int hour   = Integer.valueOf(date.substring(8,9) + date.substring(7,8))     + 1 ;
            int min    = Integer.valueOf(date.substring(10,11) + date.substring(9,10))  ;
            int sec    = Integer.valueOf(date.substring(12,13) + date.substring(11,12)) ;

            Calendar creationDate = Calendar.getInstance();
            creationDate.set(year + 1900, month, day, hour, min);
            creationDate.set(creationDate.SECOND, sec);

            Path p = Paths.get(pathname);
            try
            {
                Files.setAttribute(p, "creationTime", FileTime.fromMillis(creationDate.getTimeInMillis()));
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }

            ServerCore.send("213 Creation date and time updated");
        }
        else
            ServerCore.send("500 Invalid parameters. MFCT has this format : MFCT YYYYMMDDHHMMSS path");
    }

    public static void MFMT(String[] command) // Modify a file or folder's last modified date and time
    {
        String date = command[1];
        String pathname = command[2];

        File file = new File(pathname);

        if(file.exists() && date.length() == 14)
        {
            int year   = Integer.valueOf(date.charAt(3) + date.charAt(2) + date.charAt(1) + date.charAt(0)) - 76;
            int month  = Integer.valueOf(date.substring(4,5) + date.substring(3,4))  + 1;
            int day    = Integer.valueOf(date.substring(6,7) + date.substring(5,6))  - 2;
            int hour   = Integer.valueOf(date.substring(8,9) + date.substring(7,8))     ;
            int min    = Integer.valueOf(date.substring(10,11) + date.substring(9,10))  ;
            int sec    = Integer.valueOf(date.substring(12,13) + date.substring(11,12)) ;

            Calendar newDate = Calendar.getInstance();
            newDate.set(year + 1900, month, day, hour, min);
            newDate.set(newDate.SECOND, sec);

            file.setLastModified(newDate.getTimeInMillis());

            ServerCore.send("213 Last modified date and time updated");
        }
        else
            ServerCore.send("500 Invalid parameters. MFMT has this format : MFMT YYYYMMDDHHMMSS path");
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

    public static long folderSize(File file) // Called by DSIZ
    {
        long size = 0;
        File[] fileList = file.listFiles();

        for(File path:fileList)
        {
            if(path.isDirectory())
                size += folderSize(path);
            else
                size += path.length();
        }
        return size;
    }

    public static void DSIZ(String pathname) // Get the directory size
    {
        long size = 0;
        try
        {
            File f = new File(pathname);
            if(f.exists())
            {
                size = folderSize(f);
                ServerCore.send("226 " + pathname + " : " + String.valueOf(size) + " bytes.");
            }
            else
                ServerCore.send("450 File not available.");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void AVBL(String pathname) // Get available space in directory
    {
        File f = new File(pathname);
        if(f.exists())
        {
            ServerCore.send( "Usable space : " + String.valueOf(f.getUsableSpace()) + " bytes.");
            ServerCore.send("213 File status.");
        }
        else
            ServerCore.send("553 Service interrupted. Pathname is incorrect.");
    }

    public static void CCC() // Clear the command channel
    {
        ServerCore.send("\033[H\033[2J");
    }

    public static void run(String commandString)
    {
        String[] command = commandString.split(" ");
        String commandS = command[0];

        switch(commandS)
        {
            case "USER": // Authentication username
                if(command.length > 1)
                    USER(command);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "ACCT": // Account information
                ACCT();
            break;

            case "PASS": // Authentication password
                PASS(command);
            break;

            case "AUTH": // Authentication / Security Mechanism
                if(command.length > 1)
                    AUTH(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "ADAT": // Security Data
                ADAT();
            break;

            case "PASV": // Passive mode
                PASV();
            break;

            case "PORT": // Port
                PORT(command);
            break;

            case "TYPE": // Type
                if(command[1].equals("I"))
                    ServerCore.send("200 TYPE is now 8-bit binary.");
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "QUIT": // Quit
                QUIT();
            break;

            case "LIST": // Returns information of a file or directory if specified, else information of the current working directory is returned.
                if(command.length > 1)
                    LIST(command[1]);
                else
                    LIST(cwd);
            break;

            case "NLST": // Get a list of file names in a specified directory.
                if(command.length > 1)
                    NLST(command[1]);
                else
                    NLST(cwd);
            break;

            case "MLSD": // Lists the contents of a directory if a directory is named.
                if(command.length > 1)
                    MLSD(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "RETR": //Retrieve a copy of the file
                if(command.length > 2)
                {
                    downloadFolder = command[2];
                    RETR(command[1]);
                }
                else
                    ServerCore.send("500 Invalid parameters : RETR /src /dest");
            break;

            case "APPE": // Append the file
                if(command.length > 1)
                    APPE(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "DELE": // Delete the file.
                if(command.length > 1)
                    DELE(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "RNFR": // Rename the file (from)
                if(command.length > 1)
                    RNFR(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "RNTO": // Rename the file (to)
                if(command.length > 1)
                    RNTO(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "SIZE": // Gets the file size
                if(command.length > 1)
                    SIZE(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "MDTM": // Get a file's last modified date and time
                if(command.length > 1)
                    MDTM(command[1]);
                else
                    ServerCore.send("500 Invalid parameters.");
            break;

            case "MFCT": // Modify a file or folder's creation date and time
                if(command.length > 2)
                    MFCT(command);
                else
                    ServerCore.send("500 Invalid parameters. MFCT has this format : MFCT YYYYMMDDHHMMSS path");
            break;

            case "MFMT": // Modify a file or folder's last modified date and time
                if(command.length > 2)
                    MFMT(command);
                else
                    ServerCore.send("500 Invalid parameters. MFCT has this format : MFMT YYYYMMDDHHMMSS path");
            break;

            case "PWD": // Print working directory.
            case "XPWD":
                PWD();
            break;

            case "CWD": // Change working directory.
                if(command.length > 1)
                    CWD(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "CDUP": // Change to parent directory.
            case "XCUP":
                CDUP();
            break;

            case "MKD": // Create a directory
            case "XMKD":
                if(command.length > 1)
                    MKD(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "RMD": // Remove a directory
            case "XRMD":
                if(command.length > 1)
                    RMD(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "RMDA": // Remove a directory tree
                if(command.length > 1)
                    RMDA(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "DSIZ": // Get the directory size
                if(command.length > 1)
                    DSIZ(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "AVBL": // Available space in directory
                if(command.length > 1)
                    AVBL(command[1]);
                else
                    ServerCore.send("500 Invalid parameters");
            break;

            case "CCC": // Clear the command channel
                CCC();
            break;

/*
            case "EPRT": //Specifies an extended address and port to which the server should connect.
                ServerCore.send("200 Last command received correctly.");
            break;
*/

            default:
                ServerCore.send("502 Command not implemented.");
//              ServerCore.send("500 Last command line completely unrecognized.");
//              ServerCore.send("504 Last command invalid, action not possible at this time.");
            break;
        }
    }
}