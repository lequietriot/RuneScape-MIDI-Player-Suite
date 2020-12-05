package main;

import com.sun.media.sound.SF2Soundbank;
import main.utils.ByteArrayNode;
import main.utils.NodeHashTable;
import org.displee.cache.index.Index;

import javax.sound.midi.Soundbank;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MidiPcmStream extends PcmStream {

    NodeHashTable musicPatches;
    int musicVolume;
    int ticks;
    int[] volume;
    int[] pan;
    int[] expression;
    int[] patch;
    int[] programChange;
    int[] bankSelect;
    int[] field2423;
    int[] modulation;
    int[] portamento;
    int[] sustain;
    int[] nonRegisteredParameter;
    int[] dataEntry;
    int[] generalPurpose1;
    int[] generalPurpose2;
    int[] field2433;
    MusicPatchNode[][] musicPatchNode1;
    MusicPatchNode[][] musicPatchNode2;
    MidiFileReader midiFile;
    boolean musicLoop;
    int track;
    int trackLength;
    long sequencePosition;
    long sequenceRemainder;
    MusicPatchPcmStream patchStream;

    public MidiPcmStream() {
        this.musicVolume = 256;
        this.ticks = 1000000;
        this.volume = new int[16];
        this.pan = new int[16];
        this.expression = new int[16];
        this.patch = new int[16];
        this.programChange = new int[16];
        this.bankSelect = new int[16];
        this.field2423 = new int[16];
        this.modulation = new int[16];
        this.portamento = new int[16];
        this.sustain = new int[16];
        this.nonRegisteredParameter = new int[16];
        this.dataEntry = new int[16];
        this.generalPurpose1 = new int[16];
        this.generalPurpose2 = new int[16];
        this.field2433 = new int[16];
        this.musicPatchNode1 = new MusicPatchNode[16][128];
        this.musicPatchNode2 = new MusicPatchNode[16][128];
        this.midiFile = new MidiFileReader();
        this.patchStream = new MusicPatchPcmStream(this);
        this.musicPatches = new NodeHashTable(128);
        this.sendSystemResetMessage();
    }

    public synchronized void setPcmStreamVolume(int volume) {
        this.musicVolume = volume;
    }

    public int getPcmStreamVolume() {
        return this.musicVolume;
    }

    public synchronized boolean loadMusicTrack(MidiTrack midiTrack, Index idx15, SoundBankCache soundBank, int sampleRate) {
        midiTrack.loadMidiTrackInfo();
        boolean patchLoading = true;
        int[] sampleRateArray = null;
        if (sampleRate > 0) {
            sampleRateArray = new int[]{sampleRate};
        }

        for (ByteArrayNode tableIndex = (ByteArrayNode) midiTrack.table.first(); tableIndex != null; tableIndex = (ByteArrayNode) midiTrack.table.next()) {
            int patchID = (int) tableIndex.key;
            MusicPatch musicPatch = (MusicPatch)this.musicPatches.get(patchID);
            if (musicPatch == null) {
                musicPatch = MusicPatch.getMusicPatch(idx15, patchID, 0);
                this.musicPatches.put(musicPatch, patchID);
            }

            if (!musicPatch.loadPatchSamples(soundBank, tableIndex.byteArray, sampleRateArray)) {
                patchLoading = false;
            }
        }

        if (patchLoading) {
            midiTrack.clear();
        }

        return patchLoading;
    }

    public synchronized void clearAll() {
        for (MusicPatch musicPatch = (MusicPatch) this.musicPatches.first(); musicPatch != null; musicPatch = (MusicPatch) this.musicPatches.next()) {
            musicPatch.clear();
        }

    }

    public synchronized void removeAll() {
        for (MusicPatch musicPatch = (MusicPatch) this.musicPatches.first(); musicPatch != null; musicPatch = (MusicPatch) this.musicPatches.next()) {
            musicPatch.remove();
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
            int var4 = this.midiFile.division * this.ticks / PcmPlayer.pcmPlayer_sampleRate;

            do {
                long var5 = (long)var4 * (long)var3 + this.sequencePosition;
                if (this.sequenceRemainder - var5 >= 0L) {
                    this.sequencePosition = var5;
                    break;
                }

                int var7 = (int)(((long)var4 + (this.sequenceRemainder - this.sequencePosition) - 1L) / (long)var4);
                this.sequencePosition += (long)var7 * (long)var4;
                this.patchStream.fill(var1, var2, var7);
                var2 += var7;
                var3 -= var7;
                this.method3825();
            } while(this.midiFile.isReady());
        }

        this.patchStream.fill(var1, var2, var3);
    }

    public synchronized void setMusicTrack(MidiTrack midiTrack, boolean loop) {
        this.clear();
        this.midiFile.parse(midiTrack.midi);
        this.musicLoop = loop;
        this.sequencePosition = 0L;
        int tracks = this.midiFile.trackCount();

        for (int track = 0; track < tracks; ++track) {
            this.midiFile.gotoTrack(track);
            this.midiFile.readTrackLength(track);
            this.midiFile.markTrackPosition(track);
        }

        this.track = this.midiFile.getPrioritizedTrack();
        this.trackLength = this.midiFile.trackLengths[this.track];
        this.sequenceRemainder = this.midiFile.getTrackPosition(this.trackLength);
    }

    protected synchronized void skip(int var1) {
        if (this.midiFile.isReady()) {
            int var2 = this.midiFile.division * this.ticks / PcmPlayer.pcmPlayer_sampleRate;

            do {
                long var3 = this.sequencePosition + (long) var1 * (long) var2;
                if (this.sequenceRemainder - var3 >= 0L) {
                    this.sequencePosition = var3;
                    break;
                }

                int var5 = (int)((this.sequenceRemainder - this.sequencePosition + (long) var2 - 1L) / (long) var2);
                this.sequencePosition += (long)var5 * (long) var2;
                this.patchStream.skip(var5);
                var1 -= var5;
                this.method3825();
            } while(this.midiFile.isReady());
        }

        this.patchStream.skip(var1);
    }

    public synchronized void clear() {
        this.midiFile.clear();
        this.sendSystemResetMessage();
    }

    public synchronized boolean isReady() {
        return this.midiFile.isReady();
    }

    public synchronized void init(int channel, int program) {
        this.setTrackProgramChange(channel, program);
    }

    void setTrackProgramChange(int channel, int program) {
        this.patch[channel] = program;
        this.bankSelect[channel] = program & -128;
        this.setProgramChange(channel, program);
    }

    void setProgramChange(int channel, int data1) {
        if (data1 != this.programChange[channel]) {
            this.programChange[channel] = data1;

            for (int noteIndex = 0; noteIndex < 128; ++noteIndex) {
                this.musicPatchNode2[channel][noteIndex] = null;
            }
        }

    }

    void setNoteOn(int channel, int note, int velocity) {
        this.setNoteOff(channel, note);
        if ((this.sustain[channel] & 2) != 0) {
            for (MusicPatchNode musicPatchNode = (MusicPatchNode)this.patchStream.queue.first(); musicPatchNode != null; musicPatchNode = (MusicPatchNode)this.patchStream.queue.next()) {
                if (musicPatchNode.currentTrack == channel && musicPatchNode.field2450 < 0) {
                    this.musicPatchNode1[channel][musicPatchNode.currentNotePitch] = null;
                    this.musicPatchNode1[channel][note] = musicPatchNode;
                    int var5 = (musicPatchNode.field2455 * musicPatchNode.field2454 >> 12) + musicPatchNode.frequencyCorrection;
                    musicPatchNode.frequencyCorrection += note - musicPatchNode.currentNotePitch << 8;
                    musicPatchNode.field2454 = var5 - musicPatchNode.frequencyCorrection;
                    musicPatchNode.field2455 = 4096;
                    musicPatchNode.currentNotePitch = note;
                    return;
                }
            }
        }

        MusicPatch musicPatch = (MusicPatch) this.musicPatches.get(this.programChange[channel]);
        if (musicPatch != null) {
            AudioBuffer audioBuffer = musicPatch.audioBuffers[note];
            if (audioBuffer != null) {
                MusicPatchNode musicPatchNode = new MusicPatchNode();
                musicPatchNode.currentTrack = channel;
                musicPatchNode.patch = musicPatch;
                musicPatchNode.audioBuffer = audioBuffer;
                musicPatchNode.musicPatchInfo = musicPatch.musicPatchNode2[note];
                musicPatchNode.loopVariable = musicPatch.loopMode[note];
                musicPatchNode.currentNotePitch = note;
                musicPatchNode.maxVolumeLevel = velocity * velocity * musicPatch.volumeOffset[note] * musicPatch.panOffset[note] + 1024 >> 11;
                musicPatchNode.currentPanValue = musicPatch.panOffset[note] & 255;
                musicPatchNode.frequencyCorrection = (note << 8) - (musicPatch.pitchOffset[note] & 32767);
                musicPatchNode.field2456 = 0;
                musicPatchNode.field2457 = 0;
                musicPatchNode.field2458 = 0;
                musicPatchNode.field2450 = -1;
                musicPatchNode.field2448 = 0;

                if (this.generalPurpose1[channel] == 0) {
                    musicPatchNode.stream = RawPcmStream.method2685(audioBuffer, this.method3864(musicPatchNode), this.calculateVolume(musicPatchNode), this.method3819(musicPatchNode));
                } else {
                    musicPatchNode.stream = RawPcmStream.method2685(audioBuffer, this.method3864(musicPatchNode), 0, this.method3819(musicPatchNode));
                    this.method3852(musicPatchNode, musicPatch.pitchOffset[note] < 0);
                }

                if (musicPatch.pitchOffset[note] < 0) {
                    if (musicPatchNode.stream != null) {
                        musicPatchNode.stream.setNumLoops(-1);
                    }
                }

                if (musicPatchNode.loopVariable >= 0) {
                    MusicPatchNode var7 = this.musicPatchNode2[channel][musicPatchNode.loopVariable];
                    if (var7 != null && var7.field2450 < 0) {
                        this.musicPatchNode1[channel][var7.currentNotePitch] = null;
                        var7.field2450 = 0;
                    }

                    this.musicPatchNode2[channel][musicPatchNode.loopVariable] = musicPatchNode;
                }

                this.patchStream.queue.addFirst(musicPatchNode);
                this.musicPatchNode1[channel][note] = musicPatchNode;
            }
        }
    }

    void method3852(MusicPatchNode var1, boolean var2) {
        int var3 = var1.audioBuffer.samples.length;
        int var4;
        if (var2 && var1.audioBuffer.enableLoop) {
            int var5 = var3 + var3 - var1.audioBuffer.start;
            var4 = (int)((long)this.generalPurpose1[var1.currentTrack] * (long)var5 >> 6);
            var3 <<= 8;
            if (var4 >= var3) {
                var4 = var3 + var3 - 1 - var4;
                var1.stream.method2655();
            }
        } else {
            var4 = (int)((long)var3 * (long)this.generalPurpose1[var1.currentTrack] >> 6);
        }

        var1.stream.method2664(var4);
    }

    void setNoteOff(int channel, int note) {
        MusicPatchNode musicPatchNode = this.musicPatchNode1[channel][note];
        if (musicPatchNode != null) {
            this.musicPatchNode1[channel][note] = null;
            if ((this.sustain[channel] & 2) != 0) {
                for (MusicPatchNode musicPatchNode3 = (MusicPatchNode)this.patchStream.queue.last(); musicPatchNode3 != null; musicPatchNode3 = (MusicPatchNode)this.patchStream.queue.previous()) {
                    if (musicPatchNode3.currentTrack == musicPatchNode.currentTrack && musicPatchNode3.field2450 < 0 && musicPatchNode != musicPatchNode3) {
                        musicPatchNode.field2450 = 0;
                        break;
                    }
                }
            } else {
                musicPatchNode.field2450 = 0;
            }

        }
    }

    void setPolyphonicAftertouch(int var1, int var2, int var3) {
    }

    void setAftertouch(int var1, int var2) {
    }

    void setPitchBend(int var1, int var2) {
        this.field2423[var1] = var2;
    }

    void turnAllSoundOff(int var1) {
        for (MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
            if (var1 < 0 || var2.currentTrack == var1) {
                if (var2.stream != null) {
                    var2.stream.method2706(PcmPlayer.pcmPlayer_sampleRate / 100);
                    if (var2.stream.method2672()) {
                        this.patchStream.mixer.addSubStream(var2.stream);
                    }

                    var2.clearMusicPatchNode();
                }

                if (var2.field2450 < 0) {
                    this.musicPatchNode1[var2.currentTrack][var2.currentNotePitch] = null;
                }

                var2.remove();
            }
        }

    }

    void resetAllControllers(int var1) {
        if (var1 >= 0) {
            this.volume[var1] = 12800;
            this.pan[var1] = 8192;
            this.expression[var1] = 16383;
            this.field2423[var1] = 8192;
            this.modulation[var1] = 0;
            this.portamento[var1] = 8192;
            this.resetSustainPedal(var1);
            this.resetEffectSwitch(var1);
            this.sustain[var1] = 0;
            this.nonRegisteredParameter[var1] = 32767;
            this.dataEntry[var1] = 256;
            this.generalPurpose1[var1] = 0;
            this.method3899(var1, 8192);
        } else {
            for (var1 = 0; var1 < 16; ++var1) {
                this.resetAllControllers(var1);
            }

        }
    }

    void turnAllNotesOff(int var1) {
        for (MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
            if ((var1 < 0 || var2.currentTrack == var1) && var2.field2450 < 0) {
                this.musicPatchNode1[var2.currentTrack][var2.currentNotePitch] = null;
                var2.field2450 = 0;
            }
        }

    }

    void sendSystemResetMessage() {
        this.turnAllSoundOff(-1);
        this.resetAllControllers(-1);

        int track;
        for (track = 0; track < 16; ++track) {
            this.programChange[track] = this.patch[track];
        }

        for (track = 0; track < 16; ++track) {
            this.bankSelect[track] = this.patch[track] & -128;
        }

    }

    void resetSustainPedal(int track) {
        if ((this.sustain[track] & 2) != 0) {
            for (MusicPatchNode var2 = (MusicPatchNode) this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
                if (var2.currentTrack == track && this.musicPatchNode1[track][var2.currentNotePitch] == null && var2.field2450 < 0) {
                    var2.field2450 = 0;
                }
            }
        }
    }

    void resetEffectSwitch(int track) {
        if ((this.sustain[track] & 4) != 0) {
            for (MusicPatchNode var2 = (MusicPatchNode)this.patchStream.queue.last(); var2 != null; var2 = (MusicPatchNode)this.patchStream.queue.previous()) {
                if (var2.currentTrack == track) {
                    var2.field2462 = 0;
                }
            }
        }

    }

    void parseMessage(int midiMessage) {
        int message = midiMessage & 240;
        int channel;
        int data1;
        int data2;

        //128 = Note Off
        if (message == 128) {
            channel = midiMessage & 15;
            data1 = midiMessage >> 8 & 127;
            data2 = midiMessage >> 16 & 127;
            this.setNoteOff(channel, data1);

        //144 = Note On
        } else if (message == 144) {
            channel = midiMessage & 15;
            data1 = midiMessage >> 8 & 127;
            data2 = midiMessage >> 16 & 127;
            if (data2 > 0) {
                this.setNoteOn(channel, data1, data2);
            } else {
                this.setNoteOff(channel, data1);
            }

        //160 = Polyphonic Aftertouch
        } else if (message == 160) {
            channel = midiMessage & 15;
            data1 = midiMessage >> 8 & 127;
            data2 = midiMessage >> 16 & 127;
            this.setPolyphonicAftertouch(channel, data1, data2);

        // 176 = Control Change
        } else if (message == 176) {
            channel = midiMessage & 15;
            data1 = midiMessage >> 8 & 127;
            data2 = midiMessage >> 16 & 127;
            if (data1 == 0) {
                this.bankSelect[channel] = (data2 << 14) + (this.bankSelect[channel] & -2080769);
            }

            if (data1 == 32) {
                this.bankSelect[channel] = (data2 << 7) + (this.bankSelect[channel] & -16257);
            }

            if (data1 == 1) {
                this.modulation[channel] = (data2 << 7) + (this.modulation[channel] & -16257);
            }

            if (data1 == 33) {
                this.modulation[channel] = data2 + (this.modulation[channel] & -128);
            }

            if (data1 == 5) {
                this.portamento[channel] = (data2 << 7) + (this.portamento[channel] & -16257);
            }

            if (data1 == 37) {
                this.portamento[channel] = data2 + (this.portamento[channel] & -128);
            }

            if (data1 == 7) {
                this.volume[channel] = (data2 << 7) + (this.volume[channel] & -16257);
            }

            if (data1 == 39) {
                this.volume[channel] = data2 + (this.volume[channel] & -128);
            }

            if (data1 == 10) {
                this.pan[channel] = (data2 << 7) + (this.pan[channel] & -16257);
            }

            if (data1 == 42) {
                this.pan[channel] = data2 + (this.pan[channel] & -128);
            }

            if (data1 == 11) {
                this.expression[channel] = (data2 << 7) + (this.expression[channel] & -16257);
            }

            if (data1 == 43) {
                this.expression[channel] = data2 + (this.expression[channel] & -128);
            }

            int[] switches;
            if (data1 == 64) {
                if (data2 >= 64) {
                    switches = this.sustain;
                    switches[channel] |= 1;
                } else {
                    switches = this.sustain;
                    switches[channel] &= -2;
                }
            }

            if (data1 == 65) {
                if (data2 >= 64) {
                    switches = this.sustain;
                    switches[channel] |= 2;
                } else {
                    this.resetSustainPedal(channel);
                    switches = this.sustain;
                    switches[channel] &= -3;
                }
            }

            if (data1 == 99) {
                this.nonRegisteredParameter[channel] = (data2 << 7) + (this.nonRegisteredParameter[channel] & 127);
            }

            if (data1 == 98) {
                this.nonRegisteredParameter[channel] = (this.nonRegisteredParameter[channel] & 16256) + data2;
            }

            if (data1 == 101) {
                this.nonRegisteredParameter[channel] = (data2 << 7) + (this.nonRegisteredParameter[channel] & 127) + 16384;
            }

            if (data1 == 100) {
                this.nonRegisteredParameter[channel] = (this.nonRegisteredParameter[channel] & 16256) + data2 + 16384;
            }

            if (data1 == 120) {
                this.turnAllSoundOff(channel);
            }

            if (data1 == 121) {
                this.resetAllControllers(channel);
            }

            if (data1 == 123) {
                this.turnAllNotesOff(channel);
            }

            int var6;
            if (data1 == 6) {
                var6 = this.nonRegisteredParameter[channel];
                if (var6 == 16384) {
                    this.dataEntry[channel] = (data2 << 7) + (this.dataEntry[channel] & -16257);
                }
            }

            if (data1 == 38) {
                var6 = this.nonRegisteredParameter[channel];
                if (var6 == 16384) {
                    this.dataEntry[channel] = data2 + (this.dataEntry[channel] & -128);
                }
            }

            if (data1 == 16) {
                this.generalPurpose1[channel] = (data2 << 7) + (this.generalPurpose1[channel] & -16257);
            }

            if (data1 == 48) {
                this.generalPurpose1[channel] = data2 + (this.generalPurpose1[channel] & -128);
            }

            if (data1 == 81) {
                if (data2 >= 64) {
                    switches = this.sustain;
                    switches[channel] |= 4;
                } else {
                    this.resetEffectSwitch(channel);
                    switches = this.sustain;
                    switches[channel] &= -5;
                }
            }

            if (data1 == 17) {
                this.method3899(channel, (data2 << 7) + (this.generalPurpose2[channel] & -16257));
            }

            if (data1 == 49) {
                this.method3899(channel, data2 + (this.generalPurpose2[channel] & -128));
            }

        //192 = Program Change
        } else if (message == 192) {
            channel = midiMessage & 15;
            data1 = midiMessage >> 8 & 127;
            this.setProgramChange(channel, data1 + this.bankSelect[channel]);

        //208 = Aftertouch
        } else if (message == 208) {
            channel = midiMessage & 15;
            data1 = midiMessage >> 8 & 127;
            this.setAftertouch(channel, data1);

        //224 = Pitch Bend
        } else if (message == 224) {
            channel = midiMessage & 15;
            data1 = (midiMessage >> 8 & 127) + (midiMessage >> 9 & 16256);
            this.setPitchBend(channel, data1);

        } else {
            message = midiMessage & 255;
            if (message == 255) {
                this.sendSystemResetMessage();
            }
        }
    }

    void method3899(int var1, int var2) {
        this.generalPurpose2[var1] = var2;
        this.field2433[var1] = (int)(2097152.0D * Math.pow(2.0D, (double)var2 * 5.4931640625E-4D) + 0.5D);
    }

    int method3864(MusicPatchNode var1) {
        int var2 = (var1.field2454 * var1.field2455 >> 12) + var1.frequencyCorrection;
        var2 += (this.field2423[var1.currentTrack] - 8192) * this.dataEntry[var1.currentTrack] >> 12;
        MusicPatchNode2 var3 = var1.musicPatchInfo;
        int var4;
        if (var3.volumeEnvelopeSustain > 0 && (var3.vibratoLFOPitch > 0 || this.modulation[var1.currentTrack] > 0)) {
            var4 = var3.vibratoLFOPitch << 2;
            int var5 = var3.field2394 << 1;
            if (var1.field2461 < var5) {
                var4 = var4 * var1.field2461 / var5;
            }

            var4 += this.modulation[var1.currentTrack] >> 7;
            double var6 = Math.sin(0.01227184630308513D * (double)(var1.field2449 & 511));
            var2 += (int)((double)var4 * var6);
        }

        var4 = (int)((double)(var1.audioBuffer.sampleRate * 256) * Math.pow(2.0D, (double)var2 * 3.255208333333333E-4D) / (double)PcmPlayer.pcmPlayer_sampleRate + 0.5D);
        return Math.max(var4, 1);
    }

    int calculateVolume(MusicPatchNode var1) {
        MusicPatchNode2 var2 = var1.musicPatchInfo;
        int var3 = this.volume[var1.currentTrack] * this.expression[var1.currentTrack] + 4096 >> 13;
        var3 = var3 * var3 + 16384 >> 15;
        var3 = var3 * var1.maxVolumeLevel + 16384 >> 15;
        var3 = var3 * this.musicVolume + 128 >> 8;
        if (var2.volumeEnvelopeDecay > 0) {
            var3 = (int)((double) var3 * Math.pow(0.5D, (double) var2.volumeEnvelopeDecay * (double) var1.field2456 * 1.953125E-5D) + 0.5D);
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

        if (var1.field2450 > 0 && var2.field2398 != null) {
            var4 = var1.field2450;
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
        int var2 = this.pan[var1.currentTrack];
        return var2 < 8192 ? var2 * var1.currentPanValue + 32 >> 6 : 16384 - ((128 - var1.currentPanValue) * (16384 - var2) + 32 >> 6);
    }

    void method3825() {
        int track = this.track;
        int trackLength = this.trackLength;

        long var3;
        for (var3 = this.sequenceRemainder; trackLength == this.trackLength; var3 = this.midiFile.getTrackPosition(trackLength)) {
            while (trackLength == this.midiFile.trackLengths[track]) {
                this.midiFile.gotoTrack(track);
                int midiMessage = this.midiFile.getMessage(track);
                if (midiMessage == 1) {
                    this.midiFile.setTrackDone();
                    this.midiFile.markTrackPosition(track);
                    if (this.midiFile.isDone()) {
                        if (!this.musicLoop || trackLength == 0) {
                            this.sendSystemResetMessage();
                            this.midiFile.clear();
                            return;
                        }

                        this.midiFile.reset(var3);
                    }
                    break;
                }

                if ((midiMessage & 128) != 0) {
                    this.parseMessage(midiMessage);
                }

                this.midiFile.readTrackLength(track);
                this.midiFile.markTrackPosition(track);
            }

            track = this.midiFile.getPrioritizedTrack();
            trackLength = this.midiFile.trackLengths[track];
        }

        this.track = track;
        this.trackLength = trackLength;
        this.sequenceRemainder = var3;
    }

    boolean method3826(MusicPatchNode var1) {
        if (var1.stream == null) {
            if (var1.field2450 >= 0) {
                var1.remove();
                if (var1.loopVariable > 0 && var1 == this.musicPatchNode2[var1.currentTrack][var1.loopVariable]) {
                    this.musicPatchNode2[var1.currentTrack][var1.loopVariable] = null;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    boolean method3884(MusicPatchNode var1, int[] var2, int var3, int var4) {
        var1.field2453 = PcmPlayer.pcmPlayer_sampleRate / 100;
        if (var1.field2450 < 0 || var1.stream != null && !var1.stream.method2671()) {
            int var5 = var1.field2455;
            if (var5 > 0) {
                var5 -= (int)(16.0D * Math.pow(2.0D, (double)this.portamento[var1.currentTrack] * 4.921259842519685E-4D) + 0.5D);
                if (var5 < 0) {
                    var5 = 0;
                }

                var1.field2455 = var5;
            }

            var1.stream.method2669(this.method3864(var1));
            MusicPatchNode2 var6 = var1.musicPatchInfo;
            boolean var7 = false;
            ++var1.field2461;
            var1.field2449 += var6.volumeEnvelopeSustain;
            double var8 = (double)((var1.currentNotePitch - 60 << 8) + (var1.field2455 * var1.field2454 >> 12)) * 5.086263020833333E-6D;
            if (var6.volumeEnvelopeDecay > 0) {
                if (var6.vibratoLFOFrequency > 0) {
                    var1.field2456 += (int)(128.0D * Math.pow(2.0D, (double)var6.vibratoLFOFrequency * var8) + 0.5D);
                } else {
                    var1.field2456 += 128;
                }
            }

            if (var6.field2402 != null) {
                if (var6.volumeEnvelopeRelease > 0) {
                    var1.field2457 += (int)(128.0D * Math.pow(2.0D, (double)var6.volumeEnvelopeRelease * var8) + 0.5D);
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

            if (var1.field2450 >= 0 && var6.field2398 != null && (this.sustain[var1.currentTrack] & 1) == 0 && (var1.loopVariable < 0 || var1 != this.musicPatchNode2[var1.currentTrack][var1.loopVariable])) {
                if (var6.vibratoLFODelay > 0) {
                    var1.field2450 += (int)(128.0D * Math.pow(2.0D, (double)var6.vibratoLFODelay * var8) + 0.5D);
                } else {
                    var1.field2450 += 128;
                }

                while (var1.field2448 < var6.field2398.length - 2 && var1.field2450 > (var6.field2398[var1.field2448 + 2] & 255) << 8) {
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
                if (var1.field2450 >= 0) {
                    var1.remove();
                    if (var1.loopVariable > 0 && var1 == this.musicPatchNode2[var1.currentTrack][var1.loopVariable]) {
                        this.musicPatchNode2[var1.currentTrack][var1.loopVariable] = null;
                    }
                }

                return true;
            } else {
                var1.stream.method2704(var1.field2453, this.calculateVolume(var1), this.method3819(var1));
                return false;
            }
        } else {
            var1.clearMusicPatchNode();
            var1.remove();
            if (var1.loopVariable > 0 && var1 == this.musicPatchNode2[var1.currentTrack][var1.loopVariable]) {
                this.musicPatchNode2[var1.currentTrack][var1.loopVariable] = null;
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
            MusicPatch var9 = (MusicPatch)this.musicPatches.get(var8);
            if (var9 == null) {
                Path path = Paths.get(idx15.toString() + "/" + var8 + ".dat/");
                var9 = new MusicPatch(Files.readAllBytes(path));
                this.musicPatches.put(var9, var8);
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

    public void loadSoundFontBank(MidiTrack midiTrack, Soundbank soundbank) {

        SF2Soundbank sf2Soundbank = (SF2Soundbank) soundbank;

        midiTrack.loadMidiTrackInfo();

        for (ByteArrayNode tableIndex = (ByteArrayNode) midiTrack.table.first(); tableIndex != null; tableIndex = (ByteArrayNode) midiTrack.table.next()) {
            int patchID = (int) tableIndex.key;
            MusicPatch musicPatch = (MusicPatch) this.musicPatches.get(patchID);
            if (musicPatch == null) {
                try {
                    Path path = Paths.get(MusicPatch.localCustomSoundBank + "/" + PatchBanks.RUNESCAPE_VERSION + "/Patches/" + patchID + ".dat/");
                    if (path.toFile().exists()) {
                        musicPatch = new MusicPatch(Files.readAllBytes(path));
                        musicPatch.loadSf2ID(sf2Soundbank, patchID);
                        //musicPatch.loadCustomBankPatch(sf2Soundbank);
                    }
                    else {
                        musicPatch = PatchBanks.getCustomMusicPatch(patchID, sf2Soundbank);
                        musicPatch.loadSf2ID(sf2Soundbank, patchID);
                        //musicPatch.loadCustomBankPatchID(sf2Soundbank);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (musicPatch != null) {
                    this.musicPatches.put(musicPatch, patchID);
                }
            }

            if (musicPatch != null) {
                //musicPatch.loadCustomBankPatch(sf2Soundbank);
            }
        }
    }

    public void loadCreateSoundFontBanks(Soundbank soundbank) {

        SF2Soundbank sf2Soundbank = (SF2Soundbank) soundbank;

        for (int patchID = 0; patchID < 4000; patchID++) {

            MusicPatch musicPatch = (MusicPatch) this.musicPatches.get(patchID);

            if (musicPatch == null) {
                musicPatch = new MusicPatch(patchID, sf2Soundbank);

                if (musicPatch != null) {
                    this.musicPatches.put(musicPatch, patchID);
                }
            }

            if (musicPatch != null) {
                try {
                    musicPatch.createNewPatch(patchID);
                    musicPatch.loadCustomSampleID(patchID, sf2Soundbank);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadAllSoundFontBankPatches(Soundbank soundbank) {

        SF2Soundbank sf2Soundbank = (SF2Soundbank) soundbank;

        for (int patchID = 0; patchID < 4000; patchID++) {

            MusicPatch musicPatch = (MusicPatch) this.musicPatches.get(patchID);

            if (musicPatch == null) {
                try {
                    Path path = Paths.get(MusicPatch.localCustomSoundBank + "/" + PatchBanks.RUNESCAPE_VERSION + "/Patches/" + patchID + ".dat/");

                    if (new File(String.valueOf(path.toFile())).exists()) {
                        musicPatch = new MusicPatch(Files.readAllBytes(path));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (musicPatch != null) {
                    this.musicPatches.put(musicPatch, patchID);
                }
            }

            if (musicPatch != null) {
                try {
                    //musicPatch.createNewPatch(patchID);
                    musicPatch.loadCustomBankPatch(sf2Soundbank);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void loadTestSoundBank(MidiTrack midiTrack) {

        MidiTrack.loadMidiTrackInfo();

        for (ByteArrayNode tableIndex = (ByteArrayNode) MidiTrack.table.first(); tableIndex != null; tableIndex = (ByteArrayNode) MidiTrack.table.next()) {
            int patchID = (int) tableIndex.key;
            MusicPatch musicPatch = (MusicPatch) this.musicPatches.get(patchID);
            if (musicPatch == null) {
                musicPatch = PatchBanks.makeCustomMusicPatch(patchID);
                Path patchPath = Paths.get(PatchBanks.CUSTOM_SOUND_PATH + "/Instrument Info/" + patchID + ".txt/");

                try {

                    List<String> list = Files.readAllLines(patchPath);

                    for (int index = 0; index < list.size(); index++) {

                        if (list.get(index).contains(PatchBanks.PATCH_NAME)) {
                            System.out.println("Instrument Loading: " + list.get(index).replace(PatchBanks.PATCH_NAME, ""));
                        }

                        if (list.get(index).contains(PatchBanks.SAMPLE_NAME)) {

                            String[] patchInfoList = new String[9];

                            for (int infoIndex = 0; infoIndex < 9; infoIndex++) {
                                patchInfoList[infoIndex] = list.get(index);
                                index++;
                            }

                            musicPatch.loadCustomPatch(patchInfoList);
                        }

                        if (list.get(index).contains(PatchBanks.PARAMETER_1)) {

                            String[] patchParameterList = new String[9];

                            for (int parameter = 0; parameter < 9; parameter++) {
                                patchParameterList[parameter] = list.get(index);
                                index++;
                            }

                            musicPatch.setParameters(patchParameterList);
                        }
                    }
                } catch (IOException | UnsupportedAudioFileException ioException) {
                    ioException.printStackTrace();
                }
            }
            this.musicPatches.put(musicPatch, patchID);
        }
    }

    public void loadTestSoundBankCompletely() {

        for (int patchID = 0; patchID < 4000; patchID++) {

            MusicPatch musicPatch = (MusicPatch) this.musicPatches.get(patchID);

            if (musicPatch == null) {
                musicPatch = PatchBanks.makeCustomMusicPatch(patchID);
                Path patchPath = Paths.get(PatchBanks.CUSTOM_SOUND_PATH + "/Instrument Info/" + patchID + ".txt/");

                if (!patchPath.toFile().exists()) {
                    continue;
                }

                try {

                    List<String> list = Files.readAllLines(patchPath);

                    for (int index = 0; index < list.size(); index++) {

                        if (list.get(index).contains(PatchBanks.PATCH_NAME)) {
                            System.out.println("Instrument Loading: " + list.get(index).replace(PatchBanks.PATCH_NAME, ""));
                        }

                        if (list.get(index).contains(PatchBanks.SAMPLE_NAME)) {

                            String[] patchInfoList = new String[9];

                            for (int infoIndex = 0; infoIndex < 9; infoIndex++) {
                                patchInfoList[infoIndex] = list.get(index);
                                index++;
                            }

                            musicPatch.loadCustomPatch(patchInfoList);
                        }

                        if (list.get(index).contains(PatchBanks.PARAMETER_1)) {

                            String[] patchParameterList = new String[9];

                            for (int parameter = 0; parameter < 9; parameter++) {
                                patchParameterList[parameter] = list.get(index);
                                index++;
                            }

                            musicPatch.setParameters(patchParameterList);
                        }
                    }
                } catch (IOException | UnsupportedAudioFileException ioException) {
                    ioException.printStackTrace();
                }
            }
            this.musicPatches.put(musicPatch, patchID);
        }
    }
}
