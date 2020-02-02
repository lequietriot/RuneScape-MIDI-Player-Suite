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

    public AudioBuffer resample(Resampler resampler) {
        this.samples = resampler.resample(this.samples);
        this.sampleRate = resampler.scaleRate(this.sampleRate);
        if(this.start == this.end) {
            this.start = this.end = resampler.scalePosition(this.start);
        } else {
            this.start = resampler.scalePosition(this.start);
            this.end = resampler.scalePosition(this.end);
            if(this.start == this.end) {
                --this.start;
            }
        }
        return this;
    }
}
