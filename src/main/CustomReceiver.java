package main;

import javax.sound.midi.*;
import java.io.*;

public class CustomReceiver implements Receiver {

    public Sequence sequence;
    public byte[] midiData;
    public ByteArrayOutputStream byteArrayOutputStream;
    public DataOutputStream dataOutputStream;
    public MidiPcmStream midiStream;

    public CustomReceiver(MidiPcmStream midiPcmStream) {
        midiStream = midiPcmStream;
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {

        if (message != null) {
            if (message instanceof ShortMessage) {
                ShortMessage shortMessage = (ShortMessage) message;
                int command = shortMessage.getCommand();
                int channel = shortMessage.getChannel();
                int data1 = shortMessage.getData1();
                int data2 = shortMessage.getData2();

                System.out.println();
                System.out.println("Message Received:");
                System.out.println("Command: " + command);
                System.out.println("Channel: " + channel);
                System.out.println("Data 1: " + data1);
                System.out.println("Data 2: " + data2);
                System.out.println();

                //128 = Note Off
                if (command == 128) {
                    midiStream.setNoteOff(channel, data1);

                    //144 = Note On
                } else if (command == 144) {
                    if (data2 > 0) {
                        midiStream.setNoteOn(channel, data1, data2);
                    } else {
                        midiStream.setNoteOff(channel, data1);
                    }

                    //160 = Polyphonic Aftertouch
                } else if (command == 160) {
                    midiStream.setPolyphonicAftertouch(channel, data1, data2);

                    // 176 = Control Change
                } else if (command == 176) {
                    if (data1 == 0) {
                        midiStream.bankSelect[channel] = (data2 << 14) + (midiStream.bankSelect[channel] & -2080769);
                    }

                    if (data1 == 32) {
                        midiStream.bankSelect[channel] = (data2 << 7) + (midiStream.bankSelect[channel] & -16257);
                    }

                    if (data1 == 1) {
                        midiStream.modulation[channel] = (data2 << 7) + (midiStream.modulation[channel] & -16257);
                    }

                    if (data1 == 33) {
                        midiStream.modulation[channel] = data2 + (midiStream.modulation[channel] & -128);
                    }

                    if (data1 == 5) {
                        midiStream.portamento[channel] = (data2 << 7) + (midiStream.portamento[channel] & -16257);
                    }

                    if (data1 == 37) {
                        midiStream.portamento[channel] = data2 + (midiStream.portamento[channel] & -128);
                    }

                    if (data1 == 7) {
                        midiStream.volume[channel] = (data2 << 7) + (midiStream.volume[channel] & -16257);
                    }

                    if (data1 == 39) {
                        midiStream.volume[channel] = data2 + (midiStream.volume[channel] & -128);
                    }

                    if (data1 == 10) {
                        midiStream.pan[channel] = (data2 << 7) + (midiStream.pan[channel] & -16257);
                    }

                    if (data1 == 42) {
                        midiStream.pan[channel] = data2 + (midiStream.pan[channel] & -128);
                    }

                    if (data1 == 11) {
                        midiStream.expression[channel] = (data2 << 7) + (midiStream.expression[channel] & -16257);
                    }

                    if (data1 == 43) {
                        midiStream.expression[channel] = data2 + (midiStream.expression[channel] & -128);
                    }

                    int[] switches;
                    if (data1 == 64) {
                        if (data2 >= 64) {
                            switches = midiStream.sustain;
                            switches[channel] |= 1;
                        } else {
                            switches = midiStream.sustain;
                            switches[channel] &= -2;
                        }
                    }

                    if (data1 == 65) {
                        if (data2 >= 64) {
                            switches = midiStream.sustain;
                            switches[channel] |= 2;
                        } else {
                            midiStream.resetSustainPedal(channel);
                            switches = midiStream.sustain;
                            switches[channel] &= -3;
                        }
                    }

                    if (data1 == 99) {
                        midiStream.nonRegisteredParameter[channel] = (data2 << 7) + (midiStream.nonRegisteredParameter[channel] & 127);
                    }

                    if (data1 == 98) {
                        midiStream.nonRegisteredParameter[channel] = (midiStream.nonRegisteredParameter[channel] & 16256) + data2;
                    }

                    if (data1 == 101) {
                        midiStream.nonRegisteredParameter[channel] = (data2 << 7) + (midiStream.nonRegisteredParameter[channel] & 127) + 16384;
                    }

                    if (data1 == 100) {
                        midiStream.nonRegisteredParameter[channel] = (midiStream.nonRegisteredParameter[channel] & 16256) + data2 + 16384;
                    }

                    if (data1 == 120) {
                        midiStream.turnAllSoundOff(channel);
                    }

                    if (data1 == 121) {
                        midiStream.resetAllControllers(channel);
                    }

                    if (data1 == 123) {
                        midiStream.turnAllNotesOff(channel);
                    }

                    int var6;
                    if (data1 == 6) {
                        var6 = midiStream.nonRegisteredParameter[channel];
                        if (var6 == 16384) {
                            midiStream.dataEntry[channel] = (data2 << 7) + (midiStream.dataEntry[channel] & -16257);
                        }
                    }

                    if (data1 == 38) {
                        var6 = midiStream.nonRegisteredParameter[channel];
                        if (var6 == 16384) {
                            midiStream.dataEntry[channel] = data2 + (midiStream.dataEntry[channel] & -128);
                        }
                    }

                    if (data1 == 16) {
                        midiStream.generalPurpose1[channel] = (data2 << 7) + (midiStream.generalPurpose1[channel] & -16257);
                    }

                    if (data1 == 48) {
                        midiStream.generalPurpose1[channel] = data2 + (midiStream.generalPurpose1[channel] & -128);
                    }

                    if (data1 == 81) {
                        if (data2 >= 64) {
                            switches = midiStream.sustain;
                            switches[channel] |= 4;
                        } else {
                            midiStream.resetEffectSwitch(channel);
                            switches = midiStream.sustain;
                            switches[channel] &= -5;
                        }
                    }

                    if (data1 == 17) {
                        midiStream.method3899(channel, (data2 << 7) + (midiStream.generalPurpose2[channel] & -16257));
                    }

                    if (data1 == 49) {
                        midiStream.method3899(channel, data2 + (midiStream.generalPurpose2[channel] & -128));
                    }

                    //192 = Program Change
                } else if (command == 192) {
                    midiStream.setProgramChange(channel, data1 + midiStream.bankSelect[channel]);

                    //208 = Aftertouch
                } else if (command == 208) {
                    midiStream.setAftertouch(channel, data1);

                    //224 = Pitch Bend
                } else if (command == 224) {
                    midiStream.setPitchBend(channel, data1);

                } else {
                    if (command == 255) {
                        //midiStream.sendSystemResetMessage();
                    }
                }
            }
            midiData = message.getMessage();
        }
    }

    @Override
    public void close() {

    }
}
