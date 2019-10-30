package main;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException {
		new Main();
	}
	
	private Main() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException {
		GUI gui = new GUI();
	}
}
