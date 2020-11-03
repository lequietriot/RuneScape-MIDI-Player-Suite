package main;

import com.sun.media.sound.DLSInstrument;
import com.sun.media.sound.DLSSoundbank;

import java.io.IOException;

public class MakeDLS {

    public DLSSoundbank dlsSoundbank;
    public DLSInstrument dlsInstrument;

    public void initDLS() {
        dlsSoundbank = new DLSSoundbank();
        dlsSoundbank.setName("RuneScape DLS");
        dlsSoundbank.setMajor(1);
        dlsSoundbank.setMinor(0);
    }

    public void createDLSInstruments(MusicPatch musicPatch, SoundBankCache soundBankCache) {

        dlsInstrument = new DLSInstrument();

        int var5 = 0;
        AudioBuffer var6 = null;

        for(int var7 = 0; var7 < 128; ++var7) {
            int var8 = musicPatch.sampleOffset[var7];
            if(var8 != 0) {
                if(var8 != var5) {
                    var5 = var8--;
                    if((var8 & 1) == 0) {
                        var6 = soundBankCache.getSoundEffect(var8 >> 2, null);
                    } else {
                        var6 = soundBankCache.getMusicSample(var8 >> 2, null);
                    }

                    if(var6 == null) {
                    }
                }

                if(var6 != null) {
                    musicPatch.audioBuffers[var7] = var6;
                    musicPatch.sampleOffset[var7] = 0;
                }
            }
        }

        assert var6 != null;
        dlsInstrument.setGuid(var6.samples);
        dlsSoundbank.addInstrument(dlsInstrument);
    }

    public void addSamplesToDLS(MusicPatch musicPatch, SoundBankCache soundBankCache) {

    }

    public void saveDLSBanks() {
        try {
            dlsSoundbank.save("./RuneScape.dls/");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
