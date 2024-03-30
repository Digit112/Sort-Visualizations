// A priority queue implementation.
// Represents a series of strictly decreasing Leonardo Trees whose structure is mostly implied by an array.
// A Leonardo tree is a tree with a number of elements equal to a leonardo number.
// Leonardo numbers are those that appear in the sequence L(n) = L(n-1) + L(n-2) + 1, which is very similar to the Fibonacci sequence.
// L: 1, 1, 3, 5, 9, 15, 25...
// It can be shown that any natural number n can be written as the sum of no more than log(n) leonardo numbers.
// Therefore, any series of numbers can be represented in no more than log(n) leonardo trees, which are stored in order of decreasing size.
// Two consecutive Leonardo trees can be combined with a new root to form another Leonardo tree.
// The larger of the two becomes the left child of the new root, and the smaller becomes the right child. This is how we perform insertions.
// The trees are modified so as to also be ordered in ascending size of their root node.
public class LoggedLeonardoForest {
	public LoggedArray list;
	public ArrayList<Integer> sizes;
	
	// Conerts the passed array into a valid Leonardo forest in-place and stores the sizes of each of the trees.
	public LoggedLeonardoForest(LoggedArray list) {
		sizs = new ArrayList<Integer>();
	}
	
	// Inserts an element into the forest. Worst case O(log(n)), best case O(1).
	public insert(int elem) {
		if (sizes.size() == 0) {
			sizes.add(1);
		}
		else if (sizes.size() == 1) {
			sizes.add(0);
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