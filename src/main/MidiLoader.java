package main;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidiLoader {

    Sequence sequence;
    Sequencer[] sequencers;
    Synthesizer[] synthesizers;

    int trackCount;

    public MidiLoader() {

    }

    private Patch[] getPatchList() {

        Patch[] patches = null;
        int bankSelect = 0;

        for (Track track : sequence.getTracks()) {
            for (int index = 0; index < track.size(); index++) {
                MidiEvent midiEvent = track.get(index);
                MidiMessage midiMessage = midiEvent.getMessage();
                if (midiMessage instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) midiMessage;
                    if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
                        if (shortMessage.getData1() == 32) {
                            bankSelect = shortMessage.getData2();
                        }
                    }

                    if (shortMessage.getCommand() == ShortMessage.PROGRAM_CHANGE) {
                        Patch patch = new Patch(bankSelect, shortMessage.getData1());
                        patches = new Patch[]{patch};
                    }
                }
            }
        }
        return patches;
    }

    public void load(Soundbank soundbank, File midi) throws InvalidMidiDataException, IOException, MidiUnavailableException {

        sequence = MidiSystem.getSequence(midi);
        trackCount = sequence.getTracks().length;

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

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].setSequence(sequence);
            sequencers[track].setTrackSolo(track, true);
            sequencers[track].start();
        }
    }
}
