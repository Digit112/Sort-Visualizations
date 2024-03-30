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
		int iters = 0;
		while (requiresSwaps) {
			requiresSwaps = false;
			for (int i = iters; i < list.size()-1-iters; i++) {
				if (list.get(i) > list.get(i+1)) {
					list.swap(i, i+1);
					requiresSwaps = true;
				}
			}
			
			if (!requiresSwaps) {
				break;
			}
			
			requiresSwaps = false;
			for (int i = list.size()-1-iters; i > iters; i--) {
				if (list.get(i) < list.get(i-1)) {
					list.swap(i, i-1);
					requiresSwaps = true;
				}
			}
			
			iters++;
		}
	}
	
	// Shell sort.
	// Treat the array as h interleaved arrays and run insertion sort on each of them.
	// Repeat the process, modifying h each time, until h is 0, at which point we're done.
	// In our case, h is set to list.size() * 3 / 5 and multiplied by 3/5 each round until it drops below 50.
	// At that point, the sequence becomes 23, 10, 4, 1
	// This represents an acceleration of Insertion Sort because pieces tend to move very quickly towards their final destination.
	public static void shellSort(LoggedArray list) throws IOException {
		System.out.println("Using shell sort...");
		list.resetTime();
		
		int h = list.size() * 3 / 5;
		
		int[] predetermined = {23, 10, 4, 1, 0};
		int predeterminedInd = -1;
		
		if (h < 50) {
			predeterminedInd = 0;
			h = predetermined[0];
		}
		
		list.setTitle("Shell Sort (" + h + ")");
		
		// Repeatedly implement insertionSort.
		while (h > 0) {
			for (int i = h; i < list.size(); i++) {
				int movVal = list.get(i);
				for (int j = i - h; j >= 0; j -= h) {
					int currentComp = list.get(j);
					
					if (movVal < currentComp) {
						list.set(j + h, currentComp);
						if (j - h < 0) {
							list.set(j, movVal);
						}
					}
					else {
						list.set(j + h, movVal);
						break;
					}
				}
			}
			
			if (predeterminedInd == -1) {
				h = h * 3 / 5;
				if (h < 50) {
					predeterminedInd = 0;
				}
			}
			
			if (predeterminedInd != -1) {
				h = predetermined[predeterminedInd];
				predeterminedInd++;
			}
			list.setTitle("Shell Sort (" + h + ")");
		}
	}
	
	// Comb sort.
	// Repeatedly performs one bubble sort pass with decreasing values.
	public static void combSort(LoggedArray list) throws IOException {
		System.out.println("Using comb sort...");
		list.setTitle("Comb Sort");
		
		final float K_DIV = (float) 1.3;
		float kFloat = list.size();
		int k = 1;
		
		boolean moreSwaps = true;
		while (moreSwaps || k > 1) {
			kFloat /= K_DIV;
			k = (int) kFloat;
			
			moreSwaps = false;
			for (int i = 0; i + k < list.size(); i++) {
				if (list.get(i) > list.get(i+k)) {
					list.swap(i, i+k);
					moreSwaps = true;
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
		list.step(); list.step(); list.step(); list.step();
		
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
	
	// Bitonic sort.
	// Repeatedly create bitonic sequences and combine them.
	// Ultimately results in a sorted list.
	public static void bitonicSort(LoggedArray list) throws IOException {
		System.out.println("Using bitonic sort...");
		list.setTitle("Bitonic Sort");
		
		// The largest power of two less than the size of the list.
		int bitonicDiff = 1;
		while (bitonicDiff < list.size()) {
			bitonicDiff <<= 1;
		}
		bitonicDiff >>= 1;
		
		bitonicSortRecurse(list, 0, bitonicDiff, true);
	}
	
	private static void bitonicSortRecurse(LoggedArray list, int start, int bitonicDiff, boolean doSortForward) throws IOException {
		if (bitonicDiff == 0) {
			return;
		}
		//System.out.println("Sort " + start + " to " + (start + bitonicDiff*2 - 1));
		
		bitonicSortRecurse(list, start, bitonicDiff/2, true);
		bitonicSortRecurse(list, start + bitonicDiff, bitonicDiff/2, false);
		
		bitonicBoxRecurse(list, start, bitonicDiff, doSortForward);
	}
	
	private static void bitonicBoxRecurse(LoggedArray list, int start, int bitonicDiff, boolean doSortForward) throws IOException {
		if (bitonicDiff == 0) {
			return;
		}
		//System.out.println("Box " + start + " to " + (start + bitonicDiff*2 - 1));
		
		for (int i = start; i < start + bitonicDiff; i++) {
			int j = i + bitonicDiff;
			
			if (j >= list.size()) {
				// TODO: This causes the sort to fail unless the array size is exactly a power of two.
				// We must increase the available storage space and initialize all the extra space to maximum values.
				continue;
			}
			
			int a = list.get(i);
			int b = list.get(j);
			if (doSortForward) {
				if (a > b) {
					list.swap(i, j);
				}
			}
			else {
				if (b > a) {
					list.swap(i, j);
				}
			}
		}
		
		bitonicBoxRecurse(list, start, bitonicDiff/2, doSortForward);
		bitonicBoxRecurse(list, start + bitonicDiff, bitonicDiff/2, doSortForward);
	}
	
	// Smooth Sort
	// Good luck figuring this one out lmao here's a link: https://www.keithschwarz.com/smoothsort/
	// This one's pretty rough.
	public static void smoothSort(LoggedArray list) throws IOException {
		System.out.println("Using smooth sort...");
		list.setTitle("Smooth Sort - Construction");
		
		ArrayList<Integer> sizes = leonardifyForest(list);
		
		list.setTitle("Smooth Sort - Extraction");
		
		deleonardifyForest(list, sizes);
	}
	
	private static int getLeonardoNumber(int index) {
		if (index < 2) {
			return 1;
		}
		
		int a = 1;
		int b = 1;
		for (int i = 1; i < index; i++) {
			int c = a + b + 1;
			a = b;
			b = c;
		}
		return b;
	}
	
	// Converts an array to a series of leonardo trees with strictly decreasing size.
	// Each is a special kind of max-heap.
	// They are sorted from left to right in order of increasing root size.
	private static ArrayList<Integer> leonardifyForest(LoggedArray list) throws IOException {
		ArrayList<Integer> sizes = new ArrayList<Integer>();
		
		// Repeatedly insert elements into an initially empty Leonardo forest.
		for (int i = 0; i < list.size(); i++) {
			// If the forest is empty, start with a tree of size = 1
			if (sizes.size() == 0) {
				sizes.add(1);
			}
			// If the forest has one element, we either add 0 to follow a tree of size 1 or we add a tree of size 1.
			else if (sizes.size() == 1) {
				if (sizes.get(0) == 1) {
					sizes.add(0);
				}
				else {
					sizes.add(1);
				}
			}
			else {
				// If the right-most two trees are consecutive, merge them.
				// The inserted element becomes their new root.
				int leftSize = sizes.get(sizes.size() - 2);
				int rightSize = sizes.get(sizes.size() - 1);
				if (leftSize - rightSize == 1) {
					sizes.set(sizes.size() - 2, leftSize + 1);
					sizes.remove(sizes.size() - 1);
				}
				// Otherwise, add a new size-1 tree or a size-0 tree if the previous tree is size 1.
				else {
					if (rightSize == 1) {
						sizes.add(0);
					}
					else {
						sizes.add(1);
					}
				}
			}
			
			leonardoInsertionSortRoots(list, sizes, i, sizes.size() - 1);
		}
		
		return sizes;
	}
	
	// Repeatedly extract the top element from the leonardo tree forest and repair the result.
	private static void deleonardifyForest(LoggedArray list, ArrayList<Integer> sizes) throws IOException {
		for (int i = list.size() - 1; i >= 0; i--) {
			int currTreeLSize = sizes.get(sizes.size()-1);
			int currTreeSize = getLeonardoNumber(currTreeLSize);
			
			// If the current tree size is 1, we can completely remove it without impacting the tree.
			// Furthermore, because it is the max element (by virtue of being on the right) and the right-most element (by virtue of being the max element)
			// It is already in the correct location. Update sizes and skip to the next element.
			if (currTreeSize == 1) {
				sizes.remove( sizes.size()-1 );
				continue;
			}
			
			// Otherwise, get the children and re-sort the roots to repair the tree.
			int rChildSize = getLeonardoNumber(currTreeLSize - 2);
			int lChildSize = getLeonardoNumber(currTreeLSize - 1);
			
			int lChildInd = i - rChildSize - 1;
			int rChildInd = i - 1;
			
			// Update the sizes array.
			sizes.set(sizes.size()-1, currTreeLSize - 1);
			sizes.add(currTreeLSize - 2);
			
			leonardoInsertionSortRoots(list, sizes, lChildInd, sizes.size()-2);
			leonardoInsertionSortRoots(list, sizes, rChildInd, sizes.size()-1);
		}
	}
	
	// Perform modified insertion sort to ensure the roots are ordered in ascending order.
	// Everything except the right-most root will have been put in order by previous insertion sorts.
	// Do not perform a swap which breaks the current tree. Instead, just sift-down the current tree.
	private static void leonardoInsertionSortRoots(LoggedArray list, ArrayList<Integer> sizes, int currRootInd, int treeIndex) throws IOException {
		// Each insertion adds 1 element, thus the forest holds i+1 items.
		// The root node of the right-most tree is all he way to the right.
		while (treeIndex > 0) {
			// Note that the values in sizes index the Leonardo sequence.
			// They DO NOT give the actual sizes of the trees involved.
			int currTreeSize = getLeonardoNumber(sizes.get(treeIndex));
			int prevRootInd = currRootInd - currTreeSize;
			
			// If the previous tree does not exist, then the root has moved as far left as it ever will.
			// The loop should have exited at this point, because treeIndex is supposed to have been set to 0 by the previous loop.
			if (prevRootInd < 0) {
				assert prevRootInd == -1 : "While inserting value, earliest tree appears to go beyond the array start.";
				assert false : "Reached last tree, but the loop should have exited beecause this means treeIndex should be 0.";
				break;
			}
			
			// If this tree has no children, we swap the root node (the only node)
			// with the root of the previous tree if and only if that node is greater than this node.
			if (currTreeSize == 1) {
				if (list.get(prevRootInd) > list.get(currRootInd)) {
					list.swap(prevRootInd, currRootInd);
				}
				else {
					// If we can't swap, we're done. We go on to heapify this tree.
					break;
				}
			}
			// If this tree has children, we must not swap this root with the previous root if it will break this tree.
			// If it will, we can ensure that the roots are in the correct order by heapifying this tree.
			else {
				int rightChildSize = getLeonardoNumber(sizes.get(treeIndex)-2);
				
				int lChildInd = currRootInd - rightChildSize - 1;
				int rChildInd = currRootInd - 1;
				
				int prevRootVal = list.get(prevRootInd);
				if (
					prevRootVal > list.get(currRootInd) &&
					prevRootVal > list.get(lChildInd) &&
					prevRootVal > list.get(rChildInd)
				) {
					list.swap(prevRootInd, currRootInd);
				}
				else {
					// If we can't swap, we're done. We go on to heapify this tree.
					break;
				}
			}
			
			// If we got here, we swapped. Now we look at the previou tree.
			currRootInd = prevRootInd;
			treeIndex--;
		}
		
		// Heapify the last tree that we moved the new root to.
		leonardoSiftDown(list, currRootInd, sizes.get(treeIndex));
	}
	
	private static void leonardoSiftDown(LoggedArray list, int rootIndex, int treeLSize) throws IOException {
		// Note that the values in sizes index the Leonardo sequence.
		// They DO NOT give the actual sizes of the trees involved.
		int currTreeSize = getLeonardoNumber(treeLSize);
		
		// If the size of this tree is 1, it is already valid.
		// 2 is not a Leonardo number. Therefore, if the tree has more than 1 elements, it must have both children.
		if (currTreeSize == 1) {
			return;
		}
		
		int lChildSize = getLeonardoNumber(treeLSize-1);
		int rChildSize = getLeonardoNumber(treeLSize-2);
		
		int lChildInd = rootIndex - rChildSize - 1;
		int rChildInd = rootIndex - 1;
		
		int currVal = list.get(rootIndex);
		int leftVal = list.get(lChildInd);
		int rightVal = list.get(rChildInd);
		
		if (currVal > leftVal && currVal > rightVal) {
			return;
		}
		else if (leftVal > rightVal) {
			list.swap(lChildInd, rootIndex);
			leonardoSiftDown(list, lChildInd, treeLSize-1);
		}
		else {
			list.swap(rChildInd, rootIndex);
			leonardoSiftDown(list, rChildInd, treeLSize-2);
		}
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