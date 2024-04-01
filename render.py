from PIL import Image, ImageDraw, ImageFont
import wave
import math
import struct
import time

# Takes a log file from LoggedArray and renders the array as a series of vertical columns.
# The result is a series of frames recounting the history of the array. This is intended to be used to animate sorting algorithms.
# Note: Although the Sorter and LoggedArray classes are generics which can handle many types, this program only works on arrays of integers.

# Which file were the logs dumped into?
logFileName = "log.txt"

# How many steps to execute between renders? Highlights and Unhighlights do not count as steps.
stepsPerRender = 36

# If this is not an empty string, it will override stepsPerRender.
# This specifies a synchrony file output by a previous use of render.py.
# If specified, output a video of the same length as the previous run which stays in sync with it.
# Useful for generating videos of multiple arrays which were manipulated around the same time, as in the case of merge sort.
synchronyFileIn = ""

# If not empty, specifies the filename to output a synchrony file to.
synchronyFileOut = "synchrony.txt"

if (synchronyFileIn != "" and synchronyFileIn == synchronyFileOut):
	print("SynchronyFileIn and synchronyFileOut cannot be the same.")
	exit()

# Output for the audio file. Will not write audio if this is an empty string.
audioOutName = "audio.wav"

# Sample rate of the audio
audioSampleRate = 12000

# Video FPS. If this is incorrect, the audio will not match the video.
videoFrameRate = 25

# Each read/write/swap add frequencies to a list.
# Every time the array is modified, the average of that list is taken and an appropriate number of audio samples are created.
# This determines how many steps those frequencies live for.
audioDuration = 4

audioVolume = 0.3

# When an index is read or written to (or swapped with another) it gets highlighted.
# For how many frames does it stay highlighted?
# Does not effect indices which were manually highlighted.
modifyHighlightDuration = 1

# Number of frames to render after all commands are exhausted.
deadFrames = 50

# Dimensions of the output frames.
width = 1920
height = 1080

# Size of the black border surrounding the render on all sides.
borderTop = 80
borderLeft = 0
borderRight = 0
borderBottom = 0

# Fraction of space that a bar can take up which is actually used by that bar.
# The remainder forms the gaps between the bars.
barWidthFraction = 1

# Whether to draw the title, time, read/writes, and array size saved to the LoggedArray log.
doDrawTitle = True
doDrawTime = True
doDrawReadWrites = True
doDrawSize = True

# The font to write text with
fontFile = "./ubuntu_mono/UbuntuMono-R.ttf"

# Colors
bgColor      = (0,   0,   0  ) # Background
fgColor      = (250, 250, 250) # Font
baseColor    = (240, 240, 240) # Color of unmodified bars.
readColor    = (20,  240, 20 ) # Color of bars that were recently read from.
writtenColor = (240, 20,  20 ) # Color of bars that were recently written to.
swappedColor = (220, 220, 20 ) # Color of bars that were recently swapped with others.

# Highlight colors overwrite read/write/swap colors.
# The highlight on a bar indexes this list.
highlightColors = [
	(20, 20, 240),
	(220, 20, 220)
]

# Draws "Thank for Watching!" Over the center of the video for 5 seconds. Then fades out.
doThanks = False;

