import java.util.Random;
import java.io.IOException;

public class Main {
	public static void main(String args[]) throws IOException {
		// params
		final int NUM_VALS = 160;
		final int MAX_VAL = 1000;
		
		Random rand = new Random();
		
		System.out.println(String.format("Populating list with %d elements in the range [0, %d]...", NUM_VALS, MAX_VAL-1));
		
		LoggedArray<Integer> list = new LoggedArray<Integer>(NUM_VALS, "log.txt");
		list.setTitle("Populating list...");
		for (int i = 0; i < NUM_VALS; i++) {
			list.add(rand.nextInt(MAX_VAL));
		}
		
		System.out.println("Unsorted:");
		System.out.println(list);
		
		// CHANGE ME to desired sorting algorithm
		Sorter.selectionSort(list);
		
		list.close();
		
		System.out.println("Sorted:");
		System.out.println(list);
		
		System.out.println(String.format("%d ops (%d meta, %d real)\n", list.totalOps + list.totalMetaOps, list.totalMetaOps, list.totalOps));
		
		//Sorter.validate(100000, 50);
	}
}