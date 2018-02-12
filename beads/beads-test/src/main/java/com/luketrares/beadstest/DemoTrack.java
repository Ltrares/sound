package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.beadsproject.beads.data.Sample;

public class DemoTrack {

	String name = "";
	Sample sample;
	double pitch = 1.0;
	double volume = 1.0;
	int measureCount = 1;
	int measureSize = 4;
	double beatTimeMillis;
	List<List<DemoBeat>> beats = new ArrayList<List<DemoBeat>>();
	private boolean ignoreSequencerPitch;

	double totalTime = 0.0;
	double ellapsedTime = 0.0;
	int currentIndex = 0;
	private boolean paused;
	private boolean tunedSample = false;
	
	int currentVariation = 0;
	int nextVariation = 0;
	
	int loopCount = 0;
	
	double timeCorrection = 1.0;
	boolean useTimeCorrection = false;
	
	Map<Double,List<DemoTrackEvent>> trackEvents = new HashMap<>();
	
	
	DemoSequencer sequencer;
	
	private Set<String> tags = new HashSet<String>();
		
	public DemoTrack() {
		this.beats.add(new ArrayList<DemoBeat>());
	}

	public DemoTrack name(String string) {
		this.name = string;
		return this;
	}

	public DemoTrack sample(Sample sample) {
		this.sample = sample;
		return this;
	}

	public DemoTrack tunedSample(Sample sample ) {
		this.sample = sample;
		this.tunedSample = true;
		return this;		
	}
	
	
	public DemoTrack pitch(double pitch) {
		this.pitch = pitch;
		return this;
	}

	public DemoTrack volume(double volume) {
		this.volume = volume;
		return this;
	}

	public DemoTrack measureCount(int measureCount) {
		this.measureCount = measureCount;
		return this;
	}

	public DemoTrack measureSize(int measureSize) {
		this.measureSize = measureSize;
		return this;
	}

	public DemoTrack bpm(int bpm) {
		this.beatTimeMillis = 1000.0 * 60.0 / bpm;
		return this;
	}

	public DemoTrack addBeat(DemoBeat beat) {
		this.beats.get(currentVariation).add(beat);

		sortBeats(currentVariation);
		return this;
	}

	public DemoTrack beats(DemoBeat... beats) {
		this.beats.clear();
		for (DemoBeat beat : beats) {
			this.beats.get(currentVariation).add(beat);
		} //
		sortBeats(currentVariation);
		return this;

	}

