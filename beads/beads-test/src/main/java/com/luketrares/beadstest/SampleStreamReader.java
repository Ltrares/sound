package com.luketrares.beadstest;

import java.io.IOException;
import java.io.InputStream;

import net.beadsproject.beads.data.audiofile.FileFormatException;
import net.beadsproject.beads.data.audiofile.OperationUnsupportedException;

public class SampleStreamReader {

	InputStream iStream;
	private byte[] buffer = new byte[4096];
	private int compressionCode;
	private int numChannels;
	private long sampleRate;
	private int blockAlign;
	private int validBits;
	private int bytesPerSample;
	private long numFrames;
	private double floatScale;
	private double floatOffset;
	private int bufferPointer;
	private int bytesRead;
	private long frameCounter;
	private Object ioState;
	
	public float[][] readSample(InputStream iStream) throws FileFormatException, IOException, OperationUnsupportedException {
		this.iStream = iStream;
		int i = this.iStream.read(this.buffer, 0, 12);
		if (i != 12)
			throw new FileFormatException("Not enough wav file bytes for header");
		long l1 = getLE(this.buffer, 0, 4);
		long l2 = getLE(this.buffer, 4, 4);
		long l3 = getLE(this.buffer, 8, 4);
		if (l1 != 1179011410L)
			throw new FileFormatException("Invalid Wav Header data, incorrect riff chunk ID");
		if (l3 != 1163280727L)
			throw new FileFormatException("Invalid Wav Header data, incorrect riff type ID");
		//if (this.file.length() != l2 + 8L)
		//	throw new FileFormatException(
		//			"Header chunk size (" + l2 + ") does not match file size (" + this.file.length() + ")");
		int j = 0;
		int k = 0;
		while (true) {
			i = this.iStream.read(this.buffer, 0, 8);
			if (i == -1)
				throw new FileFormatException("Reached end of file without finding format chunk");
			if (i != 8)
				throw new FileFormatException("Could not read chunk header");
			long l4 = getLE(this.buffer, 0, 4);
			l2 = getLE(this.buffer, 4, 4);
			long l5 = (l2 % 2L == 1L) ? l2 + 1L : l2;
			if (l4 == 544501094L) {
				j = 1;
				i = this.iStream.read(this.buffer, 0, 16);
				int l = (int) getLE(this.buffer, 0, 2);
				if ((l != 1) && (l != 3))
					throw new OperationUnsupportedException("Compression Code " + l + " not supported");
				this.compressionCode = l;
				this.numChannels = (int) getLE(this.buffer, 2, 2);
				this.sampleRate = getLE(this.buffer, 4, 4);
				this.blockAlign = (int) getLE(this.buffer, 12, 2);
				this.validBits = (int) getLE(this.buffer, 14, 2);
				if (this.numChannels == 0)
					throw new FileFormatException("Number of channels specified in header is equal to zero");
				if (this.blockAlign == 0)
					throw new FileFormatException("Block Align specified in header is equal to zero");
				if (this.validBits < 2)
					throw new FileFormatException("Valid Bits specified in header is less than 2");
				if (this.validBits > 64)
					throw new FileFormatException(
							"Valid Bits specified in header is greater than 64, this is greater than a long can hold");
				if ((this.compressionCode == 3) && (this.validBits != 32) && (this.validBits != 64))
					throw new IOException("Only 32-bit and 64-bit Floating Point PCM files are supported");
				this.bytesPerSample = ((this.validBits + 7) / 8);
				if (this.bytesPerSample * this.numChannels != this.blockAlign)
					throw new FileFormatException(
							"Block Align does not agree with bytes required for validBits and number of channels");
				l5 -= 16L;
				if (l5 > 0L)
					this.iStream.skip(l5);
			} else {
				if (l4 == 1635017060L) {
					if (j == 0)
						throw new FileFormatException("Data chunk found before Format chunk");
					if (l2 % this.blockAlign != 0L)
						throw new FileFormatException("Data Chunk size is not multiple of Block Align");
					this.numFrames = (l2 / this.blockAlign);
					k = 1;
					break;
				}
				this.iStream.skip(l5);
			}
		}
		if (k == 0)
			throw new FileFormatException("Did not find a data chunk");
		if (this.validBits > 8) {
			this.floatOffset = 0.0D;
			this.floatScale = (1 << this.validBits - 1);
		} else {
			this.floatOffset = -1.0D;
			this.floatScale = (0.5D * ((1 << this.validBits) - 1));
		}
		this.bufferPointer = 0;
		this.bytesRead = 0;
		this.frameCounter = 0L;
		this.ioState = IOState.READING;		
		
		
		float[][] arrayOfFloat = new float[this.numChannels][(int) this.numFrames];
		long l = 0L;
		int i1 = 0;
		int i2 = 10000;
		do {
			l = readFrames(arrayOfFloat, i1, i2);
			i1 = (int) (i1 + l);
		} while (l != 0L);
		
		return arrayOfFloat;
		
	}

