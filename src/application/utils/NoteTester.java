package application.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class NoteTester {
	
	static SourceDataLine sdl;
	static AudioFormat format;
	static ByteBuffer bytebuf;
	static AudioResampler resampler;
	
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		
		AudioInputStream ais0 = AudioSystem.getAudioInputStream(new File("./Samples/1.wav/"));
		byte[] sample0 = new byte[ais0.available()];
		ais0.read(sample0);
		
		AudioFormat format0 = ais0.getFormat();
		AudioNote note0 = new AudioNote(sample0, format0.getSampleRate(), 60, 0, sample0.length, format0.getChannels(), format0.getSampleSizeInBits());

		resampler = new AudioResampler(44100, note0.ModifyAudioNoteTo(48));
		
		format = new AudioFormat(44100, note0.bitSize, note0.channels, true, false);
		sdl = AudioSystem.getSourceDataLine(format);
		sdl.open();
		sdl.start();
		
		FileOutputStream fos = new FileOutputStream(new File("./0.wav/"));
		fos.write(note0.resampleAudio(resampler));
		fos.close();
		
		sdl.write(note0.resampleAudio(resampler), 0, (int) note0.resampleAudio(resampler).length);
	}

	
	//int newLength = Math.min(sampleByte1.length, sampleByte2.length);
	
	//byte[] mix = new byte[newLength];
	
	//for (int index = 0; index < newLength; index++) {
		//byte b1 = sampleByte1[index];
		//byte b2 = sampleByte2[index];
		
		//mix[index] = (byte) (b1 + b2 / 2);
}
