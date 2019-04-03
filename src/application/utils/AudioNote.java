package application.utils;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioNote {

	byte[] sampleData;
	float sampleRate;
	float loopStart;
	float loopEnd;
	int samplePitch;
	int bitSize;
	int channels;
	int result;
	AudioFormat newFormat;
	
	public AudioNote(byte[] bytes, float sampledRate, int pitch, float loopBegin, float loopFinish, int audioChannels, int bits) {
		sampleData = bytes;
		sampleRate = sampledRate;
		samplePitch = pitch;
		loopStart = loopBegin;
		loopEnd = loopFinish;
		channels = audioChannels;
		bitSize = bits;
	}
	
	public byte[] resampleAudio(AudioResampler resampler) {
		
		byte[] newSampleData = resampler.method179(sampleData);
		
		return newSampleData;
	}
	
	public int ModifyAudioNoteTo(int desiredNote) throws UnsupportedAudioFileException, IOException {
		
		double semitoneMultiplier = 1.0595;
		
		for (int i = 0; i < 128; i++) {
			
			if (samplePitch < desiredNote) {
				result = (int) (sampleRate /= semitoneMultiplier);
				samplePitch++;
			}
			
			if (samplePitch > desiredNote) {
				result = (int) (sampleRate *= semitoneMultiplier);
				samplePitch--;
			}
			
			else if (samplePitch == desiredNote) {
				break;
			}
		}
		newFormat = new AudioFormat(sampleRate, bitSize, channels, true, false);
        System.out.println(newFormat);
        
		return result;
	}

}
