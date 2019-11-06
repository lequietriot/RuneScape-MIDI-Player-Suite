package main;

import javax.sound.sampled.*;

public class SoundPlayer extends PcmPlayer {

    AudioFormat audioFormat;
    SourceDataLine sourceDataLine;
    int capacity;
    byte[] byteSamples;

    void init() {
        this.audioFormat = new AudioFormat((float) AudioConstants.systemSampleRate, 16, AudioConstants.isStereo ? 2 : 1, true, false);
        this.byteSamples = new byte[256 << (AudioConstants.isStereo ? 2 : 1)];
    }

    void open(int available) {
        try {
            DataLine.Info var2 = new DataLine.Info(SourceDataLine.class, this.audioFormat, available << (AudioConstants.isStereo ? 2 : 1));
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(var2);
            sourceDataLine.open();
            sourceDataLine.start();
            capacity = available;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            this.open(available);
        }
    }

    int position() {
        return this.capacity - (this.sourceDataLine.available() >> (AudioConstants.isStereo?2:1));
    }

    void write() {
        int var1 = 256;
        if(AudioConstants.isStereo) {
            var1 <<= 1;
        }

        for(int var2 = 0; var2 < var1; ++var2) {
            int var3 = super.samples[var2];
            if((var3 + 8388608 & -16777216) != 0) {
                var3 = 8388607 ^ var3 >> 31;
            }

            this.byteSamples[var2 * 2] = (byte)(var3 >> 8);
            this.byteSamples[var2 * 2 + 1] = (byte)(var3 >> 16);
        }
        this.sourceDataLine.write(this.byteSamples, 0, var1 << 1);
    }

    void close() {
        if(this.sourceDataLine != null) {
            this.sourceDataLine.close();
            this.sourceDataLine = null;
        }
    }

    void discard() {
        this.sourceDataLine.flush();
    }
}
