package main;

import com.sun.media.sound.SF2Soundbank;

public class PatchBanks {

    public static String RUNESCAPE_VERSION = "RS3";

    public static MusicPatch getMusicPatch(int patchID, SF2Soundbank sf2Soundbank) {

        switch (patchID) {
            case 304:
                MusicPatch musicPatch304 = new MusicPatch(304, sf2Soundbank);
                musicPatch304.sampleOffset[12] = 36741;
                musicPatch304.sampleOffset[24] = 36742;
                return musicPatch304;
        }
        return null;
    }
}
