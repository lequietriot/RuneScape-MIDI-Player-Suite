package main;

import main.utils.ByteArrayNode;
import main.utils.NodeHashTable;
import org.displee.cache.index.Index;

import java.io.IOException;
import java.util.Hashtable;


public class MidiPcmStream extends PcmStream {

    NodeHashTable musicPatches;
    int volume;
    int tempoDivision;
    int[] volumeCtrlArray;
    int[] panCtrlArray;
    int[] expressionCtrlArray;
    int[] programChangeArray;
    int[] patchArray;
    int[] bankSelectArray;
    int[] pitchBendArray;
    int[] modulationCtrlArray;
    int[] portTimeCtrlArray;
    int[] switchArray;
    int[] rpnCtrlArray;
    int[] dataEntryMSBArray;
    int[] customEffectArray;
    int[] retriggerCustomArray;
    int[] percentageArray;
    MusicPatchNode[][] __v;
    MusicPatchNode[][] __ag;
    MidiFileReader midiFile;
    boolean __aj;
    int track;
    int trackLength;
    long __ac;
    long __ay;
    MusicPatchPcmStream patchStream;

    MidiPcmStream() {
        this.volume = 256;
        this.tempoDivision = 1000000;
        this.volumeCtrlArray = new int[16];
        this.panCtrlArray = new int[16];
        this.expressionCtrlArray = new int[16];
        this.programChangeArray = new int[16];
        this.patchArray = new int[16];
        this.bankSelectArray = new int[16];
        this.pitchBendArray = new int[16];
        this.modulationCtrlArray = new int[16];
        this.portTimeCtrlArray = new int[16];
        this.switchArray = new int[16];
        this.rpnCtrlArray = new int[16];
        this.dataEntryMSBArray = new int[16];
        this.customEffectArray = new int[16];
        this.retriggerCustomArray = new int[16];
        this.percentageArray = new int[16];
        this.__v = new MusicPatchNode[16][128];
        this.__ag = new MusicPatchNode[16][128];
        this.midiFile = new MidiFileReader();
        this.patchStream = new MusicPatchPcmStream(this);
        this.musicPatches = new NodeHashTable(128);
        this.__at_354();
    }

    synchronized void setMidiStreamVolume(int var1) {
        this.volume = var1;
    }

    public int getMidiStreamVolume() {
        return this.volume;
    }

    synchronized void loadMusicPatches(Index idx15, SoundBankCache soundBankCache, int patchID) {

        MusicPatch musicPatch = MusicPatch.getMusicPatch(idx15, patchID, 0);

        this.musicPatches.put(musicPatch, patchID);

        musicPatch.loadPatchSamples(soundBankCache, null, null);
        System.out.println("Loaded patch " + patchID);
    }

    synchronized boolean loadMusicTrack(MidiTrack var1, Index var2, SoundBankCache var3, int var4) {
        var1.loadMidiTrackInfo();
        boolean var5 = false;
        int[] var6 = null;
        if(var4 > 0) {
            var6 = new int[]{var4};
        }

        for(ByteArrayNode var7 = (ByteArrayNode)var1.table.first(); var7 != null; var7 = (ByteArrayNode)var1.table.next()) {
            int var8 = (int)var7.key;
            MusicPatch var9 = (MusicPatch)this.musicPatches.get(var8);
            if(var9 == null) {
                var9 = MusicPatch.getMusicPatch(var2, var8, 0);
                if(var9 == null) {
                    var5 = false;
                    continue;
                }

                this.musicPatches.put(var9, var8);
            }

            if(!var9.loadPatchSamples(var3, var7.byteArray, var6)) {
                var5 = false;
            }
        }

        if(var5) {
            var1.clear();
        }

        return var5;
    }

    public synchronized void clearAll() {

        int position = 0;
        for(MusicPatch var1 = (MusicPatch)this.musicPatches.get(position); var1 != null; var1 = (MusicPatch)this.musicPatches.get(position++)) {
            var1.clear();
        }

    }

    public synchronized void removeAll() {
        int position = 0;
        for(MusicPatch var1 = (MusicPatch)this.musicPatches.get(position); var1 != null; var1 = (MusicPatch)this.musicPatches.get(position++)) {
            var1.remove();
        }

    }

