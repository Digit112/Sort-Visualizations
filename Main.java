import java.util.Random;
import java.io.IOException;

public class Main {
	public static void main(String args[]) throws IOException {
		final int NUM_VALS = 500;
		
		Random rand = new Random();
		
		LoggedArray<Integer> list = new LoggedArray<Integer>(NUM_VALS, "log.txt");
		list.setTitle("Populating list...");
		for (int i = 0; i < NUM_VALS; i++) {
			list.add(rand.nextInt(1000));
		}
		
		System.out.println("Unsorted:");
		System.out.println(list);
		
		// CHANGE ME to desired sorting algorithm
		Sorter.mergeSort(list);
		
		list.close();
		
		System.out.println("Sorted:");
		System.out.println(list);
		
		//Sorter.validate(100000, 50);
	}
}