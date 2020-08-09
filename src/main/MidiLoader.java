package main;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidiLoader {

    Sequence sequence;
    Sequencer[] sequencers;
    Synthesizer[] synthesizers;

    int trackCount;

    public MidiLoader(Soundbank soundbank, int tracks) throws MidiUnavailableException {

        trackCount = tracks;
        sequencers = new Sequencer[trackCount];

        for (int index = 0; index < sequencers.length; index++) {
            sequencers[index] = MidiSystem.getSequencer(false);
            sequencers[index].open();
        }

        synthesizers = new Synthesizer[trackCount];

        for (int index = 0; index < synthesizers.length; index++) {
            synthesizers[index] = MidiSystem.getSynthesizer();
            synthesizers[index].open();
            synthesizers[index].unloadAllInstruments(synthesizers[index].getDefaultSoundbank());
            synthesizers[index].loadAllInstruments(soundbank);

            sequencers[index].getTransmitter().setReceiver(synthesizers[index].getReceiver());
        }
    }

    public void load(File midi) throws InvalidMidiDataException, IOException {

        sequence = MidiSystem.getSequence(midi);
        trackCount = sequence.getTracks().length;

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].setSequence(sequence);
            sequencers[track].setTrackSolo(track, true);
            sequencers[track].start();
        }
    }
}
