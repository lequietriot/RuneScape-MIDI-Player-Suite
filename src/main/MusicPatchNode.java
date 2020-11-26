package main;

import main.utils.Node;

public class MusicPatchNode extends Node {

    MusicPatch patch;
    AudioBuffer audioBuffer;
    MusicPatchNode2 musicPatchInfo;
    RawPcmStream stream;
    int currentTrack; //The current track that is being played.
    int loopVariable; //Typically -1, indicating a sample loop until note off.
    int currentNotePitch; //The note pitch, valued 0 through 127, to cover all 128 keys.
    int maxVolumeLevel; //The maximum volume level?
    int currentPanValue; //Panning value, 0 (Left) through 127 (Right): value of 64 means audio is centered.
    int frequencyCorrection; //Can be negative or positive, Each note is either -256 or +256 (octaves are -/+3072).
    int field2454;
    int field2455;
    int field2456;
    int field2457;
    int field2458;
    int field2450;
    int field2448;
    int field2461;
    int field2449;
    int field2453;
    int field2462;

    MusicPatchNode() {

    }

    void clearMusicPatchNode() {
        this.patch = null;
        this.audioBuffer = null;
        this.musicPatchInfo = null;
        this.stream = null;
    }

}
