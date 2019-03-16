package application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.sampled.SourceDataLine;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileSystemView;
import application.utils.Midi2WavRender;

public class GUI {
	
	File midiFile;
	File soundsetFile;
	
	String midiUnsetError = "Please choose a MIDI to load!";
	String midiUnsetTitleBar = "MIDI not loaded!";
	String soundsUnsetError = "Please choose a Soundfont to load!";
	String soundsUnsetTitleBar = "Soundfont not loaded!";
	String defaultSoundfontPath;
	
	long pausedTime;
	long runningTime;
	long totalTime;
	
	Sequencer sequencer;
	Sequence sequence;
	Synthesizer synth;
	Sequence sequenceFixed;
	
	JFrame frame;
	JPanel panel;
	JPanel songPanel;
	JMenu fileMenu;
	JMenu preferencesMenu;
	
	JFileChooser chooseMID;
	JFileChooser chooseSf2;
	JFileChooser saveRepatchedMIDI;
	
	JButton startButton;
	JButton pauseButton;
	JButton stopButton;
	
	JButton fixMIDIButton;
	JButton renderMIDItoWavButton;
	
	JSlider songSlider;
	
	SourceDataLine sdl;
	
	@SuppressWarnings("static-access")
	
	public GUI() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException {
		frame = new JFrame("RuneScape MIDI Player");
		frame.setDefaultLookAndFeelDecorated(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(600, 400));
		panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		frame.setContentPane(panel);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		JPanel buttonsPanel = new JPanel();
		
		try {
			
			JMenuBar jMenuBar = new JMenuBar();
			jMenuBar.setSize(20, 20);
			jMenuBar.setVisible(true);
			
			frame.setJMenuBar(jMenuBar);
			
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setSize(100, 20);
			fileMenu.setVisible(true);
			
			fileMenu.add("Load MIDI File").addActionListener(new MIDILoader());
			fileMenu.add("Load SoundFont File").addActionListener(new SF2Loader());
			fileMenu.add("Render MIDI to Audio File").addActionListener(new RenderMIDIProcess());
			
			preferencesMenu = new JMenu();
			preferencesMenu.setText("Preferences");
			preferencesMenu.setSize(100, 20);
			preferencesMenu.setVisible(true);
			
			preferencesMenu.add("Set Default SoundFont").addActionListener(new DefaultSoundFontSetter());

			jMenuBar.add(fileMenu);
			jMenuBar.add(preferencesMenu);
			
			System.out.println("Application loaded successfully!");
		}
		
		finally {
			
			Path sf2PrefFile = Paths.get("./DefaultSoundfontPath.txt/");
			
			if (sf2PrefFile.toFile().exists() == true) {
				List<String> prefString = Files.readAllLines(sf2PrefFile);
				
				for (int s = 0; s < prefString.size(); s++) {
				String pathString = prefString.get(s).toString();
				System.out.println("Automatically set Soundfont to " + prefString);
				soundsetFile = new File(pathString);
				if (soundsetFile == null) {
					frame.add(new PopupMenu("The default SoundFont is either not set or was moved!"));
				}
			}
		}
			
			else if (sf2PrefFile.toFile().exists() == false) {
				FileOutputStream fos = new FileOutputStream("./DefaultSoundfontPath.txt/");
				DataOutputStream dos = new DataOutputStream(fos);
				dos.write(0);
				dos.flush();
				dos.close();
			}

			Init(buttonsPanel);
		}
	}

