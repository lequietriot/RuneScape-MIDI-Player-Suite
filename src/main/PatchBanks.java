package main;

import com.sun.media.sound.SF2Soundbank;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PatchBanks {

    public static String RUNESCAPE_VERSION = "OSRS";

    public static Path CUSTOM_SOUND_PATH = Paths.get("./SoundBanks/" + RUNESCAPE_VERSION);

    public static String PATCH_NAME = "Instrument Name = ";
    public static String SAMPLE_NAME = "Sample = ";
    public static String SAMPLE_ROOT_KEY = "Root Key = ";
    public static String KEY_LOW_RANGE = "Lower Key Range = ";
    public static String KEY_HIGH_RANGE = "Upper Key Range = ";
    public static String MASTER_VOLUME = "Patch Volume = ";
    public static String LOOP_START = "Sample Loop Start = ";
    public static String LOOP_END = "Sample Loop End = ";
    public static String SAMPLE_VOLUME = "Sample Volume = ";
    public static String SAMPLE_PAN = "Sample Pan = ";

    public static String PARAMETER_1 = "Parameter 1 = ";
    public static String PARAMETER_2 = "Parameter 2 = ";
    public static String PARAMETER_3 = "Parameter 3 = ";
    public static String PARAMETER_4 = "Parameter 4 = ";
    public static String PARAMETER_5 = "Parameter 5 = ";
    public static String PARAMETER_6 = "Parameter 6 = ";
    public static String PARAMETER_7 = "Parameter 7 = ";

    public static String ARRAY_1 = "Array 1 = ";
    public static String ARRAY_2 = "Array 2 = ";

    public static MusicPatch makeCustomMusicPatch(int patchID) {

        return new MusicPatch(patchID);
    }

    public static MusicPatch getCustomMusicPatch(int patchID, SF2Soundbank sf2Soundbank) {

        return new MusicPatch(patchID, sf2Soundbank);
    }
}
