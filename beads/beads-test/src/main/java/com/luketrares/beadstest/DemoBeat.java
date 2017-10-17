package com.luketrares.beadstest;

public class DemoBeat {
	
	int measure;
	double beatPosition;
	double pitch;
	double volume;
	double delay;

		
	public DemoBeat location(int measure, double beatPosition ) {
		this.measure = measure;
		this.beatPosition = beatPosition;
		return this;
	}

	public DemoBeat pitch(double pitch) {
		this.pitch = pitch;
		return this;
	}

	public DemoBeat volume(double volume) {
		this.volume = volume;
		return this;
	}

	public int getMeasure() {
		return measure;
	}

	public void setMeasure(int measure) {
		this.measure = measure;
	}

	public double getBeatPosition() {
		return beatPosition;
	}

	public void setBeatPosition(double beatPosition) {
		this.beatPosition = beatPosition;
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

	public double getDelay() {
		return delay;
	}

	public void setDelay(double delay) {
		this.delay = delay;
	}

}
