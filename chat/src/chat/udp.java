package chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;



public class udp {
	private String host;
	private Ports ports ;
	
	udp() {
		this.host = new String("127.0.0.1");
		this.ports = new Ports();
	}
	
	udp(String host, int iport, int oport)
	{
		this.host = host;
		this.ports = new Ports(iport, oport);
	}
	
	udp(String host, Ports ports)
	{
		this.host = host;
		this.ports = ports;
	}
	
	public void send(message msg) {
		try {
			byte[] to = msg.to.getBytes();
			byte[] data = msg.data.getBytes();
			byte[] from = msg.from.getBytes();
			InetAddress addr = InetAddress.getByName(host);
			DatagramPacket pto = new DatagramPacket(to, to.length, addr, ports.output);
			DatagramPacket pdata = new DatagramPacket(data, data.length, addr, ports.output);
			DatagramPacket pfrom = new DatagramPacket(from, from.length, addr, ports.output);
			DatagramSocket sock = new DatagramSocket();
			sock.send(pto);
			sock.send(pfrom);
			sock.send(pdata);
			sock.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public message receive() {
		message msg = new message();
		
		try {			
			DatagramSocket sock = new DatagramSocket(ports.input);
			DatagramPacket pto = new DatagramPacket(new byte[1024], 1024);
			DatagramPacket pdata = new DatagramPacket(new byte[1024], 1024);
			DatagramPacket pfrom = new DatagramPacket(new byte[1024], 1024);
			sock.receive(pto);
			sock.receive(pfrom);
			sock.receive(pdata);
			if (pdata.getData().length > 0){
				msg = new message(new String(pfrom.getData()), new String(pto.getData()), new String(pdata.getData()));
			}
			sock.close();
		} catch (Exception e) {
			System.err.println(e);
		}
	return msg;	
	}
	
	public static void main(String[] args) {
		udp testudp;
		if (args[0].length() > 2){
			System.out.println("S");
			testudp = new udp("localhost", 1055, 1156);
			System.out.println(testudp.ports.output);
			BufferedReader in=new BufferedReader(new InputStreamReader(System.in)); 
			while (true) {
				String txt =new String();
				try {
					txt = in.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				testudp.send(new message("ALL", "N/A", txt));
				 /* try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					System.err.println(e);
				} */
			}
		} else {
			System.out.println("R");
			testudp = new udp("localhost", 1156, 1055);
			System.out.println(testudp.ports.input);
			while (true) {
				message msg = testudp.receive();
				System.out.println(msg.from+" "+msg.to+" "+msg.data);
			}
		}
	}
}
