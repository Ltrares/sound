package com.luketrares.sequences;

import com.luketrares.beadstest.ClasspathSampleManager;
import com.luketrares.beadstest.DemoBeat;
import com.luketrares.beadstest.DemoSequencer;
import com.luketrares.beadstest.DemoTrack;

import net.beadsproject.beads.data.SampleManager;

public class jungle_mystery {

	public DemoSequencer createTracks(DemoSequencer sequencer, ClasspathSampleManager sampleManager) {
		if (!sequencer.hasTrack("drum0")) {
			//sequencer.addTrack(new DemoTrack().name("drum0").sample(sampleManager.sample("drums/14_Kick_13_180_SP.wav")).pitch(1.0).volume(1.0).measureCount(1).measureSize(4).bpm(240)
			sequencer.addTrack(new DemoTrack().name("drum0").sample(sampleManager.sample("drums/DX_15_Ride_01_SP.wav")).pitch(1.0).volume(1.0).measureCount(1).measureSize(4).bpm(240)
					.addBeat(new DemoBeat().location(0, 0).pitch(1.0).volume(0.2))
					.addBeat(new DemoBeat().location(0, 2).pitch(1.0).volume(0.5))
					.addBeat(new DemoBeat().location(0, 3).pitch(1.0).volume(0.3))
					.ignoreSequencerPitch()
					.tags( "percussion" )
					);
		}

		if (!sequencer.hasTrack("rasp")) {
			//sequencer.addTrack(new DemoTrack().name("drum0").sample(sampleManager.sample("drums/14_Kick_13_180_SP.wav")).pitch(1.0).volume(1.0).measureCount(1).measureSize(4).bpm(240)
			sequencer.addTrack(new DemoTrack().name("rasp").sample(sampleManager.sample("audio/Rasp_A_02_341.wav")).pitch(1.0).volume(1.0).measureCount(4).measureSize(4).bpm(240)
					.addBeat(new DemoBeat().location(0,3).pitch(1.0).volume(0.4))
					.addBeat(new DemoBeat().location(1, 1).pitch(1.0).volume(0.3))
					.addBeat(new DemoBeat().location(2, 3).pitch(1.0).volume(0.43))			
					.tags( "percussion" )
					
					.ignoreSequencerPitch()
					);
		}
		
		
		if ( !sequencer.hasTrack("ping")) {
			sequencer.addTrack(new DemoTrack()
					.name( "ping" )
					.sample( sampleManager.sample( "audio/F_Synth_Perc_Plain_01_321_SP.wav"))
					//.paused()
					.pitch( 440.0/sampleManager.getFrequency( "audio/F_Synth_Perc_Plain_01_321_SP.wav").getFrequencyOverall(0) )
					.volume(1.0)
					.measureCount(4)
					.measureSize(4)
					.bpm(240)
					.addBeat(new DemoBeat().location(0, 1).pitch(1.0).volume(1.0))
					.addBeat(new DemoBeat().location(0, 3).pitch(1.5).volume(1.0))
					.addBeat(new DemoBeat().location(1, 1).pitch(0.5).volume(0.7))
					.addBeat(new DemoBeat().location(1, 2).pitch(0.8).volume(0.7))
					.addBeat(new DemoBeat().location(1, 3).pitch(1.5).volume(1.1))
					.addBeat(new DemoBeat().location(2, 1).pitch(0.8).volume(0.7))
					.addBeat(new DemoBeat().location(2, 3).pitch(0.8).volume(0.5))
					.addBeat(new DemoBeat().location(3, 2).pitch(1.0).volume(0.8))
					.addBeat(new DemoBeat().location(3, 3).pitch(1.5).volume(0.8))
					.tags( "short" )
					);			
		}
		
		
		if ( !sequencer.hasTrack("bass")) {
			sequencer.addTrack(new DemoTrack()
					.name("bass")
					.sample( sampleManager.sample( "audio/Ab_LFCelloShortTAPE_SP_01_376.wav"))
					.pitch( 440.0/sampleManager.getFrequency("audio/Ab_LFCelloShortTAPE_SP_01_376.wav").getFrequencyOverall(0))
					.volume(1.0)
					.measureCount(2)
					.measureSize(4)
					.bpm(240)
					.addBeat(new DemoBeat().location(0, 1).pitch(1.0).volume(0.6))
					.addBeat(new DemoBeat().location(0, 3).pitch(0.5).volume(0.7))
					.addBeat(new DemoBeat().location(1, 1).pitch(1.0).volume(0.8))
					.addBeat(new DemoBeat().location(1, 2).pitch(0.75).volume(0.7))
					.addBeat(new DemoBeat().location(1, 4).pitch(0.5).volume(0.7))
					.tags( "short", "bass" )
					);
		}
	
		if ( !sequencer.hasTrack("choir")) {
			sequencer.addTrack(new DemoTrack()
					.name("choir")
					.tunedSample( sampleManager.sample( "audio/SoulChoirVox_01_139_SP.wav") )
					.pitch( 440.0/sampleManager.getFrequency( "audio/SoulChoirVox_01_139_SP.wav").getFrequencyOverall(0) )
					.volume(1.0)
					.measureCount(36)
					.measureSize(4)
					.bpm(240)
					.addBeat( new DemoBeat().location(1, 0).pitch(1.0).volume(0.5) )
					.addBeat( new DemoBeat().location(7, 0).pitch(0.8).volume(0.6) )
					.addBeat( new DemoBeat().location(13, 0).pitch(0.5).volume(0.47) )
					.addBeat( new DemoBeat().location(19, 0).pitch(1.0).volume(0.6) )
					.addBeat( new DemoBeat().location(25, 0).pitch(1.333333).volume(0.65) )
					.addBeat( new DemoBeat().location(31, 0).pitch(0.888888).volume(0.7) )
					.tags( "long", "background-vocal" )
					);
//			
//			
//			
//		
		}
		
		return sequencer;
	}
	
	
}