class UnloggedArray:
	def __init__(self, inFileName):
		self.arr = []
		self.highlights = []
		
		self.title = ""
		self.subtitle = ""
		
		self.reads = 0
		self.writes = 0 # Does not count add operations or insertions.
		
		self.firstTime = -1 # The first time recorded, not modified by a time reset.
		self.startTime = -1 # The time from which the timer in the render measures. Set to currentTime by a time reset.
		self.currentTime = -1
		
		self.audioFout = None
		if audioOutName != "":
			self.audioFout = wave.open(audioOutName, "wb")
			self.audioFout.setnchannels(1)
			self.audioFout.setsampwidth(2)
			self.audioFout.setframerate(audioSampleRate)
			
			self.audioData = b''
			self.audioFreqArr = []
			self.audioFreqAge = []
			
			self.audioFreq = -1
			
			# Okay, so this is a value from 0 to 2PI from which to sample a sin wave for the audio samples.
			# This value is incremented each time getSample() is called.
			# The amount it increases by is controlled by audioFreq.
			# This prevents changes in the frequency from completely fucking the samples.
			# It is 3:00am.
			self.audioSamplePoint = 0
		
		self.maxVal = 0
		
		self.fout = None
		if synchronyFileOut != "":
			self.fout = open(synchronyFileOut, "w")
		
		# Tracks which indices have been read/written, and swapped, and how many frames have been rendered since then.
		self.read = []
		self.written = []
		self.swapped = []
		
		fin = open(inFileName, "r")
		self.commands = fin.read().split("\n")[:-1]
		self.index = 0
	
	def addFreqFromVal(self, val):
		if (self.maxVal > 0):
			# Uses linear interpolation where it should likely use exponential interpolation.
			self.audioFreqArr.append(val / self.maxVal * 380 + 120)
			self.audioFreqAge.append(0)
	
	def getSample(self):
		if self.audioFreq != -1:
			self.audioSamplePoint += 2 * math.pi / (audioSampleRate / self.audioFreq)
			self.audioSamplePoint = math.fmod(self.audioSamplePoint, 2 * math.pi)
			
			return int((math.sin(self.audioSamplePoint) + 1) / 2 * 65535 * audioVolume)
		
		else:
			return 0
	
	# Increments the age counter for all values in read, written, and swapped.
	# Resets all values that are too old to "None"
	# Also appends to synchronyFileOut, if it was specified.
	def renderWork(self, maxAge):
		for i in range(0, len(self.read)):
			if self.read[i] is not None:
				self.read[i] += 1
				if self.read[i] >= maxAge:
					self.read[i] = None
			
			if self.written[i] is not None:
				self.written[i] += 1
				if self.written[i] >= maxAge:
					self.written[i] = None
			
			if self.swapped[i] is not None:
				self.swapped[i] += 1
				if self.swapped[i] >= maxAge:
					self.swapped[i] = None
		
		if self.fout is not None:
			self.fout.write(str(self.currentTime) + "\n")
	
	# Modifies the array according to the next command and increments the command pointer.
	# If a command is a highlight or unhighlight, calls itself again.
	# Returns false if no commands remain to be retrieved, true otherwise.
	def step(self):
		# Eliminate old frequencies
		if self.audioFout is not None:
			while len(self.audioFreqArr) > 0 and self.audioFreqAge[0] >= audioDuration:
				del self.audioFreqArr[0]
				del self.audioFreqAge[0]
		
		if self.index >= len(self.commands):
			return False
		
		command = ""
		while len(command) == 0:
			command = self.commands[self.index]
			self.index += 1
		
		strStart = command.find(":") + 1
		
		if command[0] == 'a':
			if (strStart == 0):
				print("Error, expected ':' after add command.")
			
			val = int(command[strStart:])
			
			if val > self.maxVal:
				self.maxVal = val
			
			self.arr.append( val )
			self.highlights.append(None)
			self.read.append(None)
			self.written.append(None)
			self.swapped.append(None)
			
			if self.audioFout is not None:
				self.freq = self.addFreqFromVal(val)
			
			self.step()
		
		elif command[0] == 'r':
			ind = int( command[1:] )
			self.read[ind] = 0;
			
			self.reads += 1
			
			if self.audioFout is not None:
				self.freq = self.addFreqFromVal(self.arr[ind])
		
		elif command[0] == 'w':
			if (strStart == 0):
				print("Error, expected ':' after write command.")
			
			val = int( command[strStart:] )
			ind = int( command[1:strStart-1] )
			
			if val > self.maxVal:
				self.maxVal = val
			
			self.arr[ind] = val
			
			self.written[ind] = 0
			self.writes += 1
			
			if self.audioFout is not None:
				self.freq = self.addFreqFromVal(val)
		
		elif command[0] == 'W':
			if (strStart == 0):
				print("Error, expected ':' after write command.")
			
			val = int( command[strStart:] )
			ind = int( command[1:strStart-1] )
			
			if val > self.maxVal:
				self.maxVal = val
			
			self.arr[ind] = val
			
			self.swapped[ind] = 0
			self.writes += 1
			
			if self.audioFout is not None:
				self.freq = self.addFreqFromVal(val)
		
		elif command[0] == 's':
			delimiter = command.find(",")
			if (delimiter == -1):
				print("Error, expected ',' after swap command.")
			
			ind1 = int( command[1:delimiter] )
			ind2 = int( command[delimiter+1:] )
			
			temp = self.arr[ind1]
			self.arr[ind1] = self.arr[ind2]
			self.arr[ind2] = temp
			
			self.swapped[ind1] = 0
			self.swapped[ind2] = 0
			self.reads += 2
			self.writes += 2
			
			if self.audioFout is not None:
				self.freq = self.addFreqFromVal((self.arr[ind1] + self.arr[ind2]) / 2)
		
		elif command[0] == 'i':
			if (strStart == -1):
				print("Error, expected ':' after swap command.")
			
			val = int( command[strStart:] )
			ind = int( command[1:strStart] )
			
			if val > self.maxVal:
				self.maxVal = val
			
			self.arr.insert(ind, val)
			
			self.written[ind] = True
		
		elif command[0] == 'K':
			pass
		
		elif command[0] == 'H':
			delimiter = command.find(",")
			if (delimiter == -1):
				print("Error, expected ',' after highlight command.")
			
			ind = int( command[1:delimiter] )
			val = int( command[delimiter+1:] )
			
			self.highlights[ind] = val
			
			self.step()
		
		elif command[0] == 'U':
			if command[1] == '*':
				for i in range(0, len(self.highlights)):
					self.highlights[i] = None
			
			else:
				ind = int( command[1:] )
				self.highlights[ind] = None
			
			self.step()
		
		elif command[0] == 'T':
			if (strStart == -1):
				print("Error, expected ':' after title command.")

			self.title = command[strStart:]
			
			self.step()
		
		elif command[0] == 'Z':
			self.currentTime = int( command[1:] )
			
			if self.firstTime == -1:
				self.firstTime = self.currentTime
				self.startTime = self.currentTime
			
			self.step()
		
		elif command[0] == 'Y':
			self.currentTime = int( command[1:] )
			self.startTime = self.currentTime
			
			if self.firstTime == -1:
				self.firstTime = self.currentTime
			
			self.step()
			
		else:
			print("Command '" + command[0] + "' not recognized.")
		
		# Recalculate audio frequency.
		if self.audioFout is not None:
			self.audioFreq = 0
			for freq in self.audioFreqArr:
				self.audioFreq += freq
			
			if len(self.audioFreqArr) > 0:
				self.audioFreq /= len(self.audioFreqArr)
			else:
				self.audioFreq = -1
			
			# Age audio frequencies
			for i in range(len(self.audioFreqArr)):
				self.audioFreqAge[i] += 1
			
		return True
	
	def __del__(self):
		if self.fout is not None:
			self.fout.close()
		
		if self.audioFout is not None:
			print("Saving audio to file...")
			self.audioFout.writeframes(self.audioData)
			self.audioFout.close()

