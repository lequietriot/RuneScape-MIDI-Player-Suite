package application.utils;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

public class MidiFixerRSHD {
	
	static String patch0 = "Acoustic Grand Piano";
	static String patch1 = "Bright Piano";
	static String patch2 = "Electric Grand Piano";
	static String patch3 = "Honky-tonk Piano";
	static String patch4 = "Electric Piano 1";
	static String patch5 = "Electric Piano 2";
	static String patch6 = "Harpsichord";
	static String patch7 = "Clavinet";
	static String patch8 = "Celesta";
	static String patch9 = "Glockenspiel";
	static String patch10 = "Music Box";
	static String patch11 = "Vibraphone";
	static String patch12 = "Marimba";
	static String patch13 = "Xylophone";
	static String patch14 = "Tubular Bells";
	static String patch15 = "Dulcimer";
	static String patch16 = "Drawbar Organ";
	static String patch17 = "Percussive Organ";
	static String patch18 = "Rock Organ";
	static String patch19 = "Church Organ";
	static String patch20 = "Reed Organ";
	static String patch21 = "Accordion";
	static String patch22 = "Harmonica";
	static String patch23 = "Tango Accordion";
	static String patch24 = "Nylon String Guitar";
	static String patch25 = "Steel String Guitar";
	static String patch26 = "Jazz Guitar";
	static String patch27 = "Clean Guitar";
	static String patch28 = "Muted Guitar";
	static String patch29 = "Overdriven Guitar";
	static String patch30 = "Distortion Guitar";
	static String patch31 = "Guitar Harmonics";
	static String patch32 = "Acoustic Bass";
	static String patch33 = "Picked Bass";
	static String patch34 = "Fingered Bass";
	static String patch35 = "Fretless Bass";
	static String patch36 = "Slap Bass 1";
	static String patch37 = "Slap Bass 2";
	static String patch38 = "Synth Bass 1";
	static String patch39 = "Synth Bass 2";
	static String patch40 = "Violin";
	static String patch41 = "Viola";
	static String patch42 = "Cello";
	static String patch43 = "Contrabass";
	static String patch44 = "Tremolo Strings";
	static String patch45 = "Pizzicato Strings";
	static String patch46 = "Harp";
	static String patch47 = "Timpani";
	static String patch48 = "Strings";
	static String patch49 = "Slow Strings";
	static String patch50 = "Synth Strings 1";
	static String patch51 = "Synth Strings 2";
	static String patch52 = "Choir Aahs";
	static String patch53 = "Voice Oohs";
	static String patch54 = "Synth Voice";
	static String patch55 = "Orchestra Hit";
	static String patch56 = "Trumpet";
	static String patch57 = "Trombone";
	static String patch58 = "Tuba";
	static String patch59 = "Muted Trumpet";
	static String patch60 = "French Horn";
	static String patch61 = "Brass Section";
	static String patch62 = "Synth Brass 1";
	static String patch63 = "Synth Brass 2";
	static String patch64 = "Soprano Saxophone";
	static String patch65 = "Alto Saxophone";
	static String patch66 = "Tenor Saxophone";
	static String patch67 = "Baritone Saxophone";
	static String patch68 = "Oboe";
	static String patch69 = "English Horn";
	static String patch70 = "Bassoon";
	static String patch71 = "Clarinet";
	static String patch72 = "Piccolo";
	static String patch73 = "Flute";
	static String patch74 = "Recorder";
	static String patch75 = "Pan Flute";
	static String patch76 = "Blown Bottle";
	static String patch77 = "Shakuhachi";
	static String patch78 = "Whistle";
	static String patch79 = "Ocarina";
	static String patch80 = "Square Wave";
	static String patch81 = "Sawtooth Wave";
	static String patch82 = "Calliope Synth";
	static String patch83 = "Chiffer Synth";
	static String patch84 = "Charang Synth";
	static String patch85 = "Synth Voice Lead";
	static String patch86 = "Sawtooth Fifths";
	static String patch87 = "Bass and Lead Synth";
	static String patch88 = "New Age Pad";
	static String patch89 = "Warm Pad";
	static String patch90 = "Polysynth";
	static String patch91 = "Choir Pad";
	static String patch92 = "Bowed Pad";
	static String patch93 = "Metallic Pad";
	static String patch94 = "Halo Pad";
	static String patch95 = "Sweep Pad";
	static String patch96 = "Rain Pad";
	static String patch97 = "Soundtrack Pad";
	static String patch98 = "Crystal";
	static String patch99 = "Atmosphere";
	static String patch100 = "Brightness";
	static String patch101 = "Goblins";
	static String patch102 = "Echo Drops";
	static String patch103 = "Sci-Fi Pad";
	static String patch104 = "Sitar";
	static String patch105 = "Banjo";
	static String patch106 = "Shamisen";
	static String patch107 = "Koto";
	static String patch108 = "Kalimba";
	static String patch109 = "Bagpipes";
	static String patch110 = "Fiddle";
	static String patch111 = "Shanai";
	static String patch112 = "Tinkle Bell";
	static String patch113 = "Agogo";
	static String patch114 = "Steel Drums";
	static String patch115 = "Woodblocks";
	static String patch116 = "Taiko Drums";
	static String patch117 = "Melodic Toms";
	static String patch118 = "Synth Drum";
	static String patch119 = "Reverse Cymbal";
	static String patch120 = "Guitar Fret Noise";
	static String patch121 = "Breath Noise";
	static String patch122 = "Seashore SFX";
	static String patch123 = "Bird Tweet SFX";
	static String patch124 = "Telephone Ring SFX";
	static String patch125 = "Helicopter SFX";
	static String patch126 = "Applause SFX";
	static String patch127 = "Gunshot SFX";
	
