package main;

import main.utils.ByteBufferUtils;
import main.utils.Node;
import org.displee.cache.index.Index;
import org.gagravarr.vorbis.VorbisAudioData;
import org.gagravarr.vorbis.VorbisFile;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class MusicSample extends Node {

   static byte[] source;
   static int byteIndex;
   static int root;
   static int blockSize_0;
   static int blockSize_1;
   static MusicSampleCodebook[] codebooks;
   static MusicSampleFloor[] floors;
   static MusicSampleResidue[] residues;
   static MusicSampleMapping[] mappings;
   static boolean[] noResidues;
   static int[] modes;
   static boolean initialized;
   static float[] window;
   static float[] __cd_r;
   static float[] __cd_p;
   static float[] __cd_v;
   static float[] __cd_ag;
   static float[] __cd_aq;
   static float[] __cd_aj;
   static int[] __cd_av;
   static int[] __cd_ar;
   byte[][] packets;
   int sampleRate;
   int sampleCount;
   int start;
   int end;
   boolean loopConsistency;
   float[] __t;
   int __y;
   int __h;
   boolean blockFlag;
   byte[] samples;
   int sampleLength;
   int soundIndices;

   MusicSample(byte[] var1) {
      this.read(var1);
   }

   public MusicSample(VorbisFile oggFile, int archiveID) throws IOException {

      if (archiveID == 0) {
         throw new RuntimeException("Error: Archive 0 is used for OGG setup data!");
      }

      else {
         this.encode(oggFile, archiveID);
      }
   }

   private void encode(VorbisFile oggFile, int archiveID) throws IOException {
      //Write the setup file first (To be stored in Index 14, Archive 0)
      File setupFile = new File("./0.dat/");
      FileOutputStream setupFileOutputStream = new FileOutputStream(setupFile);
      byte[] setupData = oggFile.getSetup().getData();
      byte[] rsSetupData = new byte[setupData.length - 6];
      rsSetupData[0] = (byte) 170;
      int newIndex = 1;
      for (int index = 7; index < rsSetupData.length; index++) {
         rsSetupData[newIndex] = setupData[index];
         newIndex++;
      }
      //Writes the new OGG setup data, with the " vorbis" header replaced
      setupFileOutputStream.write(rsSetupData);

      //Now, read the input OGG file and convert to RuneScape's format
      File file = new File("./" + archiveID + ".dat/");
      FileOutputStream fileOutputStream = new FileOutputStream(file);
      DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);

      if (oggFile.getTags().getComments("Sample Rate").size() != 0) {
         dataOutputStream.writeInt(Integer.parseInt(oggFile.getTags().getComments("Sample Rate").get(0))); //Sample Rate
      }
      else {
         dataOutputStream.writeInt(oggFile.getInfo().getSampleRate());
         System.out.println("Using default Sample Rate of " + oggFile.getInfo().getSampleRate() + ".");
      }

      if (oggFile.getTags().getComments("Sample Size").size() != 0) {
         dataOutputStream.writeInt(Integer.parseInt(oggFile.getTags().getComments("Sample Size").get(0))); //Audio Sample Size
      }
      else {
         dataOutputStream.writeInt(0);
         System.out.println("WARNING: No comment tag in OGG file for Sample size/length!");
      }

      if (oggFile.getTags().getComments("Loop Start").size() != 0) {
         dataOutputStream.writeInt(Integer.parseInt(oggFile.getTags().getComments("Loop Start").get(0))); //Sample Loop Start
      }
      else {
         dataOutputStream.writeInt(0);
         System.out.println("WARNING: No comment tag in OGG file for Sample Loop Start position!");
      }

      if (oggFile.getTags().getComments("Loop End").size() != 0) {
         dataOutputStream.writeInt(Integer.parseInt(oggFile.getTags().getComments("Loop End").get(0))); //Sample Loop End
      }
      else {
         dataOutputStream.writeInt(0);
         System.out.println("WARNING: No comment tag in OGG file for Sample Loop End position!");
      }

      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

      VorbisAudioData vorbisAudioData = oggFile.getNextAudioPacket();

      int packetAmount = 0;
      while (vorbisAudioData != null) {
         //Get the length of the audio packet
         int dataLength = vorbisAudioData.getData().length;
         //If the audio packet size is greater than 255, split the size over various bytes as needed
         while (dataLength > 255) {
            byteArrayOutputStream.write(255);
            dataLength = dataLength - 256;
         }
         //Write the length of the audio packet
         byteArrayOutputStream.write(dataLength);
         //Next, write the audio packet data after the length
         for (int index = 0; index < vorbisAudioData.getData().length; index++) {
            byteArrayOutputStream.write(vorbisAudioData.getData()[index]);
         }
         packetAmount++;
         vorbisAudioData = oggFile.getNextAudioPacket();
         //Repeat until all packets are written to the data file
      }
      //Finally, write the amount of packets in the output, as well as the data from the byte array.
      dataOutputStream.writeInt(packetAmount);
      dataOutputStream.write(byteArrayOutputStream.toByteArray());
      System.out.println("Successfully encoded ID: " + archiveID + " to the RuneScape format!");
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

      int packetCount = buffer.getInt();
      this.packets = new byte[packetCount][];

      for(int packet = 0; packet < packetCount; ++packet) {
         int size = 0;

         int offset;
         do {
            offset = buffer.get() & 0xFF;
            size += offset;
         } while(offset >= 255);

         byte[] packetData = new byte[size];
         buffer.get(packetData, 0, size);
         this.packets[packet] = packetData;
         System.out.println("Packet " + packet + " - " + size);
         System.out.println(Arrays.toString(packetData));
      }
   }

   float[] mdctFloatCompute(int var1) {
      setData(this.packets[var1], 0);
      getBit();
      int modeNumber = getBits(ByteBufferUtils.iLog(modes.length - 1));
      boolean blockFlag = noResidues[modeNumber];
      int n = blockFlag ? blockSize_1 : blockSize_0;
      boolean previousWindowFlag = false;
      boolean nextWindowFlag = false;

      if(blockFlag) {
         previousWindowFlag = getBit() != 0;
         nextWindowFlag = getBit() != 0;
      }

      int windowCenter = n >> 1;
      int leftWindowStart;
      int leftWindowEnd;
      int leftN;
      if(blockFlag && !previousWindowFlag) {
         leftWindowStart = (n >> 2) - (blockSize_0 >> 2);
         leftWindowEnd = (blockSize_0 >> 2) + (n >> 2);
         leftN = blockSize_0 >> 1;
      } else {
         leftWindowStart = 0;
         leftWindowEnd = windowCenter;
         leftN = n >> 1;
      }

      int rightWindowStart;
      int rightWindowEnd;
      int rightN;
      if(blockFlag && !nextWindowFlag) {
         rightWindowStart = n - (n >> 2) - (blockSize_0 >> 2);
         rightWindowEnd = (blockSize_0 >> 2) + (n - (n >> 2));
         rightN = blockSize_0 >> 1;
      } else {
         rightWindowStart = windowCenter;
         rightWindowEnd = n;
         rightN = n >> 1;
      }

      MusicSampleMapping mapping = mappings[modes[modeNumber]];
      int submapNumber = mapping.mux;
      int floorNumber = mapping.submapFloors[submapNumber];

      boolean allFloorsEmpty = !floors[floorNumber].decodeFloor();

      for(floorNumber = 0; floorNumber < mapping.submaps; ++floorNumber) {
         MusicSampleResidue residue = residues[mapping.submapResidues[floorNumber]];
         float[] pcm = window;
         residue.decodeResidue(pcm, n >> 1, allFloorsEmpty);
      }

      int floorIndex;
      if (!allFloorsEmpty) {
         floorNumber = mapping.mux;
         floorIndex = mapping.submapFloors[floorNumber];
         floors[floorIndex].computeFloor(window, n >> 1);
      }

      float[] var21;
      int var22;
      int var49;
      if(allFloorsEmpty) {
         for(floorNumber = n >> 1; floorNumber < n; ++floorNumber) {
            window[floorNumber] = 0.0F;
         }
      } else {
         floorNumber = n >> 1;
         floorIndex = n >> 2;
         var49 = n >> 3;
         var21 = window;

         for(var22 = 0; var22 < floorNumber; ++var22) {
            var21[var22] *= 0.5F;
         }

         for(var22 = floorNumber; var22 < n; ++var22) {
            var21[var22] = -var21[n - var22 - 1];
         }

         float[] var23 = blockFlag ? __cd_ag : __cd_r;
         float[] var24 = blockFlag ? __cd_aq : __cd_p;
         float[] var25 = blockFlag ? __cd_aj : __cd_v;
         int[] var26 = blockFlag ? __cd_ar : __cd_av;

         int var27;
         float var28;
         float x;
         float var30;
         float var31;
         for(var27 = 0; var27 < floorIndex; ++var27) {
            var28 = var21[var27 * 4] - var21[n - var27 * 4 - 1];
            x = var21[var27 * 4 + 2] - var21[n - var27 * 4 - 3];
            var30 = var23[var27 * 2];
            var31 = var23[var27 * 2 + 1];
            var21[n - var27 * 4 - 1] = var28 * var30 - x * var31;
            var21[n - var27 * 4 - 3] = var28 * var31 + x * var30;
         }

         float var32;
         float var33;
         for(var27 = 0; var27 < var49; ++var27) {
            var28 = var21[floorNumber + var27 * 4 + 3];
            x = var21[floorNumber + var27 * 4 + 1];
            var30 = var21[var27 * 4 + 3];
            var31 = var21[var27 * 4 + 1];
            var21[floorNumber + var27 * 4 + 3] = var28 + var30;
            var21[floorNumber + var27 * 4 + 1] = x + var31;
            var32 = var23[floorNumber - 4 - var27 * 4];
            var33 = var23[floorNumber - 3 - var27 * 4];
            var21[var27 * 4 + 3] = (var28 - var30) * var32 - (x - var31) * var33;
            var21[var27 * 4 + 1] = (x - var31) * var32 + (var28 - var30) * var33;
         }

         var27 = ByteBufferUtils.iLog(n - 1);

         int var34;
         int var35;
         int var36;
         int var37;
         for(var34 = 0; var34 < var27 - 3; ++var34) {
            var35 = n >> var34 + 2;
            var36 = 8 << var34;

            for(var37 = 0; var37 < 2 << var34; ++var37) {
               int var38 = n - var35 * var37 * 2;
               int var39 = n - var35 * (var37 * 2 + 1);

               for(int var40 = 0; var40 < n >> var34 + 4; ++var40) {
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

         for(var34 = 0; var34 < floorNumber; ++var34) {
            var21[var34] = var21[var34 * 2 + 1];
         }

         for(var34 = 0; var34 < var49; ++var34) {
            var21[n - 1 - var34 * 2] = var21[var34 * 4];
            var21[n - 2 - var34 * 2] = var21[var34 * 4 + 1];
            var21[n - floorIndex - 1 - var34 * 2] = var21[var34 * 4 + 2];
            var21[n - floorIndex - 2 - var34 * 2] = var21[var34 * 4 + 3];
         }

         for(var34 = 0; var34 < var49; ++var34) {
            x = var25[var34 * 2];
            var30 = var25[var34 * 2 + 1];
            var31 = var21[floorNumber + var34 * 2];
            var32 = var21[floorNumber + var34 * 2 + 1];
            var33 = var21[n - 2 - var34 * 2];
            float var51 = var21[n - 1 - var34 * 2];
            float var52 = var30 * (var31 - var33) + x * (var32 + var51);
            var21[floorNumber + var34 * 2] = (var31 + var33 + var52) * 0.5F;
            var21[n - 2 - var34 * 2] = (var31 + var33 - var52) * 0.5F;
            var52 = var30 * (var32 + var51) - x * (var31 - var33);
            var21[floorNumber + var34 * 2 + 1] = (var32 - var51 + var52) * 0.5F;
            var21[n - 1 - var34 * 2] = (-var32 + var51 + var52) * 0.5F;
         }

         for(var34 = 0; var34 < floorIndex; ++var34) {
            var21[var34] = var21[floorNumber + var34 * 2] * var24[var34 * 2] + var21[floorNumber + var34 * 2 + 1] * var24[var34 * 2 + 1];
            var21[floorNumber - 1 - var34] = var21[floorNumber + var34 * 2] * var24[var34 * 2 + 1] - var21[floorNumber + var34 * 2 + 1] * var24[var34 * 2];
         }

         for(var34 = 0; var34 < floorIndex; ++var34) {
            var21[var34 + (n - floorIndex)] = -var21[var34];
         }

         for(var34 = 0; var34 < floorIndex; ++var34) {
            var21[var34] = var21[floorIndex + var34];
         }

         for(var34 = 0; var34 < floorIndex; ++var34) {
            var21[floorIndex + var34] = -var21[floorIndex - var34 - 1];
         }

         for(var34 = 0; var34 < floorIndex; ++var34) {
            var21[floorNumber + var34] = var21[n - var34 - 1];
         }

         for(var34 = leftWindowStart; var34 < leftWindowEnd; ++var34) {
            x = (float)Math.sin(((double)(var34 - leftWindowStart) + 0.5D) / (double)leftN * 0.5D * 3.141592653589793D);
            window[var34] *= (float)Math.sin(1.5707963267948966D * (double)x * (double)x);
         }

         for(var34 = rightWindowStart; var34 < rightWindowEnd; ++var34) {
            x = (float)Math.sin(((double)(var34 - rightWindowStart) + 0.5D) / (double)rightN * 0.5D * 3.141592653589793D + 1.5707963267948966D);
            window[var34] *= (float)Math.sin(1.5707963267948966D * (double)x * (double)x);
         }
      }

      var21 = null;
      if(this.__y > 0) {
         floorIndex = n + this.__y >> 2;
         var21 = new float[floorIndex];
         if(!this.blockFlag) {
            for(var49 = 0; var49 < this.__h; ++var49) {
               var22 = var49 + (this.__y >> 1);
               var21[var49] += this.__t[var22];
            }
         }

         if(!allFloorsEmpty) {
            for(var49 = leftWindowStart; var49 < n >> 1; ++var49) {
               var22 = var21.length - (n >> 1) + var49;
               var21[var22] += window[var49];
            }
         }
      }

      float[] var50 = this.__t;
      this.__t = window;
      window = var50;
      this.__y = n;
      this.__h = rightWindowEnd - (n >> 1);
      this.blockFlag = allFloorsEmpty;

      return var21;
   }

   AudioBuffer toAudioBuffer(int[] var1) {
      if(var1 != null && var1[0] <= 0) {
         return null;
      } else {
         if(this.samples == null) {
            this.__y = 0;
            this.__t = new float[blockSize_1];
            this.samples = new byte[this.sampleCount];
            this.sampleLength = 0;
            this.soundIndices = 0;
         }

         for(; this.soundIndices < this.packets.length; ++this.soundIndices) {
            if(var1 != null && var1[0] <= 0) {
               return null;
            }

            float[] var2 = this.mdctFloatCompute(this.soundIndices);
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

         byte[] data = this.samples;

         return new AudioBuffer(this.sampleRate, data, this.start, this.end, this.loopConsistency);
      }
   }

   static float float32Unpack(int i) {
      int mantissa = i & 2097151;
      int x = i & Integer.MIN_VALUE;
      int e = (i & 2145386496) >> 21;
      if(x != 0) {
         mantissa = -mantissa;
      }

      return (float) ((double) mantissa * Math.pow(2.0D, e - 788));
   }

   public static void setData(byte[] bytes, int index) {
      source = bytes;
      byteIndex = index;
      root = 0;
   }

   public static int getBit() {
      int bit = source[byteIndex] >> root & 1;
      ++root;
      byteIndex += root >> 3;
      root &= 7;
      return bit;
   }

   public static int getBits(int bits) {
      int res = 0;

      int index;
      int bitIndex;
      for(index = 0; bits >= 8 - root; bits -= bitIndex) {
         bitIndex = 8 - root;
         int mask = (1 << bitIndex) - 1;
         res += (source[byteIndex] >> root & mask) << index;
         root = 0;
         ++byteIndex;
         index += bitIndex;
      }

      if(bits > 0) {
         bitIndex = (1 << bits) - 1;
         res += (source[byteIndex] >> root & bitIndex) << index;
         root += bits;
      }

      return res;
   }

   public static void initData(byte[] var0) {
      setData(var0, 0);
      blockSize_0 = 1 << getBits(4);
      blockSize_1 = 1 << getBits(4);
      window = new float[blockSize_1];

      int codebookCount;
      int floorCount;
      int residueCount;
      int mappingCount;
      int timeCount;
      for(codebookCount = 0; codebookCount < 2; ++codebookCount) {
         floorCount = codebookCount != 0 ? blockSize_1 : blockSize_0;
         residueCount = floorCount >> 1;
         mappingCount = floorCount >> 2;
         timeCount = floorCount >> 3;
         float[] trig1 = new float[residueCount];

         for(int var7 = 0; var7 < mappingCount; ++var7) {
            trig1[var7 * 2] = (float)Math.cos((double)(var7 * 4) * 3.141592653589793D / (double)floorCount);
            trig1[var7 * 2 + 1] = -((float)Math.sin((double)(var7 * 4) * 3.141592653589793D / (double)floorCount));
         }

         float[] trig2 = new float[residueCount];

         for(int var8 = 0; var8 < mappingCount; ++var8) {
            trig2[var8 * 2] = (float)Math.cos((double)(var8 * 2 + 1) * 3.141592653589793D / (double)(floorCount * 2));
            trig2[var8 * 2 + 1] = (float)Math.sin((double)(var8 * 2 + 1) * 3.141592653589793D / (double)(floorCount * 2));
         }

         float[] trig3 = new float[mappingCount];

         for(int var9 = 0; var9 < timeCount; ++var9) {
            trig3[var9 * 2] = (float)Math.cos((double)(var9 * 4 + 2) * 3.141592653589793D / (double)floorCount);
            trig3[var9 * 2 + 1] = -((float)Math.sin((double)(var9 * 4 + 2) * 3.141592653589793D / (double)floorCount));
         }

         int[] var15 = new int[timeCount];
         int var10 = ByteBufferUtils.iLog(timeCount - 1);

         for(int var11 = 0; var11 < timeCount; ++var11) {
            var15[var11] = ByteBufferUtils.method87(var11, var10);
         }

         if(codebookCount != 0) {
            __cd_ag = trig1;
            __cd_aq = trig2;
            __cd_aj = trig3;
            __cd_ar = var15;
         } else {
            __cd_r = trig1;
            __cd_p = trig2;
            __cd_v = trig3;
            __cd_av = var15;
         }
      }

      codebookCount = getBits(8) + 1;
      codebooks = new MusicSampleCodebook[codebookCount];

      for(floorCount = 0; floorCount < codebookCount; ++floorCount) {
         codebooks[floorCount] = new MusicSampleCodebook();
      }

      floorCount = getBits(6) + 1;

      for(residueCount = 0; residueCount < floorCount; ++residueCount) {
         getBits(16);
      }

      floorCount = getBits(6) + 1;
      floors = new MusicSampleFloor[floorCount];

      for(residueCount = 0; residueCount < floorCount; ++residueCount) {
         floors[residueCount] = new MusicSampleFloor();
      }

      residueCount = getBits(6) + 1;
      residues = new MusicSampleResidue[residueCount];

      for(mappingCount = 0; mappingCount < residueCount; ++mappingCount) {
         residues[mappingCount] = new MusicSampleResidue();
      }

      mappingCount = getBits(6) + 1;
      mappings = new MusicSampleMapping[mappingCount];

      for(timeCount = 0; timeCount < mappingCount; ++timeCount) {
         mappings[timeCount] = new MusicSampleMapping();
      }

      timeCount = getBits(6) + 1;
      noResidues = new boolean[timeCount];
      modes = new int[timeCount];

      for(int index = 0; index < timeCount; ++index) {
         noResidues[index] = getBit() != 0;
         getBits(16);
         getBits(16);
         modes[index] = getBits(8);
      }
   }

   static boolean firstFileExists(Index musicSampleIndex) {
      if(!initialized) {
         byte[] fileData = musicSampleIndex.getArchive(0).getFile(0).getData();
         if(fileData == null) {
            return false;
         }

         initData(fileData);
         initialized = true;
      }

      return true;
   }

   static MusicSample readMusicSample(Index musicSampleIndex, int archiveID, int fileID) {
      if(!firstFileExists(musicSampleIndex)) {
         if (musicSampleIndex.getArchive(archiveID).getFile(fileID) != null) {
            return null;
         }
      } else {
         byte[] fileData = musicSampleIndex.getArchive(archiveID).getFile(fileID).getData();
         return fileData == null?null:new MusicSample(fileData);
      }
      return null;
   }

   static {
      initialized = false;
   }
}
