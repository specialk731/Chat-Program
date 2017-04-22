package chat;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Chat {
	
	static Chat_Window ChatWindow;

	public static void main(String[] args) {
		
		ChatWindow = new Chat_Window();
		
		ChatWindow.writetochatBox("Welcome!");
	}
	
	public static void WriteToServer(String s){
		ChatWindow.writetochatBox(s);
		System.out.println("Server got: " + s);
	}
	
	public static void WriteToChatBox(String s){
		ChatWindow.writetochatBox(s);
	}
}
