package com.luketrares.beadstest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sound.midi.Instrument;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

public class Song {

	public static void main(String[] args) throws Exception {
		for ( long seed = 0; seed < 100; seed++ ) {		
			System.out.println( "playing song " + seed );
			Song song = new Song();
			List<Note> notes = song.makeMusic(seed);
			song.play(notes, 1000.0);
		} //for
	}

	List<Integer> prevNotes = new ArrayList<Integer>();

	public void play(List<Note> notes, double speed ) throws Exception {
		System.out.println("playing song with length " + notes.size());

		initMidi();
		
		List<Note> spool = new ArrayList<Note>(notes);

		List<Note> playing = new ArrayList<Note>();

		long timeToWait = 0;
		for (Note note : notes) {
			timeToWait = playNextNote(spool,speed);
			waitForNextNote(timeToWait);
		} // for

		this.closeMidi();
		System.out.println("song is over");
		waitForNextNote(2500);
	}

	private void closeMidi() {
		// TODO Auto-generated method stub
		if ( synth != null ) synth.close();
		synth = null;
	}

	private void initMidi() throws MidiUnavailableException {
		this.synth = MidiSystem.getSynthesizer();
		synth.open();
		
		Instrument[] insts = synth.getLoadedInstruments();
//		
//		synth.loadInstrument( insts[ (int)(Math.random()*insts.length)]);
		
		Instrument inst = insts[ (int)(Math.random()*insts.length)];
		System.out.println( "chose instrument " + inst );
		for ( MidiChannel channel : synth.getChannels() ) {
			channel.programChange(inst.getPatch().getBank(),inst.getPatch().getProgram());			
		}
		
	}

	private void waitForNextNote(long timeToWait) throws InterruptedException {
		Thread.sleep(timeToWait);
	}

	private long playNextNote(List<Note> spool, double speed) throws Exception {

		Note nextNote = spool.remove(0);

		double noteSpeed = 1000;
		
		int base_note = 60;
		long note_millis = (long) (speed*nextNote.duration);
		int note_number = base_note + IntervalToHalfSteps( nextNote.interval );
		int note_velocity = 32 + (int)(85*nextNote.velocity);
		playMidiNote( note_number, note_velocity, 0, (int) note_millis );
		System.out.println(nextNote);
		return (long) (nextNote.duration * 1000.0);

	}

	private void playMidiNote(int note_number, int note_velocity, int i, int note_millis) {
		if ( prevNotes.size() >= 3 ) {
			Integer pn = prevNotes.remove(0);
			synth.getChannels()[0].noteOff( pn, 64 );
		}

		synth.getChannels()[0].noteOn( note_number, note_velocity );

		prevNotes.add( note_number );
	}

