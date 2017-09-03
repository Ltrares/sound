package com.luketrares.beadstest;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;

public class StretchDemoSample extends DemoElement {

	Sample sample;
	
	double[] prevValues;
	double[] lastCrossings;
	int[] repeatCounts;
	
	double[] currentPos;
	double msPerSample;
	int stretch;
	
	public StretchDemoSample(AudioContext paramAudioContext, int channels, Sample sample ) {
		super(paramAudioContext, channels);
		this.sample = sample;
		repeatCounts = new int[channels];
		currentPos = new double[channels];
		lastCrossings = new double[channels];
		prevValues = new double[channels];
		msPerSample = 1000.0/paramAudioContext.getSampleRate();
		stretch = 7;
	}

	@Override
	public boolean isDone() {
		return currentPos[0] >= sample.getLength();
	}

	@Override
	public void calculateBuffer() {
		float[] frame = new float[this.sample.getNumChannels()];
		for ( int i = 0; i < this.bufferSize; i++ ) {
				
			for ( int j = 0; j < this.channelCount; j++ ) {
				int sampleChannel = j % this.sample.getNumChannels();
				sample.getFrameLinear(currentPos[j], frame );
				
				this.bufOut[j][i] = frame[sampleChannel];
				
				if ( prevValues[j] < 0 && frame[sampleChannel] >= 0 || prevValues[j] > 0 && frame[sampleChannel] <= 0 ) {
				
					
					if ( repeatCounts[j] >= stretch ) {
						lastCrossings[j] = currentPos[j];
						repeatCounts[j] = 0;	
					} else {
						currentPos[j] = lastCrossings[j];
						repeatCounts[j] ++;			
					}

				} //if

				prevValues[j] = frame[sampleChannel];
				
				currentPos[j] += pitch*msPerSample;
			} //for
			
		} //for
	
	} //

	@Override
	public String textDisplay() {
		return "StretchDemoSample " + this.getId() + " - progress = " + currentPos[0]/sample.getLength();
	}
	
	
	
	
}
