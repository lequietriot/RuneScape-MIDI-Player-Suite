package main;

import java.nio.ByteBuffer;

public class SoundEffect {

    private static AudioInstrument[] instruments;
    
    private static int start;
    private static int end;

    public void decode(ByteBuffer buffer) {

        instruments = new AudioInstrument[10];

        for(int idx = 0; idx < 10; ++idx) {
            final int var3 = buffer.get() & 0xFF;
            if(var3 != 0) {
                buffer.position(buffer.position() - 1);
                instruments[idx] = new AudioInstrument();
                instruments[idx].decode(buffer);
            }
        }
        start = buffer.getShort() & 0xFFFF;
        end = buffer.getShort() & 0xFFFF;
    }

    public static RawSound toRawSound() {
        final byte[] mix = mix();
        return new RawSound(22050, mix, start * 22050 / 1000, end * 22050 / 1000);
    }

    public final int calculateDelay() {
        int var1 = 9999999;

        int var2;
        for(var2 = 0; var2 < 10; ++var2) {
            if(instruments[var2] != null && instruments[var2].offset / 20 < var1) {
                var1 = instruments[var2].offset / 20;
            }
        }

        if(start < end && start / 20 < var1) {
            var1 = start / 20;
        }

        if(var1 != 9999999 && var1 != 0) {
            for(var2 = 0; var2 < 10; ++var2) {
                if(instruments[var2] != null) {
                    instruments[var2].offset -= var1 * 20;
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
            if(instruments[var2] != null && instruments[var2].duration + instruments[var2].offset > var1) {
                var1 = instruments[var2].duration + instruments[var2].offset;
            }
        }

        if(var1 == 0) {
            return new byte[0];
        } else {
            var2 = var1 * 22050 / 1000;
            final byte[] var3 = new byte[var2];

            for(int var4 = 0; var4 < 10; ++var4) {
                if(instruments[var4] != null) {
                    final int var5 = instruments[var4].duration * 22050 / 1000;
                    final int var6 = instruments[var4].offset * 22050 / 1000;
                    final int[] var7 = instruments[var4].synthesize(var5, instruments[var4].duration);

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
