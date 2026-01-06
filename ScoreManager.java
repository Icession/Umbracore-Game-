import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class ScoreManager {

    public int score = 0;
    private int lastX = 0;

    public void addKillScore(int amount) {
        score += amount;
    }

    public void updateDistanceScore(int playerX) {
        if (playerX > lastX) {
            score += (playerX - lastX) / 10;
            lastX = playerX;
        }
    }

    public int getScore() {
        return score;
    }
    public void reset() { 
        reset(0); 
    }
    public void reset(int startX) {
        score = 0;
        lastX = startX;
    }
    

    
    public void draw(Graphics g, int screenWidth) {
        g.setFont(new Font("Monospaced", Font.BOLD, 28));
        g.setColor(Color.CYAN);

        String text = "SCORE: " + score;
        int textWidth = g.getFontMetrics().stringWidth(text);

        g.drawString(text, screenWidth - textWidth - 30, 40);
    }
}
