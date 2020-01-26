package main.utils;

import java.util.Iterator;
import java.util.LinkedList;

public class WorldMapArea {

   static int port2;
   int id0;
   String archiveName0;
   String name0;
   int __w;
   int zoom0;
   TileLocation origin0;
   int minX0;
   int maxX0;
   int minY0;
   int maxY0;
   boolean isMain0;
   LinkedList sections;

   public WorldMapArea() {
      this.id0 = -1;
      this.__w = -1;
      this.zoom0 = -1;
      this.origin0 = null;
      this.minX0 = Integer.MAX_VALUE;
      this.maxX0 = 0;
      this.minY0 = Integer.MAX_VALUE;
      this.maxY0 = 0;
      this.isMain0 = false;
   }

   public boolean containsCoord(int var1, int var2, int var3) {
      Iterator var4 = this.sections.iterator();

      while(var4.hasNext()) {
         WorldMapSection var5 = (WorldMapSection)var4.next();
         if(var5.containsCoord(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   public boolean containsPosition(int var1, int var2) {
      int var3 = var1 / 64;
      int var4 = var2 / 64;
      if(var3 >= this.minX0 && var3 <= this.maxX0) {
         if(var4 >= this.minY0 && var4 <= this.maxY0) {
            Iterator var5 = this.sections.iterator();

            while(var5.hasNext()) {
               WorldMapSection var6 = (WorldMapSection)var5.next();
               if(var6.containsPosition(var1, var2)) {
                  return true;
               }
            }

            return false;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public int[] position(int var1, int var2, int var3) {
      Iterator var4 = this.sections.iterator();

      while(var4.hasNext()) {
         WorldMapSection var5 = (WorldMapSection)var4.next();
         if(var5.containsCoord(var1, var2, var3)) {
            return var5.position(var1, var2, var3);
         }
      }

      return null;
   }

   public TileLocation coord(int var1, int var2) {
      Iterator var3 = this.sections.iterator();

      while(var3.hasNext()) {
         WorldMapSection var4 = (WorldMapSection)var3.next();
         if(var4.containsPosition(var1, var2)) {
            return var4.coord(var1, var2);
         }
      }

      return null;
   }

   void setBounds() {
      Iterator var1 = this.sections.iterator();

      while(var1.hasNext()) {
         WorldMapSection var2 = (WorldMapSection)var1.next();
         var2.expandBounds(this);
      }

   }

   public int id() {
      return this.id0;
   }

   public boolean isMain() {
      return this.isMain0;
   }

   public String archiveName() {
      return this.archiveName0;
   }

   public String name() {
      return this.name0;
   }

   int __a_39() {
      return this.__w;
   }

   public int zoom() {
      return this.zoom0;
   }

   public int minX() {
      return this.minX0;
   }

   public int maxX() {
      return this.maxX0;
   }

   public int minY() {
      return this.minY0;
   }

   public int maxY() {
      return this.maxY0;
   }

   public int originX() {
      return this.origin0.x;
   }

   public int originPlane() {
      return this.origin0.plane;
   }

   public int originY() {
      return this.origin0.y;
   }

   public TileLocation origin() {
      return new TileLocation(this.origin0);
   }

   static int method427(int var0, int var1) {
      if(var0 == -2) {
         return 12345678;
      } else if(var0 == -1) {
         if(var1 < 0) {
            var1 = 0;
         } else if(var1 > 127) {
            var1 = 127;
         }

         var1 = 127 - var1;
         return var1;
      } else {
         var1 = (var0 & 127) * var1 / 128;
         if(var1 < 2) {
            var1 = 2;
         } else if(var1 > 126) {
            var1 = 126;
         }

         return (var0 & 65408) + var1;
      }
   }
}
