package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ThisIsMySong {

	public void generate(DemoTrack track, long seed) {
		Random random = new Random();
		random.setSeed(seed);
		
		int phraseLength = 5 + random.nextInt(14);
		
		int cp = 0;
		
		int measure = 0;
		double beat = 0.0;
		
		
		for ( int i = 0; i < phraseLength; i++ ) {
			
			track.addBeat(new DemoBeat().location(measure, beat).pitch(Pitch.major(cp)).volume(1.0));
			//cp ++;
			cp += -3 + random.nextInt(6); //ThreadLocalRandom.current().nextInt(-3, 3 );
			//cp -= 1;
			beat += 0.5*( 1 + random.nextInt(3));
			if ( beat >= track.getMeasureSize() ) {
				measure ++;
				beat -= track.getMeasureSize();
			} //
			
			
		} //

		
		
		
		
	}
	
	
	//
	
	
}