	public void Init(JPanel buttonsPanel) throws MidiUnavailableException, InvalidMidiDataException, IOException {
		
		startButton = new JButton();
		pauseButton = new JButton();
		stopButton = new JButton();
		fixMIDIButton = new JButton();
		
		startButton.addActionListener(new StartButtonListener());
		pauseButton.addActionListener(new PauseButtonListener());
		stopButton.addActionListener(new StopButtonListener());
		fixMIDIButton.addActionListener(new FixButtonListener());
		
		buttonsPanel.add(startButton);
		startButton.setText("Play");
		startButton.setVisible(true);
		
		buttonsPanel.add(pauseButton);
		pauseButton.setText("Pause");
		pauseButton.setVisible(true);
		
		buttonsPanel.add(stopButton);
		stopButton.setText("Stop");
		stopButton.setVisible(true);
		
		buttonsPanel.add(fixMIDIButton);
		fixMIDIButton.setText("Fix MIDI (Unfinished)");
		fixMIDIButton.setEnabled(false);
		fixMIDIButton.setVisible(true);
		
		buttonsPanel.setVisible(true);
		buttonsPanel.setBackground(Color.LIGHT_GRAY);
		
		songPanel = new JPanel();
		songSlider = new JSlider();
		
		songSlider.setEnabled(false);
		songSlider.setValue(0);
		songPanel.add(songSlider);
		songPanel.setBackground(Color.WHITE);
		songPanel.setVisible(true);
		
		buttonsPanel.add(songPanel);
		frame.add(buttonsPanel);
		frame.pack();
	}

