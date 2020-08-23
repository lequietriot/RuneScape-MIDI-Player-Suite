package main;

import main.utils.ByteBufferUtils;

import java.util.concurrent.ScheduledExecutorService;

public class PcmPlayer {

   public static int pcmPlayer_sampleRate = 44100; //22050
   protected static boolean pcmPlayer_stereo;
   static ScheduledExecutorService soundSystemExecutor;
   static int field1423;
   protected int[] samples;
   PcmStream stream;
   int field1433;
   long timeMs;
   int capacity;
   int field1431;
   int field1432;
   long field1441;
   int field1436;
   int field1435;
   int field1428;
   long field1424;
   boolean field1438;
   int field1439;
   PcmStream[] field1440;
   PcmStream[] field1434;

   protected PcmPlayer() {
      this.field1433 = 32;
      this.timeMs = System.currentTimeMillis();
      this.field1441 = 0L;
      this.field1436 = 0;
      this.field1435 = 0;
      this.field1428 = 0;
      this.field1424 = 0L;
      this.field1438 = true;
      this.field1439 = 0;
      this.field1440 = new PcmStream[8];
      this.field1434 = new PcmStream[8];
   }

   protected void init() throws Exception {
   }

   protected void open(int var1) throws Exception {
   }

   protected int position() throws Exception {
      return this.capacity;
   }

   protected void write() throws Exception {
   }

   protected void close() {
   }

   protected void discard() throws Exception {
   }

   public final synchronized void setStream(PcmStream var1) {
      this.stream = var1;
   }

   public final synchronized void run() {
      if (this.samples != null) {
         long var1 = System.currentTimeMillis();

         try {
            if (0L != this.field1441) {
               if (var1 < this.field1441) {
                  return;
               }

               this.open(this.capacity);
               this.field1441 = 0L;
               this.field1438 = true;
            }

            int var3 = this.position();
            if (this.field1428 - var3 > this.field1436) {
               this.field1436 = this.field1428 - var3;
            }

            int var4 = this.field1431 + this.field1432;
            if (var4 + 256 > 16384) {
               var4 = 16128;
            }

            if (var4 + 256 > this.capacity) {
               this.capacity += 1024;
               if (this.capacity > 16384) {
                  this.capacity = 16384;
               }

               this.close();
               this.open(this.capacity);
               var3 = 0;
               this.field1438 = true;
               if (var4 + 256 > this.capacity) {
                  var4 = this.capacity - 256;
                  this.field1432 = var4 - this.field1431;
               }
            }

            while (var3 < var4) {
               this.fill(this.samples, 256);
               this.write();
               var3 += 256;
            }

            if (var1 > this.field1424) {
               if (!this.field1438) {
                  if (this.field1436 == 0 && this.field1435 == 0) {
                     this.close();
                     this.field1441 = var1 + 2000L;
                     return;
                  }

                  this.field1432 = Math.min(this.field1435, this.field1436);
                  this.field1435 = this.field1436;
               } else {
                  this.field1438 = false;
               }

               this.field1436 = 0;
               this.field1424 = 2000L + var1;
            }

            this.field1428 = var3;
         } catch (Exception var7) {
            this.close();
            this.field1441 = 2000L + var1;
         }

         try {
            if (var1 > 500000L + this.timeMs) {
               var1 = this.timeMs;
            }

            while (var1 > 5000L + this.timeMs) {
               this.skip(256);
               this.timeMs += (long)(256000 / pcmPlayer_sampleRate);
            }
         } catch (Exception var6) {
            this.timeMs = var1;
         }

      }
   }

   public final void method2537() {
      this.field1438 = true;
   }

   public final synchronized void tryDiscard() {
      this.field1438 = true;

      try {
         this.discard();
      } catch (Exception var2) {
         this.close();
         this.field1441 = System.currentTimeMillis() + 2000L;
      }

   }

   final void skip(int var1) {
      this.field1439 -= var1;
      if (this.field1439 < 0) {
         this.field1439 = 0;
      }

      if (this.stream != null) {
         this.stream.skip(var1);
      }

   }

   final void fill(int[] var1, int var2) {
      int var3 = var2;
      if (pcmPlayer_stereo) {
         var3 = var2 << 1;
      }

      ByteBufferUtils.clearIntArray(var1, 0, var3);
      this.field1439 -= var2;
      if (this.stream != null && this.field1439 <= 0) {
         this.field1439 += pcmPlayer_sampleRate >> 4;
         MidiPcmStream.PcmStream_disable(this.stream);
         this.method2571(this.stream, this.stream.vmethod2820());
         int var4 = 0;
         int var5 = 255;

         int var6;
         PcmStream var10;
         label104:
         for (var6 = 7; var5 != 0; --var6) {
            int var7;
            int var8;
            if (var6 < 0) {
               var7 = var6 & 3;
               var8 = -(var6 >> 2);
            } else {
               var7 = var6;
               var8 = 0;
            }

            for (int var9 = var5 >>> var7 & 286331153; var9 != 0; var9 >>>= 4) {
               if ((var9 & 1) != 0) {
                  var5 &= ~(1 << var7);
                  var10 = null;
                  PcmStream var11 = this.field1440[var7];

                  label98:
                  while (true) {
                     while (true) {
                        if (var11 == null) {
                           break label98;
                        }

                        AbstractSound var12 = var11.sound;
                        if (var12 != null && var12.position > var8) {
                           var5 |= 1 << var7;
                           var10 = var11;
                           var11 = var11.after;
                        } else {
                           var11.active = true;
                           int var13 = var11.vmethod2820();
                           var4 += var13;
                           if (var12 != null) {
                              var12.position += var13;
                           }

                           if (var4 >= this.field1433) {
                              break label104;
                           }

                           PcmStream var14 = var11.firstSubStream();
                           if (var14 != null) {
                              for (int var15 = var11.field1507; var14 != null; var14 = var11.nextSubStream()) {
                                 this.method2571(var14, var15 * var14.vmethod2820() >> 8);
                              }
                           }

                           PcmStream var18 = var11.after;
                           var11.after = null;
                           if (var10 == null) {
                              this.field1440[var7] = var18;
                           } else {
                              var10.after = var18;
                           }

                           if (var18 == null) {
                              this.field1434[var7] = var10;
                           }

                           var11 = var18;
                        }
                     }
                  }
               }

               var7 += 4;
               ++var8;
            }
         }

         for (var6 = 0; var6 < 8; ++var6) {
            PcmStream var16 = this.field1440[var6];
            PcmStream[] var17 = this.field1440;
            this.field1434[var6] = null;

            for (var17[var6] = null; var16 != null; var16 = var10) {
               var10 = var16.after;
               var16.after = null;
            }
         }
      }

      if (this.field1439 < 0) {
         this.field1439 = 0;
      }

      if (this.stream != null) {
         this.stream.update(var1, 0, var2);
      }

      this.timeMs = System.currentTimeMillis();
   }

   final void method2571(PcmStream var1, int var2) {
      int var3 = var2 >> 5;
      PcmStream var4 = this.field1434[var3];
      if (var4 == null) {
         this.field1440[var3] = var1;
      } else {
         var4.after = var1;
      }

      this.field1434[var3] = var1;
      var1.field1507 = var2;
   }

}
