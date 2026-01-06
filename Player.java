import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Player extends Entity {
    
    JPanel gp;
    KeyboardInput keyH;
    private MouseInput mouseH; 

    public boolean attacking = false;
    public boolean moving = false; 

    public BufferedImage[] attackAni; 
    public BufferedImage[] runAni; 
    
    private int attackAniIndex = 0;
    private int attackAniTick = 0;
    private final int attackAniSpeed = 5;

    int velocityY = 0;
    int gravity = 1;
    int jumpStrength = 22;
    boolean onGround = true;
    String direction = "right";
    
    public boolean knockback = false;
    public int knockbackCounter = 0;

    public Rectangle solidArea; 
    
    final int floorOffset = 48; 

    public int maxHealth = 100;
    public int health = 100;

    public Player(JPanel gp, KeyboardInput keyH, MouseInput mouseH) {
        this.gp = gp;
        this.keyH = keyH;
        this.mouseH = mouseH; 
        
        this.idleAni = new BufferedImage[3];
        this.runAni = new BufferedImage[3];

        solidArea = new Rectangle(x + 70, y + 60, 50, 100);

        setDefaultValues();
        getPlayerImage();
    }

    public Rectangle getAttackBounds() {
        int attackRange = 100; 
        int attackHeight = 100;
        
        if(direction.equals("right")) {
            return new Rectangle(x + 100, y + 40, attackRange, attackHeight);
        } 
        else {
            return new Rectangle(x - 40, y + 40, attackRange, attackHeight);
        }
    }

    public void setDefaultValues() {
        x = 100;
        y = 500; 
        speed = 4;
    }

    public void getPlayerImage() {
        try {
            BufferedImage sheet = ImageIO.read(getClass().getResourceAsStream("/res/Idlesheet.png"));
            for (int i = 0; i < idleAni.length; i++) {
                idleAni[i] = sheet.getSubimage(i * 64, 0, 64, 64);
            }

            BufferedImage walkSheet = ImageIO.read(getClass().getResourceAsStream("/res/WalkSheet.png"));
            for (int i = 0; i < runAni.length; i++) {
                runAni[i] = walkSheet.getSubimage(i * 64, 0, 64, 64);
            }

            BufferedImage attackSheet = ImageIO.read(getClass().getResourceAsStream("/res/attack.png"));
            attackAni = new BufferedImage[4]; 
            for (int i = 0; i < attackAni.length; i++) {
                attackAni[i] = attackSheet.getSubimage(i * 64, 0, 64, 64);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) {
        BufferedImage imageToDraw = null;
        int width = 64 * 3;
        int height = 64 * 3;

        if (attacking && attackAni != null && attackAniIndex < attackAni.length) {
            imageToDraw = attackAni[attackAniIndex];
        } 
        else if (moving && runAni != null && aniIndex < runAni.length) {
            imageToDraw = runAni[aniIndex];
        } 
        else if (idleAni != null && aniIndex < idleAni.length) {
            imageToDraw = idleAni[aniIndex];
        }
        
        if (imageToDraw != null) {
            if (direction.equals("right")) {
                g.drawImage(imageToDraw, x, y, width, height, null);
            } else {
                g.drawImage(imageToDraw, x + width, y, -width, height, null); 
            }
        }
        
        drawHealthBar(g, width);
    }
    
    private void drawHealthBar(Graphics g, int width) {
        int barWidth = 120;
        int barHeight = 12;
        int barX = x + (width / 2) - (barWidth / 2);
        int barY = y + 20; 

        g.setColor(Color.darkGray);
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(Color.green);
        int currentWidth = (int) ((double) health / maxHealth * barWidth);
        g.fillRect(barX, barY, currentWidth, barHeight);
        g.setColor(Color.black);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    public void update() {
        solidArea.x = x + 70;
        solidArea.y = y + 60;
        
        moving = false;

        Background bg = null;
        if (gp instanceof Background) {
            bg = (Background) gp;
        }

        if(knockback) {
            knockbackCounter++;
            if(direction.equals("right")) {
                if(x > 100) x -= 10;
                else if(bg != null) bg.bgX -= 10;
            } else {
                if(x < 400) x += 10;
                else if(bg != null) bg.bgX += 10;
            }
            
            if(knockbackCounter > 10) { 
                knockback = false;
                knockbackCounter = 0;
            }
            velocityY += gravity;
            y += velocityY;
            handleCollision(bg);
            return; 
        }

        if (mouseH.attack && !attacking) {
             attacking = true;
             attackAniIndex = 0;
             attackAniTick = 0;
             mouseH.attack = false; 
        }

        if (attacking) {
            attackAniTick++;
            if (attackAniTick > attackAniSpeed) {
                attackAniTick = 0;
                attackAniIndex++;
                if (attackAniIndex >= attackAni.length) {
                    attacking = false;
                    attackAniIndex = 0;
                }
            }
        }

        if (keyH.left) {
            direction = "left";
            moving = true; 
            if (x > 100) {
                x -= speed;
            } else if (bg != null) {
                bg.bgX -= speed; 
            }
        }

        if (keyH.right) {
            direction = "right";
            moving = true; 
            if (x < 400) {
                x += speed;
            } else if (bg != null) {
                bg.bgX += speed; 
            }
        }

        if (keyH.jump && onGround) {
            velocityY = -jumpStrength;
            onGround = false;
        }
            
        if (!attacking) { 
            aniTick++;
            if (aniTick > aniSpeed) {
                aniTick = 0;
                aniIndex++;
                
                int currentMaxLength = moving ? runAni.length : idleAni.length;
                
                if (aniIndex >= currentMaxLength)
                    aniIndex = 0;
            }
        }

        velocityY += gravity;
        y += velocityY;
        
        handleCollision(bg);
    }
    
    private void handleCollision(Background bg) {
        int currentFloorY = 725;
        if (bg != null) {
            currentFloorY = bg.getGroundY(x, y);
        }
        
        int playerHeight = 64 * 3;
        int playerFeetY = y + playerHeight - floorOffset;
        
        if (playerFeetY >= currentFloorY) {
            y = currentFloorY - playerHeight + floorOffset;
            velocityY = 0;
            onGround = true;
        } else {
            onGround = false;
        }
    }
    
    public void takeDamage(int amount) {
        if(!knockback) { 
            health -= amount;
            if (health < 0) health = 0;
            knockback = true;
            velocityY = -10; 
        }
    }
}