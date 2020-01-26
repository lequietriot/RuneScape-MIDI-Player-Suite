package main.utils;

public class VertexNormal {

   static boolean __dq_pe;
   static int __dq_rq;
   int __m;
   int __f;
   int __q;
   int __w;

   VertexNormal() {
   }

   VertexNormal(VertexNormal var1) {
      this.__m = var1.__m;
      this.__f = var1.__f;
      this.__q = var1.__q;
      this.__w = var1.__w;
   }

   static synchronized void method2984(byte[] var0) {
      if(var0.length == 100 && ByteArrayPool.ByteArrayPool_smallCount < 1000) {
         ByteArrayPool.ByteArrayPool_small[++ByteArrayPool.ByteArrayPool_smallCount - 1] = var0;
      } else if(var0.length == 5000 && ByteArrayPool.ByteArrayPool_mediumCount < 250) {
         ByteArrayPool.ByteArrayPool_medium[++ByteArrayPool.ByteArrayPool_mediumCount - 1] = var0;
      } else if(var0.length == 30000 && ByteArrayPool.ByteArrayPool_largeCount < 50) {
         ByteArrayPool.ByteArrayPool_large[++ByteArrayPool.ByteArrayPool_largeCount - 1] = var0;
      } else if(RandomUtilClass.__fe_e != null) {
         for(int var1 = 0; var1 < ByteArrayPool.__gi_g.length; ++var1) {
            if(var0.length == ByteArrayPool.__gi_g[var1] && WorldMapSection2.__ah_l[var1] < RandomUtilClass.__fe_e[var1].length) {
               RandomUtilClass.__fe_e[var1][WorldMapSection2.__ah_l[var1]++] = var0;
               return;
            }
         }
      }

   }
}
