package main;

import main.utils.ByteArrayNode;
import main.utils.ByteBufferUtils;
import main.utils.Node;
import main.utils.NodeHashTable;

import javax.sound.midi.*;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * MIDI File class from Jagex's custom MIDI Player Engine, refactored.
 * @author Rodolfo Ruiz-Velasco (https://github.com/lequietriot)
 * @author Vincent (MIDI Encoder, from Rune-Server)
 */
public class MidiTrack extends Node {

    private static final int NOTE_OFF = 0x80;
    private static final int NOTE_ON = 0x90;
    private static final int KEY_AFTER_TOUCH = 0xA0;
    private static final int CONTROL_CHANGE = 0xB0;
    private static final int PROGRAM_CHANGE = 0xC0;
    private static final int CHANNEL_AFTER_TOUCH = 0xD0;
    private static final int PITCH_WHEEL_CHANGE = 0xE0;

    private static final int END_OF_TRACK = 0x2F;
    private static final int SET_TEMPO = 0x51;

    public static byte[] midi;

    public static byte[] encoded;

    static NodeHashTable table;

    MidiTrack() {

    }

    /**
     * The class to decode RuneScape's custom encoded MIDI data.
     * @param buffer initialize the decoder with the buffer data.
     */
    MidiTrack(ByteBuffer buffer, boolean midiConvert) {
        if (midiConvert) {
            convertToMidi(buffer);
        }

        else {
            decode(buffer);
        }
    }


    /**
     * The method to decode RuneScape's custom encoded MIDI data.
     * @param buf The buffer storing encoded MIDI data
     */
    private static void decode(ByteBuffer buf) {

        buf.position(buf.limit() - 3);
        int tracks = buf.get() & 0xFF;
        int division = buf.getShort() & 0xFFFF;
        int length = 14 + tracks * 10;
        buf.position(0);
        int tempoCount = 0;
        int controlChangeCount = 0;
        int noteOnCount = 0;
        int noteOffCount = 0;
        int pitchBendCount = 0;
        int channelPressureCount = 0;
        int keyAftertouchCount = 0;
        int programChangeCount = 0;

        int track;
        int opcode;
        int eventCount;
        for (track = 0; track < tracks; ++track) {
            opcode = -1;

            while (true) {
                eventCount = buf.get() & 0xFF;
                if (eventCount != opcode) {
                    ++length;
                }

                opcode = eventCount & 15;
                if (eventCount == 7) {
                    break;
                }

                if (eventCount == 23) {
                    ++tempoCount;
                } else if (opcode == 0) {
                    ++noteOnCount;
                } else if (opcode == 1) {
                    ++noteOffCount;
                } else if (opcode == 2) {
                    ++controlChangeCount;
                } else if (opcode == 3) {
                    ++pitchBendCount;
                } else if (opcode == 4) {
                    ++channelPressureCount;
                } else if (opcode == 5) {
                    ++keyAftertouchCount;
                } else {
                    if (opcode != 6) {
                        throw new RuntimeException();
                    }

                    ++programChangeCount;
                }
            }
        }

        length += 5 * tempoCount;
        length += 2 * (noteOnCount + noteOffCount + controlChangeCount + pitchBendCount + keyAftertouchCount);
        length += channelPressureCount + programChangeCount;
        track = buf.position();
        opcode = tracks + tempoCount + controlChangeCount + noteOnCount + noteOffCount + pitchBendCount
                + channelPressureCount + keyAftertouchCount + programChangeCount;

        for (eventCount = 0; eventCount < opcode; ++eventCount) {
            ByteBufferUtils.getVarInt(buf);
        }

        length += buf.position() - track;
        eventCount = buf.position();
        int modulationMSBCount = 0;
        int modulationLSBCount = 0;
        int channelVolumeMSBCount = 0;
        int channelVolumeLSBCount = 0;
        int channelPanningMSBCount = 0;
        int channelPanningLSBCount = 0;
        int NRPNMSBCount = 0;
        int NRPNLSBCount = 0;
        int RPNMSBCount = 0;
        int RPNLSBCount = 0;
        int miscEventCount = 0;
        int toggleCount = 0;
        int controller = 0;

        int controllerCount;
        for (controllerCount = 0; controllerCount < controlChangeCount; ++controllerCount) {
            controller = controller + (buf.get() & 0xFF) & 127;
            if (controller != 0 && controller != 32) {
                if (controller == 1) {
                    ++modulationMSBCount;
                } else if (controller == 33) {
                    ++modulationLSBCount;
                } else if (controller == 7) {
                    ++channelVolumeMSBCount;
                } else if (controller == 39) {
                    ++channelVolumeLSBCount;
                } else if (controller == 10) {
                    ++channelPanningMSBCount;
                } else if (controller == 42) {
                    ++channelPanningLSBCount;
                } else if (controller == 99) {
                    ++NRPNMSBCount;
                } else if (controller == 98) {
                    ++NRPNLSBCount;
                } else if (controller == 101) {
                    ++RPNMSBCount;
                } else if (controller == 100) {
                    ++RPNLSBCount;
                } else if (controller != 64 && controller != 65 && controller != 120 && controller != 121 && controller != 123) {
                    ++toggleCount;
                } else {
                    ++miscEventCount;
                }
            } else {
                ++programChangeCount;
            }
        }

        controllerCount = 0;

        int miscEventOffset = buf.position();
        ByteBufferUtils.skip(buf, miscEventCount);

        int keyPressureOffset = buf.position();
        ByteBufferUtils.skip(buf, keyAftertouchCount);

        int channelPressureOffset = buf.position();
        ByteBufferUtils.skip(buf, channelPressureCount);

        int pitchBendOffset = buf.position();
        ByteBufferUtils.skip(buf, pitchBendCount);

        int modulationMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, modulationMSBCount);

        int channelVolumeMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, channelVolumeMSBCount);

        int channelPanningMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, channelPanningMSBCount);

        int pitchOffset = buf.position();
        ByteBufferUtils.skip(buf, noteOnCount + noteOffCount + keyAftertouchCount);

        int noteOnOffset = buf.position();
        ByteBufferUtils.skip(buf, noteOnCount);

        int toggleOffset = buf.position();
        ByteBufferUtils.skip(buf, toggleCount);

        int noteOffOffset = buf.position();
        ByteBufferUtils.skip(buf, noteOffCount);

        int modulationLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, modulationLSBCount);

        int channelVolumeLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, channelVolumeLSBCount);

        int channelPanningLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, channelPanningLSBCount);

        int programChangeOffset = buf.position();
        ByteBufferUtils.skip(buf, programChangeCount);

        int pitchBend2Offset = buf.position();
        ByteBufferUtils.skip(buf, pitchBendCount);

        int NRPNMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, NRPNMSBCount);

        int NRPNLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, NRPNLSBCount);

        int RPNMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, RPNMSBCount);

        int RPNLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, RPNLSBCount);

        int tempoOffset = buf.position();
        ByteBufferUtils.skip(buf, tempoCount * 3);

        ByteBuffer midiBuff = ByteBuffer.allocate(length + 1);

        midiBuff.putInt(1297377380);
        midiBuff.putInt(6);
        midiBuff.putShort((short) (tracks > 1 ? 1 : 0));
        midiBuff.putShort((short) tracks);
        midiBuff.putShort((short) division);

        buf.position(track);

        int messagePosition = 0;
        int pitchPosition = 0;
        int noteOnPosition = 0;
        int noteOffPosition = 0;
        int pitchBendPositions = 0;
        int channelPressurePosition = 0;
        int keyPressurePosition = 0;
        int[] controllerArray = new int[128];
        controller = 0;

        label: for (int trackIndex = 0; trackIndex < tracks; ++trackIndex) {
            midiBuff.putInt(1297379947);
            ByteBufferUtils.skip(midiBuff, 4);
            int currentPosition = midiBuff.position();
            int currentOffset = -1;

            while (true) {
                int varInt = ByteBufferUtils.getVarInt(buf);

                ByteBufferUtils.putVarInt(midiBuff, varInt);

                int controllerValue = buf.array()[controllerCount++] & 255;
                boolean messageExists = controllerValue != currentOffset;
                currentOffset = controllerValue & 15;
                if (controllerValue == 7) {
                    if (messageExists)
                    {
                        midiBuff.put((byte) 255);
                    }

                    midiBuff.put((byte) 47);
                    midiBuff.put((byte) 0);
                    ByteBufferUtils.putLengthFromMark(midiBuff, midiBuff.position() - currentPosition);
                    continue label;
                }

                if (controllerValue == 23) {
                    if (messageExists)
                    {
                        midiBuff.put((byte) 255);
                    }

                    midiBuff.put((byte) 81);
                    midiBuff.put((byte) 3);
                    midiBuff.put(buf.array()[tempoOffset++]);
                    midiBuff.put(buf.array()[tempoOffset++]);
                    midiBuff.put(buf.array()[tempoOffset++]);
                } else {
                    messagePosition ^= controllerValue >> 4;
                    if (currentOffset == 0) {
                        if (messageExists) {
                            midiBuff.put((byte) (144 + messagePosition));
                        }

                        pitchPosition += buf.array()[pitchOffset++];
                        noteOnPosition += buf.array()[noteOnOffset++];
                        midiBuff.put((byte) (pitchPosition & 127));
                        midiBuff.put((byte) (noteOnPosition & 127));
                    } else if (currentOffset == 1) {
                        if (messageExists) {
                            midiBuff.put((byte) (128 + messagePosition));
                        }

                        pitchPosition += buf.array()[pitchOffset++];
                        noteOffPosition += buf.array()[noteOffOffset++];
                        midiBuff.put((byte) (pitchPosition & 127));
                        midiBuff.put((byte) (noteOffPosition & 127));
                    } else if (currentOffset == 2) {
                        if (messageExists) {
                            midiBuff.put((byte) (176 + messagePosition));
                        }

                        controller = controller + buf.array()[eventCount++] & 127;
                        midiBuff.put((byte) controller);
                        byte controllerData;
                        if (controller != 0 && controller != 32) {
                            if (controller == 1) {
                                controllerData = buf.array()[modulationMSBOffset++];
                            } else if (controller == 33) {
                                controllerData = buf.array()[modulationLSBOffset++];
                            } else if (controller == 7) {
                                controllerData = buf.array()[channelVolumeMSBOffset++];
                            } else if (controller == 39) {
                                controllerData = buf.array()[channelVolumeLSBOffset++];
                            } else if (controller == 10) {
                                controllerData = buf.array()[channelPanningMSBOffset++];
                            } else if (controller == 42) {
                                controllerData = buf.array()[channelPanningLSBOffset++];
                            } else if (controller == 99) {
                                controllerData = buf.array()[NRPNMSBOffset++];
                            } else if (controller == 98) {
                                controllerData = buf.array()[NRPNLSBOffset++];
                            } else if (controller == 101) {
                                controllerData = buf.array()[RPNMSBOffset++];
                            } else if (controller == 100) {
                                controllerData = buf.array()[RPNLSBOffset++];
                            } else if (controller != 64 && controller != 65 && controller != 120 && controller != 121 && controller != 123) {
                                controllerData = buf.array()[toggleOffset++];
                            } else {
                                controllerData = buf.array()[miscEventOffset++];
                            }
                        } else {
                            controllerData = buf.array()[programChangeOffset++];
                        }

                        int controllerInfo = controllerData + controllerArray[controller];
                        controllerArray[controller] = controllerInfo;
                        midiBuff.put((byte) (controllerInfo & 127));
                    } else if (currentOffset == 3) {
                        if (messageExists) {
                            midiBuff.put((byte) (224 + messagePosition));
                        }

                        pitchBendPositions += buf.array()[pitchBend2Offset++];
                        pitchBendPositions += buf.array()[pitchBendOffset++] << 7;
                        midiBuff.put((byte) (pitchBendPositions & 127));
                        midiBuff.put((byte) (pitchBendPositions >> 7 & 127));
                    } else if (currentOffset == 4) {
                        if (messageExists) {
                            midiBuff.put((byte) (208 + messagePosition));
                        }

                        channelPressurePosition += buf.array()[channelPressureOffset++];
                        midiBuff.put((byte) (channelPressurePosition & 127));
                    } else if (currentOffset == 5) {
                        if (messageExists) {
                            midiBuff.put((byte) (160 + messagePosition));
                        }

                        pitchPosition += buf.array()[pitchOffset++];
                        keyPressurePosition += buf.array()[keyPressureOffset++];
                        midiBuff.put((byte) (pitchPosition & 127));
                        midiBuff.put((byte) (keyPressurePosition & 127));
                    } else {
                        if (currentOffset != 6) {
                            throw new RuntimeException();
                        }

                        if (messageExists) {
                            midiBuff.put((byte) (192 + messagePosition));
                        }

                        midiBuff.put(buf.array()[programChangeOffset++]);
                    }
                }
            }
        }

        midiBuff.flip();

        midi = midiBuff.array();
    }

    /**
     * The method to decode RuneScape's custom encoded MIDI data to a regular MIDI format.
     * @param buf The buffer storing encoded MIDI data
     */
    private static void convertToMidi(ByteBuffer buf) {

        buf.position(buf.limit() - 3);
        int tracks = buf.get() & 0xFF;
        int division = buf.getShort() & 0xFFFF;
        int length = 14 + tracks * 10;
        buf.position(0);
        int tempoCount = 0;
        int controlChangeCount = 0;
        int noteOnCount = 0;
        int noteOffCount = 0;
        int pitchBendCount = 0;
        int channelPressureCount = 0;
        int keyAftertouchCount = 0;
        int programChangeCount = 0;

        int track;
        int opcode;
        int eventCount;
        for (track = 0; track < tracks; ++track) {
            opcode = -1;

            while (true) {
                eventCount = buf.get() & 0xFF;
                if (eventCount != opcode) {
                    ++length;
                }

                opcode = eventCount & 15;
                if (eventCount == 7) {
                    break;
                }

                if (eventCount == 23) {
                    ++tempoCount;
                } else if (opcode == 0) {
                    ++noteOnCount;
                } else if (opcode == 1) {
                    ++noteOffCount;
                } else if (opcode == 2) {
                    ++controlChangeCount;
                } else if (opcode == 3) {
                    ++pitchBendCount;
                } else if (opcode == 4) {
                    ++channelPressureCount;
                } else if (opcode == 5) {
                    ++keyAftertouchCount;
                } else {
                    if (opcode != 6) {
                        throw new RuntimeException();
                    }

                    ++programChangeCount;
                }
            }
        }

        length += 5 * tempoCount;
        length += 2 * (noteOnCount + noteOffCount + controlChangeCount + pitchBendCount + keyAftertouchCount);
        length += channelPressureCount + programChangeCount;
        track = buf.position();
        opcode = tracks + tempoCount + controlChangeCount + noteOnCount + noteOffCount + pitchBendCount
                + channelPressureCount + keyAftertouchCount + programChangeCount;

        for (eventCount = 0; eventCount < opcode; ++eventCount) {
            ByteBufferUtils.getVarInt(buf);
        }

        length += buf.position() - track;
        eventCount = buf.position();
        int modulationMSBCount = 0;
        int modulationLSBCount = 0;
        int channelVolumeMSBCount = 0;
        int channelVolumeLSBCount = 0;
        int channelPanningMSBCount = 0;
        int channelPanningLSBCount = 0;
        int NRPNMSBCount = 0;
        int NRPNLSBCount = 0;
        int RPNMSBCount = 0;
        int RPNLSBCount = 0;
        int miscEventCount = 0;
        int toggleCount = 0;
        int controller = 0;

        int controllerCount;
        for (controllerCount = 0; controllerCount < controlChangeCount; ++controllerCount) {
            controller = controller + (buf.get() & 0xFF) & 127;
            if (controller != 0 && controller != 32) {
                if (controller == 1) {
                    ++modulationMSBCount;
                } else if (controller == 33) {
                    ++modulationLSBCount;
                } else if (controller == 7) {
                    ++channelVolumeMSBCount;
                } else if (controller == 39) {
                    ++channelVolumeLSBCount;
                } else if (controller == 10) {
                    ++channelPanningMSBCount;
                } else if (controller == 42) {
                    ++channelPanningLSBCount;
                } else if (controller == 99) {
                    ++NRPNMSBCount;
                } else if (controller == 98) {
                    ++NRPNLSBCount;
                } else if (controller == 101) {
                    ++RPNMSBCount;
                } else if (controller == 100) {
                    ++RPNLSBCount;
                } else if (controller != 64 && controller != 65 && controller != 120 && controller != 121 && controller != 123) {
                    ++toggleCount;
                } else {
                    ++miscEventCount;
                }
            } else {
                ++programChangeCount;
            }
        }

        controllerCount = 0;

        int miscEventOffset = buf.position();
        ByteBufferUtils.skip(buf, miscEventCount);

        int keyPressureOffset = buf.position();
        ByteBufferUtils.skip(buf, keyAftertouchCount);

        int channelPressureOffset = buf.position();
        ByteBufferUtils.skip(buf, channelPressureCount);

        int pitchBendOffset = buf.position();
        ByteBufferUtils.skip(buf, pitchBendCount);

        int modulationMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, modulationMSBCount);

        int channelVolumeMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, channelVolumeMSBCount);

        int channelPanningMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, channelPanningMSBCount);

        int pitchOffset = buf.position();
        ByteBufferUtils.skip(buf, noteOnCount + noteOffCount + keyAftertouchCount);

        int noteOnOffset = buf.position();
        ByteBufferUtils.skip(buf, noteOnCount);

        int toggleOffset = buf.position();
        ByteBufferUtils.skip(buf, toggleCount);

        int noteOffOffset = buf.position();
        ByteBufferUtils.skip(buf, noteOffCount);

        int modulationLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, modulationLSBCount);

        int channelVolumeLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, channelVolumeLSBCount);

        int channelPanningLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, channelPanningLSBCount);

        int programChangeOffset = buf.position();
        ByteBufferUtils.skip(buf, programChangeCount);

        int pitchBend2Offset = buf.position();
        ByteBufferUtils.skip(buf, pitchBendCount);

        int NRPNMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, NRPNMSBCount);

        int NRPNLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, NRPNLSBCount);

        int RPNMSBOffset = buf.position();
        ByteBufferUtils.skip(buf, RPNMSBCount);

        int RPNLSBOffset = buf.position();
        ByteBufferUtils.skip(buf, RPNLSBCount);

        int tempoOffset = buf.position();
        ByteBufferUtils.skip(buf, tempoCount * 3);

        ByteBuffer midiBuff = ByteBuffer.allocate(length + 1);

        midiBuff.putInt(1297377380);
        midiBuff.putInt(6);
        midiBuff.putShort((short) (tracks > 1 ? 1 : 0));
        midiBuff.putShort((short) tracks);
        midiBuff.putShort((short) division);

        buf.position(track);

        int messagePosition = 0;
        int pitchPosition = 0;
        int noteOnPosition = 0;
        int noteOffPosition = 0;
        int pitchBendPositions = 0;
        int channelPressurePosition = 0;
        int keyPressurePosition = 0;
        int[] controllerArray = new int[128];
        controller = 0;

        label: for (int trackIndex = 0; trackIndex < tracks; ++trackIndex) {
            midiBuff.putInt(1297379947);
            ByteBufferUtils.skip(midiBuff, 4);
            int currentPosition = midiBuff.position();
            int currentOffset = -1;

            while (true) {
                int varInt = ByteBufferUtils.getVarInt(buf);

                ByteBufferUtils.putVarInt(midiBuff, varInt);

                int controllerValue = buf.array()[controllerCount++] & 255;
                boolean messageExists = controllerValue != currentOffset;
                currentOffset = controllerValue & 15;
                if (controllerValue == 7) {
                    {
                        midiBuff.put((byte) 255);
                    }

                    midiBuff.put((byte) 47);
                    midiBuff.put((byte) 0);
                    ByteBufferUtils.putLengthFromMark(midiBuff, midiBuff.position() - currentPosition);
                    continue label;
                }

                if (controllerValue == 23) {
                    {
                        midiBuff.put((byte) 255);
                    }

                    midiBuff.put((byte) 81);
                    midiBuff.put((byte) 3);
                    midiBuff.put(buf.array()[tempoOffset++]);
                    midiBuff.put(buf.array()[tempoOffset++]);
                    midiBuff.put(buf.array()[tempoOffset++]);
                } else {
                    messagePosition ^= controllerValue >> 4;
                    if (currentOffset == 0) {
                        if (messageExists) {
                            midiBuff.put((byte) (144 + messagePosition));
                        }

                        pitchPosition += buf.array()[pitchOffset++];
                        noteOnPosition += buf.array()[noteOnOffset++];
                        midiBuff.put((byte) (pitchPosition & 127));
                        midiBuff.put((byte) (noteOnPosition & 127));
                    } else if (currentOffset == 1) {
                        if (messageExists) {
                            midiBuff.put((byte) (128 + messagePosition));
                        }

                        pitchPosition += buf.array()[pitchOffset++];
                        noteOffPosition += buf.array()[noteOffOffset++];
                        midiBuff.put((byte) (pitchPosition & 127));
                        midiBuff.put((byte) (noteOffPosition & 127));
                    } else if (currentOffset == 2) {
                        if (messageExists) {
                            midiBuff.put((byte) (176 + messagePosition));
                        }

                        controller = controller + buf.array()[eventCount++] & 127;
                        midiBuff.put((byte) controller);
                        byte controllerData;
                        if (controller != 0 && controller != 32) {
                            if (controller == 1) {
                                controllerData = buf.array()[modulationMSBOffset++];
                            } else if (controller == 33) {
                                controllerData = buf.array()[modulationLSBOffset++];
                            } else if (controller == 7) {
                                controllerData = buf.array()[channelVolumeMSBOffset++];
                            } else if (controller == 39) {
                                controllerData = buf.array()[channelVolumeLSBOffset++];
                            } else if (controller == 10) {
                                controllerData = buf.array()[channelPanningMSBOffset++];
                            } else if (controller == 42) {
                                controllerData = buf.array()[channelPanningLSBOffset++];
                            } else if (controller == 99) {
                                controllerData = buf.array()[NRPNMSBOffset++];
                            } else if (controller == 98) {
                                controllerData = buf.array()[NRPNLSBOffset++];
                            } else if (controller == 101) {
                                controllerData = buf.array()[RPNMSBOffset++];
                            } else if (controller == 100) {
                                controllerData = buf.array()[RPNLSBOffset++];
                            } else if (controller != 64 && controller != 65 && controller != 120 && controller != 121 && controller != 123) {
                                controllerData = buf.array()[toggleOffset++];
                            } else {
                                controllerData = buf.array()[miscEventOffset++];
                            }
                        } else {
                            controllerData = buf.array()[programChangeOffset++];
                        }

                        int controllerInfo = controllerData + controllerArray[controller];
                        controllerArray[controller] = controllerInfo;
                        midiBuff.put((byte) (controllerInfo & 127));
                    } else if (currentOffset == 3) {
                        if (messageExists) {
                            midiBuff.put((byte) (224 + messagePosition));
                        }

                        pitchBendPositions += buf.array()[pitchBend2Offset++];
                        pitchBendPositions += buf.array()[pitchBendOffset++] << 7;
                        midiBuff.put((byte) (pitchBendPositions & 127));
                        midiBuff.put((byte) (pitchBendPositions >> 7 & 127));
                    } else if (currentOffset == 4) {
                        if (messageExists) {
                            midiBuff.put((byte) (208 + messagePosition));
                        }

                        channelPressurePosition += buf.array()[channelPressureOffset++];
                        midiBuff.put((byte) (channelPressurePosition & 127));
                    } else if (currentOffset == 5) {
                        if (messageExists) {
                            midiBuff.put((byte) (160 + messagePosition));
                        }

                        pitchPosition += buf.array()[pitchOffset++];
                        keyPressurePosition += buf.array()[keyPressureOffset++];
                        midiBuff.put((byte) (pitchPosition & 127));
                        midiBuff.put((byte) (keyPressurePosition & 127));
                    } else {
                        if (currentOffset != 6) {
                            throw new RuntimeException();
                        }

                        if (messageExists) {
                            midiBuff.put((byte) (192 + messagePosition));
                        }

                        midiBuff.put(buf.array()[programChangeOffset++]);
                    }
                }
            }
        }

        midiBuff.flip();

        midi = midiBuff.array();
    }

    /**
     * Converts a MIDI file to the RuneScape format.
     *
     * NOTE: Jagex doesn't use the default soundbank, they have multiple soundbanks and their own instruments located in idx15 that use sound effects as their notes (idx4/14)
     * For this reason some midi files might sound different although most of their first soundbank matches the default soundbank instruments
     *
     * @author Vincent
     */
    static void encode(File midiFile) throws InvalidMidiDataException, IOException {

        Sequence sequence = MidiSystem.getSequence(midiFile);
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File("./data.dat/")));

        for (Track track : sequence.getTracks()) {
            int prevChannel = 0;
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    int ch = (sm.getChannel() ^ prevChannel) << 4;
                    switch(sm.getCommand()) {
                        case NOTE_OFF:
                            dos.write(1 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case NOTE_ON:
                            dos.write(ch);
                            prevChannel = sm.getChannel();
                            break;
                        case KEY_AFTER_TOUCH:
                            dos.write(5 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case CONTROL_CHANGE:
                            dos.write(2 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case PROGRAM_CHANGE:
                            dos.write(6 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case CHANNEL_AFTER_TOUCH:
                            dos.write(4 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case PITCH_WHEEL_CHANGE:
                            dos.write(3 | ch);
                            prevChannel = sm.getChannel();
                            break;
                    }
                } else if(message instanceof MetaMessage) {
                    MetaMessage mm = (MetaMessage) message;
                    switch(mm.getType()) {
                        case END_OF_TRACK:
                            dos.write(7);
                            break;
                        case SET_TEMPO:
                            dos.write(23);
                            break;
                        default:
                            //OTHER META EVENTS ARE IGNORED
                            break;
                    }

                }

            }

        }

        //write event timestamp for used opcodes
        for (Track track : sequence.getTracks()) {
            int lastTick = 0;
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    switch(sm.getCommand()) {
                        case NOTE_OFF:
                        case NOTE_ON:
                        case KEY_AFTER_TOUCH:
                        case CONTROL_CHANGE:
                        case PROGRAM_CHANGE:
                        case CHANNEL_AFTER_TOUCH:
                        case PITCH_WHEEL_CHANGE:
                            ByteBufferUtils.putVarIntDos(dos, (int)event.getTick() - lastTick);
                            lastTick = (int) event.getTick();
                            break;
                    }
                } else if(message instanceof MetaMessage) {
                    MetaMessage mm = (MetaMessage) message;
                    switch(mm.getType()) {
                        case END_OF_TRACK:
                        case SET_TEMPO:
                            ByteBufferUtils.putVarIntDos(dos, (int)event.getTick() - lastTick);
                            lastTick = (int) event.getTick();
                            break;
                    }
                }
            }
        }

        //jagex works with offset from the last one because this is usually 0 and gives better compression rates
        int lastController = 0;
        int lastNote = 0;
        int lastNoteOnVelocity = 0;
        int lastNoteOffVelocity = 0;
        int lastWheelChangeT = 0;
        int lastWheelChangeB = 0;
        int lastChannelAfterTouch = 0;
        int lastKeyAfterTouchVelocity = 0;

        //write controller number changes
        int[] lastControllerValue = new int[128];
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE) {
                        dos.write(sm.getData1() - lastController);
                        lastController = sm.getData1();
                    }
                }
            }
        }

        //controller 64 65 120 121 123 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && (sm.getData1() == 64 || sm.getData1() == 65 || sm.getData1() == 120 || sm.getData1() == 121 || sm.getData1() == 123)) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //key after touch velocity changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == KEY_AFTER_TOUCH) {
                        dos.write(sm.getData2() - lastKeyAfterTouchVelocity);
                        lastKeyAfterTouchVelocity = sm.getData2();
                    }
                }
            }
        }
        //channel after touch channel changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CHANNEL_AFTER_TOUCH) {
                        dos.write(sm.getData1() - lastChannelAfterTouch);
                        lastChannelAfterTouch = sm.getData1();
                    }
                }
            }
        }
        //pitch bend top values
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == PITCH_WHEEL_CHANGE) {
                        dos.write(sm.getData2() - lastWheelChangeT);
                        lastWheelChangeT = sm.getData2();
                    }
                }
            }
        }
        //controller 1 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 1) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 7 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 7) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 10 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 10) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //note changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == NOTE_OFF || sm.getCommand() == NOTE_ON || sm.getCommand() == KEY_AFTER_TOUCH) {
                        dos.write(sm.getData1() - lastNote);
                        lastNote = sm.getData1();
                    }
                }
            }
        }
        //note on velocity changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == NOTE_ON) {
                        dos.write(sm.getData2() - lastNoteOnVelocity);
                        lastNoteOnVelocity = sm.getData2();
                    }
                }
            }
        }
        //all unlisted controller changes (controllers are probably grouped like this because it gives an even better compression)
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && !(sm.getData1() == 64 || sm.getData1() == 65 || sm.getData1() == 120 || sm.getData1() == 121 || sm.getData1() == 123 || sm.getData1() == 0 || sm.getData1() == 32 || sm.getData1() == 1 || sm.getData1() == 33 || sm.getData1() == 7 || sm.getData1() == 39 || sm.getData1() == 10 || sm.getData1() == 42 || sm.getData1() == 99 || sm.getData1() == 98 || sm.getData1() == 101 || sm.getData1() == 100)) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //note off velocity changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == NOTE_OFF) {
                        dos.write(sm.getData2() - lastNoteOffVelocity);
                        lastNoteOffVelocity = sm.getData2();
                    }
                }
            }
        }
        //controller 33 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 33) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 39 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 39) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 42 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 42) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 0, 32 and program changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && (sm.getData1() == 0 || sm.getData1() == 32)) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    } else if(sm.getCommand() == PROGRAM_CHANGE) {
                        dos.write(sm.getData1());
                    }
                }
            }
        }
        //pitch bend bottom changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == PITCH_WHEEL_CHANGE) {
                        dos.write(sm.getData1() - lastWheelChangeB);
                        lastWheelChangeB = sm.getData1();
                    }
                }
            }
        }
        //controller 99 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 99) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 98 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 98) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 101 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 101) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 100 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 100) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //tempo changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof MetaMessage) {
                    MetaMessage mm = (MetaMessage) message;
                    if(mm.getType() == SET_TEMPO) {
                        dos.write(mm.getData());
                    }
                }
            }
        }
        //write footer
        dos.write(sequence.getTracks().length);
        dos.writeShort(sequence.getResolution());

        dos.flush();
        dos.close();

        Path path = Paths.get("./data.dat/");
        encoded = Files.readAllBytes(path);
    }

    public static File encode(Sequence sequence) throws IOException {

        File encodedMidiFile = new File("./data.dat/");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(encodedMidiFile));

        for (Track track : sequence.getTracks()) {
            int prevChannel = 0;
            for (int i = 0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    int ch = (sm.getChannel() ^ prevChannel) << 4;
                    switch(sm.getCommand()) {
                        case NOTE_OFF:
                            dos.write(1 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case NOTE_ON:
                            dos.write(ch);
                            prevChannel = sm.getChannel();
                            break;
                        case KEY_AFTER_TOUCH:
                            dos.write(5 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case CONTROL_CHANGE:
                            dos.write(2 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case PROGRAM_CHANGE:
                            dos.write(6 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case CHANNEL_AFTER_TOUCH:
                            dos.write(4 | ch);
                            prevChannel = sm.getChannel();
                            break;
                        case PITCH_WHEEL_CHANGE:
                            dos.write(3 | ch);
                            prevChannel = sm.getChannel();
                            break;
                    }
                } else if(message instanceof MetaMessage) {
                    MetaMessage mm = (MetaMessage) message;
                    switch(mm.getType()) {
                        case END_OF_TRACK:
                            dos.write(7);
                            break;
                        case SET_TEMPO:
                            dos.write(23);
                            break;
                        default:
                            //OTHER META EVENTS ARE IGNORED
                            break;
                    }

                }

            }

        }

        //write event timestamp for used opcodes
        for (Track track : sequence.getTracks()) {
            int lastTick = 0;
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    switch(sm.getCommand()) {
                        case NOTE_OFF:
                        case NOTE_ON:
                        case KEY_AFTER_TOUCH:
                        case CONTROL_CHANGE:
                        case PROGRAM_CHANGE:
                        case CHANNEL_AFTER_TOUCH:
                        case PITCH_WHEEL_CHANGE:
                            ByteBufferUtils.putVarIntDos(dos, (int)event.getTick() - lastTick);
                            lastTick = (int) event.getTick();
                            break;
                    }
                } else if(message instanceof MetaMessage) {
                    MetaMessage mm = (MetaMessage) message;
                    switch(mm.getType()) {
                        case END_OF_TRACK:
                        case SET_TEMPO:
                            ByteBufferUtils.putVarIntDos(dos, (int)event.getTick() - lastTick);
                            lastTick = (int) event.getTick();
                            break;
                    }
                }
            }
        }

        //jagex works with offset from the last one because this is usually 0 and gives better compression rates
        int lastController = 0;
        int lastNote = 0;
        int lastNoteOnVelocity = 0;
        int lastNoteOffVelocity = 0;
        int lastWheelChangeT = 0;
        int lastWheelChangeB = 0;
        int lastChannelAfterTouch = 0;
        int lastKeyAfterTouchVelocity = 0;

        //write controller number changes
        int[] lastControllerValue = new int[128];
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE) {
                        dos.write(sm.getData1() - lastController);
                        lastController = sm.getData1();
                    }
                }
            }
        }

        //controller 64 65 120 121 123 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && (sm.getData1() == 64 || sm.getData1() == 65 || sm.getData1() == 120 || sm.getData1() == 121 || sm.getData1() == 123)) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //key after touch velocity changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == KEY_AFTER_TOUCH) {
                        dos.write(sm.getData2() - lastKeyAfterTouchVelocity);
                        lastKeyAfterTouchVelocity = sm.getData2();
                    }
                }
            }
        }
        //channel after touch channel changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CHANNEL_AFTER_TOUCH) {
                        dos.write(sm.getData1() - lastChannelAfterTouch);
                        lastChannelAfterTouch = sm.getData1();
                    }
                }
            }
        }
        //pitch bend top values
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == PITCH_WHEEL_CHANGE) {
                        dos.write(sm.getData2() - lastWheelChangeT);
                        lastWheelChangeT = sm.getData2();
                    }
                }
            }
        }
        //controller 1 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 1) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 7 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 7) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 10 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 10) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //note changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == NOTE_OFF || sm.getCommand() == NOTE_ON || sm.getCommand() == KEY_AFTER_TOUCH) {
                        dos.write(sm.getData1() - lastNote);
                        lastNote = sm.getData1();
                    }
                }
            }
        }
        //note on velocity changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == NOTE_ON) {
                        dos.write(sm.getData2() - lastNoteOnVelocity);
                        lastNoteOnVelocity = sm.getData2();
                    }
                }
            }
        }
        //all unlisted controller changes (controllers are probably grouped like this because it gives an even better compression)
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && !(sm.getData1() == 64 || sm.getData1() == 65 || sm.getData1() == 120 || sm.getData1() == 121 || sm.getData1() == 123 || sm.getData1() == 0 || sm.getData1() == 32 || sm.getData1() == 1 || sm.getData1() == 33 || sm.getData1() == 7 || sm.getData1() == 39 || sm.getData1() == 10 || sm.getData1() == 42 || sm.getData1() == 99 || sm.getData1() == 98 || sm.getData1() == 101 || sm.getData1() == 100)) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //note off velocity changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == NOTE_OFF) {
                        dos.write(sm.getData2() - lastNoteOffVelocity);
                        lastNoteOffVelocity = sm.getData2();
                    }
                }
            }
        }
        //controller 33 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 33) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 39 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 39) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 42 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 42) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 0, 32 and program changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && (sm.getData1() == 0 || sm.getData1() == 32)) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    } else if(sm.getCommand() == PROGRAM_CHANGE) {
                        dos.write(sm.getData1());
                    }
                }
            }
        }
        //pitch bend bottom changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == PITCH_WHEEL_CHANGE) {
                        dos.write(sm.getData1() - lastWheelChangeB);
                        lastWheelChangeB = sm.getData1();
                    }
                }
            }
        }
        //controller 99 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 99) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 98 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 98) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 101 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 101) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //controller 100 changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    if(sm.getCommand() == CONTROL_CHANGE && sm.getData1() == 100) {
                        dos.write(sm.getData2() - lastControllerValue[sm.getData1()]);
                        lastControllerValue[sm.getData1()] = sm.getData2();
                    }
                }
            }
        }
        //tempo changes
        for (Track track : sequence.getTracks()) {
            for (int i=0; i < track.size(); i++) {
                MidiEvent event = track.get(i);
                MidiMessage message = event.getMessage();
                if (message instanceof MetaMessage) {
                    MetaMessage mm = (MetaMessage) message;
                    if(mm.getType() == SET_TEMPO) {
                        dos.write(mm.getData());
                    }
                }
            }
        }
        //write footer
        dos.write(sequence.getTracks().length);
        dos.writeShort(sequence.getResolution());

        dos.flush();
        dos.close();

        Path path = Paths.get("./data.dat/");
        encoded = Files.readAllBytes(path);

        return encodedMidiFile;
    }

    public static MidiTrack getMidiTrack(ByteBuffer buffer) {
        return new MidiTrack(buffer, true);
    }

    public static MidiTrack getMidiTrackData(ByteBuffer buffer) {
        return new MidiTrack(buffer, false);
    }

    /**
     * The method to return decoded MIDI data.
     * @return returns the decoded MIDI file.
     */
    byte[] getMidi() {
        return midi;
    }

    /**
     * The method to return encoded MIDI data.
     * @return returns the encoded MIDI file.
     */
    static byte[] getEncoded() {
        return encoded;
    }

    public static void loadMidiTrackInfo() {
        if (table == null) {
            table = new NodeHashTable(16);
            int[] var1 = new int[16];
            int[] var2 = new int[16];
            var2[9] = 128;
            var1[9] = 128;
            MidiFileReader var4 = new MidiFileReader(midi);
            int var5 = var4.trackCount();

            int var6;
            for (var6 = 0; var6 < var5; ++var6) {
                var4.gotoTrack(var6);
                var4.readTrackLength(var6);
                var4.markTrackPosition(var6);
            }

            label53:
            do {
                while (true) {
                    var6 = var4.getPrioritizedTrack();
                    int var7 = var4.trackLengths[var6];

                    while (var7 == var4.trackLengths[var6]) {
                        var4.gotoTrack(var6);
                        int var8 = var4.getMessage(var6);
                        if (var8 == 1) {
                            var4.setTrackDone();
                            var4.markTrackPosition(var6);
                            continue label53;
                        }

                        int var9 = var8 & 240;
                        int var10;
                        int var11;
                        int var12;
                        if (var9 == 176) {
                            var10 = var8 & 15;
                            var11 = var8 >> 8 & 127;
                            var12 = var8 >> 16 & 127;
                            if (var11 == 0) {
                                var1[var10] = (var12 << 14) + (var1[var10] & -2080769);
                            }

                            if (var11 == 32) {
                                var1[var10] = (var1[var10] & -16257) + (var12 << 7);
                            }
                        }

                        if (var9 == 192) {
                            var10 = var8 & 15;
                            var11 = var8 >> 8 & 127;
                            var2[var10] = var11 + var1[var10];
                        }

                        if (var9 == 144) {
                            var10 = var8 & 15;
                            var11 = var8 >> 8 & 127;
                            var12 = var8 >> 16 & 127;
                            if (var12 > 0) {
                                int var13 = var2[var10];
                                ByteArrayNode var14 = (ByteArrayNode) table.get(var13);
                                if (var14 == null) {
                                    var14 = new ByteArrayNode(new byte[128]);
                                    table.put(var14, var13);
                                }

                                var14.byteArray[var11] = 1;
                            }
                        }

                        var4.readTrackLength(var6);
                        var4.markTrackPosition(var6);
                    }
                }
            } while(!var4.isDone());

        }
    }

    static void clear() {
        table = null;
    }
}
