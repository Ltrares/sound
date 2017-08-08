package com.luketrares.beadstest;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.ugens.Clock;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.GranularSamplePlayer;
import net.beadsproject.beads.ugens.SamplePlayer;
import net.beadsproject.beads.ugens.Static;

public class Lesson08_Granulation {

	public static void main(String[] args) {

		AudioContext ac;

		ac = new AudioContext();

		//String audioFile = "audio/87_Bm_PostGuitar_01_483.wav";
		//String audioFile = "audio/90_D#_Humphrey_SP_313_33.wav";
		//String audioFile = "audio/C_AncientGuitar_V1_498.wav";
		//String audioFile = "audio/GrandPianoLong_47_A#5_78_SP.wav";
		//String audioFile = "audio/D_RomanticSpace_01_334_SP.wav";
		//String audioFile = "audio/20_G_Keys_05_112_SP.wav";
		//String audioFile = "audio/F#_Bowed_01_136_SP.wav";
		//String audioFile = "audio/E_Pianodoctor_SP_57_01.wav";
		String audioFile = "audio/35_G#_Skyfire_01_100_SP.wav";
		
		float rate = 1.0f;
		
		Clock clock = new Clock(ac,(float)SampleManager.sample(audioFile).getLength()/rate);

		clock.setTimerMode(true);

		final GranularSamplePlayer player = new GranularSamplePlayer(ac,
				SampleManager.sample(audioFile));

		final float[] pitches = { 1.0f/2.0f, 8.0f/15.0f, 2.0f/3.0f, 3.0f/4.0f, 4.0f/5.0f, 8.0f/9.0f, 1.0f, 16.0f/15.0f, 4.0f/3.0f, 3.0f/2.0f, 8.0f/5.0f, 16.0f/9.0f, 2.0f  };
		
		clock.addMessageListener(new Bead(){
			
			@Override
			protected void messageReceived(Bead bead) {
				
				if (!((Clock)bead).isBeat()) return;
				System.out.println( "clock tick beat = " + ((Clock)bead).isBeat() );
				
				player.setPitch( new Static(ac, pitches[ (int)(pitches.length*Math.random())]) );
			}
			
		});
		
		
		
		/*
		 * In lesson 4 we played back samples. This example is almost the same
		 * but uses GranularSamplePlayer instead of SamplePlayer. See some of
		 * the controls below.
		 */
		//String audioFile = "audio/D_RomanticSpace_01_334_SP.wav";
		//String audioFile = "audio/Gm_Piano_SP_84_01.wav";
		//String audioFile = "audio/20_G_Keys_05_112_SP.wav";
		
		/*
		 * Have some fun with the controls.
		 */
		// loop the sample at its end points
		player.setLoopType(SamplePlayer.LoopType.LOOP_FORWARDS);
		
		player.getLoopStartUGen().setValue(0);
		player.getLoopEndUGen().setValue(
				(float)SampleManager.sample(audioFile).getLength());
		
		
		
		
		// control the rate of grain firing
		//Envelope grainIntervalEnvelope = new Envelope(ac, 20);
		//grainIntervalEnvelope.addSegment(20, 10000);
		//player.setGrainInterval(new Static(ac, 33.0f));
		
		player.setGrainInterval( new Static(ac, 512.0f ));
		player.setGrainSize(new Static(ac, 1024.0f ) );
		
		//Envelope pitchEnvelope = new Envelope(ac,0.5f);
		//pitchEnvelope.addSegment(2.0f, 35000 );
		player.setPitch(new Static(ac,8.0f/9.0f));
		
		// control the playback rate
		//Envelope rateEnvelope = new Envelope(ac, 1);
		//rateEnvelope.addSegment( (float) 0.1, 15000 );
		//rateEnvelope.addSegment( (float) 1.2, 20000 );
		//rateEnvelope.addSegment(1, 5000);
		//rateEnvelope.addSegment(0, 5000);
		//rateEnvelope.addSegment(0, 2000);
		//rateEnvelope.addSegment(-0.1f, 2000);
		player.setRate( new Static(ac, rate ) );
		// a bit of noise can be nice
		//player.getRandomnessUGen().setValue(0.01f);
		/*
		 * And as before...
		 */
		Gain g = new Gain(ac, 2, 0.75f);
		g.addInput(player);
		ac.out.addInput(g);
		ac.out.addDependent(clock);
		ac.start();

	}

}
