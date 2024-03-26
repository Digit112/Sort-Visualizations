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
public class LoggedArray <T> {
	private ArrayList<T> arr;
	
	private File fout;
	private BufferedWriter foutStream;
	
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
	
	public LoggedArray(int initialCapacity, String outFileName) throws IOException {
		arr = new ArrayList<T>(initialCapacity);
		
		fout = new File(outFileName);
		foutStream = new BufferedWriter(new FileWriter(fout));
	}
	
	public LoggedArray(String outFileName) throws IOException {
		arr = new ArrayList<T>();
		
		fout = new File(outFileName);
		foutStream = new BufferedWriter(new FileWriter(fout));
	}
	
	public T get(int i) {
		try {
			foutStream.write(String.format("r%d\n", i));
		}
		catch (IOException e) {
			System.out.println("Failed to write get to file.");
		}
		
		return arr.get(i);
	}
	
	public void set(int i, T val) {
		try {
			foutStream.write(String.format("w%d:%s\n", i, val.toString()));
		}
		catch (IOException e) {
			System.out.println("Failed to write set to file.");
		}
		
		arr.set(i, val);
	}
	
	public void add(T val) {
		try {
			foutStream.write(String.format("a:%s\n", val.toString()));
		}
		catch (IOException e) {
			System.out.println("Failed to write add to file.");
		}
		
		arr.add(val);
	}
	
	public void add(int pos, T val) {
		try {
			foutStream.write(String.format("i%d:%s\n", pos, val.toString()));
		}
		catch (IOException e) {
			System.out.println("Failed to write insertion to file.");
		}
		
		arr.add(pos, val);
	}
	
	public void swap(int i, int j) {
		try {
			foutStream.write(String.format("s%d,%d\n", i, j));
		}
		catch (IOException e) {
			System.out.println("Failed to write swap to file.");
		}
		
		T temp = arr.get(i);
		arr.set(i, arr.get(j));
		arr.set(j, temp);
	}
	
	// highlight an index. This will appear in the log but will not effect the array.
	// Setting the highlight a second time overwrites the previous highlight.
	public void highlight(int i, int highlightVal) {
		try {
			foutStream.write(String.format("H%d,%d\n", i, highlightVal));
		}
		catch (IOException e) {
			System.out.println("Failed to write highlight to file.");
		}
	}
	
	// un-highlight an index.
	public void unhighlight(int i) {
		try {
			foutStream.write(String.format("U%d\n", i));
		}
		catch (IOException e) {
			System.out.println("Failed to write unhighlight to file.");
		}
	}
	
	// un-highlight all indices.
	public void unhighlightAll() {
		try {
			foutStream.write(String.format("U*\n"));
		}
		catch (IOException e) {
			System.out.println("Failed to write unhighlightAll to file.");
		}
	}
	
	// Set the title of a render. This can be changed at any time and is displayed in the final render.
	public void setTitle(String newTitle) {
		try {
			foutStream.write(String.format("T:%s\n", newTitle));
		}
		catch (IOException e) {
			System.out.println("Failed to write setTitle to file.");
		}
	}
	
	public void close() {
		try {
			foutStream.close();
		}
		catch (IOException e) {
			System.out.println("Close failure.");
		}
	}
}