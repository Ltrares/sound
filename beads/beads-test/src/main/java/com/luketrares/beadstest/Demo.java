package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.Bead;
import net.beadsproject.beads.core.UGen;

public class Demo extends UGen {

	double frames = 0;
	double ft = System.nanoTime();

	long lastTime = System.nanoTime();
	List<DemoElement> demoElements = new ArrayList<>();
	int channels;

	public Demo(AudioContext arg0, int channels) {
		super(arg0, channels);
		this.channels = channels;
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
		float[][] cbuf = null;
		for (DemoElement de : this.demoElements) {
			de.calculateBuffer();
			for (int i = 0; i < this.channels; i++) {
				for (int j = 0; j < this.bufferSize; j++) {
					this.bufOut[i][j] += de.getOutBuffer(i)[j];

					if (Math.abs(this.bufOut[i][j]) > max)
						max = Math.abs(this.bufOut[i][j]);
				} // for
			}		
		}


		for (int i = 0; i < this.ins; ++i) {
			for (int k = 0; k < this.bufferSize; ++k) {
				this.bufOut[i][k] = (float) Math.tanh(1.2*this.bufIn[i][k]/max);
			}
		}

//		if (Math.random() < 0.001)
//			System.out.println("max = " + max);

		// float max = 0.2f;
		//
		// for ( int i = 0; i < this.bufferSize; i++ ) {
		// for ( int j = 0; j < this.channels; j++ ) {
		// bufOut[j][i] += el.getOutBuffer(j)[i];
		// }
		// }
		//
		// for ( int i = 0; i < this.bufferSize; i++ ) {
		// for ( int j = 0; j < this.channels; j++ ) {
		// if ( Math.abs(bufOut[j][i]) > max ) max = Math.abs(bufOut[j][i]);
		// }
		// }
		//
		// for ( int i = 0; i < this.bufferSize; i++ ) {
		// for ( int j = 0; j < this.channels; j++ ) {
		// bufOut[j][i] = (float)(Math.tanh( bufOut[j][i]/(2.0f) ) );
		// }
		// }
		//
		// if ( max > 0.2f ) System.out.println( "max " + max );

	}

	private float clampValue(float f) {

		if (f == 0 || this.demoElements.size() <= 0)
			return 0.0f;

		return (float) (Math.tanh(f));

	}

	// @Override
	// public void update() {
	// updateDemo();
	// super.update();
	// }

	private void updateDemo() {
		if (this.isPaused())
			return;

		long diff = System.nanoTime() - lastTime;
		lastTime += diff;
		if (lastTime - ft >= 1000d * 1000000d) {
			System.out.println("frames " + frames);
			frames = 0;
			ft = lastTime;
		} // if
		frames++;
		double time = diff / 1000000d;

		DemoElement ni = null;
		
		if (Math.random() < 0.00007 * time) {
			ni = new DarkBell(this.bufferSize, this.channels);
		} else if ( Math.random() < 0.0002 * time ) {
			if ( this.demoElements.size() > 0 ) {
				ni = new Recorder(this.bufferSize, this.channels );
				ni.addInputElement( this.demoElements.get( this.demoElements.size()-1));
			} //if
		} //
		
		if ( ni != null ) {
			System.out.println("starting new demo sound " + ni.textDisplay());
			this.demoElements.add(ni);
		} //if
		
		Iterator<DemoElement> it = this.demoElements.iterator();

		while (it.hasNext()) {
			DemoElement el = it.next();

			if (el.isDone()) {
				System.out.println("ending demo sound " + el.textDisplay());
				el.kill();
				it.remove();
				continue;
			} // if

		} // while
	}

}
