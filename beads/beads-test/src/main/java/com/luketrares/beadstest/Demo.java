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

import net.beadsproject.beads.analysis.featureextractors.FFT;
import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.data.SampleManager;

public class Demo extends DemoElement {

	double frames = 0;
	double ft = System.nanoTime();

	double cpitch = 440;
	
	long lastTime = System.nanoTime();
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
	
	String[] downBeatSamples = new String[]{ "drums2/Synthkick_02.wav", "drums2/Synthkick_05.wav" };
	
	public Demo(AudioContext arg0, int channels) {
		super(arg0, channels);
		File f = new File(".");
		System.out.println(f.getAbsolutePath());
		samples = getResources("audio");
		drums = getResources("drums2");
		longSamples = getResources("longs");
		
		sampleManager.preload( samples );
		
		System.out.println(drums.size() + " drum sounds available");
		this.channels = channels;
		this.chavg = new float[channels];
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

	private InputStream getResourceAsStream(String resource) {
		final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);

		System.out.println(resource + " in: " + in);
		return in == null ? Demo.class.getResourceAsStream(resource) : in;
	}

	@Override
	public void calculateBuffer() {
		updateDemo();

		for (int i = 0; i < this.channels; i++) {
			for (int j = 0; j < this.bufferSize; j++) {
				this.bufOut[i][j] = 0.0f;
			} // for
		} // for

		float max = 1.0f;
		for (DemoElement de : this.demoElements) {
			de.calculateBuffer();

			for (int i = 0; i < de.getOuts(); i++) {
				for (int j = 0; j < this.bufferSize; j++) {
					this.bufOut[i][j] += de.getOutBuffer(i)[j];

					if (Math.abs(this.bufOut[i][j]) > max)
						max = Math.abs(this.bufOut[i][j]);
				} // for j

			} //for i
		} //for de

		// max += 0.1f;
		// max *= max;
		// max *= 1.1;

		if (max > vmax) {
			vmax = max;
		} else {
			vmax -= 0.1f * this.bufferSize / this.getContext().getSampleRate();
		}

		double weight = 0.5;
		double totalWeight = 1.2 * (1.0 + weight);
		for (int i = 0; i < this.channels; ++i) {
			for (int k = 0; k < this.bufferSize; ++k) {
				chavg[i] += weight * this.bufOut[i][k];
				chavg[i] /= totalWeight;
				// this.bufOut[i][k] = (float) Math.tanh(chavg[i]/max);
				this.bufOut[i][k] = chavg[i] / vmax;

				if (chavg[i] >= 0.90 * vmax) {
					System.out.println("***spiked***");
				} // if
			}
		}

	}

	// @Override
	// public void update() {
	// updateDemo();
	// super.update();
	// }

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

