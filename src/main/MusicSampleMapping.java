package main;

public class MusicSampleMapping {

   int field1453;
   int field1454;
   int[] field1452;
   int[] field1455;

   MusicSampleMapping() {
      MusicSample.getInt(16);
      this.field1453 = MusicSample.getBit() != 0?MusicSample.getInt(4) + 1:1;
      if(MusicSample.getBit() != 0) {
         MusicSample.getInt(8);
      }

      MusicSample.getInt(2);
      if(this.field1453 > 1) {
         this.field1454 = MusicSample.getInt(4);
      }

      this.field1452 = new int[this.field1453];
      this.field1455 = new int[this.field1453];

      for(int var1 = 0; var1 < this.field1453; ++var1) {
         MusicSample.getInt(8);
         this.field1452[var1] = MusicSample.getInt(8);
         this.field1455[var1] = MusicSample.getInt(8);
      }

   }
}
