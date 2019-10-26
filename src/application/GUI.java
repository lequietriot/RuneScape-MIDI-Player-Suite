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

import javax.sound.midi.ControllerEventListener;
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
import application.utils.MidiFixerRSHD;

public class GUI implements ControllerEventListener {
	
	private File midiFile;
	private File soundsetFile;

	private String defaultSoundfontPath;

	private boolean fixAttemptingOS;
	private boolean fixAttemptingHD;

	private long pausedTime;
	private long runningTime;
	private long totalTime;

	private Sequence sequence;

	private Sequencer sequencer1;
	private Sequencer sequencer2;
	private Sequencer sequencer3;
	private Sequencer sequencer4;
	private Sequencer sequencer5;
	private Sequencer sequencer6;

	private Synthesizer synth1;
	private Synthesizer synth2;
	private Synthesizer synth3;
	private Synthesizer synth4;
	private Synthesizer synth5;
	private Synthesizer synth6;

	private Sequence sequenceFixed;

	private JFrame frame;
	private JPanel panel;
	private JPanel songPanel;
	private JMenu fileMenu;
	private JMenu preferencesMenu;
	private JMenu utilityMenu;

	private JFileChooser chooseMID;
	private JFileChooser chooseSf2;
	private JFileChooser saveRepatchedMIDI;

	private JButton startButton;
	private JButton pauseButton;
	private JButton stopButton;
	private JButton renderMIDItoWavButton;

	private JSlider songSlider;

	private JTextPane songSliderInfo;

	private JCheckBox fixAttemptOS;
	private JCheckBox fixAttemptHD;

	private ControllerEventListener volumeListener;
	private ControllerEventListener retriggerListener;
	private boolean retriggerEffect = false;
	private int retriggerValue;

	GUI() throws MidiUnavailableException, InvalidMidiDataException, IOException {
		frame = new JFrame("RuneScape MIDI Player");
		JFrame.setDefaultLookAndFeelDecorated(true);
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
			
			utilityMenu.add("Fix MIDI File (OS Version)").addActionListener(new FixButtonListenerOSRS());
			utilityMenu.add("Fix MIDI File (HD Version)").addActionListener(new FixButtonListenerRSHD());
			
			jMenuBar.add(fileMenu);
			jMenuBar.add(preferencesMenu);
			jMenuBar.add(utilityMenu);
			
			System.out.println("Application loaded successfully!");
		}
		
