package main;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

public class RSMidiGenerator {

    Sequence sequence;

    public RSMidiGenerator() throws InvalidMidiDataException, IOException {
        sequence = new Sequence(Sequence.PPQ, 960);
        sequence.createTrack().add(createSetTempoEvent(0, 80));

        addMultipleMidiEvents(MidiSystem.getSequence(new File("./MIDI Samples/0_1_1.mid/")));
        addMultipleMidiEvents(MidiSystem.getSequence(new File("./MIDI Samples/0_2_1.mid/")));

        MidiSystem.write(sequence, 1, new File("./Random.mid/"));
    }

    public static MidiEvent createSetTempoEvent(long tick, long tempo) {

        long tempoShift = 60000000 / tempo;

        MetaMessage metaMessage = new MetaMessage();

        byte[] array = new byte[] {0, 0, 0};

        for (int index = 0; index < 3; index++) {
            int shift = (3 - 1 - index) * 8;
            array[index] = (byte) (tempoShift >> shift);
        }

        try {
            metaMessage.setMessage(81, array, 3);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new MidiEvent(metaMessage, tick);
    }

    private void addMultipleMidiEvents(Sequence midiSample) throws InvalidMidiDataException {
        Track track = sequence.createTrack();
        for (int event = 0; event < midiSample.getTracks()[0].size(); event++) {
            MidiEvent midiEvent = midiSample.getTracks()[0].get(event);
            MidiMessage midiMessage = midiEvent.getMessage();
            if (midiMessage instanceof ShortMessage) {
                ShortMessage shortMessage = (ShortMessage) midiMessage;

                if (shortMessage.getCommand() == ShortMessage.NOTE_ON) {
                    shortMessage.setMessage(shortMessage.getCommand(), shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                }

                if (shortMessage.getCommand() == ShortMessage.NOTE_OFF) {
                    shortMessage.setMessage(shortMessage.getCommand(), shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                }

                MidiEvent trackEvent = new MidiEvent(shortMessage, midiEvent.getTick() * 2);
                track.add(trackEvent);
            }
        }
    }
}
