package main;

import main.utils.Node;

public class MusicPatchNode extends Node {

    int currentTrack;
    MusicPatch patch;
    AudioBuffer audioBuffer;
    MusicPatchNode2 musicPatchInfo;
    int field2467;
    int currentNotePitch;
    int field2451;
    int currentPanValue;
    int field2445;
    int field2454;
    int field2455;
    int field2456;
    int field2457;
    int field2458;
    int field2459;
    int field2448;
    int field2461;
    int field2449;
    RawPcmStream stream;
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
