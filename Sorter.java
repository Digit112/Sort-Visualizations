import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;

public class Sorter {
	// private static void printArr(int[] arr) {
		// for (int val : arr) {
			// System.out.print(val + " ");
		// }
		// System.out.println();
	// }
	
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
				if (sorted[j] > sorted[j+1]) {
					System.out.printf("Error with seed %d, not sorted.\n", seed);
					System.out.println(unsorted);
					System.out.println(sorted);
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
					System.out.println(unsorted);
					System.out.println(sorted);
					return;
				}
			}
		}
		
		System.out.println("Testing complete. No errors found.");
	}
	
	// Bubble Sort.
	// Repeatedly iterate over the list and swap out of order elements.
	public static void bubbleSort(LoggedArray list) throws IOException {
		System.out.println("Using bubble sort...");
		list.setTitle("Bubble Sort");
		list.resetTime();
		
		boolean requiresSwaps = true;
		while (requiresSwaps) {
			requiresSwaps = false;
			for (int i = 0; i < list.size()-1; i++) {
				if (list.get(i) > list.get(i+1)) {
					list.swap(i, i+1);
					requiresSwaps = true;
				}
			}
		}
	}
	
	// Cocktail Shaker Sort.
	// Bubble sort, but direction of itration alternates.
	public static void cocktailShakerSort(LoggedArray list) throws IOException {
		System.out.println("Using cocktail shaker sort...");
		list.setTitle("Cocktail Shaker Sort");
		list.resetTime();
		
		boolean requiresSwaps = true;
		while (requiresSwaps) {
			requiresSwaps = false;
			for (int i = 0; i < list.size()-1; i++) {
				if (list.get(i) > list.get(i+1)) {
					list.swap(i, i+1);
					requiresSwaps = true;
				}
			}
			
			if (!requiresSwaps) {
				break;
			}
			
			requiresSwaps = false;
			for (int i = list.size()-1; i > 0; i--) {
				if (list.get(i) < list.get(i-1)) {
					list.swap(i, i-1);
					requiresSwaps = true;
				}
			}
		}
	}
	
	// Selection Sort.
	// Repeatedly find the smallest of the unsorted elements and swap it with the left-most unsorted element, then mark it as sorted.
	public static void selectionSort(LoggedArray list) throws IOException {
		System.out.println("Using selection sort...");
		list.setTitle("Selection Sort");
		list.resetTime();
		
		for (int i = 0; i < list.size(); i++) {
			list.highlight(i, 0);
			
			// Find minimum value among unsorted entries.
			int minIndex = i;
			int minVal = list.get(minIndex);
			for (int j = i+1; j < list.size(); j++) {
				int val = list.get(j);
				if (val < minVal) {
					if (minIndex != i) {
						list.unhighlight(minIndex);
					}
					
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
	public static void insertionSort(LoggedArray list) throws IOException {
		System.out.println("Using insertion sort...");
		list.setTitle("Insertion Sort");
		list.resetTime();
		
		for (int i = 1; i < list.size(); i++) {
			int movVal = list.get(i);
			for (int j = i-1; j >= 0; j--) {
				// If unsorted value in question is smaller, move this element right once.
				if (movVal < list.get(j)) {
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
	public static void quickSort(LoggedArray list) throws IOException {
		System.out.println("Using quick sort...");
		list.setTitle("Quick Sort");
		list.resetTime();
		
		quickSortRecurse(list, 0, list.size() - 1);
	}
	
	// Quick Sort a portion of a list.
	private static void quickSortRecurse(LoggedArray list, int i, int k) throws IOException {
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
	private static int partition(LoggedArray list, int l, int h) throws IOException {
		int pivot = list.get( l + (h - l) / 2 );
		// System.out.println(pivot);
		
		while (true) {
			// Move the low cursor and high cursor until they are both pointing to elements above and below the pivot, respectively.
			while (list.get(l) < pivot) {
				l++;
			}
			
			while (list.get(h) > pivot) {
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
	// Recursively subdivide the heap, sort the subdivisions, and merge them.
	public static void mergeSort(LoggedArray list) throws IOException {
		System.out.println("Using merge sort...");
		list.setTitle("Merge Sort");
		
		LoggedArray temp = new LoggedArray(list.size(), 0, "sublog.txt");
		temp.setTitle("Merge Sort - Temporary Storage");
		
		list.resetTime();
		temp.resetTime();
		
		mergeSortRecurse(temp, list, 0, list.size() - 1);
		
		temp.close();
	}
	
	// Merge sort a portion of a list.
	public static void mergeSortRecurse(LoggedArray temp, LoggedArray list, int l, int h) throws IOException {
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
				int i_val = list.get(i);
				int j_val = list.get(j);
				
				if (i_val < j_val) {
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
	
	// Heap sort
	// Construct a binary search tree centering the largest element.
	// Repeatedly remove the root and replace it until no unsorted elements remain.
	public static void heapSort(LoggedArray list) throws IOException {
		System.out.println("Using heap sort...");
		list.setTitle("Heap Sort - Construction");
		list.resetTime();
		
		heapify(list, 0, list.size());
		
		list.setTitle("Heap Sort - Extraction");
		
		for (int i = list.size()-1; i > 0; i--) {
			list.swap(0, i);
			siftDown(list, 0, i);
		}
			
	}
	
	// Calls heapify on both children, then calls siftdown on self.
	// Repairs the heap without assumption.
	private static void heapify(LoggedArray list, int root, int heapSize) throws IOException {
		int lchild = list.binHeapChild(root, heapSize, true);
		int rchild = list.binHeapChild(root, heapSize, false);
		
		if (lchild != -1) {
			heapify(list, lchild, heapSize);
		}
		
		if (rchild != -1) {
			heapify(list, rchild, heapSize);
		}
		
		siftDown(list, root, heapSize);
	}
	
	// Repairs heap, assuming both heaps rooted at children are valid.
	private static void siftDown(LoggedArray list, int root, int heapSize) throws IOException {
		int lchild = list.binHeapChild(root, heapSize, true);
		int rchild = list.binHeapChild(root, heapSize, false);
		
		if (lchild == -1) {
			if (rchild == -1) {
				return;
			}
			else {
				if (list.get(root) < list.get(rchild)) {
					list.swap(root, rchild);
					siftDown(list, rchild, heapSize);
				}
			}
		}
		else {
			if (rchild == -1) {
				if (list.get(root) < list.get(lchild)) {
					list.swap(root, lchild);
					siftDown(list, lchild, heapSize);
				}
			}
			else {
				int lchildVal = list.get(lchild);
				int rchildVal = list.get(rchild);
				int maxChild = lchild;
				if (rchildVal > lchildVal) {
					maxChild = rchild;
				}
				
				if (list.get(root) < list.get(maxChild)) {
					list.swap(root, maxChild);
					siftDown(list, maxChild, heapSize);
				}
			}
		}
	}
	
	// Tournament sort
	// Compare all even elements with their neighbors, the "winner of each round" goes on the right.
	// Compare all the winners of that round, and so on, to find the maximum.
	// Swap it with the rightmost value and repeat a total of list.size()-1 times.
	// This is a variant of heapSort.
	public static void tournamentSort(LoggedArray list) throws IOException {
		System.out.println("Using tournament sort...");
		list.setTitle("Tournament Sort (Heap sort)");
		
		// Get max round size by rounding up to the nearest exponent of 2.
		int maxRoundSize = 1;
		while (maxRoundSize < list.size()) {
			maxRoundSize <<= 1;
		}
		int maxRoundDiff = maxRoundSize / 2;
		
		// A temporary array sufficient to hold the results of all tournaments.
		LoggedArray temp = new LoggedArray(maxRoundSize*2 - 1, 0, "sublog.txt");
		temp.setTitle("Tournament Sort - Temp Storage");
		
		list.resetTime();
		temp.resetTime();
		
		// Compute tournament.
		for (int roundSize = 1; roundSize <= maxRoundSize; roundSize <<= 1) {
			int roundDiff = roundSize / 2;
			int start = maxRoundSize / roundSize - 1;
			
			int i;
			for (i = 0; i < list.size() - roundDiff; i += roundSize) {
				int a = list.get(i);
				int b = list.get(i + roundDiff);
				
				if (a > b) {
					temp.set(start + i/roundSize, a);
				}
				else {
					temp.set(start + i/roundSize, b);
					list.swap(i, i + roundDiff);
				}
			}
			// If some value didn't have a competitor to compare with, it moves on to the next round.
			if (i < list.size()) {
				temp.set(start + i/roundSize, list.get(i));
			}
		}
		
		for (int i = 0; i < list.size(); i++) {
			list.set(list.size()-1-i, temp.get(0));
			temp.set(0, findNewChampion(list, temp, 0, maxRoundSize, maxRoundSize));
		}
		
		temp.close();
	}
	
	private static int findNewChampion(LoggedArray list, LoggedArray temp, int i, int roundSize, int maxRoundSize) throws IOException {
		// Keep list busy for synchronization
		list.get(0); list.get(0); list.get(0); list.get(0);
		
		int oldChampion = temp.get(i);
		
		int currStart = maxRoundSize / roundSize - 1;
		int nextStart = currStart*2 + 1;
		
		int contendersInd = (i - currStart)*2 + nextStart;
		
		int a = temp.get(contendersInd);
		int b = temp.get(contendersInd + 1);
		
		if (a > b) {
			if (roundSize >= 4) {
				temp.set(contendersInd, findNewChampion(list, temp, contendersInd, roundSize/2, maxRoundSize));
			}
			else {
				temp.set(contendersInd, 0);
			}
		}
		else {
			if (roundSize >= 4) {
				temp.set(contendersInd+1, findNewChampion(list, temp, contendersInd+1, roundSize/2, maxRoundSize));
			}
			else {
				temp.set(contendersInd+1, 0);
			}
		}
		
		return Math.max(temp.get(contendersInd), temp.get(contendersInd + 1));
	}
	
	// Bogo Sort
	// Randomize the list and check whether it is sorted. Uses Fischer-Yates shuffle.
	// Retries shuffle up to 10 million times.
	public static void bogoSort(LoggedArray list) throws IOException {
		System.out.println("Using bogo sort...");
		list.setTitle("Bogo Sort");
		list.resetTime();
		
		Random rand = new Random();
		
		boolean isShuffled = false;
		int attempts = 0;
		while (!isShuffled && attempts < 10000000) {
			// Shuffle
			for (int i = 0; i < list.size()-1; i++) {
				int j = rand.nextInt(list.size() - i) + i;
				
				if (i != j) {
					list.swap(i, j);
				}
			}
			
			// Check
			isShuffled = true;
			int prev = list.get(0);
			for (int i = 1; i < list.size(); i++) {
				int curr = list.get(i);
				if (curr < prev) {
					isShuffled = false;
					break;
				}
				prev = curr;
			}
			
			attempts++;
		}
	}
}