	static String patch128 = "Standard Drum Kit";
	static String patch129 = "Gong Percussion"; // Custom Percussion
	static String patch136 = "Room Drum Kit";
	static String patch144 = "Power Drum Kit";
	static String patch152 = "Electronic Drum Kit";
	static String patch153 = "Analog Drum Kit";
	static String patch168 = "Brush Drum Kit";
	static String patch176 = "Orchestral Drum Kit";
	static String patch178 = "Ancient Drum Kit"; // Custom Percussion
	static String patch179 = "Ambient Drum Kit"; // Custom Percussion
	static String patch184 = "Special Effects Drum Kit";
	static String patch255 = "Standard Drum Kit";
	
	static String patch256 = "Choir Aahs (2)"; //This patch is transposed down 12 notes.
	static String patch257 = "Warm Pad (2)";
	static String patch258 = "Pizzicato Strings (2)";
	static String patch259 = "Taiko Drum (2)";
	static String patch260 = "Pan Flute (2)"; // This patch is transposed up 12 notes.
	static String patch261 = "Strings Marcato";
	static String patch262 = "Slow Strings (2)";
	static String patch263 = "Acoustic Grand Piano (2)";
	static String patch264 = "New Age Pad (2)";
	static String patch265 = "Synth Flute";
	static String patch266 = "Choir Aahs (3)";
	static String patch267 = "Spring SFX 1";
	static String patch268 = "Spring SFX 2";
	static String patch269 = "Spring SFX 3";
	static String patch270 = "Spring SFX 4";
	static String patch271 = "Spring SFX 5";
	static String patch272 = "Spring SFX 6";
	static String patch273 = "Spring SFX 7";
	static String patch274 = "Spring SFX 8";
	static String patch275 = "Spring SFX 9";
	static String patch276 = "Spring SFX 10";
	static String patch277 = "Strings (2)";
	static String patch278 = "Choir Aahs (4)"; // This patch is transposed down 24 notes.
	static String patch279 = "Choir Eehs"; // This patch is transposed down 24 notes.
	static String patch280 = "Frozen Ambient Pad";
	static String patch281 = "Abandoned Ambient Pad";
	static String patch282 = "Brass Section (2)";
	static String patch283 = "Brass Section (3)";
	static String patch284 = "Brass Section (4)";
	static String patch285 = "Brass Section (5)";
	static String patch286 = "Brass Section (6)";
	static String patch287 = "Furnished Ambient Pad";
	static String patch288 = "Warped Ambient Pad";
	static String patch289 = "Paranormal SFX";
	static String patch290 = "Ghost Voice Oohs";
	static String patch291 = "Black Zabeth Vocals";
	static String patch292 = "Overdriven Guitar (2)";
	static String patch293 = "Slow Tremolo Strings";
	static String patch294 = "Synth Bass 3";
	static String patch295 = "Deathcon II, Part 1";
	static String patch296 = "Deathcon II, Part 2";
	static String patch297 = "One Piercing Note, Part 1";
	static String patch298 = "Valerio's Song";
	static String patch299 = "Citharede Requiem Vocals";
	static String patch300 = "Tremolo Strings (2)";
	static String patch301 = "Diamond Jubilee";
	static String patch302 = "Percussion Loops";
	static String patch303 = "Varrock Saradominist Church";
	
