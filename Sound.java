import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.*;

public class Sound { 
    private Clip clip;
    private FloatControl volumeControl;

    public Sound(String filePath) {
        try {
            URL url = getClass().getResource(filePath);
            
            if (url == null) {
                System.err.println("Error: Sound file not found at path: " + filePath);
                return;
            }
            
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            
          
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

          
            audioInputStream.close(); 
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
   
    private void resetClip() {
        if (clip != null) {
            clip.stop(); 
            clip.setFramePosition(0); 
        }
    }

    public void play() {
        if (clip != null) {
            resetClip(); 
            clip.start();
        }
    }

    public void loop() {
        if (clip != null) {
            resetClip(); 
            clip.loop(Clip.LOOP_CONTINUOUSLY); 
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    public void setVolume(float volume) {
        if (volumeControl != null) {
            float clampedVolume = Math.max(volumeControl.getMinimum(), Math.min(volumeControl.getMaximum(), volume));
            volumeControl.setValue(clampedVolume);
        }
    }
}