		if ((System.currentTimeMillis() - lastBeat) >= beatTime && this.demoElements.size() < this.maxDemoElements) {
			beatCount++;
			long currentBeat = (beatCount % measureCount);

			
			System.out.println( "beat " + (currentBeat+1) + "/" + this.measureCount + ": " + this.downBeats.contains( currentBeat ) );
//			if ((currentBeat == 0L) && (Math.random() < 0.2 )) {
//				ni = new DarkBell(this.getContext(), this.channels);
//				addElement(ni);
//			}
			
			if ( currentBeat == 0L && Math.random() < 0.1 ) {
				double pc = 1.0;
				
				
				switch( ThreadLocalRandom.current().nextInt(4) ) {
				case 0:
					pc = 1.5;
					break;
				case 1:
					pc = 0.666666667;
					break;
				case 2:
					pc = 1.333333333;
					break;
				case 3:
					pc = 0.75;
					break;
				} //
				this.cpitch *= pc;
				
				for ( DemoElement de : this.demoElements ) {
					if ( de instanceof Recorder ) {
						de.setPitch( (float)(de.getPitch() *pc) );
					} //
				} //
				System.out.println( "pitch change: " + cpitch );
			} //
			
			
//			if ((currentBeat == 0L) && (Math.random() < 0.4) ) {
//				System.out.println( "buzzy" );
//				String sampleFile =samples.get(ThreadLocalRandom.current().nextInt(samples.size()));
//
//				Sample sample = sampleManager.sample(sampleFile);
//
//				if (sample != null) {
//					ni = new TunedGrainDemoSample(this.getContext(), 2, sample, sampleManager.getFrequency(sampleFile), cpitch );
//					//ni = new GrainDemoSample(this.getContext(), 2, sample );
//					//((DemoSample) ni).setPitch(0.5 + ThreadLocalRandom.current().nextInt(3) * 0.5);
//					addElement(ni);
//				} else {
//					System.out.println( "null sample " + sampleFile );
//				}
//			}
			
			

			if ((currentBeat == 0L) && (Math.random() < 0.2 )) {
				ni = new DarkBell(this.getContext(), this.channels);
				System.out.println("new dark bell? " + ni.textDisplay());
				addElement(ni);
			}

			if ((currentBeat == 0) && (Math.random() < 0.025) && longSamples.size() > 0) {
				String sampleFile = this.longSamples.get(ThreadLocalRandom.current().nextInt(this.longSamples.size()));

				Sample sample = sampleManager.sample(sampleFile);

				if (sample != null) {
					ni = new TunedDemoSample(this.getContext(), 2, sample, sampleManager.getFrequency(sampleFile), cpitch );
					((DemoSample) ni).setPitch(0.5 + ThreadLocalRandom.current().nextInt(3) * 0.5);
					addElement(ni);
				}
			}
			//
			//
			if ((downBeats.contains(currentBeat)) && (Math.random() < 0.05) && (samples.size() > 0)) {

				String sampleFile = samples.get(ThreadLocalRandom.current().nextInt(samples.size()));

				Sample sample = sampleManager.sample(sampleFile);

				if (sample != null) {
					ni = new TunedDemoSample(this.getContext(), 2, sample, sampleManager.getFrequency(sampleFile), cpitch );
					((DemoSample) ni).setPitch(0.5 + ThreadLocalRandom.current().nextInt(3) * 0.5);
					addElement(ni);
				}
			}

			if (Math.random() < 0.35 && drums.size() > 0) {
				String sampleFile = drums.get(ThreadLocalRandom.current().nextInt(drums.size()));

				if ( downBeats.contains(currentBeat) ) {
					sampleFile = this.downBeatSamples[ThreadLocalRandom.current().nextInt(this.downBeatSamples.length)];
				}
				

				Sample sample = sampleManager.sample(sampleFile);

				if (sample != null) {
					ni = new DemoSample(this.getContext(), 2, sample);
					((DemoSample) ni).setReverse(false);
					((DemoSample) ni).setPitch(1.0);
					if (downBeats.contains(currentBeat)) {
						float volume = ((DemoSample) ni).getVolume();
						((DemoSample) ni).setVolume(volume * 1.25f);
					}
					addElement(ni);
				}

			}

			if (Math.random() < 0.35 && this.demoElements.size() > 0 && this.demoElements.size() < (this.maxDemoElements - 5)) {
				ni = new Recorder(this.getContext(), this.channels, recorderBeatMult * beatTime);
				ni.setPitch(1.0f);
				if (ThreadLocalRandom.current().nextDouble() < 0.1)
					ni.addInputElement(this);
				else
					ni.addInputElement(this.demoElements.get(this.demoElements.size() - 1));
				addElement(ni);
			} // if

			lastBeat = System.currentTimeMillis();

		}

		Iterator<DemoElement> it = this.demoElements.iterator();

		while (it.hasNext()) {
			DemoElement el = it.next();

			if (el.isDone()) {
				//System.out.println("ending demo sound " + el.textDisplay());
				el.kill();
				it.remove();
				continue;
			} // if

		} // while
	}

	private void addElement(DemoElement ni) {
		if (ni != null) {
			//System.out.println("starting new demo sound " + ni.textDisplay() + " " + this.demoElements.size());
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