	public List<Note> makeMusic(long seed) {
		Random random = new Random();
		random.setSeed(seed);

		List<Note> notes = new ArrayList<Note>();

		int maxNotes = random.nextInt(31) + 17;
		float curVelocity = 0.5f;
		float baseBeat = 0.25f;
		int curInterval = 0;
		int lmark = -1;
		int linsert = -1;
		int rtoggle = 0;

		double avgDur = 0.0;
		
		for (int i = 0; i < maxNotes; i++) {
			Note n = new Note();

			
			
			
			n.interval = 0;
			n.duration = baseBeat;
			n.velocity = curVelocity;
			notes.add(n);
			// most beats are base_duration
			// some beats are 1/2 or 2 base duration
			// few beats are 1/4 or 4 base duration

			double fs = random.nextDouble();
			int exp = 0;
			int mult = 0;

			if (fs < 0.55f) {
				exp = 0;
			} else if (fs < .65f) {
				exp = 1;
			} else if (fs < .85f) {
				exp = 2;
			} else if (fs < 0.95f) {
				exp = 3;
			} else {
				exp = 4;
			} // else

			if (random.nextBoolean() ) {
				exp *= -1;
			} // if

			n.duration = (float) (baseBeat * Math.pow(2.0, exp));

			float up_prob = 0.5f;

			if (curInterval > 0) {
				up_prob = 0.3f;
			} else if (curInterval < 0) {
				up_prob = 0.7f;
			} // if

			int noteInterval = (int) (random.nextDouble() * 7); // IntRand(0,7);
			if (random.nextDouble() > up_prob) {
				noteInterval *= -1; // IntRand( -8, 0 );
			} // else

			curInterval += noteInterval;
			n.interval = curInterval;

			curVelocity += random.nextDouble() * 0.2 - 0.1; // FloatRand(-0.2f,0.2f);
			curVelocity = Math.min(curVelocity, 1);
			curVelocity = Math.max(curVelocity, 0);
			n.velocity = curVelocity;

			// notes.add(n);
			// when you come to a long note - insert the beginning phrase again
			if (notes.size() >= 7 && curInterval == 0) {
				lmark = notes.size();
			} // if

			if (exp >= 3) {
				if (lmark == -1) {
					if (exp == 2 && notes.size() >= 7) {
						lmark = notes.size();
					} // if

					if (exp == 3) {
						lmark = notes.size();
					} // if
				} else {
					if ((i - linsert) * exp >= 11) {
						linsert = i;
						for (int j = 0; j < lmark; j++) {
							notes.add(notes.get(j));
						} // for

						if (rtoggle > 0) {
							for (int j = 0; j < lmark; j++) {
								Note nn = new Note(notes.get(j));
								if (rtoggle == 2) {
									nn.interval *= -1;
								} else {
									nn.interval += 4;
								} // else
								notes.add(nn);
							} // for
						} // if

						rtoggle = (rtoggle + 1) % 3;
					} // if
				} // else
			} // if

		} // for

		
		
		while ( curInterval != 0 ) {		
			curInterval -= Math.signum(curInterval)*(random.nextInt(3)+1);
			Note fn = new Note();
			fn.duration = curInterval == 0 ? baseBeat *2 : baseBeat;
			
			//curInterval += ni;
			fn.interval = curInterval;
			fn.velocity = 0.5f;
			notes.add(fn);
		}
		
		
//		
//		
//		// bring it home
//		int finalVal = 3;
//
//		while (curInterval != 0 && finalVal > 0) {
//			int ni = (int) (random.nextDouble() * 3); // IntRand(0,3);
//			if (curInterval > 0) {
//				ni *= -1;
//			} // if
//			Note fn = new Note();
//			fn.duration = baseBeat;
//			curInterval += ni;
//			fn.interval = curInterval;
//			fn.velocity = 0.5f;
//			notes.add(fn);
//			finalVal--;
//		} // while
//
		return notes;
	}
	
	
	public List<Note> makeMusicClassic(long seed) {
		Random random = new Random();
		random.setSeed(seed);

		List<Note> notes = new ArrayList<Note>();

		int maxNotes = random.nextInt(31) + 17;
		float curVelocity = 0.5f;
		float baseBeat = 0.25f;
		int curInterval = 0;
		int lmark = -1;
		int linsert = -1;
		int rtoggle = 0;

		for (int i = 0; i < maxNotes; i++) {
			Note n = new Note();

			n.interval = 0;
			n.duration = baseBeat;
			n.velocity = curVelocity;
			notes.add(n);
			// most beats are base_duration
			// some beats are 1/2 or 2 base duration
			// few beats are 1/4 or 4 base duration

			double fs = random.nextDouble();
			int exp = 0;
			int mult = 0;

			if (fs < 0.55f) {
				exp = 0;
			} else if (fs < .65f) {
				exp = 1;
			} else if (fs < .85f) {
				exp = 2;
			} else if (fs < 0.95f) {
				exp = 3;
			} else {
				exp = 4;
			} // else

			if (random.nextBoolean() ) {
				exp *= -1;
			} // if

			n.duration = (float) (baseBeat * Math.pow(2.0, exp));

			float up_prob = 0.5f;

			if (curInterval > 0) {
				up_prob = 0.3f;
			} else if (curInterval < 0) {
				up_prob = 0.7f;
			} // if

			int noteInterval = (int) (random.nextDouble() * 7); // IntRand(0,7);
			if (random.nextDouble() > up_prob) {
				noteInterval *= -1; // IntRand( -8, 0 );
			} // else

			curInterval += noteInterval;
			n.interval = curInterval;

			curVelocity += random.nextDouble() * 0.2 - 0.1; // FloatRand(-0.2f,0.2f);
			curVelocity = Math.min(curVelocity, 1);
			curVelocity = Math.max(curVelocity, 0);
			n.velocity = curVelocity;

			// notes.add(n);
			// when you come to a long note - insert the beginning phrase again
			if (notes.size() >= 7 && curInterval == 0) {
				lmark = notes.size();
			} // if

			if (exp >= 3) {
				if (lmark == -1) {
					if (exp == 2 && notes.size() >= 7) {
						lmark = notes.size();
					} // if

					if (exp == 3) {
						lmark = notes.size();
					} // if
				} else {
					if ((i - linsert) * exp >= 11) {
						linsert = i;
						for (int j = 0; j < lmark; j++) {
							notes.add(notes.get(j));
						} // for

						if (rtoggle > 0) {
							for (int j = 0; j < lmark; j++) {
								Note nn = new Note(notes.get(j));
								if (rtoggle == 2) {
									nn.interval *= -1;
								} else {
									nn.interval += 4;
								} // else
								notes.add(nn);
							} // for
						} // if

						rtoggle = (rtoggle + 1) % 3;
					} // if
				} // else
			} // if

		} // for

		// bring it home
		int finalVal = 3;

		while (curInterval != 0 && finalVal > 0) {
			int ni = (int) (random.nextDouble() * 3); // IntRand(0,3);
			if (curInterval > 0) {
				ni *= -1;
			} // if
			Note fn = new Note();
			fn.duration = baseBeat;
			curInterval += ni;
			fn.interval = curInterval;
			fn.velocity = 0.5f;
			notes.add(fn);
			finalVal--;
		} // while

		return notes;
	}

