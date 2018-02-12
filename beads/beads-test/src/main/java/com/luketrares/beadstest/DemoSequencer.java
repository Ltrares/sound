package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.beadsproject.beads.core.AudioContext;

public class DemoSequencer extends DemoElement  {

	public DemoSequencer(AudioContext paramAudioContext, int channels) {
		super(paramAudioContext, channels);
	}

	double rate = 1.0;

	Map<String, DemoTrack> tracks = new HashMap<String, DemoTrack>();
	private double ellapsedTime;

	private List<DemoElement> demoElements = new ArrayList<DemoElement>();
	
	public void addTrack(DemoTrack track) {
		track.sequencer(this);
		tracks.put(track.getName(), track);
	}

	public boolean hasTrack(String trackName) {
		return tracks.containsKey(trackName);
	}

	public DemoTrack getTrack(String trackName) {
		return tracks.get(trackName);
	}
	
	
	public void update(double updateTimeMillis, Demo demo) {

		for (DemoTrack track : tracks.values()) {
			List<DemoBeat> beats = track.update(updateTimeMillis * rate);

			if (beats == null || beats.size() <= 0)
				continue;

			System.out.println("Track " + track.getName() + " playing beat/s " + beats);

			for (DemoBeat beat : beats) {
				DemoSample sample;

				if (track.isTunedSample()) {
					sample = new GrainDemoSample(this.context, this.channelCount, ClasspathSampleManager.copySample(track.getSample()));
				} else {
					sample = new DemoSample(this.context, this.channelCount, ClasspathSampleManager.copySample(track.getSample()));
				} // else
				
				double timeCorrection = 1.0;
				if ( track.isUseTimeCorrection() ) {
					timeCorrection = track.getTimeCorrection();
				}
				if (track.isIgnoreSequencerPitch()) {
					sample.setPitch(timeCorrection * track.getPitch() * beat.getPitch());
				} else {
					sample.setPitch(this.pitch * timeCorrection * track.getPitch() * beat.getPitch());
				}

				if (track.isTunedSample())
					System.out.println("playing grain sample at pitch " + sample.getPitch());

				sample.setVolume((float) (this.volume * track.getVolume() * beat.getVolume()));
				sample.setReverse(false);
				sample.setDelayBeforeStart(beat.getDelay());
				//sample.setEffect( track.getEffect() );
				demo.addElement(sample);
				
				
			} //

		} // for

		this.ellapsedTime += updateTimeMillis * rate;

		Iterator<DemoElement> it = this.demoElements.iterator();

		while (it.hasNext()) {
			DemoElement el = it.next();

			if (el.isDone()) {
				System.out.println(el + " is done " + " at " + this.ellapsedTime);
				el.kill();
				it.remove();
				continue;
			} // if

		} // while

	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public void calculateBuffer() {
		double timeInterval = 1000.0 * this.bufferSize / this.getContext().getSampleRate();

		// this.update(timeInterval);
		// for (int i = 0; i < this.channelCount; i++) {
		// for (int j = 0; j < this.bufferSize; j++) {
		// this.bufOut[i][j] = 0.0f;
		// } // for
		// } // for

		// float max = 0.0f;
		// for (DemoElement de : this.demoElements) {
		// de.calculateBuffer();
		//
		// for (int i = 0; i < de.getOuts(); i++) {
		// for (int j = 0; j < this.bufferSize; j++) {
		// this.bufOut[i][j] += de.getOutBuffer(i)[j];
		//
		//
		//
		//// if (Math.abs(this.bufOut[i][j]) > max)
		//// max = Math.abs(this.bufOut[i][j]);
		// } // for j
		//
		// } // for i

		// } // for de
		// }//if
		//
		// if ( demoElements.size() > 0 && max == 0 ) {
		// System.out.println( "quiet noise: " + demoElements.size() );
		// }
		// if ( max == 0 ) System.out.println( "demo element 0 max was 0" );

		this.ellapsedTime += timeInterval;

		// System.out.println( "max: " + max + " ellapsedTime " +
		// this.ellapsedTime/1000.0 );

	}

	Map<String,DemoSequencerEvent> events = new HashMap<>();
	
	
//	@Override
//	public boolean loopCompleted(DemoTrack track) {
//		System.out.println( "loop completed " + track.getName() + " " + track.getLoopCount() + " " + track.isPaused() );
//		//DemoSequencerEvent event = events.get( eventId( track.getName(), track.getLoopCount() ) );
//		//if ( event == null ) return false;
//		//event.runEvent(this);
//		return true;
//	}


	private String eventId(String name, Integer loopCount) {
		return name + "_" + loopCount;
	}





}
