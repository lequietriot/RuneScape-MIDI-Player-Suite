package main;

import com.sun.media.sound.SF2Soundbank;

public class PatchBanks {

    public static String RUNESCAPE_VERSION = "OSRS";

    public static MusicPatch getCustomMusicPatch(int patchID, SF2Soundbank sf2Soundbank) {

        return new MusicPatch(patchID, sf2Soundbank);
    }
}
