import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable{

   final int tileSize = 64;
   final int screenWidth = tileSize * 16;
   final int screenHeight = tileSize * 12;
   
   Thread gameThread;
   int fps = 60;

   KeyboardInput keyH = new KeyboardInput();
   private MouseInput mouseH = new MouseInput();
   Player player = new Player(this, keyH, mouseH);

   public GamePanel() {
      this.setPreferredSize(new Dimension(screenWidth, screenHeight));
      this.setBackground(Color.gray);
      this.setDoubleBuffered(true);
      this.setFocusable(true);
      this.addKeyListener(keyH);
   }

   public void startGameThread() {
      gameThread = new Thread(this);
      gameThread.start();
   }

   public void run() {
      double drawInterval = 1000000000 / fps;
      double nextDrawTime = System.nanoTime() + drawInterval;

      while (gameThread != null) {
         update();
         repaint();

         try {
            double remainingTime = nextDrawTime - System.nanoTime();
            remainingTime = remainingTime / 1000000;

            if (remainingTime < 0) {
               remainingTime = 0;
            }

            Thread.sleep((long)remainingTime);

            nextDrawTime += drawInterval;
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   public void update() {
      player.update();
   }

   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      g.setColor(new Color(10, 15, 30)); 
      g.fillRect(0, 0, screenWidth, screenHeight);

      g.setColor(new Color(25, 40, 60));
      g.fillRect(200, 0, 100, screenHeight - 200);
      g.fillRect(700, 50, 150, screenHeight - 200);
      g.fillRect(500, 150, 60, screenHeight - 200);

      g.setColor(new Color(0, 255, 255, 20));
      g.fillOval(300, 100, 600, 600);

      int groundLevel = 492; 

      g.setColor(Color.black); 
      g.fillRect(0, groundLevel, screenWidth, screenHeight - groundLevel);

      g.setColor(new Color(0, 200, 255)); 
      g.fillRect(0, groundLevel, screenWidth, 4);

      player.draw(g);
   }
}