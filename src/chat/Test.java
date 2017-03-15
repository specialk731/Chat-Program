package chat;

import java.net.*;

public class Test {

	public static void main(String[] args) {
		try {
			InetAddress IP=InetAddress.getLocalHost();
			System.out.println("IP of my system is := "+IP.getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}
