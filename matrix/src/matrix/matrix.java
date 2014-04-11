package matrix;

public class matrix {
	public int size;
	public double items[][];
	
	matrix(int size) {
		this.size = size;
		this.items = new double[size][size];
	}
	
	
	void setitem(int i, int j, double value) {
		this.items[i][j] = value;
	}
	
	matrix getminor(int i0, int j0) {
		matrix minor = new matrix(this.size - 1);
		
		int ii = 0;
		int jj = 0;
		for (int i = 0; i < this.size - 1; i++) {
			jj = 0;
			if (i == i0) ii++;
			for (int j = 0; j < this.size - 1; j++) {
				if (j == j0) jj++;
				minor.items[i][j] = this.items[ii][jj];
				jj++;
			}
			ii++;
		}
		return minor;
	}
	
	double getdeterminant() {
		if (this.size == 2) 
			return (this.items[0][0])*(this.items[1][1])-(this.items[0][1])*(this.items[1][0]);
		
		double result = 0.0;
		double k = 1.0;
		for (int i = 0; i < this.size; i++) {
			k = (i%2 == 0)?-1.0:1.0;
			matrix minor = this.getminor(0, i);
			result = result + k*(this.items[0][i])*minor.getdeterminant();
			
		}
		return result;
	}


	void print() {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) 
				System.out.print(this.items[i][j]+"\t");
			System.out.println();
		}
		System.out.println();
	}
}
