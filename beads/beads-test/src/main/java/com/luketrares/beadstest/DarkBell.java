package com.luketrares.beadstest;

import java.util.concurrent.ThreadLocalRandom;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;
import net.beadsproject.beads.ugens.Envelope;

public class DarkBell extends UGen {

	double oneOverSr;
	double currentPos = 0.0f;
	byte[] parameters;
	double lifeSpan = 0.0;
	double twoPi = Math.PI * 2.0;

	Envelope envelope;

	static {
		System.setProperty("java.util.secureRandomSeed", "true");
	}

	public DarkBell(AudioContext audioContext) {
		super(audioContext, 1);
		this.oneOverSr = (1.0 / audioContext.getSampleRate());
		System.out.println("oneOverSr=" + oneOverSr);
		this.parameters = new byte[1024];
		ThreadLocalRandom.current().nextBytes(parameters);
		initialize();

	}

	private void initialize() {
		
		double root = parameters[512] + 128.0;
		
		this.lifeSpan = root;

		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] < 0) {
				continue;
			}
			this.lifeSpan += (root/32.0)*Math.abs(parameters[i] / 4);
		} // for

		this.lifeSpan /= 1000.0;
		
		initEnvelope();
		
		
	}

	void initEnvelope() {
			envelope = new Envelope(context);
			float duration = (float)(1000.0f*lifeSpan/4.0f);
			envelope.setValue(0.1f);
			envelope.addSegment(ThreadLocalRandom.current().nextFloat(), duration );
			envelope.addSegment(ThreadLocalRandom.current().nextFloat(), duration);
			envelope.addSegment(ThreadLocalRandom.current().nextFloat(), duration);
			envelope.addSegment(0.1f, duration);
	}
	
	@Override
	public void calculateBuffer() {
		//envelope.update();
//		if ( currentPos >= lifeSpan ) {
//			currentPos = 0; 
//			initEnvelope();
//			System.out.println( "envelope flip" );
//		}
		for (int i = 0; i < this.bufferSize; i++) {
			bufOut[0][i] = darkBellSound(currentPos);
			currentPos += oneOverSr;
			for (int j = 1; j < bufOut.length; j++) {
				bufOut[j][i] = bufOut[0][i];
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

			double a3val = Math.max(0.1, (1.0 + Math.sin(angle3)) / 2.0);

			// double angle = 2.0 * Math.PI * (parameters[0]*(j+1)) *
			// (ellapsedTime + i * tinc);
			double jvalue = Math.sin(angle) * Math.sin(angle2) * a3val * parameters[200 + j] / v0;

			value += jvalue;
		} // for

		double nvalue = Math.tanh((0.5 / detailCount) * value  );
		// value = Math.max(-127.0, Math.min(0.5*value,127.0) );

		if (Math.random() < 0.00001) {
			System.out.println("currentPos: " + currentPos + " value (not normalized): " + value + " normalized: " + nvalue + " envelope: " + envelope.getCurrentValue() );
		} //if
		return (float)nvalue; //*envelope.getCurrentValue();

	}

}
