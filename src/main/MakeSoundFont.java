package main;

import com.sun.media.sound.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MakeSoundFont {

    void createSoundFont(MusicPatch musicPatch, SoundBankCache soundBankCache) {

        SF2Soundbank sf2Soundbank = new SF2Soundbank();
        musicPatch.loadPatchSamples(soundBankCache, null, null);

        int previousRootPitch = 0;

        for (int index = 0; index < 128; index++) {

            if (musicPatch.notePitches[index] == previousRootPitch) {
                continue;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            AudioFormat audioFormat = new AudioFormat(musicPatch.audioBuffers[index].sampleRate, 16, 1, true, false);
            byte[] eightBitSamples = musicPatch.audioBuffers[index].samples;
            byte[] newSamples = new byte[eightBitSamples.length * 2];

            for (int byteIndex = 0; byteIndex < newSamples.length; byteIndex++) {
                newSamples[byteIndex] = eightBitSamples[byteIndex / 2];
            }
            AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(newSamples), audioFormat, newSamples.length);
            try {
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            SF2Sample sf2Sample = new SF2Sample();
            sf2Sample.setName(musicPatch.patchID + "_" + musicPatch.notePitches[index]);
            sf2Sample.setData(byteArrayOutputStream.toByteArray());
            sf2Sample.setSampleRate(musicPatch.audioBuffers[index].sampleRate);
            sf2Sample.setOriginalPitch(musicPatch.notePitches[index]);
            sf2Sample.setStartLoop(musicPatch.audioBuffers[index].start);
            sf2Sample.setEndLoop(musicPatch.audioBuffers[index].end);
            sf2Sample.setSampleType(1); //Mono
            sf2Sample.setSampleLink(-1); //No Link
            sf2Soundbank.addResource(sf2Sample);

            previousRootPitch = musicPatch.notePitches[index];
        }

        SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Patch " + musicPatch.patchID);
        sf2Soundbank.addResource(sf2Layer);

        int lastRange;
        int nextRange = 0;
        int note = 0;

        for (int index = 0; index < sf2Soundbank.getSamples().length; index++) {

            byte[] ranges = new byte[2];

            for (int key = 0; key < 128; key++) {
                int currentKey = musicPatch.notePitches[key];
                if (musicPatch.notePitches[key] != currentKey) {
                    note++;
                }
            }

            lastRange = nextRange;
            nextRange = note;

            ranges[0] = (byte) lastRange;
            ranges[1] = (byte) nextRange;

            SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
            sf2LayerRegion.putBytes(SF2Region.GENERATOR_KEYRANGE, ranges);
            sf2LayerRegion.setSample(sf2Soundbank.getSamples()[index]);
            sf2Layer.getRegions().add(sf2LayerRegion);

            nextRange++;
        }

        try {
            sf2Soundbank.save("./0.sf2/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
