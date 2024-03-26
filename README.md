# Sorting Algorithm Visualizations

Main.java records the operations performed by a particular sorting algorithm on an array into a log file. render.py produces frames depicting the array at various points in time, which can be combined into a video. This allows the creation of videos that visualize sorting algorithms.

render.py's parameters cannot be controlled from the command line (there are a lot of them) but they're right at the top of the file when you open it.

## Quickstart

Requires: 
- Java (Tested with SE 21)
- Python (Requires 3+, tested with 3.12)
- [pillow for Python](https://pypi.org/project/pillow/)

You will want to set parameters in both Main.java and render.py.

Compile and run the current sorting algorithm on a random array.
Outputs log.txt. Some sorting algorithms also output sublog.txt.
```
javac Main.java
java Main
```

Produce audio and video from a log.
Individual frames of video are produced in out/\*. Audio is output to audio.wav by default.
Note that this does not empty the out/ folder by default. If the new render is shorter, some old frames will still be present.
```
python render.py
```

Combine the frames and audio into a video with FFmpeg:
```
ffmpeg -i out/%03d.png -i audio.wav out.mp4
```

If you use an algorithm which also produced a sublog.txt, you may use render.py to produce a second video which is the same length as the first and is synchronized to the first in the following way:

- Go into render.py, change synchronyFileIn to the value of synchronyFileOut that was used for the previous render (synchrony.txt by default).
- Change synchronyFileOut to something else, an empty string works fine.
- Set logFileName to sublog.txt
- Change audioOutName to something else or an empty string, this prevents overwriting the previous audio.
- rerun render.py

You can create the synchronous video with ffmpeg:
```
ffmpeg -i out/%03d.png out_sync.mp4
```

And you can combine the two:
```
ffmpeg -i out.mp4 -i out_sync.mp4 -filter_complex vstack final.mp4
```

You can use `hstack` if you prefer to have the videos side-by-side.

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
LoggedArray<T>(int initialSize, T fillVal, String logFileName)
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

// Writes the current time (from System.nanoTime()) to the file.
// This is used for synchronizing audio, separate videos, and for displaying an on-screen timer.
void writeTime()

// Reset the current timer (as shown in the render) to 0.
void resetTime()
```

## Sorter

Implements a few sorting algorithms as static member functions. All functions take only a LoggedArray\<T\> as a parameter and all sort in ascending order.

```
selectionSort()
insertionSort()
quickSort()
mergeSort()
```