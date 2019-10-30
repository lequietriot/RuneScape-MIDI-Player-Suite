package main.tools;

import com.sun.media.sound.SF2Soundbank;

public class MakeSoundFont {

    public void createSoundFont(byte[] sampleData) {

        SF2Soundbank sf2Soundbank = new SF2Soundbank();

        byte[] newSample = mixTo16Bit(sampleData);


    }

    private byte[] mixTo16Bit(byte[] sampleData) {

        byte[] mix = new byte[sampleData.length * 2];

        for (int i = 0; i < sampleData.length; i++) {
            mix[i * 2] = (byte) (sampleData[i] >> 16 * 2);
            mix[i * 2 + 1] = (byte) (sampleData[i] >> 16 * 2 + 1);
        }
        return mix;
    }
}
