package com.luketrares.beadstest;

public class Pitch {

	private static final double[] tones = {
			1.0/1.0, 16.0/15.0, 9.0/8.0, 6.0/5.0, 5.0/4.0, 4.0/3.0, Math.sqrt(2.0)/1.0, 3.0/2.0, 8.0/5.0, 5.0/3.0, 7.0/4.0, 15.0/8.0
	};
		
	public static final double[] major = {
			tones[0], tones[2], tones[4], tones[5], tones[7], tones[9], tones[11]			
	};
	
	public static final double[] minor = {
			tones[0], tones[2], tones[3], tones[5], tones[7], tones[8], tones[10]						
	};
	
	public static double minus(int halfTones) {
		double step = tones[halfTones];
		return 1.0/step;
	}
	
	public static double plus(int halfTones) {
		return tones[halfTones];
	}
	
	
	public static double major(int position) {		
		
		int ip = position;
		double sgn = Math.signum(position);
		double result = 1.0;
		
		while ( Math.abs(position) >= major.length ) {
			position -= sgn*major.length;
			if ( sgn > 0 ) result *= 2.0;
			else result *= 0.5;
		} //while
		
		if ( position != 0 ) {
			//System.out.println( "major[position] = " + major[position] );
			if ( sgn  > 0 ) result *= major[position];
			else result *= (major[major.length+position]/2.0);
		} //
		
		System.out.println( "position = " + ip + "(" + position + ") pitch = " + result );

		return result;
		
	}
}
