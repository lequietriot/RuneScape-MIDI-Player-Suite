package application.utils;

public class AudioResampler {

    float systemSampleRate;
    float sampleSampleRate;
    int[][] resampleTable;
    
    public AudioResampler (float sampleRate, float defaultSampleRate) {
        if (defaultSampleRate != sampleRate) {
            int newSampleRate = computeNewSampleRate((int) sampleRate, (int) defaultSampleRate);
            sampleRate /= newSampleRate;
            defaultSampleRate /= newSampleRate;
            systemSampleRate = sampleRate;
            sampleSampleRate = defaultSampleRate;
            resampleTable = new int[(int) sampleRate][14];

            for (int index = 0; index < sampleRate; index++) {
                int[] ints_5 = resampleTable[index];
                double scaleFactor = (double) index / (double) sampleRate + 6.0D;
                int i_8 = (int) Math.floor(scaleFactor - 7.0D + 1.0D);
                if (i_8 < 0)
                    i_8 = 0;

                int i_9 = (int) Math.ceil(7.0D + scaleFactor);
                if (i_9 > 14)
                    i_9 = 14;

                for (double d_10 = (double) defaultSampleRate / (double) sampleRate; i_8 < i_9; i_8++) {
                    double d_12 = (i_8 - scaleFactor) * 3.141592653589793D;
                    double d_14 = d_10;
                    if (d_12 < -1.0E-4D || d_12 > 1.0E-4D)
                        d_14 = d_10 * (Math.sin(d_12) / d_12);

                    d_14 *= 0.54D + 0.46D * Math.cos((i_8 - scaleFactor) * 0.2243994752564138D);
                    ints_5[i_8] = (int) Math.floor(0.5D + 65536.0D * d_14);
                }
            }
        }
    }

    public static int computeNewSampleRate(int defaultSampleRate, int sampleRate) {
        int newSampleRate;
        if (sampleRate > defaultSampleRate) {
            newSampleRate = defaultSampleRate;
            defaultSampleRate = sampleRate;
            sampleRate = newSampleRate;
        }

        while (sampleRate != 0) {
            newSampleRate = defaultSampleRate % sampleRate;
            defaultSampleRate = sampleRate;
            sampleRate = newSampleRate;
        }

        return defaultSampleRate;
    }

	byte[] method179(byte[] bytes_1) {
        if (resampleTable != null) {
            int i_3 = 7 + (int) ((long) sampleSampleRate * (long) bytes_1.length / systemSampleRate);
            int[] ints_4 = new int[i_3];
            int i_5 = 0;
            int i_6 = 0;

            int i_7;
            
            for (i_7 = 0; i_7 < bytes_1.length; i_7++) {
                byte b_8 = bytes_1[i_7];
                int[] ints_9 = resampleTable[i_6];

                int i_10;
                for (i_10 = 0; i_10 < 7; i_10++) {
                    ints_4[i_10 + i_5] += b_8 * ints_9[i_10];
                }

                i_6 += sampleSampleRate;
                i_10 = (int) (i_6 / systemSampleRate);
                i_5 += i_10;
                i_6 -= systemSampleRate * i_10;
            }
            
        }
        return bytes_1;
    }

    int method180(int i_1) {
        if (resampleTable != null)
            i_1 = (int) ((long) i_1 * (long) sampleSampleRate / systemSampleRate);

        return i_1;
    }

    int method181(int i_1) {
    	
        if (resampleTable != null) {
            i_1 = 6 + (int) ((long) sampleSampleRate * (long) i_1 / systemSampleRate);
        }
        return i_1;
    }
    
    public byte[] resample(byte[] sourceData, int bitsPerSample) { 
    	  
    	  int bytePerSample = bitsPerSample / 8; 
    	  int numSamples = sourceData.length / bytePerSample; 
    	  short[] amplitudes = new short[numSamples];
    	   
    	  int pointer = 0; 
    	  for (int i = 0; i < numSamples; i++) { 
    	   short amplitude = 0; 
    	   for (int byteNumber = 0; byteNumber < bytePerSample; byteNumber++) { 
    	    amplitude |= (short) ((sourceData[pointer++] & 0xFF) << (byteNumber * 8)); 
    	   } 
    	   amplitudes[i] = amplitude; 
    	  }
    	  short[] targetSample = interpolate(amplitudes); 
    	  int targetLength = targetSample.length;
    	  
    	  byte[] bytes; 
    	  if (bytePerSample==1){ 
    	   bytes= new byte[targetLength]; 
    	   for (int i=0; i<targetLength; i++){ 
    	    bytes[i]=(byte)targetSample[i]; 
    	   } 
    	  } 
    	  else{ 
    	   bytes= new byte[targetLength*2]; 
    	   for (int i=0; i<targetSample.length; i++){   
    	    bytes[i*2] = (byte)(targetSample[i] & 0xff); 
    	    bytes[i*2+1] = (byte)((targetSample[i] >> 8) & 0xff);    
    	   } 
    	  } 
    	   
    	  return bytes; 
    	 }
    
	public short[] interpolate(short[] samples) { 
		 
		  if (sampleSampleRate == systemSampleRate) { 
			  
		   return samples;
		   
		  } 
		   
		  int newLength=(int)Math.round(((float) samples.length / systemSampleRate * sampleSampleRate)); 
		  float lengthMultiplier=(float)newLength/samples.length; 
		        short[] interpolatedSamples = new short[newLength]; 
		         
		        for (int i = 0; i < newLength; i++){ 
		             
		         float currentPosition = i / lengthMultiplier; 
		            int nearestLeftPosition = (int)currentPosition; 
		            int nearestRightPosition = nearestLeftPosition + 1; 
		            if (nearestRightPosition>=samples.length){ 
		             nearestRightPosition=samples.length-1; 
		            } 
		             
		            float slope = samples[nearestRightPosition] - samples[nearestLeftPosition];
		            float positionFromLeft = currentPosition - nearestLeftPosition; 
		             
		            interpolatedSamples[i] = (short)(slope * positionFromLeft + samples[nearestLeftPosition]);
		        }
		        return interpolatedSamples; 
		 }
	}
