package application;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;

import utils.Midi2WavRender;

public class Main extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) throws IOException {
		run();
		
	}
	public static void run() {
		
        JPanel mainPanel = new javax.swing.JPanel();
        JButton OpenButton = new javax.swing.JButton();
        JButton PlayButton = new javax.swing.JButton();
        JButton StopButton = new javax.swing.JButton();
        JButton ExportButton = new javax.swing.JButton();
		
		JFrame mainframe;
		
		try {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			
			UIManager.setLookAndFeel("org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
			
			mainframe = new JFrame();
			mainframe.setSize(400, 400);
			mainframe.setLayout(new GridLayout(10, 3));
			mainframe.setVisible(true);
			mainframe.add(OpenButton);
			OpenButton.setText("Open and play MIDI");
			OpenButton.setOpaque(true);
			mainframe.add(ExportButton);
			ExportButton.setOpaque(true);
			
			OpenButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JMenuItem load_midi = new JMenuItem("Load MIDI");
					ActionListener midi;
					try {
						midi = promptFolderChoose();
						load_midi.addActionListener(midi);
					} catch (MidiUnavailableException | InvalidMidiDataException | IOException e1) {
						System.out.print("Error!");
						e1.printStackTrace();
					}
					
				}
				
			});
			
			ExportButton.setText("Convert MIDI to WAV");
			ExportButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JMenuItem save_wav = new JMenuItem("Export to WAV");
					ActionListener wavsave;
						try {
							try {
								wavsave = promptOpenandSaveLocation();
								save_wav.addActionListener(wavsave);
							} catch (LineUnavailableException e1) {
								e1.printStackTrace();
							}
						} catch (MidiUnavailableException e1) {
							e1.printStackTrace();
						} catch (InvalidMidiDataException e1) {
							e1.printStackTrace();
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (UnsupportedAudioFileException e1) {
							e1.printStackTrace();
						}
						finally {
							
						}
				}

				public ActionListener promptOpenandSaveLocation() throws MidiUnavailableException, InvalidMidiDataException, IOException, UnsupportedAudioFileException, LineUnavailableException {
					
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					if (chooser.showOpenDialog(chooser) == JFileChooser.OPEN_DIALOG) {
						File selected = chooser.getSelectedFile();
						
						System.out.print("MIDI selected!");
						
						File filedir = new File("output.wav");
						
							Midi2WavRender mid2wav;
							Soundbank soundbank = MidiSystem.getSoundbank(new File("./soundbanks/OSRS.sf2"));
							Sequence sequence = MidiSystem.getSequence(selected);
							Midi2WavRender.render(soundbank, sequence, filedir);
							
							System.out.print("MIDI successfully converted to .wav!");
					}
					return null;
				}
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static ActionListener promptFolderChoose() throws MidiUnavailableException, InvalidMidiDataException, IOException {

		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		if (chooser.showOpenDialog(chooser) == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			playFile(selected);
		
			}
			return null;
		}
	public static void playFile(File selected) throws MidiUnavailableException, InvalidMidiDataException, IOException {
		
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
		}
	}