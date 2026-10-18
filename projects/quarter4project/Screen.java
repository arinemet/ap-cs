import javax.swing.JPanel;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Screen extends JPanel implements MouseListener, KeyListener, ActionListener {

    public static final int SQ = 80;
    public static final int BOARD_PX = 640;
    public static final int SIDEBAR = 240;
    public static final int WIDTH = 880;
    public static final int HEIGHT = 720;
    public static final int BOARD_OFFSET_X = 20;
    public static final int BOARD_OFFSET_Y = 60;

    public static final int START = 0;
    public static final int PLAYING = 1;
    public static final int END = 2;

    private BoardGame game;
    private int state;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<int[]> highlightedMoves = new ArrayList<>();

    // animation state
    private boolean animating = false;
    private Sprite animPiece;
    private double animRow, animCol;
    private int animFromRow, animFromCol;
    private int animToRow, animToCol;
    private MoveResult pendingResult;
    private Timer animationTimer;

    private Map<String, BufferedImage> pieceImages = new HashMap<>();
    private Clip captureSound;
    private Clip checkSound;

    private String statusMessage = "";

    // restart button bounds on END screen
    private Rectangle restartButton = new Rectangle(WIDTH / 2 - 90, HEIGHT / 2 + 60, 180, 50);

    // start button bounds on START screen
    private Rectangle startButton = new Rectangle(WIDTH / 2 - 90, 500, 180, 60);

    public Screen() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(40, 40, 40));
        addMouseListener(this);
        setFocusable(true);
        addKeyListener(this);
        game = new BoardGame();
        state = START;
        loadImages();
        loadSounds();
        animationTimer = new Timer(16, this);
    }

    private File resourceFile(String folder, String name) {
        return new File(folder, name);
    }

    private void loadImages() {
        String[] colors = {"w", "b"};
        String[] pieceNames = {"king", "queen", "rook", "bishop", "knight", "pawn"};
        for (String piece : pieceNames) {
            for (String color : colors) {
                String name = piece + "-" + color + ".png";
                try {
                    File f = resourceFile("assets", name);
                    if (f.exists()) {
                        BufferedImage img = ImageIO.read(f);
                        pieceImages.put(name, img);
                    }
                } catch (Exception e) {
                    // leave null - fallback to glyph
                }
            }
        }
    }

    private void loadSounds() {
        captureSound = tryLoadClip(resourceFile("sounds", "capture.wav"));
        checkSound = tryLoadClip(resourceFile("sounds", "check.wav"));
    }

    private Clip tryLoadClip(File f) {
        try {
            if (!f.exists()) return null;
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    private void playClip(Clip c) {
        if (c == null) return;
        try {
            c.stop();
            c.setFramePosition(0);
            c.start();
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (state == START) {
            drawStart(g2);
        } else if (state == PLAYING) {
            drawPlaying(g2);
        } else if (state == END) {
            drawPlaying(g2);
            drawEndOverlay(g2);
        }
    }

    private void drawStart(Graphics2D g2) {
        g2.setColor(new Color(40, 40, 40));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(new Color(238, 238, 210));
        g2.setFont(new Font("SansSerif", Font.BOLD, 80));
        String title = "CHESS";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, (WIDTH - fm.stringWidth(title)) / 2, 180);

        g2.setColor(new Color(180, 180, 180));
        g2.setFont(new Font("SansSerif", Font.PLAIN, 28));
        String sub = "Q4 Project";
        fm = g2.getFontMetrics();
        g2.drawString(sub, (WIDTH - fm.stringWidth(sub)) / 2, 220);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        String[] instr = {
            "• Click a piece, then click destination to move",
            "• White moves first",
            "• Capture the king to win",
            "• Press R to restart on end screen",
            "• Press F1 to jump to end screen (test)"
        };
        int y = 320;
        for (String line : instr) {
            fm = g2.getFontMetrics();
            g2.drawString(line, (WIDTH - fm.stringWidth(line)) / 2, y);
            y += 32;
        }

        // start button
        g2.setColor(new Color(118, 150, 86));
        g2.fillRoundRect(startButton.x, startButton.y, startButton.width, startButton.height, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        String btn = "START";
        fm = g2.getFontMetrics();
        g2.drawString(btn, startButton.x + (startButton.width - fm.stringWidth(btn)) / 2,
                startButton.y + (startButton.height + fm.getAscent()) / 2 - 6);
    }

    private void drawPlaying(Graphics2D g2) {
        // status bar
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        String turn = game.isWhiteTurn() ? "White's turn" : "Black's turn";
        g2.drawString(turn, BOARD_OFFSET_X, 35);

        if (statusMessage != null && !statusMessage.isEmpty()) {
            g2.setColor(new Color(220, 60, 60));
            g2.drawString(statusMessage, BOARD_OFFSET_X + 200, 35);
        }

        // board
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                Color light = new Color(238, 238, 210);
                Color dark = new Color(118, 150, 86);
                Color sq = ((r + c) % 2 == 0) ? light : dark;
                g2.setColor(sq);
                int x = BOARD_OFFSET_X + c * SQ;
                int y = BOARD_OFFSET_Y + r * SQ;
                g2.fillRect(x, y, SQ, SQ);
            }
        }

        // selected highlight
        if (selectedRow >= 0 && selectedCol >= 0) {
            g2.setColor(new Color(255, 255, 0, 110));
            g2.fillRect(BOARD_OFFSET_X + selectedCol * SQ, BOARD_OFFSET_Y + selectedRow * SQ, SQ, SQ);
        }

        // highlighted moves
        g2.setColor(new Color(50, 50, 50, 120));
        for (int[] mv : highlightedMoves) {
            int r = mv[0];
            int c = mv[1];
            int cx = BOARD_OFFSET_X + c * SQ + SQ / 2;
            int cy = BOARD_OFFSET_Y + r * SQ + SQ / 2;
            g2.fillOval(cx - 12, cy - 12, 24, 24);
        }

        // pieces
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (animating && r == animFromRow && c == animFromCol) continue;
                Sprite p = game.getPieceAt(r, c);
                if (p == null) continue;
                if (animating && p == animPiece) continue;
                drawSprite(g2, p, BOARD_OFFSET_X + c * SQ, BOARD_OFFSET_Y + r * SQ);
            }
        }

        // animating piece
        if (animating && animPiece != null) {
            int x = BOARD_OFFSET_X + (int) Math.round(animCol * SQ);
            int y = BOARD_OFFSET_Y + (int) Math.round(animRow * SQ);
            drawSprite(g2, animPiece, x, y);
        }

        // sidebar - captured
        int sx = BOARD_OFFSET_X + BOARD_PX + 20;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        g2.drawString("Captured", sx, 35);

        ArrayList<Sprite> captured = game.getCapturedPieces();
        if (captured != null) {
            int cx = sx;
            int cy = BOARD_OFFSET_Y;
            int small = 36;
            int perRow = 5;
            int idx = 0;
            for (Sprite s : captured) {
                int row = idx / perRow;
                int col = idx % perRow;
                int dx = cx + col * (small + 4);
                int dy = cy + row * (small + 4);
                drawSpriteScaled(g2, s, dx, dy, small);
                idx++;
            }
        }
    }

    private void drawSprite(Graphics2D g2, Sprite p, int x, int y) {
        drawSpriteScaled(g2, p, x, y, SQ);
    }

    private void drawSpriteScaled(Graphics2D g2, Sprite p, int x, int y, int size) {
        String fname = p.getImageFileName();
        BufferedImage img = pieceImages.get(fname);
        if (img != null) {
            g2.drawImage(img, x, y, size, size, null);
        } else {
            // fallback Unicode glyph
            char glyph = unicodeFor(p);
            g2.setFont(new Font("Serif", Font.PLAIN, (int) (size * 0.8)));
            g2.setColor(p.isWhite() ? Color.WHITE : Color.BLACK);
            FontMetrics fm = g2.getFontMetrics();
            String gs = String.valueOf(glyph);
            int gw = fm.stringWidth(gs);
            int gh = fm.getAscent();
            g2.drawString(gs, x + (size - gw) / 2, y + (size + gh) / 2 - 4);
        }
    }

    private char unicodeFor(Sprite p) {
        char sym = p.getSymbol();
        boolean white = p.isWhite();
        char base;
        switch (Character.toUpperCase(sym)) {
            case 'K': base = white ? '♔' : '♚'; break;
            case 'Q': base = white ? '♕' : '♛'; break;
            case 'R': base = white ? '♖' : '♜'; break;
            case 'B': base = white ? '♗' : '♝'; break;
            case 'N': base = white ? '♘' : '♞'; break;
            case 'P': base = white ? '♙' : '♟'; break;
            default: base = '?';
        }
        return base;
    }

    private void drawEndOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 64));
        String go = "GAME OVER";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(go, (WIDTH - fm.stringWidth(go)) / 2, HEIGHT / 2 - 60);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 30));
        String winner = "Winner: " + game.getWinner();
        fm = g2.getFontMetrics();
        g2.drawString(winner, (WIDTH - fm.stringWidth(winner)) / 2, HEIGHT / 2);

        // restart button
        g2.setColor(new Color(118, 150, 86));
        g2.fillRoundRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height, 12, 12);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 22));
        String btn = "Restart (R)";
        fm = g2.getFontMetrics();
        g2.drawString(btn, restartButton.x + (restartButton.width - fm.stringWidth(btn)) / 2,
                restartButton.y + (restartButton.height + fm.getAscent()) / 2 - 4);
    }

    // ===== Mouse =====
    @Override
    public void mouseClicked(MouseEvent e) { }

    private void startAnimation(MoveResult res) {
        animating = true;
        animPiece = game.getPieceAt(res.toRow, res.toCol);
        animFromRow = res.fromRow;
        animFromCol = res.fromCol;
        animRow = res.fromRow;
        animCol = res.fromCol;
        animToRow = res.toRow;
        animToCol = res.toCol;
        pendingResult = res;
        selectedRow = -1;
        selectedCol = -1;
        highlightedMoves.clear();
        animationTimer.start();
    }

    private void doRestart() {
        game.reset();
        state = PLAYING;
        selectedRow = -1;
        selectedCol = -1;
        highlightedMoves.clear();
        statusMessage = "";
        animating = false;
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!animating) {
            animationTimer.stop();
            return;
        }
        double dr = animToRow - animRow;
        double dc = animToCol - animCol;
        double step = 0.15;
        if (Math.abs(dr) < 0.05 && Math.abs(dc) < 0.05) {
            animRow = animToRow;
            animCol = animToCol;
            animating = false;
            animationTimer.stop();

            if (pendingResult != null) {
                // Two sound effects: "takes" (capture) and "check".
                // A capturing move plays the capture sound; a checking/mating
                // move plays the check sound. A move that both captures AND
                // gives check plays both (capture first, then check).
                if (pendingResult.captured != null) {
                    playClip(captureSound);
                }
                if (pendingResult.check || pendingResult.checkmate) {
                    playClip(checkSound);
                }
                if (pendingResult.checkmate) statusMessage = "Checkmate!";
                else if (pendingResult.stalemate) statusMessage = "Stalemate";
                else if (pendingResult.check) statusMessage = "Check!";
                else statusMessage = "";
            }

            if (game.isGameOver()) {
                state = END;
            }
            pendingResult = null;
            animPiece = null;
        } else {
            if (Math.abs(dr) < step) animRow = animToRow;
            else animRow += Math.signum(dr) * step;
            if (Math.abs(dc) < step) animCol = animToCol;
            else animCol += Math.signum(dc) * step;
        }
        repaint();
    }

    // ===== Key =====
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F1) {
            game.forceEndGame(true);
            statusMessage = "F1 test end";
            state = END;
            repaint();
            return;
        }
        if (state == END && e.getKeyCode() == KeyEvent.VK_R) {
            doRestart();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (state == START) {
            if (startButton.contains(e.getPoint())) {
                state = PLAYING;
                repaint();
            }
            return;
        }
        if (state == END) {
            if (restartButton.contains(e.getPoint())) {
                doRestart();
            }
            return;
        }
        if (state == PLAYING && !animating) {
            int mx = e.getX();
            int my = e.getY();
            int col = (mx - BOARD_OFFSET_X) / SQ;
            int row = (my - BOARD_OFFSET_Y) / SQ;
            if (mx < BOARD_OFFSET_X || my < BOARD_OFFSET_Y || row < 0 || row > 7 || col < 0 || col > 7) {
                return;
            }

            if (selectedRow == -1) {
                Sprite p = game.getPieceAt(row, col);
                if (p != null && p.isWhite() == game.isWhiteTurn()) {
                    selectedRow = row;
                    selectedCol = col;
                    highlightedMoves = game.getLegalMoves(row, col);
                    if (highlightedMoves == null) highlightedMoves = new ArrayList<>();
                }
            } else {
                if (row == selectedRow && col == selectedCol) {
                    // deselect
                    selectedRow = -1;
                    selectedCol = -1;
                    highlightedMoves.clear();
                } else {
                    MoveResult res = game.tryMove(selectedRow, selectedCol, row, col);
                    if (res != null && res.success) {
                        startAnimation(res);
                    } else {
                        Sprite p = game.getPieceAt(row, col);
                        if (p != null && p.isWhite() == game.isWhiteTurn()) {
                            selectedRow = row;
                            selectedCol = col;
                            highlightedMoves = game.getLegalMoves(row, col);
                            if (highlightedMoves == null) highlightedMoves = new ArrayList<>();
                        } else {
                            selectedRow = -1;
                            selectedCol = -1;
                            highlightedMoves.clear();
                        }
                    }
                }
            }
            repaint();
        }
    }
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
