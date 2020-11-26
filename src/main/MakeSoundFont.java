package main;

import com.sun.media.sound.*;

import javax.sound.midi.Patch;
import java.io.IOException;

public class MakeSoundFont {

    MusicPatchNode musicPatchNode;
    SF2Sample sf2Sample;
    SF2Soundbank sf2Soundbank;
    SF2Instrument sf2Instrument;
    int instrumentIndex = 0;

    public void initSoundFont() {
        sf2Soundbank = new SF2Soundbank();
        sf2Soundbank.setName("Sample Bank");
        sf2Soundbank.setRomName("RuneScape MIDI Suite");
        sf2Soundbank.setRomVersionMajor(2);
        sf2Soundbank.setRomVersionMinor(0);
    }

    public void addSamplesToBank(MusicPatch musicPatch, SF2Sample sf2Sample, int patchID, byte[] noteRanges) {

        sf2Soundbank.addResource(sf2Sample);

        if (sf2Instrument == null) {
            sf2Instrument = new SF2Instrument();
            sf2Instrument.setName("Patch " + patchID);
            sf2Instrument.setPatch(new Patch(0, patchID));
            if (patchID <= 128) {
                int bank = 0;
                int program = patchID;
                while (program < 127) {
                    program = program - 128;
                    bank++;
                }
                sf2Instrument.setPatch(new Patch(bank, program));
            }
        }

        SF2Layer sf2Layer = new SF2Layer();
        sf2Layer.setName("Patch " + patchID);

        SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.setSample(sf2Sample);
        sf2LayerRegion.putBytes(SF2Region.GENERATOR_KEYRANGE, noteRanges);
        sf2LayerRegion.putInteger(SF2Region.GENERATOR_SAMPLEMODES, musicPatch.loopMode[noteRanges[0]] * -1);
        sf2LayerRegion.putShort(SF2Region.GENERATOR_RELEASEVOLENV, (short) 0);
        sf2LayerRegion.putShort(SF2Region.GENERATOR_INITIALATTENUATION, (short) lookUpAttenuation(musicPatch.baseVelocity + musicPatch.volumeOffset[noteRanges[0]]));
        sf2LayerRegion.putShort(SF2Region.GENERATOR_PAN, (short) ((musicPatch.panOffset[noteRanges[0]] - 64) * 16));
        sf2LayerRegion.putBytes(SF2Region.GENERATOR_ATTACKVOLENV, new byte[]{0, 64});
        sf2Layer.getRegions().add(sf2LayerRegion);

        SF2InstrumentRegion sf2InstrumentRegion = new SF2InstrumentRegion();
        sf2InstrumentRegion.setLayer(sf2Layer);

        sf2Soundbank.addResource(sf2Layer);

        sf2Instrument.getRegions().add(sf2InstrumentRegion);

        if (sf2Soundbank.getInstrument(sf2Instrument.getPatch()) == null) {
            sf2Soundbank.addInstrument(sf2Instrument);
        }
    }

