import java.util.Random;
import java.io.IOException;

public class Main {
	enum PopulationType {
		Random,  // (Independently) randomly initialize each element.
		Sorted,  // Initialize each element proportional to its index.
		Shuffle, // Initialize each element proportional to its index, then shuffle it.
		Reverse, // Initialize each element proportional to its index, but reversed.
		Almost   // Almost sorted.
	}
	
	public static void main(String args[]) throws IOException {
		// params
		final int NUM_VALS = 1000;
		final int MAX_VAL = 1000;
		final PopulationType popType = PopulationType.Sorted;
		
		// Only applies when popType is "Almost".
		int maxSwapDis = NUM_VALS / 25;
		
		Random rand = new Random();
		
		System.out.println(String.format("Populating list with %d elements in the range [0, %d]...", NUM_VALS, MAX_VAL-1));
		
		LoggedArray list = new LoggedArray(NUM_VALS, "log.txt");
		list.setTitle("Populating list...");
		switch (popType) {
			case PopulationType.Random:
				for (int i = 0; i < NUM_VALS; i++) {
					list.add(rand.nextInt(MAX_VAL));
				}
				break;
			
			case PopulationType.Sorted:
				for (int i = 0; i < NUM_VALS; i++) {
					list.add((int) ((float) (i + 1) / NUM_VALS * MAX_VAL));
				}
				break;
			
			case PopulationType.Shuffle:
				for (int i = 0; i < NUM_VALS; i++) {
					list.add((int) ((float) (i + 1) / NUM_VALS * MAX_VAL));
				}
				list.setTitle("Shuffling list...");
				for (int i = 0; i < NUM_VALS-1; i++) {
					int randVal = rand.nextInt(NUM_VALS - i) + i;
					list.swap(i, randVal);
				}
				break;
			
			case PopulationType.Reverse:
				for (int i = 0; i < NUM_VALS; i++) {
					list.add((int) ((float) (NUM_VALS - i) / NUM_VALS * MAX_VAL));
				}
				break;
			
			case PopulationType.Almost:
				for (int i = 0; i < NUM_VALS; i++) {
					list.add((int) ((float) (i + 1) / NUM_VALS * MAX_VAL));
				}
				list.setTitle("Shuffling list...");
				for (int i = 0; i < NUM_VALS-1; i++) {
					int min = Math.max(0, i - maxSwapDis);
					int max = Math.min(NUM_VALS - 1, i + maxSwapDis);
					int randVal = rand.nextInt(max - min) + min;
					list.swap(i, randVal);
				}
				break;
		}
		
		System.out.println("Unsorted:");
		System.out.println(list);
		
		// CHANGE ME to desired sorting algorithm
		Sorter.heapSort(list);
		
		list.close();
		
		System.out.println("Sorted:");
		System.out.println(list);
		
		System.out.println(String.format("%d ops (%d meta, %d real)\n", list.totalOps + list.totalMetaOps, list.totalMetaOps, list.totalOps));
		
		//Sorter.validate(100000, 50);
	}
}