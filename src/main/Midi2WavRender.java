package main;

/*
 * Copyright (c) 2007 by Karl Helgason
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import com.sun.media.sound.AudioSynthesizer;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Midi2WavRender {

	/*
	 * Render sequence using selected or default soundbank into wave audio file.
	 */
	public static void render(Soundbank soundbank, Sequence sequence,
							  File audio_file) {
		try {
			// Find available AudioSynthesizer.
			AudioSynthesizer synth = findAudioSynthesizer();
			if (synth == null) {
				System.out.println("No Audio Synthesizer was found!");
				System.exit(1);
			}

			// Open AudioStream from AudioSynthesizer.
			Map<String,Object> info = new HashMap<String,Object>();
			AudioFormat audioFormat = new AudioFormat(44100, 32, 2, true, false);
			info.put("resamplerType", "sinc");
			info.put("max polyphony", "8192");
			info.put("chorus", "false");
			info.put("reverb", "false");
			info.put("auto gain control", "false");
			info.put("format", audioFormat);
			info.put("large mode", "true");
			info.put("light reverb", "true");
			AudioInputStream stream = synth.openStream(audioFormat, info);

			// Load user-selected Soundbank into AudioSynthesizer.
			if (soundbank != null) {
				Soundbank defsbk = synth.getDefaultSoundbank();
				if (defsbk != null)
					synth.unloadAllInstruments(defsbk);
				synth.loadAllInstruments(soundbank);
			}

			// Play Sequence into AudioSynthesizer Receiver.
			double total = send(sequence, synth.getReceiver());

			// Calculate how long the WAVE file needs to be.
			long len = (long) (stream.getFormat().getFrameRate() * (total + 4));
			stream = new AudioInputStream(stream, stream.getFormat(), len);

			// Write WAVE file to disk.
			AudioSystem.write(stream, AudioFileFormat.Type.WAVE, audio_file);

			// We are finished, close synthesizer.
			synth.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Render sequence using selected or default soundbank into wave audio file.
	 */
	public static byte[] render(Soundbank soundbank, Sequence sequence,
							  byte[] audioBytes) {
		try {
			// Find available AudioSynthesizer.
			AudioSynthesizer synth = findAudioSynthesizer();
			if (synth == null) {
				System.out.println("No Audio Synthesizer was found!");
				System.exit(1);
			}

			// Open AudioStream from AudioSynthesizer.
			Map<String,Object> info = new HashMap<String,Object>();
			AudioFormat audioFormat = new AudioFormat(44100, 32, 2, true, false);
			info.put("resamplerType", "sinc");
			info.put("max polyphony", "8192");
			info.put("chorus", "false");
			info.put("reverb", "false");
			info.put("auto gain control", "false");
			info.put("format", audioFormat);
			info.put("large mode", "true");
			info.put("light reverb", "true");
			AudioInputStream stream = synth.openStream(audioFormat, info);

			// Load user-selected Soundbank into AudioSynthesizer.
			if (soundbank != null) {
				Soundbank defsbk = synth.getDefaultSoundbank();
				if (defsbk != null)
					synth.unloadAllInstruments(defsbk);
				synth.loadAllInstruments(soundbank);
			}

			// Play Sequence into AudioSynthesizer Receiver.
			double total = send(sequence, synth.getReceiver());

			// Calculate how long the WAVE file needs to be.
			long len = (long) (stream.getFormat().getFrameRate() * (total + 4));
			stream = new AudioInputStream(stream, stream.getFormat(), len);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			// Write WAVE file to disk.
			AudioSystem.write(stream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);

			// We are finished, close synthesizer.
			synth.close();

			audioBytes = byteArrayOutputStream.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return audioBytes;
	}

	/*
	 * Render sequence using selected or default soundbank into wave audio file.
	 */
	public static void renderSoloToFile(Soundbank soundbank, Sequence sequence, int track,
										File audio_file) {
		try {
			// Find available AudioSynthesizer.
			AudioSynthesizer synth = findAudioSynthesizer();
			if (synth == null) {
				System.out.println("No Audio Synthesizer was found!");
			}

			// Open AudioStream from AudioSynthesizer.
			Map<String,Object> info = new HashMap<String,Object>();
			AudioFormat audioFormat = new AudioFormat(44100, 32, 2, true, false);
			info.put("resamplerType", "sinc");
			info.put("max polyphony", "512");
			info.put("chorus", "false");
			info.put("reverb", "false");
			info.put("auto gain control", "false");
			info.put("format", audioFormat);
			info.put("large mode", "true");
			info.put("light reverb", "true");
			assert synth != null;
			AudioInputStream stream = synth.openStream(audioFormat, info);

			// Load user-selected Soundbank into AudioSynthesizer.
			if (soundbank != null) {
				Soundbank defsbk = synth.getDefaultSoundbank();
				if (defsbk != null)
					synth.unloadAllInstruments(defsbk);
				synth.loadAllInstruments(soundbank);
			}

			synth.getChannels()[track].setSolo(true);

			// Play Sequence into AudioSynthesizer Receiver.
			double total = send(sequence, synth.getReceiver());

			// Calculate how long the WAVE file needs to be.
			long len = (long) (stream.getFormat().getFrameRate() * (total + 4));
			stream = new AudioInputStream(stream, stream.getFormat(), len);

			// Write WAVE file to disk.
			AudioSystem.write(stream, AudioFileFormat.Type.WAVE, audio_file);

			// We are finished, close synthesizer.
			synth.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Render sequence using selected or default soundbank into an audio stream.
	 */
	public static ByteArrayOutputStream renderSoloToStream16Bit(Soundbank soundbank, Sequence sequence, int track) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try {
			// Find available AudioSynthesizer.
			AudioSynthesizer synth = findAudioSynthesizer();
			if (synth == null) {
				System.out.println("No Audio Synthesizer was found!");
			}

			// Open AudioStream from AudioSynthesizer.
			Map<String,Object> info = new HashMap<String,Object>();
			info.put("resamplerType", "sinc");
			info.put("max polyphony", "256");
			info.put("reverb", "false");
			info.put("light reverb", "true");
			AudioInputStream stream = null;
			if (synth != null) {
				stream = synth.openStream(new AudioFormat(44100, 16, 2, true, false), info);
			}

			// Load user-selected Soundbank into AudioSynthesizer.
			if (soundbank != null) {
				Soundbank defsbk = null;
				if (synth != null) {
					defsbk = synth.getDefaultSoundbank();
				}
				if (defsbk != null)
					synth.unloadAllInstruments(defsbk);
				if (synth != null) {
					synth.loadAllInstruments(soundbank);
				}
			}

			if (synth != null) {
				synth.getChannels()[track].setSolo(true);
			}

			// Play Sequence into AudioSynthesizer Receiver.
			double total = 0;
			if (synth != null) {
				total = send(sequence, synth.getReceiver());
			}

			// Calculate how long the WAVE file needs to be.
			long len = 0;
			if (stream != null) {
				len = (long) (stream.getFormat().getFrameRate() * (total + 4));
			}
			if (stream != null) {
				stream = new AudioInputStream(stream, stream.getFormat(), len);
			}

			// Write WAVE file to disk.
			if (stream != null) {
				AudioSystem.write(stream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
			}

			// We are finished, close synthesizer.
			if (synth != null) {
				synth.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream;
	}

	/*
	 * Render sequence using selected or default soundbank into an audio stream.
	 */
	public static ByteArrayOutputStream renderSoloToStream32Bit(Soundbank soundbank, Sequence sequence, int track) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		ArrayList<Patch> patchList = new ArrayList<>();
		Track currentTrack = sequence.getTracks()[track];
		for (int index = 0; index < currentTrack.size(); index++) {
			MidiEvent midiEvent = currentTrack.get(index);
			MidiMessage midiMessage = midiEvent.getMessage();
			if (midiMessage instanceof ShortMessage) {
				ShortMessage shortMessage = (ShortMessage) midiMessage;
				if (shortMessage.getCommand() == ShortMessage.PROGRAM_CHANGE) {
					patchList.add(new Patch(0, shortMessage.getData1()));
				}
			}
		}

		try {
			// Find available AudioSynthesizer.
			AudioSynthesizer synth = findAudioSynthesizer();
			if (synth == null) {
				System.out.println("No Audio Synthesizer was found!");
			}

			// Open AudioStream from AudioSynthesizer.
			Map<String,Object> info = new HashMap<String,Object>();
			info.put("resamplerType", "sinc");
			info.put("max polyphony", "256");
			info.put("reverb", "false");
			info.put("light reverb", "true");
			AudioInputStream stream = null;
			if (synth != null) {
				stream = synth.openStream(new AudioFormat(44100, 32, 2, true, false), info);
			}

			// Load user-selected Soundbank into AudioSynthesizer.
			if (soundbank != null) {
				Soundbank defsbk = null;
				if (synth != null) {
					defsbk = synth.getDefaultSoundbank();
				}
				if (defsbk != null)
					synth.unloadAllInstruments(defsbk);
				if (synth != null) {
					synth.loadAllInstruments(soundbank);
				}
			}

			if (synth != null) {
				synth.getChannels()[track].setSolo(true);
			}

			// Play Sequence into AudioSynthesizer Receiver.
			double total = 0;
			if (synth != null) {
				total = send(sequence, synth.getReceiver());
			}

			// Calculate how long the WAVE file needs to be.
			long len = 0;
			if (stream != null) {
				len = (long) (stream.getFormat().getFrameRate() * (total + 4));
			}
			if (stream != null) {
				stream = new AudioInputStream(stream, stream.getFormat(), len);
			}

			// Write WAVE file to disk.
			if (stream != null) {
				AudioSystem.write(stream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
			}

			// We are finished, close synthesizer.
			if (synth != null) {
				synth.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return byteArrayOutputStream;
	}

	/*
	 * Find available AudioSynthesizer.
	 */
	public static AudioSynthesizer findAudioSynthesizer()
			throws MidiUnavailableException {
		// First check if default synthesizer is AudioSynthesizer.
		Synthesizer synth = MidiSystem.getSynthesizer();
		if (synth instanceof AudioSynthesizer)
			return (AudioSynthesizer) synth;

		// If default synthesizer is not AudioSynthesizer, check others.

		Info[] infos = MidiSystem.getMidiDeviceInfo();
		MidiChannel[] channels = synth.getChannels();

		for (int i = 0; i < channels.length; i++) {
			channels[i].controlChange(91, -1);
		}

		for (int i = 0; i < infos.length; i++) {
			MidiDevice dev = MidiSystem.getMidiDevice(infos[i]);

			if (dev instanceof AudioSynthesizer)
				return (AudioSynthesizer) dev;
		}

		// No AudioSynthesizer was found, return null.
		return null;
	}

	/*
	 * Send entry MIDI Sequence into Receiver using timestamps.
	 */
	public static double send(Sequence seq, Receiver recv) {
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

}