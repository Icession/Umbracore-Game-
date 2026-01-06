import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu extends JPanel{
    private Game game;
    private Font titleFont, buttonFont;
    private Rectangle startButton;

    public MainMenu(Game game){
        this.game = game;
        setPreferredSize(new Dimension(1024,768));
        setBackground(Color.BLACK);
        setLayout(null);

        titleFont = new Font("Monospaced",Font.BOLD,72);
        buttonFont = new Font("Monospaced",Font.PLAIN,36);

        startButton = new Rectangle(412,400,200,60);
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(startButton.contains(e.getPoint())){
                    game.startGame();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        g.setColor(Color.CYAN);
        g.setFont(titleFont);
        String title = "UMBRACORE";
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth()-fm.stringWidth(title))/2;
        g.drawString(title,x,200);
        g.setColor(new Color(0, 200, 255));
        g.fillRect(startButton.x,startButton.y,startButton.width,startButton.height);
        g.setColor(Color.white);
        g.setFont(buttonFont);
        String startTest = "START";
        int textX = startButton.x + (startButton.width - g.getFontMetrics().stringWidth(startTest))/2;
        int textY = startButton.y + (startButton.height + g.getFontMetrics().getAscent()) / 2 - 10;

        g.drawString(startTest,textX,textY);

        g.setColor(Color.lightGray);
        g.setFont(new Font("Monospaced",Font.PLAIN,18));

        String[] lines = {
                "Controls:",
                "A/< = Move left",
                "D/> = Move right",
                "Right Click = Attack",
                "SPACE/^ = Jump",
                "ESC = Pause"
        };

        int lineY = 500;
        for(String line : lines){
            int lineX = (getWidth() - g.getFontMetrics().stringWidth(line)) / 2;
            g.drawString(line,lineX,lineY);
            lineY += 30;
        }
    }
}
