package array;

public class array {
	private int[] items;
	private int size;
	
	public class _iterator{
		private int index;

		public class _current{
			
			public int value()
			{
				return items[index];
			}
			public int index()
			{
				return index;
			}
			
		} 
		_current current = new _current();
		
		public void begin()
		{
			index = 0;
		}
		
		public boolean isEnd()
		{
			return !(index < size);
		}
		
		public void next()
		{
			if (!isEnd()) index++;
		}
	}
	_iterator iterator = new _iterator(); 
	
	array() {
		size = 0;
		
	}
	
	array(int size){
		this.size = size;
		items = new int[size];
	}
	
	public int getItem(int index) {
		if ((0 <= index) && (index < size))
		return items[index];
		return 0;
	}

	public void setItem(int index, int value) {
		if ((0 <= index) && (index < size)){
			this.items[index] = value;
			sort();
		}
	}

	public int getSize() {
		return size;
	}
	
	public void resize(int newsize) {
		if (newsize < 0) return;
		int min = Math.min(size, newsize);
		int[] old = new  int[min];
		for (int i = 0; i < min; i++){
			old[i] = items[i];
		}
		items = new int[newsize];
		size = newsize;
		for (int i = 0; i < min; i++){
			items[i] = old[i];
		}
		sort();
	}
	

	private void sort() {
		if (size > 0) {
			for (int i = 0; i < size; i++)
				for (int j = i; j< size; j++) {
					if (items[i] > items[j]){
						int tmp = items[i];
						items[i] = items[j];
						items[j] = tmp;
					}
				}
		}
	}
	
	
}