	int IntervalToHalfSteps(int interval) {
		// major scale

		if (interval > 0) {
			int half_steps = 12 * (int) (interval / 7);
			interval = interval % 7;
			int more_half_steps = 0;
			switch (interval) {
			case 0:
				more_half_steps = 0;
				break;

			case 1:
				more_half_steps = 2;
				break;

			case 2:
				more_half_steps = 4;
				break;

			case 3:
				more_half_steps = 5;
				break;

			case 4:
				more_half_steps = 7;
				break;

			case 5:
				more_half_steps = 9;
				break;

			case 6:
				more_half_steps = 11;
				break;

			} // switch

			return half_steps + more_half_steps;
		} // if

		int abs_interval = Math.abs(interval);
		int half_steps = 12 * (int) (abs_interval / 7);
		abs_interval = abs_interval % 7;
		int more_half_steps = 0;

		switch (abs_interval) {
		case 0:
			more_half_steps = 0;
			break;

		case 1:
			more_half_steps = 1;
			break;

		case 2:
			more_half_steps = 3;
			break;

		case 3:
			more_half_steps = 5;
			break;

		case 4:
			more_half_steps = 7;
			break;

		case 5:
			more_half_steps = 8;
			break;

		case 6:
			more_half_steps = 10;
			break;

		} // switch
		half_steps += more_half_steps;

		return -half_steps;

	} // int

	// create a list of tones
	MidiDevice device = null;
	Synthesizer synth = null;
	Receiver receiver = null;

	public void closeMidiClassic() {
		if (receiver != null)
			receiver.close();
		receiver = null;
		if (synth != null)
			synth.close();
		synth = null;
	}

	public void initMidiClassic() throws Exception {
		List<MidiDevice.Info> synthInfos = new ArrayList<MidiDevice.Info>();
		MidiDevice device = null;
		
		
		
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			device = MidiSystem.getMidiDevice(infos[i]);
			System.out.println("Midi device found: " + infos[i].getName() + ": " + infos[i].getDescription() + ": " + device.getClass().getName());
			
			//if (infos[i].getDescription().equals("External MIDI Port")) {
			if ( infos[i].getName().toLowerCase().contains( "gervill" ) ) {
				if (!device.isOpen())
					device.open();
				receiver = device.getReceiver();
		
			
			} //if
				//break;
			//} //
		} // for

		
		for (int ii = 1; ii < 16; ii++) {
			ShortMessage msg = new ShortMessage();
			//msg.setMessage(ShortMessage.PROGRAM_CHANGE, ii, (int) (35 * Math.random()), 0);
			msg.setMessage(ShortMessage.PROGRAM_CHANGE, ii, (int) (35 * Math.random()), 0);
			receiver.send(msg, -1);
		} // for

	}

	private void PlayMidiNoteClassic(int noteNumber, final int noteVelocity, final int midiChannel, final int noteDuration) throws Exception {
		if (receiver == null)
			return;

//		List<Integer> cnotes = notesPlaying.get(midiChannel);
//		if (cnotes == null) {
//			cnotes = new ArrayList<Integer>();
//			notesPlaying.put(midiChannel, cnotes);
//		} // if

		if (noteNumber < -35)
			return;

		final int cnote = (noteNumber + 128) % 128;

		ShortMessage msg = new ShortMessage();
//		if (cnotes.size() > 0) {
//			msg.setMessage(ShortMessage.NOTE_OFF, midiChannel, cnotes.remove(0).intValue(), 0);
//			receiver.send(msg, -1);
//		} // if

		msg.setMessage(ShortMessage.NOTE_ON, midiChannel, cnote, noteVelocity);
		receiver.send(msg, -1);
//		cnotes.add(noteNumber);
		//
	}
}
