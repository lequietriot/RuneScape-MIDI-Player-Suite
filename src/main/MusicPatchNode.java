package main;

import main.utils.Node;

public class MusicPatchNode extends Node {

    int volumeValue;
    MusicPatch patch;
    AudioBuffer audioBuffer;
    MusicPatchNode2 __w;
    int __o;
    int __u;
    int __g;
    int panValue;
    int __e;
    int __x;
    int __d;
    int __k;
    int __i;
    int __a;
    int __z;
    int __j;
    int __s;
    RawPcmStream stream;
    int __y;
    int __b;

    void clearAudioBuffer() {
        this.patch = null;
        this.audioBuffer = null;
        this.__w = null;
        this.stream = null;
    }
}
