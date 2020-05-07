package main;

import main.utils.Node;

public class MusicPatchNode extends Node {

    int currentTrack; //The current track that is being played.
    MusicPatch patch;
    AudioBuffer audioBuffer;
    MusicPatchNode2 musicPatchInfo;
    int loopVariable; //Typically -1, indicating a sample loop until note off.
    int currentNotePitch; //The note pitch, valued 0 through 127, to cover all 128 keys.
    int attenuation; //The attenuation in dB - this reduces the volume of the sample.
    int currentPanValue; //Panning value, 0 through 127: value of 64 means audio is centered.
    int frequencyCorrection; //Can be negative or positive, Each note is either -256 or +256 (octaves are -/+3072).
    int field2454;
    int field2455; //Release?
    int field2456;
    int field2457;
    int field2458; //Sequencer/Tick Related
    int sustainPedal; //Sustain Pedal, value of -1 indicates that it is off.
    int field2448;
    int field2461;
    int field2449;
    RawPcmStream stream;
    int field2453; //Portamento?
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
