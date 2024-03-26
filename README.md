# Sorting Algorithm Visualizations

Main.java records the operations performed by a particular sorting algorithm on an array into a log file. render.py produces frames depicting the array at various points in time, which can be combined into a video. This allows the creation of videos that visualize sorting algorithms.

render.py's parameters cannot be controlled from the command line (there are a lot of them) but they're right at the top of the file when you open it.

## LoggedArray\<T\>

LoggedArray\<T\> is a wrapper for ArrayList\<T\> that outputs all of the operations performed on it into a log file with a specific format. It currently implements:

```
T get(int i)
void set(int i, T val)
void add(T val)
void add(int i, T val)
void swap(int i, int j)
```

and has constructors:
```
LoggedArray<T>(String logFileName)
LoggedArray<T>(int initialCapacity, String logFileName)
```

Where logFileName is the path to the file to record logs. To close the file being written to, close() must be called before the LoggedArray is destroyed.
The following commands also exist:
```
// Changes this index's color in render. Indexes highlightColors in the render.py parameters.
void highlight(int i, int highlightValue)

// Removes the highlight from an index.
void unhighlight(int i)

// Sets the current title. This is displayed in the render.
void setTitle(String newTitle)
```

## Sorter

Implements a few sorting algorithms as static member functions. All functions take only a LoggedArray\<T\> as a parameter and all sort in ascending order.

```
selectionSort()
insertionSort()
quickSort()
mergeSort()
```