public class Game {
    private Window window;
    private MainMenu mainMenu;
    private Background backgroundPanel;
    private Sound backgroundMusic;
    private final String MUSIC_PATH = "/res/backgroundmusic.wav";
    
    public static ScoreManager scoreManager = new ScoreManager();

    public Game() {
        mainMenu = new MainMenu(this);
        window = new Window(mainMenu);
        mainMenu.requestFocusInWindow();
        backgroundMusic = new Sound(MUSIC_PATH);
        backgroundMusic.setVolume(-15.0f);
    }

    public void startGame() {
        Game.scoreManager.reset(100);

        window.remove(mainMenu);
        backgroundPanel = new Background(this); 
        window.add(backgroundPanel);
        window.pack();
        backgroundPanel.requestFocusInWindow();
        backgroundPanel.startGameThread();
        backgroundMusic.loop();
        window.revalidate();
        window.repaint();
    }

    public void restartGame() {
        Game.scoreManager.reset(100);

        if (backgroundPanel != null) {
            backgroundMusic.stop();
            window.remove(backgroundPanel);
        }
        backgroundPanel = new Background(this); 
        window.add(backgroundPanel);
        window.pack();
        backgroundPanel.requestFocusInWindow();
        backgroundPanel.startGameThread();
        backgroundMusic.loop();
        window.revalidate();
        window.repaint();
    }

    public void returnToMenu() {
        if (backgroundPanel != null) {
            backgroundMusic.stop();
            window.remove(backgroundPanel);
            backgroundPanel = null;
        }
        window.add(mainMenu);
        window.pack();
        mainMenu.requestFocusInWindow();
        window.revalidate();
        window.repaint();
    }
}