# Takes three two-tuples. Returns the second value in the tuple with the lowest first value.
# A first value of None is interpreted as being infinitely large. If all first values are None, returns None.
def tripleAgeMin(a, b, c):
	if a[0] is None:
		a[0] = float("inf")
	
	if b[0] is None:
		b[0] = float("inf")
	
	if c[0] is None:
		c[0] = float("inf")
	
	minimum = a
	if b[0] < minimum[0]:
		minimum = b
	
	if c[0] < minimum[0]:
		minimum = c
	
	if minimum[0] == float("inf"):
		return None
	else:
		return minimum[1]

sortingLog = UnloggedArray(logFileName)

fnt = ImageFont.truetype(fontFile, 20)
thxFnt = ImageFont.truetype(fontFile, 100)

drwWidth = width - (borderLeft + borderRight)
drwHeight = height - (borderTop + borderBottom)

doSynchrony = False
synchronyData = []
if synchronyFileIn != "":
	doSynchrony = True
	fin = open(synchronyFileIn, "r")
	synchronyData = fin.read().split("\n")[:-1]
	fin.close()
	
	for i in range(len(synchronyData)):
		synchronyData[i] = int( synchronyData[i] )

print("Rendering...")
startTime = time.time()
loadingSymbols = ["/", "|", "\\", "-"]