		finally {
			
			Path sf2PrefFile = Paths.get("./DefaultSoundfontPath.txt/");
			
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
			
			else if (!sf2PrefFile.toFile().exists()) {
				FileOutputStream fos = new FileOutputStream("./DefaultSoundfontPath.txt/");
				DataOutputStream dos = new DataOutputStream(fos);
				dos.write(0);
				dos.flush();
				dos.close();
			}

			init(buttonsPanel);
			initSynthesizers();
		}
	}

	private void init(JPanel buttonsPanel) {
		
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
			try {
				initSynthesizers();
			} catch (MidiUnavailableException | InvalidMidiDataException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initSynthesizers() throws MidiUnavailableException, InvalidMidiDataException, IOException {

		System.out.println("Initializing Synthesizers, please wait...");

		if (soundsetFile.exists()) {

			synth1 = MidiSystem.getSynthesizer();
			synth1.open();
			synth1.loadAllInstruments(MidiSystem.getSoundbank(soundsetFile));

			System.out.println("Loaded Synthesizer 1!");

			synth2 = MidiSystem.getSynthesizer();
			synth2.open();
			synth2.loadAllInstruments(MidiSystem.getSoundbank(soundsetFile));

			System.out.println("Loaded Synthesizer 2!");

			synth3 = MidiSystem.getSynthesizer();
			synth3.open();
			synth3.loadAllInstruments(MidiSystem.getSoundbank(soundsetFile));

			System.out.println("Loaded Synthesizer 3!");

			synth4 = MidiSystem.getSynthesizer();
			synth4.open();
			synth4.loadAllInstruments(MidiSystem.getSoundbank(soundsetFile));

			System.out.println("Loaded Synthesizer 4!");

			synth5 = MidiSystem.getSynthesizer();
			synth5.open();
			synth5.loadAllInstruments(MidiSystem.getSoundbank(soundsetFile));

			System.out.println("Loaded Synthesizer 5!");

			synth6 = MidiSystem.getSynthesizer();
			synth6.open();
			synth6.loadAllInstruments(MidiSystem.getSoundbank(soundsetFile));

			System.out.println("Loaded Synthesizer 6!");
		}
	}

	private void LoadMIDI(JFrame frame) {
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

	@Override
	public void controlChange(ShortMessage event) {
		
		if (!retriggerEffect) {
		
			if (event.getData1() == 81 & event.getData2() >= 64) {
				retriggerEffect = true;
			}
			
			else {
				retriggerEffect = false;
			}
		}
		
		if (event.getData1() == 17 & retriggerEffect) {
			retriggerValue = (int) (2097152.0D * Math.pow(2.0D, 5.4931640625E-4D * event.getData2()) + 0.5D);
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
				try {
					initSynthesizers();
				} catch (MidiUnavailableException | InvalidMidiDataException ex) {
					ex.printStackTrace();
				}
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
		
		public void actionPerformed(ActionEvent e) {
			
			if (sequencer1 == null) {
				startButton.setEnabled(true);
			}
			
			try {

				sequence = MidiSystem.getSequence(midiFile);

				sequencer1 = MidiSystem.getSequencer(false);
				sequencer1.open();
				sequencer1.getTransmitter().setReceiver(synth1.getReceiver());

				sequencer2 = MidiSystem.getSequencer(false);
				sequencer2.open();
				sequencer2.getTransmitter().setReceiver(synth2.getReceiver());

				sequencer3 = MidiSystem.getSequencer(false);
				sequencer3.open();
				sequencer3.getTransmitter().setReceiver(synth3.getReceiver());

				sequencer4 = MidiSystem.getSequencer(false);
				sequencer4.open();
				sequencer4.getTransmitter().setReceiver(synth4.getReceiver());

				sequencer5 = MidiSystem.getSequencer(false);
				sequencer5.open();
				sequencer5.getTransmitter().setReceiver(synth5.getReceiver());

				sequencer6 = MidiSystem.getSequencer(false);
				sequencer6.open();
				sequencer6.getTransmitter().setReceiver(synth6.getReceiver());

				setSequencerSolo(sequence.getTracks().length);

				if (!fixAttemptingOS) {
					sequencer1.setSequence(sequence);
					sequencer2.setSequence(sequence);
					sequencer3.setSequence(sequence);
					sequencer4.setSequence(sequence);
					sequencer5.setSequence(sequence);
					sequencer6.setSequence(sequence);

					setSequencerSolo(sequence.getTracks().length);
				}
				
				else {
					sequencer1.setSequence(adjustForPlayOS(sequence));
					sequencer2.setSequence(adjustForPlayOS(sequence));
					sequencer3.setSequence(adjustForPlayOS(sequence));
					sequencer4.setSequence(adjustForPlayOS(sequence));
					sequencer5.setSequence(adjustForPlayOS(sequence));
					sequencer6.setSequence(adjustForPlayOS(sequence));

					setSequencerSolo(sequence.getTracks().length);
				}
				
				if (!fixAttemptingHD) {
					sequencer1.setSequence(sequence);
					sequencer2.setSequence(sequence);
					sequencer3.setSequence(sequence);
					sequencer4.setSequence(sequence);
					sequencer5.setSequence(sequence);
					sequencer6.setSequence(sequence);

					setSequencerSolo(sequence.getTracks().length);
				}
				
				else {
					sequencer1.setSequence(adjustForPlayHD(sequence));
					sequencer2.setSequence(adjustForPlayHD(sequence));
					sequencer3.setSequence(adjustForPlayHD(sequence));
					sequencer4.setSequence(adjustForPlayHD(sequence));
					sequencer5.setSequence(adjustForPlayHD(sequence));
					sequencer6.setSequence(adjustForPlayHD(sequence));

					setSequencerSolo(sequence.getTracks().length);
				}
				
				if (pausedTime == 0) {
					sequencer1.start();
					sequencer2.start();
					sequencer3.start();
					sequencer4.start();
					sequencer5.start();
					sequencer6.start();

					if (sequencer1.isRunning()) {
						startButton.setEnabled(false);
						pauseButton.setEnabled(true);
					}
				}
				
				else {
					sequencer1.setMicrosecondPosition(pausedTime);
					sequencer2.setMicrosecondPosition(pausedTime);
					sequencer3.setMicrosecondPosition(pausedTime);
					sequencer4.setMicrosecondPosition(pausedTime);
					sequencer5.setMicrosecondPosition(pausedTime);
					sequencer6.setMicrosecondPosition(pausedTime);

					sequencer1.start();
					sequencer2.start();
					sequencer3.start();
					sequencer4.start();
					sequencer5.start();
					sequencer6.start();

					if (sequencer1.isRunning()) {
						startButton.setEnabled(false);
						pauseButton.setEnabled(true);
					}
				}
				
				if (sequencer1.isRunning()) {
					Timer timer = new Timer(100, new TimerListener());
					timer.start();
				}
			} catch (MidiUnavailableException | InvalidMidiDataException | IOException e1) {
				e1.printStackTrace();
			}
		}

		private void setSequencerSolo(int trackCount) {

			switch (trackCount) {
				case 0:
					System.out.println("Track count is 0 or invalid MIDI file.");
				case 1:
					sequencer1.setTrackSolo(0, true);
				case 2:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
				case 3:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
				case 4:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
				case 5:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
				case 6:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
				case 7:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
				case 8:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
				case 9:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
				case 10:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
				case 11:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
				case 12:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
				case 13:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
				case 14:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
				case 15:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
				case 16:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
				case 17:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
					sequencer5.setTrackSolo(16, true);
				case 18:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
					sequencer5.setTrackSolo(16, true);
					sequencer5.setTrackSolo(17, true);
				case 19:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
					sequencer5.setTrackSolo(16, true);
					sequencer5.setTrackSolo(17, true);
					sequencer5.setTrackSolo(18, true);
				case 20:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
					sequencer5.setTrackSolo(16, true);
					sequencer5.setTrackSolo(17, true);
					sequencer5.setTrackSolo(18, true);
					sequencer5.setTrackSolo(19, true);
				case 21:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
					sequencer5.setTrackSolo(16, true);
					sequencer5.setTrackSolo(17, true);
					sequencer5.setTrackSolo(18, true);
					sequencer5.setTrackSolo(19, true);
					sequencer6.setTrackSolo(20, true);
				case 22:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
					sequencer5.setTrackSolo(16, true);
					sequencer5.setTrackSolo(17, true);
					sequencer5.setTrackSolo(18, true);
					sequencer5.setTrackSolo(19, true);
					sequencer6.setTrackSolo(20, true);
					sequencer6.setTrackSolo(21, true);
				case 23:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
					sequencer5.setTrackSolo(16, true);
					sequencer5.setTrackSolo(17, true);
					sequencer5.setTrackSolo(18, true);
					sequencer5.setTrackSolo(19, true);
					sequencer6.setTrackSolo(20, true);
					sequencer6.setTrackSolo(21, true);
					sequencer6.setTrackSolo(22, true);
				case 24:
					sequencer1.setTrackSolo(0, true);
					sequencer1.setTrackSolo(1, true);
					sequencer1.setTrackSolo(2, true);
					sequencer1.setTrackSolo(3, true);
					sequencer2.setTrackSolo(4, true);
					sequencer2.setTrackSolo(5, true);
					sequencer2.setTrackSolo(6, true);
					sequencer2.setTrackSolo(7, true);
					sequencer3.setTrackSolo(8, true);
					sequencer3.setTrackSolo(9, true);
					sequencer3.setTrackSolo(10, true);
					sequencer3.setTrackSolo(11, true);
					sequencer4.setTrackSolo(12, true);
					sequencer4.setTrackSolo(13, true);
					sequencer4.setTrackSolo(14, true);
					sequencer4.setTrackSolo(15, true);
					sequencer5.setTrackSolo(16, true);
					sequencer5.setTrackSolo(17, true);
					sequencer5.setTrackSolo(18, true);
					sequencer5.setTrackSolo(19, true);
					sequencer6.setTrackSolo(20, true);
					sequencer6.setTrackSolo(21, true);
					sequencer6.setTrackSolo(22, true);
					sequencer6.setTrackSolo(23, true);
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
			pausedTime = sequencer1.getMicrosecondPosition();
			sequencer1.stop();
			sequencer2.stop();
			sequencer3.stop();
			sequencer4.stop();
			sequencer5.stop();
			sequencer6.stop();
			
			if (pausedTime != 0) {
				pauseButton.setEnabled(false);
			}
			
			if (!sequencer1.isRunning()) {
				startButton.setEnabled(true);
			}
			
			if (!sequencer1.isRunning()) {
				Timer timer = new Timer(0, new TimerListener());
				timer.stop();
			}
		}
	}
	
	public class StopButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			pausedTime = 0;
			sequencer1.stop();
			sequencer2.stop();
			sequencer3.stop();
			sequencer4.stop();
			sequencer5.stop();
			sequencer6.stop();
			
			if (!sequencer1.isRunning()) {
				pauseButton.setEnabled(true);
				startButton.setEnabled(true);
			}
			
			if (!sequencer1.isRunning()) {
				Timer timer = new Timer(100, new TimerListener());
				timer.stop();
				songSlider.setValue(0);
			}
		}
	}

	public class TimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int position = (int) (sequencer1.getMicrosecondPosition() / 1000000);
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
	}
