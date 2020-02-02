package main;

import main.utils.ByteBufferUtils;

public class MusicSampleCodebook {

   int dimensions;
   int entries;
   int[] entryLengths;
   int[] codebookMultiplicands;
   float[][] valueVector;
   int[] field1307;

   MusicSampleCodebook() {
      MusicSample.getBits(24);
      this.dimensions = MusicSample.getBits(16);
      this.entries = MusicSample.getBits(24);
      this.entryLengths = new int[this.entries];
      boolean ordered = MusicSample.getBit() != 0;
      int codebookLookupType;
      int i_2;
      int codebookValueBits;
      if(ordered) {
         codebookLookupType = 0;

         for(i_2 = MusicSample.getBits(5) + 1; codebookLookupType < this.entries; ++i_2) {
            int number = MusicSample.getBits(ByteBufferUtils.method634(this.entries - codebookLookupType));

            for(codebookValueBits = 0; codebookValueBits < number; ++codebookValueBits) {
               this.entryLengths[codebookLookupType++] = i_2;
            }
         }
      } else {
         boolean sparse = MusicSample.getBit() != 0;

         for(i_2 = 0; i_2 < this.entries; ++i_2) {
            if(sparse && MusicSample.getBit() == 0) {
               this.entryLengths[i_2] = 0;
            } else {
               this.entryLengths[i_2] = MusicSample.getBits(5) + 1;
            }
         }
      }

      this.createHuffmanTree();
      codebookLookupType = MusicSample.getBits(4);
      if(codebookLookupType > 0) {
         float codebookMinimumValue = MusicSample.float32Unpack(MusicSample.getBits(32));
         float codebookDeltaValue = MusicSample.float32Unpack(MusicSample.getBits(32));
         codebookValueBits = MusicSample.getBits(4) + 1;
         boolean codebookSequenceP = MusicSample.getBit() != 0;
         int codebookLookupValues;
         if(codebookLookupType == 1) {
            codebookLookupValues = method2310(this.entries, this.dimensions);
         } else {
            codebookLookupValues = this.entries * this.dimensions;
         }

         this.codebookMultiplicands = new int[codebookLookupValues];

         int i;
         for(i = 0; i < codebookLookupValues; ++i) {
            this.codebookMultiplicands[i] = MusicSample.getBits(codebookValueBits);
         }

         this.valueVector = new float[this.entries][this.dimensions];
         float last;
         int indexDivisor;
         int j;
         if(codebookLookupType == 1) {
            for(i = 0; i < this.entries; ++i) {
               last = 0.0F;
               indexDivisor = 1;

               for(j = 0; j < this.dimensions; ++j) {
                  int multiplicandOffset = i / indexDivisor % codebookLookupValues;
                  float valueVectorFloat = (float)this.codebookMultiplicands[multiplicandOffset] * codebookDeltaValue + codebookMinimumValue + last;
                  this.valueVector[i][j] = valueVectorFloat;
                  if(codebookSequenceP) {
                     last = valueVectorFloat;
                  }

                  indexDivisor *= codebookLookupValues;
               }
            }
         } else {
            for(i = 0; i < this.entries; ++i) {
               last = 0.0F;
               indexDivisor = i * this.dimensions;

               for(j = 0; j < this.dimensions; ++j) {
                  float valueVectorFloat2 = (float)this.codebookMultiplicands[indexDivisor] * codebookDeltaValue + codebookMinimumValue + last;
                  this.valueVector[i][j] = valueVectorFloat2;
                  if(codebookSequenceP) {
                     last = valueVectorFloat2;
                  }

                  ++indexDivisor;
               }
            }
         }
      }

   }

   void createHuffmanTree() {
      int[] entryLengths = new int[this.entries];
      int[] var2 = new int[33];

      int var3;
      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      int var9;
      int var10;
      for(var3 = 0; var3 < this.entries; ++var3) {
         var4 = this.entryLengths[var3];
         if(var4 != 0) {
            var5 = 1 << 32 - var4;
            var6 = var2[var4];
            entryLengths[var3] = var6;
            if((var6 & var5) != 0) {
               var7 = var2[var4 - 1];
            } else {
               var7 = var6 | var5;

               for(var8 = var4 - 1; var8 >= 1; --var8) {
                  var10 = var2[var8];
                  if(var10 != var6) {
                     break;
                  }

                  var9 = 1 << 32 - var8;
                  if((var10 & var9) != 0) {
                     var2[var8] = var2[var8 - 1];
                     break;
                  }

                  var2[var8] = var10 | var9;
               }
            }

            var2[var4] = var7;

            for(var8 = var4 + 1; var8 <= 32; ++var8) {
               var10 = var2[var8];
               if(var10 == var6) {
                  var2[var8] = var7;
               }
            }
         }
      }

      this.field1307 = new int[8];
      var10 = 0;

      for(var3 = 0; var3 < this.entries; ++var3) {
         var4 = this.entryLengths[var3];
         if(var4 != 0) {
            var5 = entryLengths[var3];
            var6 = 0;

            for(var7 = 0; var7 < var4; ++var7) {
               var8 = Integer.MIN_VALUE >>> var7;
               if((var5 & var8) != 0) {
                  if(this.field1307[var6] == 0) {
                     this.field1307[var6] = var10;
                  }

                  var6 = this.field1307[var6];
               } else {
                  ++var6;
               }

               if(var6 >= this.field1307.length) {
                  int[] var11 = new int[this.field1307.length * 2];

                  for(var9 = 0; var9 < this.field1307.length; ++var9) {
                     var11[var9] = this.field1307[var9];
                  }

                  this.field1307 = var11;
               }

               var8 >>>= 1;
            }

            this.field1307[var6] = ~var3;
            if(var6 >= var10) {
               var10 = var6 + 1;
            }
         }
      }

   }

   int getHuffmanRoot() {
      int var1;
      for(var1 = 0; this.field1307[var1] >= 0; var1 = MusicSample.getBit() != 0?this.field1307[var1]:var1 + 1) {
         ;
      }

      return ~this.field1307[var1];
   }

   float[] method2307() {
      return this.valueVector[this.getHuffmanRoot()];
   }

   static int method2310(int var0, int var1) {
      int var2;
      for(var2 = (int)Math.pow((double)var0, 1.0D / (double)var1) + 1; ByteBufferUtils.method3642(var2, var1) > var0; --var2) {
         ;
      }

      return var2;
   }
}
