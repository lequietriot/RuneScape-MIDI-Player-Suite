package main;

import org.displee.cache.index.Index;

import java.util.Hashtable;

public class SoundBankCache {

    private Index soundEffectIndex;
    private Index musicSampleIndex;
    private Hashtable musicSamples;
    private Hashtable rawSounds;

    SoundBankCache(Index var1, Index var2) {
        this.musicSamples = new Hashtable(256);
        this.rawSounds = new Hashtable(256);
        this.soundEffectIndex = var1;
        this.musicSampleIndex = var2;
    }

    RawSound getSoundEffect0(int var1, int var2, int[] var3) {
        int var4 = var2 ^ (var1 << 4 & 65535 | var1 >>> 12);
        var4 |= var1 << 16;
        long var5 = (long)var4;
        RawSound var7 = (RawSound)this.rawSounds.get(var5);
        if(var7 != null) {
            return var7;
        } else if(var3 != null && var3[0] <= 0) {
            return null;
        } else {
            SoundEffect var8 = SoundEffect.readSoundEffect(this.soundEffectIndex, var1, var2);
            if(var8 == null) {
                return null;
            } else {
                var7 = var8.toRawSound();
                this.rawSounds.put(var7, var5);
                if(var3 != null) {
                    var3[0] -= var7.samples.length;
                }

                return var7;
            }
        }
    }

    RawSound getMusicSample0(int var1, int var2, int[] var3) {
        int var4 = var2 ^ (var1 << 4 & 65535 | var1 >>> 12);
        var4 |= var1 << 16;
        long var5 = (long)var4 ^ 4294967296L;
        RawSound var7 = (RawSound)this.rawSounds.get(var5);
        if(var7 != null) {
            return var7;
        } else if(var3 != null && var3[0] <= 0) {
            return null;
        } else {
            MusicSample var8 = (MusicSample)this.musicSamples.get(var5);
            if(var8 == null) {
                var8 = MusicSample.readMusicSample(this.musicSampleIndex, var1, var2);
                if(var8 == null) {
                    return null;
                }

                this.musicSamples.put(var8, var5);
            }

            var7 = var8.toRawSound(var3);
            if(var7 == null) {
                return null;
            } else {
                this.rawSounds.put(var7, var5);
                return var7;
            }
        }
    }

    public RawSound getSoundEffect(int var1, int[] var2) {
        if(this.soundEffectIndex.getArchives().length == 1) {
            return this.getSoundEffect0(0, var1, var2);
        } else if(this.soundEffectIndex.getArchive(var1).getFiles().length == 1) {
            return this.getSoundEffect0(var1, 0, var2);
        } else {
            throw new RuntimeException();
        }
    }

    public RawSound getMusicSample(int var1, int[] var2) {
        if(this.musicSampleIndex.getArchives().length == 1) {
            return this.getMusicSample0(0, var1, var2);
        } else if(this.musicSampleIndex.getArchive(var1).getFiles().length == 1) {
            return this.getMusicSample0(var1, 0, var2);
        } else {
            throw new RuntimeException();
        }
    }
}
