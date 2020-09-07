package main.utils;
import java.math.BigInteger;
import java.util.logging.Logger;

public class Buffer extends Node {

   static int[] __gr_q;
   static long[] __gr_o;
   public byte[] array;
   public int index;

   public Buffer(int var1) {
      this.array = Canvas.method862(var1);
      this.index = 0;
   }

   public Buffer(byte[] var1) {
      this.array = var1;
      this.index = 0;
   }
   
   public void __f_295() {
      if(this.array != null) {
         VertexNormal.method2984(this.array);
      }

      this.array = null;
   }

   public void writeByte(int var1) {
      this.array[++this.index - 1] = (byte)var1;
   }

   public void writeShort(int var1) {
      this.array[++this.index - 1] = (byte)(var1 >> 8);
      this.array[++this.index - 1] = (byte)var1;
   }
   
   public void writeMedium(int var1) {
      this.array[++this.index - 1] = (byte)(var1 >> 16);
      this.array[++this.index - 1] = (byte)(var1 >> 8);
      this.array[++this.index - 1] = (byte)var1;
   }
   
   public void writeInt(int var1) {
      this.array[++this.index - 1] = (byte)(var1 >> 24);
      this.array[++this.index - 1] = (byte)(var1 >> 16);
      this.array[++this.index - 1] = (byte)(var1 >> 8);
      this.array[++this.index - 1] = (byte)var1;
   }

