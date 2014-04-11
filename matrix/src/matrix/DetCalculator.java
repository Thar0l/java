package matrix;


public class DetCalculator extends Thread{
	double result;
	matrix matr;
	int start;
	int end;
	
	public DetCalculator(matrix m, int start, int end) {
		this.result = 0;
		this.matr = m;
		this.start = start;
		this.end = end;
	}
	
	public DetCalculator() {
		result = 0;
	}
	
	public void run() {
		long startTime = System.currentTimeMillis();
		for (int i=start; i <= end; i++) {
			matrix minor = this.matr.getminor(0, i);
			double tmp = minor.getdeterminant()*((i%2 == 0)?-1.0:1.0);
			this.result +=tmp;
		}
		long timeSpent = System.currentTimeMillis() - startTime;
		System.out.println("Thread ["+start+"-"+end+"] works for"+timeSpent+"ms");
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		long startTime = System.currentTimeMillis();
		matrix M = new matrix(12);
		double result = 0;
		double arr2[] = {
				1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1 ,
				1,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2 ,
				1,  2,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3 ,
				1,  2,  3,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4,  4 ,
				1,  2,  3,  4,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5 ,
				1,  2,  3,  4,  5,  6,  6,  6,  6,  6,  6,  6,  6,  6,  6 ,
				1,  2,  3,  4,  5,  6,  7,  7,  7,  7,  7,  7,  7,  7,  7 ,
				1,  2,  3,  4,  5,  6,  7,  8,  8,  8,  8,  8,  8,  8,  9 ,
				1,  2,  3,  4,  5,  6,  7,  8,  9,  9,  9,  9,  9,  9,  9 ,
				1,  2,  3,  4,  5,  6,  7,  8,  9,  10, 10, 10, 10, 10, 10,
				1,  2,  3,  4,  5,  6,  7,  8,  9,  10, 11, 11, 11, 11, 11,
				1,  2,  3,  4,  5,  6,  7,  8,  9,  10, 11, 12, 12, 12, 12,
				1,  2,  3,  4,  5,  6,  7,  8,  9,  10, 11, 12, 13, 13, 13,
				1,  2,  3,  4,  5,  6,  7,  8,  9,  10, 11, 12, 13, 14, 14,
				1,  2,  3,  4,  5,  6,  7,  8,  9,  10, 11, 12, 13, 14, 15,
		};
		for (int i = 0; i < M.size; i++) 
			for (int j = 0; j < M.size; j++) 
				M.setitem(i, j, arr2[i*15+j]);
		
		int count = 1; 
		if (args.length > 0) {
			count=Integer.parseInt(args[0]); 
		}
		DetCalculator []threads = new DetCalculator[count];
		for (int i = 0; i < count; i++) {
			int start = i*(M.size/count);
			int end = (i+1)*M.size/count-1;
			threads[i] = new DetCalculator(M,start,end);
			System.out.println(i+": "+start+"-"+end);
		}
		for (DetCalculator thread: threads) {
			thread.start();
		}
		for (DetCalculator thread: threads) {
			thread.join();
			result += thread.result;
		}

		System.out.println(result);
		long timeSpent = System.currentTimeMillis() - startTime;
		System.out.println("Time spent: "+timeSpent+"ms");
	}
}
