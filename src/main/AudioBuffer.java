package main;

public class AudioBuffer extends AbstractSound {

    public int sampleRate;
    public byte[] samples;
    public int start;
    public int end;

    boolean bool;

    AudioBuffer(int sRate, byte[] data, int loopStart, int loopEnd) {
        sampleRate = sRate;
        samples = data;
        start = loopStart;
        end = loopEnd;
    }

    AudioBuffer(int sRate, byte[] data, int loopStart, int loopEnd, boolean effect) {
        sampleRate = sRate;
        samples = data;
        start = loopStart;
        end = loopEnd;
        bool = effect;
    }
}
