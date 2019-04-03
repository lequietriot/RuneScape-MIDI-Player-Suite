package application.utils;

import java.nio.ByteBuffer;

//TODO: Finish the MIDI Decoder (Almost finished)

public class MidiDecoder {
	
	//ShortMessage values
	public final int MESSAGE_NOTEON = 0x00;
	public final int MESSAGE_NOTEOFF = 0x01;
	public final int MESSAGE_CONTROLCHANGE = 0x02;
	public final int MESSAGE_PITCHBEND = 0x03;
	public final int MESSAGE_AFTERTOUCH = 0x04;
	
	//MetaMessage values
	public final int META_SEQUENCENUMBER = 0x00;
	public final int META_TEXT = 0x01;
	public final int META_COPYRIGHT = 0x02;
	public final int META_TRACKNAME = 0x03;
	public final int META_INSTRUMENTNAME = 0x04;
	public final int META_LYRICS = 0x05;
	public final int META_MARKER = 0x06;
	public final int META_CUE = 0x06;
	public final int META_MIDICHANNELPREFIX = 0x20;
	public final int META_ENDOFTRACK = 0x2F;
	public final int META_SETTEMPO = 0x51;
	public final int META_SMTPEOFFSET = 0x54;
	public final int META_TIMESIGNATURE = 0x58;
	public final int META_KEYSIGNATURE = 0x59;
	public final int META_SEQUENCERSPECIFICEVENT = 0x7F;
	
	//Byte array storing decoded data
	public byte[] midiData;
	
	MidiDecoder(ByteBuffer byteBuf) {
		byteBuf.position(byteBuf.array().length - 3);
		int tracks = byteBuf.get() & 0xFF;
		int division = byteBuf.get() & 0xFFFF;
		int length = 14 + tracks * 10;
		byteBuf.position(0);
		int tempoCount = 0;
		int controlChangeCount = 0;
		int noteOnCount = 0;
		int noteOffCount = 0;
		int pitchBendCount = 0;
		int channelPressureCount = 0;
		int keyPressureCount = 0;
		int programChangeCount = 0;
		
		int track;
		int customCode;
		int eventCount;
		
		for (track = 0; track < tracks; track++) {
			customCode = -1;
			
			while (true) {
				
				eventCount = byteBuf.get() & 0xFF;
				
				if (eventCount != customCode) {
					length++;
				}
				
				customCode = eventCount & 0xF;
				
				if (eventCount == 7) {
					break;
				}
				
				if (eventCount == 23) {
					tempoCount++;
				}
				
				else if (customCode == 0) {
					noteOnCount++;
				}
				
				else if (customCode == 1) {
					noteOffCount++;
				}
				
				else if (customCode == 2) {
					controlChangeCount++;
				}
				
				else if (customCode == 3) {
					pitchBendCount++;
				}
				
				else if (customCode == 4) {
					channelPressureCount++;
				}
				
				else if (customCode == 5) {
					keyPressureCount++;
				}
				
				else {
					
					if (customCode != 6) {
						throw new RuntimeException();
					}
					programChangeCount++;
				}
			}
		}
		
		length += tempoCount * 5;
		length += (noteOnCount + noteOffCount + controlChangeCount + pitchBendCount + keyPressureCount) * 2;
		length += (channelPressureCount + programChangeCount);
		track = byteBuf.position();
		eventCount = tracks + tempoCount + controlChangeCount + noteOnCount + noteOffCount + pitchBendCount + channelPressureCount + keyPressureCount + programChangeCount;
		
		for (customCode = 0; customCode < eventCount; customCode++) {
			
			getVariableInteger(byteBuf);
			
			length += byteBuf.position() - track;
			customCode = byteBuf.position();

	        int modulationWheelCountMSB = 0;
	        int modulationWheelCountLSB = 0;
	        int channelVolumeCountMSB = 0;
	        int channelVolumeCountLSB = 0;
	        int channelPanningCountMSB = 0;
	        int channelPanningCountLSB = 0;
	        int NRPNCountMSB = 0;
	        int NRPNCountLSB = 0;
	        int RPNCountMSB = 0;
	        int RPNCountLSB = 0;
	        int miscEventCount = 0;
	        int toggleCount = 0;
	        int controller = 0;
	        
	        int controllerCount;
	        
	        for (controllerCount = 0; controllerCount < controlChangeCount; controllerCount++) {
	        	controller = controller + (byteBuf.get() & 0xFF) & 0x7F;
	        	
	        	if (controller != 0 && controller != 32) {
	        		
	        		if (controller == 1) {
	        			modulationWheelCountMSB++;
	        		}
	        		
	        		else if (controller == 33) {
	        			modulationWheelCountLSB++;
	        		}
	        		
	        		else if (controller == 7) {
	        			channelVolumeCountMSB++;
	        		}
	        		
	        		else if (controller == 39) {
	        			channelVolumeCountLSB++;
	        		}
	        		
	        		else if (controller == 10) {
	        			channelPanningCountMSB++;
	        		}
	        		
	        		else if (controller == 42) {
	        			channelPanningCountLSB++;
	        		}
	        		
	        		else if (controller == 99) {
	        			NRPNCountMSB++;
	        		}
	        		
	        		else if (controller == 98) {
	        			NRPNCountLSB++;
	        		}
	        		
	        		else if (controller == 101) {
	        			RPNCountMSB++;
	        		}
	        		
	        		else if (controller == 100) {
	        			RPNCountLSB++;
	        		}
	        		
	        		else if (controller != 64 && controller != 65 && controller != 120 && controller != 121 && controller != 123) {
	        			toggleCount++;
	        		}
	        		
	        		else {
	        			miscEventCount++;
	        		}
	        	}
	        	
	        	else {
	        		eventCount++;
	        	}
	        }
	        
	        int someOffset = 0;
	        
	        int miscEventOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + miscEventCount);

	        int keyPressureOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + keyPressureCount);

