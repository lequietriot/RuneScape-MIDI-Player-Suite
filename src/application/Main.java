package application;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

public class Main extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) throws IOException {
		run();
		
	}
	public static void run() {
		
		Main frame = new Main();
		
		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
			frame.setTitle("RuneScape MIDI Player");
			frame.setVisible(true);

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setBounds(200, 200, 614, 454);
			
			JMenuBar menuBar = new JMenuBar();
			frame.setJMenuBar(menuBar);

			JMenu file_menu = new JMenu("File");
			menuBar.add(file_menu);
			
			JMenuItem load_midi = new JMenuItem("Load MIDI");
			ActionListener midi = promptFolderChoose();
			load_midi.addActionListener(midi);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static ActionListener promptFolderChoose() throws MidiUnavailableException, InvalidMidiDataException, IOException {


		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			openFile(selected);
		
			}
			return null;
		}
	public static void openFile(File selected) throws MidiUnavailableException, InvalidMidiDataException, IOException {
		
    Synthesizer synth = null;
    	Soundbank sbDefault = null;
    	Soundbank sbDeluxe = null;
    MidiDevice device = null;
		
        try {
            	synth = MidiSystem.getSynthesizer();
            	synth.open();
        }
        catch ( MidiUnavailableException e1) { e1.printStackTrace(); } 
        
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        synthesizer.open();
        synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
        synthesizer.loadAllInstruments(MidiSystem.getSoundbank(new File("./soundbanks/OSRS.sf2")));

        Sequencer sequencer = MidiSystem.getSequencer(false);
        sequencer.open();
        sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
        
			sequencer.setSequence(MidiSystem.getSequence(selected));
			sequencer.open();
			synth.open();
			sequencer.start();
			for (int i = 127; i > 0; i--)
			{
			try {
				Thread.sleep(127);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}