	static String patch384 = "Pirate Shanty Vocals";
	static String patch385 = "Dominion Tower, Part 1";
	static String patch386 = "Dominion Tower, Part 2";
	static String patch387 = "Dominion Tower, Part 3";
	static String patch388 = "Traveller's Tale";
	static String patch389 = "One Piercing Note, Part 2";
	static String patch390 = "One Piercing Note, Part 3";
	static String patch391 = "One Piercing Note - Quest Complete";
	static String patch392 = "Squeal of Fortune";
	static String patch393 = "Masterwork Music Box";
	static String patch394 = "Order of Ascension";
	static String patch395 = "Vorago";
	static String patch396 = "Bringing Home the Bacon";
	
	static String patch512 = "Wind SFX";

	static String patch641 = "Burthorpe and Taverley, Part 1";
	static String patch642 = "Taverley Folk";
	static String patch643 = "Burthorpe and Taverley, Part 2";
	static String patch644 = "Burthorpe and Taverley, Part 3";
	static String patch645 = "Burthorpe and Taverley, Part 4";
	static String patch646 = "Burthorpe and Taverley, Part 5";
	static String patch647 = "Burthorpe and Taverley, Part 6";

	static String patch768 = "TzHaar City, Part 1";
	static String patch769 = "TzHaar Supremacy I";
	static String patch770 = "TzHaar City, Part 2";
	static String patch771 = "TzHaar Supremacy II and III";
	static String patch773 = "Ga'al";
	
	static String patch896 = "RuneSpan, Part 1";
	static String patch897 = "RuneSpan, Part 2";
	static String patch898 = "RuneSpan, Part 3";
	static String patch899 = "RuneSpan, Part 4";
	static String patch900 = "RuneSpan, Part 5";
	static String patch901 = "RuneSpan, Part 6";
	static String patch902 = "RuneSpan, Part 7";
	static String patch903 = "RuneSpan, Part 8";
	static String patch904 = "RuneSpan, Part 9";
	static String patch905 = "RuneSpan, Part 10";
	static String patch906 = "RuneSpan, Part 11";
	static String patch907 = "RuneSpan, Part 12";
	static String patch908 = "Choir Oohs";
	static String patch909 = "Crystal (2)";
	static String patch910 = "Brass Section Crescendo";
	static String patch911 = "Tremolo Synthesizer";
	static String patch912 = "RuneSpan, Part 13";
	static String patch913 = "RuneSpan, Part 14";
	static String patch914 = "RuneSpan, Part 15";
	static String patch915 = "RuneSpan, Part 16";
	static String patch916 = "RuneSpan, Part 17";
	static String patch917 = "RuneSpan, Part 18";
	static String patch918 = "RuneSpan, Part 19";
	static String patch919 = "RuneSpan, Part 20";

	static String patch1025 = "Daemonheim";

	static String patch1153 = "Grotworm Ambience";
	static String patch1154 = "Song from the Depths, Part 1";
	static String patch1155 = "Song from the Depths, Part 2";
	static String patch1156 = "Song from the Depths, Part 3";
	static String patch1157 = "Queen Black Dragon";

	static String patch1281 = "The Crucible Ambience";
	static String patch1282 = "The Crucible, Part 1";

	static String patch1409 = "Old Scape Bold (200M Accounts Celebration)";

	static String patch1537 = "Carnillean Rising, Part 1";
	static String patch1538 = "Carnillean Rising, Part 2";
	static String patch1539 = "Carnillean Rising, Part 3";
	
	static String patch1665 = "Gielinor Games, Part 1";
	static String patch1666 = "Gielinor Games, Part 2";
	static String patch1667 = "Gielinor Games, Part 3";

