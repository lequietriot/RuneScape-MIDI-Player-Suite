package main;

import com.sun.media.sound.SF2Sample;
import com.sun.media.sound.SF2Soundbank;

import javax.sound.sampled.*;
import java.io.*;

public class MakeSoundFont {

    MusicPatchNode musicPatchNode;
    SF2Sample sf2Sample;
    SF2Soundbank sf2Soundbank;

    public void initSoundFont() {
        sf2Soundbank = new SF2Soundbank();
        sf2Soundbank.setName("Sample Bank");
        sf2Soundbank.setRomName("RuneScape MIDI Suite");
        sf2Soundbank.setRomVersionMajor(1);
        sf2Soundbank.setRomVersionMinor(0);
    }

    public void addSamplesToBank(SF2Sample sf2Sample) {
        sf2Soundbank.addResource(sf2Sample);
    }

    public void addSamplesCustom(MusicPatch musicPatch) throws IOException, UnsupportedAudioFileException {

        int var5 = 0;
        AudioBuffer audioBuffer;

        for(int var7 = 0; var7 < 128; ++var7) {

            int var8 = musicPatch.sampleOffset[var7];
            if(var8 != 0) {
                if(var8 != var5) {
                    var5 = var8--;
                    if((var8 & 1) == 0) {
                        audioBuffer = musicPatch.getCustomAudioSampleSFX(var8 >> 2);

                        byte[] rawData = audioBuffer.samples;
                        byte[] audioData = new byte[rawData.length * 2];

                        for (int frame = 0; frame < rawData.length; frame++) {
                            audioData[frame * 2] = rawData[frame];
                            audioData[frame * 2 + 1] = (rawData[frame]);
                        }

                        sf2Sample = new SF2Sample();
                        sf2Sample.setName(String.valueOf(((var8 >> 2))));
                        sf2Sample.setData(audioData);
                        sf2Sample.setSampleRate(audioBuffer.sampleRate);
                        sf2Sample.setStartLoop(audioBuffer.start);
                        sf2Sample.setEndLoop(audioBuffer.end);
                        sf2Sample.setSampleType(1);
                        sf2Sample.setSampleLink(0);

                        addSamplesToBank(sf2Sample);

                    } else {
                        audioBuffer = musicPatch.getCustomAudioSample(var8 >> 2);

                        byte[] rawData = audioBuffer.samples;
                        byte[] audioData = new byte[rawData.length * 2];

                        for (int frame = 0; frame < rawData.length; frame++) {
                            audioData[frame * 2] = rawData[frame];
                            audioData[frame * 2 + 1] = (rawData[frame]);
                        }

                        sf2Sample = new SF2Sample();
                        sf2Sample.setName(String.valueOf(((var8 >> 2))));
                        sf2Sample.setData(audioData);
                        sf2Sample.setSampleRate(audioBuffer.sampleRate);
                        sf2Sample.setStartLoop(audioBuffer.start);
                        sf2Sample.setEndLoop(audioBuffer.end);
                        sf2Sample.setSampleType(1);
                        sf2Sample.setSampleLink(0);

                        addSamplesToBank(sf2Sample);
                    }
                }
            }
        }
    }

    public void addSamples(MusicPatch musicPatch, SoundBankCache soundBankCache) {

        int var5 = 0;
        AudioBuffer audioBuffer;

        for(int var7 = 0; var7 < 128; ++var7) {

            int var8 = musicPatch.sampleOffset[var7];
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
                        sf2Sample.setSampleRate(audioBuffer.sampleRate);
                        sf2Sample.setStartLoop(audioBuffer.start);
                        sf2Sample.setEndLoop(audioBuffer.end);
                        sf2Sample.setSampleType(1);
                        sf2Sample.setSampleLink(0);

                        addSamplesToBank(sf2Sample);

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
                        sf2Sample.setSampleRate(audioBuffer.sampleRate);
                        sf2Sample.setStartLoop(audioBuffer.start);
                        sf2Sample.setEndLoop(audioBuffer.end);
                        sf2Sample.setSampleType(1);
                        sf2Sample.setSampleLink(0);

                        addSamplesToBank(sf2Sample);
                    }
                }
            }
        }
    }

    public void saveSoundBank() {
        try {
            sf2Soundbank.save("./SoundFonts/RuneScape.sf2/");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
