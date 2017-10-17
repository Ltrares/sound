package com.luketrares.beadstest;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.beadsproject.beads.data.Sample;

public class ClasspathSampleManager {

	static Map<String,Sample> samples = new ConcurrentHashMap<>();
	
	//static Map<String,float[][]> sampleData = new ConcurrentHashMap<>();
	static Map<String,SampleFrequency> frequencies = new ConcurrentHashMap<>();
	
	public Sample sample( String path ) {
	
		if ( samples.containsKey(path) ) {			
			if ( samples.containsKey(path) ) return copySample( samples.get( path) );
		} //if
		
		Sample sample = loadSampleFromClasspath( path );
		
		if ( sample != null ) {
			
			samples.put( path, sample );
	
			SampleFrequency frq = new SampleFrequency( sample );
			long ts = System.currentTimeMillis();
			frq.analyzeAll();
			ts = System.currentTimeMillis() - ts;
			
			frequencies.put( path,  frq );
		}
		
		return copySample(sample);
		
	}


	public static Sample copySample(Sample source) {
		
		float sampleRate = source.getSampleRate();
		int nchannels = source.getNumChannels();
		long nframes = source.getNumFrames(); //.sampleData[0].length;			
		Sample sample = new Sample( 1000.0*nframes/sampleRate, nchannels, sampleRate );
		setProperty( sample, source, "theSampleData" );
		sample.setSimpleName( source.getSimpleName() );

		return sample;
	}


	private static void setProperty(Sample sample, Sample source, String fieldName) {
		try {
			Field field = source.getClass().getDeclaredField( fieldName );
			field.setAccessible(true);
			field.set(sample, field.get(source) );
		} catch( Exception e ) {
			e.printStackTrace(System.out);
		}
	}


	private Sample loadSampleFromClasspath(String samplePath ) {
		
		SampleStreamReader reader = new SampleStreamReader();
		try (InputStream is = this.getClass().getClassLoader().getResourceAsStream( samplePath )) {
			//System.out.println( "is: " + is + " for " + samplePath );
			float[][] sampleData = reader.readSample(is);
			float sampleRate = reader.getSampleRate();
			int nchannels = sampleData.length;
			int nframes = sampleData[0].length;			
			Sample sample = new Sample( 1000.0*nframes/sampleRate, nchannels, sampleRate );
			sample.putFrames(0, sampleData);
			sample.setSimpleName( samplePath );
			//System.out.println( "loaded sample " + samplePath + " " + nchannels + " channels");
			
			return sample;
		} catch( Exception e ) {			
			System.out.println( "couldn't load " + samplePath + " " + e.getMessage() );
			e.printStackTrace(System.out);
		}
		
		return null;
	}


	public void preload(List<String>... sampless ) {
		for ( List<String> samples : sampless ) {
			for ( String sample : samples ) {
				sample( sample );
			} //for
		} //
	}


	public SampleFrequency getFrequency(String sampleFile) {
		return frequencies.get(sampleFile);
	}
	
	
	
}
