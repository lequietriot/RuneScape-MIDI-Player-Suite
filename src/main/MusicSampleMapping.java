package main;

public class MusicSampleMapping {

   int submaps;
   int mux;
   int[] submapFloors;
   int[] submapResidues;

   MusicSampleMapping() {
      MusicSample.getBits(16);
      this.submaps = MusicSample.getBit() != 0?MusicSample.getBits(4) + 1:1;
      if(MusicSample.getBit() != 0) {
         MusicSample.getBits(8);
      }

      MusicSample.getBits(2);
      if(this.submaps > 1) {
         this.mux = MusicSample.getBits(4);
      }

      this.submapFloors = new int[this.submaps];
      this.submapResidues = new int[this.submaps];

      for(int index = 0; index < this.submaps; ++index) {
         MusicSample.getBits(8);
         this.submapFloors[index] = MusicSample.getBits(8);
         this.submapResidues[index] = MusicSample.getBits(8);
      }

   }
}
