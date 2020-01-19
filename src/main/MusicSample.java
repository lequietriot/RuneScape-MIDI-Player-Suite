package main;

import main.utils.ByteBufferUtils;
import org.displee.cache.index.Index;

import java.nio.ByteBuffer;

public class MusicSample {

   static byte[] __cd_g;
   static int anInt;
   static int __cd_e;
   static int __cd_x;
   static int __cd_d;
   static MusicSampleCodebook[] field1323;
   static MusicSampleFloor[] field1324;
   static MusicSampleResidue[] field1344;
   static MusicSampleMapping[] field1326;
   static boolean[] __cd_z;
   static int[] __cd_j;
   static boolean __cd_s;
   static float[] __cd_c;
   static float[] __cd_r;
   static float[] __cd_p;
   static float[] __cd_v;
   static float[] __cd_ag;
   static float[] __cd_aq;
   static float[] __cd_aj;
   static int[] __cd_av;
   static int[] __cd_ar;
   byte[][] sampleContainer;
   int sampleRate;
   int sampleCount;
   int start;
   int end;
   boolean loopConsistency;
   float[] __t;
   int __y;
   int __h;
   boolean __b;
   byte[] samples;
   int sampleLength;
   int soundIndices;

   MusicSample(byte[] var1) {
      this.read(var1);
   }

   void read(byte[] var1) {
      ByteBuffer buffer = ByteBuffer.wrap(var1);
      this.sampleRate = buffer.getInt();
      this.sampleCount = buffer.getInt();
      this.start = buffer.getInt();
      this.end = buffer.getInt();
      if(this.end < 0) {
         this.end = ~this.end;
         this.loopConsistency = true;
      }

      int soundIndex = buffer.getInt();
      this.sampleContainer = new byte[soundIndex][];

      for(int soundCount = 0; soundCount < soundIndex; ++soundCount) {
         int encodedSize = 0;

         int sampleIndex;
         do {
            sampleIndex = buffer.get() & 0xFF;
            encodedSize += sampleIndex;
         } while(sampleIndex >= 255);

         byte[] encoded = new byte[encodedSize];
         buffer.get(encoded, 0, encodedSize);
         this.sampleContainer[soundCount] = encoded;
      }

   }

