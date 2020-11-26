package main;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class MidiLoader {

    Sequence sequence;
    Sequencer sequencer;

    Sequencer[] sequencers;
    Synthesizer[] synthesizers;

    int trackCount;
    long pausedPosition;

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
    }

    public void play() throws InvalidMidiDataException {

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].setSequence(sequence);
            sequencers[track].setTrackSolo(track, true);
            sequencers[track].start();
        }
    }

    public void pause() {

        for (int track = 0; track < trackCount; track++) {
            pausedPosition = sequencers[track].getMicrosecondPosition();
            sequencers[track].stop();
        }
    }

    public void resume() throws InvalidMidiDataException {

        for (int track = 0; track < trackCount; track++) {

            sequencers[track].setSequence(sequence);

            if (pausedPosition != 0) {
                sequencers[track].setMicrosecondPosition(pausedPosition);
            }

            sequencers[track].setTrackSolo(track, true);
            sequencers[track].start();
        }
    }

    public void stop() {

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].stop();
        }
    }

    public boolean isSequencerRunning() {
        return sequencers[0].isRunning();
    }

    public long getSequencePosition() {
        return sequencers[0].getMicrosecondPosition();
    }

    public void setLoop(long loopStart, long loopEnd, int loopCount) throws InvalidMidiDataException {

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].setSequence(sequence);
            sequencers[track].setLoopStartPoint(loopStart);
            sequencers[track].setLoopEndPoint(loopEnd);
            sequencers[track].setLoopCount(loopCount);
        }
    }

    public void setMusicTrack(Sequence sequence, boolean loop) {

    }

    public void loadMusicTrack(Soundbank soundbank) {

    }
}
