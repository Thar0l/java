package array;


import java.util.Random;


public class test {
	public static void main(String[] args){
		array a = new array(10);
		Random r = new Random();
		r.setSeed(System.nanoTime());
		
		for (int i = 0; i < a.getSize(); i++){
			a.setItem(0, Math.abs(r.nextInt())%100);
		}
		
		a.iterator.begin();
		while (!a.iterator.isEnd()){
			System.out.println(a.iterator.current.index()+" : "+a.iterator.current.value());
			a.iterator.next();
		}
		
		a.resize(5);
		System.out.println();
		
		a.iterator.begin();
		while (!a.iterator.isEnd()){
			System.out.println(a.iterator.current.index()+" : "+a.iterator.current.value());
			a.iterator.next();
		}
		

		
	}
}


