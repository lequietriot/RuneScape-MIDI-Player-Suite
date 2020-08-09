package main;

import main.utils.Buffer;

/**
 * MIDI File Reader class from Jagex's custom MIDI Player Engine, refactored.
 * @author Rodolfo Ruiz-Velasco (https://github.com/lequietriot)
 */
public class MidiFileReader {

    static final byte[] sequenceArray;

    Buffer buffer;
    int division;
    int[] trackStarts;
    int[] trackPositions;
    int[] trackLengths;
    int[] midiMessages;
    int tempoMPQ;
    long sequencePosition;

    static {
        sequenceArray = new byte[]{2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    /**
     * Initializes the MIDI File Reader and immediately parses it.
     * @param midiData A standard MIDI file (in a byte array) to be read.
     */
    MidiFileReader(byte[] midiData) {
        this.buffer = new Buffer(null);
        this.parse(midiData);
    }

    /**
     * Initializes the MIDI File Reader without parsing anything.
     */
    MidiFileReader() {
        this.buffer = new Buffer(null);
    }

    /**
     * Parses the received MIDI file byte array, loading relevant data into int arrays.
     */
    void parse(byte[] midiData) {
        this.buffer.array = midiData;
        this.buffer.index = 10;
        int eventCount = this.buffer.readUnsignedShort();
        this.division = this.buffer.readUnsignedShort();
        this.tempoMPQ = 500000;
        this.trackStarts = new int[eventCount];

        Buffer midiReadBuffer;
        int trackIndex;
        int bytesToRead;
        for (trackIndex = 0; trackIndex < eventCount; midiReadBuffer.index += bytesToRead) {
            int MTrk_Header = this.buffer.readInt();
            bytesToRead = this.buffer.readInt();
            if (MTrk_Header == 1297379947) {
                this.trackStarts[trackIndex] = this.buffer.index;
                ++trackIndex;
            }

            midiReadBuffer = this.buffer;
        }

        this.sequencePosition = 0L;
        this.trackPositions = new int[eventCount];

        for (trackIndex = 0; trackIndex < eventCount; ++trackIndex) {
            this.trackPositions[trackIndex] = this.trackStarts[trackIndex];
        }

        this.trackLengths = new int[eventCount];
        this.midiMessages = new int[eventCount];
    }

    /**
     * Completely clears everything that has been loaded.
     */
    void clear() {
        this.buffer.array = null;
        this.trackStarts = null;
        this.trackPositions = null;
        this.trackLengths = null;
        this.midiMessages = null;
    }

    /**
     * Is the MIDI sequence ready for playback?
     * @return returns true or false if data is present in the buffer.
     */
    boolean isReady() {
        return this.buffer.array != null;
    }

    /**
     * Obtain the total count of tracks present in the MIDI sequence.
     * @return returns the number of tracks in the sequence.
     */
    int trackCount() {
        return this.trackPositions.length;
    }

    /**
     * Prioritize the selected track.
     * @param track The selected track in the sequence.
     */
    void gotoTrack(int track) {
        this.buffer.index = this.trackPositions[track];
    }

    /**
     * Mark the current position in the selected track.
     * @param track The selected track in the sequence.
     */
    void markTrackPosition(int track) {
        this.trackPositions[track] = this.buffer.index;
    }

    /**
     * Stop reading the sequence, the track is done playing.
     */
    void setTrackDone() {
        this.buffer.index = -1;
    }

    /**
     * Read the length of the specified track.
     * @param track The selected track in the sequence.
     */
    void readTrackLength(int track) {
        int trackEvent = this.buffer.readVarInt();
        int[] trackSize = this.trackLengths;
        trackSize[track] += trackEvent;
    }

    /**
     * Obtain a specific message from a point in the sequence.
     * @param midiMessage The raw MIDI message.
     * @return The read message in the sequence.
     */
    int getMessage(int midiMessage) {
        return this.readMessage(midiMessage);
    }

    /**
     * Decodes the obtained message from a point in the sequence.
     * @param midiMessage The raw MIDI message.
     * @return The value of the read message in the sequence.
     */
    int readMessage(int midiMessage) {
        byte messageBytes = this.buffer.array[this.buffer.index];
        int message;
        if (messageBytes < 0) {
            message = messageBytes & 255;
            this.midiMessages[midiMessage] = message;
            ++this.buffer.index;
        } else {
            message = this.midiMessages[midiMessage];
        }

        if (message != 240 && message != 247) {
            return this.getMessageLength(midiMessage, message);
        } else {
            int value = this.buffer.readVarInt();
            if (message == 247 && value > 0) {
                int shortMessage = this.buffer.array[this.buffer.index] & 255;
                if (shortMessage >= 241 && shortMessage <= 243 || shortMessage == 246 || shortMessage == 248 || shortMessage >= 250 && shortMessage <= 252 || shortMessage == 254) {
                    ++this.buffer.index;
                    this.midiMessages[midiMessage] = shortMessage;
                    return this.getMessageLength(midiMessage, shortMessage);
                }
            }

            Buffer midiBuffer = this.buffer;
            midiBuffer.index += value;
            return 0;
        }
    }

    /**
     * Obtain the specified message's length.
     * @param index The specified index of a track that contains the message.
     * @param midiMessage The message from the sequence.
     * @return The message's length in bytes length.
     */
    int getMessageLength(int index, int midiMessage) {

        int messageValue;

        //Meta Message
        if (midiMessage == 255) {
            int status = this.buffer.readUnsignedByte();
            messageValue = this.buffer.readVarInt();
            Buffer midiBuffer;

            //End of Track
            if (status == 47) {
                midiBuffer = this.buffer;
                midiBuffer.index += messageValue;
                return 1;

            //Set Tempo
            } else if (status == 81) {
                int mpqValue = this.buffer.readMedium();
                messageValue -= 3;
                int trackLength = this.trackLengths[index];
                this.sequencePosition += (long) trackLength * (long) (this.tempoMPQ - mpqValue);
                this.tempoMPQ = mpqValue;
                midiBuffer = this.buffer;
                midiBuffer.index += messageValue;
                return 2;
            } else {
                midiBuffer = this.buffer;
                midiBuffer.index += messageValue;
                return 3;
            }
        } else {
            byte messageBytes = sequenceArray[midiMessage - 128];
            messageValue = midiMessage;
            if (messageBytes >= 1) {
                messageValue = midiMessage | this.buffer.readUnsignedByte() << 8;
            }

            if (messageBytes >= 2) {
                messageValue |= this.buffer.readUnsignedByte() << 16;
            }

            return messageValue;
        }
    }

    /**
     * Obtain the current position in the sequence.
     * @param tick The specified point in the sequence.
     * @return The current position in the sequence.
     */
    long getTrackPosition(int tick) {
        return this.sequencePosition + (long) tick * (long) this.tempoMPQ;
    }

    /**
     * Obtain the current prioritized track in the sequence.
     * @return The current track.
     */
    int getPrioritizedTrack() {
        int trackPositionSize = this.trackPositions.length;
        int currentTrack = -1;
        int maxSize = Integer.MAX_VALUE;

        for (int trackIndex = 0; trackIndex < trackPositionSize; ++trackIndex) {
            if (this.trackPositions[trackIndex] >= 0 && this.trackLengths[trackIndex] < maxSize) {
                currentTrack = trackIndex;
                maxSize = this.trackLengths[trackIndex];
            }
        }

        return currentTrack;
    }

    /**
     * Is the MIDI sequence done playing?
     * @return returns true or false if the sequence is done playing.
     */
    boolean isDone() {
        if (trackPositions != null) {
            for (int trackPosition : this.trackPositions) {
                if (trackPosition >= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Reset the current sequence to a certain position.
     * @param tick The position to reset the sequence to.
     */
    void reset(long tick) {
        this.sequencePosition = tick;
        int trackPosition = this.trackPositions.length;

        for (int trackIndex = 0; trackIndex < trackPosition; ++trackIndex) {
            this.trackLengths[trackIndex] = 0;
            this.midiMessages[trackIndex] = 0;
            this.buffer.index = this.trackStarts[trackIndex];
            this.readTrackLength(trackIndex);
            this.trackPositions[trackIndex] = this.buffer.index;
        }
    }
}
