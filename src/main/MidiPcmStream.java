package main;

import main.utils.ByteArrayNode;
import main.utils.NodeHashTable;
import org.displee.cache.index.Index;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MidiPcmStream extends PcmStream {

    NodeHashTable musicPatches;
    int field2415;
    int field2419;
    int[] field2443;
    int[] field2416;
    int[] field2417;
    int[] field2420;
    int[] field2421;
    int[] field2422;
    int[] field2423;
    int[] field2424;
    int[] field2437;
    int[] field2428;
    int[] field2429;
    int[] field2430;
    int[] field2431;
    int[] field2432;
    int[] field2433;
    MusicPatchNode[][] field2441;
    MusicPatchNode[][] field2435;
    MidiFileReader midiFile;
    boolean field2418;
    int track;
    int trackLength;
    long field2440;
    long field2425;
    MusicPatchPcmStream patchStream;

    public MidiPcmStream() {
        this.field2415 = 256;
        this.field2419 = 1000000;
        this.field2443 = new int[16];
        this.field2416 = new int[16];
        this.field2417 = new int[16];
        this.field2420 = new int[16];
        this.field2421 = new int[16];
        this.field2422 = new int[16];
        this.field2423 = new int[16];
        this.field2424 = new int[16];
        this.field2437 = new int[16];
        this.field2428 = new int[16];
        this.field2429 = new int[16];
        this.field2430 = new int[16];
        this.field2431 = new int[16];
        this.field2432 = new int[16];
        this.field2433 = new int[16];
        this.field2441 = new MusicPatchNode[16][128];
        this.field2435 = new MusicPatchNode[16][128];
        this.midiFile = new MidiFileReader();
        this.patchStream = new MusicPatchPcmStream(this);
        this.musicPatches = new NodeHashTable(128);
        this.method3812();
    }

    public synchronized void setPcmStreamVolume(int var1) {
        this.field2415 = var1;
    }

    public int method3793() {
        return this.field2415;
    }

    public synchronized boolean loadMusicTrack(MidiTrack var1, Index idx15, SoundBankCache var3, int var4) {
        var1.loadMidiTrackInfo();
        boolean var5 = true;
        int[] var6 = null;
        if (var4 > 0) {
            var6 = new int[]{var4};
        }

        for (ByteArrayNode var7 = (ByteArrayNode)var1.table.first(); var7 != null; var7 = (ByteArrayNode)var1.table.next()) {
            int var8 = (int)var7.key;
            MusicPatch var9 = (MusicPatch)this.musicPatches.get((long)var8);
            if (var9 == null) {
                var9 = MusicPatch.getMusicPatch(idx15, var8, 0);
                if (var9 == null) {
                    var5 = false;
                    continue;
                }

                this.musicPatches.put(var9, (long)var8);
            }

            if (!var9.loadPatchSamples(var3, var7.byteArray, var6)) {
                var5 = false;
            }
        }

        if (var5) {
            var1.clear();
        }

        return var5;
    }

    public synchronized void clearAll() {
        for (MusicPatch var1 = (MusicPatch)this.musicPatches.first(); var1 != null; var1 = (MusicPatch)this.musicPatches.next()) {
            var1.clear();
        }

    }

    public synchronized void removeAll() {
        for (MusicPatch var1 = (MusicPatch)this.musicPatches.first(); var1 != null; var1 = (MusicPatch)this.musicPatches.next()) {
            var1.remove();
        }

    }

    protected synchronized PcmStream firstSubStream() {
        return this.patchStream;
    }

    protected synchronized PcmStream nextSubStream() {
        return null;
    }

    protected synchronized int vmethod3984() {
        return 0;
    }

    protected synchronized void fill(int[] var1, int var2, int var3) {
        if (this.midiFile.isReady()) {
            int var4 = this.midiFile.division * this.field2419 / PcmPlayer.pcmPlayer_sampleRate;

            do {
                long var5 = (long)var4 * (long)var3 + this.field2440;
                if (this.field2425 - var5 >= 0L) {
                    this.field2440 = var5;
                    break;
                }

                int var7 = (int)(((long)var4 + (this.field2425 - this.field2440) - 1L) / (long)var4);
                this.field2440 += (long)var7 * (long)var4;
                this.patchStream.fill(var1, var2, var7);
                var2 += var7;
                var3 -= var7;
                this.method3825();
            } while(this.midiFile.isReady());
        }

        this.patchStream.fill(var1, var2, var3);
    }

    public synchronized void setMusicTrack(MidiTrack var1, boolean var2) {
        this.clear();
        this.midiFile.parse(var1.midi);
        this.field2418 = var2;
        this.field2440 = 0L;
        int var3 = this.midiFile.trackCount();

        for (int var4 = 0; var4 < var3; ++var4) {
            this.midiFile.gotoTrack(var4);
            this.midiFile.readTrackLength(var4);
            this.midiFile.markTrackPosition(var4);
        }

        this.track = this.midiFile.getPrioritizedTrack();
        this.trackLength = this.midiFile.trackLengths[this.track];
        this.field2425 = this.midiFile.method3935(this.trackLength);
    }

    protected synchronized void skip(int var1) {
        if (this.midiFile.isReady()) {
            int var2 = this.midiFile.division * this.field2419 / PcmPlayer.pcmPlayer_sampleRate;

            do {
                long var3 = this.field2440 + (long)var1 * (long)var2;
                if (this.field2425 - var3 >= 0L) {
                    this.field2440 = var3;
                    break;
                }

                int var5 = (int)((this.field2425 - this.field2440 + (long)var2 - 1L) / (long)var2);
                this.field2440 += (long)var5 * (long)var2;
                this.patchStream.skip(var5);
                var1 -= var5;
                this.method3825();
            } while(this.midiFile.isReady());
        }

        this.patchStream.skip(var1);
    }

    public synchronized void clear() {
        this.midiFile.clear();
        this.method3812();
    }

    public synchronized boolean isReady() {
        return this.midiFile.isReady();
    }

    public synchronized void method3800(int var1, int var2) {
        this.method3801(var1, var2);
    }

    void method3801(int var1, int var2) {
        this.field2420[var1] = var2;
        this.field2422[var1] = var2 & -128;
        this.method3802(var1, var2);
    }

    void method3802(int var1, int var2) {
        if (var2 != this.field2421[var1]) {
            this.field2421[var1] = var2;

            for (int var3 = 0; var3 < 128; ++var3) {
                this.field2435[var1][var3] = null;
            }
        }

    }

    void method3808(int var1, int var2, int var3) {
        this.method3805(var1, var2, 64);
        if ((this.field2428[var1] & 2) != 0) {
            for (MusicPatchNode var4 = (MusicPatchNode)this.patchStream.queue.first(); var4 != null; var4 = (MusicPatchNode)this.patchStream.queue.next()) {
                if (var4.field2452 == var1 && var4.field2459 < 0) {
                    this.field2441[var1][var4.field2464] = null;
                    this.field2441[var1][var2] = var4;
                    int var5 = (var4.field2455 * var4.field2454 >> 12) + var4.field2445;
                    var4.field2445 += var2 - var4.field2464 << 8;
                    var4.field2454 = var5 - var4.field2445;
                    var4.field2455 = 4096;
                    var4.field2464 = var2;
                    return;
                }
            }
        }

        MusicPatch var9 = (MusicPatch)this.musicPatches.get((long)this.field2421[var1]);
        if (var9 != null) {
            AudioBuffer var8 = var9.audioBuffers[var2];
            if (var8 != null) {
                MusicPatchNode var6 = new MusicPatchNode();
                var6.field2452 = var1;
                var6.patch = var9;
                var6.audioBuffer = var8;
                var6.musicPatchInfo = var9.musicPatchNode2[var2];
                var6.field2467 = var9.loopMode[var2];
                var6.field2464 = var2;
                var6.field2451 = var3 * var3 * var9.volume[var2] * var9.panOffset[var2] + 1024 >> 11;
                var6.field2465 = var9.panOffset[var2] & 255;
                var6.field2445 = (var2 << 8) - (var9.pitchOffset[var2] & 32767);
                var6.field2456 = 0;
                var6.field2457 = 0;
                var6.field2458 = 0;
                var6.field2459 = -1;
                var6.field2448 = 0;
                if (this.field2431[var1] == 0) {
                    var6.stream = RawPcmStream.method2685(var8, this.method3864(var6), this.method3818(var6), this.method3819(var6));
                } else {
                    var6.stream = RawPcmStream.method2685(var8, this.method3864(var6), 0, this.method3819(var6));
                    this.method3852(var6, var9.pitchOffset[var2] < 0);
                }

                if (var9.pitchOffset[var2] < 0) {
                    var6.stream.setNumLoops(-1);
                }

                if (var6.field2467 >= 0) {
                    MusicPatchNode var7 = this.field2435[var1][var6.field2467];
                    if (var7 != null && var7.field2459 < 0) {
                        this.field2441[var1][var7.field2464] = null;
                        var7.field2459 = 0;
                    }

                    this.field2435[var1][var6.field2467] = var6;
                }

                this.patchStream.queue.addFirst(var6);
                this.field2441[var1][var2] = var6;
            }
        }
    }

    void method3852(MusicPatchNode var1, boolean var2) {
        int var3 = var1.audioBuffer.samples.length;
        int var4;
        if (var2 && var1.audioBuffer.enableLoop) {
            int var5 = var3 + var3 - var1.audioBuffer.start;
            var4 = (int)((long)this.field2431[var1.field2452] * (long)var5 >> 6);
            var3 <<= 8;
            if (var4 >= var3) {
                var4 = var3 + var3 - 1 - var4;
                var1.stream.method2655();
            }
        } else {
            var4 = (int)((long)var3 * (long)this.field2431[var1.field2452] >> 6);
        }

        var1.stream.method2664(var4);
    }

    void method3805(int var1, int var2, int var3) {
        MusicPatchNode var4 = this.field2441[var1][var2];
        if (var4 != null) {
            this.field2441[var1][var2] = null;
            if ((this.field2428[var1] & 2) != 0) {
                for (MusicPatchNode var5 = (MusicPatchNode)this.patchStream.queue.last(); var5 != null; var5 = (MusicPatchNode)this.patchStream.queue.previous()) {
                    if (var5.field2452 == var4.field2452 && var5.field2459 < 0 && var4 != var5) {
                        var4.field2459 = 0;
                        break;
                    }
                }
            } else {
                var4.field2459 = 0;
            }

        }
    }

    void method3900(int var1, int var2, int var3) {
    }

    void method3817(int var1, int var2) {
    }

    void method3799(int var1, int var2) {
        this.field2423[var1] = var2;
    }

    void method3809(int var1) {
        for (MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
            if (var1 < 0 || var2.field2452 == var1) {
                if (var2.stream != null) {
                    var2.stream.method2706(PcmPlayer.pcmPlayer_sampleRate / 100);
                    if (var2.stream.method2672()) {
                        this.patchStream.mixer.addSubStream(var2.stream);
                    }

                    var2.clearMusicPatchNode();
                }

                if (var2.field2459 < 0) {
                    this.field2441[var2.field2452][var2.field2464] = null;
                }

                var2.remove();
            }
        }

    }

    void method3810(int var1) {
        if (var1 >= 0) {
            this.field2443[var1] = 12800;
            this.field2416[var1] = 8192;
            this.field2417[var1] = 16383;
            this.field2423[var1] = 8192;
            this.field2424[var1] = 0;
            this.field2437[var1] = 8192;
            this.method3813(var1);
            this.method3916(var1);
            this.field2428[var1] = 0;
            this.field2429[var1] = 32767;
            this.field2430[var1] = 256;
            this.field2431[var1] = 0;
            this.method3899(var1, 8192);
        } else {
            for (var1 = 0; var1 < 16; ++var1) {
                this.method3810(var1);
            }

        }
    }

    void method3840(int var1) {
        for (MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
            if ((var1 < 0 || var2.field2452 == var1) && var2.field2459 < 0) {
                this.field2441[var2.field2452][var2.field2464] = null;
                var2.field2459 = 0;
            }
        }

    }

    void method3812() {
        this.method3809(-1);
        this.method3810(-1);

        int var1;
        for (var1 = 0; var1 < 16; ++var1) {
            this.field2421[var1] = this.field2420[var1];
        }

        for (var1 = 0; var1 < 16; ++var1) {
            this.field2422[var1] = this.field2420[var1] & -128;
        }

    }

    void method3813(int var1) {
        if ((this.field2428[var1] & 2) != 0) {
            for (MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
                if (var2.field2452 == var1 && this.field2441[var1][var2.field2464] == null && var2.field2459 < 0) {
                    var2.field2459 = 0;
                }
            }
        }

    }

    void method3916(int var1) {
        if ((this.field2428[var1] & 4) != 0) {
            for (MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
                if (var2.field2452 == var1) {
                    var2.field2462 = 0;
                }
            }
        }

    }

    void method3815(int var1) {
        int var2 = var1 & 240;
        int var3;
        int var4;
        int var5;
        if (var2 == 128) {
            var3 = var1 & 15;
            var4 = var1 >> 8 & 127;
            var5 = var1 >> 16 & 127;
            this.method3805(var3, var4, var5);
        } else if (var2 == 144) {
            var3 = var1 & 15;
            var4 = var1 >> 8 & 127;
            var5 = var1 >> 16 & 127;
            if (var5 > 0) {
                this.method3808(var3, var4, var5);
            } else {
                this.method3805(var3, var4, 64);
            }

        } else if (var2 == 160) {
            var3 = var1 & 15;
            var4 = var1 >> 8 & 127;
            var5 = var1 >> 16 & 127;
            this.method3900(var3, var4, var5);
        } else if (var2 == 176) {
            var3 = var1 & 15;
            var4 = var1 >> 8 & 127;
            var5 = var1 >> 16 & 127;
            if (var4 == 0) {
                this.field2422[var3] = (var5 << 14) + (this.field2422[var3] & -2080769);
            }

            if (var4 == 32) {
                this.field2422[var3] = (var5 << 7) + (this.field2422[var3] & -16257);
            }

            if (var4 == 1) {
                this.field2424[var3] = (var5 << 7) + (this.field2424[var3] & -16257);
            }

            if (var4 == 33) {
                this.field2424[var3] = var5 + (this.field2424[var3] & -128);
            }

            if (var4 == 5) {
                this.field2437[var3] = (var5 << 7) + (this.field2437[var3] & -16257);
            }

            if (var4 == 37) {
                this.field2437[var3] = var5 + (this.field2437[var3] & -128);
            }

            if (var4 == 7) {
                this.field2443[var3] = (var5 << 7) + (this.field2443[var3] & -16257);
            }

            if (var4 == 39) {
                this.field2443[var3] = var5 + (this.field2443[var3] & -128);
            }

            if (var4 == 10) {
                this.field2416[var3] = (var5 << 7) + (this.field2416[var3] & -16257);
            }

            if (var4 == 42) {
                this.field2416[var3] = var5 + (this.field2416[var3] & -128);
            }

            if (var4 == 11) {
                this.field2417[var3] = (var5 << 7) + (this.field2417[var3] & -16257);
            }

            if (var4 == 43) {
                this.field2417[var3] = var5 + (this.field2417[var3] & -128);
            }

            int[] var10000;
            if (var4 == 64) {
                if (var5 >= 64) {
                    var10000 = this.field2428;
                    var10000[var3] |= 1;
                } else {
                    var10000 = this.field2428;
                    var10000[var3] &= -2;
                }
            }

            if (var4 == 65) {
                if (var5 >= 64) {
                    var10000 = this.field2428;
                    var10000[var3] |= 2;
                } else {
                    this.method3813(var3);
                    var10000 = this.field2428;
                    var10000[var3] &= -3;
                }
            }

            if (var4 == 99) {
                this.field2429[var3] = (var5 << 7) + (this.field2429[var3] & 127);
            }

            if (var4 == 98) {
                this.field2429[var3] = (this.field2429[var3] & 16256) + var5;
            }

            if (var4 == 101) {
                this.field2429[var3] = (var5 << 7) + (this.field2429[var3] & 127) + 16384;
            }

            if (var4 == 100) {
                this.field2429[var3] = (this.field2429[var3] & 16256) + var5 + 16384;
            }

            if (var4 == 120) {
                this.method3809(var3);
            }

            if (var4 == 121) {
                this.method3810(var3);
            }

            if (var4 == 123) {
                this.method3840(var3);
            }

            int var6;
            if (var4 == 6) {
                var6 = this.field2429[var3];
                if (var6 == 16384) {
                    this.field2430[var3] = (var5 << 7) + (this.field2430[var3] & -16257);
                }
            }

            if (var4 == 38) {
                var6 = this.field2429[var3];
                if (var6 == 16384) {
                    this.field2430[var3] = var5 + (this.field2430[var3] & -128);
                }
            }

            if (var4 == 16) {
                this.field2431[var3] = (var5 << 7) + (this.field2431[var3] & -16257);
            }

            if (var4 == 48) {
                this.field2431[var3] = var5 + (this.field2431[var3] & -128);
            }

            if (var4 == 81) {
                if (var5 >= 64) {
                    var10000 = this.field2428;
                    var10000[var3] |= 4;
                } else {
                    this.method3916(var3);
                    var10000 = this.field2428;
                    var10000[var3] &= -5;
                }
            }

            if (var4 == 17) {
                this.method3899(var3, (var5 << 7) + (this.field2432[var3] & -16257));
            }

            if (var4 == 49) {
                this.method3899(var3, var5 + (this.field2432[var3] & -128));
            }

        } else if (var2 == 192) {
            var3 = var1 & 15;
            var4 = var1 >> 8 & 127;
            this.method3802(var3, var4 + this.field2422[var3]);
        } else if (var2 == 208) {
            var3 = var1 & 15;
            var4 = var1 >> 8 & 127;
            this.method3817(var3, var4);
        } else if (var2 == 224) {
            var3 = var1 & 15;
            var4 = (var1 >> 8 & 127) + (var1 >> 9 & 16256);
            this.method3799(var3, var4);
        } else {
            var2 = var1 & 255;
            if (var2 == 255) {
                this.method3812();
            }
        }
    }

    void method3899(int var1, int var2) {
        this.field2432[var1] = var2;
        this.field2433[var1] = (int)(2097152.0D * Math.pow(2.0D, (double)var2 * 5.4931640625E-4D) + 0.5D);
    }

    int method3864(MusicPatchNode var1) {
        int var2 = (var1.field2454 * var1.field2455 >> 12) + var1.field2445;
        var2 += (this.field2423[var1.field2452] - 8192) * this.field2430[var1.field2452] >> 12;
        MusicPatchNode2 var3 = var1.musicPatchInfo;
        int var4;
        if (var3.field2401 > 0 && (var3.vibratoLFOPitch > 0 || this.field2424[var1.field2452] > 0)) {
            var4 = var3.vibratoLFOPitch << 2;
            int var5 = var3.field2394 << 1;
            if (var1.field2461 < var5) {
                var4 = var4 * var1.field2461 / var5;
            }

            var4 += this.field2424[var1.field2452] >> 7;
            double var6 = Math.sin(0.01227184630308513D * (double)(var1.field2449 & 511));
            var2 += (int)((double)var4 * var6);
        }

        var4 = (int)((double)(var1.audioBuffer.sampleRate * 256) * Math.pow(2.0D, (double)var2 * 3.255208333333333E-4D) / (double)PcmPlayer.pcmPlayer_sampleRate + 0.5D);
        return var4 < 1 ? 1 : var4;
    }

    int method3818(MusicPatchNode var1) {
        MusicPatchNode2 var2 = var1.musicPatchInfo;
        int var3 = this.field2443[var1.field2452] * this.field2417[var1.field2452] + 4096 >> 13;
        var3 = var3 * var3 + 16384 >> 15;
        var3 = var3 * var1.field2451 + 16384 >> 15;
        var3 = var3 * this.field2415 + 128 >> 8;
        if (var2.volEnvDecay > 0) {
            var3 = (int)((double)var3 * Math.pow(0.5D, (double)var2.volEnvDecay * (double)var1.field2456 * 1.953125E-5D) + 0.5D);
        }

        int var4;
        int var5;
        int var6;
        int var7;
        if (var2.field2402 != null) {
            var4 = var1.field2457;
            var5 = var2.field2402[var1.field2458 + 1];
            if (var1.field2458 < var2.field2402.length - 2) {
                var6 = (var2.field2402[var1.field2458] & 255) << 8;
                var7 = (var2.field2402[var1.field2458 + 2] & 255) << 8;
                var5 += (var4 - var6) * (var2.field2402[var1.field2458 + 3] - var5) / (var7 - var6);
            }

            var3 = var3 * var5 + 32 >> 6;
        }

        if (var1.field2459 > 0 && var2.field2398 != null) {
            var4 = var1.field2459;
            var5 = var2.field2398[var1.field2448 + 1];
            if (var1.field2448 < var2.field2398.length - 2) {
                var6 = (var2.field2398[var1.field2448] & 255) << 8;
                var7 = (var2.field2398[var1.field2448 + 2] & 255) << 8;
                var5 += (var4 - var6) * (var2.field2398[var1.field2448 + 3] - var5) / (var7 - var6);
            }

            var3 = var5 * var3 + 32 >> 6;
        }

        return var3;
    }

    int method3819(MusicPatchNode var1) {
        int var2 = this.field2416[var1.field2452];
        return var2 < 8192 ? var2 * var1.field2465 + 32 >> 6 : 16384 - ((128 - var1.field2465) * (16384 - var2) + 32 >> 6);
    }

    void method3825() {
        int var1 = this.track;
        int var2 = this.trackLength;

        long var3;
        for (var3 = this.field2425; var2 == this.trackLength; var3 = this.midiFile.method3935(var2)) {
            while (var2 == this.midiFile.trackLengths[var1]) {
                this.midiFile.gotoTrack(var1);
                int var5 = this.midiFile.readMessage(var1);
                if (var5 == 1) {
                    this.midiFile.setTrackDone();
                    this.midiFile.markTrackPosition(var1);
                    if (this.midiFile.isDone()) {
                        if (!this.field2418 || var2 == 0) {
                            this.method3812();
                            this.midiFile.clear();
                            return;
                        }

                        this.midiFile.reset(var3);
                    }
                    break;
                }

                if ((var5 & 128) != 0) {
                    this.method3815(var5);
                }

                this.midiFile.readTrackLength(var1);
                this.midiFile.markTrackPosition(var1);
            }

            var1 = this.midiFile.getPrioritizedTrack();
            var2 = this.midiFile.trackLengths[var1];
        }

        this.track = var1;
        this.trackLength = var2;
        this.field2425 = var3;
    }

    boolean method3826(MusicPatchNode var1) {
        if (var1.stream == null) {
            if (var1.field2459 >= 0) {
                var1.remove();
                if (var1.field2467 > 0 && var1 == this.field2435[var1.field2452][var1.field2467]) {
                    this.field2435[var1.field2452][var1.field2467] = null;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    boolean method3884(MusicPatchNode var1, int[] var2, int var3, int var4) {
        var1.field2453 = PcmPlayer.pcmPlayer_sampleRate / 100;
        if (var1.field2459 < 0 || var1.stream != null && !var1.stream.method2671()) {
            int var5 = var1.field2455;
            if (var5 > 0) {
                var5 -= (int)(16.0D * Math.pow(2.0D, (double)this.field2437[var1.field2452] * 4.921259842519685E-4D) + 0.5D);
                if (var5 < 0) {
                    var5 = 0;
                }

                var1.field2455 = var5;
            }

            var1.stream.method2669(this.method3864(var1));
            MusicPatchNode2 var6 = var1.musicPatchInfo;
            boolean var7 = false;
            ++var1.field2461;
            var1.field2449 += var6.field2401;
            double var8 = (double)((var1.field2464 - 60 << 8) + (var1.field2455 * var1.field2454 >> 12)) * 5.086263020833333E-6D;
            if (var6.volEnvDecay > 0) {
                if (var6.vibratoLFOFrequency > 0) {
                    var1.field2456 += (int)(128.0D * Math.pow(2.0D, (double)var6.vibratoLFOFrequency * var8) + 0.5D);
                } else {
                    var1.field2456 += 128;
                }
            }

            if (var6.field2402 != null) {
                if (var6.volEnvAttack > 0) {
                    var1.field2457 += (int)(128.0D * Math.pow(2.0D, (double)var6.volEnvAttack * var8) + 0.5D);
                } else {
                    var1.field2457 += 128;
                }

                while (var1.field2458 < var6.field2402.length - 2 && var1.field2457 > (var6.field2402[var1.field2458 + 2] & 255) << 8) {
                    var1.field2458 += 2;
                }

                if (var6.field2402.length - 2 == var1.field2458 && var6.field2402[var1.field2458 + 1] == 0) {
                    var7 = true;
                }
            }

            if (var1.field2459 >= 0 && var6.field2398 != null && (this.field2428[var1.field2452] & 1) == 0 && (var1.field2467 < 0 || var1 != this.field2435[var1.field2452][var1.field2467])) {
                if (var6.vibratoLFODelay > 0) {
                    var1.field2459 += (int)(128.0D * Math.pow(2.0D, (double)var6.vibratoLFODelay * var8) + 0.5D);
                } else {
                    var1.field2459 += 128;
                }

                while (var1.field2448 < var6.field2398.length - 2 && var1.field2459 > (var6.field2398[var1.field2448 + 2] & 255) << 8) {
                    var1.field2448 += 2;
                }

                if (var6.field2398.length - 2 == var1.field2448) {
                    var7 = true;
                }
            }

            if (var7) {
                var1.stream.method2706(var1.field2453);
                if (var2 != null) {
                    var1.stream.fill(var2, var3, var4);
                } else {
                    var1.stream.skip(var4);
                }

                if (var1.stream.method2672()) {
                    this.patchStream.mixer.addSubStream(var1.stream);
                }

                var1.clearMusicPatchNode();
                if (var1.field2459 >= 0) {
                    var1.remove();
                    if (var1.field2467 > 0 && var1 == this.field2435[var1.field2452][var1.field2467]) {
                        this.field2435[var1.field2452][var1.field2467] = null;
                    }
                }

                return true;
            } else {
                var1.stream.method2704(var1.field2453, this.method3818(var1), this.method3819(var1));
                return false;
            }
        } else {
            var1.clearMusicPatchNode();
            var1.remove();
            if (var1.field2467 > 0 && var1 == this.field2435[var1.field2452][var1.field2467]) {
                this.field2435[var1.field2452][var1.field2467] = null;
            }

            return true;
        }
    }


    public synchronized boolean loadMusicTrackFiles(MidiTrack var1, SoundBankCache soundCache, File idx15, int var4) throws IOException, UnsupportedAudioFileException {
        var1.loadMidiTrackInfo();
        boolean var5 = true;
        int[] var6 = null;
        if (var4 > 0) {
            var6 = new int[]{var4};
        }

        for (ByteArrayNode var7 = (ByteArrayNode)var1.table.first(); var7 != null; var7 = (ByteArrayNode)var1.table.next()) {
            int var8 = (int)var7.key;
            MusicPatch var9 = (MusicPatch)this.musicPatches.get((long)var8);
            if (var9 == null) {
                Path path = Paths.get(idx15.toString() + "/" + var8 + ".dat/");
                var9 = new MusicPatch(Files.readAllBytes(path));
                this.musicPatches.put(var9, (long)var8);
            }

            if (!var9.localPatchLoader(soundCache, var7.byteArray, var6)) {
                var5 = false;
            }
        }

        if (var5) {
            var1.clear();
        }

        return var5;
    }

    static final void PcmStream_disable(PcmStream var0) {
        var0.active = false;
        if (var0.sound != null) {
            var0.sound.position = 0;
        }

        for (PcmStream var1 = var0.firstSubStream(); var1 != null; var1 = var0.nextSubStream()) {
            PcmStream_disable(var1);
        }

    }
}
