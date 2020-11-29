package main;

import javax.sound.midi.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CustomReceiver implements Receiver {

    public byte[] midiData;

    public CustomReceiver() {

    }

    @Override
    public void send(MidiMessage message, long timeStamp) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (message != null) {
            try {
                Sequence sequence = new Sequence(Sequence.PPQ, 960);
                Track track = sequence.createTrack();
                track.add(new MidiEvent(message, timeStamp));
                MidiSystem.write(sequence, 1, byteArrayOutputStream);
                System.out.println(new MidiEvent(message, timeStamp).getMessage());

            } catch (InvalidMidiDataException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close() {

    }
}
