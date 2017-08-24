package com.luketrares.beadstest;

public class QuickMath {

	private static final int ANGLE_RESOLUTION = 1000;
	private static final double[] SINE_TABLE = new double[ANGLE_RESOLUTION];
	
	private static final double TWO_PI = 2.0*Math.PI;
	static {
		for ( int i = 0; i < ANGLE_RESOLUTION; i++ ) {
			double angle = TWO_PI*i/ANGLE_RESOLUTION;
			SINE_TABLE[i] = Math.sin(angle);
		} //
		
	}
	
	
	public static double sin( double angleInRadians ) {
	
		int angleIndex = (int)(ANGLE_RESOLUTION*angleInRadians/TWO_PI);
		
		if ( angleIndex >= 0 && angleIndex < ANGLE_RESOLUTION ) return SINE_TABLE[ angleIndex ];

		
		angleIndex = angleIndex % ANGLE_RESOLUTION;
		
		if ( angleIndex < 0 ) angleIndex += ANGLE_RESOLUTION;
		
		return SINE_TABLE[angleIndex];
	}

	
	
}
