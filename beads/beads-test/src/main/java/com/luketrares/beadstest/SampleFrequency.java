package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.List;

import org.jtransforms.fft.DoubleFFT_1D;

import com.sun.media.sound.FFT;

import net.beadsproject.beads.data.Sample;

public class SampleFrequency {

	private static final int FFT_SIZE = 8192;

	private static final int FFT_STEP_SIZE = 1024;

	private static final int STEP_COUNT = FFT_SIZE / FFT_STEP_SIZE;

	final Sample sample;

	double[][] frequencies;

	int stepsInSample;

	double frequencyBin;

	int currentStep;

	double[][] fftData;

	DoubleFFT_1D fft;

	List<List<double[]>> buffers = new ArrayList<List<double[]>>();
	List<double[]> unusedBuffers = new ArrayList<double[]>();

	double[][] fftTotals;

	public SampleFrequency(Sample sample) {
		this.sample = sample;

		stepsInSample = (int) (Math.ceil(sample.getNumFrames() / FFT_STEP_SIZE));

		frequencies = new double[stepsInSample][sample.getNumChannels()]; // [sample.getNumChannels()];

		frequencyBin = sample.getSampleRate() / FFT_SIZE;

	}

	private void init() {
		fft = new DoubleFFT_1D(FFT_SIZE);
		fftData = new double[sample.getNumChannels()][FFT_SIZE];

		fftTotals = new double[sample.getNumChannels()][FFT_SIZE];

		for (int i = 0; i < sample.getNumChannels(); i++) {
			List<double[]> cb = new ArrayList<double[]>();
			for (int j = 0; j < STEP_COUNT; j++) {
				cb.add(new double[FFT_STEP_SIZE]);
			} //
			buffers.add(cb);
		} // for
	}

	public void analyzeAll() {
		init();
		currentStep = 0;
		for (int i = 0; i < stepsInSample; i++) {
			analyzeStep();
		} // for

		this.fftData = null;
		//this.fftTotals = null;
		this.buffers.clear();
		this.unusedBuffers.clear();
		this.fft = null;
	}

	public void analyzeStep() {

		for (int i = 0; i < sample.getNumChannels(); i++) {
			double[] data = unusedBuffers.size() > 0 ? unusedBuffers.remove(0) : new double[FFT_SIZE];
			readStep(i, currentStep, data);
			pushFftData(i, data);
			analyzeFft(i);

		} //
		currentStep++;
	}

	private void analyzeFft(int channel) {
		List<MF> maxs = maxBins(fftData[channel]);
		frequencies[currentStep][channel] = maxs.get(1).bin * this.frequencyBin;

		// System.out.println( "step " + currentStep + " " +
		// frequencies[currentStep][0] );

		for (int i = 0; i < FFT_SIZE; i++) {
			fftTotals[channel][i] += fftData[channel][i] * fftData[channel][i];
		} // for
	}

	private static class MF {
		int bin;
		double max;

		public String toString() {
			return "[bin=" + bin + ", max=" + max + "]";
		}
	}

	private List<MF> maxBins(double[] data) {

		List<MF> maxs = new ArrayList<MF>();

		int maxc = 5;

		for (int i = 0; i < maxc; i++) {
			maxs.add(new MF());
		}

		for (int i = 0; i < FFT_SIZE / 8; i++) {
			double cv = data[i] * data[i];

			int cbin = i;

			for (int k = 0; k < maxs.size(); k++) {
				MF mf = maxs.get(k);

				if (cv > mf.max) {
					double pmax = mf.max;
					int pbin = mf.bin;

					mf.max = cv;
					mf.bin = cbin;

					cv = pmax;
					cbin = pbin;

				} // if
			} //
		} // for

		return maxs;

	}

	private void pushFftData(int channel, double[] data) {
		this.buffers.get(channel).add(data);
		this.unusedBuffers.add(this.buffers.get(channel).remove(0));

		double tv = 0;
		for (int i = 0; i < STEP_COUNT; i++) {
			int startingPoint = i * FFT_STEP_SIZE;
			for (int j = 0; j < FFT_STEP_SIZE; j++) {
				fftData[channel][startingPoint + j] = buffers.get(channel).get(i)[j];
				tv += buffers.get(channel).get(i)[j];
			} // for
		} // for

		fft.realForward(fftData[channel]);

	}

	private void readStep(int channel, int currentStep, double[] data) {
		int startPosition = currentStep * FFT_STEP_SIZE;
		float[] frame = new float[sample.getNumChannels()];

		for (int i = 0; i < FFT_STEP_SIZE; i++) {
			if (startPosition + i >= sample.getNumFrames()) {
				data[i] = 0.0;
			} else {
				sample.getFrame(startPosition + i, frame);
				data[i] = frame[channel];
			} // else
		} // for

	}

	public double getFrequencyOverall(int channel) {
		List<MF> maxs = maxBins(fftTotals[channel]);
		return maxs.get(1).bin * this.frequencyBin;
	}

	public double getFrequencyAtTime(int channel, double time) {
		if (time > this.sample.getLength()) {
			return 0;
		} // if

		int step = convertTimeToStep(time);

		return frequencies[step][channel];

	}

	private int convertTimeToStep(double time) {
		double position = time / this.sample.getLength();
		return (int) (this.stepsInSample * position);
	}

}
