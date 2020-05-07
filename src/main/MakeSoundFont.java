package main;

import com.sun.media.sound.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MakeSoundFont {

    void createSoundFont(MidiPcmStream midiPcmStream) throws IOException {

        PcmPlayer.pcmPlayer_stereo = false;
        PcmPlayer.pcmPlayer_sampleRate = 22050;

        SF2Soundbank sf2Soundbank = new SF2Soundbank();
        sf2Soundbank.setName("Patch");
        sf2Soundbank.setRomName("RuneScape MIDI Suite");
        sf2Soundbank.setRomVersionMajor(1);
        sf2Soundbank.setRomVersionMinor(0);

        SF2Sample sf2Sample = new SF2Sample();
        SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        SF2Layer sf2Layer = new SF2Layer();

        SoundPlayer soundPlayer = new SoundPlayer();
        soundPlayer.setStream(midiPcmStream);
        soundPlayer.samples = new int[512];
        soundPlayer.capacity = 16384;
        soundPlayer.init();
        soundPlayer.open(soundPlayer.capacity);

        while (midiPcmStream.active) {
            soundPlayer.fill(soundPlayer.samples, 256);
            soundPlayer.writeToBuffer();

            for (MusicPatchNode musicPatchNode = (MusicPatchNode) midiPcmStream.patchStream.queue.first(); musicPatchNode != null; musicPatchNode = (MusicPatchNode) midiPcmStream.patchStream.queue.next()) {

                AudioFormat audioFormat = new AudioFormat(PcmPlayer.pcmPlayer_sampleRate, 16, 1, true, false);

                AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(soundPlayer.byteArrayOutputStream.toByteArray()), audioFormat, soundPlayer.byteArrayOutputStream.toByteArray().length);
                ByteArrayOutputStream audioStream = new ByteArrayOutputStream();

                try {
                    AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, audioStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                sf2Sample.setName(musicPatchNode.patch.key + "_" + musicPatchNode.currentNotePitch);
                sf2Sample.setData(audioStream.toByteArray());
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

            if (midiPcmStream.midiFile.isDone()) {
                break;
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

        sf2Soundbank.save(new File("./0.sf2/"));
    }
}
