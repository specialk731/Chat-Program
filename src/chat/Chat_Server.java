package chat;

import java.net.*;

public class Chat_Server {
	String myAddress, myPort;
	String[] conns, ports;
	
	static ServerSocket serversocket;
	
	Chat_Server(String address, String port){
		myAddress = address;
		myPort = port;
	}
	
	Chat_Server(String address, String port, String[] Connections, String[] Ports){
		myAddress = address;
		myPort = port;
		conns = Connections;
		ports = Ports;
	}
	
	public void run(){
		System.out.println("Running Server.");
		System.out.println("Address: " + myAddress);
		System.out.println("Port: " + myPort);
	}
	

}
