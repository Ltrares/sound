package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Voice {

	double cbeat = 0;
	int cmeasure = 0;
	int numberOfMeasures = 1;
	int beatsPerMeasure = 4;
	double beatIncrement = 0.25; //4/4 time with 16th note resolution
	
	Map<Double,List<DemoElement>> sequence = new TreeMap<>();
	
	public List<DemoElement> update() {
		double beat = cbeat;
		cbeat += beatIncrement;
		return sequence.get(beat);
	}


	
	


}
