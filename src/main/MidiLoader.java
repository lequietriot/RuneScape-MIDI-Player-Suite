package main;

import com.sun.media.sound.AudioSynthesizer;

import javax.sound.midi.*;
import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MidiLoader {

    Sequence sequence;
    Sequencer sequencer;

    Sequencer[] sequencers;
    AudioSynthesizer[] audioSynthesizers;
    SourceDataLine[] sourceDataLines;

    ByteArrayOutputStream byteArrayOutputStream;

    int trackCount;
    long pausedPosition;

    public synchronized void load(Soundbank soundbank, File midi) throws InvalidMidiDataException, IOException, MidiUnavailableException, LineUnavailableException {

        sequence = MidiSystem.getSequence(midi);
        trackCount = sequence.getTracks().length;

        sequencers = new Sequencer[trackCount];
        audioSynthesizers = new AudioSynthesizer[trackCount];
        sourceDataLines = new SourceDataLine[trackCount];
        byteArrayOutputStream = new ByteArrayOutputStream();

        for (int index = 0; index < sequencers.length; index++) {
            sequencers[index] = MidiSystem.getSequencer(false);
            sequencers[index].open();
        }

        for (int track = 0; track < audioSynthesizers.length; track++) {

            audioSynthesizers[track] = getAudioSynthesizer();

            if (audioSynthesizers[track] == null) {
                System.out.println("No synth was found!");
            }

            Map<String, Object> info = new HashMap<>();
            info.put("resamplerType", "sinc");
            info.put("max polyphony", "256");
            info.put("chorus", "false");
            info.put("reverb", "false");
            info.put("auto gain control", "false");
            info.put("large mode", "false");
            info.put("light reverb", "false");

            AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
            sourceDataLines[track] = AudioSystem.getSourceDataLine(audioFormat);
            audioSynthesizers[track].open(sourceDataLines[track], info);
            sourceDataLines[track].start();

            /**
            for (int e = 0; e < sequence.getTracks()[track].size(); e++) {
                MidiEvent midiEvent = sequence.getTracks()[track].get(e);
                MidiMessage midiMessage = midiEvent.getMessage();
                if (midiMessage instanceof ShortMessage) {
                    ShortMessage shortMessage = (ShortMessage) midiMessage;

                    if (shortMessage.getCommand() == ShortMessage.CONTROL_CHANGE) {
                        int bankSelect = -1;
                        boolean usingBankSelect = false;
                        if (shortMessage.getData1() == 32) {
                            bankSelect = shortMessage.getData2();
                            shortMessage.setMessage(shortMessage.getCommand(), shortMessage.getChannel(), shortMessage.getData1(), 0);
                            ShortMessage bankMSBMessage = new ShortMessage(shortMessage.getCommand(), shortMessage.getChannel(), 0, bankSelect);
                            sequence.getTracks()[track].add(new MidiEvent(bankMSBMessage, 0));
                            sequence.getTracks()[track].add(new MidiEvent(bankMSBMessage, 3));
                            usingBankSelect = true;
                        }

                        if (shortMessage.getData1() == 0 && usingBankSelect) {
                            shortMessage.setMessage(shortMessage.getCommand(), shortMessage.getChannel(), shortMessage.getData1(), bankSelect);
                        }

                        else {
                            break;
                        }
                    }
                }
            }
             **/

            if (soundbank != null) {
                Soundbank defaultSoundbank = audioSynthesizers[track].getDefaultSoundbank();
                if (defaultSoundbank != null) {
                    audioSynthesizers[track].unloadAllInstruments(defaultSoundbank);
                    audioSynthesizers[track].loadAllInstruments(soundbank);
                }
            }

            MidiChannel[] channels = audioSynthesizers[track].getChannels();

            for (int i = 0; i < channels.length; i++) {
                channels[i].controlChange(91, -1);
            }

            sequencers[track].getTransmitter().setReceiver(audioSynthesizers[track].getReceiver());
        }
    }

    private synchronized AudioSynthesizer getAudioSynthesizer() throws MidiUnavailableException {

        Synthesizer synthesizer = MidiSystem.getSynthesizer();

        if (synthesizer instanceof AudioSynthesizer) {
            return (AudioSynthesizer) synthesizer;
        }

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for (int i = 0; i < infos.length; i++) {
            MidiDevice dev = MidiSystem.getMidiDevice(infos[i]);

            if (dev instanceof AudioSynthesizer)
                return (AudioSynthesizer) dev;
        }
        return null;
    }

    public synchronized void play() throws InvalidMidiDataException, IOException, InterruptedException {

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].setSequence(sequence);
            sequencers[track].setTrackSolo(track, true);
        }

        Thread.sleep(1);

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].start();
        }
    }

    public synchronized void pause() {

        for (int track = 0; track < trackCount; track++) {
            pausedPosition = sequencers[track].getMicrosecondPosition();
            sequencers[track].stop();
        }
    }

    public synchronized void resume() throws InvalidMidiDataException {

        for (int track = 0; track < trackCount; track++) {

            sequencers[track].setSequence(sequence);

            if (pausedPosition != 0) {
                sequencers[track].setMicrosecondPosition(pausedPosition);
            }

            sequencers[track].setTrackSolo(track, true);
            sequencers[track].start();
        }
    }

    public synchronized void stop() {

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].stop();
        }
    }

    public synchronized boolean isSequencerRunning() {
        return sequencers[0].isRunning();
    }

    public synchronized long getSequencePosition() {
        return sequencers[0].getMicrosecondPosition();
    }

    public synchronized void setLoop(long loopStart, long loopEnd, int loopCount) throws InvalidMidiDataException {

        for (int track = 0; track < trackCount; track++) {
            sequencers[track].setSequence(sequence);
            sequencers[track].setLoopStartPoint(loopStart);
            sequencers[track].setLoopEndPoint(loopEnd);
            sequencers[track].setLoopCount(loopCount);
        }
    }
}
