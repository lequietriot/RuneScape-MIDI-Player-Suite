package main;

import com.sun.media.sound.SF2Soundbank;

public class PatchBanks {

    public static String RUNESCAPE_VERSION = "OSRS";

    public static MusicPatch getCustomMusicPatch(int patchID, SF2Soundbank sf2Soundbank) {

        switch (patchID) {

            case 304:
                MusicPatch musicPatch304 = new MusicPatch(304, sf2Soundbank);
                musicPatch304.sampleOffset[12] = 36741;
                musicPatch304.sampleOffset[24] = 36742;
                return musicPatch304;
            case 305:
                MusicPatch musicPatch305 = new MusicPatch(305, sf2Soundbank);
                musicPatch305.sampleOffset[12] = 36743;
                musicPatch305.sampleOffset[24] = 36744;
                return musicPatch305;

        }
        return new MusicPatch(patchID, sf2Soundbank);
    }
}