    protected synchronized PcmStream firstSubStream() {
        return this.patchStream;
    }

    protected synchronized PcmStream nextSubStream() {
        return null;
    }

    protected synchronized int __l_171() {
        return 0;
    }

    protected synchronized void __e_172(int[] var1, int var2, int var3) {
        if(this.midiFile.isReady()) {
            int var4 = this.midiFile.division * this.tempoDivision / AudioConstants.systemSampleRate;

            do {
                long var5 = (long)var4 * (long)var3 + this.__ac;
                if(this.__ay - var5 >= 0L) {
                    this.__ac = var5;
                    break;
                }

                int var7 = (int)(((long)var4 + (this.__ay - this.__ac) - 1L) / (long)var4);
                this.__ac += (long)var4 * (long)var7;
                this.patchStream.__e_172(var1, var2, var7);
                var2 += var7;
                var3 -= var7;
                this.__ai_367();
            } while(this.midiFile.isReady());
        }

        this.patchStream.__e_172(var1, var2, var3);
    }

    synchronized void setMusicTrack(MidiTrack var1, boolean var2) {
        this.clear();
        this.midiFile.parse(var1.midi);
        this.__aj = var2;
        this.__ac = 0L;
        int var3 = this.midiFile.trackCount();

        for(int var4 = 0; var4 < var3; ++var4) {
            this.midiFile.gotoTrack(var4);
            this.midiFile.readTrackLength(var4);
            this.midiFile.markTrackPosition(var4);
        }

        this.track = this.midiFile.getPrioritizedTrack();
        this.trackLength = this.midiFile.trackLengths[this.track];
        this.__ay = this.midiFile.__a_372(this.trackLength);
    }

    protected synchronized void __d_173(int var1) {
        if(this.midiFile.isReady()) {
            int var2 = this.midiFile.division * this.tempoDivision / AudioConstants.systemSampleRate;

            do {
                long var3 = this.__ac + (long)var2 * (long)var1;
                if(this.__ay - var3 >= 0L) {
                    this.__ac = var3;
                    break;
                }

                int var5 = (int)(((long)var2 + (this.__ay - this.__ac) - 1L) / (long)var2);
                this.__ac += (long)var5 * (long)var2;
                this.patchStream.__d_173(var5);
                var1 -= var5;
                this.__ai_367();
            } while(this.midiFile.isReady());
        }

        this.patchStream.__d_173(var1);
    }

    public synchronized void clear() {
        this.midiFile.clear();
        this.__at_354();
    }

    public synchronized boolean isReady() {
        return this.midiFile.isReady();
    }

    public synchronized void __j_342(int var1, int var2) {
        this.__s_343(var1, var2);
    }

    void __s_343(int var1, int var2) {
        this.programChangeArray[var1] = var2;
        this.bankSelectArray[var1] = var2 & -128;
        this.__t_344(var1, var2);
    }

    void __t_344(int var1, int var2) {
        if(var2 != this.patchArray[var1]) {
            this.patchArray[var1] = var2;

            for(int var3 = 0; var3 < 128; ++var3) {
                this.__ag[var1][var3] = null;
            }
        }

    }

