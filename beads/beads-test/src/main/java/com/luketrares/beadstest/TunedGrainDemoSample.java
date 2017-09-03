package com.luketrares.beadstest;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;

public class TunedGrainDemoSample extends GrainDemoSample {

	SampleFrequency sampleFreq;
	double[] freqFrame;
	double targetFrequency;
	
	double overallFrequency;
	
	double pf = 0.0;
	
	public TunedGrainDemoSample(AudioContext paramAudioContext, int channels, Sample sample, SampleFrequency sampleFreq, double targetFrequency ) {
		super(paramAudioContext, channels, sample);
		
		this.sampleFreq = sampleFreq;
	
		this.freqFrame = new double[sample.getNumChannels()];		
	
		
		this.overallFrequency = sampleFreq.getFrequencyOverall(0);
		
		this.targetFrequency = findNearestHarmonic( this.overallFrequency, targetFrequency );
			
	}

	@Override
	public String textDisplay() {
		return super.textDisplay() + " freq = " + this.overallFrequency;
	}

	@Override
	double calcPitch(double time) {		
		double freq = this.overallFrequency;
		if ( freq == 0 ) return 1.0;
		
		double correction = targetFrequency/freq;
		
		return correction;
	}
	
	
	
	
	

}
