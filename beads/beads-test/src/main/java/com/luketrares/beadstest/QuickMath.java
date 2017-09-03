package com.luketrares.beadstest;

public class QuickMath {

	private static final int ANGLE_RESOLUTION = 1000;
	private static final double[] SINE_TABLE = new double[ANGLE_RESOLUTION];
	private static final double[] SINE_4_WINDOW = new double[ANGLE_RESOLUTION];
	private static final double[] SINE_WINDOW = new double[ANGLE_RESOLUTION];
	
	private static final double TWO_PI = 2.0*Math.PI;
	static {
		for ( int i = 0; i < ANGLE_RESOLUTION; i++ ) {
			double angle = TWO_PI*i/ANGLE_RESOLUTION;
			SINE_TABLE[i] = Math.sin(angle);
		} //for
		
	
		for ( int i = 0; i < ANGLE_RESOLUTION; i++ ) {
			double range = Math.PI*i/ANGLE_RESOLUTION;
			double domain = Math.sin(range);
			SINE_4_WINDOW[i] = domain*domain*domain*domain;
			SINE_WINDOW[i] = domain;
		} //for
		
	}
	
	
	public static double sin( double angleInRadians ) {
	
		int angleIndex = (int)(ANGLE_RESOLUTION*angleInRadians/TWO_PI);
		
		if ( angleIndex >= 0 && angleIndex < ANGLE_RESOLUTION ) return SINE_TABLE[ angleIndex ];

		
		angleIndex = angleIndex % ANGLE_RESOLUTION;
		
		if ( angleIndex < 0 ) angleIndex += ANGLE_RESOLUTION;
		
		return SINE_TABLE[angleIndex];
	}

	
	
	public static double sinFourWindow( double range ) {
		
		int rangeIndex = (int)(ANGLE_RESOLUTION*range);
		
		if ( rangeIndex >= 0 && rangeIndex < ANGLE_RESOLUTION ) return SINE_4_WINDOW[ rangeIndex ];
		
		rangeIndex = rangeIndex % ANGLE_RESOLUTION;
		
		if ( rangeIndex < 0 ) rangeIndex += ANGLE_RESOLUTION;
		
		return SINE_4_WINDOW[rangeIndex];

	}

	public static double sineWindow( double range ) {
		
		int rangeIndex = (int)(ANGLE_RESOLUTION*range);
		
		if ( rangeIndex >= 0 && rangeIndex < ANGLE_RESOLUTION ) return SINE_WINDOW[ rangeIndex ];
		
		rangeIndex = rangeIndex % ANGLE_RESOLUTION;
		
		if ( rangeIndex < 0 ) rangeIndex += ANGLE_RESOLUTION;
		
		return SINE_WINDOW[rangeIndex];

	}
	
}