	static String patch1793 = "Penguin Prison";
	static String patch1794 = "Sub Way";
	static String patch1795 = "Some Like It Cold - Quest Complete";
	static String patch1796 = "Some Like It Cold - The Prison Escape";
	static String patch1797 = "Some Like It Cold - Making Noise";

	static String patch1921 = "Big Chinchompa";

	static String patch2049 = "A Song For Khnum";
	static String patch2050 = "Agents of Keli";
	static String patch2051 = "Breaking In";
	static String patch2052 = "Darkness In Misthalin";
	static String patch2053 = "Draynor Market";
	static String patch2054 = "Everything in its Right Palace";
	static String patch2055 = "Kharidian Bustle";
	static String patch2056 = "Palace";
	static String patch2057 = "Palace Above";
	static String patch2058 = "Rooftops";
	static String patch2059 = "Skullery Jam";
	static String patch2060 = "Stand-off";
	static String patch2061 = "The Stalker";
	static String patch2062 = "Violinsanity";
	static String patch2063 = "While We Sleep";
	static String patch2064 = "Worlds";
	static String patch2065 = "Above the City";
	static String patch2066 = "Dune";
	static String patch2067 = "Oasis";
	static String patch2068 = "Sands of Time";
	static String patch2069 = "Shantay Pass";
	static String patch2070 = "The Cave";
	static String patch2071 = "The Chozan One";
	static String patch2072 = "Quicksand!";
	static String patch2073 = "Kalphite Nursery Ambience";
	static String patch2074 = "Desert Fanfares, Part 1";
	static String patch2075 = "Final Showdown";
	static String patch2076 = "Desert Fanfares, Part 2";
	static String patch2077 = "Ozan Quest Cutscene SFX";

	static String patch2177 = "Royal Rumble";
	
	static String patch2305 = "Tears of Guthix Rework";
	
	static String patch2433 = "Bloody Foundation";
	static String patch2434 = "Bloody Foundation II";
	static String patch2435 = "The Harmony of the Runes";
	static String patch2436 = "Blue Wizards";
	static String patch2437 = "The Ritual";
	static String patch2438 = "Inner Secret";
	static String patch2439 = "Forgotten Orders";
	static String patch2440 = "Vision Rework";

	static String patch2561 = "Sea Folk";
	static String patch2562 = "Sea Hear";
	static String patch2563 = "Sea You Late Oar";
	static String patch2564 = "Shanty Instrumental";
	static String patch2565 = "Stick Your Ale In";
	static String patch2566 = "Stick Your Oar In";

	static String patch2689 = "King of the Desert";
	static String patch2690 = "Coleoptera";
	static String patch2691 = "Insect Queen Rework";

	static String patch2817 = "Desert Fanfares, Part 3";
	
	static String patch2945 = "Temple of Guthix I";
	static String patch2946 = "Boulder and Brighter";
	static String patch2947 = "Guthix's Warning";
	static String patch2948 = "Defenders of Guthix I";
	static String patch2949 = "Defenders of Guthix II";
	static String patch2950 = "Invaders Must Die I";
	static String patch2951 = "Invaders Must Die II";
	static String patch2952 = "Invaders Must Die III";
	static String patch2953 = "End Song";
	static String patch2954 = "Memories of Guthix";
	static String patch2955 = "Chamber of Guthix";
	static String patch2956 = "Temple of Guthix II";
	static String patch2957 = "Bird of Prey";

	static String patch3073 = "Castle Wars Rework";
	static String patch3074 = "Castle Warz Rework";
	static String patch3075 = "Castle Wars Fanfares Rework";

	static String patch3201 = "Duel Arena Rework";
	static String patch3202 = "Duel Arena and Combat Fanfares Rework";

	static int bankLSB;

	static int customBank1;
	static int customBank2;
	static int customBank3;
	static int customBank4;
	static int customBank5;
	static int customBank6;
	static int customBank7;
	static int customBank8;
	static int customBank9;
	static int customBank10;
	static int customBank11;
	static int customBank12;
	static int customBank13;
	static int customBank14;
	static int customBank15;
	static int customBank16;
	static int customBank17;
	static int customBank18;
	static int customBank19;
	static int customBank20;
	static int customBank21;
	static int customBank22;
	static int customBank23;
	static int customBank24;
	static int customBank25;
	
