package main;

import main.utils.class212;

public class PcmPlayer {

   static boolean isStereo;
   int[] samples;
   PcmStream stream0;
   int __z;
   long timeMs;
   int capacity;
   int frequency;
   int __y;
   long retryTimeMs;
   int __b;
   int __c;
   int nextPosition;
   long __p;
   boolean __v;
   int __ac;
   PcmStream[] __ay;
   PcmStream[] __ah;

   PcmPlayer() {
      this.__z = 32;
      this.timeMs = System.currentTimeMillis();
      this.retryTimeMs = 0L;
      this.__b = 0;
      this.__c = 0;
      this.nextPosition = 0;
      this.__p = 0L;
      this.__v = true;
      this.__ac = 0;
      this.__ay = new PcmStream[8];
      this.__ah = new PcmStream[8];
   }

   void init() throws Exception {
   }

   void open(int var1) throws Exception {
   }

   int position() throws Exception {
      return this.capacity;
   }

   void write() throws Exception {
   }

   void close() {
   }

   void discard() throws Exception {
   }

   public final synchronized void setStream(PcmStream var1) {
      this.stream0 = var1;
   }

   public final synchronized void run() {
      if(this.samples != null) {
         long var1 = System.currentTimeMillis();

         try {
            if(0L != this.retryTimeMs) {
               if(var1 < this.retryTimeMs) {
                  return;
               }

               this.open(this.capacity);
               this.retryTimeMs = 0L;
               this.__v = true;
            }

            int var3 = this.position();
            if(this.nextPosition - var3 > this.__b) {
               this.__b = this.nextPosition - var3;
            }

            int var4 = this.__y + this.frequency;
            if(var4 + 256 > 16384) {
               var4 = 16128;
            }

            if(var4 + 256 > this.capacity) {
               this.capacity += 1024;
               if(this.capacity > 16384) {
                  this.capacity = 16384;
               }

               this.close();
               this.open(this.capacity);
               var3 = 0;
               this.__v = true;
               if(var4 + 256 > this.capacity) {
                  var4 = this.capacity - 256;
                  this.__y = var4 - this.frequency;
               }
            }

            while(var3 < var4) {
               this.fill(this.samples, 256);
               this.write();
               var3 += 256;
            }

            if(var1 > this.__p) {
               if(!this.__v) {
                  if(this.__b == 0 && this.__c == 0) {
                     this.close();
                     this.retryTimeMs = 2000L + var1;
                     return;
                  }

                  this.__y = Math.min(this.__c, this.__b);
                  this.__c = this.__b;
               } else {
                  this.__v = false;
               }

               this.__b = 0;
               this.__p = 2000L + var1;
            }

            this.nextPosition = var3;
         } catch (Exception var6) {
            this.close();
            this.retryTimeMs = var1 + 2000L;
         }

         try {
            if(var1 > this.timeMs + 500000L) {
               var1 = this.timeMs;
            }

            while(var1 > 5000L + this.timeMs) {
               this.skip(256);
               this.timeMs += (long)(256000 / AudioConstants.systemSampleRate);
            }

         } catch (Exception var5) {
            this.timeMs = var1;
         }
      }

   }

   public final void __ac_176() {
      this.__v = true;
   }

   public final synchronized void tryDiscard() {
      this.__v = true;

      try {
         this.discard();
      } catch (Exception var2) {
         this.close();
         this.retryTimeMs = System.currentTimeMillis() + 2000L;
      }

   }

   final void skip(int var1) {
      this.__ac -= var1;
      if(this.__ac < 0) {
         this.__ac = 0;
      }

      if(this.stream0 != null) {
         this.stream0.__d_173(var1);
      }

   }

   final void fill(int[] var1, int var2) {
      int var3 = var2;
      if(isStereo) {
         var3 = var2 << 1;
      }

      class212.clearIntArray(var1, 0, var3);
      this.__ac -= var2;
      if(this.stream0 != null && this.__ac <= 0) {
         this.__ac += AudioConstants.systemSampleRate >> 4;
         MidiPcmStream.PcmStream_disable(this.stream0);
         this.__ab_177(this.stream0, this.stream0.__az_179());
         int var4 = 0;
         int var5 = 255;

         int var6;
         PcmStream var7;
         label106:
         for(var6 = 7; var5 != 0; --var6) {
            int var8;
            int var9;
            if(var6 < 0) {
               var8 = var6 & 3;
               var9 = -(var6 >> 2);
            } else {
               var8 = var6;
               var9 = 0;
            }

            for(int var10 = var5 >>> var8 & 286331153; var10 != 0; var10 >>>= 4) {
               if((var10 & 1) != 0) {
                  var5 &= ~(1 << var8);
                  var7 = null;
                  PcmStream var11 = this.__ay[var8];

                  label100:
                  while(true) {
                     while(true) {
                        if(var11 == null) {
                           break label100;
                        }

                        AbstractSound var12 = var11.sound;
                        if(var12 != null && var12.position > var9) {
                           var5 |= 1 << var8;
                           var7 = var11;
                           var11 = var11.after;
                        } else {
                           var11.active = true;
                           int var13 = var11.__l_171();
                           var4 += var13;
                           if(var12 != null) {
                              var12.position += var13;
                           }

                           if(var4 >= this.__z) {
                              break label106;
                           }

                           PcmStream var14 = var11.firstSubStream();
                           if(var14 != null) {
                              for(int var15 = var11.__s; var14 != null; var14 = var11.nextSubStream()) {
                                 this.__ab_177(var14, var15 * var14.__az_179() >> 8);
                              }
                           }

                           PcmStream var18 = var11.after;
                           var11.after = null;
                           if(var7 == null) {
                              this.__ay[var8] = var18;
                           } else {
                              var7.after = var18;
                           }

                           if(var18 == null) {
                              this.__ah[var8] = var7;
                           }

                           var11 = var18;
                        }
                     }
                  }
               }

               var8 += 4;
               ++var9;
            }
         }

         for(var6 = 0; var6 < 8; ++var6) {
            PcmStream var16 = this.__ay[var6];
            PcmStream[] var17 = this.__ay;
            this.__ah[var6] = null;

            for(var17[var6] = null; var16 != null; var16 = var7) {
               var7 = var16.after;
               var16.after = null;
            }
         }
      }

      if(this.__ac < 0) {
         this.__ac = 0;
      }

      if(this.stream0 != null) {
         this.stream0.__e_172(var1, 0, var2);
      }

      this.timeMs = System.currentTimeMillis();
   }

   final void __ab_177(PcmStream var1, int var2) {
      int var3 = var2 >> 5;
      PcmStream var4 = this.__ah[var3];
      if(var4 == null) {
         this.__ay[var3] = var1;
      } else {
         var4.after = var1;
      }

      this.__ah[var3] = var1;
      var1.__s = var2;
   }
}