    void __y_345(int var1, int var2, int var3) {
        this.__b_347(var1, var2, 64);
        if((this.switchArray[var1] & 2) != 0) {
            for(MusicPatchNode var4 = (MusicPatchNode) this.patchStream.queue.first(); var4 != null; var4 = (MusicPatchNode)this.patchStream.queue.next()) {
                if(var4.volumeValue == var1 && var4.__a < 0) {
                    this.__v[var1][var4.__u] = null;
                    this.__v[var1][var2] = var4;
                    int var5 = (var4.__d * var4.__x >> 12) + var4.__e;
                    var4.__e += var2 - var4.__u << 8;
                    var4.__x = var5 - var4.__e;
                    var4.__d = 4096;
                    var4.__u = var2;
                    return;
                }
            }
        }

        MusicPatch var9 = (MusicPatch)this.musicPatches.get((long)this.patchArray[var1]);
        if(var9 != null) {
            RawSound var8 = var9.rawSounds[var2];
            if(var8 != null) {
                MusicPatchNode var6 = new MusicPatchNode();
                var6.volumeValue = var1;
                var6.patch = var9;
                var6.rawSound = var8;
                var6.__w = var9.__u[var2];
                var6.__o = var9.l[var2];
                var6.__u = var2;
                var6.__g = var3 * var3 * var9.__w[var2] * var9.__m + 1024 >> 11;
                var6.panValue = var9.__o[var2] & 255;
                var6.__e = (var2 << 8) - (var9.l[var2] & 32767);
                var6.__k = 0;
                var6.__o = 0;
                var6.__i = 0;
                var6.__a = -1;
                var6.__z = 0;
                if(this.customEffectArray[var1] == 0) {
                    var6.stream = RawPcmStream.method2524(var8, this.__aa_359(var6), this.__ax_360(var6), this.__af_361(var6));
                } else {
                    var6.stream = RawPcmStream.method2524(var8, this.__aa_359(var6), 0, this.__af_361(var6));
                    this.__h_346(var6, var9.l[var2] < 0);
                }

                if(var9.l[var2] < 0) {
                    var6.stream.setLoopOnSample(-1);
                }

                if(var6.__o >= 0) {
                    MusicPatchNode var7 = this.__ag[var1][var6.__o];
                    if(var7 != null && var7.__a < 0) {
                        this.__v[var1][var7.__u] = null;
                        var7.__a = 0;
                    }

                    this.__ag[var1][var6.__o] = var6;
                }

                this.patchStream.queue.addFirst(var6);
                this.__v[var1][var2] = var6;
            }
        }
    }

    void __h_346(MusicPatchNode var1, boolean var2) {
        int var3 = var1.rawSound.samples.length;
        int var4;
        if(var2 && var1.rawSound.effectsEnabled) {
            int var5 = var3 + var3 - var1.rawSound.start;
            var4 = (int)((long)var5 * (long)this.customEffectArray[var1.volumeValue] >> 6);
            var3 <<= 8;
            if(var4 >= var3) {
                var4 = var3 + var3 - 1 - var4;
                var1.stream.__h_188();
            }
        } else {
            var4 = (int)((long)this.customEffectArray[var1.volumeValue] * (long)var3 >> 6);
        }

        var1.stream.__y_187(var4);
    }

    void __b_347(int var1, int var2, int var3) {
        MusicPatchNode var4 = this.__v[var1][var2];
        if(var4 != null) {
            this.__v[var1][var2] = null;
            if((this.switchArray[var1] & 2) != 0) {
                for(MusicPatchNode var5 = (MusicPatchNode)this.patchStream.queue.last(); var5 != null; var5 = (MusicPatchNode)this.patchStream.queue.previous()) {
                    if(var5.volumeValue == var4.volumeValue && var5.__a < 0 && var5 != var4) {
                        var4.__a = 0;
                        break;
                    }
                }
            } else {
                var4.__a = 0;
            }

        }
    }

    void __c_348(int var1, int var2, int var3) {
    }

    void __p_349(int var1, int var2) {
    }

    void __v_350(int var1, int var2) {
        this.pitchBendArray[var1] = var2;
    }

    void __ah_351(int var1) {
        for(MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
            if(var1 < 0 || var2.volumeValue == var1) {
                if(var2.stream != null) {
                    var2.stream.__v_192(AudioConstants.systemSampleRate / 100);
                    if(var2.stream.__at_196()) {
                        this.patchStream.mixer.addSubStream(var2.stream);
                    }

                    var2.__m_370();
                }

                if(var2.__a < 0) {
                    this.__v[var2.volumeValue][var2.__u] = null;
                }

                var2.remove();
            }
        }

    }

    void __ab_352(int var1) {
        if(var1 >= 0) {
            this.volumeCtrlArray[var1] = 12800;
            this.panCtrlArray[var1] = 8192;
            this.expressionCtrlArray[var1] = 16383;
            this.pitchBendArray[var1] = 8192;
            this.modulationCtrlArray[var1] = 0;
            this.portTimeCtrlArray[var1] = 8192;
            this.__ad_355(var1);
            this.__ap_356(var1);
            this.switchArray[var1] = 0;
            this.rpnCtrlArray[var1] = 32767;
            this.dataEntryMSBArray[var1] = 256;
            this.customEffectArray[var1] = 0;
            this.__ao_358(var1, 8192);
        } else {
            for(var1 = 0; var1 < 16; ++var1) {
                this.__ab_352(var1);
            }

        }
    }