    private double lookUpAttenuation(int value) {

        switch (value) {
            case 0:
                return 80.00 * 25;
            case 1:
                return 78.2 * 25;
            case 2:
                return 66.2 * 25;
            case 3:
                return 59.1 * 25;
            case 4:
                return 54.1 * 25;
            case 5:
                return 50.3 * 25;
            case 6:
                return 47.1 * 25;
            case 7:
                return 44.4 * 25;
            case 8:
                return 42.1 * 25;
            case 9:
                return 40.0 * 25;
            case 10:
                return 38.2 * 25;
            case 11:
                return 36.6 * 25;
            case 12:
                return 35.1 * 25;
            case 13:
                return 33.7 * 25;
            case 14:
                return 32.4 * 25;
            case 15:
                return 31.2 * 25;
            case 16:
                return 30.1 * 25;
            case 17:
                return 29.0 * 25;
            case 18:
                return 28.0 * 25;
            case 19:
                return 27.1 * 25;
            case 20:
                return 26.2 * 25;
            case 21:
                return 25.3 * 25;
            case 22:
                return 24.5 * 25;
            case 23:
                return 23.8 * 25;
            case 24:
                return 23.0 * 25;
            case 25:
                return 22.3 * 25;
            case 26:
                return 21.6 * 25;
            case 27:
                return 21.0 * 25;
            case 28:
                return 20.3 * 25;
            case 29:
                return 19.7 * 25;
            case 30:
                return 19.1 * 25;
            case 31:
                return 18.6 * 25;
            case 32:
                return 18.0 * 25;
            case 33:
                return 17.5 * 25;
            case 34:
                return 17.0 * 25;
            case 35:
                return 16.5 * 25;
            case 36:
                return 16.0 * 25;
            case 37:
                return 15.5 * 25;
            case 38:
                return 15.0 * 25;
            case 39:
                return 14.6 * 25;
            case 40:
                return 14.1 * 25;
            case 41:
                return 13.7 * 25;
            case 42:
                return 13.3 * 25;
            case 43:
                return 12.9 * 25;
            case 44:
                return 12.5 * 25;
            case 45:
                return 12.1 * 25;
            case 46:
                return 11.7 * 25;
            case 47:
                return 11.3 * 25;
            case 48:
                return 11.0 * 25;
            case 49:
                return 10.6 * 25;
            case 50:
                return 10.3 * 25;
            case 51:
                return 9.9 * 25;
            case 52:
                return 9.6 * 25;
            case 53:
                return 9.2 * 25;
            case 54:
                return 8.9 * 25;
            case 55:
                return 8.6 * 25;
            case 56:
                return 8.3 * 25;
            case 57:
                return 8.0 * 25;
            case 58:
                return 7.7 * 25;
            case 59:
                return 7.4 * 25;
            case 60:
                return 7.1 * 25;
            case 61:
                return 6.8 * 25;
            case 62:
                return 6.5 * 25;
            case 63:
                return 6.2 * 25;
            case 64:
                return 6.0 * 25;
            case 65:
                return 5.7 * 25;
            case 66:
                return 5.4 * 25;
            case 67:
                return 5.2 * 25;
            case 68:
                return 4.9 * 25;
            case 69:
                return 4.7 * 25;
            case 70:
                return 4.4 * 25;
            case 71:
                return 4.2 * 25;
            case 72:
                return 3.9 * 25;
            case 73:
                return 3.7 * 25;
            case 74:
                return 3.5 * 25;
            case 75:
                return 3.2 * 25;
            case 76:
                return 3.0 * 25;
            case 77:
                return 2.8 * 25;
            case 78:
                return 2.5 * 25;
            case 79:
                return 2.3 * 25;
            case 80:
                return 2.1 * 25;
            case 81:
                return 1.9 * 25;
            case 82:
                return 1.7 * 25;
            case 83:
                return 1.5 * 25;
            case 84:
                return 1.2 * 25;
            case 85:
                return 1.0 * 25;
            case 86:
                return 0.8 * 25;
            case 87:
                return 0.6 * 25;
            case 88:
                return 0.4 * 25;
            case 89:
                return 0.2 * 25;
            case 90:
                return 0.0 * 25;
            case 91:
                return -0.1 * 25;
            case 92:
                return -0.3 * 25;
            case 93:
                return -0.5 * 25;
            case 94:
                return -0.7 * 25;
            case 95:
                return -0.9 * 25;
            case 96:
                return -1.1 * 25;
            case 97:
                return -1.3 * 25;
            case 98:
                return -1.4 * 25;
            case 99:
                return -1.6 * 25;
            case 100:
                return -1.8 * 25;
            case 101:
                return -2.0 * 25;
            case 102:
                return -2.1 * 25;
            case 103:
                return -2.3 * 25;
            case 104:
                return -2.5 * 25;
            case 105:
                return -2.6 * 25;
            case 106:
                return -2.8 * 25;
            case 107:
                return -3.0 * 25;
            case 108:
                return -3.1 * 25;
            case 109:
                return -3.3 * 25;
            case 110:
                return -3.4 * 25;
            case 111:
                return -3.6 * 25;
            case 112:
                return -3.7 * 25;
            case 113:
                return -3.9 * 25;
            case 114:
                return -4.1 * 25;
            case 115:
                return -4.2 * 25;
            case 116:
                return -4.4 * 25;
            case 117:
                return -4.5 * 25;
            case 118:
                return -4.7 * 25;
            case 119:
                return -4.8 * 25;
            case 120:
                return -4.9 * 25;
            case 121:
                return -5.1 * 25;
            case 122:
                return -5.2 * 25;
            case 123:
                return -5.4 * 25;
            case 124:
                return -5.5 * 25;
            case 125:
                return -5.7 * 25;
            case 126:
                return -5.8 * 25;
            case 127:
                return -6.0 * 25;
        }
        return 0.00;
    }