	static int channelPosition;
	
	static int trackName = 0x03;
	
	public static Sequence returnFixedMIDI(Sequence sequence, boolean write) throws InvalidMidiDataException, IOException {
		
		for (Track track : sequence.getTracks()) {
			for (int i = 0; i < track.size(); i++) {
				MidiEvent midiEvent = track.get(i);
				MidiMessage midiMessage = midiEvent.getMessage();
				if (midiMessage instanceof ShortMessage) {
					ShortMessage sm = (ShortMessage) midiMessage;
					
					if (sm.getCommand() == ShortMessage.CONTROL_CHANGE) {
						if (sm.getData1() == 32) {
							bankLSB = sm.getData2();
							continue;
						}
						continue;
					}
					
					if (sm.getCommand() == ShortMessage.PROGRAM_CHANGE) {
					
						int program = sm.getData1();
						MetaMessage metaMessage = new MetaMessage();
						
						if (program == 0 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch0.getBytes(), patch0.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 0 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch128.getBytes(), patch128.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 0 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch256.getBytes(), patch256.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
							
							sm.setMessage(sm.getCommand(), sm.getChannel(), 52, 0);
							customBank2 = 1;
							channelPosition = sm.getChannel();
						}
						
						if (program == 1 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch1.getBytes(), patch1.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 1 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch1.getBytes(), patch1.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 0, 0);
							customBank1 = 2;
							channelPosition = sm.getChannel();
						}

						if (program == 1 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch257.getBytes(), patch257.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 89, 0);
							customBank2 = 2;
							channelPosition = sm.getChannel();
						}

						if (program == 2 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch2.getBytes(), patch2.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 2 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch258.getBytes(), patch258.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 45, 0);
							customBank2 = 3;
							channelPosition = sm.getChannel();
						}
						
						if (program == 3 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch3.getBytes(), patch3.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 3 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch259.getBytes(), patch259.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 116, 0);
							customBank2 = 4;
							channelPosition = sm.getChannel();
						}
						
						if (program == 4 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch4.getBytes(), patch4.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 4 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch260.getBytes(), patch260.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 75, 0);
							customBank2 = 5;
							channelPosition = sm.getChannel();
						}
						
