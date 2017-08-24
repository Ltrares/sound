package com.luketrares.beadstest;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public class HyperbolicGain extends UGen {

	float gain;
	public HyperbolicGain(AudioContext paramAudioContext, int paramInt, float gain ) {
		super(paramAudioContext, paramInt, paramInt );
		this.gain = gain;
	}
	
	@Override
	public void calculateBuffer() {
		
		float max = 1.0f;
		
		for ( int i = 0; i < this.bufferSize; i++ ) {
			for ( int j = 0; j < this.ins; j++ ) {
				if ( Math.abs(bufIn[j][i]) > max ) max = bufIn[j][i]; 
			} //for int j
		} //for int i
		
		
		for ( int i = 0; i < this.bufferSize; i++ ) {
			for ( int j = 0; j < this.outs; j++ ) {
				bufOut[j][i] = gain*bufIn[j][i]/max;
			} //for int j
		} //for int i
		
		
		
	}


	
	
	
}
