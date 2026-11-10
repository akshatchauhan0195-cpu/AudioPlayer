import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioPlayer {

    private static final List<String> SUPPORTED = List.of(".wav", ".aiff", ".aif", ".au");

    private static List<File> songs = new ArrayList<>();
    private static int current = 0;
    private static Clip clip;
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            String path;
            if (args.length > 0) {
                path = args[0];
            } else {
                System.out.print("Enter a music folder, a playlist (.m3u) or a song file: ");
                path = sc.nextLine().trim();
            }

            songs = loadSongs(path);
            if (songs.isEmpty()) {
                System.out.println("No playable songs found (supported: WAV, AIFF, AU).");
                return;
            }

            System.out.println("\nLoaded " + songs.size() + " song(s).");
            printList();
            loadTrack(current);

            String response = "";
            while (!response.equals("Q")) {
                printMenu();
                response = sc.nextLine().trim().toUpperCase();

                if (response.matches("\\d+")) {
                    int n = Integer.parseInt(response);
                    if (n >= 1 && n <= songs.size()) {
                        current = n - 1;
                        loadTrack(current);
                        play();
                    } else {
                        System.out.println("No song with that number.");
                    }
                    continue;
                }

                switch (response) {
                    case "P" -> play();
                    case "S" -> {
                        if (clip != null) clip.stop();
                    }
                    case "R" -> {
                        if (clip != null) clip.setMicrosecondPosition(0);
                    }
                    case "N" -> {
                        current = (current + 1) % songs.size();
                        loadTrack(current);
                        play();
                    }
                    case "B" -> {
                        current = (current - 1 + songs.size()) % songs.size();
                        loadTrack(current);
                        play();
                    }
                    case "L" -> printList();
                    case "Q" -> {
                        if (clip != null) clip.close();
                    }
                    default -> System.out.println("Not a valid response");
                }
            }
        } finally {
            System.out.println("Program has ended");
        }
    }

    // Accepts a folder, an .m3u playlist file, or a single audio file.
    private static List<File> loadSongs(String rawPath) {
        List<File> found = new ArrayList<>();
        String path = rawPath.replaceFirst("^~", System.getProperty("user.home"));
        File f = new File(path);

        if (!f.exists()) {
            System.out.println("Path not found: " + path);
            return found;
        }

        if (f.isDirectory()) {
            File[] files = f.listFiles(File::isFile);
            if (files != null) {
                Arrays.sort(files, Comparator.comparing(x -> x.getName().toLowerCase()));
                for (File x : files) {
                    if (isSupported(x)) found.add(x);
                }
            }
        } else if (f.getName().toLowerCase().endsWith(".m3u")) {
            try {
                for (String line : Files.readAllLines(f.toPath())) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    File song = new File(line);
                    if (!song.isAbsolute()) song = new File(f.getParentFile(), line);
                    if (song.isFile() && isSupported(song)) found.add(song);
                }
            } catch (IOException e) {
                System.out.println("Could not read playlist file");
            }
        } else if (isSupported(f)) {
            found.add(f);
        }
        return found;
    }

    private static boolean isSupported(File f) {
        String name = f.getName().toLowerCase();
        return SUPPORTED.stream().anyMatch(name::endsWith);
    }

    private static void loadTrack(int index) {
        if (clip != null) clip.close();
        File file = songs.get(index);
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)) {
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException e) {
            System.out.println("File is not a supported audio format: " + file.getName());
            clip = null;
        } catch (LineUnavailableException e) {
            System.out.println("Audio line is unavailable");
            clip = null;
        } catch (IOException e) {
            System.out.println("Could not read file: " + file.getName());
            clip = null;
        }
    }

    private static void play() {
        if (clip == null) {
            System.out.println("Nothing loaded to play.");
            return;
        }
        clip.start();
        System.out.println("Now playing: " + songs.get(current).getName());
    }

    private static void printList() {
        System.out.println("\n--- Playlist ---");
        for (int i = 0; i < songs.size(); i++) {
            String marker = (i == current) ? " <" : "";
            System.out.println((i + 1) + ". " + songs.get(i).getName() + marker);
        }
    }

    private static void printMenu() {
        System.out.println("\nP = Play   S = Stop   R = Restart song   N = Next   B = Back");
        System.out.println("L = List songs   <number> = Play that song   Q = Quit");
        System.out.print("Enter your choice: ");
    }
}