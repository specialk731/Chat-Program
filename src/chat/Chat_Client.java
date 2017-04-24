package chat;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import javax.swing.JOptionPane;

public class Chat_Client extends Thread{
	Socket client;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	private static PriorityBlockingQueue<connection> Q = new PriorityBlockingQueue<connection>();
	String message, UName;
	static String Address, Port;
	Boolean Hostable;

	//Constructor for Chat_Client
	public Chat_Client(String ServerIP, String ServerPort, String UserName, Boolean Host) throws Exception{
			//System.out.println("New Chat Client");
			client = new Socket(ServerIP, Integer.parseInt(ServerPort));
			Address = client.getInetAddress().getHostAddress();
			Port = String.valueOf(client.getPort());
			UName = UserName;
			Hostable = Host;
			//System.out.println("End of new chat client");
	}
	
	//The main code for the Chat Client Thread
	@SuppressWarnings("unchecked")
	public void run(){
		//System.out.println("Start of chat client run()");
		try {
			oos = new ObjectOutputStream(client.getOutputStream());
			ois = new ObjectInputStream(client.getInputStream());

			//Tell the server your username and whether you can host or not
			oos.writeUTF(UName);
			oos.writeBoolean(Hostable);
			oos.flush();
			
			//The Server will respond with the Priority Queue of connections for if you get disconnected
			Q = (PriorityBlockingQueue<connection>) ois.readObject();
			
			//System.out.println("Next host: " + Q.peek().list);
			
			//Put the contents of the Queue into the GUI window for the user to see
			for(connection c : Q){
				Chat_Window.AddToList(c.list);
			}
			
			//Respond to the Server with what you think the Queue is so that everything is synched up
			oos.writeUTF("q");
			oos.writeObject(Q);
			oos.flush();
			
			//Read a message from the Server
			message = ois.readUTF();
			
			while(true){
				//If the message starts with the letter m it is a message for the chatbox
				if(message.charAt(0) == 'm'){
					//Remove the first letter (it was only to tell us it was a message
					message = message.substring(1, message.length());
					//System.out.println("Got message: " + message);
					//Write the message to the chatbox
					Chat.WriteToChatBox(message);
				//If the message starts with the letter q it is a Queue update	
				}else if(message.charAt(0) == 'q'){
					//Change our Queue to be what the Server says it is
					Q = (PriorityBlockingQueue<connection>) ois.readObject();
					
					//Clear the old Queue info out of the GUI list
					Chat_Window.ResetList(Q);
					
					//System.out.println("Got new Q in chat client");
					
					//System.out.println("Next host: " + Q.peek().list);
					
					/*for(connection c : Q)
						System.out.println(c.list);*/
				}
				
				//Read another message from the Server
				message = ois.readUTF();
			}
		} catch (Exception e) {
		}
		
		//If the code gets here it means our Client connection to the server has been severed (and threw an Exception above)
		
		//First take the old host out from the top of the Queue
		Q.poll();
		
		//System.out.println(Q.peek().list);
		//System.out.println("*** " + UName + " " + Address + ":" + Port);
		
		try{
			//If the next connection in the Queue is this chat session...
			if(Q.peek().list.equals("*** " + UName + " /" + Address + ":" + Port)){
				//Start our own Chat_Server and start it
				Chat_Server cs = new Chat_Server(Address,Port,Q);
				cs.start();
				//Restart our Chat_Client to connect to the new Server and start it
				Chat_Client cc = new Chat_Client(Address, Port, UName, Hostable);
				cc.start();
				//Update all the info for the Chat_Window GUI and reset the Q
				Chat_Window.client = cc;
				Chat_Window.server = cs;
				Chat_Window.EmptyList();
				Chat_Window.SetHostable();
				Q = new PriorityBlockingQueue<connection>();
			//IF the next connection isnt this chat session but there is another Hostable available...	
			}else if(Q.peek().list.startsWith("***")){
				//Tell the user to wait a second
				Chat.WriteToChatBox("ATTEMPTING TO CONNECT TO NEW HOST!!!");
				//Wait 2 seconds for new Server to get set up (somewhere else)
				Thread.sleep(2000);
				//Create a new Chat_Client to connect to the new Server
				Chat_Client cc = new Chat_Client(Q.peek().ip.replace("/", ""), Q.peek().port, UName, Hostable);
				cc.start();
				//Update the Chat_Window info
				Chat_Window.client = cc;
				Q = new PriorityBlockingQueue<connection>();
				Chat_Window.EmptyList();
				Chat.WriteToChatBox("New Host Found.");
			//If the next connection isnt this chat session and there isnt another Hostable then we are done	
			}else{
				JOptionPane.showMessageDialog(null, "There are no more Hostable members in the chat...");
				System.exit(0);
			}
		}catch(Exception e){
			
		}
		
	}
	
	//Send a message
	public void send(String message){
		try {
			//Write the String m (for message) + the username and message to the Server
			oos.writeUTF("m" + UName + ": " + message);
			oos.flush();
			//Then put it on the screen
			Chat.WriteToChatBox(UName + ": " + message);
			
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public static String GetHostAddress(){
		return Address;
	}
	
	public static String GetHostPort(){
		return Port;
	}
}