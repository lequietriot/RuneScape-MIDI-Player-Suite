package main;

import main.utils.Node;

public abstract class PcmStream extends Node {

    volatile boolean active;
    PcmStream after;
    int field1507;
    AbstractSound sound;

    protected PcmStream() {
        this.active = true;
    }

    protected abstract PcmStream firstSubStream();

    protected abstract PcmStream nextSubStream();

    protected abstract int vmethod3984();

    protected abstract void fill(int[] var1, int var2, int var3);

    protected abstract void skip(int var1);

    int vmethod2820() {
        return 255;
    }

    final void update(int[] var1, int var2, int var3) {
        if (this.active) {
            this.fill(var1, var2, var3);
        } else {
            this.skip(var3);
        }

    }
}
