import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JPanel;  

public class Background extends JPanel implements Runnable {

    final int tileSize = 64;
    final int screenWidth = tileSize * 16;
    final int screenHeight = tileSize * 12;

    Thread gameThread;
    int fps = 60;
    private Game game; 

    public int bgX = 0;

    final int platY = 500;
    final int plat1X = 550;
    final int plat1W = 200;

    public boolean paused = false;
    boolean wasEscPressed = false; 
    
    Rectangle resumeButton = new Rectangle(412, 350, 200, 60);
    Rectangle restartButton = new Rectangle(412, 430, 200, 60);
    Rectangle menuButton = new Rectangle(412, 510, 200, 60);

    KeyboardInput keyH = new KeyboardInput();
    private MouseInput mouseH = new MouseInput();
   
    Player player; 
    
    ArrayList<Enemy> enemies = new ArrayList<>();
    boolean stageCleared = false;
   
    public Background(Game game) {
        this.game = game; 
        this.player = new Player(this, keyH, mouseH);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyH);
        this.addMouseListener(mouseH);
        
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (paused || player.health <= 0 || stageCleared) {
                    
                    
                    if (resumeButton.contains(e.getPoint()) && !stageCleared && player.health > 0) {
                        paused = false;
                    } 
                    
                    else if (restartButton.contains(e.getPoint())) { 
                        stopGameThread(); 
                        game.restartGame(); 
                    }
                   
                    else if (menuButton.contains(e.getPoint())) {
                        stopGameThread();
                        game.returnToMenu();
                    }
                }
            }
        });

        spawnEnemies();
    }

    public void spawnEnemies() {
        enemies.add(new Enemy(this, 800, 595));
        enemies.add(new Enemy(this, 1400, 595));
        enemies.add(new Enemy(this, 2000, 595));
    }

    public void stopGameThread() {
        if (gameThread != null) {
            gameThread.interrupt();
            gameThread = null;
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / fps;
        double nextDrawTime = System.nanoTime() + drawInterval;

        while (gameThread != null) {
            update();
            repaint();

            try {
                double remainingTime = nextDrawTime - System.nanoTime();
                remainingTime = remainingTime / 1000000;

                if (remainingTime < 0) remainingTime = 0;

                Thread.sleep((long)remainingTime);
                nextDrawTime += drawInterval;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        if (keyH.escape && !wasEscPressed) {
            paused = !paused;
            wasEscPressed = true;
        } else if (!keyH.escape) {
            wasEscPressed = false;
        }
        
        if (paused || stageCleared || player.health <= 0) { 
            return;
        }

        player.update();
        
        Game.scoreManager.updateDistanceScore(player.x + bgX); 

        Iterator<Enemy> it = enemies.iterator();
        while(it.hasNext()) {
            Enemy e = it.next();
            e.update();
            
            if(player.attacking && !e.invincible) {
                if(player.getAttackBounds().intersects(e.solidArea)) {
                    e.damageEnemy(10, player.x + bgX); 
                }
            }
            
            if(!e.dead && !e.invincible) {
                if(e.isAttacking()) {
                    if(player.solidArea.intersects(e.getAttackBounds())) {
                        player.takeDamage(10);
                    }
                }
                if(player.solidArea.intersects(e.solidArea)) {
                    player.takeDamage(10); 
                }
            }
            
            if(e.dead) {
                Game.scoreManager.addKillScore(100); 
                it.remove();
            }
        }
        
        if(enemies.isEmpty()) {
            stageCleared = true;
        }
    }

    public int getGroundY(int playerX, int playerY) {
        int defaultGround = 725;
        int relativeX = (playerX + bgX) % screenWidth;
        if (relativeX < 0) relativeX += screenWidth;
        int centerX = relativeX + 96;

        if (centerX > plat1X && centerX < plat1X + plat1W) {
            if (playerY + 192 <= platY + 60) {
                 return platY; 
            }
        }
        return defaultGround; 
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int shift = bgX % screenWidth;
        if (shift < 0) shift += screenWidth;

        drawSciFiBackground(g, -shift);
        drawSciFiBackground(g, -shift + screenWidth);
        
        for(Enemy e : enemies) {
            e.draw(g);
        }

        player.draw(g);
        Game.scoreManager.draw(g, screenWidth); 

        if (paused) {
            drawMenuOverlay(g, "PAUSED");
        } else if (stageCleared) {
            drawMenuOverlay(g, "STAGE CLEARED!");
        } else if (player.health <= 0) {
            drawMenuOverlay(g, "GAME OVER");
        }
    }

    private void drawMenuOverlay(Graphics g, String title) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, screenWidth, screenHeight);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.BOLD, 72));
        int x = (screenWidth - g.getFontMetrics().stringWidth(title)) / 2;
        g.drawString(title, x, 250);
        
        if (stageCleared || player.health <= 0) {
            g.setFont(new Font("Monospaced", Font.PLAIN, 40));
            String scoreText = "Final Score: " + Game.scoreManager.getScore();
            int scoreX = (screenWidth - g.getFontMetrics().stringWidth(scoreText)) / 2;
            g.drawString(scoreText, scoreX, 320);
        }
        
        if(title.equals("PAUSED")) {
            g.setColor(new Color(0, 200, 255));
            g.fillRect(resumeButton.x, resumeButton.y, resumeButton.width, resumeButton.height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Monospaced", Font.PLAIN, 36));
            String btnTextResume = "RESUME";
            int btnXResume = resumeButton.x + (resumeButton.width - g.getFontMetrics().stringWidth(btnTextResume)) / 2;
            int btnYResume = resumeButton.y + (resumeButton.height + g.getFontMetrics().getAscent()) / 2 - 10;
            g.drawString(btnTextResume, btnXResume, btnYResume);
        }
        
        g.setColor(new Color(255, 100, 0)); 
        g.fillRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 36));
        String btnTextRestart = "RESTART";
        int btnXRestart = restartButton.x + (restartButton.width - g.getFontMetrics().stringWidth(btnTextRestart)) / 2;
        int btnYRestart = restartButton.y + (restartButton.height + g.getFontMetrics().getAscent()) / 2 - 10;
        g.drawString(btnTextRestart, btnXRestart, btnYRestart);

        g.setColor(new Color(150, 0, 200)); 
        g.fillRect(menuButton.x, menuButton.y, menuButton.width, menuButton.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Monospaced", Font.PLAIN, 36));
        String btnTextMenu = "MENU";
        int btnXMenu = menuButton.x + (menuButton.width - g.getFontMetrics().stringWidth(btnTextMenu)) / 2;
        int btnYMenu = menuButton.y + (menuButton.height + g.getFontMetrics().getAscent()) / 2 - 10;
        g.drawString(btnTextMenu, btnXMenu, btnYMenu);
    }

    private void drawSciFiBackground(Graphics g, int xOffset) {
        g.setColor(new Color(5, 5, 15));
        g.fillRect(xOffset, 0, screenWidth, screenHeight);
        g.setColor(new Color(20, 25, 40)); 
        g.fillRect(xOffset + 100, 100, 80, screenHeight);
        g.fillRect(xOffset + 800, 200, 120, screenHeight);
        g.setColor(new Color(30, 35, 50));
        int[] xPointsA = {xOffset + 200, xOffset + 280, xOffset + 260, xOffset + 300, xOffset + 220};
        int[] yPointsA = {0, 200, 400, 600, screenHeight};
        g.fillPolygon(new Polygon(xPointsA, yPointsA, 5));
        int[] xPointsB = {xOffset + 500, xOffset + 580, xOffset + 540};
        int[] yPointsB = {0, 0, 450};
        g.fillPolygon(new Polygon(xPointsB, yPointsB, 3));
        int[] xPointsC = {xOffset + 750, xOffset + 900, xOffset + 950, xOffset + 850};
        int[] yPointsC = {0, 0, screenHeight, screenHeight};
        g.fillPolygon(new Polygon(xPointsC, yPointsC, 4));
        g.setColor(new Color(0, 100, 150, 30));
        g.fillOval(xOffset + 200, 200, 600, 400);
        drawPlatform(g, xOffset + plat1X, platY, plat1W);
        int groundLevel = 725;
        g.setColor(new Color(15, 15, 20));
        g.fillRect(xOffset, groundLevel, screenWidth, screenHeight - groundLevel);
        g.setColor(new Color(30, 35, 50));
        g.fillOval(xOffset + 100, groundLevel - 10, 50, 20);
        g.fillOval(xOffset + 400, groundLevel - 5, 30, 15);
        g.fillOval(xOffset + 850, groundLevel - 15, 60, 25);
    }

    private void drawPlatform(Graphics g, int px, int py, int pw) {
        g.setColor(new Color(30, 35, 50));
        int[] platXPoints = {px, px + pw, px + pw - 20, px + 20};
        int[] platYPoints = {py, py, py + 40, py + 30};
        g.fillPolygon(new Polygon(platXPoints, platYPoints, 4));
        g.setColor(new Color(0, 200, 255));
        g.fillRect(px, py, pw, 4);
    }
}