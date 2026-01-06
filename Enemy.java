import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Enemy extends Entity {

    private Background gp;

    public int maxHealth = 30;
    public int health = maxHealth;
    public boolean dead = false;
    public boolean active = true; 

    public Rectangle solidArea;
    
    private int state;
    private final int RUN = 1;
    private final int ATTACK = 2;
    private final int HIT = 3;
    private final int DEAD = 4;

    private BufferedImage[] idleAni;
    private BufferedImage[] runAni;
    private BufferedImage[] attackAni;
    private BufferedImage[] hitAni;
    private BufferedImage[] deadAni;
    
    private int aniTick = 0;
    private int aniIndex = 0;
    private int aniSpeed = 15;

    public boolean invincible = false;
    public int invincibleCounter = 0;

    public Enemy(Background gp, int startX, int startY) {
        this.gp = gp;
        this.x = startX;
        this.y = startY;
        this.speed = 1; 
        this.state = RUN;
        
        solidArea = new Rectangle(0, 0, 64, 64);

        loadImages();
    }

    public boolean isAttacking() {
        return state == ATTACK;
    }

    public Rectangle getAttackBounds() {
        int screenX = x - gp.bgX;
        return new Rectangle(screenX - 60, y + 64, 140, 64);
    }
    

    private void loadImages() {
        idleAni = loadAnimStrip("/res/Idle.png");
        runAni = loadAnimStrip("/res/Run.png");
        attackAni = loadAnimStrip("/res/Attack_1.png");
        hitAni = loadAnimStrip("/res/Hurt.png");
        deadAni = loadAnimStrip("/res/Dead.png");
    }

    private BufferedImage[] loadAnimStrip(String path) {
        BufferedImage[] frames = null;
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream(path));
            if(sheet != null) {
                int h = sheet.getHeight();
                int w = sheet.getWidth();
                int cols = w / h; 
                frames = new BufferedImage[cols];
                for (int i = 0; i < cols; i++) {
                    frames[i] = sheet.getSubimage(i * h, 0, h, h);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading enemy sprite: " + path);
            e.printStackTrace();
        }
        return frames;
    }

    public void update() {
        if (!active) return;
        
        int screenX = x - gp.bgX;
        solidArea.x = screenX + 32; 
        solidArea.y = y + 64; 
        solidArea.width = 64;
        solidArea.height = 64;

        if (state == DEAD) {
            updateAnimation(deadAni);
            if (aniIndex >= deadAni.length - 1) {
                dead = true; 
                active = false;
            }
            return;
        }

        if (state == HIT) {
            updateAnimation(hitAni);
            if (aniIndex >= hitAni.length - 1) {
                state = RUN; 
                invincible = false;
                aniIndex = 0; 
            }
            return;
        }

        if (state == RUN) {
            x -= speed;
            updateAnimation(runAni);
        }
        
        int playerWorldX = gp.player.x + gp.bgX;
        int dist = Math.abs(playerWorldX - x);
        
        if (dist < 100 && state != ATTACK && state != HIT && state != DEAD) {
            state = ATTACK;
            aniIndex = 0; 
            aniTick = 0;
        }
        
        if (state == ATTACK) {
            updateAnimation(attackAni);
            if (aniIndex >= attackAni.length - 1) {
                state = RUN;
                aniIndex = 0;
            }
        }
    }

    private void updateAnimation(BufferedImage[] currentFrames) {
        if(currentFrames == null) return;
        
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= currentFrames.length) {
                aniIndex = 0;
            }
        }
    }

    public void damageEnemy(int damage, int playerWorldX) {
        if (!invincible && state != DEAD) {
            health -= damage;
            state = HIT;
            aniIndex = 0; 
            aniTick = 0;
            invincible = true;

            int knockbackDir = (this.x > playerWorldX) ? 1 : -1;
            
            x += 50 * knockbackDir; 

            if (health <= 0) {
                health = 0;
                state = DEAD;
                aniIndex = 0; 
                aniTick = 0;
            }
        }
    }

    public void draw(Graphics g) {
        if (!active && dead) return;

        int screenX = x - gp.bgX;
        
        BufferedImage[] currentAni = idleAni;
        switch(state) {
            case RUN: currentAni = runAni; break;
            case ATTACK: currentAni = attackAni; break;
            case HIT: currentAni = hitAni; break;
            case DEAD: currentAni = deadAni; break;
        }

        if (currentAni != null && aniIndex < currentAni.length) {
            g.drawImage(currentAni[aniIndex], screenX, y, 128, 128, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(screenX, y, 64, 64);
        }

        if (state != DEAD) {
            g.setColor(Color.RED);
            g.fillRect(screenX + 34, y, 60, 5);
            g.setColor(Color.GREEN);
            double hpPercent = (double) health / maxHealth;
            g.fillRect(screenX + 34, y, (int) (60 * hpPercent), 5);
        }
    }
}