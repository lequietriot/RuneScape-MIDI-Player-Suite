package main;

import org.displee.cache.index.Index;

import java.nio.ByteBuffer;

public class SoundEffect {

    private static Tone[] tones;
    
    private static int start;
    private static int end;

    public static SoundEffect readSoundEffect(Index soundEffectIndex, int archiveId, int fileId) {
        SoundEffect sfx = new SoundEffect();
        sfx.decode(ByteBuffer.wrap(soundEffectIndex.getArchive(archiveId).getFile(fileId).getData()));
        return sfx;
    }

    public void decode(ByteBuffer buffer) {

        tones = new Tone[10];

        for(int index = 0; index < 10; ++index) {
            final int type = buffer.get() & 0xFF;
            if(type != 0) {
                buffer.position(buffer.position() - 1);
                tones[index] = new Tone();
                tones[index].decode(buffer);
            }
        }
        start = buffer.getShort() & 0xFFFF;
        end = buffer.getShort() & 0xFFFF;
    }

    public AudioBuffer toAudioBuffer() {
        final byte[] mix = mix();
        return new AudioBuffer(22050, mix, start * 22050 / 1000, end * 22050 / 1000);
    }

    public final int calculateDelay() {
        int var1 = 9999999;

        int var2;
        for(var2 = 0; var2 < 10; ++var2) {
            if(tones[var2] != null && tones[var2].offset / 20 < var1) {
                var1 = tones[var2].offset / 20;
            }
        }

        if(start < end && start / 20 < var1) {
            var1 = start / 20;
        }

        if(var1 != 9999999 && var1 != 0) {
            for(var2 = 0; var2 < 10; ++var2) {
                if(tones[var2] != null) {
                    tones[var2].offset -= var1 * 20;
                }
            }

            if(start < end) {
                start -= var1 * 20;
                end -= var1 * 20;
            }

            return var1;
        } else {
            return 0;
        }
    }

    private static byte[] mix() {
        int var1 = 0;

        int var2;
        for(var2 = 0; var2 < 10; ++var2) {
            if(tones[var2] != null && tones[var2].duration + tones[var2].offset > var1) {
                var1 = tones[var2].duration + tones[var2].offset;
            }
        }

        if(var1 == 0) {
            return new byte[0];
        } else {
            var2 = var1 * 22050 / 1000;
            final byte[] var3 = new byte[var2];

            for(int var4 = 0; var4 < 10; ++var4) {
                if(tones[var4] != null) {
                    final int var5 = tones[var4].duration * 22050 / 1000;
                    final int var6 = tones[var4].offset * 22050 / 1000;
                    final int[] var7 = tones[var4].synthesize(var5, tones[var4].duration);

                    for(int var8 = 0; var8 < var5; ++var8) {
                        int var9 = (var7[var8] >> 8) + var3[var8 + var6];
                        if((var9 + 128 & -256) != 0) {
                            var9 = var9 >> 31 ^ 127;
                        }

                        var3[var8 + var6] = (byte)var9;
                    }
                }
            }
            return var3;
        }
    }
}
