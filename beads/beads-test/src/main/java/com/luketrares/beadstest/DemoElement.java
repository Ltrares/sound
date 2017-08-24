package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import net.beadsproject.beads.core.AudioContext;
import net.beadsproject.beads.core.UGen;

public abstract class DemoElement extends UGen implements Finishable {

	private static final AtomicLong inputIdGenerator = new AtomicLong(0L);
	
	final long id;
	
	public DemoElement(AudioContext paramAudioContext, int channels) {
		super(paramAudioContext, channels);
		id = inputIdGenerator.incrementAndGet();
	}
	
	public final long getId() {
		return id;
	}

	public String textDisplay() {
		return "Abstract Demo Input ID " + this.getId() + " - isDone = " + isDone();
	}
	
	
	final List<DemoElement> inputElements = new ArrayList<>();
	

	public List<DemoElement> getInputElements() {
		return inputElements;
	}
	
	public void addInputElement( DemoElement element ) {
		this.inputElements.add(element);
	} //
	
	
}
