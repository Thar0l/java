package udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Sender {
	private String host;
	private int port;
	
	Sender(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void sendmsg(String msg) {
		try {
			byte[] data = msg.getBytes();
			InetAddress addr = InetAddress.getByName(host);
			DatagramPacket pack = new DatagramPacket(data, data.length, addr, port);
			DatagramSocket sock = new DatagramSocket();
			sock.send(pack);
			sock.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}
	
	public static void main(String[] args) {
		Sender s = new Sender(args[0], Integer.parseInt(args[1]));
		BufferedReader in=new BufferedReader(new InputStreamReader(System.in)); 
		
		while (true) {
			String msg = new String();
			try {
				msg = in.readLine();
				s.sendmsg(msg);
			} catch (IOException e) {
				System.err.println(e);
			}
			
		}
	}
}