	public void LoadSoundFont(JFrame frame) {
		chooseSf2 = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		chooseSf2.setSize(400, 200);
		chooseSf2.setDialogTitle("Please choose a Soundfont File");
		chooseSf2.setVisible(true);
		frame.add(chooseSf2);
		int returnValue = chooseSf2.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			soundsetFile = chooseSf2.getSelectedFile();
			defaultSoundfontPath = chooseSf2.getSelectedFile().getPath();
		}
	}

	public void LoadMIDI(JFrame frame) {
		chooseMID = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		chooseMID.setSize(400, 200);
		chooseMID.setDialogTitle("Please choose a MIDI File");
		chooseMID.setVisible(true);
		frame.add(chooseMID);
		int returnValue = chooseMID.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			midiFile = chooseMID.getSelectedFile();
		}
	}
	
	public class MIDILoader implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			LoadMIDI(frame);
		}
	}
	
	public class SF2Loader implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			LoadSoundFont(frame);
		}
	}
	
	public class DefaultSoundFontSetter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			LoadSoundFont(frame);
			
			File preferences = new File("./DefaultSoundfontPath.txt/");
			FileOutputStream fos;
			
			try {
				fos = new FileOutputStream(preferences);
				DataOutputStream dos = new DataOutputStream(fos);
				dos.writeBytes(defaultSoundfontPath);
				dos.flush();
				dos.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public class StartButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			
			if (sequencer == null) {
				startButton.setEnabled(true);
			}
			
			try {
				Soundbank soundbank = MidiSystem.getSoundbank(soundsetFile);
				sequencer = MidiSystem.getSequencer(false);
				sequence = MidiSystem.getSequence(midiFile);
				synth = MidiSystem.getSynthesizer();
				
				sequencer.open();
				synth.open();
				synth.loadAllInstruments(soundbank);
				
				sequencer.getTransmitter().setReceiver(synth.getReceiver());
				sequencer.setSequence(sequence);
				
				if (pausedTime == 0) {
					sequencer.start();

					if (sequencer.isRunning()) {
						startButton.setEnabled(false);
						pauseButton.setEnabled(true);

						totalTime = sequence.getMicrosecondLength();
					}
				}
				
				else if (pausedTime != 0) {
					sequencer.setMicrosecondPosition(pausedTime);
					sequencer.start();

					if (sequencer.isRunning()) {
						startButton.setEnabled(false);
						pauseButton.setEnabled(true);
						
						totalTime = sequence.getMicrosecondLength();
					}
				}
				
			} catch (MidiUnavailableException e1) {
				e1.printStackTrace();
			} catch (InvalidMidiDataException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public class PauseButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pausedTime = sequencer.getMicrosecondPosition();
			sequencer.stop();
			
			if (pausedTime != 0) {
				pauseButton.setEnabled(false);
			}
			
			if (sequencer.isRunning() == false) {
				startButton.setEnabled(true);
			}
		}
	}
	
	public class StopButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pausedTime = 0;
			sequencer.stop();
			
			if (sequencer.isRunning() == false) {
				pauseButton.setEnabled(true);
				startButton.setEnabled(true);
			}
		}
	}
	
	public class RenderMIDIProcess implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Soundbank soundbank;
			try {
				soundbank = MidiSystem.getSoundbank(soundsetFile);
				sequence = MidiSystem.getSequence(midiFile);
				Midi2WavRender.render(soundbank, sequence, new File("./Rendered.wav/"));
				System.out.print("Successfully rendered MIDI to Audio!");
			} catch (InvalidMidiDataException | IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public class FixButtonListener implements ActionListener {
		
		int bankLSBValue;
		int nextFreeChannel;

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				saveFixedMIDI();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		public void saveFixedMIDI() throws IOException {
			saveRepatchedMIDI = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
			saveRepatchedMIDI.setSize(400, 200);
			saveRepatchedMIDI.setDialogTitle("Please save the fixed MIDI");
			saveRepatchedMIDI.setVisible(true);
			frame.add(saveRepatchedMIDI);
			int returnValue = saveRepatchedMIDI.showSaveDialog(null);
			if (returnValue == JFileChooser.SAVE_DIALOG) {
				String savedName = saveRepatchedMIDI.getName();
				File fixedMIDI = new File(savedName);
				FileOutputStream fos = new FileOutputStream(fixedMIDI);
				MidiSystem.write(sequenceFixed, 0, fos);
			}
		}

	public Sequence adjustMIDIforSequencer(Sequence sequence) throws InvalidMidiDataException {
			
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					MidiEvent midiEvent = track.get(i);
					MidiMessage midiMessage = midiEvent.getMessage();
					if (midiMessage instanceof ShortMessage) {
						ShortMessage shortMessage = (ShortMessage) midiMessage;
						
						//Handle Control Change: Bank Select
						if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
							int channel = shortMessage.getChannel();
							int bytes = shortMessage.getData1();
							int value = shortMessage.getData2();
							
							//Bank LSB - Used in RuneScape to select a custom bank
							if (bytes == 32) {
								bankLSBValue = value;
							}
							
							if (bytes == 0) {
								if (value != bankLSBValue) {
									shortMessage.setMessage(ShortMessage.CONTROL_CHANGE, channel, bytes, bankLSBValue);
								}
							}
						}
						
						if (shortMessage.getChannel() != 9 && (shortMessage.getData1() == 32 && shortMessage.getData2() == 1)) {
							
							shortMessage.setMessage(ShortMessage.NOTE_ON, 9, shortMessage.getData1(), shortMessage.getData2());
							shortMessage.setMessage(ShortMessage.NOTE_OFF, 9, shortMessage.getData1(), shortMessage.getData2());
							shortMessage.setMessage(ShortMessage.CONTROL_CHANGE, 9, shortMessage.getData1(), shortMessage.getData2());
							shortMessage.setMessage(ShortMessage.PROGRAM_CHANGE, 9, shortMessage.getData1(), shortMessage.getData2());
							shortMessage.setMessage(ShortMessage.CHANNEL_PRESSURE, 9, shortMessage.getData1(), shortMessage.getData2());
						}
						

						else if (shortMessage.getChannel() == 9 & bankLSBValue != 1) {
							
							nextFreeChannel = 0;
							
							while (nextFreeChannel == i) {
								nextFreeChannel++;
								while (nextFreeChannel == 9) {
									nextFreeChannel++;
									if (nextFreeChannel != i && nextFreeChannel != 9) {
										continue;
									}
								}
							}
							
							shortMessage.setMessage(ShortMessage.NOTE_ON, nextFreeChannel, shortMessage.getData1(), shortMessage.getData2());
							shortMessage.setMessage(ShortMessage.NOTE_OFF, nextFreeChannel, shortMessage.getData1(), shortMessage.getData2());
							shortMessage.setMessage(ShortMessage.CONTROL_CHANGE, nextFreeChannel, shortMessage.getData1(), shortMessage.getData2());
							shortMessage.setMessage(ShortMessage.PROGRAM_CHANGE, nextFreeChannel, shortMessage.getData1(), shortMessage.getData2());
							shortMessage.setMessage(ShortMessage.CHANNEL_PRESSURE, nextFreeChannel, shortMessage.getData1(), shortMessage.getData2());
						}
					}
				}
			}
			return sequence;
		}
	}
}