   public void writeLongMedium(long var1) {
      this.array[++this.index - 1] = (byte)((int)(var1 >> 40));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 32));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 24));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 16));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 8));
      this.array[++this.index - 1] = (byte)((int)var1);
   }

   public void writeLong(long var1) {
      this.array[++this.index - 1] = (byte)((int)(var1 >> 56));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 48));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 40));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 32));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 24));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 16));
      this.array[++this.index - 1] = (byte)((int)(var1 >> 8));
      this.array[++this.index - 1] = (byte)((int)var1);
   }

   public void writeBoolean(boolean var1) {
      this.writeByte(var1?1:0);
   }

   public void __s_297(byte[] var1, int var2, int var3) {
      for(int var4 = var2; var4 < var3 + var2; ++var4) {
         this.array[++this.index - 1] = var1[var4];
      }

   }

   public void __t_298(int var1) {
      this.array[this.index - var1 - 4] = (byte)(var1 >> 24);
      this.array[this.index - var1 - 3] = (byte)(var1 >> 16);
      this.array[this.index - var1 - 2] = (byte)(var1 >> 8);
      this.array[this.index - var1 - 1] = (byte)var1;
   }

   public void __y_299(int var1) {
      this.array[this.index - var1 - 2] = (byte)(var1 >> 8);
      this.array[this.index - var1 - 1] = (byte)var1;
   }
   
   public void __h_300(int var1) {
      this.array[this.index - var1 - 1] = (byte)var1;
   }

   public void writeSmartByteShort(int var1) {
      if(var1 >= 0 && var1 < 128) {
         this.writeByte(var1);
      } else {
         if(var1 < 0 || var1 >= 32768) {
            throw new IllegalArgumentException();
         }

         this.writeShort(var1 + 32768);
      }

   }

   public void __c_301(int var1) {
      if((var1 & -128) != 0) {
         if((var1 & -16384) != 0) {
            if((var1 & -2097152) != 0) {
               if((var1 & -268435456) != 0) {
                  this.writeByte(var1 >>> 28 | 128);
               }

               this.writeByte(var1 >>> 21 | 128);
            }

            this.writeByte(var1 >>> 14 | 128);
         }

         this.writeByte(var1 >>> 7 | 128);
      }

      this.writeByte(var1 & 127);
   }

   public int readUnsignedByte() {
      return this.array[++this.index - 1] & 255;
   }

   public byte readByte() {
      return this.array[++this.index - 1];
   }

   public int __ag_302() {
      this.index += 2;
      return (this.array[this.index - 1] & 255) + ((this.array[this.index - 2] & 255) << 8);
   }

   public int __aq_303() {
      this.index += 2;
      int var1 = (this.array[this.index - 1] & 255) + ((this.array[this.index - 2] & 255) << 8);
      if(var1 > 32767) {
         var1 -= 65536;
      }

      return var1;
   }

   public int readMedium() {
      this.index += 3;
      return ((this.array[this.index - 3] & 255) << 16) + (this.array[this.index - 1] & 255) + ((this.array[this.index - 2] & 255) << 8);
   }

   public int readInt() {
      this.index += 4;
      return ((this.array[this.index - 3] & 255) << 16) + (this.array[this.index - 1] & 255) + ((this.array[this.index - 2] & 255) << 8) + ((this.array[this.index - 4] & 255) << 24);
   }

   public long readLong() {
      long var1 = (long)this.readInt() & 4294967295L;
      long var3 = (long)this.readInt() & 4294967295L;
      return (var1 << 32) + var3;
   }

   public boolean readBoolean() {
      return (this.readUnsignedByte() & 1) == 1;
   }

   public String __aw_304() {
      byte var1 = this.array[++this.index - 1];
      if(var1 != 0) {
         throw new IllegalStateException("");
      } else {
         int var2 = this.__as_311();
         if(var2 + this.index > this.array.length) {
            throw new IllegalStateException("");
         } else {
            byte[] var3 = this.array;
            int var4 = this.index;
            char[] var5 = new char[var2];
            int var6 = 0;
            int var7 = var4;

            int var8;
            for(int var9 = var4 + var2; var7 < var9; var5[var6++] = (char)var8) {
               int var10 = var3[var7++] & 255;
               if(var10 < 128) {
                  if(var10 == 0) {
                     var8 = 65533;
                  } else {
                     var8 = var10;
                  }
               } else if(var10 < 192) {
                  var8 = 65533;
               } else if(var10 < 224) {
                  if(var7 < var9 && (var3[var7] & 192) == 128) {
                     var8 = (var10 & 31) << 6 | var3[var7++] & 63;
                     if(var8 < 128) {
                        var8 = 65533;
                     }
                  } else {
                     var8 = 65533;
                  }
               } else if(var10 < 240) {
                  if(var7 + 1 < var9 && (var3[var7] & 192) == 128 && (var3[var7 + 1] & 192) == 128) {
                     var8 = (var10 & 15) << 12 | (var3[var7++] & 63) << 6 | var3[var7++] & 63;
                     if(var8 < 2048) {
                        var8 = 65533;
                     }
                  } else {
                     var8 = 65533;
                  }
               } else if(var10 < 248) {
                  if(var7 + 2 < var9 && (var3[var7] & 192) == 128 && (var3[var7 + 1] & 192) == 128 && (var3[var7 + 2] & 192) == 128) {
                     var8 = (var10 & 7) << 18 | (var3[var7++] & 63) << 12 | (var3[var7++] & 63) << 6 | var3[var7++] & 63;
                     if(var8 >= 65536 && var8 <= 1114111) {
                        var8 = 65533;
                     } else {
                        var8 = 65533;
                     }
                  } else {
                     var8 = 65533;
                  }
               } else {
                  var8 = 65533;
               }
            }

            String var11 = new String(var5, 0, var6);
            this.index += var2;
            return var11;
         }
      }
   }

   public void __al_305(byte[] var1, int var2, int var3) {
      for(int var4 = var2; var4 < var3 + var2; ++var4) {
         var1[var4] = this.array[++this.index - 1];
      }

   }

   public int __ab_306() {
      int var1 = this.array[this.index] & 255;
      return var1 < 128?this.readUnsignedByte() - 64:this.__ag_302() - 49152;
   }

   public int __ae_307() {
      int var1 = this.array[this.index] & 255;
      return var1 < 128?this.readUnsignedByte():this.__ag_302() - 32768;
   }

   public int __at_308() {
      int var1 = 0;

      int var2;
      for(var2 = this.__ae_307(); var2 == 32767; var2 = this.__ae_307()) {
         var1 += 32767;
      }

      var1 += var2;
      return var1;
   }

   public int __ad_309() {
      return this.array[this.index] < 0?this.readInt() & Integer.MAX_VALUE:this.__ag_302();
   }

   public int __ap_310() {
      if(this.array[this.index] < 0) {
         return this.readInt() & Integer.MAX_VALUE;
      } else {
         int var1 = this.__ag_302();
         return var1 == 32767?-1:var1;
      }
   }

   public int __as_311() {
      byte var1 = this.array[++this.index - 1];

      int var2;
      for(var2 = 0; var1 < 0; var1 = this.array[++this.index - 1]) {
         var2 = (var2 | var1 & 127) << 7;
      }

      return var2 | var1;
   }

   public void xteaEncryptAll(int[] var1) {
      int var2 = this.index / 8;
      this.index = 0;

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = this.readInt();
         int var5 = this.readInt();
         int var6 = 0;
         int var7 = -1640531527;

         for(int var8 = 32; var8-- > 0; var5 += var4 + (var4 << 4 ^ var4 >>> 5) ^ var1[var6 >>> 11 & 3] + var6) {
            var4 += var5 + (var5 << 4 ^ var5 >>> 5) ^ var6 + var1[var6 & 3];
            var6 += var7;
         }

         this.index -= 8;
         this.writeInt(var4);
         this.writeInt(var5);
      }

   }

   public void xteaDecryptAll(int[] var1) {
      int var2 = this.index / 8;
      this.index = 0;

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = this.readInt();
         int var5 = this.readInt();
         int var6 = -957401312;
         int var7 = -1640531527;

         for(int var8 = 32; var8-- > 0; var4 -= var5 + (var5 << 4 ^ var5 >>> 5) ^ var6 + var1[var6 & 3]) {
            var5 -= var4 + (var4 << 4 ^ var4 >>> 5) ^ var1[var6 >>> 11 & 3] + var6;
            var6 -= var7;
         }

         this.index -= 8;
         this.writeInt(var4);
         this.writeInt(var5);
      }

   }

   public void xteaEncrypt(int[] var1, int var2, int var3) {
      int var4 = this.index;
      this.index = var2;
      int var5 = (var3 - var2) / 8;

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = this.readInt();
         int var8 = this.readInt();
         int var9 = 0;
         int var10 = -1640531527;

         for(int var11 = 32; var11-- > 0; var8 += var7 + (var7 << 4 ^ var7 >>> 5) ^ var1[var9 >>> 11 & 3] + var9) {
            var7 += var8 + (var8 << 4 ^ var8 >>> 5) ^ var9 + var1[var9 & 3];
            var9 += var10;
         }

         this.index -= 8;
         this.writeInt(var7);
         this.writeInt(var8);
      }

      this.index = var4;
   }

   public void xteaDecrypt(int[] var1, int var2, int var3) {
      int var4 = this.index;
      this.index = var2;
      int var5 = (var3 - var2) / 8;

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = this.readInt();
         int var8 = this.readInt();
         int var9 = -957401312;
         int var10 = -1640531527;

         for(int var11 = 32; var11-- > 0; var7 -= var8 + (var8 << 4 ^ var8 >>> 5) ^ var9 + var1[var9 & 3]) {
            var8 -= var7 + (var7 << 4 ^ var7 >>> 5) ^ var1[var9 >>> 11 & 3] + var9;
            var9 -= var10;
         }

         this.index -= 8;
         this.writeInt(var7);
         this.writeInt(var8);
      }

      this.index = var4;
   }

   public void encryptRsa(BigInteger var1, BigInteger var2) {
      int var3 = this.index;
      this.index = 0;
      byte[] var4 = new byte[var3];
      this.__al_305(var4, 0, var3);
      BigInteger var5 = new BigInteger(var4);
      BigInteger var6 = var5.modPow(var1, var2);
      byte[] var7 = var6.toByteArray();
      this.index = 0;
      this.writeShort(var7.length);
      Logger.getAnonymousLogger().warning("unsigned short " + var7.length);
      this.__s_297(var7, 0, var7.length);
   }

   public int __aa_312(int var1) {
      byte[] var2 = this.array;
      int var3 = this.index;
      int var4 = -1;

      for(int var5 = var1; var5 < var3; ++var5) {
         var4 = var4 >>> 8 ^ __gr_q[(var4 ^ var2[var5]) & 255];
      }

      var4 = ~var4;
      this.writeInt(var4);
      return var4;
   }

   public boolean __ax_313() {
      this.index -= 4;
      byte[] var1 = this.array;
      int var2 = this.index;
      int var3 = -1;

      int var4;
      for(var4 = 0; var4 < var2; ++var4) {
         var3 = var3 >>> 8 ^ __gr_q[(var3 ^ var1[var4]) & 255];
      }

      var3 = ~var3;
      var4 = this.readInt();
      return var4 == var3;
   }

   public void __af_314(int var1) {
      this.array[++this.index - 1] = (byte)(var1 + 128);
   }

   public void __ai_315(int var1) {
      this.array[++this.index - 1] = (byte)(0 - var1);
   }

   public void __ba_316(int var1) {
      this.array[++this.index - 1] = (byte)(128 - var1);
   }

   public int __bb_317() {
      return this.array[++this.index - 1] - 128 & 255;
   }

   public int readUnsignedByteNegate() {
      return 0 - this.array[++this.index - 1] & 255;
   }

   public int __bq_318() {
      return 128 - this.array[++this.index - 1] & 255;
   }

   public byte __bn_319() {
      return (byte)(this.array[++this.index - 1] - 128);
   }

   public byte __bk_320() {
      return (byte)(0 - this.array[++this.index - 1]);
   }

   public byte __bd_321() {
      return (byte)(128 - this.array[++this.index - 1]);
   }

   public void writeShortLE(int var1) {
      this.array[++this.index - 1] = (byte)var1;
      this.array[++this.index - 1] = (byte)(var1 >> 8);
   }

   public void __bo_322(int var1) {
      this.array[++this.index - 1] = (byte)(var1 >> 8);
      this.array[++this.index - 1] = (byte)(var1 + 128);
   }

   public void __bx_323(int var1) {
      this.array[++this.index - 1] = (byte)(var1 + 128);
      this.array[++this.index - 1] = (byte)(var1 >> 8);
   }

   public int __by_324() {
      this.index += 2;
      return ((this.array[this.index - 1] & 255) << 8) + (this.array[this.index - 2] & 255);
   }

   public int __bu_325() {
      this.index += 2;
      return (this.array[this.index - 1] - 128 & 255) + ((this.array[this.index - 2] & 255) << 8);
   }

   public int __bm_326() {
      this.index += 2;
      return ((this.array[this.index - 1] & 255) << 8) + (this.array[this.index - 2] - 128 & 255);
   }

   public int __bl_327() {
      this.index += 2;
      int var1 = ((this.array[this.index - 1] & 255) << 8) + (this.array[this.index - 2] & 255);
      if(var1 > 32767) {
         var1 -= 65536;
      }

      return var1;
   }

   public int __br_328() {
      this.index += 2;
      int var1 = ((this.array[this.index - 1] & 255) << 8) + (this.array[this.index - 2] - 128 & 255);
      if(var1 > 32767) {
         var1 -= 65536;
      }

      return var1;
   }

   public int __bj_329() {
      this.index += 3;
      return (this.array[this.index - 3] & 255) + ((this.array[this.index - 2] & 255) << 8) + ((this.array[this.index - 1] & 255) << 16);
   }

   public void writeIntLE(int var1) {
      this.array[++this.index - 1] = (byte)var1;
      this.array[++this.index - 1] = (byte)(var1 >> 8);
      this.array[++this.index - 1] = (byte)(var1 >> 16);
      this.array[++this.index - 1] = (byte)(var1 >> 24);
   }

   public void writeIntME(int var1) {
      this.array[++this.index - 1] = (byte)(var1 >> 8);
      this.array[++this.index - 1] = (byte)var1;
      this.array[++this.index - 1] = (byte)(var1 >> 24);
      this.array[++this.index - 1] = (byte)(var1 >> 16);
   }

   public void writeIntLE16(int var1) {
      this.array[++this.index - 1] = (byte)(var1 >> 16);
      this.array[++this.index - 1] = (byte)(var1 >> 24);
      this.array[++this.index - 1] = (byte)var1;
      this.array[++this.index - 1] = (byte)(var1 >> 8);
   }

   public int __bt_330() {
      this.index += 4;
      return (this.array[this.index - 4] & 255) + ((this.array[this.index - 3] & 255) << 8) + ((this.array[this.index - 2] & 255) << 16) + ((this.array[this.index - 1] & 255) << 24);
   }

   public int __bp_331() {
      this.index += 4;
      return ((this.array[this.index - 2] & 255) << 24) + ((this.array[this.index - 4] & 255) << 8) + (this.array[this.index - 3] & 255) + ((this.array[this.index - 1] & 255) << 16);
   }

   public int __bf_332() {
      this.index += 4;
      return ((this.array[this.index - 1] & 255) << 8) + ((this.array[this.index - 4] & 255) << 16) + (this.array[this.index - 2] & 255) + ((this.array[this.index - 3] & 255) << 24);
   }

   public void __bh_333(byte[] var1, int var2, int var3) {
      for(int var4 = var2; var4 < var3 + var2; ++var4) {
         var1[var4] = (byte)(this.array[++this.index - 1] - 128);
      }

   }

   static {
      __gr_q = new int[256];

      int var0;
      for(int var1 = 0; var1 < 256; ++var1) {
         int var2 = var1;

         for(var0 = 0; var0 < 8; ++var0) {
            if((var2 & 1) == 1) {
               var2 = var2 >>> 1 ^ -306674912;
            } else {
               var2 >>>= 1;
            }
         }

         __gr_q[var1] = var2;
      }

      __gr_o = new long[256];

      for(var0 = 0; var0 < 256; ++var0) {
         long var4 = (long)var0;

         for(int var3 = 0; var3 < 8; ++var3) {
            if((var4 & 1L) == 1L) {
               var4 = var4 >>> 1 ^ -3932672073523589310L;
            } else {
               var4 >>>= 1;
            }
         }

         __gr_o[var0] = var4;
      }

   }

   public int readVarInt() {
      byte var1 = this.array[++this.index - 1];

      int var2;
      for (var2 = 0; var1 < 0; var1 = this.array[++this.index - 1]) {
         var2 = (var2 | var1 & 127) << 7;
      }

      return var2 | var1;
   }

   public int readUnsignedShort() {
      this.index += 2;
      return (this.array[this.index - 1] & 255) + ((this.array[this.index - 2] & 255) << 8);
   }

   public static byte[] writeSignedVarInt(int value) {
      return writeUnsignedVarInt((value << 1) ^ (value >> 31));
   }

   public static byte[] writeUnsignedVarInt(int value) {
      byte[] byteArrayList = new byte[10];
      int i = 0;
      while ((value & 0xFFFFFF80) != 0L) {
         byteArrayList[i++] = ((byte) ((value & 0x7F) | 0x80));
         value >>>= 7;
      }
      byteArrayList[i] = ((byte) (value & 0x7F));
      byte[] out = new byte[i + 1];
      for (; i >= 0; i--) {
         out[i] = byteArrayList[i];
      }
      return out;
   }
}