   float[] __g_175(int var1) {
      setData(this.sampleContainer[var1], 0);
      method2338();
      int var2 = method2352(ByteBufferUtils.method634(__cd_j.length - 1));
      boolean var3 = __cd_z[var2];
      int var4 = var3?__cd_d:__cd_x;
      boolean var5 = false;
      boolean var6 = false;
      if(var3) {
         var5 = method2338() != 0;
         var6 = method2338() != 0;
      }

      int var7 = var4 >> 1;
      int var8;
      int var9;
      int var10;
      if(var3 && !var5) {
         var8 = (var4 >> 2) - (__cd_x >> 2);
         var9 = (__cd_x >> 2) + (var4 >> 2);
         var10 = __cd_x >> 1;
      } else {
         var8 = 0;
         var9 = var7;
         var10 = var4 >> 1;
      }

      int var11;
      int var12;
      int var13;
      if(var3 && !var6) {
         var11 = var4 - (var4 >> 2) - (__cd_x >> 2);
         var12 = (__cd_x >> 2) + (var4 - (var4 >> 2));
         var13 = __cd_x >> 1;
      } else {
         var11 = var7;
         var12 = var4;
         var13 = var4 >> 1;
      }

      MusicSampleMapping var14 = field1326[__cd_j[var2]];
      int var15 = var14.field1454;
      int var16 = var14.field1452[var15];
      boolean var17 = !field1324[var16].method2367();
      boolean var18 = var17;

      for(var16 = 0; var16 < var14.field1453; ++var16) {
         MusicSampleResidue var19 = field1344[var14.field1455[var16]];
         float[] var20 = __cd_c;
         var19.method2473(var20, var4 >> 1, var18);
      }

      int var48;
      if(!var17) {
         var16 = var14.field1454;
         var48 = var14.field1452[var16];
         field1324[var48].method2375(__cd_c, var4 >> 1);
      }

      float[] var21;
      int var22;
      int var49;
      if(var17) {
         for(var16 = var4 >> 1; var16 < var4; ++var16) {
            __cd_c[var16] = 0.0F;
         }
      } else {
         var16 = var4 >> 1;
         var48 = var4 >> 2;
         var49 = var4 >> 3;
         var21 = __cd_c;

         for(var22 = 0; var22 < var16; ++var22) {
            var21[var22] *= 0.5F;
         }

         for(var22 = var16; var22 < var4; ++var22) {
            var21[var22] = -var21[var4 - var22 - 1];
         }

         float[] var23 = var3?__cd_ag:__cd_r;
         float[] var24 = var3?__cd_aq:__cd_p;
         float[] var25 = var3?__cd_aj:__cd_v;
         int[] var26 = var3?__cd_ar:__cd_av;

         int var27;
         float var28;
         float var29;
         float var30;
         float var31;
         for(var27 = 0; var27 < var48; ++var27) {
            var28 = var21[var27 * 4] - var21[var4 - var27 * 4 - 1];
            var29 = var21[var27 * 4 + 2] - var21[var4 - var27 * 4 - 3];
            var30 = var23[var27 * 2];
            var31 = var23[var27 * 2 + 1];
            var21[var4 - var27 * 4 - 1] = var28 * var30 - var29 * var31;
            var21[var4 - var27 * 4 - 3] = var28 * var31 + var29 * var30;
         }

         float var32;
         float var33;
         for(var27 = 0; var27 < var49; ++var27) {
            var28 = var21[var16 + var27 * 4 + 3];
            var29 = var21[var16 + var27 * 4 + 1];
            var30 = var21[var27 * 4 + 3];
            var31 = var21[var27 * 4 + 1];
            var21[var16 + var27 * 4 + 3] = var28 + var30;
            var21[var16 + var27 * 4 + 1] = var29 + var31;
            var32 = var23[var16 - 4 - var27 * 4];
            var33 = var23[var16 - 3 - var27 * 4];
            var21[var27 * 4 + 3] = (var28 - var30) * var32 - (var29 - var31) * var33;
            var21[var27 * 4 + 1] = (var29 - var31) * var32 + (var28 - var30) * var33;
         }

         var27 = ByteBufferUtils.method634(var4 - 1);

         int var34;
         int var35;
         int var36;
         int var37;
         for(var34 = 0; var34 < var27 - 3; ++var34) {
            var35 = var4 >> var34 + 2;
            var36 = 8 << var34;

            for(var37 = 0; var37 < 2 << var34; ++var37) {
               int var38 = var4 - var35 * var37 * 2;
               int var39 = var4 - var35 * (var37 * 2 + 1);

               for(int var40 = 0; var40 < var4 >> var34 + 4; ++var40) {
                  int var41 = var40 * 4;
                  float var42 = var21[var38 - 1 - var41];
                  float var43 = var21[var38 - 3 - var41];
                  float var44 = var21[var39 - 1 - var41];
                  float var45 = var21[var39 - 3 - var41];
                  var21[var38 - 1 - var41] = var42 + var44;
                  var21[var38 - 3 - var41] = var43 + var45;
                  float var46 = var23[var40 * var36];
                  float var47 = var23[var40 * var36 + 1];
                  var21[var39 - 1 - var41] = (var42 - var44) * var46 - (var43 - var45) * var47;
                  var21[var39 - 3 - var41] = (var43 - var45) * var46 + (var42 - var44) * var47;
               }
            }
         }

         for(var34 = 1; var34 < var49 - 1; ++var34) {
            var35 = var26[var34];
            if(var34 < var35) {
               var36 = var34 * 8;
               var37 = var35 * 8;
               var32 = var21[var36 + 1];
               var21[var36 + 1] = var21[var37 + 1];
               var21[var37 + 1] = var32;
               var32 = var21[var36 + 3];
               var21[var36 + 3] = var21[var37 + 3];
               var21[var37 + 3] = var32;
               var32 = var21[var36 + 5];
               var21[var36 + 5] = var21[var37 + 5];
               var21[var37 + 5] = var32;
               var32 = var21[var36 + 7];
               var21[var36 + 7] = var21[var37 + 7];
               var21[var37 + 7] = var32;
            }
         }

         for(var34 = 0; var34 < var16; ++var34) {
            var21[var34] = var21[var34 * 2 + 1];
         }

         for(var34 = 0; var34 < var49; ++var34) {
            var21[var4 - 1 - var34 * 2] = var21[var34 * 4];
            var21[var4 - 2 - var34 * 2] = var21[var34 * 4 + 1];
            var21[var4 - var48 - 1 - var34 * 2] = var21[var34 * 4 + 2];
            var21[var4 - var48 - 2 - var34 * 2] = var21[var34 * 4 + 3];
         }

         for(var34 = 0; var34 < var49; ++var34) {
            var29 = var25[var34 * 2];
            var30 = var25[var34 * 2 + 1];
            var31 = var21[var16 + var34 * 2];
            var32 = var21[var16 + var34 * 2 + 1];
            var33 = var21[var4 - 2 - var34 * 2];
            float var51 = var21[var4 - 1 - var34 * 2];
            float var52 = var30 * (var31 - var33) + var29 * (var32 + var51);
            var21[var16 + var34 * 2] = (var31 + var33 + var52) * 0.5F;
            var21[var4 - 2 - var34 * 2] = (var31 + var33 - var52) * 0.5F;
            var52 = var30 * (var32 + var51) - var29 * (var31 - var33);
            var21[var16 + var34 * 2 + 1] = (var32 - var51 + var52) * 0.5F;
            var21[var4 - 1 - var34 * 2] = (-var32 + var51 + var52) * 0.5F;
         }

         for(var34 = 0; var34 < var48; ++var34) {
            var21[var34] = var21[var16 + var34 * 2] * var24[var34 * 2] + var21[var16 + var34 * 2 + 1] * var24[var34 * 2 + 1];
            var21[var16 - 1 - var34] = var21[var16 + var34 * 2] * var24[var34 * 2 + 1] - var21[var16 + var34 * 2 + 1] * var24[var34 * 2];
         }

         for(var34 = 0; var34 < var48; ++var34) {
            var21[var34 + (var4 - var48)] = -var21[var34];
         }

         for(var34 = 0; var34 < var48; ++var34) {
            var21[var34] = var21[var48 + var34];
         }

         for(var34 = 0; var34 < var48; ++var34) {
            var21[var48 + var34] = -var21[var48 - var34 - 1];
         }

         for(var34 = 0; var34 < var48; ++var34) {
            var21[var16 + var34] = var21[var4 - var34 - 1];
         }

         for(var34 = var8; var34 < var9; ++var34) {
            var29 = (float)Math.sin(((double)(var34 - var8) + 0.5D) / (double)var10 * 0.5D * 3.141592653589793D);
            __cd_c[var34] *= (float)Math.sin(1.5707963267948966D * (double)var29 * (double)var29);
         }

         for(var34 = var11; var34 < var12; ++var34) {
            var29 = (float)Math.sin(((double)(var34 - var11) + 0.5D) / (double)var13 * 0.5D * 3.141592653589793D + 1.5707963267948966D);
            __cd_c[var34] *= (float)Math.sin(1.5707963267948966D * (double)var29 * (double)var29);
         }
      }

      var21 = null;
      if(this.__y > 0) {
         var48 = var4 + this.__y >> 2;
         var21 = new float[var48];
         if(!this.__b) {
            for(var49 = 0; var49 < this.__h; ++var49) {
               var22 = var49 + (this.__y >> 1);
               var21[var49] += this.__t[var22];
            }
         }

         if(!var17) {
            for(var49 = var8; var49 < var4 >> 1; ++var49) {
               var22 = var21.length - (var4 >> 1) + var49;
               var21[var22] += __cd_c[var49];
            }
         }
      }

      float[] var50 = this.__t;
      this.__t = __cd_c;
      __cd_c = var50;
      this.__y = var4;
      this.__h = var12 - (var4 >> 1);
      this.__b = var17;
      return var21;
   }