	public DemoTrack variation() {
		currentVariation++;
		nextVariation = currentVariation;
		this.beats.add(new ArrayList<DemoBeat>());
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Sample getSample() {
		return sample;
	}

	public void setSample(Sample sample) {
		this.sample = sample;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public int getMeasureCount() {
		return measureCount;
	}

	public void setMeasureCount(int measureCount) {
		this.measureCount = measureCount;
	}

	public int getMeasureSize() {
		return measureSize;
	}

	public void setMeasureSize(int measureSize) {
		this.measureSize = measureSize;
	}

	public double getBeatTimeMillis() {
		return beatTimeMillis;
	}

	public void setBeatTimeMillis(double beatTimeMillis) {
		this.beatTimeMillis = beatTimeMillis;
	}

	private void sortBeats(int variation) {
		Collections.sort(this.beats.get(variation), new Comparator<DemoBeat>() {
			@Override
			public int compare(DemoBeat arg0, DemoBeat arg1) {
				if (arg0 == null && arg1 == null)
					return 0;
				if (arg0 == null)
					return -1;
				if (arg1 == null)
					return 1;

				if (arg0.getMeasure() < arg1.getMeasure())
					return -1;
				if (arg0.getMeasure() > arg1.getMeasure())
					return 1;
				if (arg0.getBeatPosition() < arg1.getBeatPosition())
					return -1;
				if (arg0.getBeatPosition() > arg1.getBeatPosition())
					return 1;

				if (arg0.getVolume() < arg1.getVolume())
					return -1;
				if (arg0.getVolume() > arg1.getVolume())
					return 1;

				if (arg0.getPitch() < arg1.getPitch())
					return -1;
				if (arg0.getPitch() > arg1.getPitch())
					return 1;

				return 0;

			}
		});

	}

	public List<DemoBeat> update(double time) {
		double delay = 0;
		double currentTime = ellapsedTime;
		ellapsedTime += time;

		List<DemoBeat> results = null;
		double tt = getTrackTime();

		if ( currentTime == 0 ) {
			
			double sampleLength = this.sample.getLength();
			
			System.out.println( "expected track length " + tt + ", actual track length " + sampleLength );
			
			this.timeCorrection = sampleLength/tt;
			System.out.println( "track length correction = " + this.timeCorrection );
		} //
		
		while (currentTime <= ellapsedTime) {
			if (currentTime >= tt) {
				loopCount++;
				
				currentTime = 0;
				currentIndex = 0;
				ellapsedTime -= tt;

				this.currentVariation = this.nextVariation;
			
				
				System.out.println( this.getName() + " " + loopCount + " loops completed at " + this.totalTime );
				//sequencer.loopCompleted(this);
			} // if

			double trackEventIndex = getTrackEventIndex( loopCount, currentTime/tt );
			
			List<DemoTrackEvent> events = trackEvents.get(trackEventIndex);
			
			if ( events != null && events.size() > 0 ) {
				System.out.println( "running track event on track " + name + " at " + trackEventIndex + " (" + currentTime + ")" );
				for ( DemoTrackEvent event : events ) {
					event.run( sequencer, this );
				} //
			}
			
			
			if (currentIndex < this.beats.get(currentVariation).size() && getBeatTime(this.beats.get(currentVariation).get(currentIndex)) <= currentTime) {

				if (results == null)
					results = new ArrayList<DemoBeat>();

				DemoBeat cbeat = this.beats.get(currentVariation).get(currentIndex);
				cbeat.setDelay(delay);
	
				if (!paused) {
					results.add(cbeat);
					System.out.println( "playing " + this.getName() + " at " + currentTime + "/" + totalTime );
				} else {
					System.out.println( "skipping paused beat for track " + this.getName() );
				}
				currentIndex++;

			} //

			currentTime += 1.0;
			delay += 1.0;
			totalTime += 1.0;
		}

		return results;
	}

	private double getTrackEventIndex(int loopCount, double timeInLoop) {
		double result = loopCount + Math.floor(timeInLoop*1000)/1000.0;
		return result;
	}

	private double getBeatTime(DemoBeat demoBeat) {
		return (demoBeat.getMeasure() * this.measureSize + demoBeat.getBeatPosition()) * this.beatTimeMillis;
	}

	private double getTrackTime() {
		return this.measureCount * this.measureSize * this.beatTimeMillis;
	}

	public boolean isIgnoreSequencerPitch() {
		return ignoreSequencerPitch;
	}

	public void setIgnoreSequencerPitch(boolean ignoreSequencerPitch) {
		this.ignoreSequencerPitch = ignoreSequencerPitch;
	}

	public DemoTrack ignoreSequencerPitch() {
		this.ignoreSequencerPitch = true;
		return this;
	}

	public DemoTrack paused() {
		this.setPaused(true);
		return this;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public int getCurrentVariation() {
		return currentVariation;
	}

	public void setCurrentVariation(int currentVariation) {
		this.currentVariation = currentVariation;
		this.nextVariation = currentVariation;
	}

	public List<List<DemoBeat>> getBeats() {
		return beats;
	}

	public void setBeats(List<List<DemoBeat>> beats) {
		this.beats = beats;
	}

	public int getNextVariation() {
		return nextVariation;
	}

	public void setNextVariation(int nextVariation) {
		this.nextVariation = nextVariation;
	}

	public DemoTrack currentVariation(int currentVariation) {
		this.currentVariation = currentVariation;
		this.nextVariation = this.currentVariation;
		return this;
	}

	public int getVariationCount() {
		return this.beats.size();
	}

	public boolean isTunedSample() {
		return tunedSample;
	}

	public void setTunedSample(boolean tunedSample) {
		this.tunedSample = tunedSample;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public DemoTrack tags(String...tags) {
		for ( String tag : tags ) {
			this.tags.add( tag );
		}
		return this;
	}

	public DemoSequencer getSequencer() {
		return sequencer;
	}

	public void setSequencer(DemoSequencer sequencer) {
		this.sequencer = sequencer;
	}


	public DemoTrack sequencer(DemoSequencer sequencer) {
		this.sequencer = sequencer;
		return this;
	}

	public int getLoopCount() {
		return loopCount;
	}

	public void setLoopCount(int loopCount) {
		this.loopCount = loopCount;
	}

	public DemoTrack pauseAfter( double loopTime ) {
		double index = Math.floor(loopTime*1000.0)/1000.0;
		addTrackEvent( index, (sequencer, demoTrack) -> demoTrack.setPaused(true));
		return this;
	}
	
	public DemoTrack unpauseAfter( double loopTime ) {
		double index = Math.floor(loopTime*1000.0)/1000.0;
		addTrackEvent( index, (sequencer, demoTrack) -> demoTrack.setPaused(false));
		System.out.println( "unpause event scheduled on track " + name + " at " + index );
		return this;
	}

	private void addTrackEvent(double index, DemoTrackEvent event) {
		List<DemoTrackEvent> events = trackEvents.get(index);
		if ( events == null ) {
			events = new ArrayList<DemoTrackEvent>();
			trackEvents.put(index, events);
		} //if
		events.add(event);
		
	}

	public double getTimeCorrection() {
		return timeCorrection;
	}

	public void setTimeCorrection(double timeCorrection) {
		this.timeCorrection = timeCorrection;
	}

	public boolean isUseTimeCorrection() {
		return useTimeCorrection;
	}

	public void setUseTimeCorrection(boolean useTimeCorrection) {
		this.useTimeCorrection = useTimeCorrection;
	}
	
	public DemoTrack useTimeCorrection() {
		this.setUseTimeCorrection(true);
		return this;
	}
	
}
