package com.luketrares.beadstest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.sound.sampled.LineUnavailableException;

import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.SampleManager;
import net.beadsproject.beads.ugens.Gain;

public class Demo extends DemoElement {

	double frames = 0;
	double ft = System.nanoTime();

	double cpitch = 440;

	long lastTime;
	List<DemoElement> demoElements = new ArrayList<>();
	int channels;
	// "audio/Train_pass_by_Inadera_Japan_484.wav",
	List<String> samples;
	float[] chavg;
	List<String> drums;
	List<String> longSamples;

	int maxDemoElements = 30;
	ClasspathSampleManager sampleManager = new ClasspathSampleManager();
	float vmax = 0.0f;
	int beatTime = 125;
	long beatCount = 0;
	int measureCount = 7;
	int recorderBeatMult = 1;
	Set<Long> downBeats = new HashSet<Long>(Arrays.asList(0L, 2L, 4L));

	Thread updateThread;

	DemoSequencer sequencer = null;
	public static void main(String[] args) throws LineUnavailableException {
		AudioContext ac;

		ac = new AudioContext();
		System.out.println( "audio context sample rate: " + ac.getSampleRate() );
		Demo demo = new Demo(ac,2);
		
		Gain g = new Gain(ac, 2, 1.0f);
		g.addInput(demo);
		ac.out.addInput(g);
		ac.start();
	
//		JavaSoundPlayer jsp = new JavaSoundPlayer( (int)ac.getSampleRate(), demo.getChannelCount(), ac.getAudioFormat().bitDepth/8, ac.getBufferSize()  );
		
		
//		while ( true ) {
//			float[][] sound = new float[demo.getChannelCount()][];
//			if ( jsp.ready( ac.getBufferSize() ) ) {
//				demo.calculateBuffer();
//				for ( int i = 0; i < demo.getChannelCount(); i++ ) {
//					sound[i] = demo.getOutBuffer(i);
//				} //
//				//System.out.println( "updated" );
//				jsp.play( sound );
//			} else {
//				try {
//					Thread.sleep(1);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
		
		
	}
	public Demo(AudioContext arg0, int channels) {
		super(arg0, channels);
		File f = new File(".");
		System.out.println(f.getAbsolutePath());
		samples = getResources("audio");
		drums = getResources("drums2");
		longSamples = getResources("longs");

		sampleManager.preload(samples);
		sampleManager.preload(longSamples);

		System.out.println(drums.size() + " drum sounds available");
		this.channels = channels;
		this.chavg = new float[channels];

		sequencer = new DemoSequencer(this.context, this.channelCount);

		this.demoElements.add(sequencer);
		this.lastTime = System.nanoTime();
		
		
		Sine sine = new Sine(this.context,this.channelCount,220);
		this.demoElements.add(sine);

	}

	private static List<String> getResources(String path) {
		InputStream is = Demo.class.getClassLoader().getResourceAsStream(path);
		List<String> results = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line = null;

			while ((line = br.readLine()) != null) {
				results.add(path + "/" + line);
			} // while

		} catch (IOException e) {
		}

		return results;
	}

//	private InputStream getResourceAsStream(String resource) {
//		final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
//
//		System.out.println(resource + " in: " + in);
//		return in == null ? Demo.class.getResourceAsStream(resource) : in;
//	}

	@Override
	public void calculateBuffer() {
		updateDemo();

		for (int i = 0; i < this.channels; i++) {
			for (int j = 0; j < this.bufferSize; j++) {
				this.bufOut[i][j] = 0.0f;
			} // for
		} // for

		float max = 1.1f;
		for (DemoElement de : this.demoElements) {
			de.calculateBuffer();

			for (int i = 0; i < de.getOuts(); i++) {
				for (int j = 0; j < this.bufferSize; j++) {
					this.bufOut[i][j] += de.getOutBuffer(i)[j];
		
					if (Math.abs(this.bufOut[i][j]) > max)
						max = Math.abs(this.bufOut[i][j]);
				} // for j

			} // for i

		} // for de


		for (int i = 0; i < this.channels; ++i) {
			for (int k = 0; k < this.bufferSize; ++k) {
				this.bufOut[i][k] = (float) Math.tanh(bufOut[i][k]/2.0);
			}
		}

	}

	private long lastBeat = 0;

	private void updateDemo() {
		if (this.isPaused())
			return;

		long diff = System.nanoTime() - lastTime;
		lastTime += diff;
		if (lastTime - ft >= 1000d * 1000000d) {
			// System.out.println("frames " + frames);
			frames = 0;
			ft = lastTime;
		} // if
		frames++;
		double time = diff / 1000000d;

		
		
		DemoElement ni = null;

//		if (Math.random() < 0.002 ) {
//			ni = new DemoSample( this.getContext(), this.channelCount, sampleManager.sample( "longs/Atmosphere_10_SP.wav") );
//			ni.setPitch(1.0f);
//			ni.setVolume(0.7f);
//			((DemoSample)ni).setReverse(false);
//		}
//		addElement(ni);
		
//		

		
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
					.paused()
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
		
		
		
//		if ( Math.random() < 0.001 ) {
//			//DemoTrack track = sequencer.getTrack( "ping" );
//			
//			double newPitch = sequencer.getPitch();
//			if ( Math.random() < 0.5 && newPitch < 3 ) newPitch *= 1.5;
//			else if ( newPitch >= 0.25 ) newPitch *= 0.666666667;
//			sequencer.setPitch( (float)newPitch );
//			
//			
//		}
		

		sequencer.update(time, this );
		//sequencer.update(time);


		Iterator<DemoElement> it = this.demoElements.iterator();

		while (it.hasNext()) {
			DemoElement el = it.next();

			if (el.isDone()) {
				// System.out.println("ending demo sound " + el.textDisplay());
				el.kill();
				it.remove();
				continue;
			} // if

		} // while
	}

	public void addElement(DemoElement ni) {
		if (ni != null) {
			System.out.println("starting new demo sound " + ni.textDisplay()
			 + " " + this.demoElements.size());
			this.demoElements.add(ni);

			if (Math.random() < 0.1) {
				System.out.println();
				for (DemoElement el : this.demoElements) {
					System.out.println(el.textDisplay());
				}
				System.out.println();
			} //

		} // if
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

}
