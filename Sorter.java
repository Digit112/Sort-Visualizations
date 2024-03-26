import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;

public class Sorter {
	private static <T> void printArr(T[] arr) {
		for (T val : arr) {
			System.out.print(val + " ");
		}
		System.out.println();
	}
	
	// Runs a hard-coded algorithm a certain number of times on randomly-generated arrays of arrLength integers in the range [0, 99].
	// Reports seed values with an error.
	public static void validate(int numTests, int arrLength) {
		Integer[] sorted = new Integer[arrLength];
		Integer[] unsorted = new Integer[arrLength];
		
		int[] histogram = new int[100];
		
		Random seedGen = new Random();
		Random valGen = new Random();
		
		for (int i = 0; i < numTests; i++) {
			int seed = seedGen.nextInt();
			valGen.setSeed(seed);
			
			for (int j = 0; j < 100; j++) {
				histogram[j] = 0;
			}
			
			for (int j = 0; j < arrLength; j++) {
				int randVal = valGen.nextInt(100);
				
				sorted[j] = randVal;
				unsorted[j] = randVal;
				
				histogram[randVal]++;
			}
			
			// mergeSort(sorted); // Change this to validate different methods.
			
			// System.out.println("------------");
			// printArr(unsorted);
			// printArr(sorted);
			
			// Check that the list is sorted.
			for (int j = 0; j < arrLength-1; j++) {
				if (sorted[j].compareTo(sorted[j+1]) > 0) {
					System.out.printf("Error with seed %d, not sorted.\n", seed);
					printArr(unsorted);
					printArr(sorted);
					return;
				}
			}
			
			// Check that the histogram matches.
			for (int j = 0; j < arrLength; j++) {
				histogram[sorted[j]]--;
			}
			for (int j = 0; j < 100; j++) {
				if (histogram[j] != 0) {
					System.out.printf("Error with seed %d, value %d appears too frequently or infrequently in the sorted array.\n", seed, j);
					printArr(unsorted);
					printArr(sorted);
					return;
				}
			}
		}
		
		System.out.println("Testing complete. No errors found.");
	}
	
	// Selection Sort.
	// Repeatedly find the smallest of the unsorted elements and swap it with the left-most unsorted element, then mark it as sorted.
	public static <T extends Comparable<T>> void selectionSort(LoggedArray<T> list) throws IOException {
		list.setTitle("Selection Sort");
		list.resetTime();
		
		for (int i = 0; i < list.size(); i++) {
			list.highlight(i, 0);
			
			// Find minimum value among unsorted entries.
			int minIndex = 0;
			T minVal;
			for (int j = i+1; j < list.size(); j++) {
				T val = list.get(j);
				if (val.compareTo(list.get(minIndex)) < 0) {
					list.unhighlight(minIndex);
					list.highlight(j, 1);
					
					minIndex = j;
					
					minVal = val;
				}
			}
			list.unhighlight(minIndex);
			list.unhighlight(i);
			
			// Swap with current value, if current is not the minimum.
			if (minIndex != i) {
				list.swap(i, minIndex);
			}
		}
	}
	
	// Insertion sort.
	// Take the left-most unsorted element and move it left as long as its left neighbor is larger than itself.
	// Instead of repeatedly swapping, this algorithm moves elements right repeatedly and only writes each unsorted element back to the list once.
	public static <T extends Comparable<T>> void insertionSort(LoggedArray<T> list) throws IOException {
		list.setTitle("Insertion Sort");
		list.resetTime();
		
		for (int i = 1; i < list.size(); i++) {
			T movVal = list.get(i);
			for (int j = i-1; j >= 0; j--) {
				// If unsorted value in question is smaller, move this element right once.
				if (movVal.compareTo(list.get(j)) < 0) {
					list.set(j+1, list.get(j));
					if (j == 0) {
						list.set(0, movVal);
					}
				}
				// If unsorted value in question is larger, write it to the now-empty space on the right.
				else {
					list.set(j+1, movVal);
					break;
				}
			}
		}
	}
	