    public void addSamples(MusicPatch musicPatch, SoundBankCache soundBankCache, int archiveID) {

        byte[] noteRange = new byte[2];
        int nextNoteRange = 0;

        int var5 = 0;
        AudioBuffer audioBuffer;

        for (int noteIndex = 0; noteIndex < 128; ++noteIndex) {

            int var8 = musicPatch.sampleOffset[noteIndex];

            if(var8 != 0) {
                if(var8 != var5) {
                    var5 = var8--;
                    if((var8 & 1) == 0) {

                        audioBuffer = soundBankCache.getSoundEffect(var8 >> 2, null);

                        byte[] rawData = audioBuffer.samples;
                        byte[] audioData = new byte[rawData.length * 2];

                        for (int frame = 0; frame < rawData.length; frame++) {
                            audioData[frame * 2] = rawData[frame];
                            audioData[frame * 2 + 1] = (rawData[frame]);
                        }

                        int samplePitch = (musicPatch.pitchOffset[noteIndex] / 256) + 128;

                        while (samplePitch > 127) {
                            samplePitch = samplePitch - 128;
                        }

                        sf2Sample = new SF2Sample();
                        sf2Sample.setName(String.valueOf(((var8 >> 2))));
                        sf2Sample.setData(audioData);
                        sf2Sample.setOriginalPitch(samplePitch);
                        sf2Sample.setSampleRate(audioBuffer.sampleRate);
                        sf2Sample.setStartLoop(audioBuffer.start);
                        sf2Sample.setEndLoop(audioBuffer.end);
                        sf2Sample.setSampleType(1);
                        sf2Sample.setSampleLink(0);

                        for (int index = noteIndex; index < 128; index++) {
                            if (musicPatch.sampleOffset[index] == var5) {
                                nextNoteRange++;
                            }
                        }

                        noteRange[0] = (byte) noteIndex;
                        noteRange[1] = (byte) nextNoteRange;

                        addSamplesToBank(musicPatch, sf2Sample, archiveID, noteRange);

                        if (nextNoteRange >= 127) {
                            //sf2Soundbank.addInstrument(sf2Instrument);
                            sf2Instrument = null;
                        }

                    } else {

                        audioBuffer = soundBankCache.getMusicSample(var8 >> 2, null);

                        byte[] rawData = audioBuffer.samples;
                        byte[] audioData = new byte[rawData.length * 2];

                        for (int frame = 0; frame < rawData.length; frame++) {
                            audioData[frame * 2] = rawData[frame];
                            audioData[frame * 2 + 1] = (rawData[frame]);
                        }

                        int samplePitch = (musicPatch.pitchOffset[noteIndex] / 256) + 128;

                        while (samplePitch > 127) {
                            samplePitch = samplePitch - 128;
                        }

                        sf2Sample = new SF2Sample();
                        sf2Sample.setName(String.valueOf(((var8 >> 2))));
                        sf2Sample.setData(audioData);
                        sf2Sample.setOriginalPitch(samplePitch);
                        sf2Sample.setSampleRate(audioBuffer.sampleRate);
                        sf2Sample.setStartLoop(audioBuffer.start);
                        sf2Sample.setEndLoop(audioBuffer.end);
                        sf2Sample.setSampleType(1);
                        sf2Sample.setSampleLink(0);

                        for (int index = noteIndex; index < 128; index++) {
                            if (musicPatch.sampleOffset[index] == var5) {
                                nextNoteRange++;
                            }
                        }

                        noteRange[0] = (byte) noteIndex;
                        noteRange[1] = (byte) nextNoteRange;

                        addSamplesToBank(musicPatch, sf2Sample, archiveID, noteRange);

                        if (nextNoteRange >= 127) {
                            //sf2Soundbank.addInstrument(sf2Instrument);
                            sf2Instrument = null;
                        }
                    }
                }
            }
        }
    }

    public void saveSoundBank(int songID) {
        try {
            sf2Soundbank.save("./SoundFonts/" + songID + ".sf2/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeSoundFont(MusicPatch musicPatch, SoundBankCache soundBankCache, int archiveID) {
        addSamples(musicPatch, soundBankCache, archiveID);
    }
}
