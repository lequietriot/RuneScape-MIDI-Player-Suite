package main;

public class MusicPatchPcmStream extends PcmStream {

   MidiPcmStream superStream;
   NodeDeque queue;
   PcmStreamMixer mixer;

   MusicPatchPcmStream(MidiPcmStream midiPcmStream) {
      this.queue = new NodeDeque();
      this.mixer = new PcmStreamMixer();
      this.superStream = midiPcmStream;
   }

   void method3992(MusicPatchNode var1, int[] var2, int var3, int var4, int var5) {
      if ((this.superStream.sustain[var1.currentTrack] & 4) != 0 && var1.field2450 < 0) {
         int var6 = this.superStream.field2433[var1.currentTrack] / PcmPlayer.pcmPlayer_sampleRate;

         while (true) {
            int var7 = (var6 + 1048575 - var1.field2462) / var6;
            if (var7 > var4) {
               var1.field2462 += var4 * var6;
               break;
            }

            var1.stream.fill(var2, var3, var7);
            var3 += var7;
            var4 -= var7;
            var1.field2462 += var7 * var6 - 1048576;
            int var8 = PcmPlayer.pcmPlayer_sampleRate / 100;
            int var9 = 262144 / var6;
            if (var9 < var8) {
               var8 = var9;
            }

            RawPcmStream var10 = var1.stream;
            if (this.superStream.generalPurpose1[var1.currentTrack] == 0) {
               var1.stream = RawPcmStream.method2685(var1.audioBuffer, var10.method2670(), var10.method2662(), var10.method2663());
            } else {
               var1.stream = RawPcmStream.method2685(var1.audioBuffer, var10.method2670(), 0, var10.method2663());
               this.superStream.method3852(var1, var1.patch.pitchOffset[var1.currentNotePitch] < 0);
               var1.stream.method2666(var8, var10.method2662());
            }

            if (var1.patch.pitchOffset[var1.currentNotePitch] < 0) {
               assert var1.stream != null;
               var1.stream.setNumLoops(-1);
            }

            var10.method2706(var8);
            var10.fill(var2, var3, var5 - var3);
            if (var10.method2672()) {
               this.mixer.addSubStream(var10);
            }
         }
      }

      var1.stream.fill(var2, var3, var4);
   }

   void method3989(MusicPatchNode musicPatchNode, int var2) {
      if ((this.superStream.sustain[musicPatchNode.currentTrack] & 4) != 0 && musicPatchNode.field2450 < 0) {
         int var3 = this.superStream.field2433[musicPatchNode.currentTrack] / PcmPlayer.pcmPlayer_sampleRate;
         int var4 = (var3 + 1048575 - musicPatchNode.field2462) / var3;
         musicPatchNode.field2462 = var3 * var2 + musicPatchNode.field2462 & 1048575;
         if (var4 <= var2) {
            if (this.superStream.generalPurpose1[musicPatchNode.currentTrack] == 0) {
               musicPatchNode.stream = RawPcmStream.method2685(musicPatchNode.audioBuffer, musicPatchNode.stream.method2670(), musicPatchNode.stream.method2662(), musicPatchNode.stream.method2663());
            } else {
               musicPatchNode.stream = RawPcmStream.method2685(musicPatchNode.audioBuffer, musicPatchNode.stream.method2670(), 0, musicPatchNode.stream.method2663());
               this.superStream.method3852(musicPatchNode, musicPatchNode.patch.pitchOffset[musicPatchNode.currentNotePitch] < 0);
            }

            if (musicPatchNode.patch.pitchOffset[musicPatchNode.currentNotePitch] < 0) {
               assert musicPatchNode.stream != null;
               musicPatchNode.stream.setNumLoops(-1);
            }

            var2 = musicPatchNode.field2462 / var3;
         }
      }

      assert musicPatchNode.stream != null;
      musicPatchNode.stream.skip(var2);
   }

   protected PcmStream firstSubStream() {
      MusicPatchNode var1 = (MusicPatchNode)this.queue.last();
      if (var1 == null) {
         return null;
      } else {
         return var1.stream != null ? var1.stream : this.nextSubStream();
      }
   }

   protected PcmStream nextSubStream() {
      MusicPatchNode var1;
      do {
         var1 = (MusicPatchNode)this.queue.previous();
         if (var1 == null) {
            return null;
         }
      } while(var1.stream == null);

      return var1.stream;
   }

   @Override
   protected int vmethod3984() {
      return 0;
   }

   protected void fill(int[] var1, int var2, int var3) {
      this.mixer.fill(var1, var2, var3);

      for (MusicPatchNode var6 = (MusicPatchNode)this.queue.last(); var6 != null; var6 = (MusicPatchNode)this.queue.previous()) {
         if (!this.superStream.method3826(var6)) {
            int var4 = var2;
            int var5 = var3;

            do {
               if (var5 <= var6.field2453) {
                  this.method3992(var6, var1, var4, var5, var4 + var5);
                  var6.field2453 -= var5;
                  break;
               }

               this.method3992(var6, var1, var4, var6.field2453, var4 + var5);
               var4 += var6.field2453;
               var5 -= var6.field2453;
            } while(!this.superStream.method3884(var6, var1, var4, var5));
         }
      }

   }

   protected void skip(int var1) {
      this.mixer.skip(var1);

      for (MusicPatchNode var3 = (MusicPatchNode)this.queue.last(); var3 != null; var3 = (MusicPatchNode)this.queue.previous()) {
         if (!this.superStream.method3826(var3)) {
            int var2 = var1;

            do {
               if (var2 <= var3.field2453) {
                  this.method3989(var3, var2);
                  var3.field2453 -= var2;
                  break;
               }

               this.method3989(var3, var3.field2453);
               var2 -= var3.field2453;
            } while(!this.superStream.method3884(var3, null, 0, var2));
         }
      }

   }

}
