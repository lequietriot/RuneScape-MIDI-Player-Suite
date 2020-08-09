package main;

import com.sun.media.sound.AudioSynthesizer;
import org.displee.CacheLibrary;
import org.displee.cache.index.Index;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI {
	
	private File midiFile;
	private File soundsetFile;

    private static CacheLibrary cacheLibrary;

	private String defaultSoundfontPath;

	private boolean fixAttemptingOS;
	private boolean fixAttemptingHD;

	private long pausedTime;
	private long runningTime;
	private long totalTime;

	private Sequence sequence;

	private Sequencer sequencer;

	private Synthesizer synthesizer;

	private Sequence sequenceFixed;

	private JFrame frame;
	private JFrame cacheFrame;
	private static JFrame noteGeneratorFrame;

	private JPanel panel;
	private JPanel songPanel;

	private JMenu fileMenu;
	private JMenu preferencesMenu;
	private JMenu utilityMenu;
	private JMenu playlistMenu;

	private JFileChooser chooseMID;
	private JFileChooser chooseSf2;
    private JFileChooser chooseCache;
	private JFileChooser saveRepatchedMIDI;

	private JButton startButton;
	private JButton pauseButton;
	private JButton stopButton;
	private JButton loopButton;
	private JButton testButton;
	private JButton renderMIDItoWavButton;

	private JSlider songSlider;

	private JTextPane songSliderInfo;

	private JCheckBox fixAttemptOS;
	private JCheckBox fixAttemptHD;

	private ControllerEventListener volumeListener;
	private ControllerEventListener retriggerListener;
	private boolean retriggerEffect = false;
	private int retriggerValue;

	private JTextPane volumeInfo;
	private JTextPane sampleRateInfo;
	private int volume = 256;

	private boolean loopMode = false;
	private MidiPcmStream midiPcmStream;

	private MidiDevice externalDevice;
	private Transmitter transmitter;
	private Receiver receiver;

	GUI() throws IOException {

		frame = new JFrame("RuneScape MIDI Player");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(700, 240));
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
			fileMenu.add("Load RuneScape Cache").addActionListener(new CacheLoader());
			fileMenu.add("Render MIDI to Audio File").addActionListener(new RenderMIDIProcess());
			
			preferencesMenu = new JMenu();
			preferencesMenu.setText("Preferences");
			preferencesMenu.setSize(100, 20);
			preferencesMenu.setVisible(true);

			preferencesMenu.add("Set Default SoundFont").addActionListener(new DefaultSoundFontSetter());
			preferencesMenu.add("SoundBank - Audio Settings").addActionListener(new SoundBankSettings());

			utilityMenu = new JMenu();
			utilityMenu.setText("Tools");
			utilityMenu.setSize(100, 20);
			utilityMenu.setVisible(true);

			utilityMenu.add("Encode Data - MIDI Music...").addActionListener(new MidiEncoder());
			//utilityMenu.add("Generate Sound - MIDI Note...").addActionListener(new MidiNoteGenerator());
			//utilityMenu.add("Generate Random Music").addActionListener(new RandomMidiGenerator());
			//utilityMenu.add("Modify Existing Music").addActionListener(new MidiTransposer());

			utilityMenu.add("Dump Data - MIDI Music...").addActionListener(new MidiDumper());
			utilityMenu.add("Dump Data - Sound Effects...").addActionListener(new SfxDumper());
            utilityMenu.add("Dump Data - Sound Bank Samples...").addActionListener(new MusicSampleDumper());

			utilityMenu.add("Dump Data - RS3 Sound Effects...").addActionListener(new StreamSoundEffectDumper());

			utilityMenu.add("Fix MIDI File (OS Version)").addActionListener(new FixButtonListenerOSRS());
			utilityMenu.add("Fix MIDI File (HD Version)").addActionListener(new FixButtonListenerRSHD());

			utilityMenu.add("Convert Data - Sound Bank to SoundFont").addActionListener(new SoundFontCreator());

			utilityMenu.add("Dump Raw Soundbank").addActionListener(new SoundDumper());
			utilityMenu.add("Pack Raw Soundbank").addActionListener(new SoundPacker());

			utilityMenu.add("---");
			utilityMenu.add("Test MIDI with selected SoundBank").addActionListener(new SoundBankSongTester());
			utilityMenu.add("Test MIDI with a custom SoundBank").addActionListener(new CustomSoundBankSongTester());
			utilityMenu.add("Write song to file using SoundBank").addActionListener(new SoundBankSongDumper());
			utilityMenu.add("Batch Convert with SoundBank").addActionListener(new BatchConverter());
			utilityMenu.add("Encode Data - Soundbank Sample...").addActionListener(new SoundBankEncoder());

			playlistMenu = new JMenu();
			playlistMenu.setText("Playlist");
			playlistMenu.setSize(100, 20);
			playlistMenu.setVisible(true);

			playlistMenu.add("Slot 1");

			jMenuBar.add(fileMenu);
			jMenuBar.add(preferencesMenu);
			jMenuBar.add(utilityMenu);
			jMenuBar.add(playlistMenu);
			
			System.out.println("Application loaded successfully!");
		}
		
		finally {

			Path sf2PrefFile = Paths.get("./DefaultSoundfontPath.txt/");
            Path cachePrefFile = Paths.get("./DefaultCachePath.txt/");
			
			if (sf2PrefFile.toFile().exists()) {
				List<String> prefString = Files.readAllLines(sf2PrefFile);
				
				for (int s = 0; s < prefString.size(); s++) {
				String pathString = prefString.get(s);
				System.out.println("Automatically set Soundfont to " + prefString);
				soundsetFile = new File(pathString);

				if (!soundsetFile.exists()) {
					frame.add(new PopupMenu("The default SoundFont is either not set or was moved!"));
				}
			}
		}
			if (cachePrefFile.toFile().exists()) {
                List<String> cachePrefString = Files.readAllLines(cachePrefFile);

                for (int s = 0; s < cachePrefString.size(); s++) {
                    String pathString = cachePrefString.get(s);
                    System.out.println("Automatically set Cache path to " + pathString);
                    cacheLibrary = new CacheLibrary(pathString);
                }
            }
			
			else if (!sf2PrefFile.toFile().exists()) {
				FileOutputStream fos = new FileOutputStream("./DefaultSoundfontPath.txt/");
				DataOutputStream dos = new DataOutputStream(fos);
				dos.write(0);
				dos.flush();
				dos.close();
			}

			init(buttonsPanel);
		}
	}

	private void init(JPanel buttonsPanel) {

		JPanel infoPanel = new JPanel();

		startButton = new JButton();
		pauseButton = new JButton();
		stopButton = new JButton();
		loopButton = new JButton();
		testButton = new JButton();
		
		startButton.addActionListener(new StartButtonListener());
		pauseButton.addActionListener(new PauseButtonListener());
		stopButton.addActionListener(new StopButtonListener());
		loopButton.addActionListener(new LoopButtonListener());
		testButton.addActionListener(new TestButtonListener());
		
		buttonsPanel.add(startButton);
		startButton.setText("Play");
		startButton.setVisible(true);
		
		buttonsPanel.add(pauseButton);
		pauseButton.setText("Pause");
		pauseButton.setVisible(true);
		
		buttonsPanel.add(stopButton);
		stopButton.setText("Stop");
		stopButton.setVisible(true);

		buttonsPanel.add(loopButton);

		testButton.setText("Test");
		testButton.setVisible(true);
		buttonsPanel.add(testButton);

		if (loopMode) {
			loopButton.setText("Loop Mode: On");
		}
		else {
			loopButton.setText("Loop Mode: Off");
		}

		loopButton.setVisible(true);

		buttonsPanel.setVisible(true);
		buttonsPanel.setBackground(Color.LIGHT_GRAY);
		
		songPanel = new JPanel();
		songSlider = new JSlider();
		songSliderInfo = new JTextPane();
		volumeInfo = new JTextPane();
		sampleRateInfo = new JTextPane();
		
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

		volumeInfo.setBackground(Color.LIGHT_GRAY);
		volumeInfo.setSelectedTextColor(Color.BLACK);
		volumeInfo.setText("Sound Bank Volume is: " + volume + " (" + ((volume / 256) * 100) + "%) |");
		volumeInfo.setEnabled(true);
		volumeInfo.setEditable(false);
		volumeInfo.setVisible(true);
		volumeInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
		volumeInfo.setAlignmentY(Component.BOTTOM_ALIGNMENT);

		sampleRateInfo.setBackground(Color.LIGHT_GRAY);
		sampleRateInfo.setSelectedTextColor(Color.BLACK);
		sampleRateInfo.setText("Sample Rate is: " + PcmPlayer.pcmPlayer_sampleRate);
		sampleRateInfo.setEnabled(true);
		sampleRateInfo.setEditable(false);
		sampleRateInfo.setVisible(true);
		sampleRateInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
		sampleRateInfo.setAlignmentY(Component.BOTTOM_ALIGNMENT);

		songPanel.add(songSliderInfo);
		songPanel.setBackground(Color.LIGHT_GRAY);
		songPanel.setAlignmentX(0);
		songPanel.setAlignmentY(0);
		songPanel.setVisible(true);
		
		fixAttemptOS = new JCheckBox();
		fixAttemptOS.setText("Attempt to fix songs (OSRS)");
		fixAttemptOS.addActionListener(new FixAttempterOS());
		fixAttemptOS.setVisible(true);
		
		fixAttemptHD = new JCheckBox();
		fixAttemptHD.setText("Attempt to fix songs (RSHD)");
		fixAttemptHD.addActionListener(new FixAttempterHD());
		fixAttemptHD.setVisible(true);
		
		buttonsPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		buttonsPanel.add(songPanel);
		buttonsPanel.add(fixAttemptOS);
		buttonsPanel.add(fixAttemptHD);

		infoPanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
		infoPanel.setLocation(0, 120);
		infoPanel.setBackground(Color.LIGHT_GRAY);
		infoPanel.add(volumeInfo);
		infoPanel.add(sampleRateInfo);

		frame.add(infoPanel);
		frame.add(buttonsPanel);
		frame.pack();
	}

	private void LoadSoundFont(JFrame frame) {
		chooseSf2 = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
		chooseSf2.setSize(400, 200);
		chooseSf2.setDialogTitle("Please choose a SoundFont File");
		chooseSf2.setVisible(true);
		frame.add(chooseSf2);
		int returnValue = chooseSf2.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			soundsetFile = chooseSf2.getSelectedFile();
			defaultSoundfontPath = chooseSf2.getSelectedFile().getPath();
		}
	}

	private void initSynthesizers() throws MidiUnavailableException, InvalidMidiDataException, IOException {

		/**
		 System.out.println("Initializing Synthesizers, please wait...");

		 if (synthesizers == null) {
		 synthesizers = new Synthesizer[MidiSystem.getSequence(midiFile).getTracks().length];
		 }

		 for (int synth = 0; synth < MidiSystem.getSequence(midiFile).getTracks().length; synth++) {

		 synthesizers[synth] = MidiSystem.getSynthesizer();
		 synthesizers[synth].open();
		 Synthesizers[synth].unloadAllInstruments(synthesizers[synth].getDefaultSoundbank());
		 synthesizers[synth].loadAllInstruments(MidiSystem.getSoundbank(soundsetFile));
		 System.out.println("Loaded Synthesizer #" + synth + "!");
		 }
		 **/

		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

		for (MidiDevice.Info info : infos) {

			MidiDevice midiDevice = MidiSystem.getMidiDevice(info);

			if (midiDevice.getDeviceInfo().getName().contains("Bus 1")) {

				if (transmitter == null) {
					transmitter = MidiSystem.getTransmitter();
					externalDevice = midiDevice;
					externalDevice.open();
				}

				if (receiver == null) {
					receiver = midiDevice.getReceiver();
				}

				if (transmitter != null && receiver != null) {
					MidiSystem.getTransmitter().setReceiver(receiver);
				}
			}
		}
	}

	private void LoadMIDI(JFrame frame) throws MidiUnavailableException, InvalidMidiDataException, IOException {
		chooseMID = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
		chooseMID.setSize(400, 200);
		chooseMID.setDialogTitle("Please choose a MIDI File");
		chooseMID.setVisible(true);
		frame.add(chooseMID);
		int returnValue = chooseMID.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			midiFile = chooseMID.getSelectedFile();
			initSynthesizers();
		}
	}

	public class MIDILoader implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				LoadMIDI(frame);
			} catch (MidiUnavailableException | InvalidMidiDataException | IOException ex) {
				ex.printStackTrace();
			}

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

			else {
				totalTime = sequence.getMicrosecondLength() / 1000000;
				songSlider.setMinimum(0);
				songSlider.setMaximum((int) totalTime);
				songSlider.setToolTipText("Time: " + i + " / " + totalTime + " (In total seconds)");
			}
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
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public class StartButtonListener implements ActionListener {
		
		int bankLSB;

		int drumChannel = 9;
		int chPosition = -1;

		boolean customBank;
		long loopStart = 0;
		long loopEnd = 0;
		StringBuilder loopMarker;

		public void actionPerformed(ActionEvent e) {

			try {
				MidiLoader midiLoader = new MidiLoader(MidiSystem.getSoundbank(soundsetFile), MidiSystem.getSequence(midiFile).getTracks().length);
				midiLoader.load(midiFile);
			} catch (MidiUnavailableException | IOException | InvalidMidiDataException midiUnavailableException) {
				midiUnavailableException.printStackTrace();
			}
			/**
			try {
				Thread.sleep(50);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}

			startButton.setEnabled(true);

			try {

				sequence = MidiSystem.getSequence(midiFile);

				sequencer = MidiSystem.getSequencer(false);
				sequencer.open();
				sequencer.setSequence(sequence);
				sequencer.getTransmitter().setReceiver(externalDevice.getReceiver());

				if (loopMode) {
					setLoop();
				}

				if (pausedTime == 0) {

					sequencer.start();

					if (sequencer.isRunning()) {
						startButton.setEnabled(false);
						pauseButton.setEnabled(true);
					}
				}

				else {

					sequencer.setMicrosecondPosition(pausedTime);
					sequencer.start();

					if (sequencer.isRunning()) {
						startButton.setEnabled(false);
						pauseButton.setEnabled(true);
					}
				}

				if (sequencer.isRunning()) {
					Timer timer = new Timer(0, new TimerListener());
					timer.start();
				}
			} catch (MidiUnavailableException | InvalidMidiDataException | IOException e1) {
				e1.printStackTrace();
			}
			 **/
		}

		void setLoop() {

			loopMarker = new StringBuilder();

			for (Track track : sequence.getTracks()) {
				for (int index = 0; index < track.size(); index++) {
					MidiEvent midiEvent = track.get(index);
					MidiMessage midiMessage = midiEvent.getMessage();

					if (midiMessage instanceof MetaMessage) {
						MetaMessage mm = (MetaMessage) midiMessage;

						if (mm.getType() == 0x06) {
							for (int i = 0; i < mm.getData().length; i++) {
								if (i < mm.getData().length) {
									loopMarker.append((char) mm.getData()[i]);
								}

								if (loopMarker.toString().contains("loopStart")) {

									loopStart = (int) midiEvent.getTick();
									System.out.println("loopStart = " + midiEvent.getTick());
									loopMarker = new StringBuilder();
									continue;

								}

								if (loopMarker.toString().contains("loopEnd")) {

									loopEnd = (int) midiEvent.getTick();
									System.out.println("loopEnd = " + midiEvent.getTick());
									break;
								}

								else {

									if (loopEnd == 0) {
										loopStart = 0;
										loopEnd = (int) sequence.getTickLength();
									}
								}
							}
						}
					}
				}

				sequencer.setLoopStartPoint(loopStart);
				sequencer.setLoopEndPoint(loopEnd);
				sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			}
		}

		public Sequence adjustForPlayOS(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					
					MidiEvent midiEvent = track.get(i);
					
					MidiMessage midiMessage = midiEvent.getMessage();
						
						if (midiMessage instanceof ShortMessage) {
							
							ShortMessage sm = (ShortMessage) midiMessage;
								
							if (sm.getChannel() < 16) {
								getBankLSB(sm);
								
								if (i == 0 & chPosition != chPosition + 1 & bankLSB != 1) {
									chPosition++;
									if (chPosition == 9) {
										chPosition = 10;
									}
								}
								
								if (customBank == false) {
									
									if (sm.getChannel() == 9) {
										bankLSB = 1;
									}
									
									if (sm.getChannel() != 9) {
										bankLSB = 0;
									}
								}
							}
							
							if (bankLSB == 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
							}
							
							else if (bankLSB != 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
							}
						}
					}
				}
				return MidiFixerOSRS.returnFixedMIDI(sequence, false, customBank);
			}

		public Sequence adjustForPlayHD(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					
					MidiEvent midiEvent = track.get(i);
					
					MidiMessage midiMessage = midiEvent.getMessage();
						
						if (midiMessage instanceof ShortMessage) {
							
							ShortMessage sm = (ShortMessage) midiMessage;
								
							if (sm.getChannel() < 16) {
								getBankLSB(sm);
								
								if (i == 0 & chPosition != chPosition + 1 & bankLSB != 1) {
									chPosition++;
									if (chPosition == 9) {
										chPosition = 10;
									}
								}
								
								if (customBank == false) {
									
									if (sm.getChannel() == 9) {
										bankLSB = 1;
									}
									
									if (sm.getChannel() != 9) {
										bankLSB = 0;
									}
								}
							}
							
							if (bankLSB == 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
							}
							
							else if (bankLSB != 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
							}
						}
					}
				}
				return MidiFixerRSHD.returnFixedMIDI(sequence, false, customBank);
			}

		public void getBankLSB(ShortMessage sm) {
			
				if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
					
					if (sm.getData1() == 32) {
						bankLSB = sm.getData2();
						customBank = true;
					}
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
			
			if (!sequencer.isRunning()) {
				startButton.setEnabled(true);
			}
			
			if (!sequencer.isRunning()) {
				Timer timer = new Timer(0, new TimerListener());
				timer.stop();
			}
		}
	}
	
	public class StopButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pausedTime = 0;

			sequencer.stop();
			
			if (!sequencer.isRunning()) {
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

	private class LoopButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			loopMode = !loopMode;

			if (loopMode) {
				loopButton.setText("Loop Mode: On");
			}
			else {
				loopButton.setText("Loop Mode: Off");
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
	
	public class FixAttempterOS implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fixAttemptOS.isEnabled()) {
				fixAttemptingOS = true;
			}
			
			else if (!fixAttemptOS.isEnabled()) {
				fixAttemptingOS = false;
			}
		}
	}
	
	public class FixAttempterHD implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fixAttemptOS.isEnabled()) {
				fixAttemptingHD = true;
			}
			
			else if (!fixAttemptOS.isEnabled()) {
				fixAttemptingHD = false;
			}
		}
	}
	
	public class RenderMIDIProcess implements ActionListener {

		int bankLSB;
		
		int drumChannel = 9;
		int chPosition = -1;

		boolean customBank;

		@Override
		public void actionPerformed(ActionEvent e) {
			Soundbank soundbank;
			try {
				soundbank = MidiSystem.getSoundbank(soundsetFile);
				sequence = MidiSystem.getSequence(midiFile);
				
				if (fixAttemptingOS == false) {
					Midi2WavRender.render(soundbank, sequence, new File("./Rendered.wav/"));
				}
				
				else if (fixAttemptingOS == true) {
					Midi2WavRender.render(soundbank, adjustForRenderOS(sequence), new File("./Rendered.wav/"));	
				}

				if (fixAttemptingHD == false) {
					Midi2WavRender.render(soundbank, sequence, new File("./Rendered.wav/"));
				}
				
				else if (fixAttemptingHD == true) {
					Midi2WavRender.render(soundbank, adjustForRenderHD(sequence), new File("./Rendered.wav/"));	
				}
				
				System.out.println("Successfully rendered MIDI to Audio!");
				
			} catch (InvalidMidiDataException | IOException e1) {
				e1.printStackTrace();
			}
		}

		public Sequence adjustForRenderOS(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					
					MidiEvent midiEvent = track.get(i);
					
					MidiMessage midiMessage = midiEvent.getMessage();
						
						if (midiMessage instanceof ShortMessage) {
							
							ShortMessage sm = (ShortMessage) midiMessage;
								
							if (sm.getChannel() < 16) {
								getBankLSB(sm);
								
								if (i == 0 & chPosition != chPosition + 1 & bankLSB != 1) {
									chPosition++;
									if (chPosition == 9) {
										chPosition = 10;
									}
								}
								
								if (customBank == false) {
									
									if (sm.getChannel() == 9) {
										bankLSB = 1;
									}
									
									if (sm.getChannel() != 9) {
										bankLSB = 0;
									}
								}
							}
							
							if (bankLSB == 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
							}
							
							else if (bankLSB != 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
							}
						}
					}
				}		
				return MidiFixerOSRS.returnFixedMIDI(sequence, false, customBank);
			}

		public Sequence adjustForRenderHD(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					
					MidiEvent midiEvent = track.get(i);
					
					MidiMessage midiMessage = midiEvent.getMessage();
						
						if (midiMessage instanceof ShortMessage) {
							
							ShortMessage sm = (ShortMessage) midiMessage;
								
							if (sm.getChannel() < 16) {
								getBankLSB(sm);
								
								if (i == 0 & chPosition != chPosition + 1 & bankLSB != 1) {
									chPosition++;
									if (chPosition == 9) {
										chPosition = 10;
									}
								}
								
								if (customBank == false) {
									
									if (sm.getChannel() == 9) {
										bankLSB = 1;
									}
									
									if (sm.getChannel() != 9) {
										bankLSB = 0;
									}
								}
							}
							
							if (bankLSB == 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
							}
							
							else if (bankLSB != 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
							}
						}
					}
				}	
				return MidiFixerRSHD.returnFixedMIDI(sequence, false, customBank);
			}

		public void getBankLSB(ShortMessage sm) throws InvalidMidiDataException {
			
				if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
					
					if (sm.getData1() == 32) {
						bankLSB = sm.getData2();
						customBank = true;
					}
				}
			}
		}
	
	public class FixButtonListenerOSRS implements ActionListener {

		boolean customBank = false;
		
		int bankLSB = 0;
		
		int drumChannel = 9;
		int chPosition = -1;

		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				
				sequence = MidiSystem.getSequence(midiFile);
				
				reorderChannels(sequence);
				
				System.out.println("Sucessfully wrote the fixed Old School RuneScape MIDI to file!");
				
			} catch (InvalidMidiDataException | IOException e1) {
				e1.printStackTrace();
			}
		}

		public void reorderChannels(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					
					MidiEvent midiEvent = track.get(i);
					
					MidiMessage midiMessage = midiEvent.getMessage();
						
						if (midiMessage instanceof ShortMessage) {
							
							ShortMessage sm = (ShortMessage) midiMessage;
								
							if (sm.getChannel() < 16) {
								getBankLSB(sm);
								
								if (i == 0 & chPosition != chPosition + 1 & bankLSB != 1) {
									chPosition++;
									if (chPosition == 9) {
										chPosition = 10;
									}
								}
								
								if (customBank == false) {
									
									if (sm.getChannel() == 9) {
										bankLSB = 1;
									}
									
									if (sm.getChannel() != 9) {
										bankLSB = 0;
									}
								}
							}
							
							if (bankLSB == 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
							}
							
							else if (bankLSB != 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
							}
						}
					}
				}		
				MidiFixerOSRS.returnFixedMIDI(sequence, true, customBank);
			}

		public void getBankLSB(ShortMessage sm) throws InvalidMidiDataException {
			
				if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
					
					if (sm.getData1() == 32) {
						bankLSB = sm.getData2();
						customBank = true;
					}
				}
			}
		}
	
	public class FixButtonListenerRSHD implements ActionListener {

		boolean customBank = false;
		
		int bankLSB = 0;
		
		int drumChannel = 9;
		int chPosition = -1;

		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				
				sequence = MidiSystem.getSequence(midiFile);
				
				reorderChannels(sequence);
				
				System.out.println("Sucessfully wrote the fixed RuneScape HD MIDI to file!");
				
			} catch (InvalidMidiDataException | IOException e1) {
				e1.printStackTrace();
			}
		}

		public void reorderChannels(Sequence sequence) throws InvalidMidiDataException, IOException {
			for (Track track : sequence.getTracks()) {
				for (int i = 0; i < track.size(); i++) {
					
					MidiEvent midiEvent = track.get(i);
					
					MidiMessage midiMessage = midiEvent.getMessage();
						
						if (midiMessage instanceof ShortMessage) {
							
							ShortMessage sm = (ShortMessage) midiMessage;
								
							if (sm.getChannel() < 16) {
								getBankLSB(sm);
								
								if (i == 0 & chPosition != chPosition + 1 & bankLSB != 1) {
									chPosition++;
									if (chPosition == 9) {
										chPosition = 10;
									}
								}
								
								if (customBank == false) {
									
									if (sm.getChannel() == 9) {
										bankLSB = 1;
									}
									
									if (sm.getChannel() != 9) {
										bankLSB = 0;
									}
								}
							}
							
							if (bankLSB == 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, drumChannel, sm.getData1(), sm.getData2());
								}
							}
							
							else if (bankLSB != 1) {
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_OFF) {
									sm.setMessage(ShortMessage.NOTE_OFF, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.NOTE_ON) {
									sm.setMessage(ShortMessage.NOTE_ON, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
									sm.setMessage(ShortMessage.PROGRAM_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
									sm.setMessage(ShortMessage.CONTROL_CHANGE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.PITCH_BEND) {
									sm.setMessage(ShortMessage.PITCH_BEND, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.CHANNEL_PRESSURE) {
									sm.setMessage(ShortMessage.CHANNEL_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
								
								if (sm.getCommand() == ShortMessage.POLY_PRESSURE) {
									sm.setMessage(ShortMessage.POLY_PRESSURE, chPosition, sm.getData1(), sm.getData2());
								}
							}
						}
					}
				}		
				MidiFixerRSHD.returnFixedMIDI(sequence, true, customBank);
			}

		public void getBankLSB(ShortMessage sm) throws InvalidMidiDataException {
			
				if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
					
					if (sm.getData1() == 32) {
						bankLSB = sm.getData2();
						customBank = true;
					}
				}
			}
		}

    private class CacheLoader implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                loadCache(frame);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private void loadCache(JFrame frame) throws IOException {
            chooseCache = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
            chooseCache.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooseCache.setSize(400, 200);
            chooseCache.setDialogTitle("Please choose a RuneScape Cache Directory.");
            chooseCache.setVisible(true);
            frame.add(chooseCache);
            int returnValue = chooseCache.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                cacheLibrary = new CacheLibrary(chooseCache.getSelectedFile().getPath() + "/");

                File preferences = new File("./DefaultCachePath.txt/");
                FileOutputStream fos;

                try {
                    fos = new FileOutputStream(preferences);
                    DataOutputStream dos = new DataOutputStream(fos);
                    dos.writeBytes(chooseCache.getSelectedFile().getPath() + "/");
                    dos.flush();
                    dos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

	private class MidiDumper implements ActionListener {

		private JTextField cacheMidiTextField1;
		private JTextField cacheMidiTextField2;

		@Override
		public void actionPerformed(ActionEvent e) {

			try {

				if (cacheMidiTextField1 == null && cacheMidiTextField2 == null) {
					chooseCacheMidi();
				}

				else {

					if (cacheMidiTextField1 != null) {
						checkForInput1(cacheMidiTextField1);
					}

					if (cacheMidiTextField2 != null) {
						checkForInput2(cacheMidiTextField2);
					}
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		private void chooseCacheMidi() {

			cacheFrame = new JFrame("MIDI File Decoding Tool");

			JPanel cachePanel = new JPanel();
			cachePanel.setVisible(true);

			JLabel cacheMidiLabelField1 = new JLabel("Choose a Music File (or type 'All' to dump them all!) - ");
			cacheMidiLabelField1.setVisible(true);

			JLabel cacheMidiLabelField2 = new JLabel("Choose a Fanfare File (or type 'All' to dump them all!) - ");
			cacheMidiLabelField2.setVisible(true);

			cacheMidiTextField1 = new JTextField("Enter ID here (Index 6)... ");
			cacheMidiTextField1.setVisible(true);
			cacheMidiTextField1.addActionListener(e -> checkForInput1(this.cacheMidiTextField1));

			cacheMidiTextField2 = new JTextField("Enter ID here (Index 11)... ");
			cacheMidiTextField2.setVisible(true);
			cacheMidiTextField2.addActionListener(e -> checkForInput2(this.cacheMidiTextField2));

			cachePanel.add(cacheMidiLabelField1);
			cachePanel.add(cacheMidiTextField1);
			cachePanel.add(cacheMidiLabelField2);
			cachePanel.add(cacheMidiTextField2);

			cacheFrame.setLayout(null);
			cacheFrame.setResizable(false);
			cacheFrame.setMaximumSize(new Dimension(50, 400));
			cacheFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			cacheFrame.setContentPane(cachePanel);
			cacheFrame.setLocationRelativeTo(null);
			cacheFrame.setVisible(true);
			cacheFrame.pack();
		}

		private void checkForInput1(JTextField textField) {

			String id = textField.getText();

			if (id.equals("all")) {
				dumpAllMusic();
			}

			if (id.equals("All")) {
				dumpAllMusic();
			}

			else {

				int idInt = Integer.parseInt(id);

				ByteBuffer midiBuffer = ByteBuffer.wrap(cacheLibrary.getIndex(6).getArchive(idInt).getFile(0).getData());
				MidiTrack midiTrack = new MidiTrack(midiBuffer, true);

				try {
					File dir = new File("./MIDI/Music/");
					if (dir.mkdirs()) {
						System.out.println("Created new directory: /MIDI/Music/");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./MIDI/Music/" + idInt + ".mid")));
						dos.write(midiTrack.getMidi());
						System.out.println("Wrote MIDI data to file!");
					} else {
						System.out.println("Couldn't create new directory (It might already exist).");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./MIDI/Music/" + idInt + ".mid")));
						dos.write(midiTrack.getMidi());
						System.out.println("Wrote MIDI data to file!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void checkForInput2(JTextField textField) {

			String id = textField.getText();

			if (id.equals("All")) {
				dumpAllFanfares();
			}

			if (id.equals("all")) {
				dumpAllFanfares();
			} else {

				int idInt = Integer.parseInt(id);

				ByteBuffer midiBuffer = ByteBuffer.wrap(cacheLibrary.getIndex(11).getArchive(idInt).getFile(0).getData());
				MidiTrack midiTrack = new MidiTrack(midiBuffer, true);

				try {
					File dir = new File("./MIDI/Fanfares/");
					if (dir.mkdirs()) {
						System.out.println("Created new directory: /MIDI/Fanfares/");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./MIDI/Fanfares/" + idInt + ".mid")));
						dos.write(midiTrack.getMidi());
						System.out.println("Wrote MIDI data to file!");
					} else {
						System.out.println("Couldn't create new directory (It might already exist).");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./MIDI/Fanfares/" + idInt + ".mid")));
						dos.write(midiTrack.getMidi());
						System.out.println("Wrote MIDI data to file!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void dumpAllMusic() {

			for (int idInt = 0; idInt < cacheLibrary.getIndex(6).getArchives().length; idInt++) {
				ByteBuffer midiBuffer = ByteBuffer.wrap(cacheLibrary.getIndex(6).getArchive(idInt).getFile(0).getData());
				MidiTrack midiTrack = new MidiTrack(midiBuffer, true);

				try {
					File dir = new File("./MIDI/Music/");
					if (dir.mkdirs()) {
						System.out.println("Created new directory: /MIDI/Music/");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./MIDI/Music/" + idInt + ".mid")));
						dos.write(midiTrack.getMidi());
						System.out.println("Wrote MIDI data to file! - " + idInt);
					} else {
						System.out.println("Couldn't create new directory (It might already exist).");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./MIDI/Music/" + idInt + ".mid")));
						dos.write(midiTrack.getMidi());
						System.out.println("Wrote MIDI data to file! - " + idInt);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void dumpAllFanfares() {

			for (int idInt = 0; idInt < cacheLibrary.getIndex(11).getArchives().length; idInt++) {
				ByteBuffer midiBuffer = ByteBuffer.wrap(cacheLibrary.getIndex(11).getArchive(idInt).getFile(0).getData());
				MidiTrack midiTrack = new MidiTrack(midiBuffer, true);

				try {
					File dir = new File("./MIDI/Fanfares/");
					if (dir.mkdirs()) {
						System.out.println("Created new directory: /MIDI/Fanfares/");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./MIDI/Fanfares/" + idInt + ".mid")));
						dos.write(midiTrack.getMidi());
						System.out.println("Wrote MIDI data to file! - " + idInt);
					} else {
						System.out.println("Couldn't create new directory (It might already exist).");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./MIDI/Fanfares/" + idInt + ".mid")));
						dos.write(midiTrack.getMidi());
						System.out.println("Wrote MIDI data to file! - " + idInt);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class MidiEncoder implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (midiFile != null) {
				try {
					sequence = MidiSystem.getSequence(midiFile);
					File encodedMidi = MidiTrack.encode(sequence);
					byte[] encodedData = Files.readAllBytes(Paths.get(encodedMidi.getPath()));

					cacheLibrary.getIndex(6).getArchive(0).removeFile(0);
					cacheLibrary.getIndex(6).addArchive(0).addFile(encodedData);
					cacheLibrary.getIndex(6).getArchive(0).getFile(0).setName(0);
					cacheLibrary.getIndex(6).update();

					System.out.println("MIDI file successfully encoded and packed to ID - 0!");

				} catch (IOException | InvalidMidiDataException ex) {
					ex.printStackTrace();
				}
			}

			else {
				System.out.println("Error: Please load a valid MIDI file!");
			}
		}
	}

	private class SfxDumper implements ActionListener {

		private JTextField cacheSfxTextField;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (cacheSfxTextField == null) {
				chooseCacheSoundEffect();
			}

			else {
				checkForInput(cacheSfxTextField);
			}
		}

		private void chooseCacheSoundEffect() {

			cacheFrame = new JFrame("Sound Effect Decoding Tool");

			JPanel cachePanel = new JPanel();
			cachePanel.setVisible(true);

			JLabel cacheSfxLabelField = new JLabel("Choose a Sound Effect (or type 'All' to dump them all!) - ");
			cacheSfxLabelField.setVisible(true);

			cacheSfxTextField = new JTextField("Enter ID here (Index 4)... ");
			cacheSfxTextField.setVisible(true);
			cacheSfxTextField.addActionListener(e -> checkForInput(this.cacheSfxTextField));

			cachePanel.add(cacheSfxLabelField);
			cachePanel.add(cacheSfxTextField);

			cacheFrame.setLayout(null);
			cacheFrame.setResizable(false);
			cacheFrame.setMaximumSize(new Dimension(50, 400));
			cacheFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			cacheFrame.setContentPane(cachePanel);
			cacheFrame.setLocationRelativeTo(null);
			cacheFrame.setVisible(true);
			cacheFrame.pack();
		}

		private void checkForInput(JTextField cacheSfxTextField) {

			String id = cacheSfxTextField.getText();

			if (id.equals("All")) {
				dumpAllSfx();
			}

			if (id.equals("all")) {
				dumpAllSfx();
			} else {

				int idInt = Integer.parseInt(id);

				ByteBuffer sfxBuffer = ByteBuffer.wrap(cacheLibrary.getIndex(4).getArchive(idInt).getFile(0).getData());
				SoundEffect soundEffect = new SoundEffect();
				soundEffect.decode(sfxBuffer);

				try {
					File dir = new File("./Sounds/Sound Effects/");
					if (dir.mkdirs()) {
						System.out.println("Created new directory: /Sounds/Sound Effects/");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./Sounds/Sound Effects/" + idInt + ".wav")));
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						AudioBuffer audioBuffer = soundEffect.toAudioBuffer();
						AudioInputStream audioInputStream;
						audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBuffer.samples), new AudioFormat(22050, 8, 1, true, false), audioBuffer.samples.length);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
						dos.write(byteArrayOutputStream.toByteArray());
						System.out.println("Wrote sound effect data to WAVE file!");
					} else {
						System.out.println("Couldn't create new directory (It might already exist).");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./Sounds/Sound Effects/" + idInt + ".wav")));
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						AudioBuffer audioBuffer = soundEffect.toAudioBuffer();
						AudioInputStream audioInputStream;
						audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBuffer.samples), new AudioFormat(22050, 8, 1, true, false), audioBuffer.samples.length);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
						dos.write(byteArrayOutputStream.toByteArray());
						System.out.println("Wrote sound effect data to WAVE file!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void dumpAllSfx() {

			for (int idInt = 0; idInt < cacheLibrary.getIndex(4).getArchives().length; idInt++) {

				ByteBuffer sfxBuffer = ByteBuffer.wrap(cacheLibrary.getIndex(4).getArchive(idInt).getFile(0).getData());
				SoundEffect soundEffect = new SoundEffect();
				soundEffect.decode(sfxBuffer);

				try {
					File dir = new File("./Sounds/Sound Effects/");
					if (dir.mkdirs()) {
						System.out.println("Created new directory: /Sounds/Sound Effects/");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./Sounds/Sound Effects/" + idInt + ".wav")));
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						AudioBuffer audioBuffer = soundEffect.toAudioBuffer();
						AudioInputStream audioInputStream;
						audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBuffer.samples), new AudioFormat(22050, 8, 1, true, false), audioBuffer.samples.length);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
						dos.write(byteArrayOutputStream.toByteArray());
						System.out.println("Wrote sound effect data to WAVE file!");
					} else {
						System.out.println("Couldn't create new directory (It might already exist).");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./Sounds/Sound Effects/" + idInt + ".wav")));
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						AudioBuffer audioBuffer = soundEffect.toAudioBuffer();
						AudioInputStream audioInputStream;
						audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBuffer.samples), new AudioFormat(22050, 8, 1, true, false), audioBuffer.samples.length);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
						dos.write(byteArrayOutputStream.toByteArray());
						System.out.println("Wrote sound effect data to WAVE file!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class MusicSampleDumper implements ActionListener {

    	private JTextField musicSampleTextField;

		@Override
		public void actionPerformed(ActionEvent e) {

			if (musicSampleTextField == null) {
				chooseCacheMusicSample();
			}

			else {
				checkForInput(musicSampleTextField);
			}
		}

		private void chooseCacheMusicSample() {

			cacheFrame = new JFrame("Sound Bank Sample Decoding Tool");

			JPanel cachePanel = new JPanel();
			cachePanel.setVisible(true);

			JLabel cacheSampleLabelField = new JLabel("Choose a Music Sample (or type 'All' to dump them all!) - ");
			cacheSampleLabelField.setVisible(true);

			musicSampleTextField = new JTextField("Enter ID here (Index 14)... ");
			musicSampleTextField.setVisible(true);
			musicSampleTextField.addActionListener(e -> checkForInput(this.musicSampleTextField));

			cachePanel.add(cacheSampleLabelField);
			cachePanel.add(musicSampleTextField);

			cacheFrame.setLayout(null);
			cacheFrame.setResizable(false);
			cacheFrame.setMaximumSize(new Dimension(50, 400));
			cacheFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			cacheFrame.setContentPane(cachePanel);
			cacheFrame.setLocationRelativeTo(null);
			cacheFrame.setVisible(true);
			cacheFrame.pack();
		}

		private void checkForInput(JTextField musicSampleTextField) {

			String id = musicSampleTextField.getText();

			if (id.equals("All")) {
				dumpAllSamples();
			}

			if (id.equals("all")) {
				dumpAllSamples();
			} else {

				int idInt = Integer.parseInt(id);

				SoundBankCache soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));

				try {
					File dir = new File("./Sounds/Sound Bank Samples/");
					if (dir.mkdirs()) {
						System.out.println("Created new directory: /Sounds/Sound Bank Samples/");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./Sounds/Sound Bank Samples/" + idInt + ".wav")));
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						AudioBuffer audioBuffer = soundBankCache.getMusicSample(idInt, null);
						AudioInputStream audioInputStream;
						audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBuffer.samples), new AudioFormat(audioBuffer.sampleRate, 8, 1, true, false), audioBuffer.samples.length);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
						dos.write(byteArrayOutputStream.toByteArray());
						System.out.println("Wrote sound bank sample data to WAVE file!");
					} else {
						System.out.println("Couldn't create new directory (It might already exist).");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./Sounds/Sound Bank Samples/" + idInt + ".wav")));
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						AudioBuffer audioBuffer = soundBankCache.getMusicSample(idInt, null);
						AudioInputStream audioInputStream;
						audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBuffer.samples), new AudioFormat(audioBuffer.sampleRate, 8, 1, true, false), audioBuffer.samples.length);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
						dos.write(byteArrayOutputStream.toByteArray());
						System.out.println("Wrote sound bank sample data to WAVE file!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void dumpAllSamples() {

			SoundBankCache soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));

			//Starts at ID 1, because ID 0 is typically an incorrect or corrupted file.

			for (int idInt = 1; idInt < cacheLibrary.getIndex(14).getArchives().length; idInt++) {
				try {
					File dir = new File("./Sounds/Sound Bank Samples/");
					if (dir.mkdirs()) {
						System.out.println("Created new directory: /Sounds/Sound Bank Samples/");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./Sounds/Sound Bank Samples/" + idInt + ".wav")));
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						AudioBuffer audioBuffer = soundBankCache.getMusicSample(idInt, null);
						if (audioBuffer == null) {
							continue;
						}
						AudioInputStream audioInputStream;
						audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBuffer.samples), new AudioFormat(audioBuffer.sampleRate, 8, 1, true, false), audioBuffer.samples.length);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
						dos.write(byteArrayOutputStream.toByteArray());
						System.out.println("Wrote sound bank sample data to WAVE file!");
					} else {
						System.out.println("Couldn't create new directory (It might already exist).");
						DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./Sounds/Sound Bank Samples/" + idInt + ".wav")));
						ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						AudioBuffer audioBuffer = soundBankCache.getMusicSample(idInt, null);
						if (audioBuffer == null) {
							continue;
						}
						AudioInputStream audioInputStream;
						audioInputStream = new AudioInputStream(new ByteArrayInputStream(audioBuffer.samples), new AudioFormat(audioBuffer.sampleRate, 8, 1, true, false), audioBuffer.samples.length);
						AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
						dos.write(byteArrayOutputStream.toByteArray());
						System.out.println("Wrote sound bank sample data to WAVE file!");
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class MidiNoteGenerator implements ActionListener {

    	private JTextField programInput;
		private JTextField noteInput;

		private static Sequence sequence;
		private Index index15;
		private SoundBankCache soundBankCache;
		private static int bankLSBValue;
		private static int programValue;
		private static int patchValue;
		private static int noteValue;

		@Override
		public void actionPerformed(ActionEvent e) {

			if (programInput == null && noteInput == null) {

				try {

					sequence = new Sequence(Sequence.PPQ, 480);

					generateMidi();

				} catch (InvalidMidiDataException ex) {
					ex.printStackTrace();
				}
			}

			else {

				if (programInput != null && noteInput != null) {
					checkForInput(programInput, noteInput);
				}
			}
		}

		public synchronized void generateSound() {

			index15 = cacheLibrary.getIndex(15);
			soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));

			File filePath = new File("./Generated Midi/");
			File generatedMidi = new File("./Generated Midi/" + patchValue + "_" + noteValue + ".mid/");

			if (filePath.mkdir()) {
				try {
					DataOutputStream dos = new DataOutputStream(new FileOutputStream(generatedMidi));
					MidiSystem.write(sequence, 1, dos);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			else {
				try {
					DataOutputStream dos = new DataOutputStream(new FileOutputStream(generatedMidi));
					MidiSystem.write(sequence, 1, dos);

					File encodedMidi = MidiTrack.encode(sequence);
					byte[] encodedData = Files.readAllBytes(Paths.get(encodedMidi.getPath()));

					cacheLibrary.getIndex(6).getArchive(0).removeFile(0);
					cacheLibrary.getIndex(6).addArchive(0).addFile(encodedData);
					cacheLibrary.getIndex(6).update();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void generateMidi() {

			noteGeneratorFrame = new JFrame("MIDI Note Generation Tool");

			JPanel cachePanel = new JPanel();
			cachePanel.setVisible(true);

			JLabel programLabelField = new JLabel("Type in a valid program change number - ");
			programLabelField.setVisible(true);

			JLabel noteLabelField = new JLabel("Type in a valid note value (0-127) - ");
			noteLabelField.setVisible(true);

			programInput = new JTextField("Enter a program change... ");
			programInput.setVisible(true);
			programInput.addActionListener(e -> checkForInput(this.programInput, this.noteInput));

			noteInput = new JTextField("Enter a note value... ");
			noteInput.setVisible(true);
			noteInput.addActionListener(e -> checkForInput(this.programInput, this.noteInput));

			cachePanel.add(programLabelField);
			cachePanel.add(programInput);
			cachePanel.add(noteLabelField);
			cachePanel.add(noteInput);

			noteGeneratorFrame.setLayout(null);
			noteGeneratorFrame.setResizable(false);
			noteGeneratorFrame.setMaximumSize(new Dimension(50, 400));
			noteGeneratorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			noteGeneratorFrame.setContentPane(cachePanel);
			noteGeneratorFrame.setLocationRelativeTo(null);
			noteGeneratorFrame.setVisible(true);
			noteGeneratorFrame.pack();
		}

		private void checkForInput(JTextField programInput, JTextField noteInput) {

			String programText = programInput.getText();
			String noteText = noteInput.getText();

			if (programText != null && noteText != null) {

				patchValue = Integer.parseInt(programText);
				programValue = Integer.parseInt(programText);
				noteValue = Integer.parseInt(noteText);

				ShortMessage bankSelectMSB = new ShortMessage();
				ShortMessage bankSelectLSB = new ShortMessage();
				ShortMessage programChange = new ShortMessage();
				ShortMessage volumeValue = new ShortMessage();
				ShortMessage volumeValueLSB = new ShortMessage();
				ShortMessage expression = new ShortMessage();
				ShortMessage sustain = new ShortMessage();
				ShortMessage noteOnValue = new ShortMessage();
				ShortMessage noteOffValue = new ShortMessage();

				MetaMessage tempoValue = new MetaMessage();
				MetaMessage endOfTrack = new MetaMessage();

				bankLSBValue = (int) Math.floor(programValue >> 7);
				System.out.println("Bank LSB - " + bankLSBValue);

				System.out.println("Selected Patch - " + programValue);
				while (programValue >= 128) {
					programValue = programValue - 128;
				}

				try {

					Track track = sequence.createTrack();

					bankSelectMSB.setMessage(ShortMessage.CONTROL_CHANGE, 0, 0, 0);
					bankSelectLSB.setMessage(ShortMessage.CONTROL_CHANGE, 0, 32, bankLSBValue);
					programChange.setMessage(ShortMessage.PROGRAM_CHANGE, 0, programValue, 0);
					volumeValue.setMessage(ShortMessage.CONTROL_CHANGE, 0, 7, 127);
					volumeValueLSB.setMessage(ShortMessage.CONTROL_CHANGE, 0, 39, 127);
					expression.setMessage(ShortMessage.CONTROL_CHANGE, 0, 11, 127);
					sustain.setMessage(ShortMessage.CONTROL_CHANGE, 0, 64, 127);
					noteOnValue.setMessage(ShortMessage.NOTE_ON, 0, noteValue, 127);
					noteOffValue.setMessage(ShortMessage.NOTE_OFF, 0, noteValue, 127);
					noteOffValue.setMessage(ShortMessage.NOTE_OFF, 0, noteValue, 127);

					track.add(new MidiEvent(volumeValue, 1));
					track.add(new MidiEvent(volumeValueLSB, 1));
					track.add(new MidiEvent(expression, 1));
					track.add(new MidiEvent(sustain, 1));
					track.add(new MidiEvent(noteOnValue, 3));
					track.add(new MidiEvent(noteOffValue, 30003));
					track.add(new MidiEvent(programChange, 2));
					track.add(new MidiEvent(bankSelectMSB, 2));
					track.add(new MidiEvent(bankSelectLSB, 1));

					if (sequence.getTracks()[0] != null) {
						generateSound();
					}
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class SoundFontCreator implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			PcmPlayer.pcmPlayer_stereo = false;

			Index musicIndex = cacheLibrary.getIndex(6);
			Index patchIndex = cacheLibrary.getIndex(15);

			int bank = 0;
			int program = 60;

			for (int notePitch = 0; notePitch < 128; notePitch++) {

				SoundBankCache soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				Sequence midiSeq;
				try {
					midiSeq = new Sequence(Sequence.PPQ, 960);
					ShortMessage programChangeMsg = new ShortMessage();
					ShortMessage noteOnMessage = new ShortMessage();
					ShortMessage noteOffMessage = new ShortMessage();
					ShortMessage bankMSBMessage = new ShortMessage();
					ShortMessage bankLSBMessage = new ShortMessage();

					programChangeMsg.setMessage(ShortMessage.PROGRAM_CHANGE, 0, program, 0);
					bankMSBMessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, 0, 0);
					bankLSBMessage.setMessage(ShortMessage.CONTROL_CHANGE, 0, 32, bank);
					noteOnMessage.setMessage(ShortMessage.NOTE_ON, 0, notePitch, 127);
					noteOffMessage.setMessage(ShortMessage.NOTE_OFF, 0, notePitch, 127);

					midiSeq.createTrack().add(new MidiEvent(programChangeMsg, 2));
					midiSeq.getTracks()[0].add(new MidiEvent(bankLSBMessage, 1));
					midiSeq.getTracks()[0].add(new MidiEvent(bankMSBMessage, 0));
					midiSeq.getTracks()[0].add(new MidiEvent(noteOnMessage, 3));
					midiSeq.getTracks()[0].add(new MidiEvent(noteOffMessage, 9003));
					midiSeq.getTracks()[0].add(new MidiEvent(programChangeMsg, 20000));
					MidiSystem.write(midiSeq, 1, byteArrayOutputStream);

				} catch (InvalidMidiDataException | IOException ex) {
					ex.printStackTrace();
				}

				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				MidiTrack.midi = byteArrayOutputStream.toByteArray();
				MidiTrack.loadMidiTrackInfo();

				MidiPcmStream midiPcmStream = new MidiPcmStream();
				midiPcmStream.init(9, 128);
				midiPcmStream.setMusicTrack(midiTrack, false);
				midiPcmStream.loadMusicTrack(midiTrack, patchIndex, soundBankCache, 0);

				MakeSoundFont makeSoundFont = new MakeSoundFont();
				makeSoundFont.saveSoundResource(midiPcmStream, bank, program, notePitch);
				//makeSoundFont.createSoundFont(midiPcmStream, bank, program);
			}
		}
	}

	private static class StreamSoundEffectDumper implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			//Start at file 1
			for (int archiveIndex = 1; archiveIndex < cacheLibrary.getIndex(14).getArchives().length; archiveIndex++) {
				try {
					byte[] data = cacheLibrary.getIndex(14).getArchive(archiveIndex).getFile(0).getData();
					if (data != null) {
						FileOutputStream fileOutputStream = new FileOutputStream(new File("./Streams/Sound Effects/" + archiveIndex + ".ogg/"));
						fileOutputStream.write(data);
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private class TestButtonListener implements ActionListener {

		AudioFormat audioFormat;
		int trackCount;

		@Override
		public void actionPerformed(ActionEvent e) {

			audioFormat = new AudioFormat(44100, 16, 2, true, false);

			try {
				Sequencer sequencer = MidiSystem.getSequencer(false);
				Sequence sequence = MidiSystem.getSequence(midiFile);
				Soundbank soundbank = MidiSystem.getSoundbank(soundsetFile);

				sequencer.open();
				sequencer.setSequence(sequence);
				trackCount = sequence.getTracks().length;

				System.out.println("Track Count - " + trackCount);

				for (int track = 0; track < trackCount; track++) {

					File file = new File("./Tracks/" + track + ".wav/");
					FileOutputStream fileOutputStream = new FileOutputStream(file);

					System.out.println("Rendering... Track " + track);

					AudioSynthesizer audioSynthesizer = findAudioSynthesizer();

					Map<String,Object> infoMap = new HashMap<>();
					infoMap.put("resamplerType", "sinc");
					infoMap.put("maxPolyphony", "1024");
					AudioInputStream audioInputStream = audioSynthesizer.openStream(audioFormat, infoMap);

					audioSynthesizer.unloadAllInstruments(audioSynthesizer.getDefaultSoundbank());
					audioSynthesizer.loadAllInstruments(soundbank);

					sequencer.setTrackSolo(track, true);
					sequencer.getTransmitter().setReceiver(audioSynthesizer.getReceiver());

					Sequence soloTrack = new Sequence(sequence.getDivisionType(), sequence.getResolution());
					Track soloedTrack = soloTrack.createTrack();

					for (int index = 0; index < sequence.getTracks()[track].size(); index++) {
						MidiEvent midiEvent = sequence.getTracks()[track].get(index);
						soloedTrack.add(midiEvent);
					}

					double total = send(soloTrack, audioSynthesizer.getReceiver());

					long length = (long) (audioInputStream.getFormat().getFrameRate() * (total + 4));
					audioInputStream = new AudioInputStream(audioInputStream, audioInputStream.getFormat(), length);

					AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOutputStream);
				}

			} catch (MidiUnavailableException | IOException | InvalidMidiDataException exception) {
				exception.printStackTrace();
			}
		}

		public double send(Sequence seq, Receiver recv) {
			float divtype = seq.getDivisionType();
			assert (seq.getDivisionType() == Sequence.PPQ);
			Track[] tracks = seq.getTracks();
			int[] trackspos = new int[tracks.length];
			int mpq = 500000;
			int seqres = seq.getResolution();
			long lasttick = 0;
			long curtime = 0;
			while (true) {
				MidiEvent selevent = null;
				int seltrack = -1;
				for (int i = 0; i < tracks.length; i++) {
					int trackpos = trackspos[i];
					Track track = tracks[i];
					if (trackpos < track.size()) {
						MidiEvent event = track.get(trackpos);
						if (selevent == null
								|| event.getTick() < selevent.getTick()) {
							selevent = event;
							seltrack = i;
						}
					}
				}
				if (seltrack == -1)
					break;
				trackspos[seltrack]++;
				long tick = selevent.getTick();
				if (divtype == Sequence.PPQ)
					curtime += ((tick - lasttick) * mpq) / seqres;
				else
					curtime = (long) ((tick * 1000000.0 * divtype) / seqres);
				lasttick = tick;
				MidiMessage msg = selevent.getMessage();
				if (msg instanceof MetaMessage) {
					if (divtype == Sequence.PPQ)
						if (((MetaMessage) msg).getType() == 0x51) {
							byte[] data = ((MetaMessage) msg).getData();
							mpq = ((data[0] & 0xff) << 16)
									| ((data[1] & 0xff) << 8) | (data[2] & 0xff);
						}
				} else {
					if (recv != null)
						recv.send(msg, curtime);
				}
			}
			return curtime / 1000000.0;
		}

		public AudioSynthesizer findAudioSynthesizer() throws MidiUnavailableException {

			Synthesizer synth = MidiSystem.getSynthesizer();

			if (synth instanceof AudioSynthesizer) {
				return (AudioSynthesizer) synth;
			}

			MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

			for (MidiDevice.Info info : infos) {
				MidiDevice dev = MidiSystem.getMidiDevice(info);
				if (dev instanceof AudioSynthesizer) {
					return (AudioSynthesizer) dev;
				}
			}
			return null;
		}
	}

	private class CustomSoundBankSongTester implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index musicIndex = cacheLibrary.getIndex(6);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			MusicPatch.localSoundBankSamples = new File("./Sounds/Sound Bank Samples/");
			MusicPatch.localSoundBankPatches = new File("./Sounds/Sound Bank Patches/");
			MusicPatch.localSoundEffects = new File("./Sounds/Sound Effects/");

			MusicPatch.localCustomSoundBank = new File("./Sounds/Custom Sound Bank/");

			SoundBankCache soundBankCache = new SoundBankCache(soundEffectIndex, soundBankIndex);
			midiPcmStream = new MidiPcmStream();
			Path midiPath = Paths.get(midiFile.toURI());

			try {

				PcmPlayer.pcmPlayer_stereo = true;

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());

				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				MidiTrack.midi = Files.readAllBytes(midiPath);
				MidiTrack.loadMidiTrackInfo();

				midiPcmStream.init(9, 128);
				midiPcmStream.setMusicTrack(midiTrack, loopMode);
				midiPcmStream.setPcmStreamVolume(volume);
				midiPcmStream.loadMusicTrackFiles(midiTrack, soundBankCache, MusicPatch.localSoundBankPatches, 0);

				SoundPlayer soundPlayer = new SoundPlayer();
				soundPlayer.setStream(midiPcmStream);
				soundPlayer.samples = new int[2048];
				soundPlayer.capacity = 16384;
				soundPlayer.init();
				soundPlayer.open(soundPlayer.capacity);

				Thread songThread = new Thread(() -> {
					while (midiPcmStream.active) {
						soundPlayer.fill(soundPlayer.samples, 256);
						soundPlayer.writeCustom();
						if (midiPcmStream.midiFile.isDone()) {
							break;
						}
					}
				});

				songThread.start();

			} catch (IOException | UnsupportedAudioFileException ex) {
				ex.printStackTrace();
			}
		}
	}
	private class SoundBankSongTester implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index musicIndex = cacheLibrary.getIndex(6);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			MusicPatch.localSoundBankSamples = new File("./Sounds/Sound Bank Samples/");
			MusicPatch.localSoundBankPatches = new File("./Sounds/Sound Bank Patches/");
			MusicPatch.localSoundEffects = new File("./Sounds/Sound Effects/");

			SoundBankCache soundBankCache = new SoundBankCache(soundEffectIndex, soundBankIndex);
			midiPcmStream = new MidiPcmStream();
			Path path = Paths.get(midiFile.toURI());

			try {

				PcmPlayer.pcmPlayer_stereo = true;

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());

				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				MidiTrack.midi = Files.readAllBytes(path);
				MidiTrack.loadMidiTrackInfo();

				midiPcmStream.init(9, 128);
				midiPcmStream.setMusicTrack(midiTrack, loopMode);
				midiPcmStream.setPcmStreamVolume(volume);
				midiPcmStream.loadMusicTrack(midiTrack, musicPatchIndex, soundBankCache, 0);

				SoundPlayer soundPlayer = new SoundPlayer();
				soundPlayer.setStream(midiPcmStream);
				soundPlayer.samples = new int[512];
				soundPlayer.capacity = 16384;
				soundPlayer.init();
				soundPlayer.open(soundPlayer.capacity);

				Thread songThread = new Thread(() -> {
					while (midiPcmStream.active) {
						soundPlayer.fill(soundPlayer.samples, 256);
						soundPlayer.write();

						if (midiPcmStream.midiFile.isDone()) {
							break;
						}
					}
				});

				songThread.start();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static class SoundDumper implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			
			for (int i = 0; i < soundEffectIndex.getArchives().length; i++) {
				try {
					if (soundEffectIndex.getArchive(i) != null) {
						FileOutputStream fileOutputStream = new FileOutputStream(new File("./Raw Data/Index 4/" + i + ".dat/"));
						fileOutputStream.write(soundEffectIndex.getArchive(i).getFile(0).getData());
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			for (int i = 0; i < soundBankIndex.getArchives().length; i++) {
				try {
					if (soundBankIndex.getArchive(i) != null) {
						FileOutputStream fileOutputStream = new FileOutputStream(new File("./Raw Data/Index 14/" + i + ".dat/"));
						fileOutputStream.write(soundBankIndex.getArchive(i).getFile(0).getData());
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			for (int i = 0; i < 4000; i++) {
				try {

					if (musicPatchIndex.getArchive(i) == null) {
						continue;
					}

					FileOutputStream fileOutputStream = new FileOutputStream(new File("./Raw Data/Index 15/" + i + ".dat/"));
					fileOutputStream.write(musicPatchIndex.getArchive(i).getFile(0).getData());
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private class SoundPacker implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			for (int i = 0; i < 100000; i++) {
				Path path = Paths.get("./Raw Data/Index 4/" + i + ".dat/");
				try {
					if (path.toFile().exists()) {

						byte[] data = Files.readAllBytes(path);

						if (soundEffectIndex.getArchive(i) == null) {
							soundEffectIndex.addArchive(i).addFile(data);
							soundEffectIndex.update();
						}
						else {
							soundEffectIndex.getArchive(i).removeFile(0);
							soundEffectIndex.getArchive(i).addFile(data);
							soundEffectIndex.update();
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			for (int i = 0; i < 100000; i++) {
				Path path = Paths.get("./Raw Data/Index 14/" + i + ".dat/");
				try {
					if (path.toFile().exists()) {

						byte[] data = Files.readAllBytes(path);

						if (soundBankIndex.getArchive(i) == null) {
							soundBankIndex.addArchive(i).addFile(data);
							soundBankIndex.update();
						}
						else {
							soundBankIndex.getArchive(i).removeFile(0);
							soundBankIndex.getArchive(i).addFile(data);
							soundBankIndex.update();
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			for (int i = 0; i < 100000; i++) {
				Path path = Paths.get("./Raw Data/Index 15/" + i + ".dat/");
				try {
					if (path.toFile().exists()) {

						byte[] data = Files.readAllBytes(path);

						if (musicPatchIndex.getArchive(i) == null) {
							musicPatchIndex.addArchive(i).addFile(data);
							musicPatchIndex.update();
						}
						else {
							musicPatchIndex.getArchive(i).removeFile(0);
							musicPatchIndex.getArchive(i).addFile(data);
							musicPatchIndex.update();
						}
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private class SoundBankEncoder implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {

				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./Test.wav/"));

				FileOutputStream fileOutputStream = new FileOutputStream(new File("./Test.dat/"));
				DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

				MusicSample musicSample = new MusicSample(audioInputStream, dataOutputStream, 0);

				Path path = Paths.get("./Test.dat/");

				cacheLibrary.getIndex(14).getArchive(1).removeFile(0);
				cacheLibrary.getIndex(14).addArchive(1).addFile(Files.readAllBytes(path));
				cacheLibrary.getIndex(14).getArchive(1).getFile(0).setName(0);
				cacheLibrary.getIndex(14).update();

			} catch (IOException | UnsupportedAudioFileException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class SoundBankSongDumper implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index musicIndex = cacheLibrary.getIndex(6);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			MusicPatch.localSoundBankSamples = new File("./Sounds/Sound Bank Samples/");
			MusicPatch.localSoundBankPatches = new File("./Sounds/Sound Bank Patches/");
			MusicPatch.localSoundEffects = new File("./Sounds/Sound Effects/");

			SoundBankCache soundBankCache = new SoundBankCache(soundEffectIndex, soundBankIndex);
			midiPcmStream = new MidiPcmStream();
			Path path = Paths.get(midiFile.toURI());

			try {

				PcmPlayer.pcmPlayer_stereo = true;

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());

				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				MidiTrack.midi = Files.readAllBytes(path);
				MidiTrack.loadMidiTrackInfo();

				midiPcmStream.init(9, 128);
				midiPcmStream.setMusicTrack(midiTrack, loopMode);
				midiPcmStream.setPcmStreamVolume(volume);
				midiPcmStream.loadMusicTrack(midiTrack, musicPatchIndex, soundBankCache, 0);
				//midiPcmStream.loadMusicTrackFiles(midiTrack, soundBankCache, MusicPatch.localSoundBankPatches, 0);

				SoundPlayer soundPlayer = new SoundPlayer();
				soundPlayer.setStream(midiPcmStream);
				soundPlayer.samples = new int[512];
				soundPlayer.capacity = 16384;
				soundPlayer.init();
				soundPlayer.open(soundPlayer.capacity);

				while (midiPcmStream.active) {
					soundPlayer.fill(soundPlayer.samples, 256);
					soundPlayer.writeToBuffer();
					if (midiPcmStream.midiFile.isDone()) {
						break;
					}
				}

				byte[] data = soundPlayer.byteArrayOutputStream.toByteArray();

				File outFile = new File("./MIDI Audio/" + midiFile.getName() + ".wav/");
				FileOutputStream fos;

				try {

					fos = new FileOutputStream(outFile);
					AudioFormat format = new AudioFormat(PcmPlayer.pcmPlayer_sampleRate, 16, 2, true, false);
					AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
					AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fos);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class SoundBankSettings implements ActionListener {

		private JTextField volumeTextField;
		private JTextField sampleRateTextField;

		@Override
		public void actionPerformed(ActionEvent e) {

			JFrame soundBankFrame = new JFrame("SoundBank - Audio Settings");

			JPanel settingsPanel = new JPanel();
			settingsPanel.setVisible(true);

			sampleRateTextField = new JTextField("Enter the sample rate here...");
			sampleRateTextField.setVisible(true);
			sampleRateTextField.addActionListener(actionEvent -> checkForRateInput(this.sampleRateTextField));

			volumeTextField = new JTextField("Enter the volume value here...");
			volumeTextField.setVisible(true);
			volumeTextField.addActionListener(actionEvent -> checkForVolumeInput(this.volumeTextField));

			settingsPanel.add(volumeTextField);
			settingsPanel.add(sampleRateTextField);

			soundBankFrame.setLayout(null);
			soundBankFrame.setResizable(false);
			soundBankFrame.setMaximumSize(new Dimension(100, 400));
			soundBankFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			soundBankFrame.setContentPane(settingsPanel);
			soundBankFrame.setLocationRelativeTo(null);
			soundBankFrame.setVisible(true);
			soundBankFrame.pack();
		}

		private void checkForRateInput(JTextField sampleRateTextField) {
			String sampleRateInput = sampleRateTextField.getText();
			PcmPlayer.pcmPlayer_sampleRate = Integer.parseInt(sampleRateInput);
			sampleRateInfo.setText("Sample Rate is: " + PcmPlayer.pcmPlayer_sampleRate);
		}

		private void checkForVolumeInput(JTextField volumeTextField) {

			String volumeInput = volumeTextField.getText();
			volume = Integer.parseInt(volumeInput);
			volumeInfo.setText("Sound Bank Volume is: " + volume + " (" + (volume / 256.00) * 100.00 + "%)");

			if (midiPcmStream != null) {
				midiPcmStream.setPcmStreamVolume(volume);
			}
		}
	}

	private class BatchConverter implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index musicIndex = cacheLibrary.getIndex(6);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			MusicPatch.localSoundBankSamples = new File("./Sounds/Sound Bank Samples/");
			MusicPatch.localSoundBankPatches = new File("./Sounds/Sound Bank Patches/");
			MusicPatch.localSoundEffects = new File("./Sounds/Sound Effects/");

			SoundBankCache soundBankCache = new SoundBankCache(soundEffectIndex, soundBankIndex);
			midiPcmStream = new MidiPcmStream();
			Path path = Paths.get(midiFile.toURI());

			PcmPlayer.pcmPlayer_stereo = true;

			ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());

			MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
			try {
				MidiTrack.midi = Files.readAllBytes(path);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
			MidiTrack.loadMidiTrackInfo();

			midiPcmStream.init(9, 128);
			midiPcmStream.setMusicTrack(midiTrack, loopMode);
			midiPcmStream.setPcmStreamVolume(volume);
			midiPcmStream.loadMusicTrack(midiTrack, musicPatchIndex, soundBankCache, 0);

			SoundPlayer soundPlayer = new SoundPlayer();
			soundPlayer.setStream(midiPcmStream);
			soundPlayer.samples = new int[512];
			soundPlayer.capacity = 16384;
			soundPlayer.init();
			soundPlayer.open(soundPlayer.capacity);

			while (midiPcmStream.active) {
				soundPlayer.fill(soundPlayer.samples, 256);
				soundPlayer.writeToBuffer();
				if (midiPcmStream.midiFile.isDone()) {
					break;
				}
			}

			byte[] data = soundPlayer.byteArrayOutputStream.toByteArray();

			File outFile = new File("./MIDI Audio/" + midiFile.getName() + ".wav/");
			File mp3File = new File("./MIDI Audio/" + midiFile.getName() + ".mp3/");
			FileOutputStream fos;

			try {

				fos = new FileOutputStream(outFile);
				AudioFormat format = new AudioFormat(PcmPlayer.pcmPlayer_sampleRate, 16, 2, true, false);
				AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
				AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fos);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private class RandomMidiGenerator implements ActionListener {

		private String mood = "Default";

		private int noteC = 0;
		private int noteCSharp = 1;
		private int noteD = 2;
		private int noteDSharp = 3;
		private int noteE = 4;
		private int noteF = 5;
		private int noteFSharp = 6;
		private int noteG = 7;
		private int noteGSharp = 8;
		private int noteA = 9;
		private int noteASharp = 10;
		private int noteB = 11;

		private int[] octaves = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

		private int[] baseCNotes = new int[]{0, 12, 24, 36, 48, 60, 72, 84, 96, 108, 120};

		private int[] cMajorScale = new int[]{noteC, noteD, noteE, noteF, noteG, noteA, noteB};

		private int[] cMinorScale = new int[]{noteC, noteD, noteDSharp, noteF, noteG, noteGSharp, noteASharp};

		@Override
		public void actionPerformed(ActionEvent e) {

			Index musicIndex = cacheLibrary.getIndex(6);
			Index patchIndex = cacheLibrary.getIndex(15);

			SoundBankCache soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));

			ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			Sequence midiSeq;
			try {
				midiSeq = new Sequence(Sequence.PPQ, 960);
				MidiSystem.write(generateSong(midiSeq, mood), 1, byteArrayOutputStream);

			} catch (InvalidMidiDataException | IOException ex) {
				ex.printStackTrace();
			}

			MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
			MidiTrack.midi = byteArrayOutputStream.toByteArray();
			MidiTrack.loadMidiTrackInfo();

			MidiPcmStream midiPcmStream = new MidiPcmStream();
			midiPcmStream.init(9, 128);
			midiPcmStream.setMusicTrack(midiTrack, false);
			midiPcmStream.loadMusicTrack(midiTrack, patchIndex, soundBankCache, 0);

			SoundPlayer soundPlayer = new SoundPlayer();
			soundPlayer.setStream(midiPcmStream);
			soundPlayer.samples = new int[512];
			soundPlayer.capacity = 16384;
			soundPlayer.init();
			soundPlayer.open(soundPlayer.capacity);

			while (midiPcmStream.active) {
				soundPlayer.fill(soundPlayer.samples, 256);
				soundPlayer.write();

				if (midiPcmStream.midiFile.isDone()) {
					break;
				}
			}
		}

		private Sequence generateSong(Sequence midiSeq, String mood) {
			switch (mood) {
				case "Default":
					midiSeq = generateDefaultMood(midiSeq);
			}
			return midiSeq;
		}

		private Sequence generateDefaultMood(Sequence midiSeq) {
			return midiSeq;
		}
	}

	private class MidiTransposer implements ActionListener {

		private String desiredMood = "Happy";
		private String determinedKey = "Cm";

		private int noteC = 0;
		private int noteCSharp = 1;
		private int noteD = 2;
		private int noteDSharp = 3;
		private int noteE = 4;
		private int noteF = 5;
		private int noteFSharp = 6;
		private int noteG = 7;
		private int noteGSharp = 8;
		private int noteA = 9;
		private int noteASharp = 10;
		private int noteB = 11;

		private int[] baseCNotes = new int[]{0, 12, 24, 36, 48, 60, 72, 84, 96, 108, 120};

		private int[] cMajorScale = new int[]{noteC, noteD, noteE, noteF, noteG, noteA, noteB};

		private int[] cMinorScale = new int[]{noteC, noteD, noteDSharp, noteF, noteG, noteGSharp, noteASharp};

		@Override
		public void actionPerformed(ActionEvent e) {

			Index musicIndex = cacheLibrary.getIndex(6);
			Index patchIndex = cacheLibrary.getIndex(15);

			SoundBankCache soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));
			midiPcmStream = new MidiPcmStream();

			try {

				PcmPlayer.pcmPlayer_stereo = true;

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());

				Sequence midiSeq = MidiSystem.getSequence(midiFile);
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				MidiSystem.write(transposeSong(midiSeq, desiredMood), 1, byteArrayOutputStream);

				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				MidiTrack.midi = byteArrayOutputStream.toByteArray();
				MidiTrack.loadMidiTrackInfo();

				midiPcmStream.init(9, 128);
				midiPcmStream.setMusicTrack(midiTrack, loopMode);
				midiPcmStream.setPcmStreamVolume(volume);
				midiPcmStream.loadMusicTrack(midiTrack, patchIndex, soundBankCache, 0);

				SoundPlayer soundPlayer = new SoundPlayer();
				soundPlayer.setStream(midiPcmStream);
				soundPlayer.samples = new int[512];
				soundPlayer.capacity = 16384;
				soundPlayer.init();
				soundPlayer.open(soundPlayer.capacity);

				Thread songThread = new Thread(() -> {
					while (midiPcmStream.active) {
						soundPlayer.fill(soundPlayer.samples, 256);
						soundPlayer.write();
						if (midiPcmStream.midiFile.isDone()) {
							break;
						}
					}
				});

				songThread.start();
			} catch (IOException | InvalidMidiDataException ex) {
				ex.printStackTrace();
			}
		}

		private Sequence transposeSong(Sequence midiSeq, String mood) throws InvalidMidiDataException {
			switch (mood) {
				case "Happy":
					midiSeq = transposeHappyMood(midiSeq);
			}
			return midiSeq;
		}

		private Sequence transposeHappyMood(Sequence midiSeq) throws InvalidMidiDataException {
			for (Track track : midiSeq.getTracks()) {
				for (int event = 0; event < track.size(); event++) {
					MidiEvent midiEvent = track.get(event);
					MidiMessage midiMessage = midiEvent.getMessage();
					if (midiMessage instanceof ShortMessage) {
						ShortMessage shortMessage = (ShortMessage) midiMessage;

						if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
							int noteValue = shortMessage.getData1();
							for (int keys = 0; keys < cMinorScale.length; keys++) {
								for (int octaves = 0; octaves < baseCNotes.length; octaves++) {
									if (baseCNotes[octaves] * keys == noteValue) {
										noteValue++;
										shortMessage.setMessage(shortMessage.getCommand(), shortMessage.getChannel(), noteValue, shortMessage.getData2());
									}
								}
							}
						}

						if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
							int noteValue = shortMessage.getData1();
							for (int keys = 0; keys < cMinorScale.length; keys++) {
								for (int octaveKeys = 0; octaveKeys < baseCNotes.length; octaveKeys++) {
									if (keys * octaveKeys == noteValue) {
										noteValue = baseCNotes[octaveKeys] - keys;
										System.out.println(noteValue);
										shortMessage.setMessage(shortMessage.getCommand(), shortMessage.getChannel(), noteValue, shortMessage.getData2());
									}
								}
							}
						}
					}
				}
			}
			return midiSeq;
		}
	}
}