	// Quick Sort.
	// Recursively partition the array into smaller arrays.
	// Select a pivot and move all elements to its right or left depending on the result of compareTo.
	// Then, recursively partition each new section.
	// Works particularly well with nearly sorted lists.
	public static <T extends Comparable<T>> void quickSort(LoggedArray<T> list) throws IOException {
		list.setTitle("Quick Sort");
		list.resetTime();
		
		quickSortRecurse(list, 0, list.size() - 1);
	}
	
	// Quick Sort a portion of a list.
	private static <T extends Comparable<T>> void quickSortRecurse(LoggedArray<T> list, int i, int k) throws IOException {
		// If the high and low value are equal, then the list is already sorted.
		if (k == i) {
			return;
		}
		
		// Partition the list into a low section and a high section.
		// The returned value can be seen as belonging to either section.
		int j = partition(list, i, k);
		
		// Recursively quicksort each section.
		quickSortRecurse(list, i, j);
		quickSortRecurse(list, j+1, k);
	}
	
	// Partition a portion of a list into two smaller portions.
	// Returns the new index of the pivot (or an equal value).
	// Everything to the left of the returned index will be <= everything to the right of it.
	private static <T extends Comparable<T>> int partition(LoggedArray<T> list, int l, int h) throws IOException {
		T pivot = list.get( l + (h - l) / 2 );
		// System.out.println(pivot);
		
		while (true) {
			// Move the low cursor and high cursor until they are both pointing to elements above and below the pivot, respectively.
			while (list.get(l).compareTo(pivot) < 0) {
				l++;
			}
			
			while (list.get(h).compareTo(pivot) > 0) {
				h--;
			}
			
			// If the cursors have passed each other or met up, then partitioning is complete.
			if (h <= l) {
				break;
			}
			
			// If not, then they are pointing to values in the wrong partitions. Swap them.
			list.swap(l, h);
			
			// The elements under the cursors must now be in the correct partitions. Advance the cursors.
			// This is necessary to avoid infinite recursion in the case that the pivot appears multiple times in the list.
			h--;
			l++;
		}
		
		return h;
	}
	
	// Merge sort.
	public static <T extends Comparable<T>> void mergeSort(LoggedArray<T> list) throws IOException {
		list.setTitle("Merge Sort");
		
		LoggedArray<T> temp = new LoggedArray(list.size(), 0, "sublog.txt");
		temp.setTitle("Merge Sort - Temporary Storage");
		
		list.resetTime();
		temp.resetTime();
		
		mergeSortRecurse(temp, list, 0, list.size() - 1);
		
		temp.close();
	}
	
	// Merge sort a portion of a list.
	public static <T extends Comparable<T>> void mergeSortRecurse(LoggedArray<T> temp, LoggedArray<T> list, int l, int h) throws IOException {
		if (l == h) {
			return;
		}
		
		int midpoint = l + (h-l) / 2; // This value is included in the lower list.
		
		mergeSortRecurse(temp, list, l, midpoint);
		mergeSortRecurse(temp, list, midpoint+1, h);
		
		// Now we know that the left and right halves of the list are sorted.
		// We only need to combine them.
		
		int i = l;
		int j = midpoint+1;
		int k = l;
		while (k <= h) {
			if (i <= midpoint && j <= h) {
				T i_val = list.get(i);
				T j_val = list.get(j);
				
				if (i_val.compareTo(j_val) < 0) {
					temp.set(k, i_val);
					i++;
				}
				else {
					temp.set(k, j_val);
					j++;
				}
			}
			else if (i <= midpoint) {
				temp.set(k, list.get(i));
				i++;
			}
			else if (j <= h) {
				temp.set(k, list.get(j));
				j++;
			}
			
			k++;
		}
		
		// Copy the merged list back to the actual list.
		for (int ind = l; ind <= h; ind++) {
			list.set(ind, temp.get(ind));
		}
	}
}