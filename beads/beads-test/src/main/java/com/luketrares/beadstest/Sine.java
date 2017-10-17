package com.luketrares.beadstest;

import net.beadsproject.beads.core.AudioContext;

public class Sine extends DemoElement {
	
	double ellapsedTime = 0;
	double msPerSample;
	double frequency;
	
	
	public Sine(AudioContext paramAudioContext, int channels, double frequency ) {
		super(paramAudioContext, channels);
		this.msPerSample = 1000.0/paramAudioContext.getSampleRate();
		this.volume = 0.7f;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public void calculateBuffer() {
	
		double mult = 2.0*Math.PI/1000.0;
		
		for ( int i = 0; i < this.bufferSize; i++ ) {
			for ( int j = 0; j < this.channelCount; j++ ) {
				this.bufOut[j][i] = (float)Math.sin(mult*this.frequency*this.ellapsedTime);
			}
			ellapsedTime += this.msPerSample;			
		}		
		
		//if ( Math.random() < 0.01 ) System.out.println( "sin updated: " + this.ellapsedTime );
	}

}
