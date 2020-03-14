package ftp;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerMain
{
    public static boolean quit = false;

    private static boolean startClicked = false;

    private static JFrame frame = new JFrame("FTP Server");
    private static JPanel panel = new JPanel();

/*
    public static void changePort()
    {
        StartServer(ServerMain.numThreads, ServerMain.list, ServerMain.serverPort);
    }
*/

    static Thread mainThread = new Thread()
    {
    	@Override
    	public void run()
        {
        	int serverPort = 1212;
            AtomicInteger numThreads = new AtomicInteger(0);
            ArrayList<Thread> list   = new ArrayList<Thread>();
        	ServerSocket socket = null;
            
        	try
            {
                socket = new ServerSocket(serverPort);
                System.out.println("Server listening on port " + serverPort);
                
                while (!quit)
                {
                    Socket client = socket.accept();
                    Thread thrd = new Thread(new ServerCore(client));
                    list.add(thrd);
                    thrd.start();
                    numThreads.incrementAndGet();
                    System.out.println("Thread " + numThreads.get() + " started.");
                }
    		} 
    		catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            finally
            {
            	if(socket != null)
            	{
    				try
                    {
    					socket.close();
    				}
    				catch (IOException e)
                    {
    					e.printStackTrace();
    				}
            	}
            }
    	}
    };
    
	
	public static void main(String[] args)
    {
        frame.setVisible(true);
        frame.setSize(500,200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        JButton button = new JButton("START");
        panel.add(button);
        button.addActionListener(new Start());


        JButton button2 = new JButton("STOP");
        panel.add(button2);
        button2.addActionListener(new Stop());
    }

    private static class Start implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
        	if(!startClicked)
	            try
	            {
	                Thread.sleep(1000);
	                startClicked = true;
	                mainThread.start();
	            }
	            catch (InterruptedException ex)
	            {
	                ex.printStackTrace();
	            }
        }
    }

    private static class Stop implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (startClicked)
            {
                System.out.println("QUIT WAS CLICKED");
                quit = true;
                System.exit(0);
            }
        }
    }
}