   RawSound toRawSound(int[] var1) {
      if(var1 != null && var1[0] <= 0) {
         return null;
      } else {
         if(this.samples == null) {
            this.__y = 0;
            this.__t = new float[__cd_d];
            this.samples = new byte[this.sampleCount];
            this.sampleLength = 0;
            this.soundIndices = 0;
         }

         for(; this.soundIndices < this.sampleContainer.length; ++this.soundIndices) {
            if(var1 != null && var1[0] <= 0) {
               return null;
            }

            float[] var2 = this.__g_175(this.soundIndices);
            if(var2 != null) {
               int var3 = this.sampleLength;
               int var4 = var2.length;
               if(var4 > this.sampleCount - var3) {
                  var4 = this.sampleCount - var3;
               }

               for(int var5 = 0; var5 < var4; ++var5) {
                  int var6 = (int)(128.0F + var2[var5] * 128.0F);
                  if((var6 & -256) != 0) {
                     var6 = ~var6 >> 31;
                  }

                  this.samples[var3++] = (byte)(var6 - 128);
               }

               if(var1 != null) {
                  var1[0] -= var3 - this.sampleLength;
               }

               this.sampleLength = var3;
            }
         }

         byte[] var7 = this.samples;
         
         return new RawSound(this.sampleRate, var7, this.start, this.end, this.loopConsistency);
      }
   }

   static float method2357(int var0) {
      int var1 = var0 & 2097151;
      int var2 = var0 & Integer.MIN_VALUE;
      int var3 = (var0 & 2145386496) >> 21;
      if(var2 != 0) {
         var1 = -var1;
      }

      return (float)((double)var1 * Math.pow(2.0D, (double)(var3 - 788)));
   }

   static void setData(byte[] var0, int var1) {
      __cd_g = var0;
      anInt = var1;
      __cd_e = 0;
   }

   static int method2338() {
      int var0 = __cd_g[anInt] >> __cd_e & 1;
      ++__cd_e;
      anInt += __cd_e >> 3;
      __cd_e &= 7;
      return var0;
   }

