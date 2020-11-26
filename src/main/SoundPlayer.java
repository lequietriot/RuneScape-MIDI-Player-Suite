package main;

import javax.sound.sampled.*;
import java.io.*;

public class SoundPlayer extends PcmPlayer {

    AudioFormat audioFormat;
    SourceDataLine sourceDataLine;
    int capacity2;
    byte[] byteSamples;
    DataOutputStream dataOutputStream;
    ByteArrayOutputStream byteArrayOutputStream;

    public void init() {
        this.audioFormat = new AudioFormat((float) PcmPlayer.pcmPlayer_sampleRate, 16, PcmPlayer.pcmPlayer_stereo ? 2 : 1, true, false);
        this.byteSamples = new byte[256 << (PcmPlayer.pcmPlayer_stereo ? 2 : 1)];
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    public void open(int available) {
        try {
            DataLine.Info var2 = new DataLine.Info(SourceDataLine.class, this.audioFormat, available << (PcmPlayer.pcmPlayer_stereo ? 2 : 1));
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(var2);
            sourceDataLine.open();
            sourceDataLine.start();
            capacity2 = available;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public int position() {
        return this.capacity2 - (this.sourceDataLine.available() >> (PcmPlayer.pcmPlayer_stereo?2:1));
    }

    public void write() {
        int var1 = 256;
        if(pcmPlayer_stereo) {
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

    public void writeToBuffer() {
        int var1 = 256;
        if(pcmPlayer_stereo) {
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
         try {
            byteArrayOutputStream.write(this.byteSamples);
         } catch (IOException e) {
         e.printStackTrace();
         }
    }

    public void writeCustom() {
        int var1 = 256;
        if(pcmPlayer_stereo) {
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

    public void close() {
        if(this.sourceDataLine != null) {
            this.sourceDataLine.close();
            this.sourceDataLine = null;
        }
    }

    public void discard() {
        this.sourceDataLine.flush();
    }
}
