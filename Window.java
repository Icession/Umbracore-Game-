import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class Window extends JFrame {

    public Window(JPanel panel) {
        setTitle("Umbracore");
        try {
            BufferedImage image = ImageIO.read(getClass().getResource("/res/icon.png"));
            setIconImage(image);
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        add(panel);
        pack();  
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}