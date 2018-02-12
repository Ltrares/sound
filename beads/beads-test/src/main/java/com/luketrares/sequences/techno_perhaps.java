package com.luketrares.sequences;

import com.luketrares.beadstest.ClasspathSampleManager;
import com.luketrares.beadstest.DemoBeat;
import com.luketrares.beadstest.DemoSequencer;
import com.luketrares.beadstest.DemoTrack;
import com.luketrares.beadstest.Pitch;
import com.luketrares.beadstest.ThisIsMySong;

import net.beadsproject.beads.data.SampleManager;

public class techno_perhaps {

	public DemoSequencer createTracks(DemoSequencer sequencer, ClasspathSampleManager sampleManager) {
//		if (!sequencer.hasTrack("drum0")) {
//			//sequencer.addTrack(new DemoTrack().name("drum0").sample(sampleManager.sample("drums/14_Kick_13_180_SP.wav")).pitch(1.0).volume(1.0).measureCount(1).measureSize(4).bpm(240)
//			sequencer.addTrack(new DemoTrack().name("drum0").sample(sampleManager.sample("drums/130_Drums_472_SP_ST_15.wav")).pitch(1.0).volume(1.0).measureCount(4).measureSize(4).bpm(130)
//					.addBeat(new DemoBeat().location(0, 0).pitch(1.0).volume(0.875))
//					.ignoreSequencerPitch()
//					.tags( "percussion" )
//					.pauseAfter(1.0)
//					.useTimeCorrection()
//					);
//		
//		
//			
//		
//		}
		
		if (!sequencer.hasTrack("drum1")) {
			sequencer.addTrack(new DemoTrack().name("drum1").sample(sampleManager.sample("drums/130_Drums_472_SP_ST_03.wav")).pitch(1.0).volume(1.0).measureCount(4).measureSize(4).bpm(130)
					.addBeat(new DemoBeat().location(0, 0).pitch(0.8).volume(0.75))
					.ignoreSequencerPitch()
					.tags( "percussion" )
//					.paused()
//					.unpauseAfter( 1.0 )
					.useTimeCorrection()
					);
		
		
			
		
		}

		
//		if (!sequencer.hasTrack("bass1")) {
//			sequencer.addTrack(new DemoTrack().name("bass1").sample(sampleManager.sample("audio/G_DoubleBass_SP_84_01.wav")).pitch(1.0).volume(0.8).measureCount(4).measureSize(4).bpm(130)
//					.addBeat(new DemoBeat().location(0, 0).pitch(Pitch.minus(3)).volume(0.75))
//					.addBeat(new DemoBeat().location(0, 1.85).pitch(Pitch.minus(0)).volume(0.85))
//					.addBeat(new DemoBeat().location(2, 0).pitch(Pitch.plus(3)).volume(0.75))
//					.addBeat(new DemoBeat().location(2, 1.85).pitch(Pitch.minus(0)).volume(0.85))
//					.addBeat(new DemoBeat().location(3, 2.85).pitch(Pitch.plus(4)).volume(0.85))
//					.ignoreSequencerPitch()
//					.tags( "bass" )
//					.paused()
//					.unpauseAfter( 1.0 )
//				
//					);
//		
//		}
		
		if ( !sequencer.hasTrack("song")) {
			DemoTrack track = new DemoTrack().name("song")
//					.sample(sampleManager.sample("audio/C_NiceOnTop_345_01.wav"))					
					.sample(sampleManager.sample("audio/GrandPianoLong_47_A#5_78_SP.wav"))
					.pitch(1.0).volume(0.8).measureCount(4).measureSize(4).bpm(130);
					//.paused()
					//.unpauseAfter( 1.0 );		
			ThisIsMySong song = new ThisIsMySong();
			song.generate(track, 234 );
			sequencer.addTrack( track );
			
//			.useTimeCorrection()
			
		}
		
//		
//		if (!sequencer.hasTrack("chord1")) {
//			sequencer.addTrack(new DemoTrack().name("chord1").sample(sampleManager.sample("audio/120_Em_Dusty_03_113_SP.wav")).pitch(1.0).volume(0.75).measureCount(2).measureSize(4).bpm(130)
//					.addBeat(new DemoBeat().location(1, 0).pitch(Pitch.plus(0)).volume(0.75))
//					//.ignoreSequencerPitch()
//					.tags( "chord" )
//					.paused()
//					.unpauseAfter( 4.0 )
//					.useTimeCorrection()
//				
//					);
//		
//		}

		
		
		//sequencer.after().track( "drum0").playsNTimes(1).then().playTrack( "drum1" );
		
		//sequencer.switchTrack( "drum0", 1, "drum1" );
		

		
//		if (!sequencer.hasTrack("rasp")) {
//			//sequencer.addTrack(new DemoTrack().name("drum0").sample(sampleManager.sample("drums/14_Kick_13_180_SP.wav")).pitch(1.0).volume(1.0).measureCount(1).measureSize(4).bpm(240)
//			sequencer.addTrack(new DemoTrack().name("rasp").sample(sampleManager.sample("audio/Rasp_A_02_341.wav")).pitch(1.0).volume(1.0).measureCount(4).measureSize(4).bpm(240)
//					.addBeat(new DemoBeat().location(0,3).pitch(1.0).volume(0.4))
//					.addBeat(new DemoBeat().location(1, 1).pitch(1.0).volume(0.3))
//					.addBeat(new DemoBeat().location(2, 3).pitch(1.0).volume(0.43))			
//					.tags( "percussion" )
//					
//					.ignoreSequencerPitch()
//					);
//		}
//		
//		
//		if ( !sequencer.hasTrack("ping")) {
//			sequencer.addTrack(new DemoTrack()
//					.name( "ping" )
//					.sample( sampleManager.sample( "audio/F_Synth_Perc_Plain_01_321_SP.wav"))
//					//.paused()
//					.pitch( 440.0/sampleManager.getFrequency( "audio/F_Synth_Perc_Plain_01_321_SP.wav").getFrequencyOverall(0) )
//					.volume(1.0)
//					.measureCount(4)
//					.measureSize(4)
//					.bpm(240)
//					.addBeat(new DemoBeat().location(0, 1).pitch(1.0).volume(1.0))
//					.addBeat(new DemoBeat().location(0, 3).pitch(1.5).volume(1.0))
//					.addBeat(new DemoBeat().location(1, 1).pitch(0.5).volume(0.7))
//					.addBeat(new DemoBeat().location(1, 2).pitch(0.8).volume(0.7))
//					.addBeat(new DemoBeat().location(1, 3).pitch(1.5).volume(1.1))
//					.addBeat(new DemoBeat().location(2, 1).pitch(0.8).volume(0.7))
//					.addBeat(new DemoBeat().location(2, 3).pitch(0.8).volume(0.5))
//					.addBeat(new DemoBeat().location(3, 2).pitch(1.0).volume(0.8))
//					.addBeat(new DemoBeat().location(3, 3).pitch(1.5).volume(0.8))
//					.tags( "short" )
//					);			
//		}
//		
//		
//		if ( !sequencer.hasTrack("bass")) {
//			sequencer.addTrack(new DemoTrack()
//					.name("bass")
//					.sample( sampleManager.sample( "audio/Ab_LFCelloShortTAPE_SP_01_376.wav"))
//					.pitch( 440.0/sampleManager.getFrequency("audio/Ab_LFCelloShortTAPE_SP_01_376.wav").getFrequencyOverall(0))
//					.volume(1.0)
//					.measureCount(2)
//					.measureSize(4)
//					.bpm(240)
//					.addBeat(new DemoBeat().location(0, 1).pitch(1.0).volume(0.6))
//					.addBeat(new DemoBeat().location(0, 3).pitch(0.5).volume(0.7))
//					.addBeat(new DemoBeat().location(1, 1).pitch(1.0).volume(0.8))
//					.addBeat(new DemoBeat().location(1, 2).pitch(0.75).volume(0.7))
//					.addBeat(new DemoBeat().location(1, 4).pitch(0.5).volume(0.7))
//					.tags( "short", "bass" )
//					);
//		}
//	
//		if ( !sequencer.hasTrack("choir")) {
//			sequencer.addTrack(new DemoTrack()
//					.name("choir")
//					.tunedSample( sampleManager.sample( "audio/SoulChoirVox_01_139_SP.wav") )
//					.pitch( 440.0/sampleManager.getFrequency( "audio/SoulChoirVox_01_139_SP.wav").getFrequencyOverall(0) )
//					.volume(1.0)
//					.measureCount(36)
//					.measureSize(4)
//					.bpm(240)
//					.addBeat( new DemoBeat().location(1, 0).pitch(1.0).volume(0.5) )
//					.addBeat( new DemoBeat().location(7, 0).pitch(0.8).volume(0.6) )
//					.addBeat( new DemoBeat().location(13, 0).pitch(0.5).volume(0.47) )
//					.addBeat( new DemoBeat().location(19, 0).pitch(1.0).volume(0.6) )
//					.addBeat( new DemoBeat().location(25, 0).pitch(1.333333).volume(0.65) )
//					.addBeat( new DemoBeat().location(31, 0).pitch(0.888888).volume(0.7) )
//					.tags( "long", "background-vocal" )
//					);
////			
////			
////			
////		
//		}
		
		return sequencer;
	}
	
	
}
