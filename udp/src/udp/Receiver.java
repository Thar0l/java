package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver {
	private int port;
	
	public Receiver(int port) {
		this.port = port;
	}
	String receivemsg()
	{
		String msg = new String();
		try {
		
		DatagramSocket sock = new DatagramSocket(port);
		DatagramPacket pack = new DatagramPacket(new byte[1024], 1024);
		sock.receive(pack);
		if (pack.getData().length > 0)
		msg = new String(pack.getData());
		sock.close();
		} catch (Exception e) {
			System.err.println(e);
		}
		
		return msg;
		
	}
	
	public static void main(String[] args) {
		Receiver r = new Receiver(Integer.parseInt(args[0]));
		while (true) {
			String msg = r.receivemsg();
			if (msg.length()>0) System.out.println(msg);
		}
	}
}
