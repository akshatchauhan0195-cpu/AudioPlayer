# Java Audio Player

A simple terminal music player written in plain Java. Point it at a folder of songs, a playlist file, or a single audio file, and control playback with the keyboard.

## Features

- Play a whole folder of songs as a playlist
- Load an `.m3u` playlist file, or a single song
- Play, stop, restart, next, back, and jump to any song by number
- List all songs and see which one is current
- `~` works as a shortcut for your home folder
- No external libraries needed

## Supported formats

Java's built-in sound API plays these formats:

- WAV (`.wav`)
- AIFF (`.aiff`, `.aif`)
- AU (`.au`)

MP3 files are **not** supported by the built-in API (see [Converting MP3 files](#converting-mp3-files)).

## Requirements

- JDK 17 or newer (developed with Java 21)

## Run

```bash
javac AudioPlayer.java
java AudioPlayer /path/to/your/music/folder
```

You can also pass an `.m3u` playlist or a single file:

```bash
java AudioPlayer ~/Music/playlist.m3u
java AudioPlayer ~/Music/song.wav
```

If you leave out the path, the program asks you for one.

## Controls

| Key        | Action                          |
|------------|---------------------------------|
| `P`        | Play the current song           |
| `S`        | Stop (pause) the song           |
| `R`        | Restart the song from the start |
| `N`        | Next song                       |
| `B`        | Previous song                   |
| `L`        | List all songs                  |
| `<number>` | Play that song from the list    |
| `Q`        | Quit                            |

Type the key and press **Enter**.

## Converting MP3 files

On a Mac, the built-in `afconvert` tool converts MP3 to WAV with nothing to install:

```bash
afconvert -f WAVE -d LEI16 song.mp3 song.wav
```

To convert every MP3 in a folder:

```bash
for f in *.mp3; do afconvert -f WAVE -d LEI16 "$f" "${f%.mp3}.wav"; done
```

On Windows or Linux you can use [ffmpeg](https://ffmpeg.org/): `ffmpeg -i song.mp3 song.wav`.

WAV files are large, roughly 10 MB per minute of audio.

## Playlist files (.m3u)

An `.m3u` file is a plain text file with one song path per line. Lines starting with `#` are ignored, and relative paths are resolved from the playlist's folder.

```
# My playlist
/Users/you/Music/song1.wav
song2.wav
```

## Using Spotify songs

Songs downloaded from Spotify are encrypted and only play inside the Spotify app, so this player can't open them. Use audio files you own or have the rights to play.

## Troubleshooting

| Message                                  | Cause and fix                                                           |
|------------------------------------------|-------------------------------------------------------------------------|
| `No playable songs found`                | The path has no WAV, AIFF or AU files. Convert MP3s first.              |
| `Path not found`                         | Check the path for typos. Drag the folder into the terminal to paste it.|
| `File is not a supported audio format`   | The file can't be read by Java. Re-convert it to WAV.                   |
| `Audio line is unavailable`              | Another app is using the audio device. Close it and try again.          |

## Project structure

```
.
├── AudioPlayer.java
└── README.md
```

## Ideas for improvement

- Play the next song automatically when one ends
- Add volume control
- Shuffle and repeat modes
- Support MP3 using a library such as JLayer
- Show song length and a progress display
