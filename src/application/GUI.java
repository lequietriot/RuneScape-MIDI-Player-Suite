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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;

import application.utils.Midi2WavRender;
import application.utils.MidiFixerOSRS;

public class GUI {
	
	File midiFile;
	File soundsetFile;
	
	String defaultSoundfontPath;
	
	boolean fixAttempting;
	
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
	JMenu utilityMenu;
	
	JFileChooser chooseMID;
	JFileChooser chooseSf2;
	JFileChooser saveRepatchedMIDI;
	
	JButton startButton;
	JButton pauseButton;
	JButton stopButton;
	JButton renderMIDItoWavButton;
	
	JSlider songSlider;
	
	JTextPane songSliderInfo;
	
	JCheckBox fixAttempt;
	
	@SuppressWarnings("static-access")
	
	public GUI() throws MidiUnavailableException, InvalidMidiDataException, IOException, InterruptedException {
		frame = new JFrame("RuneScape MIDI Player");
		frame.setDefaultLookAndFeelDecorated(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(520, 200));
		panel = new JPanel(new BorderLayout());
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createEmptyBorder());
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

			utilityMenu = new JMenu();
			utilityMenu.setText("Tools");
			utilityMenu.setSize(100, 20);
			utilityMenu.setVisible(true);
			
			utilityMenu.add("Fix MIDI File (OSRS Version)").addActionListener(new FixButtonListenerOSRS());
			
			jMenuBar.add(fileMenu);
			jMenuBar.add(preferencesMenu);
			jMenuBar.add(utilityMenu);
			
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
		
		startButton.addActionListener(new StartButtonListener());
		pauseButton.addActionListener(new PauseButtonListener());
		stopButton.addActionListener(new StopButtonListener());
		
		buttonsPanel.add(startButton);
		startButton.setText("Play");
		startButton.setVisible(true);
		
		buttonsPanel.add(pauseButton);
		pauseButton.setText("Pause");
		pauseButton.setVisible(true);
		
		buttonsPanel.add(stopButton);
		stopButton.setText("Stop");
		stopButton.setVisible(true);

		buttonsPanel.setVisible(true);
		buttonsPanel.setBackground(Color.LIGHT_GRAY);
		
		songPanel = new JPanel();
		songSlider = new JSlider();
		songSliderInfo = new JTextPane();
		
		if (midiFile == null) {
			songSlider.setEnabled(false);
			songSlider.setToolTipText("MIDI file not loaded!");
		}
		
		songSlider.setValue(0);
		songSlider.addChangeListener(new SongSliderListener());
		songPanel.add(songSlider);
		
		if (midiFile == null) {
			songSliderInfo.setText("We're not playing anything! Try loading a MIDI.");
		}
		songSliderInfo.setBackground(Color.LIGHT_GRAY);
		songSliderInfo.setSelectedTextColor(Color.BLACK);
		songSliderInfo.setEnabled(true);
		songSliderInfo.setEditable(false);
		songSliderInfo.setVisible(true);
		songPanel.add(songSliderInfo);
		songPanel.setBackground(Color.LIGHT_GRAY);
		songPanel.setAlignmentX(0);
		songPanel.setAlignmentY(0);
		songPanel.setVisible(true);
		
		fixAttempt = new JCheckBox();
		fixAttempt.setText("Attempt to fix songs");
		fixAttempt.addActionListener(new FixAttempter());
		fixAttempt.setVisible(true);
		
		buttonsPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		buttonsPanel.add(songPanel);
		buttonsPanel.add(fixAttempt);
		frame.add(buttonsPanel);
		frame.pack();
	}

	public void LoadSoundFont(JFrame frame) {
		chooseSf2 = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
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
		chooseMID = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
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
			
			if (midiFile.exists()) {
				songSlider.setEnabled(true);
				songSliderInfo.setText("Song loaded: " + midiFile.getName());
			}
		}
	}
	
	public class SongSliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			int i = songSlider.getValue();
			
			if (sequence == null) {
				try {
					sequence = MidiSystem.getSequence(midiFile);
				} catch (InvalidMidiDataException | IOException e1) {
					e1.printStackTrace();
				}
			}
			
			totalTime = sequence.getMicrosecondLength() / 1000000;
			songSlider.setMinimum(0);
			songSlider.setMaximum((int) totalTime);
			songSlider.setToolTipText("Time: " + i + " / " + totalTime + " (In total seconds)");
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
		
		int bankLSBSelect;
		int position = 0;
		int chPosition = 0;
		int drumChannel = 9;

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
				
				if (fixAttempting == false) {
					sequencer.setSequence(sequence);
				}
				
				else if (fixAttempting == true) {
					sequencer.setSequence(adjustForPlay(sequence));
				}
				
				if (pausedTime == 0) {
					sequencer.start();

					if (sequencer.isRunning()) {
						startButton.setEnabled(false);
						pauseButton.setEnabled(true);
					}
				}
				
				else if (pausedTime != 0) {
					sequencer.setMicrosecondPosition(pausedTime);
					sequencer.start();

					if (sequencer.isRunning()) {
						startButton.setEnabled(false);
						pauseButton.setEnabled(true);
					}
				}
				
				if (sequencer.isRunning()) {
					Timer timer = new Timer(100, new TimerListener());
					timer.start();
				}
				
			} catch (MidiUnavailableException e1) {
				e1.printStackTrace();
			} catch (InvalidMidiDataException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		public Sequence adjustForPlay(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					MidiEvent midiEvent = track.get(i);
					MidiMessage midiMessage = midiEvent.getMessage();
					
					if (midiMessage instanceof ShortMessage) {
						ShortMessage allShortMessages = (ShortMessage) midiMessage;
						
						if (chPosition != 16) {
							
							if (getBankLSB(midiEvent) == 1) {
							
								if (allShortMessages.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									allShortMessages.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CONTROL_CHANGE) {
									allShortMessages.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_ON) {
									allShortMessages.setMessage(ShortMessage.NOTE_ON, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_OFF) {
									allShortMessages.setMessage(ShortMessage.NOTE_OFF, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.PITCH_BEND) {
									allShortMessages.setMessage(ShortMessage.PITCH_BEND, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.POLY_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
							}
						
							else if (getBankLSB(midiEvent) != 1) {
							
								if (allShortMessages.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									allShortMessages.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CONTROL_CHANGE) {
									allShortMessages.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_ON) {
									allShortMessages.setMessage(ShortMessage.NOTE_ON, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_OFF) {
									allShortMessages.setMessage(ShortMessage.NOTE_OFF, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.PITCH_BEND) {
									allShortMessages.setMessage(ShortMessage.PITCH_BEND, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.POLY_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.POLY_PRESSURE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
							}
						}
					}
				}	
			}
			return sequence;
		}

		public int getBankLSB(MidiEvent midiEvent) throws InvalidMidiDataException {

			MidiMessage midiMessage = midiEvent.getMessage();
			
			if (midiMessage instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) midiMessage;
				if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
					if (sm.getData1() == 32) {
						bankLSBSelect = sm.getData2();
						
						if (sm.getChannel() != 16) {
							chPosition = sm.getChannel();
							if (chPosition == 9 & bankLSBSelect != 1) {
								chPosition--;
							}
						}
					}
					
					if (sm.getData1() == 0) {
						sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1(), bankLSBSelect);
					}
				}
			}
			return bankLSBSelect;
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
			
			if (!sequencer.isRunning()) {
				Timer timer = new Timer(100, new TimerListener());
				timer.stop();
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
			
			if (!sequencer.isRunning()) {
				Timer timer = new Timer(100, new TimerListener());
				timer.stop();
				songSlider.setValue(0);
			}
		}
	}

	public class TimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int position = (int) (sequencer.getMicrosecondPosition() / 1000000);
			songSlider.setValue(position);
		}
	}
	
	public class FixAttempter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fixAttempt.isEnabled() == true) {
				fixAttempting = true;
			}
			
			else if (fixAttempt.isEnabled() == false) {
				fixAttempting = false;
			}
		}
	}
	
	public class RenderMIDIProcess implements ActionListener {

		int bankLSBSelect;
		int drumChannel = 9;
		int position = 0;
		int chPosition = 0;

		@Override
		public void actionPerformed(ActionEvent e) {
			Soundbank soundbank;
			try {
				soundbank = MidiSystem.getSoundbank(soundsetFile);
				sequence = MidiSystem.getSequence(midiFile);
				
				if (fixAttempting == false) {
					Midi2WavRender.render(soundbank, sequence, new File("./Rendered.wav/"));
				}
				
				else if (fixAttempting == true) {
					Midi2WavRender.render(soundbank, adjustForRender(sequence), new File("./Rendered.wav/"));	
				}
				System.out.println("Successfully rendered MIDI to Audio!");
			} catch (InvalidMidiDataException | IOException e1) {
				e1.printStackTrace();
			}
		}

		public Sequence adjustForRender(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					MidiEvent midiEvent = track.get(i);
					MidiMessage midiMessage = midiEvent.getMessage();
					
					if (midiMessage instanceof ShortMessage) {
						ShortMessage allShortMessages = (ShortMessage) midiMessage;
						
						if (chPosition != 16) {
							
							if (getBankLSB(midiEvent) == 1) {
							
								if (allShortMessages.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									allShortMessages.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CONTROL_CHANGE) {
									allShortMessages.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_ON) {
									allShortMessages.setMessage(ShortMessage.NOTE_ON, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_OFF) {
									allShortMessages.setMessage(ShortMessage.NOTE_OFF, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.PITCH_BEND) {
									allShortMessages.setMessage(ShortMessage.PITCH_BEND, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.POLY_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
							}
						
							else if (getBankLSB(midiEvent) != 1) {
							
								if (allShortMessages.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									allShortMessages.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CONTROL_CHANGE) {
									allShortMessages.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_ON) {
									allShortMessages.setMessage(ShortMessage.NOTE_ON, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_OFF) {
									allShortMessages.setMessage(ShortMessage.NOTE_OFF, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.PITCH_BEND) {
									allShortMessages.setMessage(ShortMessage.PITCH_BEND, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.POLY_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.POLY_PRESSURE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
							}
						}
					}
				}	
			}
			return sequence;
		}

		public int getBankLSB(MidiEvent midiEvent) throws InvalidMidiDataException {

			MidiMessage midiMessage = midiEvent.getMessage();
			
			if (midiMessage instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) midiMessage;
				if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
					if (sm.getData1() == 32) {
						bankLSBSelect = sm.getData2();
						
						if (sm.getChannel() != 16) {
							chPosition = sm.getChannel();
							if (chPosition == 9 & bankLSBSelect != 1) {
								chPosition--;
							}
						}
					}
					
					if (sm.getData1() == 0) {
						sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1(), bankLSBSelect);
					}
				}
			}
			return bankLSBSelect;
		}
	}
	
	public class FixButtonListenerOSRS implements ActionListener {

		int bankLSBSelect;
		int drumChannel = 9;
		int position = 0;
		int chPosition;

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				sequence = MidiSystem.getSequence(midiFile);
				
				MidiFixerOSRS.returnFixedMIDI(fixMIDI(sequence));
				
				System.out.println("Sucessfully wrote the fixed Old School RuneScape MIDI to file!");
				
			} catch (InvalidMidiDataException | IOException e1) {
				e1.printStackTrace();
			}
		}

		public Sequence fixMIDI(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					MidiEvent midiEvent = track.get(i);
					MidiMessage midiMessage = midiEvent.getMessage();
					
					if (midiMessage instanceof ShortMessage) {
						ShortMessage allShortMessages = (ShortMessage) midiMessage;
						
						if (chPosition != 16) {
							
							if (getBankLSB(midiEvent) == 1) {
							
								if (allShortMessages.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									allShortMessages.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CONTROL_CHANGE) {
									allShortMessages.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_ON) {
									allShortMessages.setMessage(ShortMessage.NOTE_ON, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_OFF) {
									allShortMessages.setMessage(ShortMessage.NOTE_OFF, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.PITCH_BEND) {
									allShortMessages.setMessage(ShortMessage.PITCH_BEND, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.POLY_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, allShortMessages.getData1(), allShortMessages.getData2());
								}
							}
						
							else if (getBankLSB(midiEvent) != 1) {
							
								if (allShortMessages.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									allShortMessages.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CONTROL_CHANGE) {
									allShortMessages.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_ON) {
									allShortMessages.setMessage(ShortMessage.NOTE_ON, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.NOTE_OFF) {
									allShortMessages.setMessage(ShortMessage.NOTE_OFF, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.PITCH_BEND) {
									allShortMessages.setMessage(ShortMessage.PITCH_BEND, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
								
								if (allShortMessages.getCommand() == ShortMessage.POLY_PRESSURE) {
									allShortMessages.setMessage(ShortMessage.POLY_PRESSURE, chPosition, allShortMessages.getData1(), allShortMessages.getData2());
								}
							}
						}
					}
				}	
			}
			return sequence;
		}

		public int getBankLSB(MidiEvent midiEvent) throws InvalidMidiDataException {

			MidiMessage midiMessage = midiEvent.getMessage();
			
			if (midiMessage instanceof ShortMessage) {
				ShortMessage sm = (ShortMessage) midiMessage;
				if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
					if (sm.getData1() == 32) {
						bankLSBSelect = sm.getData2();
						
						if (sm.getChannel() != 16) {
							chPosition = sm.getChannel();
							
							if (chPosition == 9 & bankLSBSelect != 1) {
								chPosition--;
							}
						}
					}
					
					if (sm.getData1() == 0) {
						sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1(), bankLSBSelect);
					}
				}
			}
			return bankLSBSelect;
		}
	}
}
