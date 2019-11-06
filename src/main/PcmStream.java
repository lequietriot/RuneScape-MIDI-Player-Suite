package main;

import main.utils.Node;

public abstract class PcmStream extends Node {

    volatile boolean active;
    PcmStream after;
    int __s;
    AbstractSound sound;

    protected PcmStream() {
        this.active = true;
    }

    protected abstract PcmStream firstSubStream();

    protected abstract PcmStream nextSubStream();

    protected abstract int __l_171();

    protected abstract void __e_172(int[] var1, int var2, int var3);

    protected abstract void __d_173(int var1);

    int __az_179() {
        return 255;
    }

    final void update(int[] var1, int var2, int var3) {
        if(this.active) {
            this.__e_172(var1, var2, var3);
        } else {
            this.__d_173(var3);
        }

    }
}