   static int method2352(int var0) {
      int var1 = 0;

      int var2;
      int var3;
      for(var2 = 0; var0 >= 8 - __cd_e; var0 -= var3) {
         var3 = 8 - __cd_e;
         int var4 = (1 << var3) - 1;
         var1 += (__cd_g[anInt] >> __cd_e & var4) << var2;
         __cd_e = 0;
         ++anInt;
         var2 += var3;
      }

      if(var0 > 0) {
         var3 = (1 << var0) - 1;
         var1 += (__cd_g[anInt] >> __cd_e & var3) << var2;
         __cd_e += var0;
      }

      return var1;
   }

   static void method2341(byte[] var0) {
      setData(var0, 0);
      __cd_x = 1 << method2352(4);
      __cd_d = 1 << method2352(4);
      __cd_c = new float[__cd_d];

      int var1;
      int var2;
      int var3;
      int var4;
      int var5;
      for(var1 = 0; var1 < 2; ++var1) {
         var2 = var1 != 0?__cd_d:__cd_x;
         var3 = var2 >> 1;
         var4 = var2 >> 2;
         var5 = var2 >> 3;
         float[] var6 = new float[var3];

         for(int var7 = 0; var7 < var4; ++var7) {
            var6[var7 * 2] = (float)Math.cos((double)(var7 * 4) * 3.141592653589793D / (double)var2);
            var6[var7 * 2 + 1] = -((float)Math.sin((double)(var7 * 4) * 3.141592653589793D / (double)var2));
         }

         float[] var13 = new float[var3];

         for(int var8 = 0; var8 < var4; ++var8) {
            var13[var8 * 2] = (float)Math.cos((double)(var8 * 2 + 1) * 3.141592653589793D / (double)(var2 * 2));
            var13[var8 * 2 + 1] = (float)Math.sin((double)(var8 * 2 + 1) * 3.141592653589793D / (double)(var2 * 2));
         }

         float[] var14 = new float[var4];

         for(int var9 = 0; var9 < var5; ++var9) {
            var14[var9 * 2] = (float)Math.cos((double)(var9 * 4 + 2) * 3.141592653589793D / (double)var2);
            var14[var9 * 2 + 1] = -((float)Math.sin((double)(var9 * 4 + 2) * 3.141592653589793D / (double)var2));
         }

         int[] var15 = new int[var5];
         int var10 = ByteBufferUtils.method634(var5 - 1);

         for(int var11 = 0; var11 < var5; ++var11) {
            var15[var11] = ByteBufferUtils.method87(var11, var10);
         }

         if(var1 != 0) {
            __cd_ag = var6;
            __cd_aq = var13;
            __cd_aj = var14;
            __cd_ar = var15;
         } else {
            __cd_r = var6;
            __cd_p = var13;
            __cd_v = var14;
            __cd_av = var15;
         }
      }

      var1 = method2352(8) + 1;
      field1323 = new MusicSampleCodebook[var1];

      for(var2 = 0; var2 < var1; ++var2) {
         field1323[var2] = new MusicSampleCodebook();
      }

      var2 = method2352(6) + 1;

      for(var3 = 0; var3 < var2; ++var3) {
         method2352(16);
      }

      var2 = method2352(6) + 1;
      field1324 = new MusicSampleFloor[var2];

      for(var3 = 0; var3 < var2; ++var3) {
         field1324[var3] = new MusicSampleFloor();
      }

      var3 = method2352(6) + 1;
      field1344 = new MusicSampleResidue[var3];

      for(var4 = 0; var4 < var3; ++var4) {
         field1344[var4] = new MusicSampleResidue();
      }

      var4 = method2352(6) + 1;
      field1326 = new MusicSampleMapping[var4];

      for(var5 = 0; var5 < var4; ++var5) {
         field1326[var5] = new MusicSampleMapping();
      }

      var5 = method2352(6) + 1;
      __cd_z = new boolean[var5];
      __cd_j = new int[var5];

      for(int var12 = 0; var12 < var5; ++var12) {
         __cd_z[var12] = method2338() != 0;
         method2352(16);
         method2352(16);
         __cd_j[var12] = method2352(8);
      }

   }

   static boolean method2343(Index musicSampleIndex) {
      if(!__cd_s) {
         byte[] var1 = musicSampleIndex.getArchive(0).getFile(0).getData();
         if(var1 == null) {
            return false;
         }

         method2341(var1);
         __cd_s = true;
      }

      return true;
   }

   static MusicSample readMusicSample(Index musicSampleIndex, int var1, int var2) {
      if(!method2343(musicSampleIndex)) {
         if (musicSampleIndex.getArchive(var1).getFile(var2) != null) {
            return null;
         }
      } else {
         byte[] var3 = musicSampleIndex.getArchive(var1).getFile(var2).getData();
         return var3 == null?null:new MusicSample(var3);
      }
      return null;
   }

   static {
      __cd_s = false;
   }
}
