package com.luketrares.beadstest;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class JavaSoundPlayer {

	SourceDataLine line;
	AudioFormat af;
	byte[] buffer;	
	int samplesInBuffer;
	double secondsPerSample;
	double maxValue;
	int bytesPerSample;
	
	public JavaSoundPlayer( int sampleRate, int channels, int bytesPerSample, int samplesInBuffer ) throws LineUnavailableException {
		af = new AudioFormat(sampleRate, 8 * bytesPerSample, channels, true, true);
		this.samplesInBuffer = samplesInBuffer;
		buffer = new byte[samplesInBuffer*channels*bytesPerSample];
		line = AudioSystem.getSourceDataLine(af);
		line.open(af, buffer.length*3);
		line.start();
		secondsPerSample = 1.0 / (double) sampleRate;
		maxValue = Math.pow(2.0, this.af.getSampleSizeInBits()- 1) - 1.0;
		this.bytesPerSample = bytesPerSample;
	}
	
	
	
	
	public boolean ready( int length ) {
		return line.available() >= length;
	}
	
	public boolean play(float[][] sound ) {
		prepareOutputBuffer( sound );
		int result = line.write(buffer, 0, buffer.length);
		
		return true;
	} //

	
	private void prepareOutputBuffer(float[][] sound) {
		int outputPosition = 0;

		byte[] op = new byte[Long.BYTES];
		for (int channelIndex = 0; channelIndex < this.af.getChannels(); channelIndex++) {

			for (int sampleIndex = 0; sampleIndex < this.samplesInBuffer; sampleIndex++) {
				//channelAverages[channelIndex] += weight * highResBuffer[channelIndex][sampleIndex];
				
				long result = (long) (maxValue*Math.tanh(sound[channelIndex][sampleIndex]));
				
				//if ( result > maxValue ) result = (long)maxValue;
				//else if ( result < -maxValue ) result = -(long)maxValue;

				longToBytes(result, op);

				for (int byteIndex = 0; byteIndex < this.bytesPerSample; byteIndex++) {
					buffer[outputPosition] = op[this.bytesPerSample - 1 - byteIndex];
					outputPosition++;
				} // for byteIndex

			} // for sampleIndex

		} // for channelIndex

	
	}

	public static void longToBytes(long l, byte[] result) {
		for (int i = 0; i < Long.BYTES; ++i) {
			result[Long.BYTES - 1 - i] = (byte) (l >> (Long.BYTES - i - 1 << 3));
		}
	}
	
	public int getSamplesInBuffer() {
		return samplesInBuffer;
	}


	public void setSamplesInBuffer(int samplesInBuffer) {
		this.samplesInBuffer = samplesInBuffer;
	}


	public double getSecondsPerSample() {
		return secondsPerSample;
	}


	public void setSecondsPerSample(double secondsPerSample) {
		this.secondsPerSample = secondsPerSample;
	}
	
}
