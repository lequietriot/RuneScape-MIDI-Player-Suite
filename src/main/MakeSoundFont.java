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

        int bankSelect = 0;
        int programChange = patchID;

        if (programChange > 127) {

            while (programChange > 127) {
                programChange = programChange - 128;
                bankSelect++;
            }
        }

        sf2Soundbank.addResource(sf2Sample);

        if (sf2Instrument == null) {
            sf2Instrument = new SF2Instrument();
            sf2Instrument.setName("Patch " + patchID);
            sf2Instrument.setPatch(new Patch(bankSelect, programChange));
        }

        SF2Layer sf2Layer = new SF2Layer();
        sf2Layer.setName("Patch " + patchID);

        SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.setSample(sf2Sample);
        sf2LayerRegion.putBytes(SF2Region.GENERATOR_KEYRANGE, noteRanges);
        sf2LayerRegion.putInteger(SF2Region.GENERATOR_SAMPLEMODES, musicPatch.loopMode[noteRanges[0]] * -1);
        sf2LayerRegion.putShort(SF2Region.GENERATOR_RELEASEVOLENV, (short) 0);
        sf2Layer.getRegions().add(sf2LayerRegion);

        SF2InstrumentRegion sf2InstrumentRegion = new SF2InstrumentRegion();
        sf2InstrumentRegion.setLayer(sf2Layer);

        sf2Soundbank.addResource(sf2Layer);

        sf2Instrument.getRegions().add(sf2InstrumentRegion);
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

                        sf2Sample = new SF2Sample();
                        sf2Sample.setName(String.valueOf(((var8 >> 2))));
                        sf2Sample.setData(audioData);
                        sf2Sample.setOriginalPitch((musicPatch.pitchOffset[noteIndex] / 256) + 128);
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
                            sf2Soundbank.addInstrument(sf2Instrument);
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

                        sf2Sample = new SF2Sample();
                        sf2Sample.setName(String.valueOf(((var8 >> 2))));
                        sf2Sample.setData(audioData);
                        sf2Sample.setOriginalPitch((musicPatch.pitchOffset[noteIndex] / 256) + 128);
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
                            sf2Soundbank.addInstrument(sf2Instrument);
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
