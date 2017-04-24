package chat;

public class Chat {
	
	static Chat_Window ChatWindow;

	//Entry point for the program
	public static void main(String[] args) {
		
		//Start the Swing window
		ChatWindow = new Chat_Window();
		
		//Display a welcome message
		ChatWindow.writetochatBox("Welcome!");
	}
	
	/*public static void WriteToServer(String s){
		ChatWindow.writetochatBox(s);
		//System.out.println("Server got: " + s);
	}*/
	
	//Write a message to the chat box
	public static void WriteToChatBox(String s){
		ChatWindow.writetochatBox(s);
	}
}
