import wave
import math
import struct

wavSampleRate = 12000

fout = wave.open("test.wav", "wb")
fout.setnchannels(1)
fout.setsampwidth(2)
fout.setframerate(wavSampleRate)

freq = 90

data = b''
for i in range(wavSampleRate*4):
	t = i / wavSampleRate
	
	if t < 1:
		freq = 100
	elif t < 2:
		freq = 200
	else:
		freq = 400
	
	sample = int((math.sin(t * freq * 2 * math.pi) + 1) * 3000 + 3000)
	
	data += struct.pack("<H", sample)

fout.writeframes(data)
fout.close()