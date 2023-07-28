package util;

import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioFilePlayer {

    public static int counter = 0;
    public static int octCounter = 0;
    // preloaded sounds
    public static ArrayList<Clip> sounds = new ArrayList<Clip>();

    public static void playCounter() {
        ;
        int oldCounter = counter;
        /* System.out.println("oc");
        System.out.println(oldCounter); */
        counter += 1;
        if (counter > 4) {
            counter = 0;
        }
        if (counter < 0) {
            counter = 4;
        }
        /* System.out.println("c");
        System.out.println(counter); */
        playSound(sounds.get(oldCounter));

    }

    public static void playOctaveCounter() {
        int oldCounter = octCounter;
        octCounter += 1;
        if (octCounter > 4) {
            octCounter = 0;
        }
        if (counter < 0) {
            counter = 4;
        }
        // play sound fully and then stop
        playSound(sounds.get(oldCounter + 5));

    }

    public static void playSound(Clip clip) {
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public static void loopSound(Clip clip){
        clip.stop();
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void init() {
        // Load all sounds
        try {
            for (int i = 1; i < 6; i++) {
                AudioInputStream audioInputStream = AudioSystem
                        .getAudioInputStream(new File("res/sounds/" + i + ".wav").getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                sounds.add(clip);
                System.out.println("Added Sound " + i);
            }
            for (int i = 1; i < 6; i++) {

                AudioInputStream audioInputStream = AudioSystem
                        .getAudioInputStream(new File("res/sounds/oct" + i + ".wav").getAbsoluteFile());
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                sounds.add(clip);
                System.out.println("Added Sound oct" + i);
            }
            AudioInputStream audioInputStream = AudioSystem
                    .getAudioInputStream(new File("res/sounds/titlemusic.wav").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            sounds.add(clip);
            System.out.println("Added Sound title");
        } catch (Exception ex) {
            System.out.println("Error with init sound.");
            ex.printStackTrace();
        }
    }
}