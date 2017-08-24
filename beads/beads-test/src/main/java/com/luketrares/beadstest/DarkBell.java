package com.luketrares.beadstest;

import java.util.concurrent.ThreadLocalRandom;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Envelope;

public class DarkBell extends DemoElement {

	double oneOverSr;
	double currentPos = 0.0f;
	byte[] parameters;
	double lifeSpan = 0.0;
	double twoPi = Math.PI * 2.0;
	float[] pan;
	int channels = 0;
	
	static {
		System.setProperty("java.util.secureRandomSeed", "true");
	}

	@Override
	public String textDisplay() {
		return "dark bell " + getId();
	}

	public DarkBell(int bufferSize, int channels) {
		super(new AudioContext(),channels);
		this.bufOut = new float[channels][bufferSize];
		this.channels = channels;
		initialize();
	} //
	
	public DarkBell(AudioContext context, int channels) {
		super(context, channels);
		this.channels = channels;
		initialize();
	}

	private void initialize() {
		this.pan = new float[this.channels];
		for ( int i = 0; i < this.channels; i++ ) {
			this.pan[i] = 1.0f;
		} //for
		if ( ThreadLocalRandom.current().nextBoolean() ) {
			int fixedChannel = ThreadLocalRandom.current().nextInt( this.channels );
			
			for ( int i = 0; i < this.channels; i++ ) {
				if ( i == fixedChannel ) continue;
				this.pan[i] = ThreadLocalRandom.current().nextFloat();
			}
			
		} //if
		
		
		this.oneOverSr = (1.0 / context.getSampleRate());
		this.parameters = new byte[1024];
		ThreadLocalRandom.current().nextBytes(parameters);	
		double root = parameters[512] + 128.0;
		
		this.lifeSpan = root;

		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] < 0) {
				continue;
			}
			this.lifeSpan += (root/64.0)*Math.abs(parameters[i] / 4);
		} // for

		this.lifeSpan /= 1000.0;
		
	}
	
	@Override
	public void calculateBuffer() {

		for (int i = 0; i < this.bufferSize; i++) {
			float value = darkBellSound(currentPos);
			currentPos += oneOverSr;
			for (int j = 0; j < bufOut.length; j++) {
				bufOut[j][i] = pan[j]*value;
			} // for
		}

	}

	float noise() {
		return (float) (1.0D - (2.0D * Math.random()));
	}

	float sine(double pos) {
		float val = (float) Math.sin(440.0 * pos * twoPi);
		return val;

	}

	private float darkBellSound(double pos) {
		//envelope.update();
		double value = 0.0;

		int detailCount = 29;
		for (int j = 0; j < detailCount; j++) {
			int v2 = parameters[20 + j]; // + 128*parameters[300+j];
			int v1 = parameters[0 + j]; // + 128*parameters[100+j];
			int v3 = 1 + parameters[100 + j];
			int v4 = 1 + parameters[200 + j];
			int v5 = 1 + parameters[300 + j];
			double v0 = 1.0 + j;
			double angle3 = twoPi * (v2 / (double) (v3 != 0 ? v3 : 1)) * 0.001 * (pos); // + 0.00005*noise();
			double angle2 = twoPi * (v2 / (double) (v3 != 0 ? v3 : 1)) * 0.1 * (pos); // + 0.005*noise();
			double angle = twoPi * (v1 / (double) (v4 != 0 ? v4 : 1)) * 200.0 * (pos); // + 50.0*noise();

			double a3val = Math.max(0.1, (1.0 + QuickMath.sin(angle3)) / 2.0);

			// double angle = 2.0 * Math.PI * (parameters[0]*(j+1)) *
			// (ellapsedTime + i * tinc);
			double jvalue = QuickMath.sin(angle) * QuickMath.sin(angle2) * a3val * parameters[200 + j] / v0;

			value += jvalue;
		} // for

		double nvalue = Math.tanh((0.5 / detailCount) * value  );
		// value = Math.max(-127.0, Math.min(0.5*value,127.0) );

//		if (Math.random() < 0.00001) {
//			System.out.println("currentPos: " + currentPos + " value (not normalized): " + value + " normalized: " + nvalue + " envelope: " + envelope.getCurrentValue() );
//		} //if
		
		float env = (float) QuickMath.sin( Math.PI*currentPos/lifeSpan );
		
		return (float)nvalue*env; //*envelope.getCurrentValue();

	}

	@Override
	public boolean isDone() {
		return this.currentPos >= this.lifeSpan;
	}

}
