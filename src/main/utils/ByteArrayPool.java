package main.utils;

import org.displee.cache.index.Index;

public class ByteArrayPool {

   static int ByteArrayPool_smallCount;
   static int ByteArrayPool_mediumCount;
   static int ByteArrayPool_largeCount;
   static byte[][] ByteArrayPool_small;
   static byte[][] ByteArrayPool_medium;
   static byte[][] ByteArrayPool_large;
   static int[] __gi_g;
   static Index indexCache13;

   static synchronized byte[] ByteArrayPool_get(int var0, boolean var1) {
      byte[] var2;
      if(var0 != 100) {
         if(var0 < 100) {
            ;
         }
      } else if(ByteArrayPool_smallCount > 0) {
         var2 = ByteArrayPool_small[--ByteArrayPool_smallCount];
         ByteArrayPool_small[ByteArrayPool_smallCount] = null;
         return var2;
      }

      if(var0 != 5000) {
         if(var0 < 5000) {
            ;
         }
      } else if(ByteArrayPool_mediumCount > 0) {
         var2 = ByteArrayPool_medium[--ByteArrayPool_mediumCount];
         ByteArrayPool_medium[ByteArrayPool_mediumCount] = null;
         return var2;
      }

      if(var0 != 30000) {
         if(var0 < 30000) {
            ;
         }
      } else if(ByteArrayPool_largeCount > 0) {
         var2 = ByteArrayPool_large[--ByteArrayPool_largeCount];
         ByteArrayPool_large[ByteArrayPool_largeCount] = null;
         return var2;
      }

      if(RandomUtilClass.__fe_e != null) {
         for(int var3 = 0; var3 < __gi_g.length; ++var3) {
            if(__gi_g[var3] != var0) {
               if(var0 < __gi_g[var3]) {
                  ;
               }
            } else if(WorldMapSection2.__ah_l[var3] > 0) {
               byte[] var4 = RandomUtilClass.__fe_e[var3][--WorldMapSection2.__ah_l[var3]];
               RandomUtilClass.__fe_e[var3][WorldMapSection2.__ah_l[var3]] = null;
               return var4;
            }
         }
      }

      return new byte[var0];
   }

   public static String method4001(int var0, boolean var1) {
      if(var1 && var0 >= 0) {
         int var2 = var0;
         String var3;
         if(var1 && var0 >= 0) {
            int var4 = 2;

            for(int var5 = var0 / 10; var5 != 0; ++var4) {
               var5 /= 10;
            }

            char[] var9 = new char[var4];
            var9[0] = '+';

            for(int var6 = var4 - 1; var6 > 0; --var6) {
               int var7 = var2;
               var2 /= 10;
               int var8 = var7 - var2 * 10;
               if(var8 >= 10) {
                  var9[var6] = (char)(var8 + 87);
               } else {
                  var9[var6] = (char)(var8 + 48);
               }
            }

            var3 = new String(var9);
         } else {
            var3 = Integer.toString(var0, 10);
         }

         return var3;
      } else {
         return Integer.toString(var0);
      }
   }

   static {
      ByteArrayPool_smallCount = 0;
      ByteArrayPool_mediumCount = 0;
      ByteArrayPool_largeCount = 0;
      ByteArrayPool_small = new byte[1000][];
      ByteArrayPool_medium = new byte[250][];
      ByteArrayPool_large = new byte[50][];
   }
}
