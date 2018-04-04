package serwer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class SessionThread implements Runnable {

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	SessionThread(Socket socket)
	{
		Thread t = new Thread(this);
		t.start();
	}
	
	
	@Override
	public void run() 
	{
		String incom;
		try
		{
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			String login;
			Operations operations=new Operations();
			boolean loged=false;
			while (true)
			{
			incom=(String)in.readObject();
			String[] params = incom.split("|");
			switch(params[0])
			{
			case "FIL":
				
				break;
			case "FIE":
				
				break;
			case "LOG":
				if(operations.login(params[1], params[2]))
				{
					login=params[1];
					loged=true;
					out.writeObject("ACK");
				}
				else out.writeObject("REJ");
				
				break;
			}
			}
		}
		catch(Exception e)
		{
			
		}

		
	}

}