    void __ae_353(int var1) {
        for(MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
            if((var1 < 0 || var2.volumeValue == var1) && var2.__a < 0) {
                this.__v[var2.volumeValue][var2.__u] = null;
                var2.__a = 0;
            }
        }

    }

    void __at_354() {
        this.__ah_351(-1);
        this.__ab_352(-1);

        int var1;
        for(var1 = 0; var1 < 16; ++var1) {
            this.patchArray[var1] = this.programChangeArray[var1];
        }

        for(var1 = 0; var1 < 16; ++var1) {
            this.bankSelectArray[var1] = this.programChangeArray[var1] & -128;
        }

    }

    void __ad_355(int var1) {
        if((this.switchArray[var1] & 2) != 0) {
            for(MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
                if(var2.volumeValue == var1 && this.__v[var1][var2.__u] == null && var2.__a < 0) {
                    var2.__a = 0;
                }
            }
        }

    }

    void __ap_356(int var1) {
        if((this.switchArray[var1] & 4) != 0) {
            for(MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
                if(var2.volumeValue == var1) {
                    var2.__b = 0;
                }
            }
        }

    }

    void __au_357(int var1) {
        int var2 = var1 & 240;
        int var3;
        int midiCtrl;
        int dataValue1;
        if(var2 == 128) {
            var3 = var1 & 15;
            midiCtrl = var1 >> 8 & 127;
            dataValue1 = var1 >> 16 & 127;
            this.__b_347(var3, midiCtrl, dataValue1);
        } else if(var2 == 144) {
            var3 = var1 & 15;
            midiCtrl = var1 >> 8 & 127;
            dataValue1 = var1 >> 16 & 127;
            if(dataValue1 > 0) {
                this.__y_345(var3, midiCtrl, dataValue1);
            } else {
                this.__b_347(var3, midiCtrl, 64);
            }

        } else if(var2 == 160) {
            var3 = var1 & 15;
            midiCtrl = var1 >> 8 & 127;
            dataValue1 = var1 >> 16 & 127;
            this.__c_348(var3, midiCtrl, dataValue1);
        } else if(var2 == 176) {
            var3 = var1 & 15;
            midiCtrl = var1 >> 8 & 127;
            dataValue1 = var1 >> 16 & 127;
            if(midiCtrl == 0) {
                this.bankSelectArray[var3] = (dataValue1 << 14) + (this.bankSelectArray[var3] & -2080769);
            }

            if(midiCtrl == 32) {
                this.bankSelectArray[var3] = (dataValue1 << 7) + (this.bankSelectArray[var3] & -16257);
            }

            if(midiCtrl == 1) {
                this.modulationCtrlArray[var3] = (dataValue1 << 7) + (this.modulationCtrlArray[var3] & -16257);
            }

            if(midiCtrl == 33) {
                this.modulationCtrlArray[var3] = dataValue1 + (this.modulationCtrlArray[var3] & -128);
            }

            if(midiCtrl == 5) {
                this.portTimeCtrlArray[var3] = (dataValue1 << 7) + (this.portTimeCtrlArray[var3] & -16257);
            }

            if(midiCtrl == 37) {
                this.portTimeCtrlArray[var3] = dataValue1 + (this.portTimeCtrlArray[var3] & -128);
            }

            if(midiCtrl == 7) {
                this.volumeCtrlArray[var3] = (dataValue1 << 7) + (this.volumeCtrlArray[var3] & -16257);
            }

            if(midiCtrl == 39) {
                this.volumeCtrlArray[var3] = dataValue1 + (this.volumeCtrlArray[var3] & -128);
            }

            if(midiCtrl == 10) {
                this.panCtrlArray[var3] = (dataValue1 << 7) + (this.panCtrlArray[var3] & -16257);
            }

            if(midiCtrl == 42) {
                this.panCtrlArray[var3] = dataValue1 + (this.panCtrlArray[var3] & -128);
            }

            if(midiCtrl == 11) {
                this.expressionCtrlArray[var3] = (dataValue1 << 7) + (this.expressionCtrlArray[var3] & -16257);
            }

            if(midiCtrl == 43) {
                this.expressionCtrlArray[var3] = dataValue1 + (this.expressionCtrlArray[var3] & -128);
            }

            if(midiCtrl == 64) {
                if(dataValue1 >= 64) {
                    this.switchArray[var3] |= 1;
                } else {
                    this.switchArray[var3] &= -2;
                }
            }

            if(midiCtrl == 65) {
                if(dataValue1 >= 64) {
                    this.switchArray[var3] |= 2;
                } else {
                    this.__ad_355(var3);
                    this.switchArray[var3] &= -3;
                }
            }

            if(midiCtrl == 99) {
                this.rpnCtrlArray[var3] = (dataValue1 << 7) + (this.rpnCtrlArray[var3] & 127);
            }

            if(midiCtrl == 98) {
                this.rpnCtrlArray[var3] = (this.rpnCtrlArray[var3] & 16256) + dataValue1;
            }

            if(midiCtrl == 101) {
                this.rpnCtrlArray[var3] = (dataValue1 << 7) + (this.rpnCtrlArray[var3] & 127) + 16384;
            }

            if(midiCtrl == 100) {
                this.rpnCtrlArray[var3] = (this.rpnCtrlArray[var3] & 16256) + dataValue1 + 16384;
            }

            if(midiCtrl == 120) {
                this.__ah_351(var3);
            }

            if(midiCtrl == 121) {
                this.__ab_352(var3);
            }

            if(midiCtrl == 123) {
                this.__ae_353(var3);
            }

            int var6;
            if(midiCtrl == 6) {
                var6 = this.rpnCtrlArray[var3];
                if(var6 == 16384) {
                    this.dataEntryMSBArray[var3] = (dataValue1 << 7) + (this.dataEntryMSBArray[var3] & -16257);
                }
            }

            if(midiCtrl == 38) {
                var6 = this.rpnCtrlArray[var3];
                if(var6 == 16384) {
                    this.dataEntryMSBArray[var3] = dataValue1 + (this.dataEntryMSBArray[var3] & -128);
                }
            }

            if(midiCtrl == 16) {
                this.customEffectArray[var3] = (dataValue1 << 7) + (this.customEffectArray[var3] & -16257);
            }

            if(midiCtrl == 48) {
                this.customEffectArray[var3] = dataValue1 + (this.customEffectArray[var3] & -128);
            }

            if(midiCtrl == 81) {
                if(dataValue1 >= 64) {
                    this.switchArray[var3] |= 4;
                } else {
                    this.__ap_356(var3);
                    this.switchArray[var3] &= -5;
                }
            }

            if(midiCtrl == 17) {
                this.__ao_358(var3, (dataValue1 << 7) + (this.retriggerCustomArray[var3] & -16257));
            }

            if(midiCtrl == 49) {
                this.__ao_358(var3, dataValue1 + (this.retriggerCustomArray[var3] & -128));
            }

        } else if(var2 == 192) {
            var3 = var1 & 15;
            midiCtrl = var1 >> 8 & 127;
            this.__t_344(var3, midiCtrl + this.bankSelectArray[var3]);
        } else if(var2 == 208) {
            var3 = var1 & 15;
            midiCtrl = var1 >> 8 & 127;
            this.__p_349(var3, midiCtrl);
        } else if(var2 == 224) {
            var3 = var1 & 15;
            midiCtrl = (var1 >> 8 & 127) + (var1 >> 9 & 16256);
            this.__v_350(var3, midiCtrl);
        } else {
            var2 = var1 & 255;
            if(var2 == 255) {
                this.__at_354();
            }
        }
    }

