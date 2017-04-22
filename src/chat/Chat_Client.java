package chat;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Chat_Client extends Thread{
	Socket client;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	private static PriorityBlockingQueue<connection> Q = new PriorityBlockingQueue<connection>();
	String message, UName;
	static String Address, Port;
	Boolean Hostable;

	
	public Chat_Client(String ServerIP, String ServerPort, String UserName, Boolean Host) throws Exception{
			System.out.println("New Chat Client");
			client = new Socket(ServerIP, Integer.parseInt(ServerPort));
			Address = client.getInetAddress().getHostAddress();
			Port = String.valueOf(client.getPort());
			UName = UserName;
			Hostable = Host;
			System.out.println("End of new chat client");
	}
	
	@SuppressWarnings("unchecked")
	public void run(){
		System.out.println("Start of chat client run()");
		try {
			oos = new ObjectOutputStream(client.getOutputStream());
			ois = new ObjectInputStream(client.getInputStream());

			oos.writeUTF(UName);
			oos.writeBoolean(Hostable);
			oos.flush();
			
			Q = (PriorityBlockingQueue<connection>) ois.readObject();
			
			Q.peek().Display();
			
			System.out.println("Client " + UName + " listening for messages");
			message = ois.readUTF();
			
			while(message.compareTo("TERMINATE") != 0){
				System.out.println("Got message: " + message);
				Chat.WriteToChatBox(message);
				message = ois.readUTF();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void send(String message){
		try {
			oos.writeUTF(UName + ": " + message);
			oos.flush();
			Chat.WriteToChatBox(UName + ": " + message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String GetHostAddress(){
		return Address;
	}
	
	public static String GetHostPort(){
		return Port;
	}
}