package com.luketrares.beadstest;

import java.util.concurrent.ThreadLocalRandom;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class Recorder extends DemoElement {
	float[][] recordBuffer;
	int currentPos = 0;
	int recordPos = 0;
	int repeatCount = 0;
	int currentRepeat = 0;
	int channelCount = 1;
	int outputBuffersize = 0;
	int recordBufferSize = 0;
	boolean inPlayback = false;
	boolean reverse = false;
	
	public Recorder(int bufferSize, int channelCount) {
		super(new AudioContext(), channelCount);
		// buffer = new
		// float[channelCount][(int)(duration*audioContext.getSampleRate())];
		this.repeatCount = ThreadLocalRandom.current().nextInt(1, 13);

		this.channelCount = channelCount;
		// this.bufferSize = bufferSize;
		this.outputBuffersize = bufferSize;

		this.recordBufferSize = (int) (Math.random() * 17.0*44100.0);

		this.recordBuffer = new float[this.channelCount][this.recordBufferSize];
	
		this.reverse = ThreadLocalRandom.current().nextBoolean();
	}

	@Override
	public String textDisplay() {
		return "Recorder " + getId() + ": length = " + this.recordBufferSize + " repeat = " + this.repeatCount + " reverse = " + this.reverse;
	}

	@Override
	public void calculateBuffer() {

		if (recordPos < this.recordBufferSize) {
			int amountToRecord = ( recordPos + this.outputBuffersize ) < this.recordBufferSize ? this.outputBuffersize : this.recordBufferSize - recordPos;

			if (this.getInputElements().size() > 0) {
				for (int i = 0; i < amountToRecord; i++) {
					DemoElement element = this.getInputElements().get(0);
					// for (DemoElement element : this.getInputElements()) {
					for (int j = 0; j < this.channelCount; j++) {
						recordBuffer[j][recordPos] += element.getOutBuffer(j)[i];
					} //

					// }
					recordPos++;
					
					//if ( Math.random() < 0.001 ) System.out.println( textDisplay() + " recording " + recordPos );
				} //
			} // else
			return;
		} //if

		if ( !inPlayback ) {
			System.out.println( this.textDisplay() + " in playback" );
			inPlayback = true;
		} //if
		for (int i = 0; i < this.outputBuffersize; i++) {
			for (int j = 0; j < channelCount; j++) {
				bufOut[j][i] = getNextValue(j);
			} //for
		} //for

	}

	private float getNextValue(int channel) {
		if (isDone())
			return 0.0f;

		if (currentPos >= recordBufferSize) {
			this.currentRepeat++;
			currentPos = 0;

			if (isDone())
				return 0.0f;

			System.out.println(this.getId() + " playback " + currentRepeat + "/" + this.repeatCount);
		} //

		float value = this.reverse ? this.recordBuffer[channel][this.recordBufferSize-currentPos-1] : this.recordBuffer[channel][currentPos];

		this.currentPos ++;
		return value;
	}

	@Override
	public boolean isDone() {
		return currentRepeat >= repeatCount;
	}

}
