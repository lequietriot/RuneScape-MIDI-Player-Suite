package main;

public class MakeSoundFont {

    void createSoundFont(MusicPatch musicPatch, SoundBankCache soundBankCache) {

        /**
        SF2Soundbank sf2Soundbank = new SF2Soundbank();
        sf2Soundbank.setName("Patch RuneScape");
        sf2Soundbank.setRomName("RuneScape MIDI Suite");
        sf2Soundbank.setRomVersionMajor(1);
        sf2Soundbank.setRomVersionMinor(0);

        musicPatch.loadPatchSamples(soundBankCache, null, null);

        int previousRootPitch = 0;

        for (int index = 0; index < 128; index++) {

            if (musicPatch.audioBuffers[index] == null) {
                continue;
            }

            if (musicPatch.notePitches[index] == previousRootPitch) {
                continue;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            AudioFormat audioFormat = new AudioFormat(musicPatch.audioBuffers[index].sampleRate, 16, 1, true, false);
            byte[] eightBitSamples = musicPatch.audioBuffers[index].samples;
            byte[] newSamples = new byte[eightBitSamples.length * 2];

            for (int byteIndex = 0; byteIndex < newSamples.length; byteIndex++) {
                newSamples[byteIndex] = eightBitSamples[byteIndex / 2];
            }
            AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(newSamples), audioFormat, newSamples.length);
            try {
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, byteArrayOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            SF2Sample sf2Sample = new SF2Sample();

            while (musicPatch.notePitches[index] > 128) {
                musicPatch.notePitches[index] -= 128;
            }

            sf2Sample.setName(musicPatch.patchID + "_" + musicPatch.notePitches[index]);
            sf2Sample.setData(byteArrayOutputStream.toByteArray());
            sf2Sample.setSampleRate(musicPatch.audioBuffers[index].sampleRate);
            sf2Sample.setOriginalPitch(musicPatch.notePitches[index]);
            sf2Sample.setStartLoop(musicPatch.audioBuffers[index].start);
            sf2Sample.setEndLoop(musicPatch.audioBuffers[index].end);
            sf2Sample.setSampleType(1); //Mono
            sf2Sample.setSampleLink(-1); //No Link
            sf2Soundbank.addResource(sf2Sample);

            previousRootPitch = musicPatch.notePitches[index];
        }

        SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Patch " + musicPatch.patchID);
        sf2Soundbank.addResource(sf2Layer);

        byte[][] ranges = new byte[sf2Soundbank.getSamples().length][2];

        int position = 0;
        int lastPosition = 0;
        int samplePosition = 0;

        for (int range = 0; range < ranges.length; range++) {

            for (int keys = lastPosition; keys < 128; keys++) {

                if (musicPatch.notePitches[keys] == 0) {
                    position++;
                }

                if (musicPatch.notePitches[keys] == musicPatch.notePitches[keys]) {
                    position++;
                }

                else {
                    break;
                }
            }

            ranges[samplePosition][1] = (byte) lastPosition;
            ranges[samplePosition][0] = (byte) position;

            samplePosition++;
            position++;

            lastPosition = position;
            System.out.println(position);
        }

        for (int samples = 0; samples < sf2Soundbank.getSamples().length; samples++) {

            SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
            sf2LayerRegion.putBytes(SF2Region.GENERATOR_KEYRANGE, ranges[samples]);

            int loopType = -1;
            if (musicPatch.loopMode[samples] == -1) {
                loopType = 1; //Change RuneScape's loop mode (-1) to a SoundFont Loop Mode (1).
            }

            sf2LayerRegion.putInteger(SF2Region.GENERATOR_SAMPLEMODES, loopType);
            sf2LayerRegion.setSample(sf2Soundbank.getSamples()[samples]);
            sf2Layer.getRegions().add(sf2LayerRegion);
        }

        try {

            sf2Soundbank.save("./0.sf2/");

        } catch (IOException e) {
            e.printStackTrace();
        }
         **/
    }
}
