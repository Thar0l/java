package chat;

public class message {
	String to;
	String from;
	String data;
	
	message() {
		to = new String();
		from = new String();
		data = new String();
	}
	
	message(String from, String to, String data) {
		this();
		this.to = to;
		this.from = from;
		this. data = data;
	}
}
