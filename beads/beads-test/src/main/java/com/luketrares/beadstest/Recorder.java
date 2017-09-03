package com.luketrares.beadstest;

import java.util.concurrent.ThreadLocalRandom;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Sample;

public class Recorder extends DemoElement {
	//float[][] recordBuffer;
	int repeatCount = 0;
	int currentRepeat = 0;
	int channelCount = 1;
	int outputBuffersize = 0;
	double duration = 0;
	//int recordBufferSize = 0;
	boolean inPlayback = false;
	boolean reverse = false;
	Sample sample;
	double msPerSample = 0;
	double currentPos = 0;
	double currentSamplePos = 0;
	int recordPos = 0; 
	
	int noiseCount = 0;

	float noiseCutoff = 0.1f;
	private double noiseLevel = 0.0d;
	boolean alternate = false;
	
	public Recorder(AudioContext audioContext, int channelCount, int beatDuration ) {
		super(audioContext, channelCount);
		// buffer = new
		// float[channelCount][(int)(duration*audioContext.getSampleRate())];
		this.repeatCount = ThreadLocalRandom.current().nextInt(1, 29);

		this.channelCount = channelCount;
		// this.bufferSize = bufferSize;
		this.outputBuffersize = bufferSize;

		//available durations are multiples of 250*
		
		this.duration = beatDuration *ThreadLocalRandom.current().nextInt( 1, 20 ); //ThreadLocalRandom.current().nextDouble()*17000;
		this.sample = new Sample( duration, channelCount, audioContext.getSampleRate() );
		
		this.reverse = ThreadLocalRandom.current().nextBoolean();
		this.msPerSample = 1000.0/audioContext.getSampleRate();
	
		this.pitch = 0.5f + ThreadLocalRandom.current().nextFloat()*1.75f; // 0.5f + (float)0.5*ThreadLocalRandom.current().nextInt(3);

		this.volume = 0.7f + ThreadLocalRandom.current().nextFloat()*0.25f;
		
		this.alternate = ThreadLocalRandom.current().nextBoolean();
	}

	@Override
	public String textDisplay() {
		if ( this.inPlayback ) {
			return "Recorder " + getId() + ": length = " + this.duration + " repeat = " + this.currentRepeat + "/" + this.repeatCount + " reverse = " + this.reverse;
		} //
		
		return "Recorder " + getId() + ": recording length = " + this.duration;
	}

	@Override
	public void calculateBuffer() {
		
		if ( recordPos < this.sample.getNumFrames() ) {
			float[] frame = new float[channelCount];
			float[] pframe = new float[channelCount];
			
			int amountToRecord = (int) Math.min( this.bufferSize, this.sample.getNumFrames() - recordPos );
			for (int i = 0; i < amountToRecord; i++) {
				DemoElement element = this.getInputElements().get(0);
				
				for (int j = 0; j < element.getOuts(); j++) {
					frame[j] = element.getOutBuffer(j)[i];
					if ( Math.abs( frame[j] - pframe[j] ) > noiseCutoff ) {
						noiseCount ++;
					} //if
					
				} //for

				this.sample.putFrame(recordPos, frame);
				
				float[] tmp = pframe;
				pframe = frame;
				frame = tmp;
				
				recordPos ++;
				
			} //
			return;
		} //if

		this.noiseLevel = (double)this.noiseCount/(this.channelCount*this.sample.getNumFrames());
		if ( !inPlayback ) {
		//	System.out.println( this.textDisplay() + " in playback - noiseLevel = " + String.format( "%6.5f", noiseLevel ) );
			inPlayback = true;
			
	
			
		} //if
		
		float[] frame = new float[this.channelCount];
		for (int i = 0; i < this.outputBuffersize; i++) {	
			getNextFrame( frame );
			float env = (float) QuickMath.sinFourWindow( currentPos/ sample.getLength() );
			for (int j = 0; j < channelCount; j++) {
				bufOut[j][i] = this.pan[j]*env*volume*frame[j];
			} //for
		} //for

	}

	private float[] zeroFrame(float[] frame) {
		for ( int i = 0; i < frame.length; i++ ) {
			frame[i] = 0.0f;
		} //for
		return frame;
	}

	private float[] getNextFrame( float[] frame ) {
		if (isDone()) return zeroFrame(frame);
	
		if (currentPos >= this.sample.getLength() ) {
			if ( alternate ) reverse = !reverse;
			this.currentRepeat++;
			currentPos = 0;
			currentSamplePos = 0;
			//System.out.println( this.textDisplay() + " playback " + currentRepeat + "/" + repeatCount + " noise = " + String.format( "%6.5f", noiseLevel ) );
			if (isDone()) return zeroFrame(frame);
		} //
	
		if ( currentSamplePos < this.sample.getLength() ) {
			if ( reverse )  this.sample.getFrameLinear( this.duration - currentPos, frame );
			else this.sample.getFrameLinear( currentPos, frame );
		} else {
			zeroFrame( frame );
		}
		currentPos += this.msPerSample;
		currentSamplePos += this.pitch*msPerSample;
		
		return frame;
		
	}
	
	

	@Override
	public boolean isDone() {
		return currentRepeat >= repeatCount;
	}

}