	        int channelPressureOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + channelPressureCount);

	        int pitchBendOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + pitchBendCount);

	        int modWheelMSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + modulationWheelCountMSB);
	        
	        int channelVolumeMSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + channelVolumeCountMSB);
	        
	        int channelPanningMSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + channelPanningCountMSB);
	        
	        int pitchOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + noteOnCount + noteOffCount + keyPressureCount);
	        
	        int noteOnOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + noteOnCount);

	        int toggleOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + toggleCount);

	        int noteOffOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + noteOffCount);

	        int modWheelLSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + modulationWheelCountLSB);

	        int channelVolumeLSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + channelVolumeCountLSB);

	        int channelPanningLSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + channelPanningCountLSB);
	        
	        int programChangeOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + programChangeCount);
	        
	        int pitchBendOffset_2 = byteBuf.position();
	        byteBuf.position(byteBuf.position() + pitchBendCount);
	        
	        int NPRNMSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + NRPNCountMSB);
	        
	        int NPRNLSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + NRPNCountLSB);
	        
	        int RPNMSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + RPNCountMSB);
	        
	        int RPNLSBOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + RPNCountLSB);
	        
	        int tempoOffset = byteBuf.position();
	        byteBuf.position(byteBuf.position() + tempoCount * 3);
	        
	        midiData = new byte[length];
	        ByteBuffer midiBuffer = ByteBuffer.allocate(length + 1);
	        
	        midiBuffer.putInt(1297377380);
	        midiBuffer.putInt(6);
	        midiBuffer.putShort((short)(tracks > 1 ? 1 : 0));
	        midiBuffer.putShort((short) tracks);
	        midiBuffer.putShort((short) division);
	        
	        byteBuf.position(track);
	        
	        int var1 = 0;
	        int var2 = 0;
	        int var3 = 0;
	        int var4 = 0;
	        int var5 = 0;
	        int var6 = 0;
	        int var7 = 0;
	        int[] intArray = new int[128];
	        controller = 0;
	        
	        label: for (int i = 0; i < tracks; i++) {
	        	midiBuffer.putInt(1297379947);
	        	midiBuffer.position(midiBuffer.position() + 4);
	        	int var8 = midiBuffer.position();
	        	int var9 = -1;
	        	
	        	while (true) {
	        		while (true) {
	        			
	        			int var10 = getVariableInteger(byteBuf);
	        			putVariableInteger(midiBuffer, var10);
	        			
	        			int var11 = byteBuf.array()[someOffset++] & 0xFF;
	        			boolean strict = var11 != var9;
	        			var9 = var11 & 0xF;
	        			if (var11 == 7) {
	        				
	        			}
	        		}
	        	}
	        }
		}
	}


	public static int getVariableInteger(ByteBuffer bytebuf) {
		
		byte byteValue = bytebuf.get();

		int intValue;
		
		for (intValue = 0; byteValue < 0; byteValue = bytebuf.get()) {
			
			intValue = (intValue | byteValue & 0x7F) << 7;
			
		}

		return intValue | byteValue;
	}
	
	public static void putVariableInteger(ByteBuffer buffer, int value) {
		
		if ((value & 0xFFFFFF80) != 0) {
			
			if ((value & 0xFFFFC000) != 0) {
				
				if ((value & 0xFFE00000) != 0) {
					
					if ((value & 0xF0000000) != 0) {
						
						buffer.put((byte) (value >>> 28 | 0x80));
					}

					buffer.put((byte) (value >>> 21 | 0x80));
				}

				buffer.put((byte) (value >>> 14 | 0x80));
			}

			buffer.put((byte) (value >>> 7 | 0x80));
		}

		buffer.put((byte) (value & 0x7F));
	}

}
