package utils;

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

import java.io.File;
import java.io.FileNotFoundException;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.sun.media.sound.AudioSynthesizer;

public class Midi2WavRender {

	public static void main(String[] args) {
		if (args.length >= 2)
			try {
				File midi_file = new File(args[0]);
				if (!midi_file.exists())
					throw new FileNotFoundException();
				Sequence sequence = MidiSystem.getSequence(midi_file);
				Soundbank soundbank = null;
				if (args.length >= 3) {
					File soundbank_file = new File(args[2]);
					if (soundbank_file.exists())
						soundbank = MidiSystem.getSoundbank(soundbank_file);
				}
				render(soundbank, sequence, new File(args[1]));
				System.exit(0);

			} catch (Exception e) {
				System.out.println(e.toString());
				System.out.println();
			}
		System.out.println("MIDI to WAVE Render: usages:");
		System.out
				.println("java Midi2WavRender <midi_file_in> <wave_file_out> <soundbank_file>");
		System.exit(1);
	}

	/*
	 * Render sequence using selected or default soundbank into wave audio file.
	 */
	public static void render(Soundbank soundbank, Sequence sequence,
			File audio_file) {
		try {
			// Find available AudioSynthesizer.
			AudioSynthesizer synth = findAudioSynthesizer();
			if (synth == null) {
				System.out.println("No AudioSynhtesizer was found!");
				System.exit(1);
			}

			// Open AudioStream from AudioSynthesizer.
			AudioInputStream stream = synth.openStream(null, null);

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
	 * Find available AudioSynthesizer.
	 */
	public static AudioSynthesizer findAudioSynthesizer()
			throws MidiUnavailableException {
		// First check if default synthesizer is AudioSynthesizer.
		Synthesizer synth = MidiSystem.getSynthesizer();
		if (synth instanceof AudioSynthesizer)
			return (AudioSynthesizer) synth;

		// If default synhtesizer is not AudioSynthesizer, check others.
		Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			MidiDevice dev = MidiSystem.getMidiDevice(infos[i]);
			if (dev instanceof AudioSynthesizer)
				return (AudioSynthesizer) dev;
		}

		// No AudioSynthesizer was found, return null.
		return null;
	}

	/*
	 * Send entiry MIDI Sequence into Receiver using timestamps.
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
