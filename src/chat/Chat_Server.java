package chat;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import java.util.*;

public class Chat_Server extends Thread{
	String myAddress, myPort;
	String[] conns, ports;
	PriorityBlockingQueue<connection> Q = new PriorityBlockingQueue<connection>();
	List<Server_Thread> threads = new ArrayList<Server_Thread>();
	Server_Thread tmp;
	
	static ServerSocket serversocket;
	
	Chat_Server(String address, String port){
		myAddress = address;
		myPort = port;
	}
	
	Chat_Server(String address, String port, PriorityBlockingQueue<connection> newQ){
		myAddress = address;
		myPort = port;
		//Q = newQ;
		Recovery();
	}
	
	//Main section of code for Chat_Server
	public void run(){
		//System.out.println("Running Server.");
		//System.out.println("Address: " + myAddress);
		//System.out.println("Port: " + myPort);
		
		try{
			//Create a new ServerSocket to listen for connections
			serversocket = new ServerSocket(Integer.parseInt(myPort));
			
			//For each new connection create a new Server_Thread and start it (also add it to the list of threads
			while(true){
				Socket s = serversocket.accept();
				tmp = new Server_Thread(s, this);
				threads.add(tmp);
				tmp.start();
			}
		} catch (Exception e){
			//e.printStackTrace();
		}
	}
	
	//This is called to write a message to every Server_Thread except the st Thread
	public synchronized void writeall(String message, Server_Thread st) {
		for(Server_Thread s : threads)
			if (s != st)
				try {
					if(message.equals("q")){
						//System.out.println("writeall q");
						s.writetoClients("q", Q);
					}else
						s.writetoClients(message);
				} catch (Exception e) {
					//e.printStackTrace();
				}
	}

	
	private void Recovery() {	//We are now starting a new Server because the old one failed...
	}
}

//Created by Chat_Server to handle connections
class Server_Thread extends Thread {
	String uname, ip, port, message = "";
	Socket socket = null;
	Boolean hostable = false;
	connection con;
	Chat_Server cs;
	ObjectInputStream ois;
	ObjectOutputStream oos;


	public Server_Thread(Socket s, Chat_Server chatServer) throws IOException {
		//System.out.println("Creating new Server Thread");
		cs = chatServer;
		socket = s;
		//System.out.println("New Server Thread Created : " + socket.getInetAddress());
	}
	
	//Main code section for Server_Thread
	@SuppressWarnings("unchecked")
	public void run(){
		
		try{
			//Create I/O streams
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			//System.out.println("Now Running Server Thread");
			
			//Get information about the connection from the Sockets and from the Client thread
			ip = socket.getInetAddress().toString();
			port = String.valueOf(socket.getLocalPort());
			//System.out.println("Starting to read");
			uname = ois.readUTF();
			//System.out.println("Server_Thread got uname: " + uname);
			hostable = ois.readBoolean();
			
			//Create a new connection class with info about this connection and add is to the Queue
			con = new connection(ip,port,uname,hostable);
			cs.Q.add(con);
			
			//Send that Queue to the Client Thread
			oos.writeObject(cs.Q);
			oos.flush();
			
			//Send the new Queue to all the clients listening for it
			cs.writeall("q", this);
			//Tell all the clients that the user username has entered the chat
			cs.writeall("m" + uname + " entered the chat", this);

			//System.out.println("End of Server Thread run() Setup");
			while(true){
				//Read messages from the Clients
				message = ois.readUTF();
				//System.out.println("Server got message: " + message);
				
				switch(message.charAt(0)){
				//If it is a simple message pass it along to the other clients via the other Server Threads
				case'm':
					cs.writeall(message, this);
					break;
				//If it is a Queue update we expect the next thing to be a Queue object so capture it and update our Queue	
				case'q':
					cs.Q = (PriorityBlockingQueue<connection>) ois.readObject();
					cs.writeall(message, this);
					break;
				}
			}
			
		} catch (Exception e) {
			
		}
		//If this code is executed it means the connection was terminated by an Exception above
		//So remove the connection from the queue
		cs.Q.remove(con);
		//REmove this thread from the threads list
		cs.threads.remove(this);
		//Tell everyone that the Queue has changed
		cs.writeall("q", this);
		//Say goodbye
		cs.writeall("m" + con.uname + " left the chat.", this);
	}
	
	public void writetoClients(String m) throws Exception {
		oos.writeUTF(m);
		oos.flush();
	}
	
	public void writetoClients(String m, PriorityBlockingQueue<connection> q) throws Exception{
		oos.writeUTF(m);
		oos.writeObject(q);
		oos.flush();
	}

}

//connection class is just a collection of Strings and a Boolean. Used to maintain the Queue
class connection  implements Comparable<connection>, Serializable{
	private static final long serialVersionUID = -7863289088864595768L;
	String ip, uname, port, list;
	Boolean hostable = false;
	
	//Constructor
	public connection (String i, String p, String u, Boolean h) {
		ip = i;
		port = p;
		uname = u;
		hostable = h;
		list = uname + " " + ip + ":" + port;
		if(hostable)
			list = "*** " + list;
	}

	//Needed for Prioritization of the Queue
	@Override
	public int compareTo(connection c) {
		if (c.hostable == true && this.hostable == false)
			return 1;
		if (c.hostable == false && this.hostable == true)
			return -1;
		else
			return 0;
	}
	
	//Needed for Remove from Queue
	public boolean equals(Object o){
		if(o instanceof connection){
			if(((connection) o).list.equals(this.list))
				return true;
			else
				return false;
		}else
			return false;
			
	}
	
	//Testing purposes
	public void Display(){
		System.out.println("Connection: ");
		System.out.println("IP: " + ip);
		System.out.println("Port: " + port);
		System.out.println("UserName: " + uname);
		System.out.println(list);
	}
}