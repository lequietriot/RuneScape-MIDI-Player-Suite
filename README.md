# RuneScape MIDI Player
With this tool, you can hear any MIDI file in the RuneScape SoundBanks! You can also choose to use any custom SoundFonts.

Setup instructions (Custom SoundFont):
1. In the File tab, you want to load a MIDI file. It can be any MIDI.
2. Next, you want to go to the Preferences tab and set a default SoundFont. It can be any SoundFont as well.

Since you set the preference for your SoundFont, it should remember what you wanted when you re-open the application!

Setup instructions (Official SoundBank):
1. In the File tab, you want to load a RuneScape cache. These are typically found online, or if you play Old School RuneScape, it can be found in your user home folder under the path "jagexcache/oldschool/LIVE".
2. Now, you can simply go to the File tab again and load a MIDI file.
3. Lastly, you can go to the Tools tab, and Test the MIDI with your selected SoundBank, using the cache.

# Credits/Thanks

- Rodolfo (https://github.com/lequietriot): creation of the MIDI Player.
- Displee (https://github.com/Displee): for the amazing cache library!
- Vincent (from Rune-Server): for figuring out the MIDI Encoder.
- The RuneScape Team: for being an inspiration in making this happen.

# Features
- Can dump any MIDI from the RuneScape cache if loaded from the File tab.
- Can also encode any custom MIDI into a file using the RuneScape format.
- Loop Mode button to easily loop your songs. If the MIDI has metadata messages named "loopStart" & "loopEnd", it will loop from those positions.
- The program now supports pausing, and stopping instead of just playing. 
- Rendering a MIDI file to Waveform Audio was carried on from the previous versions into this new revamp.
- A refined custom HQ SoundFont based on the Old School RuneScape soundbanks is included.
- The program can actually remember what soundfont you chose by writing the path to a text file and reading it on startup.
- A time slider along with some hints and tooltips! You can now see where your position is in the song playback.
- The ability to utilize the tools menu and save the loaded MIDI as an attempt to fix it.
- A feature that comes in the form of check-boxes to enable fixing MIDI files when rendering and/or playing the song. **NOTE: This feature is intended to work on weird-sounding RuneScape MIDI files, when used on regular sounding ones they may sound weird.**
- This program supports fixing and resaving Old School RuneScape MIDI files, handling and converting custom instruments to basic ones. The notes are corrected to the normal sound, and all tracks are labelled according to instrument used.
- This program supports fixing and resaving RuneScape HD MIDI files, handling and converting custom instruments to basic ones. The notes are corrected to the normal sound, and all tracks are labelled according to instrument used.
- Can dump all raw soundbank-related data.
- Can pack all raw soundbank-related data.
Yes, go ahead and experiment with the soundbank dumping and packing, you can mix old with new or vice versa!

# To do list (Ideas)
- Sound Encoding tools (Sound Effects, Sound Bank...)
- Automatic soundfont creating from cache (Being worked on)
