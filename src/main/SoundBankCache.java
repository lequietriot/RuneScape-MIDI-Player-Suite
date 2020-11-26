package main;

import main.utils.NodeHashTable;
import org.displee.cache.index.Index;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SoundBankCache {

    private final Index soundEffectIndex;
    private final Index musicSampleIndex;
    private final NodeHashTable musicSamples;
    private final NodeHashTable AudioBuffers;

    SoundBankCache(Index var1, Index var2) {
        this.musicSamples = new NodeHashTable(256);
        this.AudioBuffers = new NodeHashTable(256);
        this.soundEffectIndex = var1;
        this.musicSampleIndex = var2;
    }

    AudioBuffer getSoundEffect0(int var1, int var2, int[] var3) {
        int var4 = var2 ^ (var1 << 4 & 65535 | var1 >>> 12);
        var4 |= var1 << 16;
        long var5 = var4;
        AudioBuffer var7 = (AudioBuffer)this.AudioBuffers.get(var5);
        if(var7 != null) {
            return var7;
        } else if(var3 != null && var3[0] <= 0) {
            return null;
        } else {
            SoundEffect var8 = SoundEffect.readSoundEffect(this.soundEffectIndex, var1, var2);
            if(var8 == null) {
                return null;
            } else {
                var7 = var8.toAudioBuffer();
                this.AudioBuffers.put(var7, var5);
                if(var3 != null) {
                    var3[0] -= var7.samples.length;
                }

                return var7;
            }
        }
    }

    AudioBuffer getMusicSample0(int var1, int var2, int[] var3) {
        int var4 = var2 ^ (var1 << 4 & 65535 | var1 >>> 12);
        var4 |= var1 << 16;
        long var5 = (long) var4 ^ 4294967296L; //32-bit value range possibility
        AudioBuffer var7 = (AudioBuffer) this.AudioBuffers.get(var5);
        if(var7 != null) {
            return var7;
        } else if(var3 != null && var3[0] <= 0) {
            return null;
        } else {
            MusicSample var8 = (MusicSample) this.musicSamples.get(var5);
            if(var8 == null) {
                var8 = MusicSample.readMusicSample(this.musicSampleIndex, var1, var2);
                if(var8 == null) {
                    return null;
                }

                this.musicSamples.put(var8, var5);
            }

            var7 = var8.toAudioBuffer(var3);
            if(var7 == null) {
                return null;
            } else {
                this.AudioBuffers.put(var7, var5);
                return var7;
            }
        }
    }

    public AudioBuffer getSoundEffect(int var1, int[] var2) {
        if(this.soundEffectIndex.getArchives().length == 1) {
            return this.getSoundEffect0(0, var1, var2);
        } else if(this.soundEffectIndex.getArchive(var1).getFiles().length == 1) {
            return this.getSoundEffect0(var1, 0, var2);
        } else {
            throw new RuntimeException();
        }
    }

    public AudioBuffer getMusicSample(int var1, int[] var2) {
        if(this.musicSampleIndex.getArchives().length == 1) {
            return this.getMusicSample0(0, var1, var2);
        } else if(this.musicSampleIndex.getArchive(var1).getFiles().length == 1) {
            return this.getMusicSample0(var1, 0, var2);
        } else {
            throw new RuntimeException();
        }
    }


    public static AudioBuffer getCustomSoundEffect(File idx4, int i, int[] var3) throws IOException, UnsupportedAudioFileException {

        File soundEffectFile = new File(idx4.toString() + "/" + i + ".wav/");
        byte[] data = AudioSystem.getAudioInputStream(soundEffectFile).readAllBytes();
        int sampleRate = (int) AudioSystem.getAudioInputStream(soundEffectFile).getFormat().getSampleRate();

        for (int l = 0; l < data.length; l++) {
            data[l] = (byte) ((data[l] ^ 255) & 0xFF);
        }

        return new AudioBuffer(sampleRate, data, 0, 0);
    }

    public static AudioBuffer getCustomMusicSample(File idx14, int id) throws IOException {

        int sampleRate;
        AudioBuffer raw;

        try {
            File sampleFile = new File(idx14.toString() + "/" + id + ".wav/");

            byte[] data = AudioSystem.getAudioInputStream(sampleFile).readAllBytes();
            sampleRate = (int) AudioSystem.getAudioInputStream(sampleFile).getFormat().getSampleRate();

            for (int l = 0; l < data.length; l++) {
                data[l] = (byte) ((data[l] ^ 255) & 0xFF);
            }

            raw = new AudioBuffer(sampleRate, data, 0, 0);
            return raw;

            /**
            for (int l = 0; l < data.length; l++) {
                data[l] = (byte) (data[l] & 0xFF);
            }

            if (LoopConstants.getHDStart(id) != 0) {
                raw = new AudioBuffer(sampleRate * 4, data, LoopConstants.getHDStart(id), data.length);
                return raw;
            }
            else {
                raw = new AudioBuffer(sampleRate * 4, data, LoopConstants.getHDStart(id), data.length);
                return raw;
            }
             **/
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }

        return null;
    }
}
