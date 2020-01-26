package main.utils;

import java.awt.*;

public final class Canvas extends java.awt.Canvas {

   public static String[] __ao_s;
   static int __ao_gk;
   Component component;

   Canvas(Component var1) {
      this.component = var1;
   }

   public final void update(Graphics var1) {
      this.component.update(var1);
   }
   
   public final void paint(Graphics var1) {
      this.component.paint(var1);
   }

   static synchronized byte[] method862(int var0) {
      return ByteArrayPool.ByteArrayPool_get(var0, false);
   }
}
