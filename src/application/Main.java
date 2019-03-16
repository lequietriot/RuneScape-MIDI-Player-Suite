package application;

import java.awt.Dialog;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;

public class Main {
	
	Dialog dialog;
	GUI gui;
	
	public static void main(String[] args) throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException {
		new Main();
	}
	
	public Main() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException {
		gui = new GUI();
		
	}
}
