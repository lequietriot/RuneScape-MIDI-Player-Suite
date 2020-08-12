package main;

import com.sun.media.sound.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

public class MakeSoundFont {

    MusicPatchNode musicPatchNode;

    void createSoundFont(MidiPcmStream midiPcmStream, int bank, int program) throws IOException {

        int patchNumber;

        if (bank > 0) {
            program += (bank * 128);
        }

        patchNumber = program;

        PcmPlayer.pcmPlayer_stereo = false;
        PcmPlayer.pcmPlayer_sampleRate = 44100;

        SF2Soundbank sf2Soundbank = new SF2Soundbank();
        sf2Soundbank.setName("Patch");
        sf2Soundbank.setRomName("RuneScape MIDI Suite");
        sf2Soundbank.setRomVersionMajor(1);
        sf2Soundbank.setRomVersionMinor(0);

        SF2Sample sf2Sample = new SF2Sample();
        SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        SF2Layer sf2Layer = new SF2Layer();

        SoundPlayer soundPlayer = new SoundPlayer();
        soundPlayer.byteArrayOutputStream = new ByteArrayOutputStream();
        soundPlayer.setStream(midiPcmStream);
        soundPlayer.samples = new int[512];
        soundPlayer.capacity = 16384;
        soundPlayer.init();
        soundPlayer.open(soundPlayer.capacity);

        while (midiPcmStream.active) {

            soundPlayer.fill(soundPlayer.samples, 256);
            soundPlayer.writeToBuffer();

            for (MusicPatchNode musicPatchNode1 = (MusicPatchNode) midiPcmStream.patchStream.queue.first(); musicPatchNode1 != null; musicPatchNode1 = (MusicPatchNode) midiPcmStream.patchStream.queue.next()) {
                musicPatchNode = musicPatchNode1;
            }

            if (midiPcmStream.midiFile.isDone()) {

                sf2Sample.setName(patchNumber + "_" + musicPatchNode.currentNotePitch);
                sf2Sample.setData(soundPlayer.byteArrayOutputStream.toByteArray());
                sf2Sample.setSampleRate(PcmPlayer.pcmPlayer_sampleRate);
                sf2Sample.setOriginalPitch(musicPatchNode.currentNotePitch);
                //sf2Sample.setStartLoop(musicPatchNode.audioBuffer.start);
                //sf2Sample.setEndLoop(musicPatchNode.audioBuffer.end);
                sf2Sample.setSampleType(1); //Mono
                sf2Sample.setSampleLink(-1); //No Link
                sf2Soundbank.addResource(sf2Sample);

                sf2Layer.setName("Layer");

                sf2LayerRegion.putInteger(SF2Region.GENERATOR_INITIALATTENUATION, musicPatchNode.attenuation / 4);
                sf2LayerRegion.putInteger(SF2Region.GENERATOR_SAMPLEMODES, musicPatchNode.loopVariable * -1);
                sf2LayerRegion.putInteger(SF2Region.GENERATOR_RELEASEVOLENV, musicPatchNode.field2453);
                sf2LayerRegion.setSample(sf2Sample);
                sf2Layer.getRegions().add(sf2LayerRegion);

                sf2Soundbank.addResource(sf2Layer);
                midiPcmStream.active = false;
            }
        }

        /**
        for (MusicPatchNode musicPatchNode = (MusicPatchNode) midiPcmStream.patchStream.queue.first(); musicPatchNode != null; musicPatchNode = (MusicPatchNode) midiPcmStream.patchStream.queue.next()) {

            System.out.println();
            System.out.println("Patch #" + musicPatchNode.patch.key);
            System.out.println("Patch Volume 1: " + musicPatchNode.patch.velocity);
            System.out.println("Patch Volume 2: " + musicPatchNode.patch.volume[musicPatchNode.currentNotePitch]);
            System.out.println("offset: " + musicPatchNode.patch.pitchOffset[musicPatchNode.currentNotePitch]);
            System.out.println();
            System.out.println("Attack?: " + musicPatchNode.musicPatchInfo.volEnvAttack);
            System.out.println("Decay?: " + musicPatchNode.musicPatchInfo.volEnvDecay);
            System.out.println("2394: " + musicPatchNode.musicPatchInfo.field2394);
            System.out.println("2401: " + musicPatchNode.musicPatchInfo.field2401);
            System.out.println();
            System.out.println("Active Track: " + musicPatchNode.currentTrack);
            System.out.println("Attenuation: " + musicPatchNode.attenuation);
            System.out.println("Loop Mode: " + musicPatchNode.loopVariable);
            System.out.println("Note Pitch (0-127): " + musicPatchNode.currentNotePitch);
            System.out.println("Pan (0-127): " + musicPatchNode.currentPanValue);
            System.out.println("Frequency Correction: " + musicPatchNode.frequencyCorrection);
            System.out.println("2454: " + musicPatchNode.field2454);
            System.out.println("Sustain Pedal: " + musicPatchNode.sustainPedal);
            System.out.println("2458: " + musicPatchNode.field2458);
            System.out.println("2455: " + musicPatchNode.field2455);
            System.out.println("Portamento: " + musicPatchNode.field2453);
            System.out.println("2462: " + musicPatchNode.field2462);
            System.out.println("2449: " + musicPatchNode.field2449);
            System.out.println();

            AudioFormat audioFormat = new AudioFormat(PcmPlayer.pcmPlayer_sampleRate, 16, 1, true, false);

            AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(soundPlayer.byteArrayOutputStream.toByteArray()), audioFormat, soundPlayer.byteArrayOutputStream.toByteArray().length);
            try {
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, soundPlayer.byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            sf2Sample.setName(musicPatchNode.patch.key + "_" + musicPatchNode.currentNotePitch);
            sf2Sample.setData(soundPlayer.byteArrayOutputStream.toByteArray());
            sf2Sample.setSampleRate(musicPatchNode.audioBuffer.sampleRate);
            sf2Sample.setOriginalPitch(musicPatchNode.currentNotePitch);
            sf2Sample.setStartLoop(musicPatchNode.audioBuffer.start);
            sf2Sample.setEndLoop(musicPatchNode.audioBuffer.end);
            sf2Sample.setSampleType(1); //Mono
            sf2Sample.setSampleLink(-1); //No Link
            sf2Soundbank.addResource(sf2Sample);

            sf2Layer.setName("Layer");

            sf2LayerRegion.putInteger(SF2Region.GENERATOR_INITIALATTENUATION, musicPatchNode.attenuation / 4);
            sf2LayerRegion.putInteger(SF2Region.GENERATOR_SAMPLEMODES, musicPatchNode.loopVariable * -1);
            sf2LayerRegion.putInteger(SF2Region.GENERATOR_RELEASEVOLENV, musicPatchNode.field2453);
            sf2LayerRegion.setSample(sf2Sample);
            sf2Layer.getRegions().add(sf2LayerRegion);

            sf2Soundbank.addResource(sf2Layer);
        }

         **/

        sf2Soundbank.save(new File("./SoundFonts/" + patchNumber + "/" + musicPatchNode.currentNotePitch + ".sf2/"));
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
