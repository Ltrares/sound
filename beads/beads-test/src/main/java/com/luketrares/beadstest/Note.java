package com.luketrares.beadstest;

public class Note {

	int interval;
	float duration;
	float velocity;
	
	public Note(final Note note) {
		this.interval = note.interval;
		this.duration = note.duration;
		this.velocity = note.velocity;
	}

	public Note() {
	}

	@Override
	public String toString() {
		return "{interval:" + interval + ",duration:"+duration+",velocity:" + velocity + "}";
	}
	
	
	
	
}
