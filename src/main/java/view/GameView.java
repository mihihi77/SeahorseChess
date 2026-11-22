package view;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GameView {

    private static final String BOARD_IMAGE_PATH = "resources/board.png";
    private static final int SLOT_MARGIN_X = 24;
    private static final int SLOT_MARGIN_Y = 20;
    private static final int SLOT_W = 240;
    private static final int SLOT_H = 74;
    private static final int SLOT_TEXT_PADDING = 14;
    private static final int SLOT_BORDER = 3;
    private static final Color SLOT_BG = new Color(12, 34, 62, 230);

    // ---- pixel font metrics for player name labels
    private static final int LABEL_SCALE = 1;
    private static final int LABEL_SPACING = -14;
    private static final int LABEL_WORD_SPACING = 14;
    private static final int GLYPH_CELL_PX = 32;

    /** Which board corner belongs to which color. */
    private enum Corner {
        GREEN(true, true),
        RED(false, true),
        YELLOW(true, false),
        BLUE(false, false);

        final boolean left;
        final boolean top;

        Corner(boolean left, boolean top) {
            this.left = left;
            this.top = top;
        }
    }

    private final List<GameSetupView.PlayerSetupData> players;
    private BufferedImage boardImage;

    private JFrame frame;

    public GameView(List<GameSetupView.PlayerSetupData> players) {
        this.players = (players != null) ? new ArrayList<>(players) : new ArrayList<>();
        loadBoardImage();
    }

    private void loadBoardImage() {
        File file = new File(BOARD_IMAGE_PATH);
        if (!file.exists()) {
            System.err.println("Board image not found: " + BOARD_IMAGE_PATH);
            return;
        }
        try {
            boardImage = ImageIO.read(file);
        } catch (IOException ex) {
            System.err.println("Failed to load board image: " + ex.getMessage());
        }
    }

    public void displayWindow() {
        frame = Theme.createFrame("LUDO Chess - Game");

        JPanel gamePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

                g2d.setColor(Theme.BACKGROUND);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                if (boardImage != null) {
                    g2d.drawImage(boardImage, 0, 0, getWidth(), getHeight(), null);
                }

                drawCornerPlayers(g2d, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        gamePanel.setPreferredSize(new Dimension(Theme.WIN_W, Theme.WIN_H));

        Theme.showFrame(frame, gamePanel);
    }

    private Map<String, GameSetupView.PlayerSetupData> playersByColor() {
        Map<String, GameSetupView.PlayerSetupData> byColor = new LinkedHashMap<>();
        for (GameSetupView.PlayerSetupData p : players) {
            if (p != null && p.colorName != null) {
                byColor.put(p.colorName.toUpperCase(), p);
            }
        }
        return byColor;
    }

    private void drawCornerPlayers(Graphics2D g2d, int panelW, int panelH) {
        Map<String, GameSetupView.PlayerSetupData> byColor = playersByColor();

        for (Corner corner : Corner.values()) {
            GameSetupView.PlayerSetupData player = byColor.get(corner.name());
            if (player == null) continue;

            int x = corner.left ? SLOT_MARGIN_X : panelW - SLOT_MARGIN_X - SLOT_W;
            int y = corner.top ? SLOT_MARGIN_Y : panelH - SLOT_MARGIN_Y - SLOT_H;
            drawPlayerSlot(g2d, Theme.PLAYER_COLORS.get(corner.name()), player, x, y, corner.left);
        }
    }

    private void drawPlayerSlot(Graphics2D g2d, Color accent, GameSetupView.PlayerSetupData player,
                                int x, int y, boolean alignLeft) {
        g2d.setColor(SLOT_BG);
        g2d.fillRect(x, y, SLOT_W, SLOT_H);
        g2d.setColor(accent);
        g2d.setStroke(new BasicStroke(SLOT_BORDER));
        g2d.drawRect(x + 1, y + 1, SLOT_W - 2, SLOT_H - 2);

        PixelFont font = PixelFont.getInstance();
        String label = player.name.toLowerCase();
        int glyphH = GLYPH_CELL_PX * LABEL_SCALE;
        int textY = y + (SLOT_H - glyphH) / 2 + 3;

        int textW = font.getTextWidth(label, LABEL_SCALE, LABEL_SPACING, LABEL_WORD_SPACING);
        int textX = x + (SLOT_W - textW) / 2;

        font.drawText(g2d, label, textX, textY, LABEL_SCALE, LABEL_SPACING, LABEL_WORD_SPACING, Color.WHITE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<GameSetupView.PlayerSetupData> demo = new ArrayList<>();
            demo.add(new GameSetupView.PlayerSetupData(1, "p1", "GREEN", Theme.PLAYER_COLORS.get("GREEN")));
            demo.add(new GameSetupView.PlayerSetupData(2, "p2", "RED", Theme.PLAYER_COLORS.get("RED")));
            demo.add(new GameSetupView.PlayerSetupData(3, "p3", "YELLOW", Theme.PLAYER_COLORS.get("YELLOW")));
            demo.add(new GameSetupView.PlayerSetupData(4, "p4", "BLUE", Theme.PLAYER_COLORS.get("BLUE")));
            new GameView(demo).displayWindow();
        });
    }
}