package main;

public class Resampler {

    int inputRate;
    int outputRate;
    int[][] table;

    public Resampler(int var1, int var2) {
        if(var2 != var1) {
            int var3 = var1;
            int var4 = var2;
            if(var2 > var1) {
                var3 = var2;
                var4 = var1;
            }

            int var5;
            while(var4 != 0) {
                var5 = var3 % var4;
                var3 = var4;
                var4 = var5;
            }

            var1 /= var3;
            var2 /= var3;
            this.inputRate = var1;
            this.outputRate = var2;
            this.table = new int[var1][14];

            for(var5 = 0; var5 < var1; ++var5) {
                int[] var6 = this.table[var5];
                double var7 = (double)var5 / (double)var1 + 6.0D;
                int var9 = (int)Math.floor(var7 - 7.0D + 1.0D);
                if(var9 < 0) {
                    var9 = 0;
                }

                int var10 = (int)Math.ceil(var7 + 7.0D);
                if(var10 > 14) {
                    var10 = 14;
                }

                for(double var11 = (double)var2 / (double)var1; var9 < var10; ++var9) {
                    double var13 = ((double)var9 - var7) * 3.141592653589793D;
                    double var15 = var11;
                    if(var13 < -1.0E-4D || var13 > 1.0E-4D) {
                        var15 = var11 * (Math.sin(var13) / var13);
                    }

                    var15 *= 0.54D + 0.46D * Math.cos(((double)var9 - var7) * 0.2243994752564138D);
                    var6[var9] = (int)Math.floor(0.5D + var15 * 65536.0D);
                }
            }
        }

    }

    byte[] resample(byte[] var1) {
        if(this.table != null) {
            int var2 = (int)((long)var1.length * (long)this.outputRate / (long)this.inputRate) + 14;
            int[] var3 = new int[var2];
            int var4 = 0;
            int var5 = 0;

            int var6;
            for(var6 = 0; var6 < var1.length; ++var6) {
                byte var7 = var1[var6];
                int[] var8 = this.table[var5];

                int var9;
                for(var9 = 0; var9 < 14; ++var9) {
                    var3[var9 + var4] += var8[var9] * var7;
                }

                var5 += this.outputRate;
                var9 = var5 / this.inputRate;
                var4 += var9;
                var5 -= var9 * this.inputRate;
            }

            var1 = new byte[var2];

            for(var6 = 0; var6 < var2; ++var6) {
                int var10 = var3[var6] + 32768 >> 16;
                if(var10 < -128) {
                    var1[var6] = -128;
                } else if(var10 > 127) {
                    var1[var6] = 127;
                } else {
                    var1[var6] = (byte)var10;
                }
            }
        }

        return var1;
    }

    int scaleRate(int var1) {
        if(this.table != null) {
            var1 = (int)((long)this.outputRate * (long)var1 / (long)this.inputRate);
        }

        return var1;
    }

    int scalePosition(int var1) {
        if(this.table != null) {
            var1 = (int)((long)this.outputRate * (long)var1 / (long)this.inputRate) + 6;
        }

        return var1;
    }

}
