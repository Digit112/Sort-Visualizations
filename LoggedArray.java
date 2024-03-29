import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

// Array that keeps track of all operations performed on it and occasionally saves them to file.
// Make sure to call close() when you're done. Attempts to modify the array after calling close() will cause errors.
// Each operation is placed on its own line in the log file.
// All lines are prefixed with 'a', 'r', 'w', 'i', or 's' for "add", "read", "write", "insert", and "swap".
// Index values are written as integers in ASCII, separated by a comma in the case of a swap operation.
// In the case of "add", "write", and "insert", the value written is encoded as an ASCII string following a colon. The encoding is obtained from T.toString().
// Additional operations exist which do not affect the code or return a value.
// This includes highlighting, which marks an index.
// The title of an array can also be set. This is displayed in the render.
public class LoggedArray {
	private ArrayList<Integer> arr;
	
	private File fout;
	private BufferedWriter foutStream;
	
	public int totalOps;
	public int totalMetaOps;
	
	public int writesBetweenClock; // How many writes before System.nanoTime is appended to the file?
	private int numWrites = 0;
	
	public LoggedArray(int initialCapacity, String outFileName) throws IOException {
		arr = new ArrayList(initialCapacity);
		
		fout = new File(outFileName);
		foutStream = new BufferedWriter(new FileWriter(fout));
		
		writesBetweenClock = 6;
		resetTime();
	}
	
	public LoggedArray(String outFileName) throws IOException {
		arr = new ArrayList();
		
		fout = new File(outFileName);
		foutStream = new BufferedWriter(new FileWriter(fout));
		
		writesBetweenClock = 6;
		resetTime();
	}
	
	// Fill constructor. Filling the array does appear in the log.
	public LoggedArray(int initialSize, int fillVal, String outFileName) throws IOException {
		arr = new ArrayList(initialSize);
		
		fout = new File(outFileName);
		foutStream = new BufferedWriter(new FileWriter(fout));
		
		writesBetweenClock = 6;
		resetTime();
		
		for (int i = 0; i < initialSize; i++) {
			add(fillVal);
		}
	}
	
	// Appends the current result of system.nanoTime() to the file if enough writes have occurred.
	private void doWrite(String val, boolean isMeta) throws IOException {
		foutStream.write(val);
		
		if (isMeta) {
			totalMetaOps++;
		}
		else {
			totalOps++;
		}
		
		numWrites++;
		if (isMeta && numWrites >= writesBetweenClock) {
			writeTime();
			numWrites = 0;
		}
	}
	
	// Returns the parent of this node. Returns -1 if the index would be < 0.
	public int binHeapParent(int i) {
		int j = (int) Math.floor((i - 1) / 2);
		if (j < 0) {
			return -1;
		}
		else {
			return j;
		}
	}
	
	// Returns the child of this node. Returns -1 if the index would be >= heapSize.
	public int binHeapChild(int i, int heapSize, boolean getLeft) {
		int j;
		if (getLeft) {
			j = 2*i + 1;
		}
		else {
			j = 2*i + 2;
		}
		
		if (j >= heapSize) {
			return -1;
		}
		else {
			return j;
		}
	}
	
	// Writes the current time.
	public void writeTime() throws IOException {
		doWrite(String.format("Z%d\n", System.nanoTime()), false);
	}
	
	// Writes the current time, but also causes the timer in the render to reset to 0.
	public void resetTime() throws IOException {
		doWrite(String.format("Y%d\n", System.nanoTime()), false);
	}
		
	
	// Converts to a string. Reads are not logged.
	public String toString() {
		String ret = "";
		for (int i = 0; i < arr.size(); i++) {
			ret += arr.get(i).toString() + " ";
		}
		
		return ret;
	}
	
	public int size() {
		return arr.size();
	}
	
	public int get(int i) throws IOException {
		doWrite(String.format("r%d\n", i), true);
		
		return arr.get(i);
	}
	
	public void set(int i, int val) throws IOException {
		doWrite(String.format("w%d:%s\n", i, val), true);
		
		arr.set(i, val);
	}
	
	public void add(int val) throws IOException {
		doWrite(String.format("a:%s\n", val), true);
		
		arr.add(val);
	}
	
	public void add(int pos, int val) throws IOException {
		doWrite(String.format("i%d:%s\n", pos, val), true);
		
		arr.add(pos, val);
	}
	
	public void swap(int i, int j) throws IOException {
		doWrite(String.format("s%d,%d\n", i, j), true);
		
		int temp = arr.get(i);
		arr.set(i, arr.get(j));
		arr.set(j, temp);
	}
	
	// Counts as a step in the final output. Good for synchronizing disparate arrays.
	public void step() throws IOException {
		doWrite("K\n", false);
	}
	
	// highlight an index. This will appear in the log but will not effect the array.
	// Setting the highlight a second time overwrites the previous highlight.
	public void highlight(int i, int highlightVal) throws IOException {
		doWrite(String.format("H%d,%d\n", i, highlightVal), false);
	}
	
	// un-highlight an index.
	public void unhighlight(int i) throws IOException {
		doWrite(String.format("U%d\n", i), false);
	}
	
	// un-highlight all indices.
	public void unhighlightAll() throws IOException {
		doWrite(String.format("U*\n"), false);
	}
	
	// Set the title of a render. This can be changed at any time and is displayed in the final render.
	public void setTitle(String newTitle) throws IOException {
		doWrite(String.format("T:%s\n", newTitle), false);
	}
	
	public void close() throws IOException {
		writeTime();
		
		try {
			foutStream.close();
		}
		catch (IOException e) {
			System.out.println("Close failure.");
		}
	}
}