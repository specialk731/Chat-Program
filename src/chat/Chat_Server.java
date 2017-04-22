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
		Q = newQ;
		Recovery();
	}
	
	public void run(){
		System.out.println("Running Server.");
		System.out.println("Address: " + myAddress);
		System.out.println("Port: " + myPort);
		
		try{
			serversocket = new ServerSocket(Integer.parseInt(myPort));
			
			while(true){
				Socket s = serversocket.accept();
				tmp = new Server_Thread(s, this);
				threads.add(tmp);
				tmp.start();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void writeall(String message, Server_Thread st) {
		for(Server_Thread s : threads)
			if (s != st)
				try {
					s.writetoClients(message);
				} catch (IOException e) {
					e.printStackTrace();
				}
	}
	
	private void Recovery() {	//We are now starting a new Server because the old one failed...
		
	}
}

class Server_Thread extends Thread {
	String uname, ip, port, message = "";
	Socket socket = null;
	Boolean hostable = false;
	connection con;
	Chat_Server cs;
	ObjectInputStream ois;
	ObjectOutputStream oos;


	public Server_Thread(Socket s, Chat_Server chatServer) throws IOException {
		System.out.println("Creating new Server Thread");
		cs = chatServer;
		socket = s;
		System.out.println("New Server Thread Created : " + socket.getInetAddress());
	}
	
	public void run(){
		
		try{
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			System.out.println("Now Running Server Thread");
			
			ip = socket.getInetAddress().toString();
			port = String.valueOf(socket.getLocalPort());
			System.out.println("Starting to read");
			uname = ois.readUTF();
			System.out.println("Server_Thread got uname: " + uname);
			hostable = ois.readBoolean();
			con = new connection(ip,port,uname,hostable);
			cs.Q.add(con);
			cs.Q.peek().Display();
			
			oos.writeObject(cs.Q);
			oos.flush();
			System.out.println("End of Server Thread run() Setup");
			while(!message.equals("TERMINATE")) {
				message = ois.readUTF();
				System.out.println("Server got message: " + message);
				cs.writeall(message, this);					
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//HERE IS WHERE I AM!!!!!
	public void writetoClients(String m) throws IOException {
		oos.writeUTF(m);
		oos.flush();
	}

}

class connection  implements Comparable<connection>, Serializable{
	private static final long serialVersionUID = -7863289088864595768L;
	String ip, uname, port;
	Boolean hostable = false;
	
	public connection (String i, String p, String u, Boolean h) {
		ip = i;
		port = p;
		uname = u;
		hostable = h;
	}

	@Override
	public int compareTo(connection c) {
		if (c.hostable == true && this.hostable == false)
			return -1;
		if (c.hostable == false && this.hostable == true)
			return 1;
		else
			return 0;
	}
	
	public void Display(){
		System.out.println("Connection: ");
		System.out.println("IP: " + ip);
		System.out.println("Port: " + port);
		System.out.println("UserName: " + uname);			
	}
}