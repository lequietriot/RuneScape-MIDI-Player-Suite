package main;

public class MusicPatchPcmStream extends PcmStream {

   static int __hq_qg;
   MidiPcmStream superStream;
   NodeDeque queue;
   PcmStreamMixer mixer;

   public MusicPatchPcmStream(MidiPcmStream var1) {
      this.queue = new NodeDeque();
      this.mixer = new PcmStreamMixer();
      this.superStream = var1;
   }

   void __m_379(MusicPatchNode var1, int[] var2, int var3, int var4, int var5) {
      if((this.superStream.switchArray[var1.volumeValue] & 4) != 0 && var1.__a < 0) {
         int var6 = this.superStream.percentageArray[var1.volumeValue] / AudioConstants.systemSampleRate;

         while(true) {
            int var7 = (var6 + 1048575 - var1.__b) / var6;
            if(var7 > var4) {
               var1.__b += var4 * var6;
               break;
            }

            var1.stream.__e_172(var2, var3, var7);
            var3 += var7;
            var4 -= var7;
            var1.__b += var7 * var6 - 1048576;
            int var8 = AudioConstants.systemSampleRate / 100;
            int var9 = 262144 / var6;
            if(var9 < var8) {
               var8 = var9;
            }

            RawPcmStream var10 = var1.stream;
            if(this.superStream.customEffectArray[var1.volumeValue] == 0) {
               var1.stream = RawPcmStream.method2524(var1.rawSound, var10.__ab_194(), var10.__s_185(), var10.__t_186());
            } else {
               var1.stream = RawPcmStream.method2524(var1.rawSound, var10.__ab_194(), 0, var10.__t_186());
               this.superStream.__h_346(var1, var1.patch.generators[var1.__u] < 0);
               var1.stream.__c_190(var8, var10.__s_185());
            }

            if(var1.patch.generators[var1.__u] < 0) {
               var1.stream.setLoopOnSample(-1);
            }

            var10.__v_192(var8);
            var10.__e_172(var2, var3, var5 - var3);
            if(var10.__at_196()) {
               this.mixer.addSubStream(var10);
            }
         }
      }

      var1.stream.__e_172(var2, var3, var4);
   }

   void __f_380(MusicPatchNode var1, int var2) {
      if((this.superStream.switchArray[var1.volumeValue] & 4) != 0 && var1.__a < 0) {
         int var3 = this.superStream.percentageArray[var1.volumeValue] / AudioConstants.systemSampleRate;
         int var4 = (var3 + 1048575 - var1.__b) / var3;
         var1.__b = var3 * var2 + var1.__b & 1048575;
         if(var4 <= var2) {
            if(this.superStream.customEffectArray[var1.volumeValue] == 0) {
               var1.stream = RawPcmStream.method2524(var1.rawSound, var1.stream.__ab_194(), var1.stream.__s_185(), var1.stream.__t_186());
            } else {
               var1.stream = RawPcmStream.method2524(var1.rawSound, var1.stream.__ab_194(), 0, var1.stream.__t_186());
               this.superStream.__h_346(var1, var1.patch.generators[var1.__u] < 0);
            }

            if(var1.patch.generators[var1.__u] < 0) {
               var1.stream.setLoopOnSample(-1);
            }

            var2 = var1.__b / var3;
         }
      }

      var1.stream.__d_173(var2);
   }

   protected PcmStream firstSubStream() {
      MusicPatchNode var1 = (MusicPatchNode)this.queue.last();
      return (PcmStream)(var1 == null?null:(var1.stream != null?var1.stream:this.nextSubStream()));
   }

   protected PcmStream nextSubStream() {
      MusicPatchNode var1;
      do {
         var1 = (MusicPatchNode)this.queue.previous();
         if(var1 == null) {
            return null;
         }
      } while(var1.stream == null);

      return var1.stream;
   }

   protected int __l_171() {
      return 0;
   }

   protected void __e_172(int[] var1, int var2, int var3) {
      this.mixer.__e_172(var1, var2, var3);

      for(MusicPatchNode var4 = (MusicPatchNode)this.queue.last(); var4 != null; var4 = (MusicPatchNode)this.queue.previous()) {
         if(!this.superStream.__ba_368(var4)) {
            int var5 = var2;
            int var6 = var3;

            do {
               if(var6 <= var4.__y) {
                  this.__m_379(var4, var1, var5, var6, var6 + var5);
                  var4.__y -= var6;
                  break;
               }

               this.__m_379(var4, var1, var5, var4.__y, var6 + var5);
               var5 += var4.__y;
               var6 -= var4.__y;
            } while(!this.superStream.__bb_369(var4, var1, var5, var6));
         }
      }

   }

   protected void __d_173(int var1) {
      this.mixer.__d_173(var1);

      for(MusicPatchNode var2 = (MusicPatchNode)this.queue.last(); var2 != null; var2 = (MusicPatchNode)this.queue.previous()) {
         if(!this.superStream.__ba_368(var2)) {
            int var3 = var1;

            do {
               if(var3 <= var2.__y) {
                  this.__f_380(var2, var3);
                  var2.__y -= var3;
                  break;
               }

               this.__f_380(var2, var2.__y);
               var3 -= var2.__y;
            } while(!this.superStream.__bb_369(var2, (int[])null, 0, var3));
         }
      }

   }
}
