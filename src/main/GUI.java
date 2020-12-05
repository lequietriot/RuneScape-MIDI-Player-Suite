package main;

import main.utils.ByteArrayNode;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class GUI {
	
	private static File midiFile;
	private File soundFontFile;
	private static File[] midiFiles;

	private final MidiLoader midiLoader = new MidiLoader();

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
	private JButton shuffleButton;
	private JButton testButton;
	private JButton renderMIDItoWavButton;

	private static JSlider songSlider;

	private static JTextPane songSliderInfo;

	private JCheckBox fixAttemptOS;
	private JCheckBox fixAttemptHD;

	private JTextPane volumeInfo;
	private JTextPane sampleRateInfo;
	private JTextPane engineInfo;
	private int volume = 256;

	private boolean loopMode = false;
	private boolean shuffleMode = false;

	private MidiPcmStream midiPcmStream;

	private MidiDevice externalDevice;
	private Transmitter transmitter;
	private Receiver receiver;

	GUI() throws IOException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		frame = new JFrame("RuneScape MIDI Player Suite - v2.0");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(700, 300));
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
			preferencesMenu.add("SoundBank - MIDI Effects").addActionListener(new SoundBankControls());

			utilityMenu = new JMenu();
			utilityMenu.setText("Tools");
			utilityMenu.setSize(100, 20);
			utilityMenu.setVisible(true);

			utilityMenu.add("Encode Data - MIDI Music...").addActionListener(new MidiEncoder());
			utilityMenu.add("Create Sound Bank Patch").addActionListener(new PatchCreator());
			utilityMenu.add("Generate Sound - MIDI Note...").addActionListener(new MidiNoteGenerator());
			//utilityMenu.add("Generate Random Music").addActionListener(new RandomMidiGenerator());
			//utilityMenu.add("Modify Existing Music").addActionListener(new MidiTransposer());

			utilityMenu.add("Dump Data - MIDI Music...").addActionListener(new MidiDumper());
			utilityMenu.add("Dump Data - MIDI and SoundFont").addActionListener(new MidiAndSoundFontDumper());
			utilityMenu.add("Dump Data - Sound Effects...").addActionListener(new SfxDumper());
            utilityMenu.add("Dump Data - Sound Bank Samples...").addActionListener(new MusicSampleDumper());
			utilityMenu.add("Dump Data - Sound Bank Patch...").addActionListener(new SoundBankPatchDumper());

			utilityMenu.add("Dump Data - RS3 Sound Effects...").addActionListener(new StreamSoundEffectDumper());

			utilityMenu.add("Fix MIDI File (OS Version)").addActionListener(new FixButtonListenerOSRS());
			utilityMenu.add("Fix MIDI File (HD Version)").addActionListener(new FixButtonListenerRSHD());

			utilityMenu.add("Convert Data - Sound Bank to SoundFont").addActionListener(new SoundFontCreator());
			utilityMenu.add("Convert Data - DLS Export...").addActionListener(new DLSCreator());

			utilityMenu.add("Dump Raw Soundbank").addActionListener(new SoundDumper());
			utilityMenu.add("Pack Raw Soundbank").addActionListener(new SoundPacker());

			utilityMenu.add("---");
			utilityMenu.add("Test MIDI with selected SoundBank").addActionListener(new SoundBankSongTester());
			utilityMenu.add("Test MIDI with a custom SoundBank").addActionListener(new SoundBankPatchTest());
			utilityMenu.add("Write song to file using SoundBank").addActionListener(new SoundBankSongDumper());
			utilityMenu.add("Write song to file using custom SoundBank").addActionListener(new CustomSoundBankDumper());
			utilityMenu.add("Batch Convert with SoundBank").addActionListener(new BatchConverter());
			utilityMenu.add("Batch Convert with custom SoundBank").addActionListener(new CustomBatchConverter());
			utilityMenu.add("Test SoundBank - Live").addActionListener(new SoundBankTester());

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

			Path midiFolderFile = Paths.get("./DefaultMidiPath.txt/");
			Path sf2PrefFile = Paths.get("./DefaultSoundfontPath.txt/");
            Path cachePrefFile = Paths.get("./DefaultCachePath.txt/");

            if (midiFolderFile.toFile().exists()) {
				List<String> midiString = Files.readAllLines(midiFolderFile);
				for (int i = 0; i < midiString.size(); i++) {
					String midiPath = midiString.get(i);
					System.out.println("Setting Midi Folder to " + midiPath);
					midiFiles = new File(midiPath).listFiles(new MidiFileFilter());
				}
			}

			if (sf2PrefFile.toFile().exists()) {
				List<String> prefString = Files.readAllLines(sf2PrefFile);
				
				for (int s = 0; s < prefString.size(); s++) {
				String pathString = prefString.get(s);
				System.out.println("Automatically set Soundfont to " + prefString);
				soundFontFile = new File(pathString);

				if (!soundFontFile.exists()) {
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

		if (midiFiles == null) {
			File homePath = new File(System.getProperty("user.home"));
			midiFiles = homePath.listFiles(new MidiFileFilter());
		}

		JPanel midiPanel = new JPanel(new BorderLayout());

		String[] midiNames = new String[midiFiles.length];
		for (int index = 0; index < midiNames.length; index++) {
			midiNames[index] = midiFiles[index].getName();
		}

		final JList<String> midiList = new JList<>(midiNames);
		JScrollPane midiScrollPane = new JScrollPane(midiList);
		midiScrollPane.setViewportView(midiList);
		midiList.setLayoutOrientation(JList.VERTICAL);
		midiList.addMouseListener(new MidiMousePress());
		midiPanel.add(midiScrollPane);
		midiPanel.setVisible(true);

		JFrame midiFrame = new JFrame("MIDI Files");
		midiFrame.add(midiPanel);
		midiFrame.setSize(500, 200);
		midiFrame.setLocationRelativeTo(null);
		midiFrame.setVisible(true);
		midiFrame.setResizable(false);
		midiFrame.pack();

		startButton = new JButton();
		pauseButton = new JButton();
		stopButton = new JButton();
		loopButton = new JButton();
		shuffleButton = new JButton();
		testButton = new JButton();
		
		startButton.addActionListener(new StartButtonListener());
		pauseButton.addActionListener(new PauseButtonListener());
		stopButton.addActionListener(new StopButtonListener());
		loopButton.addActionListener(new LoopButtonListener());
		shuffleButton.addActionListener(new ShuffleButtonListener());
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

		if (loopMode) {
			loopButton.setText("Loop Mode: On");
		}
		else {
			loopButton.setText("Loop Mode: Off");
		}

		loopButton.setVisible(true);

		buttonsPanel.add(shuffleButton);

		if (shuffleMode) {
			shuffleButton.setText("Shuffle Mode: On");
		}
		else {
			shuffleButton.setText("Shuffle Mode: Off");
		}

		shuffleButton.setVisible(true);

		testButton.setText("Test");
		testButton.setVisible(true);
		buttonsPanel.add(testButton);

		buttonsPanel.setVisible(true);
		buttonsPanel.setBackground(Color.LIGHT_GRAY);
		
		songPanel = new JPanel();
		songSlider = new JSlider();
		songSliderInfo = new JTextPane();
		volumeInfo = new JTextPane();
		sampleRateInfo = new JTextPane();
		engineInfo = new JTextPane();
		
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
		volumeInfo.setText("SoundBank Volume is: " + volume + " (" + ((volume / 256) * 100) + "%) |");
		volumeInfo.setEnabled(true);
		volumeInfo.setEditable(false);
		volumeInfo.setVisible(true);
		volumeInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
		volumeInfo.setAlignmentY(Component.TOP_ALIGNMENT);

		sampleRateInfo.setBackground(Color.LIGHT_GRAY);
		sampleRateInfo.setSelectedTextColor(Color.BLACK);
		sampleRateInfo.setText("SoundBank Sample Rate is: " + PcmPlayer.pcmPlayer_sampleRate + " |");
		sampleRateInfo.setEnabled(true);
		sampleRateInfo.setEditable(false);
		sampleRateInfo.setVisible(true);
		sampleRateInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
		sampleRateInfo.setAlignmentY(Component.TOP_ALIGNMENT);

		engineInfo.setBackground(Color.LIGHT_GRAY);
		engineInfo.setSelectedTextColor(Color.BLACK);
		engineInfo.setText("SoundBank Engine: " + PatchBanks.RUNESCAPE_VERSION);
		engineInfo.setEnabled(true);
		engineInfo.setEditable(false);
		engineInfo.setVisible(true);
		engineInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
		engineInfo.setAlignmentY(Component.BOTTOM_ALIGNMENT);

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
		infoPanel.add(engineInfo);

		frame.add(infoPanel);
		frame.add(buttonsPanel);
		frame.pack();
	}

	private static class MidiMousePress implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				for (File midi : midiFiles) {
					if (midi.getName().contains(((JList) e.getSource()).getSelectedValue().toString())) {
						setMidiFile(midi);
						break;
					}
				}
			}
		}

		private void setMidiFile(File midi) {

			midiFile = midi;

			if (midiFile.exists()) {
				songSlider.setEnabled(true);
				songSliderInfo.setText("Song loaded: " + midiFile.getName());
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}

	private static class MidiFileFilter implements FileFilter {

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().contains(".mid");
		}
	}

	private void LoadSoundFont(JFrame frame) {
		chooseSf2 = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
		chooseSf2.setSize(400, 200);
		chooseSf2.setDialogTitle("Please choose a SoundFont File");
		chooseSf2.setVisible(true);
		frame.add(chooseSf2);
		int returnValue = chooseSf2.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			soundFontFile = chooseSf2.getSelectedFile();
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
			midiFiles = chooseMID.getSelectedFile().listFiles();
		}
	}

	public class MIDILoader implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			try {

				LoadMIDI(frame);

				File preferences = new File("./DefaultMidiPath.txt/");
				FileOutputStream fos;

				try {

					fos = new FileOutputStream(preferences);
					DataOutputStream dos = new DataOutputStream(fos);
					dos.writeBytes(midiFile.getPath().replace(midiFile.getName(), ""));
					dos.flush();
					dos.close();

				} catch (IOException e1) {
					e1.printStackTrace();
				}
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
				midiLoader.load(MidiSystem.getSoundbank(soundFontFile), midiFile);

				if (loopMode) {
					setLoop(MidiSystem.getSequence(midiFile));
				}

				if (pausedTime != 0) {
					midiLoader.resume();
				}

				else {
					midiLoader.play();
				}
			} catch (InvalidMidiDataException | IOException | MidiUnavailableException invalidMidiDataException) {
				invalidMidiDataException.printStackTrace();
			}

			/**

			startButton.setEnabled(true);

			try {

				sequence = MidiSystem.getSequence(midiFile);

				sequencer = MidiSystem.getSequencer(false);
				sequencer.open();

				synthesizer = MidiSystem.getSynthesizer();
				synthesizer.open();

				if (!synthesizer.loadAllInstruments(MidiSystem.getSoundbank(soundsetFile))) {
					synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
					synthesizer.loadAllInstruments(MidiSystem.getSoundbank(soundsetFile));
				}

				MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

				for (MidiDevice.Info info : infos) {
					if (info.getName().contains("FluidSynth")) {
						if (!MidiSystem.getMidiDevice(info).isOpen()) {
							MidiSystem.getMidiDevice(info).open();
							System.out.println("Using FluidSynth!");
						}

						//sequencer.getTransmitter().setReceiver(MidiSystem.getMidiDevice(info).getReceiver());
					}
					else {
						sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
					}
				}

				sequencer.setSequence(sequence);

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

		void setLoop(Sequence midiSequence) {

			loopMarker = new StringBuilder();

			for (Track track : midiSequence.getTracks()) {
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
										loopEnd = (int) midiSequence.getTickLength();
									}
								}
							}
						}
					}
				}

				try {
					midiLoader.setLoop(loopStart, loopEnd, Sequencer.LOOP_CONTINUOUSLY);
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}
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

			midiLoader.pause();
			pausedTime = midiLoader.pausedPosition;

			//sequencer.stop();
			
			if (pausedTime != 0) {
				pauseButton.setEnabled(false);
			}

			if (!midiLoader.isSequencerRunning()) {
				startButton.setEnabled(true);
			}
			
			if (!midiLoader.isSequencerRunning()) {
				Timer timer = new Timer(0, new TimerListener());
				timer.stop();
			}
		}
	}
	
	public class StopButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pausedTime = 0;

			//sequencer.stop();
			midiLoader.stop();
			
			if (!midiLoader.isSequencerRunning()) {
				pauseButton.setEnabled(true);
				startButton.setEnabled(true);
			}
			
			if (!midiLoader.isSequencerRunning()) {
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
			int position = (int) (midiLoader.getSequencePosition() / 1000000);
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
				soundbank = MidiSystem.getSoundbank(soundFontFile);
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

		int songArchiveID = 2;

		@Override
		public void actionPerformed(ActionEvent e) {

			if (midiFile != null) {
				try {
					sequence = MidiSystem.getSequence(midiFile);
					File encodedMidi = MidiTrack.encode(sequence);
					byte[] encodedData = Files.readAllBytes(Paths.get(encodedMidi.getPath()));

					cacheLibrary.getIndex(6).getArchive(songArchiveID).removeFile(0);
					cacheLibrary.getIndex(6).addArchive(songArchiveID).addFile(encodedData);
					cacheLibrary.getIndex(6).getArchive(songArchiveID).getFile(0).setName(0);
					cacheLibrary.getIndex(6).update();

					System.out.println("MIDI file successfully encoded and packed to ID " + songArchiveID + "!");

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

				if (cacheLibrary.getIndex(4).getArchive(idInt) != null) {

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

						System.out.println("Loop Start: " + audioBuffer.start);
						System.out.println("Loop End: " + audioBuffer.end);
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

						System.out.println("Loop Start: " + audioBuffer.start);
						System.out.println("Loop End: " + audioBuffer.end);
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
		private Index index4;
		private Index index14;
		private Index index15;
		private SoundBankCache soundBankCache;
		private static int bankLSBValue;
		private static int programValue;
		private static int patchValue;
		private static int noteValue;
		private MidiPcmStream midiPcmStream;

		@Override
		public void actionPerformed(ActionEvent e) {

			if (programInput == null && noteInput == null) {

				try {

					sequence = new Sequence(Sequence.PPQ, 960);

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

			index4 = cacheLibrary.getIndex(4);
			index14 = cacheLibrary.getIndex(14);
			index15 = cacheLibrary.getIndex(15);

			soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));

			File filePath = new File("./Generated Midi/");
			File generatedMidi = new File("./Generated Midi/" + patchValue + "_" + noteValue + ".mid/");

			if (filePath.mkdir()) {
				try {
					DataOutputStream dos = new DataOutputStream(new FileOutputStream(generatedMidi));
					MidiSystem.write(sequence, 1, dos);

					SoundBankCache soundBankCache = new SoundBankCache(index4, index14);
					midiPcmStream = new MidiPcmStream();
					Path path = Paths.get(generatedMidi.toURI());

					try {

						PcmPlayer.pcmPlayer_stereo = true;

						ByteBuffer byteBuffer = ByteBuffer.wrap(cacheLibrary.getIndex(6).getArchive(0).getFile(0).getData());

						MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
						MidiTrack.midi = Files.readAllBytes(path);
						MidiTrack.loadMidiTrackInfo();

						midiPcmStream.init(9, 128);
						midiPcmStream.setMusicTrack(midiTrack, false);
						midiPcmStream.setPcmStreamVolume(255 / 2);
						midiPcmStream.loadMusicTrack(midiTrack, index15, soundBankCache, 0);

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

						File outFile = new File("./MIDI Audio/" + generatedMidi.getName().replace(".mid", "") + ".wav/");
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

				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			else {
				try {
					DataOutputStream dos = new DataOutputStream(new FileOutputStream(generatedMidi));
					MidiSystem.write(sequence, 1, dos);

					SoundBankCache soundBankCache = new SoundBankCache(index4, index14);
					midiPcmStream = new MidiPcmStream();
					Path path = Paths.get(generatedMidi.toURI());

					try {

						PcmPlayer.pcmPlayer_stereo = true;

						ByteBuffer byteBuffer = ByteBuffer.wrap(cacheLibrary.getIndex(6).getArchive(0).getFile(0).getData());

						MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
						MidiTrack.midi = Files.readAllBytes(path);
						MidiTrack.loadMidiTrackInfo();

						midiPcmStream.init(9, 128);
						midiPcmStream.setMusicTrack(midiTrack, false);
						midiPcmStream.setPcmStreamVolume(255 / 2);
						midiPcmStream.loadMusicTrack(midiTrack, index15, soundBankCache, 0);

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

						File outFile = new File("./MIDI Audio/" + generatedMidi.getName().replace(".mid", "") + ".wav/");
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
					//track.add(new MidiEvent(sustain, 1));
					track.add(new MidiEvent(noteOnValue, 3));
					track.add(new MidiEvent(noteOffValue, 30003));
					track.add(new MidiEvent(noteOnValue, 60003));
					track.add(new MidiEvent(noteOffValue, 60006));
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
			}

			SoundBankCache soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));

			for (int archive = 0; archive < 1; archive++) {
				MakeSoundFont makeSoundFont = new MakeSoundFont();
				makeSoundFont.initSoundFont();
				if (patchIndex.getArchive(archive) != null) {
					makeSoundFont.makeSoundFont(new MusicPatch(patchIndex.getArchive(archive).getFile(0).getData()), soundBankCache, archive);
					makeSoundFont.saveSoundBank(archive);
					System.out.println("Created a new SoundFont using Patch " + archive);
				}
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

		@Override
		public void actionPerformed(ActionEvent e) {

			Thread songThread = new Thread(() -> {

				try {

					SoundPlayer soundPlayer = initMidiStream(midiFile);

					while (midiPcmStream.active) {
						playSound(soundPlayer);

						if (shuffleMode && midiPcmStream.midiFile.isDone()) {
							soundPlayer = initMidiStream(midiFiles[(int) (Math.random() * midiFiles.length)]);
						}
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			});
			songThread.start();
		}

		private void playSound(SoundPlayer soundPlayer) {
			soundPlayer.fill(soundPlayer.samples, 256);
			soundPlayer.write();
		}

		private SoundPlayer initMidiStream(File midi) throws IOException {

			songSliderInfo.setText("Song loaded: " + midi.getName());

			midiPcmStream = new MidiPcmStream();
			Path path = Paths.get(midi.toURI());
			PcmPlayer.pcmPlayer_stereo = true;

			MidiTrack midiTrack = new MidiTrack();
			MidiTrack.midi = Files.readAllBytes(path);
			MidiTrack.loadMidiTrackInfo();

			midiPcmStream.init(9, 128);
			midiPcmStream.setMusicTrack(midiTrack, loopMode);
			midiPcmStream.setPcmStreamVolume(volume);
			midiPcmStream.loadTestSoundBankCompletely();

			SoundPlayer soundPlayer = new SoundPlayer();
			soundPlayer.setStream(midiPcmStream);
			soundPlayer.samples = new int[512];
			soundPlayer.capacity = 16384;
			soundPlayer.init();
			soundPlayer.open(soundPlayer.capacity);

			return soundPlayer;
		}

		private void initMidi() throws InvalidMidiDataException, InterruptedException {

			Sequence sequence = new Sequence(Sequence.PPQ, 960);
			sequencer.setSequence(sequence);

			for (int track = 0; track < 16; track++) {
				sequencer.recordEnable(sequence.createTrack(), track);
			}

			sequencer.startRecording();
			Thread.sleep(10000);
			sequencer.stopRecording();
		}

		/**
		public void unusedAction() {

			Index musicIndex = cacheLibrary.getIndex(6);

			try {
				midiPcmStream = new MidiPcmStream();
				Path path = Paths.get(midiFile.toURI());
				PcmPlayer.pcmPlayer_stereo = true;

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());
				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				MidiTrack.midi = Files.readAllBytes(path);
				MidiTrack.loadMidiTrackInfo();

				midiPcmStream.init(9, 128);
				midiPcmStream.setMusicTrack(midiTrack, loopMode);
				midiPcmStream.setPcmStreamVolume(volume);
				midiPcmStream.loadTestSoundBank(midiTrack);

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

			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		 **/
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

						for (MusicPatchNode musicPatchNode = (MusicPatchNode) midiPcmStream.patchStream.queue.first(); musicPatchNode != null; musicPatchNode = (MusicPatchNode) midiPcmStream.patchStream.queue.next()) {
							//System.out.println();
							//System.out.println("volume : " + midiPcmStream.calculateVolume(musicPatchNode));
							//System.out.println("method 3864 : " + midiPcmStream.method3864(musicPatchNode));
							//System.out.println("method 3819 : " + midiPcmStream.method3819(musicPatchNode));
						}

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

	private class SoundBankTester implements ActionListener {

		CustomReceiver customReceiver;

		@Override
		public void actionPerformed(ActionEvent e) {

			try {

				customReceiver = new CustomReceiver();
				sequencer = MidiSystem.getSequencer();
				sequencer.open();

				MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

				for (int index = 0; index < infos.length; index++) {

					MidiDevice midiDevice = MidiSystem.getMidiDevice(infos[index]);

					if (midiDevice.getDeviceInfo().getName().contains("Bus")) {

						if (!midiDevice.isOpen()) {
							midiDevice.open();
							MidiSystem.getTransmitter().setReceiver(customReceiver);
							//midiDevice.getTransmitter().setReceiver(customReceiver);
							System.out.println(infos[index].getName());
							break;
						}
					}
				}

				Index soundEffectIndex = cacheLibrary.getIndex(4);
				Index soundBankIndex = cacheLibrary.getIndex(14);
				Index musicPatchIndex = cacheLibrary.getIndex(15);

				SoundBankCache soundBankCache = new SoundBankCache(soundEffectIndex, soundBankIndex);
				midiPcmStream = new MidiPcmStream();

				PcmPlayer.pcmPlayer_stereo = true;

				if (customReceiver.midiData != null) {
					MidiTrack midiTrack = new MidiTrack();
					midiTrack.midi = customReceiver.midiData;
					MidiTrack.loadMidiTrackInfo();
					midiPcmStream.init(9, 128);
					midiPcmStream.setMusicTrack(midiTrack, false);
					midiPcmStream.setPcmStreamVolume(volume);
					midiPcmStream.loadMusicTrack(midiTrack, musicPatchIndex, soundBankCache, 0);
				}

				CustomReceiver finalCustomReceiver = customReceiver;

				SoundPlayer soundPlayer = new SoundPlayer();
				soundPlayer.setStream(midiPcmStream);
				soundPlayer.samples = new int[16384];
				soundPlayer.capacity = 16384;
				soundPlayer.init();
				soundPlayer.open(soundPlayer.capacity);

				do {

					while (finalCustomReceiver.midiData != null) {
						MidiTrack nextMidi = new MidiTrack();
						nextMidi.midi = finalCustomReceiver.midiData;
						midiPcmStream = new MidiPcmStream();
						midiPcmStream.init(9, 128);
						midiPcmStream.setPcmStreamVolume(volume);
						midiPcmStream.setMusicTrack(nextMidi, false);
						midiPcmStream.loadMusicTrack(nextMidi, musicPatchIndex, soundBankCache, 0);

						soundPlayer.setStream(midiPcmStream);
						soundPlayer.fill(soundPlayer.samples, 256);
						soundPlayer.write();
					}

				} while (true);

			} catch (MidiUnavailableException midiUnavailableException) {
				midiUnavailableException.printStackTrace();
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

		File[] midiFiles;

		@Override
		public void actionPerformed(ActionEvent e) {

			try {

				selectMidiFolder(frame);

				if (midiFiles != null) {
					writeMidiAudio();
				}
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}

		private void writeMidiAudio() throws IOException {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index musicIndex = cacheLibrary.getIndex(6);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			MusicPatch.localSoundBankSamples = new File("./Sounds/Sound Bank Samples/");
			MusicPatch.localSoundBankPatches = new File("./Sounds/Sound Bank Patches/");
			MusicPatch.localSoundEffects = new File("./Sounds/Sound Effects/");

			SoundBankCache soundBankCache = new SoundBankCache(soundEffectIndex, soundBankIndex);

			for (File midiSeqFile : midiFiles) {

				if (midiSeqFile.getName().contains(".mid")) {

					System.out.println(midiSeqFile.getName());

					midiPcmStream = new MidiPcmStream();
					Path path = Paths.get(midiSeqFile.toURI());

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

					File midiAudioDirectory = new File("./MIDI Audio/");

					if (midiAudioDirectory.mkdirs()) {

						File outFile = new File(midiAudioDirectory + "/" + midiSeqFile.getName().replace(".mid", "") + ".wav/");
						FileOutputStream fos;

						try {

							fos = new FileOutputStream(outFile);
							AudioFormat format = new AudioFormat(PcmPlayer.pcmPlayer_sampleRate, 16, 2, true, false);
							AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
							AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fos);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					} else {

						File outFile = new File(midiAudioDirectory + "/" + midiSeqFile.getName().replace(".mid", "") + ".wav/");
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

				else {
					System.out.println("Invalid MIDI File... Skipping this one!");
				}
			}
		}

		private void selectMidiFolder(JFrame frame) throws IOException {
			JFileChooser chooseMidiFolder = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
			chooseMidiFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooseMidiFolder.setSize(400, 200);
			chooseMidiFolder.setDialogTitle("Please choose a folder containing MIDI files.");
			chooseMidiFolder.setVisible(true);
			frame.add(chooseMidiFolder);
			int returnValue = chooseMidiFolder.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File path = new File(chooseMidiFolder.getSelectedFile().getPath());
				midiFiles = path.listFiles();
			}
		}
	}

	private class SoundBankPatchTest implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index musicIndex = cacheLibrary.getIndex(6);

			MusicPatch.localCustomSoundBank = new File("./Sounds/Custom Sound Bank/");

			try {

				midiPcmStream = new MidiPcmStream();
				Path path = Paths.get(midiFile.toURI());
				PcmPlayer.pcmPlayer_stereo = true;

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());

				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				MidiTrack.midi = Files.readAllBytes(path);
				MidiTrack.loadMidiTrackInfo();

				midiPcmStream.init(9, 128);
				midiPcmStream.setMusicTrack(midiTrack, loopMode);
				midiPcmStream.setPcmStreamVolume(volume);
				//midiPcmStream.loadCustomSoundBank(midiTrack);

				try {
					//midiPcmStream.loadCreateSoundFontBanks(MidiSystem.getSoundbank(soundsetFile));
					midiPcmStream.loadSoundFontBank(midiTrack, MidiSystem.getSoundbank(soundFontFile));
				} catch (InvalidMidiDataException invalidMidiDataException) {
					invalidMidiDataException.printStackTrace();
				}

				SoundPlayer soundPlayer = new SoundPlayer();
				soundPlayer.setStream(midiPcmStream);
				soundPlayer.samples = new int[512];
				soundPlayer.capacity = 16384;
				soundPlayer.init();
				soundPlayer.open(soundPlayer.capacity);

				Thread songThread = new Thread(() -> {
					while (midiPcmStream.active) {
						soundPlayer.fill(soundPlayer.samples, 256);
						soundPlayer.writeCustom(); //write

						if (midiPcmStream.midiFile.isDone()) {
							break;
						}
					}
				});

				songThread.start();

			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

	private class SoundBankPatchDumper implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			MusicPatch.localCustomSoundBank = new File("./Sounds/Custom Sound Bank/");

			MusicPatch.localSoundBankSamples = new File("./Sounds/Sound Bank Samples/");
			MusicPatch.localSoundBankPatches = new File("./Sounds/Sound Bank Patches/");
			MusicPatch.localSoundEffects = new File("./Sounds/Sound Effects/");

			SoundBankCache soundBankCache = new SoundBankCache(soundEffectIndex, soundBankIndex);
			//MakeSoundFont makeSoundFont = new MakeSoundFont();
			//makeSoundFont.initSoundFont();

			for (int id = 0; id < 4000; id++) {

				File patch = new File(MusicPatch.localCustomSoundBank + "/" + PatchBanks.RUNESCAPE_VERSION + "/Patches/" + id + ".dat/");

				if (patch.exists()) {

					try {

						MusicPatch musicPatch = new MusicPatch(Files.readAllBytes(Path.of(patch.getPath())));

						File patchText = new File("./Patch Text Files/" + id + ".txt/");
						FileOutputStream patchFileOut = new FileOutputStream(patchText);
						BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(patchFileOut), 160);

						bufferedWriter.write(PatchBanks.PATCH_NAME + id);
						bufferedWriter.newLine();

						for (int index = 0; index < 128; index++) {

							if (musicPatch.musicPatchNode2[index] != null) {

								bufferedWriter.write('\n');
								bufferedWriter.write(PatchBanks.SAMPLE_NAME + getSampleOffsetID(musicPatch, index));

								int rootKey = ((musicPatch.pitchOffset[index] / 256) + 128);

								while (rootKey > 128) {
									rootKey = rootKey - 128;
								}

								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.SAMPLE_ROOT_KEY + rootKey);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.KEY_LOW_RANGE + index);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.KEY_HIGH_RANGE + index);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.MASTER_VOLUME + musicPatch.baseVelocity);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.LOOP_START + getLoopStart(soundBankCache, getSampleOffsetID(musicPatch, index), isSfx(musicPatch, index)));
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.LOOP_END + getLoopEnd(soundBankCache, getSampleOffsetID(musicPatch, index), isSfx(musicPatch, index)));
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.SAMPLE_VOLUME + musicPatch.volumeOffset[index]);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.SAMPLE_PAN + musicPatch.panOffset[index]);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.PARAMETER_1 + musicPatch.musicPatchNode2[index].volumeEnvelopeDecay);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.PARAMETER_2 + musicPatch.musicPatchNode2[index].volumeEnvelopeRelease);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.PARAMETER_3 + musicPatch.musicPatchNode2[index].vibratoLFODelay);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.PARAMETER_4 + musicPatch.musicPatchNode2[index].vibratoLFOFrequency);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.PARAMETER_5 + musicPatch.musicPatchNode2[index].vibratoLFOPitch);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.PARAMETER_6 + musicPatch.musicPatchNode2[index].volumeEnvelopeSustain);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.PARAMETER_7 + musicPatch.musicPatchNode2[index].field2394);
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.ARRAY_1 + Arrays.toString(musicPatch.musicPatchNode2[index].field2398));
								bufferedWriter.newLine();
								bufferedWriter.write(PatchBanks.ARRAY_2 + Arrays.toString(musicPatch.musicPatchNode2[index].field2402));
								bufferedWriter.newLine();
								bufferedWriter.flush();
							}
						}

						System.out.println("Wrote patch " + id);

					} catch (IOException ioException) {
						ioException.printStackTrace();
					}
				}
			}

			/**
			for (int index = 0; index < 4000; index++) {
				File patch = new File(MusicPatch.localCustomSoundBank + "/" + PatchBanks.RUNESCAPE_VERSION + "/Patches/" + index + ".dat/");
				if (patch.exists()) {
					try {
						MusicPatch musicPatch = new MusicPatch(Files.readAllBytes(Path.of(patch.getPath())));
						makeSoundFont.addSamplesCustom(musicPatch);
					} catch (IOException | UnsupportedAudioFileException ioException) {
						ioException.printStackTrace();
					}
				}
			}

			for (int index = 0; index < 4000; index++) {
				if (musicPatchIndex.getArchive(index) != null) {
					MusicPatch musicPatch = new MusicPatch(musicPatchIndex.getArchive(index).getFile(0).getData());
					makeSoundFont.addSamples(musicPatch, soundBankCache);
				}
			}
			 **/

			//makeSoundFont.saveSoundBank();
		}

		private boolean isSfx(MusicPatch musicPatch, int note) {

			int var5 = 0;
			int var8 = musicPatch.sampleOffset[note];

			if (var8 != 0) {
				if (var5 != var8) {
					var5 = var8--;
					if ((var8 & 1) == 0) {
						return true;
					} else {
						return false;
					}
				}
			}

			return false;
		}

		private int getLoopStart(SoundBankCache soundBankCache, int sampleOffsetID, boolean soundEffects) {

			if (soundEffects) {
				return soundBankCache.getSoundEffect(sampleOffsetID, null).start;
			}
			else {
				return soundBankCache.getMusicSample(sampleOffsetID, null).start;
			}
		}

		private int getLoopEnd(SoundBankCache soundBankCache, int sampleOffsetID, boolean soundEffects) {

			if (soundEffects) {
				return soundBankCache.getSoundEffect(sampleOffsetID, null).end;
			}
			else {
				return soundBankCache.getMusicSample(sampleOffsetID, null).end;
			}
		}

		private int getSampleOffsetID(MusicPatch musicPatch, int note) {

			int var5 = 0;
			int var8 = musicPatch.sampleOffset[note];

			if (var8 != 0) {
				if (var5 != var8) {
					var5 = var8--;
					if ((var8 & 1) == 0) {
						return var8 >> 2;
					} else {
						return var8 >> 2;
					}
				}
			}
			return var5;
		}
	}

	private class CustomSoundBankDumper implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index musicIndex = cacheLibrary.getIndex(6);

			MusicPatch.localCustomSoundBank = new File("./Sounds/Custom Sound Bank/");

			try {

				midiPcmStream = new MidiPcmStream();
				Path path = Paths.get(midiFile.toURI());
				PcmPlayer.pcmPlayer_stereo = true;

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());

				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				MidiTrack.midi = Files.readAllBytes(path);
				MidiTrack.loadMidiTrackInfo();

				midiPcmStream.init(9, 128);
				midiPcmStream.setMusicTrack(midiTrack, loopMode);
				midiPcmStream.setPcmStreamVolume(volume);
				//midiPcmStream.loadCustomSoundBank(midiTrack);
				try {
					midiPcmStream.loadSoundFontBank(midiTrack, MidiSystem.getSoundbank(soundFontFile));
				} catch (InvalidMidiDataException invalidMidiDataException) {
					invalidMidiDataException.printStackTrace();
				}

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

	private class SoundBankControls implements ActionListener {

		private JTextField patchVolumeTextField;

		@Override
		public void actionPerformed(ActionEvent e) {

			JFrame soundBankFrame = new JFrame("SoundBank - Audio Controls");

			JPanel controlPanel = new JPanel();
			controlPanel.setVisible(true);

			patchVolumeTextField = new JTextField("Value");
			patchVolumeTextField.setVisible(true);
			patchVolumeTextField.addActionListener(actionEvent -> checkForVolumeInput(this.patchVolumeTextField));

			controlPanel.add(patchVolumeTextField);

			soundBankFrame.setLayout(null);
			soundBankFrame.setResizable(false);
			soundBankFrame.setMinimumSize(new Dimension(400, 100));
			soundBankFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			soundBankFrame.setContentPane(controlPanel);
			soundBankFrame.setLocationRelativeTo(null);
			soundBankFrame.setVisible(true);
			soundBankFrame.pack();
		}

		private void checkForVolumeInput(JTextField sampleRateTextField) {

			String volumeInput = sampleRateTextField.getText();
			int value = Integer.parseInt(volumeInput);

			if (midiPcmStream != null) {
				for (MusicPatchNode musicPatchNode = (MusicPatchNode) midiPcmStream.patchStream.queue.first(); musicPatchNode != null; musicPatchNode = (MusicPatchNode) midiPcmStream.patchStream.queue.next()) {
					for (int index = 0; index < 128; index++) {
						musicPatchNode.patch.musicPatchNode2[index].volumeEnvelopeDecay = value;

					}
				}
			}
		}
	}

	private class CustomBatchConverter implements ActionListener {

		File[] midiFiles;

		@Override
		public void actionPerformed(ActionEvent e) {

			try {

				selectMidiFolder(frame);

				if (midiFiles != null) {

					MusicPatch.localCustomSoundBank = new File("./Sounds/Custom Sound Bank/");

					for (File midiSeqFile : midiFiles) {
						writeMidiAudio(midiSeqFile);
					}
				}
			} catch (IOException | InvalidMidiDataException ioException) {
				ioException.printStackTrace();
			}
		}

		private void writeMidiAudio(File midiSeqFile) throws IOException, InvalidMidiDataException {

			Index musicIndex = cacheLibrary.getIndex(6);

			if (midiSeqFile.getName().contains(".mid")) {

				System.out.println(midiSeqFile.getName());

				midiPcmStream = new MidiPcmStream();

				Path path = Paths.get(midiSeqFile.toURI());

				PcmPlayer.pcmPlayer_stereo = true;

				ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(0).getFile(0).getData());

				MidiTrack midiTrack = MidiTrack.getMidiTrackData(byteBuffer);
				try {
					MidiTrack.midi = Files.readAllBytes(path);
					MidiTrack.loadMidiTrackInfo();

					Soundbank soundBank = MidiSystem.getSoundbank(soundFontFile);

					midiPcmStream.init(9, 128);
					midiPcmStream.setMusicTrack(midiTrack, loopMode);
					midiPcmStream.setPcmStreamVolume(volume);
					//midiPcmStream.loadMusicTrack(midiTrack, musicPatchIndex, soundBankCache, 0);
					midiPcmStream.loadSoundFontBank(midiTrack, MidiSystem.getSoundbank(soundFontFile));

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

					File midiAudioDirectory = new File("./MIDI Audio/");

					if (midiAudioDirectory.mkdirs()) {

						File outFile = new File(midiAudioDirectory + "/" + midiSeqFile.getName().replace(".mid", "") + ".wav/");
						FileOutputStream fos;

						try {

							fos = new FileOutputStream(outFile);
							AudioFormat format = new AudioFormat(PcmPlayer.pcmPlayer_sampleRate, 16, 2, true, false);
							AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
							AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fos);
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					} else {

						File outFile = new File(midiAudioDirectory + "/" + midiSeqFile.getName().replace(".mid", "") + ".wav/");
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
				} catch (IOException | InvalidMidiDataException e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Invalid MIDI File... Skipping this one!");
			}
		}

		private void selectMidiFolder(JFrame frame) throws IOException {
			JFileChooser chooseMidiFolder = new JFileChooser(FileSystemView.getFileSystemView().getDefaultDirectory());
			chooseMidiFolder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooseMidiFolder.setSize(400, 200);
			chooseMidiFolder.setDialogTitle("Please choose a folder containing MIDI files.");
			chooseMidiFolder.setVisible(true);
			frame.add(chooseMidiFolder);
			int returnValue = chooseMidiFolder.showOpenDialog(null);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				File path = new File(chooseMidiFolder.getSelectedFile().getPath());
				midiFiles = path.listFiles();
			}
		}
	}

	private class PatchCreator implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				File patchFile = new File("./Patch.dat/");
				FileOutputStream fileOutputStream = new FileOutputStream(patchFile);
				DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
				dataOutputStream.write(0);

			} catch (IOException error) {
				error.printStackTrace();
			}
		}
	}

	private static class DLSCreator implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index musicPatchIndex = cacheLibrary.getIndex(15);
			SoundBankCache soundBankCache = new SoundBankCache(cacheLibrary.getIndex(4), cacheLibrary.getIndex(14));
			MusicPatch musicPatch = new MusicPatch(musicPatchIndex.getArchive(0).getFile(0).getData());

			MakeDLS makeDLS = new MakeDLS();
			makeDLS.initDLS();
			makeDLS.createDLSInstruments(musicPatch, soundBankCache);
			makeDLS.saveDLSBanks();
		}
	}

	private class MidiAndSoundFontDumper implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			Index soundEffectIndex = cacheLibrary.getIndex(4);
			Index musicIndex = cacheLibrary.getIndex(6);
			Index soundBankIndex = cacheLibrary.getIndex(14);
			Index musicPatchIndex = cacheLibrary.getIndex(15);

			int songID = 500;

			SoundBankCache soundBankCache = new SoundBankCache(soundEffectIndex, soundBankIndex);

			ByteBuffer byteBuffer = ByteBuffer.wrap(musicIndex.getArchive(songID).getFile(0).getData());

			MidiTrack midiTrack = MidiTrack.getMidiTrack(byteBuffer);
			midiTrack.loadMidiTrackInfo();

			MakeSoundFont makeSoundFont = new MakeSoundFont();
			makeSoundFont.initSoundFont();

			for (ByteArrayNode tableIndex = (ByteArrayNode) midiTrack.table.first(); tableIndex != null; tableIndex = (ByteArrayNode) midiTrack.table.next()) {
				makeSoundFont.makeSoundFont(new MusicPatch(musicPatchIndex.getArchive((int) tableIndex.key).getFile(0).getData()), soundBankCache, (int) tableIndex.key);
			}

			try {
				DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./SoundFonts/" + songID + ".mid")));
				dos.write(midiTrack.getMidi());
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}

			makeSoundFont.saveSoundBank(songID);
		}
	}

	private class ShuffleButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			shuffleMode = !shuffleMode;

			if (shuffleMode) {
				shuffleButton.setText("Shuffle Mode: On");
			}
			else {
				shuffleButton.setText("Shuffle Mode: Off");
			}
		}
	}
}
