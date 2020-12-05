package main;

import com.sun.media.sound.SF2Instrument;
import com.sun.media.sound.SF2Region;
import com.sun.media.sound.SF2Sample;
import com.sun.media.sound.SF2Soundbank;
import main.utils.Buffer;
import main.utils.Node;
import org.displee.cache.index.Index;

import javax.sound.midi.Patch;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MusicPatch extends Node {

    AudioBuffer[] audioBuffers;
    short[] pitchOffset;
    byte[] volumeOffset;
    byte[] panOffset;
    MusicPatchNode2[] musicPatchNode2;
    byte[] loopMode;
    int[] sampleOffset;
    int baseVelocity;

    static File localSoundBankSamples;
    static File localSoundBankPatches;
    static File localSoundEffects;
    static File localCustomSoundBank;

    public MusicPatch(int patchID) {
        createNewPatch(patchID);
    }

    public static MusicPatch getMusicPatch(Index index, int archiveID, int fileID) {
        byte[] data = index.getArchive(archiveID).getFile(fileID).getData();
        return new MusicPatch(data);
    }

    MusicPatch() {
        this.audioBuffers = new AudioBuffer[128];
        this.pitchOffset = new short[128];
        this.volumeOffset = new byte[128];
        this.panOffset = new byte[128];
        this.musicPatchNode2 = new MusicPatchNode2[128];
        this.loopMode = new byte[128];
        this.sampleOffset = new int[128];
    }

    MusicPatch(byte[] patchData) {
        this.audioBuffers = new AudioBuffer[128];
        this.pitchOffset = new short[128];
        this.volumeOffset = new byte[128];
        this.panOffset = new byte[128];
        this.musicPatchNode2 = new MusicPatchNode2[128];
        this.loopMode = new byte[128];
        this.sampleOffset = new int[128];
        Buffer patchBuffer = new Buffer(patchData);

        int var3;
        for (var3 = 0; patchBuffer.array[var3 + patchBuffer.index] != 0; ++var3) {
        }

        byte[] var4 = new byte[var3];

        int var5;
        for (var5 = 0; var5 < var3; ++var5) {
            var4[var5] = patchBuffer.readByte();
        }

        ++patchBuffer.index;
        ++var3;
        var5 = patchBuffer.index;
        patchBuffer.index += var3;

        int var6;
        for (var6 = 0; patchBuffer.array[var6 + patchBuffer.index] != 0; ++var6) {
        }

        byte[] var7 = new byte[var6];

        int var8;
        for (var8 = 0; var8 < var6; ++var8) {
            var7[var8] = patchBuffer.readByte();
        }

        ++patchBuffer.index;
        ++var6;
        var8 = patchBuffer.index;
        patchBuffer.index += var6;

        int var9;
        for (var9 = 0; patchBuffer.array[var9 + patchBuffer.index] != 0; ++var9) {
        }

        byte[] var10 = new byte[var9];

        for (int var11 = 0; var11 < var9; ++var11) {
            var10[var11] = patchBuffer.readByte();
        }

        ++patchBuffer.index;
        ++var9;
        byte[] var38 = new byte[var9];
        int var12;
        int var14;
        if (var9 > 1) {
            var38[1] = 1;
            int var13 = 1;
            var12 = 2;

            for (var14 = 2; var14 < var9; ++var14) {
                int var15 = patchBuffer.readUnsignedByte();
                if (var15 == 0) {
                    var13 = var12++;
                } else {
                    if (var15 <= var13) {
                        --var15;
                    }

                    var13 = var15;
                }

                var38[var14] = (byte)var13;
            }
        } else {
            var12 = var9;
        }

        MusicPatchNode2[] var39 = new MusicPatchNode2[var12];

        MusicPatchNode2 var40;
        for (var14 = 0; var14 < var39.length; ++var14) {
            var40 = var39[var14] = new MusicPatchNode2();
            int var16 = patchBuffer.readUnsignedByte();
            if (var16 > 0) {
                var40.field2402 = new byte[var16 * 2];
            }

            var16 = patchBuffer.readUnsignedByte();
            if (var16 > 0) {
                var40.field2398 = new byte[var16 * 2 + 2];
                var40.field2398[1] = 64;
            }
        }

        var14 = patchBuffer.readUnsignedByte();
        byte[] var47 = var14 > 0 ? new byte[var14 * 2] : null;
        var14 = patchBuffer.readUnsignedByte();
        byte[] var41 = var14 > 0 ? new byte[var14 * 2] : null;

        int var17;
        for (var17 = 0; patchBuffer.array[var17 + patchBuffer.index] != 0; ++var17) {
        }

        byte[] var18 = new byte[var17];

        int var19;
        for (var19 = 0; var19 < var17; ++var19) {
            var18[var19] = patchBuffer.readByte();
        }

        ++patchBuffer.index;
        ++var17;
        var19 = 0;

        int var20;
        for (var20 = 0; var20 < 128; ++var20) {
            var19 += patchBuffer.readUnsignedByte();
            this.pitchOffset[var20] = (short)var19;
        }

        var19 = 0;

        short[] var50;
        for (var20 = 0; var20 < 128; ++var20) {
            var19 += patchBuffer.readUnsignedByte();
            var50 = this.pitchOffset;
            var50[var20] = (short)(var50[var20] + (var19 << 8));
        }

        var20 = 0;
        int var21 = 0;
        int var22 = 0;

        int var23;
        for (var23 = 0; var23 < 128; ++var23) {
            if (var20 == 0) {
                if (var21 < var18.length) {
                    var20 = var18[var21++];
                } else {
                    var20 = -1;
                }

                var22 = patchBuffer.readVarInt();
            }

            var50 = this.pitchOffset;
            var50[var23] = (short)(var50[var23] + ((var22 - 1 & 2) << 14));
            this.sampleOffset[var23] = var22;
            --var20;

        }

        var20 = 0;
        var21 = 0;
        var23 = 0;

        int var24;
        for (var24 = 0; var24 < 128; ++var24) {
            if (this.sampleOffset[var24] != 0) {
                if (var20 == 0) {
                    if (var21 < var4.length) {
                        var20 = var4[var21++];
                    } else {
                        var20 = -1;
                    }

                    var23 = patchBuffer.array[var5++] - 1;
                }

                this.loopMode[var24] = (byte)var23;
                --var20;
            }
        }

        var20 = 0;
        var21 = 0;
        var24 = 0;

        for (int var25 = 0; var25 < 128; ++var25) {
            if (this.sampleOffset[var25] != 0) {
                if (var20 == 0) {
                    if (var21 < var7.length) {
                        var20 = var7[var21++];
                    } else {
                        var20 = -1;
                    }

                    var24 = patchBuffer.array[var8++] + 16 << 2;
                }

                this.panOffset[var25] = (byte)var24;
                --var20;
            }
        }

        var20 = 0;
        var21 = 0;
        MusicPatchNode2 var42 = null;

        int var26;
        for (var26 = 0; var26 < 128; ++var26) {
            if (this.sampleOffset[var26] != 0) {
                if (var20 == 0) {
                    var42 = var39[var38[var21]];
                    if (var21 < var10.length) {
                        var20 = var10[var21++];
                    } else {
                        var20 = -1;
                    }
                }

                this.musicPatchNode2[var26] = var42;
                --var20;
            }
        }

        var20 = 0;
        var21 = 0;
        var26 = 0;

        int var27;
        for (var27 = 0; var27 < 128; ++var27) {
            if (var20 == 0) {
                if (var21 < var18.length) {
                    var20 = var18[var21++];
                } else {
                    var20 = -1;
                }

                if (this.sampleOffset[var27] > 0) {
                    var26 = patchBuffer.readUnsignedByte() + 1;
                }
            }

            this.volumeOffset[var27] = (byte)var26;
            --var20;
        }

        this.baseVelocity = patchBuffer.readUnsignedByte() + 1;

        MusicPatchNode2 var28;
        int var29;
        for (var27 = 0; var27 < var12; ++var27) {
            var28 = var39[var27];
            if (var28.field2402 != null) {
                for (var29 = 1; var29 < var28.field2402.length; var29 += 2) {
                    var28.field2402[var29] = patchBuffer.readByte();
                }
            }

            if (var28.field2398 != null) {
                for (var29 = 3; var29 < var28.field2398.length - 2; var29 += 2) {
                    var28.field2398[var29] = patchBuffer.readByte();
                }
            }
        }

        if (var47 != null) {
            for (var27 = 1; var27 < var47.length; var27 += 2) {
                var47[var27] = patchBuffer.readByte();
            }
        }

        if (var41 != null) {
            for (var27 = 1; var27 < var41.length; var27 += 2) {
                var41[var27] = patchBuffer.readByte();
            }
        }

        for (var27 = 0; var27 < var12; ++var27) {
            var28 = var39[var27];
            if (var28.field2398 != null) {
                var19 = 0;

                for (var29 = 2; var29 < var28.field2398.length; var29 += 2) {
                    var19 = var19 + 1 + patchBuffer.readUnsignedByte();
                    var28.field2398[var29] = (byte)var19;
                }
            }
        }

        for (var27 = 0; var27 < var12; ++var27) {
            var28 = var39[var27];
            if (var28.field2402 != null) {
                var19 = 0;

                for (var29 = 2; var29 < var28.field2402.length; var29 += 2) {
                    var19 = 1 + var19 + patchBuffer.readUnsignedByte();
                    var28.field2402[var29] = (byte)var19;
                }
            }
        }

        byte var30;
        int var32;
        int var33;
        int var34;
        int var35;
        int var36;
        int var44;
        byte var46;
        if (var47 != null) {
            var19 = patchBuffer.readUnsignedByte();
            var47[0] = (byte)var19;

            for (var27 = 2; var27 < var47.length; var27 += 2) {
                var19 = 1 + var19 + patchBuffer.readUnsignedByte();
                var47[var27] = (byte)var19;
            }

            var46 = var47[0];
            byte var43 = var47[1];

            for (var29 = 0; var29 < var46; ++var29) {
                this.volumeOffset[var29] = (byte)(var43 * this.volumeOffset[var29] + 32 >> 6);
            }

            for (var29 = 2; var29 < var47.length; var29 += 2) {
                var30 = var47[var29];
                byte var31 = var47[var29 + 1];
                var32 = var43 * (var30 - var46) + (var30 - var46) / 2;

                for (var33 = var46; var33 < var30; ++var33) {
                    var35 = var30 - var46;
                    var36 = var32 >>> 31;
                    var34 = (var36 + var32) / var35 - var36;
                    this.volumeOffset[var33] = (byte)(var34 * this.volumeOffset[var33] + 32 >> 6);
                    var32 += var31 - var43;
                }

                var46 = var30;
                var43 = var31;
            }

            for (var44 = var46; var44 < 128; ++var44) {
                this.volumeOffset[var44] = (byte)(var43 * this.volumeOffset[var44] + 32 >> 6);
            }
        }

        if (var41 != null) {
            var19 = patchBuffer.readUnsignedByte();
            var41[0] = (byte)var19;

            for (var27 = 2; var27 < var41.length; var27 += 2) {
                var19 = 1 + var19 + patchBuffer.readUnsignedByte();
                var41[var27] = (byte)var19;
            }

            var46 = var41[0];
            int var49 = var41[1] << 1;

            for (var29 = 0; var29 < var46; ++var29) {
                var44 = var49 + (this.panOffset[var29] & 255);
                if (var44 < 0) {
                    var44 = 0;
                }

                if (var44 > 128) {
                    var44 = 128;
                }

                this.panOffset[var29] = (byte)var44;
            }

            int var45;
            for (var29 = 2; var29 < var41.length; var29 += 2) {
                var30 = var41[var29];
                var45 = var41[var29 + 1] << 1;
                var32 = var49 * (var30 - var46) + (var30 - var46) / 2;

                for (var33 = var46; var33 < var30; ++var33) {
                    var35 = var30 - var46;
                    var36 = var32 >>> 31;
                    var34 = (var36 + var32) / var35 - var36;
                    int var37 = var34 + (this.panOffset[var33] & 255);
                    if (var37 < 0) {
                        var37 = 0;
                    }

                    if (var37 > 128) {
                        var37 = 128;
                    }

                    this.panOffset[var33] = (byte)var37;
                    var32 += var45 - var49;
                }

                var46 = var30;
                var49 = var45;
            }

            for (var44 = var46; var44 < 128; ++var44) {
                var45 = var49 + (this.panOffset[var44] & 255);
                if (var45 < 0) {
                    var45 = 0;
                }

                if (var45 > 128) {
                    var45 = 128;
                }

                this.panOffset[var44] = (byte)var45;
            }
        }

        for (var27 = 0; var27 < var12; ++var27) {
            var39[var27].volumeEnvelopeDecay = patchBuffer.readUnsignedByte();
        }

        for (var27 = 0; var27 < var12; ++var27) {
            var28 = var39[var27];
            if (var28.field2402 != null) {
                var28.volumeEnvelopeRelease = patchBuffer.readUnsignedByte();
            }

            if (var28.field2398 != null) {
                var28.vibratoLFODelay = patchBuffer.readUnsignedByte();
            }

            if (var28.volumeEnvelopeDecay > 0) {
                var28.vibratoLFOFrequency = patchBuffer.readUnsignedByte();
            }
        }

        for (var27 = 0; var27 < var12; ++var27) {
            var39[var27].volumeEnvelopeSustain = patchBuffer.readUnsignedByte();
        }

        for (var27 = 0; var27 < var12; ++var27) {
            var28 = var39[var27];
            if (var28.volumeEnvelopeSustain > 0) {
                var28.vibratoLFOPitch = patchBuffer.readUnsignedByte();
            }
        }

        for (var27 = 0; var27 < var12; ++var27) {
            var28 = var39[var27];
            if (var28.vibratoLFOPitch > 0) {
                var28.field2394 = patchBuffer.readUnsignedByte();
            }
        }
    }

    boolean loadPatchSamples(SoundBankCache var1, byte[] var2, int[] var3) {
        boolean var4 = true;
        int var5 = 0;
        AudioBuffer var6 = null;

        for(int var7 = 0; var7 < 128; ++var7) {
            if(var2 == null || var2[var7] != 0) {
                int var8 = this.sampleOffset[var7];
                if(var8 != 0) {
                    if(var8 != var5) {
                        var5 = var8--;
                        if((var8 & 1) == 0) {
                            var6 = var1.getSoundEffect(var8 >> 2, var3);
                        } else {
                            var6 = var1.getMusicSample(var8 >> 2, var3);
                        }

                        if(var6 == null) {
                            var4 = false;
                        }
                    }

                    if(var6 != null) {
                        this.audioBuffers[var7] = var6;
                        this.sampleOffset[var7] = 0;
                    }
                }
            }
        }
        return var4;
    }

    /**
     * Constructs a new custom Music Patch, using basic default values, or pre-existing values.
     * @param patchID a custom patch ID to create the patch in.
     * @param soundBank the custom SoundFont sample bank.
     */
    public MusicPatch(int patchID, SF2Soundbank soundBank) {
        createNewPatch(patchID);
        //loadCustomSampleID(patchID, soundBank);
    }

    public void modifyExistingPatch(int customID) {

        //short lowestNote = -32768;
        for (int index = 0; index < 128; index++) {
            //pitchOffset[index] = lowestNote;
            //lowestNote += 256;
        }

        //byte volumeValue = 32;
        for (int index = 0; index < 128; index++) {
            //volume[index] = volumeValue;
        }

        //byte panning = 64;
        for (int index = 0; index < 128; index++) {
            //panOffset[index] = panning;
        }

        //byte loop = -1;
        for (int index = 0; index < 128; index++) {
            //loopMode[index] = loop;
        }

        //int sampleID = customID * 128;
        for (int index = 0; index < 128; index++) {
            //sampleOffset[index] = sampleID;
            //sampleID++;
        }

        for (int index = 0; index < 128; index++) {
            musicPatchNode2[index] = new MusicPatchNode2();
            musicPatchNode2[index].field2398 = null;
            musicPatchNode2[index].field2402 = null;
            musicPatchNode2[index].volumeEnvelopeRelease = 0;
            musicPatchNode2[index].volumeEnvelopeDecay = 0;
            musicPatchNode2[index].vibratoLFOFrequency = 0;
            musicPatchNode2[index].vibratoLFOPitch = 0;
            musicPatchNode2[index].vibratoLFODelay = 0;
            musicPatchNode2[index].volumeEnvelopeSustain = 0;
            musicPatchNode2[index].field2394 = 0;
        }
    }

    public void createNewPatch(int customID) {

        this.audioBuffers = new AudioBuffer[128];
        this.pitchOffset = new short[128];
        this.volumeOffset = new byte[128];
        this.panOffset = new byte[128];
        this.musicPatchNode2 = new MusicPatchNode2[128];
        this.loopMode = new byte[128];
        this.sampleOffset = new int[128];

        this.baseVelocity = 32;

        short lowestNote = -32768;
        for (int index = 0; index < 128; index++) {
            pitchOffset[index] = lowestNote;
            lowestNote += 256;
        }

        byte volumeValue = 32;
        for (int index = 0; index < 128; index++) {
            volumeOffset[index] = volumeValue;
        }

        byte panning = 64;
        for (int index = 0; index < 128; index++) {
            panOffset[index] = panning;
        }

        byte loop = -1;
        for (int index = 0; index < 128; index++) {
            loopMode[index] = loop;
        }

        int sampleID = customID * 128;
        for (int index = 0; index < 128; index++) {
            sampleOffset[index] = sampleID;
            sampleID++;
        }

        for (int index = 0; index < 128; index++) {
            musicPatchNode2[index] = new MusicPatchNode2();
            musicPatchNode2[index].field2398 = null;
            musicPatchNode2[index].field2402 = null;
            musicPatchNode2[index].volumeEnvelopeRelease = 0; //Pitch Correction?
            musicPatchNode2[index].volumeEnvelopeDecay = 0; //Volume Sustain?
            musicPatchNode2[index].vibratoLFOFrequency = 0; //Volume Hold?
            musicPatchNode2[index].vibratoLFOPitch = 0;
            musicPatchNode2[index].vibratoLFODelay = 0;
            musicPatchNode2[index].volumeEnvelopeSustain = 0;
            musicPatchNode2[index].field2394 = 0;
        }
    }

    /**
     * Loads the initialized custom Music Patch.
     */
    public void loadCustomSampleID(int patchID, SF2Soundbank sf2Soundbank) throws IOException {

        AudioBuffer audioBuffer = null;

        for (int note = 0; note < 128; ++note) {
            int sampleID = this.sampleOffset[note];
            for (SF2Sample sf2Sample : sf2Soundbank.getSamples()) {
                String patchSample = patchID + "_" + sampleID;
                if (patchSample.equals(sf2Sample.getName())) {
                    audioBuffer = new AudioBuffer((int) sf2Sample.getSampleRate(), getEightBitData(sf2Sample), (int) sf2Sample.getStartLoop(), (int) sf2Sample.getEndLoop());
                }
            }

            if (audioBuffer != null) {
                this.audioBuffers[note] = audioBuffer;
                this.sampleOffset[note] = 0;
            }
        }
    }

    /**
     * Loads the initialized custom Music Patch.
     */
    public void loadCustomBankPatch(SF2Soundbank sf2Soundbank) throws IOException {

        int position = 0;
        AudioBuffer audioBuffer = null;

        for (int note = 0; note < 128; ++note) {
            int sampleID = this.sampleOffset[note];
            if (sampleID != 0) {
                if(sampleID != position) {
                    position = sampleID--;
                    if((sampleID & 1) == 0) {
                        for (SF2Sample sf2Sample : sf2Soundbank.getSamples()) {
                            if ((sampleID >> 2) == Integer.parseInt(sf2Sample.getName())) {
                                audioBuffer = new AudioBuffer((int) sf2Sample.getSampleRate(), getEightBitData(sf2Sample), (int) sf2Sample.getStartLoop(), (int) sf2Sample.getEndLoop());
                                System.out.println("Loaded Sample ID " + (sampleID));
                            }
                        }
                    } else {
                        for (SF2Sample sf2Sample : sf2Soundbank.getSamples()) {
                            if ((sampleID >> 2) == Integer.parseInt(sf2Sample.getName())) {
                                audioBuffer = new AudioBuffer((int) sf2Sample.getSampleRate(), getEightBitData(sf2Sample), (int) sf2Sample.getStartLoop(), (int) sf2Sample.getEndLoop());
                                System.out.println("Loaded Sample ID " + (sampleID));
                            }
                        }
                    }
                }

                if (audioBuffer != null) {
                    this.audioBuffers[note] = audioBuffer;
                    this.sampleOffset[note] = 0;
                }
            }
        }
    }

    public byte[] getEightBitData(SF2Sample sf2Sample) throws IOException {

        byte[] data = ((AudioInputStream) sf2Sample.getData()).readAllBytes();
        byte[] compressedData = new byte[data.length / 2];

        for (int index = 0; index < compressedData.length; index++) {
            compressedData[index] = data[index * 2 + 1];
        }

        return compressedData;
    }

    public boolean localPatchLoader(SoundBankCache soundBankCache, byte[] byteArray, int[] var3) throws IOException, UnsupportedAudioFileException {
        boolean var4 = true;
        int var5 = 0;
        AudioBuffer var6 = null;

        for (int var7 = 0; var7 < 128; ++var7) {
            if (byteArray == null || byteArray[var7] != 0) {
                int var8 = this.sampleOffset[var7];
                if (var8 != 0) {
                    if (var5 != var8) {
                        var5 = var8--;
                        if ((var8 & 1) == 0) {
                            var6 = soundBankCache.getSoundEffect(0, var3);
                            if (var6 != null) {
                                var6 = soundBankCache.getCustomSoundEffect(localSoundEffects, var8 >> 2, var3);
                            }
                        } else

                        {
                            var6 = soundBankCache.getMusicSample(1, var3);
                            if (var6 != null) {
                                var6 = soundBankCache.getCustomMusicSample(localSoundBankSamples, var8 >> 2);
                            }
                        }

                        if (var6 == null) {
                            var4 = false;
                        }
                    }

                    if (var6 != null) {
                        this.audioBuffers[var7] = var6;
                        this.sampleOffset[var7] = 0;
                    }
                }
            }
        }

        return var4;
    }

    void clear() {
        this.sampleOffset = null;
    }

    /**
     * Loads the initialized custom Music Patch.
     */
    public void loadCustomBankPatchID(SF2Soundbank sf2Soundbank) throws IOException {

        int position = 0;
        AudioBuffer audioBuffer = null;

        for (int note = 0; note < 128; ++note) {
            int sampleID = this.sampleOffset[note];
            if (sampleID != 0) {
                //if(sampleID != position) {
                    //position = sampleID--;
                    if((sampleID & 1) == 0) {
                        for (SF2Sample sf2Sample : sf2Soundbank.getSamples()) {
                            if ((sampleID) == Integer.parseInt(sf2Sample.getName())) {
                                audioBuffer = new AudioBuffer((int) sf2Sample.getSampleRate(), getEightBitData(sf2Sample), (int) sf2Sample.getStartLoop(), (int) sf2Sample.getEndLoop());
                                System.out.println("Loaded Sample ID " + (sampleID));
                            }
                        }
                    } else {
                        for (SF2Sample sf2Sample : sf2Soundbank.getSamples()) {
                            if ((sampleID) == Integer.parseInt(sf2Sample.getName())) {
                                audioBuffer = new AudioBuffer((int) sf2Sample.getSampleRate(), getEightBitData(sf2Sample), (int) sf2Sample.getStartLoop(), (int) sf2Sample.getEndLoop());
                                System.out.println("Loaded Sample ID " + (sampleID));
                            }
                        }
                    }
                }

                if (audioBuffer != null) {
                    this.audioBuffers[note] = audioBuffer;
                    this.sampleOffset[note] = 0;
                }
            //}
        }
    }

    /**
     * Loads the initialized custom Music Patch.
     */
    public void loadSf2ID(SF2Soundbank sf2Soundbank, int patchID) throws IOException {

        AudioBuffer audioBuffer;
        Patch patch;

        int bank = 0;
        int patchNumber = patchID;

        while (patchNumber > 127) {
            patchNumber = patchNumber - 128;
            bank++;
        }

        bank = bank * 128;

        patch = new Patch(bank, patchNumber);

        System.out.println();
        System.out.println(bank);
        System.out.println(patchNumber);
        System.out.println(sf2Soundbank.getInstrument(patch));

        if (sf2Soundbank.getInstrument(patch) != null) {

            for (int region = 0; region < ((SF2Instrument) (sf2Soundbank.getInstrument(patch))).getRegions().toArray().length; region++) {

                for (int layer = 0; layer < ((SF2Instrument) (sf2Soundbank.getInstrument(patch))).getRegions().get(region).getLayer().getRegions().toArray().length; layer++) {

                    SF2Sample sf2Sample = ((SF2Instrument) (sf2Soundbank.getInstrument(patch))).getRegions().get(region).getLayer().getRegions().get(layer).getSample();
                    byte[] noteRange = ((SF2Instrument) (sf2Soundbank.getInstrument(patch))).getRegions().get(region).getLayer().getRegions().get(layer).getBytes(SF2Region.GENERATOR_KEYRANGE);
                    int loopMode = ((SF2Instrument) (sf2Soundbank.getInstrument(patch))).getRegions().get(region).getLayer().getRegions().get(layer).getInteger(SF2Region.GENERATOR_SAMPLEMODES);
                    int pitchCorrection = sf2Sample.getPitchCorrection();

                    //byte[] volumeAttackEnv = (((SF2Instrument) sf2Soundbank.getInstrument(patch)).getRegions().get(region).getLayer().getGlobalRegion().getBytes(SF2Region.GENERATOR_ATTACKVOLENV));
                    //byte[] volumeDecayEnv = (((SF2Instrument) sf2Soundbank.getInstrument(patch)).getRegions().get(region).getLayer().getGlobalRegion().getBytes(SF2Region.GENERATOR_DECAYVOLENV));
                    //byte[] volumeSustainEnv = (((SF2Instrument) sf2Soundbank.getInstrument(patch)).getRegions().get(region).getLayer().getGlobalRegion().getBytes(SF2Region.GENERATOR_SUSTAINVOLENV));
                    //byte[] volumeReleaseEnv = (((SF2Instrument) sf2Soundbank.getInstrument(patch)).getRegions().get(region).getLayer().getGlobalRegion().getBytes(SF2Region.GENERATOR_RELEASEVOLENV));

                    byte[] overridingNote = ((SF2Instrument) (sf2Soundbank.getInstrument(patch))).getRegions().get(region).getLayer().getRegions().get(layer).getBytes(SF2Region.GENERATOR_OVERRIDINGROOTKEY);
                    int fineTune = ((SF2Instrument) (sf2Soundbank.getInstrument(patch))).getRegions().get(region).getLayer().getRegions().get(layer).getInteger(SF2Region.GENERATOR_FINETUNE);
                    int coarseTune = ((SF2Instrument) (sf2Soundbank.getInstrument(patch))).getRegions().get(region).getLayer().getRegions().get(layer).getInteger(SF2Region.GENERATOR_COARSETUNE);

                    if (noteRange[0] == noteRange[1]) {
                        noteRange[1]++;
                    }

                    for (int note = noteRange[0]; note < noteRange[1] + 1; note++) {

                        if (loopMode >= 1) {
                            audioBuffer = new AudioBuffer((int) sf2Sample.getSampleRate(), getEightBitData(sf2Sample), (int) sf2Sample.getStartLoop(), (int) sf2Sample.getEndLoop());
                        } else {
                            audioBuffer = new AudioBuffer((int) sf2Sample.getSampleRate(), getEightBitData(sf2Sample), 0, 0);
                        }

                        this.audioBuffers[note] = audioBuffer;
                        this.sampleOffset[note] = 0;

                        if (overridingNote != null && overridingNote[0] != -1) {
                            this.pitchOffset[note] = (short) (((overridingNote[0] * 256)) - 32768 + (pitchCorrection + fineTune + coarseTune));
                        }

                        else {
                            this.pitchOffset[note] = (short) (((sf2Sample.getOriginalPitch() * 256)) - 32768 + (pitchCorrection + fineTune + coarseTune));
                        }

                        //musicPatchNode2[note].field2402 = new byte[]{0, 64}; //Patch Duration?
                        //musicPatchNode2[note].field2398 = new byte[]{0, 16, 16, 8, 8, 16, 16, 8, 8, 0}; //ADSR Envelope?
                    }
                }
            }
        }
    }

    public void loadCustomPatch(String[] patchInfoList) throws IOException, UnsupportedAudioFileException {

        String sampleName = patchInfoList[0].replace(PatchBanks.SAMPLE_NAME, "").trim();
        int rootKey = Integer.parseInt((patchInfoList[1].replace(PatchBanks.SAMPLE_ROOT_KEY, "").trim()));
        int lowKeyRange = Integer.parseInt(patchInfoList[2].replace(PatchBanks.KEY_LOW_RANGE, "").trim());
        int highKeyRange = Integer.parseInt(patchInfoList[3].replace(PatchBanks.KEY_HIGH_RANGE, "").trim());
        int masterVolume = Integer.parseInt(patchInfoList[4].replace(PatchBanks.MASTER_VOLUME, "").trim());
        int loopStart = Integer.parseInt(patchInfoList[5].replace(PatchBanks.LOOP_START, "").trim());
        int loopEnd = Integer.parseInt(patchInfoList[6].replace(PatchBanks.LOOP_END, "").trim());
        int volume = Integer.parseInt(patchInfoList[7].replace(PatchBanks.SAMPLE_VOLUME, "").trim());
        int pan = Integer.parseInt(patchInfoList[8].replace(PatchBanks.SAMPLE_PAN, "").trim());

        baseVelocity = masterVolume;

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(PatchBanks.CUSTOM_SOUND_PATH + "/Instrument Samples/" + sampleName + ".wav/"));
        AudioFormat audioFormat = audioInputStream.getFormat();

        byte[] audioSample = audioInputStream.readAllBytes();

        if (audioFormat.getChannels() == 2 && audioFormat.getSampleSizeInBits() == 16) {

            byte[] monoSample = downmixStereoToMono(audioSample);
            byte[] finalSample = downmix16BitsTo8Bits(monoSample);

            AudioBuffer audioBuffer = new AudioBuffer((int) audioFormat.getSampleRate(), finalSample, loopStart, loopEnd);

            for (int note = lowKeyRange; note < highKeyRange; note++) {
                audioBuffers[note] = audioBuffer;
                pitchOffset[note] = (short) ((rootKey * 256) - 32768);
                panOffset[note] = (byte) pan;
                volumeOffset[note] = (byte) volume;
            }

            if (lowKeyRange == highKeyRange) {
                audioBuffers[lowKeyRange] = audioBuffer;
                pitchOffset[lowKeyRange] = (short) ((rootKey * 256) - 32768);
                panOffset[lowKeyRange] = (byte) pan;
                volumeOffset[lowKeyRange] = (byte) volume;
            }
        }

        if (audioFormat.getChannels() == 1 && audioFormat.getSampleSizeInBits() == 16) {

            byte[] finalSample = downmix16BitsTo8Bits(audioSample);

            AudioBuffer audioBuffer = new AudioBuffer((int) audioFormat.getSampleRate(), finalSample, loopStart, loopEnd);

            for (int note = lowKeyRange; note < highKeyRange; note++) {
                audioBuffers[note] = audioBuffer;
                pitchOffset[note] = (short) ((rootKey * 256) - 32768);
                panOffset[note] = (byte) pan;
                volumeOffset[note] = (byte) volume;
            }

            if (lowKeyRange == highKeyRange) {
                audioBuffers[lowKeyRange] = audioBuffer;
                pitchOffset[lowKeyRange] = (short) ((rootKey * 256) - 32768);
                panOffset[lowKeyRange] = (byte) pan;
                volumeOffset[lowKeyRange] = (byte) volume;
            }
        }

        else {

            byte[] finalSample = new byte[audioSample.length];

            for (int index = 0; index < finalSample.length; index++) {
                finalSample[index] = (byte) ((audioSample[index] + 128) & 0xFF);
            }

            AudioBuffer audioBuffer;

            if (loopStart == 0) {
                audioBuffer = new AudioBuffer((int) audioFormat.getSampleRate(), finalSample, 0, 0);
            }

            else {
                audioBuffer = new AudioBuffer((int) audioFormat.getSampleRate(), finalSample, loopStart, loopEnd);
            }

            for (int note = lowKeyRange; note < highKeyRange; note++) {
                audioBuffers[note] = audioBuffer;
                pitchOffset[note] = (short) ((rootKey * 256) - 32768);
                panOffset[note] = (byte) pan;
                volumeOffset[note] = (byte) volume;
            }

            if (lowKeyRange == highKeyRange) {
                audioBuffers[lowKeyRange] = audioBuffer;
                pitchOffset[lowKeyRange] = (short) ((rootKey * 256) - 32768);
                panOffset[lowKeyRange] = (byte) pan;
                volumeOffset[lowKeyRange] = (byte) volume;
            }
        }
    }

    public void setParameters(String[] patchParameterList) {

        int globalParameter1 = Integer.parseInt(patchParameterList[0].replace(PatchBanks.PARAMETER_1, "").trim());
        int globalParameter2 = Integer.parseInt(patchParameterList[1].replace(PatchBanks.PARAMETER_2, "").trim());
        int globalParameter3 = Integer.parseInt(patchParameterList[2].replace(PatchBanks.PARAMETER_3, "").trim());
        int globalParameter4 = Integer.parseInt(patchParameterList[3].replace(PatchBanks.PARAMETER_4, "").trim());
        int globalParameter5 = Integer.parseInt(patchParameterList[4].replace(PatchBanks.PARAMETER_5, "").trim());
        int globalParameter6 = Integer.parseInt(patchParameterList[5].replace(PatchBanks.PARAMETER_6, "").trim());
        int globalParameter7 = Integer.parseInt(patchParameterList[6].replace(PatchBanks.PARAMETER_7, "").trim());

        String[] globalStringArray1 = patchParameterList[7].replace(PatchBanks.ARRAY_1, "").split(",");
        byte[] globalArray1 = new byte[globalStringArray1.length];

        if (globalStringArray1[0].contains("null")) {
            globalArray1 = null;
        }
        else {
            for (int index = 0; index < Objects.requireNonNull(globalArray1).length; index++) {
                if (globalStringArray1[index].indexOf("[") != 0) {
                    String strings = globalStringArray1[index].substring(globalStringArray1[index].indexOf("[") + 1).trim();
                    globalArray1[index] = Byte.parseByte(strings.replace("[", "").replace("]", ""));
                }
            }
        }

        String[] globalStringArray2 = patchParameterList[8].replace(PatchBanks.ARRAY_2, "").split(",");
        byte[] globalArray2 = new byte[globalStringArray2.length];

        if (globalStringArray2[0].contains("null")) {
            globalArray2 = null;
        }

        else {

        for (int index = 0; index < Objects.requireNonNull(globalArray2).length; index++) {
                if (globalStringArray2[index].indexOf("[") != 0) {
                    String strings = globalStringArray2[index].substring(globalStringArray2[index].indexOf("[") + 1).trim();
                    globalArray2[index] = Byte.parseByte(strings.replace("[", "").replace("]", ""));
                }
            }
        }

        for (int note = 0; note < 128; note++) {
            musicPatchNode2[note].field2398 = globalArray1;
            musicPatchNode2[note].field2402 = globalArray2;
            musicPatchNode2[note].volumeEnvelopeDecay = globalParameter1;
            musicPatchNode2[note].volumeEnvelopeRelease = globalParameter2;
            musicPatchNode2[note].vibratoLFODelay = globalParameter3;
            musicPatchNode2[note].vibratoLFOFrequency = globalParameter4;
            musicPatchNode2[note].vibratoLFOPitch = globalParameter5;
            musicPatchNode2[note].volumeEnvelopeSustain = globalParameter6;
            musicPatchNode2[note].field2394 = globalParameter7;
        }
    }

    private byte[] downmixStereoToMono(byte[] stereoSample) {

        byte[] monoSample = new byte[stereoSample.length / 2];

        for (int index = 0; index < monoSample.length; index++) {
            monoSample[index] = stereoSample[index * 2];
        }

        return monoSample;
    }

    private byte[] downmix16BitsTo8Bits(byte[] sample) {

        byte[] compressedData = new byte[sample.length / 2];

        for (int index = 0; index < compressedData.length; index++) {
            compressedData[index] = sample[index * 2 + 1];
        }

        return compressedData;
    }
}