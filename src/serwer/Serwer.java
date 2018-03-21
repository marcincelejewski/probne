package serwer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
public class Serwer implements Runnable{
	static final int SERVER_PORT=88;
	private String host;
	private ServerSocket server;
	Serwer()
	{
		Thread t = new Thread(this);
	}
	@Override
	public void run() 
	{
		Socket socket;
		try {
			host = InetAddress.getLocalHost().getHostName();
			server=new ServerSocket(SERVER_PORT);
			
			
		} catch (UnknownHostException e) 
		{
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true)
		{
			try {
				socket=server.accept();
				if (socket!=null)
				{
					
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
	

	

}
