package udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class msgThread extends Thread {
	private int type = 0;
	private int port1 = 1050;
	private int port2 = 1051;
	private String name;
	private String host;
	private Sender sender;
	private Receiver receiver;
	private BufferedReader in;
	
	public msgThread(String n, String h,int p1,int p2, int t) {
		host = h;
		port1 = p1;
		port2 = p2;
		type = t;
		
		name = n;
		host = new String("127.0.0.1");
		sender = new Sender(host, port1);
		receiver = new Receiver(port2);
		in=new BufferedReader(new InputStreamReader(System.in));
	}
	
	public void run(){
		String inmsg = new String();
		String outmsg = new String();
		while (true) try {	
			if (type == 0){
				outmsg = in.readLine();
				sender.sendmsg(name+" : "+outmsg);
			} else {
				inmsg = receiver.receivemsg();
				if (inmsg.length()>0) System.out.println(inmsg);
			}
			
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}

public class msg {
	static Sender sender;
	static Receiver receiver;
	public static void main(String[] args) {
		new msgThread(args[3],args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), 0).start();
		new msgThread(args[3],args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), 1).start();
	}
}
