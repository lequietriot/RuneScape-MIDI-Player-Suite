package main;

public class MusicSampleResidue {

   int type;
   int end;
   int begin;
   int partitionSize;
   int classifications;
   int classBook;
   int[] books;

   MusicSampleResidue() {
      this.type = MusicSample.getBits(16);
      this.end = MusicSample.getBits(24);
      this.begin = MusicSample.getBits(24);
      this.partitionSize = MusicSample.getBits(24) + 1;
      this.classifications = MusicSample.getBits(6) + 1;
      this.classBook = MusicSample.getBits(8);
      int[] cascade = new int[this.classifications];

      int acc;
      for(acc = 0; acc < this.classifications; ++acc) {
         int highBits = 0;
         int lowBits = MusicSample.getBits(3);
         boolean validBit = MusicSample.getBit() != 0;
         if(validBit) {
            highBits = MusicSample.getBits(5);
         }

         cascade[acc] = highBits << 3 | lowBits;
      }

      this.books = new int[this.classifications * 8];

      for(acc = 0; acc < this.classifications * 8; ++acc) {
         this.books[acc] = (cascade[acc >> 3] & 1 << (acc & 7)) != 0? MusicSample.getBits(8) : -1;
      }

   }

   void decodeResidue(float[] var1, int var2, boolean var3) {
      int var4;
      for(var4 = 0; var4 < var2; ++var4) {
         var1[var4] = 0.0F;
      }

      if(!var3) {
         var4 = MusicSample.codebooks[this.classBook].dimensions;
         int var5 = this.begin - this.end;
         int var6 = var5 / this.partitionSize;
         int[] var7 = new int[var6];

         for(int var8 = 0; var8 < 8; ++var8) {
            int var9 = 0;

            while(var9 < var6) {
               int var10;
               int var11;
               if(var8 == 0) {
                  var10 = MusicSample.codebooks[this.classBook].getHuffmanRoot();

                  for(var11 = var4 - 1; var11 >= 0; --var11) {
                     if(var9 + var11 < var6) {
                        var7[var9 + var11] = var10 % this.classifications;
                     }

                     var10 /= this.classifications;
                  }
               }

               for(var10 = 0; var10 < var4; ++var10) {
                  var11 = var7[var9];
                  int var12 = this.books[var8 + var11 * 8];
                  if(var12 >= 0) {
                     int var13 = var9 * this.partitionSize + this.end;
                     MusicSampleCodebook var14 = MusicSample.codebooks[var12];
                     int var15;
                     if(this.type == 0) {
                        var15 = this.partitionSize / var14.dimensions;

                        for(int var19 = 0; var19 < var15; ++var19) {
                           float[] var20 = var14.method2307();

                           for(int var18 = 0; var18 < var14.dimensions; ++var18) {
                              var1[var13 + var19 + var18 * var15] += var20[var18];
                           }
                        }
                     } else {
                        var15 = 0;

                        while(var15 < this.partitionSize) {
                           float[] var16 = var14.method2307();

                           for(int var17 = 0; var17 < var14.dimensions; ++var17) {
                              var1[var13 + var15] += var16[var17];
                              ++var15;
                           }
                        }
                     }
                  }

                  ++var9;
                  if(var9 >= var6) {
                     break;
                  }
               }
            }
         }
      }

   }
}
