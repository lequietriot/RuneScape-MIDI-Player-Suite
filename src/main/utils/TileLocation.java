package main.utils;

public class TileLocation {

   public int plane;
   public int x;
   public int y;

   public TileLocation(TileLocation var1) {
      this.plane = var1.plane;
      this.x = var1.x;
      this.y = var1.y;
   }

   public TileLocation(int var1, int var2, int var3) {
      this.plane = var1;
      this.x = var2;
      this.y = var3;
   }

   public TileLocation(int var1) {
      if(var1 == -1) {
         this.plane = -1;
      } else {
         this.plane = var1 >> 28 & 3;
         this.x = var1 >> 14 & 16383;
         this.y = var1 & 16383;
      }

   }

   public int packed() {
      return this.plane << 28 | this.x << 14 | this.y;
   }

   boolean equals0(TileLocation var1) {
      return this.plane != var1.plane?false:(this.x != var1.x?false:this.y == var1.y);
   }

   String toString0(String var1) {
      return this.plane + var1 + (this.x >> 6) + var1 + (this.y >> 6) + var1 + (this.x & 63) + var1 + (this.y & 63);
   }

   public boolean __equals_382(Object var1) {
      return this == var1?true:(!(var1 instanceof TileLocation)?false:this.equals0((TileLocation)var1));
   }

   public int __hashCode_383() {
      return this.packed();
   }

   public String __toString_384() {
      return this.toString0(",");
   }
}
