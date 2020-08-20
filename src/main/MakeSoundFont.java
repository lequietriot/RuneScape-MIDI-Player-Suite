package main;

import com.sun.media.sound.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.Arrays;

public class MakeSoundFont {

    MusicPatchNode musicPatchNode;

    void createSoundFont(MusicPatch musicPatch, SoundBankCache soundBankCache) throws IOException {

        int patchNumber = (int) musicPatch.key;

        SF2Soundbank sf2Soundbank = new SF2Soundbank();
        sf2Soundbank.setName("Patch " + patchNumber);
        sf2Soundbank.setRomName("RuneScape MIDI Suite");
        sf2Soundbank.setRomVersionMajor(1);
        sf2Soundbank.setRomVersionMinor(0);

        SF2Sample sf2Sample = new SF2Sample();
        SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        SF2Layer sf2Layer = new SF2Layer();

        FileOutputStream fileOutputStream = new FileOutputStream(new File("./SoundFonts/Patch.txt/"));
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

        for (int index = 0; index < 128; index++) {

            int sampleID = musicPatch.sampleOffset[index];
            AudioBuffer audioBuffer = null;

            if (sampleID != 0) {
                if ((sampleID & 1) == 0) {
                    audioBuffer = soundBankCache.getSoundEffect(musicPatch.sampleOffset[index] >> 2, null);
                } else {
                    audioBuffer = soundBankCache.getMusicSample(musicPatch.sampleOffset[index] >> 2, null);
                }
            }

            byte[] rawData = audioBuffer.samples;
            byte[] audioData = new byte[rawData.length * 2];

            for (int frame = 0; frame < rawData.length; frame++) {
                audioData[frame * 2] = rawData[frame];
                audioData[frame * 2 + 1] = (rawData[frame]);
            }

            sf2Sample.setName(String.valueOf(((musicPatch.sampleOffset[index] >> 2))));
            sf2Sample.setData(audioData);
            sf2Sample.setSampleRate(audioBuffer.sampleRate);
            sf2Sample.setStartLoop(audioBuffer.start);
            sf2Sample.setEndLoop(audioBuffer.end);
            sf2Sample.setOriginalPitch(128 - (musicPatch.pitchOffset[index] / -256));
            sf2Sample.setSampleType(1);
            sf2Sample.setSampleLink(0);

            System.out.println();
            System.out.println("Note " + index);
            System.out.println("Fixed Velocity = " + (musicPatch.velocity + musicPatch.volume[index]));
            System.out.println("Pan Value = " + (musicPatch.panOffset[index] - 64) * 1.28);
            System.out.println("Sample ID = " + ((musicPatch.sampleOffset[index] >> 2) - 1));
            System.out.println("Sample Rate = " + soundBankCache.getMusicSample((musicPatch.sampleOffset[index] >> 2) - 1, null).sampleRate);
            System.out.println("Sample Loop Start = " + soundBankCache.getMusicSample((musicPatch.sampleOffset[index] >> 2) - 1, null).start);
            System.out.println("Sample Loop End = " + soundBankCache.getMusicSample((musicPatch.sampleOffset[index] >> 2) - 1, null).end);
            System.out.println("Sample Pitch = " + (128 - (musicPatch.pitchOffset[index] / -256)));
            System.out.println();
            System.out.println("??? = " + Arrays.toString(musicPatch.musicPatchNode2[index].field2402));
            System.out.println("??? = " + Arrays.toString(musicPatch.musicPatchNode2[index].field2398));
            System.out.println("??? = " + musicPatch.musicPatchNode2[index].volEnvAttack);
            System.out.println("Decay = " + (musicPatch.musicPatchNode2[index].volumeEnvelopeDecay));
            System.out.println("??? = " + musicPatch.musicPatchNode2[index].vibratoLFODelay);
            System.out.println("??? = " + musicPatch.musicPatchNode2[index].vibratoLFOFrequency);
            System.out.println("??? = " + musicPatch.musicPatchNode2[index].vibratoLFOPitch);
            System.out.println("??? = " + musicPatch.musicPatchNode2[index].field2401);
            System.out.println("??? = " + musicPatch.musicPatchNode2[index].field2394);
        }

        sf2Soundbank.addResource(sf2Sample);
        sf2Soundbank.save(new File("./SoundFonts/" + patchNumber + ".sf2/"));
    }

    void saveSoundResource(MidiPcmStream midiPcmStream, int bank, int program, int notePitch) {

        int patchNumber;

        if (bank > 0) {
            program += (bank * 128);
        }

        patchNumber = program;

        PcmPlayer.pcmPlayer_stereo = true;
        PcmPlayer.pcmPlayer_sampleRate = 22050;

        SoundPlayer soundPlayer = new SoundPlayer();
        soundPlayer.byteArrayOutputStream = new ByteArrayOutputStream();
        soundPlayer.setStream(midiPcmStream);
        soundPlayer.samples = new int[512];
        soundPlayer.capacity = 16384;
        soundPlayer.init();
        soundPlayer.open(soundPlayer.capacity);

        while (midiPcmStream.active) {

            for (MusicPatchNode musicPatchNode1 = (MusicPatchNode) midiPcmStream.patchStream.queue.first(); musicPatchNode1 != null; musicPatchNode1 = (MusicPatchNode) midiPcmStream.patchStream.queue.next()) {
                musicPatchNode = musicPatchNode1;

                System.out.println();
                System.out.println("Patch #" + musicPatchNode.patch.key);
                System.out.println("Note/Key = " + musicPatchNode.currentNotePitch);
                System.out.println("Fixed Velocity = " + (musicPatchNode.patch.velocity + musicPatchNode.patch.volume[musicPatchNode.currentNotePitch]));
                System.out.println();

            }

            soundPlayer.fill(soundPlayer.samples, 256);
            soundPlayer.writeToBuffer();

            if (midiPcmStream.midiFile.isDone()) {
                break;
            }
        }

        byte[] data = soundPlayer.byteArrayOutputStream.toByteArray();

        File outFile = new File("./Sounds/Sound Renders/" + patchNumber + "_" + notePitch + ".wav/");
        FileOutputStream fos;

        try {

            fos = new FileOutputStream(outFile);
            AudioFormat format = new AudioFormat(PcmPlayer.pcmPlayer_sampleRate, 16, 2, true, false);
            AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, fos);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
