package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.List;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.data.Sample;
import net.beadsproject.beads.ugens.Static;
import net.beadsproject.beads.ugens.SamplePlayer.InterpolationType;

public class GrainDemoSample extends DemoSample {

	Sample sample;

	double msPerSample;

	boolean firstGrain = true;
	List<Grain> grains = new ArrayList<>();
	List<Grain> freeGrains = new ArrayList<>();
	List<Grain> deadGrains = new ArrayList<>();

	//double pitch = 1.0;
	double grainInterval = 100.0f;
	double grainSize = 200.0f;
	boolean loopInsideGrains = false;
	double timeSinceLastGrain = 0.0;
	double randomness = 0.0;

	double position = 0.0;

	double rate = 1.0;
	float[] frame;

	InterpolationType interpolationType;
	double positionIncrement;
	boolean isDone = false;

	boolean reverse = false;
	
	long startTime = System.currentTimeMillis();
	public GrainDemoSample(AudioContext paramAudioContext, int channels, Sample sample) {
		super(paramAudioContext, channels, sample );

		this.sample = sample;

		this.interpolationType = InterpolationType.ADAPTIVE;

		this.pitch = 1.0f; // (new Static(paramAudioContext, 1.0f));

		this.grainInterval = 100.0;
		this.grainSize = 200.0;

		this.rate = 1.0;
		this.randomness = 0.1; //0.035;
		msPerSample = 1000.0 / paramAudioContext.getSampleRate();

		frame = new float[sample.getNumChannels()];
	}

	@Override
	public boolean isDone() {
		return this.isDone;
	}

	private void firstGrain() {
		if (!(this.firstGrain))
			return;
		Grain localGrain = new Grain();
		localGrain.position = this.position;
		localGrain.age = (this.grainSize / 4.0F);
		this.grains.add(localGrain);
		this.firstGrain = false;
		this.timeSinceLastGrain = (this.grainInterval / 2.0F);

		// setGrainPan(localGrain, this.randomPanEnvelope.getValue(0, 0));
	}

	private void resetGrain(Grain paramGrain, int paramInt) {
		paramGrain.position = (this.position + this.grainSize * this.randomness * (Math.random() * 2.0D - 1.0D));
		paramGrain.age = 0.0D;
		paramGrain.grainSize = this.grainSize;
	}

	@Override
	public synchronized void calculateBuffer() {

		firstGrain();
		for (int i = 0; i < this.bufferSize; ++i) {
			//this.position += this.msPerSample;
			if (this.timeSinceLastGrain > this.grainInterval) {
				Grain localGrain1 = null;
				if (this.freeGrains.size() > 0) {
					localGrain1 = (Grain) this.freeGrains.get(0);
					this.freeGrains.remove(0);
				} else {
					localGrain1 = new Grain();
				}
				resetGrain(localGrain1, i);
				this.grains.add(localGrain1);
				this.timeSinceLastGrain = 0.0F;
			} // if
			for (int j = 0; j < this.channelCount; ++j)
				this.bufOut[j][i] = 0.0F;
			Grain localGrain2;
			for (int j = 0; j < this.grains.size(); ++j) {
				localGrain2 = (Grain) this.grains.get(j);
				float f = this.volume * (float) QuickMath.sineWindow((localGrain2.age / localGrain2.grainSize));
				switch (this.interpolationType) {
				case ADAPTIVE:
					if (this.pitch > 2.5F)
						this.sample.getFrameNoInterp(localGrain2.position, this.frame);
					else if (this.pitch > 0.5F)
						this.sample.getFrameLinear(localGrain2.position, this.frame);
					else
						this.sample.getFrameCubic(localGrain2.position, this.frame);
					break;
				case LINEAR:
					this.sample.getFrameLinear(localGrain2.position, this.frame);
					break;
				case CUBIC:
					this.sample.getFrameCubic(localGrain2.position, this.frame);
					break;
				case NONE:
					this.sample.getFrameNoInterp(localGrain2.position, this.frame);
				} // case inerpolationType
				
				
				for (int k = 0; k < this.channelCount; ++k) {
					this.bufOut[k][i] += pan[k] * f * this.frame[(k % this.sample.getNumChannels())];
				} //for k
			} // for j
			calculateNextPosition();
			// this.pitch = Math.abs(this.pitchEnvelope.getValue(0, i));
			for (int j = 0; j < this.grains.size(); ++j) {
				localGrain2 = (Grain) this.grains.get(j);
				calculateNextGrainPosition(localGrain2);
			} // for j
			GrainDemoSample tmp541_540 = this;
			tmp541_540.timeSinceLastGrain = (float) (tmp541_540.timeSinceLastGrain + this.msPerSample);
			for (int j = 0; j < this.grains.size(); ++j) {
				localGrain2 = (Grain) this.grains.get(j);
				if (localGrain2.age <= localGrain2.grainSize)
					continue;
				this.freeGrains.add(localGrain2);
				this.deadGrains.add(localGrain2);
			} // for j
			for (int j = 0; j < this.deadGrains.size(); ++j) {
				localGrain2 = (Grain) this.deadGrains.get(j);
				this.grains.remove(localGrain2);
			} // for j
			this.deadGrains.clear();
		} // for i
	}

	protected void calculateNextPosition() {
		this.position += this.msPerSample * this.rate;
		
		//permit pitch to change once per pos+ition increment - affected by rate but not pitch		
		this.pitch = calcPitch( (float)this.position );
		
		if ((this.position <= this.sample.getLength()) && (this.position >= 0.0D))
			return;
		
		this.isDone = true;
	}

	float calcPitch(float d) {
		return this.pitch;
	}

	private void calculateNextGrainPosition(Grain paramGrain) {

		int i = (this.rate >= 0.0F) ? 1 : -1;
		paramGrain.age += this.msPerSample;
		//if (this.loopInsideGrains)
		//	paramGrain.position += i * this.msPerSample * this.pitch;
		//else
		paramGrain.position += i * this.msPerSample * this.pitch;

	}

	private static class Grain {
		double position;
		double age;
		double grainSize;
		float[] pan;
	}
	
	@Override
	public String textDisplay() {
		return super.textDisplay() + " - progress=" + this.position / this.sample.getLength() + " pitch=" + this.pitch + " rate=" + this.rate + " age=" + (System.currentTimeMillis() - this.startTime) + "/" + sample.getLength();
	}

}
