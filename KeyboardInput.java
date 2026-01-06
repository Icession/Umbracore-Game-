import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardInput implements KeyListener {
    public boolean left, right, jump, escape;

    @Override
    public void keyTyped(KeyEvent e) {
                
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) left = false;
        if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) right = false;
        if(key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) jump = false;
        if(key == KeyEvent.VK_ESCAPE) escape = false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) left = true;
        if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) right = true;
        if(key == KeyEvent.VK_SPACE || key == KeyEvent.VK_UP) jump = true;
        if(key == KeyEvent.VK_ESCAPE) escape = true;
    }
}