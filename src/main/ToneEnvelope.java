package main;

import java.nio.ByteBuffer;

class ToneEnvelope {
    private int segments;
    private int[] durations;
    private int[] phases;
    int start;
    int end;
    int form;
    private int ticks;
    private int phaseIndex;
    private int step;
    private int max;
    private int amplitude;

    ToneEnvelope() {
        this.segments = 2;
        this.durations = new int[2];
        this.phases = new int[2];
        this.durations[0] = 0;
        this.durations[1] = 65535;
        this.phases[0] = 0;
        this.phases[1] = 65535;
    }

    final void decode(final ByteBuffer var1) {
        this.form = var1.get() & 0xFF;
        this.start = var1.getInt();
        this.end = var1.getInt();
        this.decodeSegments(var1);
    }

    final void decodeSegments(final ByteBuffer var1) {
        this.segments = var1.get() & 0xFF;
        this.durations = new int[this.segments];
        this.phases = new int[this.segments];

        for(int var2 = 0; var2 < this.segments; ++var2) {
            this.durations[var2] = var1.getShort() & 0xFFFF;
            this.phases[var2] = var1.getShort() & 0xFFFF;
        }

    }

    final void reset() {
        this.ticks = 0;
        this.phaseIndex = 0;
        this.step = 0;
        this.amplitude = 0;
        this.max = 0;
    }

    final int step(final int var1) {
        if(this.max >= this.ticks) {
            this.amplitude = this.phases[this.phaseIndex++] << 15;
            if(this.phaseIndex >= this.segments) {
                this.phaseIndex = this.segments - 1;
            }

            this.ticks = (int)(this.durations[this.phaseIndex] / 65536.0D * var1);
            if(this.ticks > this.max) {
                this.step = ((this.phases[this.phaseIndex] << 15) - this.amplitude) / (this.ticks - this.max);
            }
        }

        this.amplitude += this.step;
        ++this.max;
        return this.amplitude - this.step >> 15;
    }
}
