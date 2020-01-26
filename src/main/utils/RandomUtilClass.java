package main.utils;

public abstract class RandomUtilClass {

   static int Interpreter_intStackSize;
   static byte[][][] __fe_e;
   public int field2120;
   public int field2121;
   public int field2122;
   public int field2123;

   public static int method3642(int var0, int var1) {
      int var2;
      for(var2 = 1; var1 > 1; var1 >>= 1) {
         if((var1 & 1) != 0) {
            var2 = var0 * var2;
         }

         var0 *= var0;
      }

      if(var1 == 1) {
         return var0 * var2;
      } else {
         return var2;
      }
   }
}
