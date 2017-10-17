package com.luketrares.beadstest;

import javax.sound.sampled.LineUnavailableException;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.WavePlayer;


public class Lesson02_EnvelopeAndWavePlayer {

	public static void main(String[] args) throws LineUnavailableException {
		AudioContext ac;

		ac = new AudioContext();
		System.out.println( "audio context sample rate: " + ac.getSampleRate() );
		Demo demo = new Demo(ac,2);
		
		//Gain g = new Gain(ac, 2, 1.0f);
		//g.addInput(demo);
		//ac.out.addInput(g);
		//ac.start();
	
		JavaSoundPlayer jsp = new JavaSoundPlayer( (int)ac.getSampleRate(), demo.getChannelCount(), ac.getAudioFormat().bitDepth/8, ac.getBufferSize()  );
		
		
		while ( true ) {
			float[][] sound = new float[demo.getChannelCount()][];
			if ( jsp.ready( ac.getBufferSize() ) ) {
				demo.calculateBuffer();
				for ( int i = 0; i < demo.getChannelCount(); i++ ) {
					sound[i] = demo.getOutBuffer(i);
				} //
				//System.out.println( "updated" );
				jsp.play( sound );
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
	}
}