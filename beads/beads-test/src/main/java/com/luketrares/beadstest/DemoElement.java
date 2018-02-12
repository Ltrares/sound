package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public abstract class DemoElement extends UGen implements Finishable {

	private static final AtomicLong inputIdGenerator = new AtomicLong(0L);

	final long id;

	final float[] pan;

	float volume;

	float pitch;

	final int channelCount;

	double delayBeforeStart = 0.0;
	
	private Bob instigator;
	
	//DemoEffect effect;
	
//	public void applyEffect() {
//		if ( effect == null ) return;
//		effect.calculateBuffer(this);
//	}
	
	public DemoElement(AudioContext paramAudioContext, int channels) {
		super(paramAudioContext, channels);
		id = inputIdGenerator.incrementAndGet();

		pan = initPan(channels);

		channelCount = channels;

		volume = 1.0f;

		pitch = 1.0f;
	}

	private float[] initPan(int channels) {
		float[] result = new float[channels];
		for (int i = 0; i < channels; i++) {
			result[i] = 1.0f;
		} // for
		if (ThreadLocalRandom.current().nextBoolean()) {
			int fixedChannel = ThreadLocalRandom.current().nextInt(channels);

			for (int i = 0; i < channels; i++) {
				if (i == fixedChannel)
					continue;
				result[i] = ThreadLocalRandom.current().nextFloat();
			}

		} // if

		return result;
	}

	public final long getId() {
		return id;
	}

	public boolean isDelayed( double timeIncrement ) {		
		if ( this.delayBeforeStart <= 0 ) return false;
		this.delayBeforeStart -= timeIncrement;
		return this.delayBeforeStart > 0;
	}
	
	public String textDisplay() {
		return this.getClass().getSimpleName() + " " + this.getId() + " - isDone = " + isDone();
	}

	final List<DemoElement> inputElements = new ArrayList<>();

	public List<DemoElement> getInputElements() {
		return inputElements;
	}

	public void addInputElement(DemoElement element) {
		this.inputElements.add(element);
	} //

	public float[] getPan() {
		return pan;
	}

	public int getChannelCount() {
		return channelCount;
	}

	final public void setInstigator(Bob bob) {
		this.instigator = bob;
	}

	final public Bob getInstigator() {
		return this.getInstigator();
	}

	public void initialize() {
	}

	public float getVolume() {
		return volume;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	double findNearestHarmonic(double source, double target) {

		double ratio = source / target;

		if (ratio > 2)
			return findNearestHarmonic(source, target * 2.0);
		if (ratio < 0.5)
			return findNearestHarmonic(source, target / 2.0);

		if (ratio > 1.5)
			return target * 2.0;

		if (ratio < 0.75)
			return target / 2.0;

		return target;

	}


}
