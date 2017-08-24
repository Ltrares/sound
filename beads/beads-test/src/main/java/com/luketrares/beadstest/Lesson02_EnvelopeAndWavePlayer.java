package com.luketrares.beadstest;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Buffer;
import net.beadsproject.beads.ugens.Envelope;
import net.beadsproject.beads.ugens.Gain;
import net.beadsproject.beads.ugens.WavePlayer;


public class Lesson02_EnvelopeAndWavePlayer {

	public static void main(String[] args) {

		AudioContext ac;

		ac = new AudioContext();
		System.out.println( "audio context sample rate: " + ac.getSampleRate() );
		Demo demo = new Demo(ac,2);
		demo.addInput(new DarkBell(ac,2));
		Gain g = new Gain(ac, 2, 0.5f);
		g.addInput(demo);
		ac.out.addInput(g);
		ac.start();
		
	}
}