	private int readFrames(float[][] paramArrayOfFloat, int paramInt1, int paramInt2) throws IOException {
		if (this.ioState != IOState.READING)
			throw new IOException("Incorrect IOState");
		int l;
		if ((this.compressionCode == 3) && (this.validBits == 32))
			for (int i = 0; i < paramInt2; ++i) {
				if (this.frameCounter == this.numFrames)
					return i;
				for (l = 0; l < this.numChannels; ++l)
					paramArrayOfFloat[l][paramInt1] = Float.intBitsToFloat((int) readSample());
				++paramInt1;
				this.frameCounter += 1L;
			}
		else if ((this.compressionCode == 3) && (this.validBits == 64))
			for (int j = 0; j < paramInt2; ++j) {
				if (this.frameCounter == this.numFrames)
					return j;
				for (l = 0; l < this.numChannels; ++l)
					paramArrayOfFloat[l][paramInt1] = (float) Double.longBitsToDouble(readSample());
				++paramInt1;
				this.frameCounter += 1L;
			}
		else
			for (int k = 0; k < paramInt2; ++k) {
				if (this.frameCounter == this.numFrames)
					return k;
				for (l = 0; l < this.numChannels; ++l)
					paramArrayOfFloat[l][paramInt1] = (float) (this.floatOffset + readSample() / this.floatScale);
				++paramInt1;
				this.frameCounter += 1L;
			}
		return paramInt2;
	}

	private long readSample() throws IOException {
		long l1 = 0L;
		for (int i = 0; i < this.bytesPerSample; ++i) {
			if (this.bufferPointer == this.bytesRead) {
				int j = this.iStream.read(this.buffer, 0, 4096);
				if (j == -1)
					throw new IOException("Not enough data available");
				this.bytesRead = j;
				this.bufferPointer = 0;
			}
			long l2 = this.buffer[this.bufferPointer];
			if ((i < this.bytesPerSample - 1) || (this.bytesPerSample == 1))
				l2 &= 255L;
			l1 += (l2 << i * 8);
			this.bufferPointer += 1;
		}
		return l1;
	}
	private static long getLE(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
		paramInt1 += --paramInt2;
		long l = paramArrayOfByte[paramInt1] & 0xFF;
		for (int i = 0; i < paramInt2; ++i)
			l = (l << 8) + (paramArrayOfByte[(--paramInt1)] & 0xFF);
		return l;
	}
	
	private static enum IOState {
		READING, WRITING, CLOSED;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	public int getCompressionCode() {
		return compressionCode;
	}

	public void setCompressionCode(int compressionCode) {
		this.compressionCode = compressionCode;
	}

	public int getNumChannels() {
		return numChannels;
	}

	public void setNumChannels(int numChannels) {
		this.numChannels = numChannels;
	}

	public long getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(long sampleRate) {
		this.sampleRate = sampleRate;
	}

	public int getBlockAlign() {
		return blockAlign;
	}

	public void setBlockAlign(int blockAlign) {
		this.blockAlign = blockAlign;
	}

	public int getValidBits() {
		return validBits;
	}

	public void setValidBits(int validBits) {
		this.validBits = validBits;
	}

	public int getBytesPerSample() {
		return bytesPerSample;
	}

	public void setBytesPerSample(int bytesPerSample) {
		this.bytesPerSample = bytesPerSample;
	}

	public long getNumFrames() {
		return numFrames;
	}

	public void setNumFrames(long numFrames) {
		this.numFrames = numFrames;
	}

	public double getFloatScale() {
		return floatScale;
	}

	public void setFloatScale(double floatScale) {
		this.floatScale = floatScale;
	}

	public double getFloatOffset() {
		return floatOffset;
	}

	public void setFloatOffset(double floatOffset) {
		this.floatOffset = floatOffset;
	}

	public int getBufferPointer() {
		return bufferPointer;
	}

	public void setBufferPointer(int bufferPointer) {
		this.bufferPointer = bufferPointer;
	}

	public int getBytesRead() {
		return bytesRead;
	}

	public void setBytesRead(int bytesRead) {
		this.bytesRead = bytesRead;
	}

	public long getFrameCounter() {
		return frameCounter;
	}

	public void setFrameCounter(long frameCounter) {
		this.frameCounter = frameCounter;
	}

	public Object getIoState() {
		return ioState;
	}

	public void setIoState(Object ioState) {
		this.ioState = ioState;
	}
}
