package main;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.concurrent.LinkedBlockingDeque;

public class MidiFileReceiver implements Receiver {

    LinkedBlockingDeque<MidiMessage> midiMessageQueue;

    public MidiFileReceiver() {
        midiMessageQueue = new LinkedBlockingDeque<>();
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        midiMessageQueue.add(message);
    }

    @Override
    public void close() {
        midiMessageQueue = null;
    }
}
