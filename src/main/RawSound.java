package main;

public class RawSound extends AbstractSound {

    public int sampleRate;
    public byte[] samples;
    public int start;
    public int end;

    boolean effectsEnabled;

    RawSound(int sRate, byte[] data, int loopStart, int loopEnd) {
        sampleRate = sRate;
        samples = data;
        start = loopStart;
        end = loopEnd;
    }

    RawSound(int sRate, byte[] data, int loopStart, int loopEnd, boolean effect) {
        sampleRate = sRate;
        samples = data;
        start = loopStart;
        end = loopEnd;
        effectsEnabled = effect;
    }
}
