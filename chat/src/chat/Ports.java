package chat;

public class Ports {
	public int input;
	public int output;
	
	Ports() {
		this.input = 1050;
		this.output = 1051;
	}
	
	Ports( int input, int output) {
		this.input = input;
		this.output = output;
	}
}