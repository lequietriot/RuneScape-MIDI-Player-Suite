package main.utils;

public class WorldMapSection2 implements WorldMapSection {

   static int[] __ah_l;
   public static String[] cacheDirectoryLocations;
   int __m;
   int __f;
   int __q;
   int __w;
   int __o;
   int __u;

   public void expandBounds(WorldMapArea var1) {
      if(var1.minX0 > this.__o) {
         var1.minX0 = this.__o;
      }

      if(var1.maxX0 < this.__o) {
         var1.maxX0 = this.__o;
      }

      if(var1.minY0 > this.__u) {
         var1.minY0 = this.__u;
      }

      if(var1.maxY0 < this.__u) {
         var1.maxY0 = this.__u;
      }

   }

   public boolean containsCoord(int var1, int var2, int var3) {
      return var1 >= this.__m && var1 < this.__m + this.__f?var2 >> 6 == this.__q && var3 >> 6 == this.__w:false;
   }

   public boolean containsPosition(int var1, int var2) {
      return var1 >> 6 == this.__o && var2 >> 6 == this.__u;
   }

   public int[] position(int var1, int var2, int var3) {
      if(!this.containsCoord(var1, var2, var3)) {
         return null;
      } else {
         int[] var4 = new int[]{this.__o * 64 - this.__q * 64 + var2, var3 + (this.__u * 64 - this.__w * 64)};
         return var4;
      }
   }

   public TileLocation coord(int var1, int var2) {
      if(!this.containsPosition(var1, var2)) {
         return null;
      } else {
         int var3 = this.__q * 64 - this.__o * 64 + var1;
         int var4 = this.__w * 64 - this.__u * 64 + var2;
         return new TileLocation(this.__m, var3, var4);
      }
   }

   public void read(Buffer var1) {
      this.__m = var1.readUnsignedByte();
      this.__f = var1.readUnsignedByte();
      this.__q = var1.__ag_302();
      this.__w = var1.__ag_302();
      this.__o = var1.__ag_302();
      this.__u = var1.__ag_302();
      this.__g_65();
   }

   void __g_65() {
   }

}
