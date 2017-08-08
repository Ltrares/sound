package com.luketrares.beadstest;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class Recorder extends UGen implements Finishable {
	float[][] buffer;
	int currentPos = 0;
	int recordPos = 0;
	int repeatCount = 0;
	int currentRepeat = 0;
	int channelCount = 1;
	
	public Recorder(AudioContext audioContext, int channelCount, float duration, int repeatCount ) {
		super(audioContext, channelCount);		
		buffer = new float[channelCount][(int)(duration*audioContext.getSampleRate())];
		this.repeatCount = repeatCount;
		this.channelCount = channelCount;
	}
	
	@Override
	public void calculateBuffer() {		
		if ( recordPos < this.buffer.length ) {
			int amountToRecord = recordPos + this.bufferSize < this.buffer.length ? this.bufferSize : this.buffer.length - recordPos;
			
			for ( int i = 0; i < amountToRecord; i++ ) {
				for ( int j = 0; j < this.channelCount; j++ ) {
					buffer[j][recordPos] = bufIn[j][i];
				}
				recordPos ++;
			}

			return;
		}
		
		for ( int i = 0; i < this.bufferSize; i++ ) {
			for ( int j = 0; j < channelCount; j++ ) {
				bufOut[j][i] = getNextValue(j );
			}
		} //
		
	}


	private float getNextValue(int channel) {
		if ( isDone() ) return 0.0f;
		
		if ( currentPos >= this.buffer.length ) {
			this.repeatCount ++;
			currentPos = 0;
		
			if ( isDone() ) return 0.0f;
		} //

		currentPos += 1;
		
		return buffer[channel][currentPos];
		
	}





	@Override
	public boolean isDone() {
		return currentRepeat >= repeatCount;
	}

}
