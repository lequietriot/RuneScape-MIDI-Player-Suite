package main;

import java.nio.ByteBuffer;

class ToneFilter {
    private static final float[][] minimisedCoefficients;
    static final int[][] coefficients;
    private static float forwardMinimisedCoefficientMultiplier;
    static int forwardMultiplier;
    int[] pairs;
    private int[][][] phases;
    private int[][][] magnitudes;
    private int[] unity;

    static {
        minimisedCoefficients = new float[2][8];
        coefficients = new int[2][8];
    }

    ToneFilter() {
        this.pairs = new int[2];
        this.phases = new int[2][2][4];
        this.magnitudes = new int[2][2][4];
        this.unity = new int[2];
    }

    private float interpolateMagnitude(final int var1, final int var2, final float var3) {
        float var4 = this.magnitudes[var1][0][var2] + var3 * (this.magnitudes[var1][1][var2] - this.magnitudes[var1][0][var2]);
        var4 *= 0.0015258789F;
        return 1.0F - (float)Math.pow(10.0D, (-var4 / 20.0F));
    }

    private float interpolatePhase(final int var1, final int var2, final float var3) {
        float var4 = this.phases[var1][0][var2] + var3 * (this.phases[var1][1][var2] - this.phases[var1][0][var2]);
        var4 *= 1.2207031E-4F;
        return normalise(var4);
    }

    int compute(final int var1, final float var2) {
        float var3;
        if(var1 == 0) {
            var3 = this.unity[0] + (this.unity[1] - this.unity[0]) * var2;
            var3 *= 0.0030517578F;
            forwardMinimisedCoefficientMultiplier = (float)Math.pow(0.1D, (var3 / 20.0F));
            forwardMultiplier = (int)(forwardMinimisedCoefficientMultiplier * 65536.0F);
        }

        if(this.pairs[var1] == 0) {
            return 0;
        } else {
            var3 = this.interpolateMagnitude(var1, 0, var2);
            minimisedCoefficients[var1][0] = -2.0F * var3 * (float)Math.cos(this.interpolatePhase(var1, 0, var2));
            minimisedCoefficients[var1][1] = var3 * var3;

            int var4;
            for(var4 = 1; var4 < this.pairs[var1]; ++var4) {
                var3 = this.interpolateMagnitude(var1, var4, var2);
                final float var5 = -2.0F * var3 * (float)Math.cos(this.interpolatePhase(var1, var4, var2));
                final float var6 = var3 * var3;
                minimisedCoefficients[var1][var4 * 2 + 1] = minimisedCoefficients[var1][var4 * 2 - 1] * var6;
                minimisedCoefficients[var1][var4 * 2] = minimisedCoefficients[var1][var4 * 2 - 1] * var5 + minimisedCoefficients[var1][var4 * 2 - 2] * var6;

                for(int var7 = var4 * 2 - 1; var7 >= 2; --var7) {
                    minimisedCoefficients[var1][var7] += minimisedCoefficients[var1][var7 - 1] * var5 + minimisedCoefficients[var1][var7 - 2] * var6;
                }

                minimisedCoefficients[var1][1] += minimisedCoefficients[var1][0] * var5 + var6;
                minimisedCoefficients[var1][0] += var5;
            }

            if(var1 == 0) {
                for(var4 = 0; var4 < this.pairs[0] * 2; ++var4) {
                    minimisedCoefficients[0][var4] *= forwardMinimisedCoefficientMultiplier;
                }
            }

            for(var4 = 0; var4 < this.pairs[var1] * 2; ++var4) {
                coefficients[var1][var4] = (int)(minimisedCoefficients[var1][var4] * 65536.0F);
            }

            return this.pairs[var1] * 2;
        }
    }

    final void decode(final ByteBuffer var1, final ToneEnvelope var2) {
        final int var3 = var1.get() & 0xFF;
        this.pairs[0] = var3 >> 4;
        this.pairs[1] = var3 & 15;
        if(var3 != 0) {
            this.unity[0] = var1.getShort() & 0xFFFF;
            this.unity[1] = var1.getShort() & 0xFFFF;
            final int var4 = var1.get() & 0xFF;

            int var5;
            int var6;
            for(var5 = 0; var5 < 2; ++var5) {
                for(var6 = 0; var6 < this.pairs[var5]; ++var6) {
                    this.phases[var5][0][var6] = var1.getShort() & 0xFFFF;
                    this.magnitudes[var5][0][var6] = var1.getShort() & 0xFFFF;
                }
            }

            for(var5 = 0; var5 < 2; ++var5) {
                for(var6 = 0; var6 < this.pairs[var5]; ++var6) {
                    if((var4 & 1 << var5 * 4 << var6) != 0) {
                        this.phases[var5][1][var6] = var1.getShort() & 0xFFFF;
                        this.magnitudes[var5][1][var6] = var1.getShort() & 0xFFFF;
                    } else {
                        this.phases[var5][1][var6] = this.phases[var5][0][var6];
                        this.magnitudes[var5][1][var6] = this.magnitudes[var5][0][var6];
                    }
                }
            }

            if(var4 != 0 || this.unity[1] != this.unity[0]) {
                var2.decodeSegments(var1);
            }
        } else {
            final int[] var7 = this.unity;
            this.unity[1] = 0;
            var7[0] = 0;
        }

    }

    private static float normalise(final float var0) {
        final float var1 = 32.703197F * (float)Math.pow(2.0D, var0);
        return var1 * 3.1415927F / 11025.0F;
    }
}