    void __ao_358(int var1, int var2) {
        this.retriggerCustomArray[var1] = var2;
        this.percentageArray[var1] = (int)(2097152.0D * Math.pow(2.0D, 5.4931640625E-4D * (double)var2) + 0.5D);
    }

    int __aa_359(MusicPatchNode var1) {
        int var2 = (var1.__d * var1.__x >> 12) + var1.__e;
        var2 += (this.pitchBendArray[var1.volumeValue] - 8192) * this.dataEntryMSBArray[var1.volumeValue] >> 12;
        MusicPatchNode2 var3 = var1.__w;
        int var4;
        if(var3.__x > 0 && (var3.__g > 0 || this.modulationCtrlArray[var1.volumeValue] > 0)) {
            var4 = var3.__g << 2;
            int var5 = var3.__e << 1;
            if(var1.__j < var5) {
                var4 = var4 * var1.__j / var5;
            }

            var4 += this.modulationCtrlArray[var1.volumeValue] >> 7;
            double var6 = Math.sin((double)(var1.__s & 511) * 0.01227184630308513D);
            var2 += (int)(var6 * (double)var4);
        }

        var4 = (int)((double)(var1.rawSound.sampleRate * 256) * Math.pow(2.0D, (double)var2 * 3.255208333333333E-4D) / (double)AudioConstants.systemSampleRate + 0.5D);
        return var4 < 1?1:var4;
    }

