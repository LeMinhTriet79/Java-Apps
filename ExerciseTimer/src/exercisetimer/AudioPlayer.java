/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package exercisetimer;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author Minh Triet
 */
public class AudioPlayer {
    public static void playBeep() {
        try {
            // Giả sử file custom_beep.wav nằm trong thư mục resources ở trong classpath
            URL soundURL = AudioPlayer.class.getResource("/sound/message-1-Nokia.wav");
            if (soundURL == null) {
                System.err.println("Không tìm thấy file âm thanh!");
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }
}
