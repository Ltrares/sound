package com.luketrares.beadstest;

import java.util.concurrent.ThreadLocalRandom;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;

public class DemoSampleSample extends DemoElement {

	Sample sample;
	Sample multSample;
	boolean reverse = false;
	int channels = 2;
	float sampleRate;

	double currentPos;
	double msPerSample;
	
	double delayBeforeStart;
	
	int sampleChannels;
	float[] frame;
	float[] multFrame;
	float[] pMultFrame;
	
	public DemoSampleSample(AudioContext paramAudioContext, int channels, Sample sample, Sample multSample ) {
		super(paramAudioContext, channels);
		this.sample = sample;
		this.multSample = multSample;
		this.msPerSample = 1000.0/paramAudioContext.getSampleRate();
		this.channels = channels;
		this.volume = 0.8f + (float)ThreadLocalRandom.current().nextFloat()*0.4f;
		this.reverse = ThreadLocalRandom.current().nextBoolean();
		
		this.pitch = 0.5f + 1.75f*ThreadLocalRandom.current().nextFloat(); //(float)0.5*ThreadLocalRandom.current().nextInt(3);
		this.frame = new float[sample.getNumChannels()];
		this.multFrame = new float[multSample.getNumChannels()];
		this.pMultFrame = new float[multSample.getNumChannels()];
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
			
			if ( !reverse ) sample.getFrameLinear( currentPos, frame );
			else sample.getFrameLinear( sample.getLength() - currentPos, frame );

			double multSamplePos = currentPos;
			while ( multSamplePos >= multSample.getLength() ) {
				multSamplePos -= multSample.getLength();
			} //
			
			multSample.getFrameNoInterp( multSamplePos, multFrame );
			
			for ( int j = 0; j < multSample.getNumChannels(); j++ ) {
				pMultFrame[j] +=  0.01*Math.abs(multFrame[j]);
				pMultFrame[j] /= 1.01;
			} //
			
			
			for ( int j = 0; j < this.channels; j++ ) {
				int sampleChannel = j % sample.getNumChannels();				
				int multSampleChannel = sampleChannel % multSample.getNumChannels();
				this.bufOut[j][i] = this.pan[j]*this.volume*frame[sampleChannel]*pMultFrame[multSampleChannel];
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
