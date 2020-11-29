package main;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		new Main();
	}
	
	private Main() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
		GUI gui = new GUI();
	}
}
