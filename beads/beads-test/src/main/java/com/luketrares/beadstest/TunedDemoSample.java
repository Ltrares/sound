package com.luketrares.beadstest;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;

public class TunedDemoSample extends DemoSample {

	double overallFrequency;
	double targetFrequency;
	
	public TunedDemoSample(AudioContext paramAudioContext, int channels, Sample sample, SampleFrequency sampleFreq, double targetFrequency ) {
		super(paramAudioContext, channels, sample);
	
		this.overallFrequency = sampleFreq.getFrequencyOverall(0);
		
		this.targetFrequency = findNearestHarmonic( this.overallFrequency, targetFrequency );
	
		if ( this.overallFrequency != 0.0 ) setPitch( this.targetFrequency/this.overallFrequency );
		else setPitch( 1.0 );
		
	}

	



	
	
	
}