    int __ax_360(MusicPatchNode var1) {
        MusicPatchNode2 var2 = var1.__w;
        int var3 = this.volumeCtrlArray[var1.volumeValue] * this.expressionCtrlArray[var1.volumeValue] + 4096 >> 13;
        var3 = var3 * var3 + 16384 >> 15;
        var3 = var3 * var1.__g + 16384 >> 15;
        var3 = var3 * this.volume + 128 >> 8;
        if(var2.__q > 0) {
            var3 = (int)((double)var3 * Math.pow(0.5D, (double)var2.__q * (double)var1.__k * 1.953125E-5D) + 0.5D);
        }

        int var4;
        int var5;
        int var6;
        int var7;
        if(var2.__m != null) {
            var4 = var1.__o;
            var5 = var2.__m[var1.__i + 1];
            if(var1.__i < var2.__m.length - 2) {
                var6 = (var2.__m[var1.__i] & 255) << 8;
                var7 = (var2.__m[var1.__i + 2] & 255) << 8;
                var5 += (var2.__m[var1.__i + 3] - var5) * (var4 - var6) / (var7 - var6);
            }

            var3 = var5 * var3 + 32 >> 6;
        }

        if(var1.__a > 0 && var2.__f != null) {
            var4 = var1.__a;
            var5 = var2.__f[var1.__z + 1];
            if(var1.__z < var2.__f.length - 2) {
                var6 = (var2.__f[var1.__z] & 255) << 8;
                var7 = (var2.__f[var1.__z + 2] & 255) << 8;
                var5 += (var4 - var6) * (var2.__f[var1.__z + 3] - var5) / (var7 - var6);
            }

            var3 = var3 * var5 + 32 >> 6;
        }

        return var3;
    }

    int __af_361(MusicPatchNode var1) {
        int var2 = this.panCtrlArray[var1.volumeValue];
        return var2 < 8192?var2 * var1.panValue + 32 >> 6:16384 - ((128 - var1.panValue) * (16384 - var2) + 32 >> 6);
    }

    void __ai_367() {
        int var1 = this.track;
        int var2 = this.trackLength;

        long var3;
        for(var3 = this.__ay; var2 == this.trackLength; var3 = this.midiFile.__a_372(var2)) {
            while(var2 == this.midiFile.trackLengths[var1]) {
                this.midiFile.gotoTrack(var1);
                int var5 = this.midiFile.readMessage(var1);
                if(var5 == 1) {
                    this.midiFile.setTrackDone();
                    this.midiFile.markTrackPosition(var1);
                    if(this.midiFile.isDone()) {
                        if(!this.__aj || var2 == 0) {
                            this.__at_354();
                            this.midiFile.clear();
                            return;
                        }

                        this.midiFile.reset(var3);
                    }
                    break;
                }

                if((var5 & 128) != 0) {
                    this.__au_357(var5);
                }

                this.midiFile.readTrackLength(var1);
                this.midiFile.markTrackPosition(var1);
            }

            var1 = this.midiFile.getPrioritizedTrack();
            var2 = this.midiFile.trackLengths[var1];
        }

        this.track = var1;
        this.trackLength = var2;
        this.__ay = var3;
    }