						if (program == 5 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch5.getBytes(), patch5.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 5 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch261.getBytes(), patch261.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 48, 0);
							customBank2 = 6;
							channelPosition = sm.getChannel();
						}
						
						if (program == 6 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch6.getBytes(), patch6.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 6 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch262.getBytes(), patch262.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 49, 0);
							customBank2 = 7;
							channelPosition = sm.getChannel();
						}
						
						if (program == 7 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch7.getBytes(), patch7.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 7 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch263.getBytes(), patch263.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 0, 0);
							customBank2 = 8;
							channelPosition = sm.getChannel();
						}
						
						if (program == 8 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch8.getBytes(), patch8.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 8 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch136.getBytes(), patch136.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 8 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch264.getBytes(), patch264.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 88, 0);
							customBank2 = 9;
							channelPosition = sm.getChannel();
						}
						
						if (program == 9 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch9.getBytes(), patch9.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 9 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch265.getBytes(), patch265.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 73, 0);
							customBank2 = 10;
							channelPosition = sm.getChannel();
						}
						
						if (program == 10 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch10.getBytes(), patch10.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 10 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch266.getBytes(), patch266.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 52, 0);
							customBank2 = 11;
							channelPosition = sm.getChannel();
						}
						
						if (program == 11 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch11.getBytes(), patch11.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 11 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch267.getBytes(), patch267.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 12;
							channelPosition = sm.getChannel();
						}
						
						if (program == 12 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch12.getBytes(), patch12.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 12 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch268.getBytes(), patch268.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 13;
							channelPosition = sm.getChannel();
						}
						
						if (program == 13 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch13.getBytes(), patch13.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 13 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch269.getBytes(), patch269.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 14;
							channelPosition = sm.getChannel();
						}
						
						if (program == 14 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch14.getBytes(), patch14.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 14 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch270.getBytes(), patch270.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 15;
							channelPosition = sm.getChannel();
						}
						
						if (program == 15 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch15.getBytes(), patch15.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 15 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch271.getBytes(), patch271.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 16;
							channelPosition = sm.getChannel();
						}
						
						if (program == 16 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch16.getBytes(), patch16.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 16 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch144.getBytes(), patch144.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 16 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch272.getBytes(), patch272.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 17;
							channelPosition = sm.getChannel();
						}
						
						if (program == 17 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch17.getBytes(), patch17.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 17 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch273.getBytes(), patch273.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 18;
							channelPosition = sm.getChannel();
						}
						
						if (program == 18 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch18.getBytes(), patch18.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 18 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch274.getBytes(), patch274.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 19;
							channelPosition = sm.getChannel();
						}
						
						if (program == 19 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch19.getBytes(), patch19.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 19 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch275.getBytes(), patch275.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 20;
							channelPosition = sm.getChannel();
						}
						
						if (program == 20 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch20.getBytes(), patch20.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 20 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch276.getBytes(), patch276.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 120, 0);
							customBank2 = 21;
							channelPosition = sm.getChannel();
						}
						
						if (program == 21 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch21.getBytes(), patch21.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 21 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch277.getBytes(), patch277.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 48, 0);
							customBank2 = 22;
							channelPosition = sm.getChannel();
						}
						
						if (program == 22 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch22.getBytes(), patch22.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 22 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch278.getBytes(), patch278.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 52, 0);
							customBank2 = 23;
							channelPosition = sm.getChannel();
						}
						
						if (program == 23 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch23.getBytes(), patch23.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 23 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch279.getBytes(), patch279.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 52, 0);
							customBank2 = 24;
							channelPosition = sm.getChannel();
						}
						
						if (program == 24 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch24.getBytes(), patch24.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 24 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch152.getBytes(), patch152.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 24 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch280.getBytes(), patch280.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 97, 0);
							customBank2 = 25;
							channelPosition = sm.getChannel();
						}
						
						if (program == 25 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch25.getBytes(), patch25.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 25 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch153.getBytes(), patch153.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 25 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch281.getBytes(), patch281.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 97, 0);
							customBank2 = 26;
							channelPosition = sm.getChannel();
						}
						
						if (program == 26 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch26.getBytes(), patch26.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 26 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch282.getBytes(), patch282.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 61, 0);
							customBank2 = 27;
							channelPosition = sm.getChannel();
						}
						
						if (program == 27 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch27.getBytes(), patch27.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 27 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch283.getBytes(), patch283.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 61, 0);
							customBank2 = 28;
							channelPosition = sm.getChannel();
						}
						
						if (program == 28 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch28.getBytes(), patch28.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 28 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch284.getBytes(), patch284.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 61, 0);
							customBank2 = 29;
							channelPosition = sm.getChannel();
						}
						
						if (program == 29 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch29.getBytes(), patch29.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 29 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch285.getBytes(), patch285.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 61, 0);
							customBank2 = 30;
							channelPosition = sm.getChannel();
						}
						
						if (program == 30 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch30.getBytes(), patch30.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 30 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch286.getBytes(), patch286.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 61, 0);
							customBank2 = 31;
							channelPosition = sm.getChannel();
						}
						
						if (program == 31 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch31.getBytes(), patch31.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 31 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch287.getBytes(), patch287.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 97, 0);
							customBank2 = 32;
							channelPosition = sm.getChannel();
						}
						
						if (program == 32 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch32.getBytes(), patch32.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 32 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch288.getBytes(), patch288.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 97, 0);
							customBank2 = 33;
							channelPosition = sm.getChannel();
						}
						
						if (program == 33 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch33.getBytes(), patch33.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 33 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch289.getBytes(), patch289.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 126, 0);
							customBank2 = 34;
							channelPosition = sm.getChannel();
						}
						
						if (program == 34 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch34.getBytes(), patch34.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 34 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch290.getBytes(), patch290.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 53, 0);
							customBank2 = 35;
							channelPosition = sm.getChannel();
						}
						
						if (program == 35 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch35.getBytes(), patch35.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 35 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch291.getBytes(), patch291.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							customBank2 = 36;
							channelPosition = sm.getChannel();
						}
						
						if (program == 36 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch36.getBytes(), patch36.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 36 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch292.getBytes(), patch292.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 29, 0);
							customBank2 = 37;
							channelPosition = sm.getChannel();
						}
						
						if (program == 37 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch37.getBytes(), patch37.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 37 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch293.getBytes(), patch293.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 44, 0);
							customBank2 = 38;
							channelPosition = sm.getChannel();
						}
						
						if (program == 38 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch38.getBytes(), patch38.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 38 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch294.getBytes(), patch294.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 38, 0);
							customBank2 = 39;
							channelPosition = sm.getChannel();
						}
						
						if (program == 39 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch39.getBytes(), patch39.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 39 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch295.getBytes(), patch295.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							customBank2 = 40;
							channelPosition = sm.getChannel();
						}
						
						if (program == 40 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch40.getBytes(), patch40.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 40 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch168.getBytes(), patch168.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 40 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch296.getBytes(), patch296.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							customBank2 = 41;
							channelPosition = sm.getChannel();
						}
						
						if (program == 41 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch41.getBytes(), patch41.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 41 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch297.getBytes(), patch297.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							customBank2 = 42;
							channelPosition = sm.getChannel();
						}
						
						if (program == 42 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch42.getBytes(), patch42.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 42 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch298.getBytes(), patch298.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							customBank2 = 43;
							channelPosition = sm.getChannel();
						}
						
						if (program == 43 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch43.getBytes(), patch43.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 43 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch299.getBytes(), patch299.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							customBank2 = 44;
							channelPosition = sm.getChannel();
						}
						
						if (program == 44 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch44.getBytes(), patch44.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 44 & bankLSB == 2) {
							metaMessage.setMessage(trackName, patch300.getBytes(), patch300.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							customBank2 = 45;
							channelPosition = sm.getChannel();
						}
						
						if (program == 45 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch45.getBytes(), patch45.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 46 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch46.getBytes(), patch46.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 47 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch47.getBytes(), patch47.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 48 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch48.getBytes(), patch48.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 48 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch176.getBytes(), patch176.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 49 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch49.getBytes(), patch49.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 50 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch50.getBytes(), patch50.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 50 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch178.getBytes(), patch178.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 48, 0);
							customBank1 = 51;
							channelPosition = sm.getChannel();
						}
						
						if (program == 51 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch51.getBytes(), patch51.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 51 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch179.getBytes(), patch179.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);

							sm.setMessage(sm.getCommand(), sm.getChannel(), 48, 0);
							customBank1 = 52;
							channelPosition = sm.getChannel();
						}
						
						if (program == 52 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch52.getBytes(), patch52.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 53 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch53.getBytes(), patch53.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 54 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch54.getBytes(), patch54.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 55 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch55.getBytes(), patch55.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 56 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch56.getBytes(), patch56.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 56 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch184.getBytes(), patch184.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 57 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch57.getBytes(), patch57.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 58 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch58.getBytes(), patch58.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 59 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch59.getBytes(), patch59.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 60 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch60.getBytes(), patch60.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 61 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch61.getBytes(), patch61.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 62 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch62.getBytes(), patch62.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 63 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch63.getBytes(), patch63.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 64 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch64.getBytes(), patch64.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 65 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch65.getBytes(), patch65.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 66 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch66.getBytes(), patch66.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 67 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch67.getBytes(), patch67.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 68 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch68.getBytes(), patch68.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 69 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch69.getBytes(), patch69.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 70 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch70.getBytes(), patch70.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 71 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch71.getBytes(), patch71.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 72 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch72.getBytes(), patch72.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 73 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch73.getBytes(), patch73.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 74 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch74.getBytes(), patch74.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 75 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch75.getBytes(), patch75.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 76 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch76.getBytes(), patch76.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 77 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch77.getBytes(), patch77.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 78 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch78.getBytes(), patch78.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 79 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch79.getBytes(), patch79.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 80 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch80.getBytes(), patch80.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 81 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch81.getBytes(), patch81.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 82 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch82.getBytes(), patch82.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 83 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch83.getBytes(), patch83.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 84 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch84.getBytes(), patch84.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 85 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch85.getBytes(), patch85.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 86 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch86.getBytes(), patch86.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 87 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch87.getBytes(), patch87.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 88 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch88.getBytes(), patch88.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 89 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch89.getBytes(), patch89.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 90 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch90.getBytes(), patch90.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 91 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch91.getBytes(), patch91.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 92 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch92.getBytes(), patch92.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 93 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch93.getBytes(), patch93.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 94 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch94.getBytes(), patch94.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 95 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch95.getBytes(), patch95.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 96 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch96.getBytes(), patch96.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 97 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch97.getBytes(), patch97.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 98 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch98.getBytes(), patch98.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 99 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch99.getBytes(), patch99.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 100 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch100.getBytes(), patch100.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 101 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch101.getBytes(), patch101.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 102 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch102.getBytes(), patch102.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 103 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch103.getBytes(), patch103.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 104 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch104.getBytes(), patch104.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 105 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch105.getBytes(), patch105.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 106 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch106.getBytes(), patch106.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 107 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch107.getBytes(), patch107.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 108 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch108.getBytes(), patch108.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 109 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch109.getBytes(), patch109.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 110 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch110.getBytes(), patch110.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 111 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch111.getBytes(), patch111.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 112 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch112.getBytes(), patch112.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 113 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch113.getBytes(), patch113.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 114 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch114.getBytes(), patch114.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 115 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch115.getBytes(), patch115.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 116 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch116.getBytes(), patch116.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 117 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch117.getBytes(), patch117.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 118 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch118.getBytes(), patch118.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 119 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch119.getBytes(), patch119.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 120 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch120.getBytes(), patch120.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 121 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch121.getBytes(), patch121.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 122 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch122.getBytes(), patch122.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 123 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch123.getBytes(), patch123.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 124 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch124.getBytes(), patch124.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 125 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch125.getBytes(), patch125.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 126 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch126.getBytes(), patch126.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}

						if (program == 127 & bankLSB == 0) {
							metaMessage.setMessage(trackName, patch127.getBytes(), patch127.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (program == 127 & bankLSB == 1) {
							metaMessage.setMessage(trackName, patch255.getBytes(), patch255.length());
							MidiEvent metaEvent = new MidiEvent(metaMessage, (long) 0);
							track.add(metaEvent);
						}
						
						if (channelPosition != sm.getChannel()) {
							break;
						}
						
						else if (channelPosition == sm.getChannel()) {
							continue;
						}
					}
					
					if (sm.getCommand() == ShortMessage.NOTE_ON & channelPosition == sm.getChannel()) {

						//Bank 1
						if (customBank1 == 2) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), 57, sm.getData2());
						}
						
						if (customBank1 == 51) {
							break;
							//TODO: Add range of event handlers
						}
						
						if (customBank1 == 52) {
							break;
							//TODO: Add range of event handlers
						}
						
						//Bank 2
						if (customBank2 == 1) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() + 12, sm.getData2());
						}
						
						if (customBank2 == 5) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() - 12, sm.getData2());
						}

						if (customBank2 == 23) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() + 24, sm.getData2());
						}

						if (customBank2 == 24) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() + 24, sm.getData2());
						}
						
						continue;
					}

					if (sm.getCommand() == ShortMessage.NOTE_OFF & channelPosition == sm.getChannel()) {

						//Bank 1
						if (customBank1 == 2) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), 57, sm.getData2());
						}
						
						if (customBank1 == 51) {
							break;
							//TODO: Add range of event handlers
						}
						
						if (customBank1 == 52) {
							break;
							//TODO: Add range of event handlers
						}
						
						//Bank 2
						if (customBank2 == 1) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() + 12, sm.getData2());
						}
						
						if (customBank2 == 5) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() - 12, sm.getData2());
						}

						if (customBank2 == 23) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() + 24, sm.getData2());
						}

						if (customBank2 == 24) {
							sm.setMessage(sm.getCommand(), sm.getChannel(), sm.getData1() + 24, sm.getData2());
						}
						continue;
					}
				}
			}
		}
		if (write == true) {
			MidiSystem.write(sequence, 1, new File("./FixedMIDI.mid/"));
		}
		return sequence;
	}
}
