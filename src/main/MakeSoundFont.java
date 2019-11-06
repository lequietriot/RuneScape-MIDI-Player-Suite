package main;

import com.sun.media.sound.SF2Soundbank;

import main.MusicPatch;
import main.RawSound;
import main.SoundBankCache;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;

public class MakeSoundFont {

    void createSoundFont(MusicPatch musicPatch) {

        SF2Soundbank sf2Soundbank = new SF2Soundbank();

        for (int index = 0; index < 128; index++) {

            RawSound rawSound = musicPatch.rawSounds[index];
            byte[] newSound = mixTo16Bit(rawSound.samples);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(newSound), new AudioFormat(rawSound.sampleRate, 16, 1, true, false), newSound.length);
            try {
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(new File("./Soundbank/" + index + ".wav/"));
                fileOutputStream.write(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