    boolean __ba_368(MusicPatchNode var1) {
        if(var1.stream == null) {
            if(var1.__a >= 0) {
                var1.remove();
                if(var1.__o > 0 && var1 == this.__ag[var1.volumeValue][var1.__o]) {
                    this.__ag[var1.volumeValue][var1.__o] = null;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    boolean __bb_369(MusicPatchNode var1, int[] var2, int var3, int var4) {
        var1.__y = AudioConstants.systemSampleRate / 100;
        if(var1.__a < 0 || var1.stream != null && !var1.stream.__ae_195()) {
            int var5 = var1.__d;
            if(var5 > 0) {
                var5 -= (int)(16.0D * Math.pow(2.0D, (double)this.portTimeCtrlArray[var1.volumeValue] * 4.921259842519685E-4D) + 0.5D);
                if(var5 < 0) {
                    var5 = 0;
                }

                var1.__d = var5;
            }

            var1.stream.__ah_193(this.__aa_359(var1));
            MusicPatchNode2 var6 = var1.__w;
            boolean var7 = false;
            ++var1.__j;
            var1.__s += var6.__x;
            double var8 = 5.086263020833333E-6D * (double)((var1.__u - 60 << 8) + (var1.__x * var1.__d >> 12));
            if(var6.__q > 0) {
                if(var6.__u > 0) {
                    var1.__k += (int)(128.0D * Math.pow(2.0D, var8 * (double)var6.__u) + 0.5D);
                } else {
                    var1.__k += 128;
                }
            }

            if(var6.__m != null) {
                if(var6.__w > 0) {
                    var1.__o += (int)(128.0D * Math.pow(2.0D, var8 * (double)var6.__w) + 0.5D);
                } else {
                    var1.__o += 128;
                }

                while(var1.__i < var6.__m.length - 2 && var1.__o > (var6.__m[var1.__i + 2] & 255) << 8) {
                    var1.__i += 2;
                }

                if(var6.__m.length - 2 == var1.__i && var6.__m[var1.__i + 1] == 0) {
                    var7 = true;
                }
            }

            if(var1.__a >= 0 && var6.__f != null && (this.switchArray[var1.volumeValue] & 1) == 0 && (var1.__o < 0 || var1 != this.__ag[var1.volumeValue][var1.__o])) {
                if(var6.__o > 0) {
                    var1.__a += (int)(128.0D * Math.pow(2.0D, var8 * (double)var6.__o) + 0.5D);
                } else {
                    var1.__a += 128;
                }

                while(var1.__z < var6.__f.length - 2 && var1.__a > (var6.__f[var1.__z + 2] & 255) << 8) {
                    var1.__z += 2;
                }

                if(var6.__f.length - 2 == var1.__z) {
                    var7 = true;
                }
            }

            if(var7) {
                var1.stream.__v_192(var1.__y);
                if(var2 != null) {
                    var1.stream.__e_172(var2, var3, var4);
                } else {
                    var1.stream.__d_173(var4);
                }

                if(var1.stream.__at_196()) {
                    this.patchStream.mixer.addSubStream(var1.stream);
                }

                var1.__m_370();
                if(var1.__a >= 0) {
                    var1.remove();
                    if(var1.__o > 0 && var1 == this.__ag[var1.volumeValue][var1.__o]) {
                        this.__ag[var1.volumeValue][var1.__o] = null;
                    }
                }

                return true;
            } else {
                var1.stream.__p_191(var1.__y, this.__ax_360(var1), this.__af_361(var1));
                return false;
            }
        } else {
            var1.__m_370();
            var1.remove();
            if(var1.__o > 0 && var1 == this.__ag[var1.volumeValue][var1.__o]) {
                this.__ag[var1.volumeValue][var1.__o] = null;
            }

            return true;
        }
    }

    static final void PcmStream_disable(PcmStream var0) {
        var0.active = false;
        if(var0.sound != null) {
            var0.sound.position = 0;
        }

        for(PcmStream var1 = var0.firstSubStream(); var1 != null; var1 = var0.nextSubStream()) {
            PcmStream_disable(var1);
        }

    }

}