moreFrames = True
frameCounter = 0
audioFrameCounter = 0
while True:
	loadingIndex = int((time.time() - startTime) * 2) % 4
	
	if doSynchrony:
		if SynchronyFileIn == "":
			print("Error: SynchronyFileIn has not been set.")
		
		# Step forward until we have an accurate timer or there are no steps remaining.
		# This should only ever take 1 step.
		while (sortingLog.currentTime == -1):
			if not sortingLog.step():
				break
				
		# If the synchrony file is finished, then we're done rendering.
		if frameCounter >= len(synchronyData):
			break
		
		# If the synchrony file is ahead of us, step until we're ahead of it.
		while synchronyData[frameCounter] > sortingLog.currentTime:
			if not sortingLog.step():
				break
		
	else:
		for i in range(stepsPerRender):
			moreFrames = sortingLog.step()
			
			# Write audio
			if sortingLog.audioFout is not None and moreFrames:
				videoTimeSeconds = (frameCounter + i/stepsPerRender) / videoFrameRate
				idealNumFrames = videoTimeSeconds * audioSampleRate
				
				while audioFrameCounter < idealNumFrames and sortingLog.audioFreq != -1:
					sortingLog.audioData += struct.pack("<H", sortingLog.getSample())
					
					audioFrameCounter += 1
		
		if not moreFrames:
			deadFrames -= 1
		
		if deadFrames <= 0:
			break
	
	print("\r%c %.2f%% (%d/%d steps) Rendering out/%03d.png" % (loadingSymbols[loadingIndex], sortingLog.index / len(sortingLog.commands) * 100, sortingLog.index, len(sortingLog.commands), frameCounter), end="")
	
	img = Image.new("RGB", (width, height), bgColor)
	drw = ImageDraw.Draw(img)
	
	if len(sortingLog.arr) > 0:
		# Calculate sizing for all bars
		barSpaceWidth = drwWidth / len(sortingLog.arr)
		barWidth = barSpaceWidth * barWidthFraction
		
		for i in range(len(sortingLog.arr)):
			# Calculate height for this bar.
			barHeight = drwHeight
			if sortingLog.maxVal > 0:
				barHeight = sortingLog.arr[i] / sortingLog.maxVal * drwHeight
			
			# Calculate position of upper-right and lower-left corners of this bar.
			barPosLow = (
				borderLeft + i*barSpaceWidth + barSpaceWidth/2 - barWidth/2,
				borderTop + drwHeight - barHeight
			)
			
			barPosHigh = (
				barPosLow[0] + barWidth,
				barPosLow[1] + barHeight
			)
			
			# Calculate color for this bar.
			color = baseColor
			
			# Override base color with most recent modify color, if it exists.
			modifyColor = tripleAgeMin(
				[sortingLog.swapped[i], swappedColor],
				[sortingLog.read[i], readColor],
				[sortingLog.written[i], writtenColor]
			)
			
			if modifyColor != None:
				color = modifyColor
			
			# Override modify color with highlight, if it exists.
			if sortingLog.highlights[i] is not None:
				color = highlightColors[sortingLog.highlights[i]]
			
			# Draw this bar
			drw.rectangle([barPosLow, barPosHigh], fill=color)
	
	# Draw text
	if doDrawTitle and sortingLog.title != "":
		drw.text((10, 10), sortingLog.title, fill=fgColor, font=fnt)
	
	if doDrawTime:
		drw.text((10, 36), "Time: %.3fms" % ((sortingLog.currentTime - sortingLog.startTime) / 1000000), fill=fgColor, font=fnt)
	
	if doDrawReadWrites:
		drw.text((410, 10), "Reads: %d" % (sortingLog.reads), fill=fgColor, font=fnt)
		drw.text((410, 36), "Writes: %d" % (sortingLog.writes), fill=fgColor, font=fnt)
	
	if doDrawSize:
		drw.text((810, 10), "Size: %s" % (len(sortingLog.arr)), fill=fgColor, font=fnt)
	
	# Thank you text.
	if doThanks:
		currentTimeSec = frameCounter / videoFrameRate
		
		opacity = 255
		if currentTimeSec >= 5:
			opacity = 0
		
		if opacity > 0:
			bbox = thxFnt.getbbox("Thanks for Watching!")
			textWidth2 = abs(bbox[2] - bbox[0]) / 2
			textHeight2 = abs(bbox[3] - bbox[1]) / 2
			
			drw.rectangle([(width/2 - textWidth2 - 30, height/2 - textHeight2 - 30), (width/2 + textWidth2 + 30, height/2 + textHeight2 + 30)], fill=bgColor)
			drw.text((width/2 - textWidth2, height/2 - textHeight2), "Thanks for Watching!", fill=fgColor, font=thxFnt)
	
	# Save this image.
	img.save("out/%03d.png" % frameCounter)
	frameCounter += 1
	sortingLog.renderWork(modifyHighlightDuration)

print("")
print("Finished in %.1f seconds." % (time.time() - startTime))
print("Wrote %d frames; %d audio samples." % (frameCounter, audioFrameCounter))