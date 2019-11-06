package main;

import main.utils.ByteBufferUtils;
import main.utils.Node;
import org.displee.cache.index.Index;

import java.nio.ByteBuffer;

public class MusicPatch extends Node {

    int __m;
    RawSound[] rawSounds;
    short[] l;
    byte[] __w;
    byte[] __o;
    MusicPatchNode2[] __u;
    byte[] __g;
    int[] containerIDs;

    int[] notePitches;

    static MusicPatch getMusicPatch(Index patchIndex, int archiveID, int fileID) {
        byte[] patchData = patchIndex.getArchive(archiveID).getFile(fileID).getData();
        return new MusicPatch(ByteBuffer.wrap(patchData));
    }

    MusicPatch(ByteBuffer buffer) {
        this.rawSounds = new RawSound[128];
        this.l = new short[128];
        this.__w = new byte[128];
        this.__o = new byte[128];
        this.__u = new MusicPatchNode2[128];
        this.__g = new byte[128];
        this.containerIDs = new int[128];
        this.notePitches = new int[128];

        int var3;
        for(var3 = 0; buffer.array()[var3 + buffer.position()] != 0; ++var3) {
            ;
        }

        byte[] var4 = new byte[var3];

        int var5;
        for(var5 = 0; var5 < var3; ++var5) {
            var4[var5] = (byte) (buffer.get() & 0xFF);
        }

        buffer.position(buffer.position() + 1);
        ++var3;
        var5 = buffer.position();
        buffer.position(buffer.position() + var3);

        int var6;
        for(var6 = 0; buffer.array()[var6 + buffer.position()] != 0; ++var6) {
            ;
        }

        byte[] var7 = new byte[var6];

        int var8;
        for(var8 = 0; var8 < var6; ++var8) {
            var7[var8] = (byte) (buffer.get() & 0xFF);
        }

        buffer.position(buffer.position() + 1);
        ++var6;
        var8 = buffer.position();
        buffer.position(buffer.position() + var6);

        int var9;
        for(var9 = 0; buffer.array()[var9 + buffer.position()] != 0; ++var9) {
            ;
        }

        byte[] var10 = new byte[var9];

        for(int var11 = 0; var11 < var9; ++var11) {
            var10[var11] = (byte) (buffer.get() & 0xFF);
        }

        buffer.position(buffer.position() + 1);
        ++var9;
        byte[] var42 = new byte[var9];
        int var12;
        int var13;
        if(var9 > 1) {
            var42[1] = 1;
            int var14 = 1;
            var12 = 2;

            for(var13 = 2; var13 < var9; ++var13) {
                int var15 = buffer.get() & 0xFF;
                if(var15 == 0) {
                    var14 = var12++;
                } else {
                    if(var15 <= var14) {
                        --var15;
                    }

                    var14 = var15;
                }

                var42[var13] = (byte)var14;
            }
        } else {
            var12 = var9;
        }

        MusicPatchNode2[] var43 = new MusicPatchNode2[var12];

        MusicPatchNode2 var44;
        for(var13 = 0; var13 < var43.length; ++var13) {
            var44 = var43[var13] = new MusicPatchNode2();
            int var16 = buffer.get() & 0xFF;
            if(var16 > 0) {
                var44.__m = new byte[var16 * 2];
            }

            var16 = buffer.get() & 0xFF;
            if(var16 > 0) {
                var44.__f = new byte[var16 * 2 + 2];
                var44.__f[1] = 64;
            }
        }

        var13 = buffer.get() & 0xFF;
        byte[] var45 = var13 > 0?new byte[var13 * 2]:null;
        var13 = buffer.get() & 0xFF;
        byte[] var17 = var13 > 0?new byte[var13 * 2]:null;

        int var18;
        for(var18 = 0; buffer.array()[var18 + buffer.position()] != 0; ++var18) {
            ;
        }

        byte[] var19 = new byte[var18];

        int notePitch;
        for(notePitch = 0; notePitch < var18; ++notePitch) {
            var19[notePitch] = (byte) (buffer.get() & 0xFF);
        }

        buffer.position(buffer.position() + 1);
        ++var18;
        notePitch = 0;

        int noteRangeCount;
        for(noteRangeCount = 0; noteRangeCount < 128; ++noteRangeCount) {
            notePitch += buffer.get() & 0xFF;
            this.l[noteRangeCount] = (short) notePitch;
        }

        notePitch = 0;

        for(noteRangeCount = 0; noteRangeCount < 128; ++noteRangeCount) {
            notePitch += buffer.get() & 0xFF;
            this.l[noteRangeCount] = (short)(this.l[noteRangeCount] + (notePitch << 8));

            notePitches[noteRangeCount] = notePitch;
        }

        noteRangeCount = 0;
        int var22 = 0;
        int containerID = 0;

        int containerNoteRanges;
        for(containerNoteRanges = 0; containerNoteRanges < 128; ++containerNoteRanges) {
            if(noteRangeCount == 0) {
                if(var22 < var19.length) {
                    noteRangeCount = var19[var22++];
                } else {
                    noteRangeCount = -1;
                }

                containerID = ByteBufferUtils.__as_311(buffer);
            }

            this.l[containerNoteRanges] = (short)(this.l[containerNoteRanges] + ((containerID - 1 & 2) << 14));
            this.containerIDs[containerNoteRanges] = containerID;
            --noteRangeCount;
        }

        noteRangeCount = 0;
        var22 = 0;
        containerNoteRanges = 0;

        int var25;
        for(var25 = 0; var25 < 128; ++var25) {
            if(this.containerIDs[var25] != 0) {
                if(noteRangeCount == 0) {
                    if(var22 < var4.length) {
                        noteRangeCount = var4[var22++];
                    } else {
                        noteRangeCount = -1;
                    }

                    containerNoteRanges = buffer.array()[var5++] - 1;
                }

                this.__g[var25] = (byte)containerNoteRanges;
                --noteRangeCount;
            }
        }

        noteRangeCount = 0;
        var22 = 0;
        var25 = 0;

        for(int var26 = 0; var26 < 128; ++var26) {
            if(this.containerIDs[var26] != 0) {
                if(noteRangeCount == 0) {
                    if(var22 < var7.length) {
                        noteRangeCount = var7[var22++];
                    } else {
                        noteRangeCount = -1;
                    }

                    var25 = buffer.array()[var8++] + 16 << 2;
                }

                this.__o[var26] = (byte)var25;
                --noteRangeCount;
            }
        }


        noteRangeCount = 0;
        var22 = 0;
        MusicPatchNode2 var46 = null;

        int var27;
        for(var27 = 0; var27 < 128; ++var27) {
            if(this.containerIDs[var27] != 0) {
                if(noteRangeCount == 0) {
                    var46 = var43[var42[var22]];
                    if(var22 < var10.length) {
                        noteRangeCount = var10[var22++];
                    } else {
                        noteRangeCount = -1;
                    }
                }

                this.__u[var27] = var46;
                --noteRangeCount;
            }
        }

        noteRangeCount = 0;
        var22 = 0;
        var27 = 0;

        int var28;
        for(var28 = 0; var28 < 128; ++var28) {
            if(noteRangeCount == 0) {
                if(var22 < var19.length) {
                    noteRangeCount = var19[var22++];
                } else {
                    noteRangeCount = -1;
                }

                if(this.containerIDs[var28] > 0) {
                    var27 = buffer.get() & 0xFF + 1;
                }
            }

            this.__w[var28] = (byte)var27;
            --noteRangeCount;
        }

        this.__m = buffer.get() & 0xFF + 1;

        MusicPatchNode2 var29;
        int var30;
        for(var28 = 0; var28 < var12; ++var28) {
            var29 = var43[var28];
            if(var29.__m != null) {
                for(var30 = 1; var30 < var29.__m.length; var30 += 2) {
                    var29.__m[var30] = (byte) (buffer.get() & 0xFF);
                }
            }

            if(var29.__f != null) {
                for(var30 = 3; var30 < var29.__f.length - 2; var30 += 2) {
                    var29.__f[var30] = (byte) (buffer.get() & 0xFF);
                }
            }
        }

        if(var45 != null) {
            for(var28 = 1; var28 < var45.length; var28 += 2) {
                var45[var28] = (byte) (buffer.get() & 0xFF);
            }
        }

        if(var17 != null) {
            for(var28 = 1; var28 < var17.length; var28 += 2) {
                var17[var28] = (byte) (buffer.get() & 0xFF);
            }
        }

        for(var28 = 0; var28 < var12; ++var28) {
            var29 = var43[var28];
            if(var29.__f != null) {
                notePitch = 0;

                for(var30 = 2; var30 < var29.__f.length; var30 += 2) {
                    notePitch = 1 + notePitch + buffer.get() & 0xFF;
                    var29.__f[var30] = (byte)notePitch;
                }
            }
        }

        for(var28 = 0; var28 < var12; ++var28) {
            var29 = var43[var28];
            if(var29.__m != null) {
                notePitch = 0;

                for(var30 = 2; var30 < var29.__m.length; var30 += 2) {
                    notePitch = 1 + notePitch + buffer.get() & 0xFF;
                    var29.__m[var30] = (byte)notePitch;
                }
            }
        }

        byte var31;
        int var32;
        int var33;
        int var34;
        int var35;
        int var36;
        int var37;
        byte var38;
        if(var45 != null) {
            notePitch = buffer.get() & 0xFF;
            var45[0] = (byte)notePitch;

            for(var28 = 2; var28 < var45.length; var28 += 2) {
                notePitch = 1 + notePitch + buffer.get() & 0xFF;
                var45[var28] = (byte)notePitch;
            }

            var38 = var45[0];
            byte var39 = var45[1];

            for(var30 = 0; var30 < var38; ++var30) {
                this.__w[var30] = (byte)(var39 * this.__w[var30] + 32 >> 6);
            }

            for(var30 = 2; var30 < var45.length; var30 += 2) {
                var31 = var45[var30];
                byte var40 = var45[var30 + 1];
                var32 = var39 * (var31 - var38) + (var31 - var38) / 2;

                for(var33 = var38; var33 < var31; ++var33) {
                    var35 = var31 - var38;
                    var36 = var32 >>> 31;
                    var34 = (var32 + var36) / var35 - var36;
                    this.__w[var33] = (byte)(var34 * this.__w[var33] + 32 >> 6);
                    var32 += var40 - var39;
                }

                var38 = var31;
                var39 = var40;
            }

            for(var37 = var38; var37 < 128; ++var37) {
                this.__w[var37] = (byte)(var39 * this.__w[var37] + 32 >> 6);
            }

            var44 = null;
        }

        if(var17 != null) {
            notePitch = buffer.get() & 0xFF;
            var17[0] = (byte)notePitch;

            for(var28 = 2; var28 < var17.length; var28 += 2) {
                notePitch = 1 + notePitch + buffer.get() & 0xFF;
                var17[var28] = (byte)notePitch;
            }

            var38 = var17[0];
            int var47 = var17[1] << 1;

            for(var30 = 0; var30 < var38; ++var30) {
                var37 = var47 + (this.__o[var30] & 255);
                if(var37 < 0) {
                    var37 = 0;
                }

                if(var37 > 128) {
                    var37 = 128;
                }

                this.__o[var30] = (byte)var37;
            }

            int var48;
            for(var30 = 2; var30 < var17.length; var30 += 2) {
                var31 = var17[var30];
                var48 = var17[var30 + 1] << 1;
                var32 = var47 * (var31 - var38) + (var31 - var38) / 2;

                for(var33 = var38; var33 < var31; ++var33) {
                    var35 = var31 - var38;
                    var36 = var32 >>> 31;
                    var34 = (var32 + var36) / var35 - var36;
                    int var41 = var34 + (this.__o[var33] & 255);
                    if(var41 < 0) {
                        var41 = 0;
                    }

                    if(var41 > 128) {
                        var41 = 128;
                    }

                    this.__o[var33] = (byte)var41;
                    var32 += var48 - var47;
                }

                var38 = var31;
                var47 = var48;
            }

            for(var37 = var38; var37 < 128; ++var37) {
                var48 = var47 + (this.__o[var37] & 255);
                if(var48 < 0) {
                    var48 = 0;
                }

                if(var48 > 128) {
                    var48 = 128;
                }

                this.__o[var37] = (byte)var48;
            }

            Object var49 = null;
        }

        for(var28 = 0; var28 < var12; ++var28) {
            var43[var28].__q = buffer.get() & 0xFF;
        }

        for(var28 = 0; var28 < var12; ++var28) {
            var29 = var43[var28];
            if(var29.__m != null) {
                var29.__w = buffer.get() & 0xFF;
            }

            if(var29.__f != null) {
                var29.__o = buffer.get() & 0xFF;
            }

            if(var29.__q > 0) {
                var29.__u = buffer.get() & 0xFF;
            }
        }

        for(var28 = 0; var28 < var12; ++var28) {
            var43[var28].__x = buffer.get() & 0xFF;
        }

        for(var28 = 0; var28 < var12; ++var28) {
            var29 = var43[var28];
            if(var29.__x > 0) {
                var29.__g = buffer.get() & 0xFF;
            }
        }

        for(var28 = 0; var28 < var12; ++var28) {
            var29 = var43[var28];
            if(var29.__g > 0) {
                var29.__e = buffer.get() & 0xFF;
            }
        }
    }

    boolean loadPatchSamples(SoundBankCache var1, byte[] var2, int[] var3) {

        boolean var4 = true;
        int var5 = 0;
        RawSound var6 = null;

        for(int var7 = 0; var7 < 128; ++var7) {
            if(var2 == null || var2[var7] != 0) {
                int var8 = this.containerIDs[var7];
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
                        this.rawSounds[var7] = var6;
                        this.containerIDs[var7] = 0;
                    }
                }
            }
        }
        return var4;
    }

    void clear() {
        this.containerIDs = null;
    }
}
