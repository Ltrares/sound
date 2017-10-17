package com.luketrares.beadstest;

import java.util.concurrent.ThreadLocalRandom;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;

public class DemoSample extends DemoElement {

	Sample sample;
	boolean reverse = false;
	int channels = 2;
	float sampleRate;

	double currentPos;
	double msPerSample;
	
	double delayBeforeStart;
	
	int sampleChannels;
	float[] frame;
	public DemoSample(AudioContext paramAudioContext, int channels, Sample sample ) {
		super(paramAudioContext, channels);
		this.sample = sample;
		this.msPerSample = 1000.0/paramAudioContext.getSampleRate();
		this.channels = channels;
		this.volume = 0.8f + (float)ThreadLocalRandom.current().nextFloat()*0.4f;
		this.reverse = ThreadLocalRandom.current().nextBoolean();
		
		this.pitch = 0.5f + 1.75f*ThreadLocalRandom.current().nextFloat(); //(float)0.5*ThreadLocalRandom.current().nextInt(3);
		this.frame = new float[sample.getNumChannels()];
	}

	@Override
	public boolean isDone() {
		
		return currentPos >= this.sample.getLength();
	}

	@Override
	public void calculateBuffer() {

		for ( int i = 0; i < this.bufferSize; i++ ) {
			if ( this.delayBeforeStart > 0 ) {
				this.delayBeforeStart -= msPerSample;
				continue;
			}
			//this.pitch = (float)calcPitch( currentPos );
			
			if ( !reverse ) sample.getFrameLinear( currentPos, frame );
			else sample.getFrameLinear( sample.getLength() - currentPos, frame );
			
			for ( int j = 0; j < this.channels; j++ ) {
				int sampleChannel = j % sample.getNumChannels();				
				this.bufOut[j][i] = this.pan[j]*this.volume*frame[sampleChannel];
			} //for
		
			currentPos += pitch*msPerSample;

		} //for i
		
	}

	@Override
	public String textDisplay() {
		return super.textDisplay() + " " + sample.getSimpleName() + " length=" + this.sample.getLength() + " progress=" + this.currentPos/this.sample.getLength();
	}

	public void setReverse(boolean b) {
		this.reverse = b;
	}

	public void setPitch(double d) {
		this.pitch = (float)d;
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public double getDelayBeforeStart() {
		return delayBeforeStart;
	}

	public void setDelayBeforeStart(double delayBeforeStart) {
		this.delayBeforeStart = delayBeforeStart;
	}
	
	
	
}
