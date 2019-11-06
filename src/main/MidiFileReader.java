package main;

import main.utils.ByteBufferUtils;

import java.nio.ByteBuffer;

public class MidiFileReader {

    static final byte[] __hs_x;
    ByteBuffer buffer;
    int division;
    int[] trackStarts;
    int[] trackPositions;
    int[] trackLengths;
    int[] __u;
    int microseconds;
    long __e;

    MidiFileReader(byte[] var1) {
        this.parse(var1);
    }

    MidiFileReader() {

    }

    void parse(byte[] var1) {
        this.buffer = ByteBuffer.wrap(var1);
        this.buffer.position(10);
        int var2 = ByteBufferUtils.__ag_302(this.buffer);
        this.division = ByteBufferUtils.__ag_302(this.buffer);
        this.microseconds = 500000;
        this.trackStarts = new int[var2];

        int var3;
        int var4;
        for(var3 = 0; var3 < var2; this.buffer.position(this.buffer.position() + var4)) {
            int var5 = this.buffer.getInt();
            var4 = this.buffer.getInt();
            if(var5 == 1297379947) {
                this.trackStarts[var3] = this.buffer.position();
                ++var3;
            }
        }

        this.__e = 0L;
        this.trackPositions = new int[var2];

        for(var3 = 0; var3 < var2; ++var3) {
            this.trackPositions[var3] = this.trackStarts[var3];
        }

        this.trackLengths = new int[var2];
        this.__u = new int[var2];
    }

    void clear() {
        this.buffer = null;
        this.trackStarts = null;
        this.trackPositions = null;
        this.trackLengths = null;
        this.__u = null;
    }

    boolean isReady() {
        return this.buffer != null;
    }

    int trackCount() {
        return this.trackPositions.length;
    }

    void gotoTrack(int var1) {
        this.buffer.position(this.trackPositions[var1]);
    }

    void markTrackPosition(int var1) {
        this.trackPositions[var1] = this.buffer.position();
    }

    void setTrackDone() {
        this.buffer.position(-1);
    }

    void readTrackLength(int var1) {
        int var2 = ByteBufferUtils.__as_311(this.buffer);
        this.trackLengths[var1] += var2;
    }

    int readMessage(int var1) {
        int var2 = this.readMessage0(var1);
        return var2;
    }

    int readMessage0(int var1) {
        byte var2 = this.buffer.array()[this.buffer.position()];
        int var3;
        if(var2 < 0) {
            var3 = var2 & 255;
            this.__u[var1] = var3;
            this.buffer.position(this.buffer.position() + 1);
        } else {
            var3 = this.__u[var1];
        }

        if(var3 != 240 && var3 != 247) {
            return this.__d_371(var1, var3);
        } else {
            int var4 = ByteBufferUtils.__as_311(this.buffer);
            if(var3 == 247 && var4 > 0) {
                int var5 = this.buffer.array()[this.buffer.position()] & 255;
                if(var5 >= 241 && var5 <= 243 || var5 == 246 || var5 == 248 || var5 >= 250 && var5 <= 252 || var5 == 254) {
                    this.buffer.position(this.buffer.position() + 1);
                    this.__u[var1] = var5;
                    return this.__d_371(var1, var5);
                }
            }

            this.buffer.position(this.buffer.position() + var4);
            return 0;
        }
    }

    int __d_371(int var1, int var2) {
        int var3;
        if(var2 == 255) {
            int var7 = this.buffer.get() & 0xFF;
            var3 = ByteBufferUtils.__as_311(this.buffer);
            if(var7 == 47) {
                this.buffer.position(this.buffer.position() + var3);
                return 1;
            } else if(var7 == 81) {
                int var5 = ByteBufferUtils.getMedium(this.buffer);
                var3 -= 3;
                int var6 = this.trackLengths[var1];
                this.__e += (long)var6 * (long)(this.microseconds - var5);
                this.microseconds = var5;
                this.buffer.position(this.buffer.position() + var3);
                return 2;
            } else {
                this.buffer.position(this.buffer.position() + var3);
                return 3;
            }
        } else {
            byte var4 = __hs_x[var2 - 128];
            var3 = var2;
            if(var4 >= 1) {
                var3 = var2 | (this.buffer.get() & 0xFF) << 8;
            }

            if(var4 >= 2) {
                var3 |= (this.buffer.get() & 0xFF) << 16;
            }

            return var3;
        }
    }

    long __a_372(int var1) {
        return this.__e + (long)var1 * (long)this.microseconds;
    }

    int getPrioritizedTrack() {
        int var1 = this.trackPositions.length;
        int var2 = -1;
        int var3 = Integer.MAX_VALUE;

        for(int var4 = 0; var4 < var1; ++var4) {
            if(this.trackPositions[var4] >= 0 && this.trackLengths[var4] < var3) {
                var2 = var4;
                var3 = this.trackLengths[var4];
            }
        }

        return var2;
    }

    boolean isDone() {
        int var1 = this.trackPositions.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            if(this.trackPositions[var2] >= 0) {
                return false;
            }
        }

        return true;
    }

    void reset(long var1) {
        this.__e = var1;
        int var3 = this.trackPositions.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            this.trackLengths[var4] = 0;
            this.__u[var4] = 0;
            this.buffer.position(this.trackStarts[var4]);
            this.readTrackLength(var4);
            this.trackPositions[var4] = this.buffer.position();
        }

    }

    static {
        __hs_x = new byte[]{(byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)0, (byte)1, (byte)2, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
    }
}
