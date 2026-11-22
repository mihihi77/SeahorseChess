package view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import model.Player;
import model.PlayerColor;

public class MenuView {

    private static final double BUTTON_SCALE = 1.7;
    private static final int BUTTON_GAP = 4;
    private static final double BUTTON_Y_RATIO = 0.53;
    private static final double WELCOME_Y_RATIO = 0.09;
    private static final int WELCOME_CHAR_DELAY_MS = 120;
    private static final int FALLBACK_FRAME_DELAY_MS = 100;

    private static final String BG_GIF_PATH = "resources/menu/mainview.gif";
    private static final String PLAY_BTN_PATH = "resources/menu/PLAY.png";
    private static final String SETTING_BTN_PATH = "resources/menu/SETTING.png";
    private static final String RANK_BTN_PATH = "resources/menu/RANKING.png";

    private static final String WELCOME_TEXT = "welcome to";

    private final GifLoader.Animation background;
    private int currentFrameIdx = 0;
    private final Timer gifTimer;

    private int visibleCharCount = 0;
    private final Timer welcomeTimer;

    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel buttonPanel;

    public MenuView() {
        background = GifLoader.load(BG_GIF_PATH);

        welcomeTimer = new Timer(WELCOME_CHAR_DELAY_MS, e -> {
            if (visibleCharCount < WELCOME_TEXT.length()) {
                visibleCharCount++;
                repaintPanel();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });

        gifTimer = background.isEmpty() ? null : createGifTimer();
    }

    private Timer createGifTimer() {
        Timer timer = new Timer(frameDelay(0), null);
        timer.addActionListener(e -> {
            currentFrameIdx = (currentFrameIdx + 1) % background.frames.size();
            timer.setDelay(frameDelay(currentFrameIdx));
            repaintPanel();
        });
        return timer;
    }

    private int frameDelay(int index) {
        return background.delaysMs.isEmpty() ? FALLBACK_FRAME_DELAY_MS : background.delaysMs.get(index);
    }

    private void repaintPanel() {
        if (mainPanel != null) mainPanel.repaint();
    }


    public void displayWindow() {
        mainFrame = Theme.createFrame("LUDO Chess");
        mainPanel = createMainPanel();
        buttonPanel = createButtonPanel();
        layoutButtons();
        mainPanel.add(buttonPanel);

        Theme.showFrame(mainFrame, mainPanel);

        welcomeTimer.start();
        if (gifTimer != null) gifTimer.start();
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

                g2d.setColor(Theme.BACKGROUND);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                if (!background.isEmpty()) {
                    BufferedImage frame = background.frames.get(currentFrameIdx);
                    g2d.drawImage(frame, 0, 0, getWidth(), getHeight(), null);
                }

                PixelFont.getInstance().drawCenteredText(
                        g2d,
                        WELCOME_TEXT.substring(0, visibleCharCount),
                        getWidth() / 2,
                        (int) (getHeight() * WELCOME_Y_RATIO),
                        2, -32, 40,
                        Color.WHITE
                );
                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(Theme.WIN_W, Theme.WIN_H));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JButton playButton = createImageButton(PLAY_BTN_PATH);
        JButton settingButton = createImageButton(SETTING_BTN_PATH);
        JButton rankingButton = createImageButton(RANK_BTN_PATH);

        playButton.addActionListener(e -> openGameSetup());
        settingButton.addActionListener(e -> System.out.println("Open settings"));
        rankingButton.addActionListener(e -> System.out.println("Open ranking"));

        panel.add(playButton);
        panel.add(Box.createRigidArea(new Dimension(0, BUTTON_GAP)));
        panel.add(settingButton);
        panel.add(Box.createRigidArea(new Dimension(0, BUTTON_GAP)));
        panel.add(rankingButton);
        return panel;
    }

    private void layoutButtons() {
        Dimension size = buttonPanel.getPreferredSize();
        buttonPanel.setSize(size);
        buttonPanel.setLocation((Theme.WIN_W - size.width) / 2, (int) (Theme.WIN_H * BUTTON_Y_RATIO));
    }

    private void openGameSetup() {
        welcomeTimer.stop();
        if (gifTimer != null) gifTimer.stop();
        mainFrame.dispose();

        new GameSetupView(this, setupDataList -> {
            List<Player> domainPlayers = new ArrayList<>();
            for (GameSetupView.PlayerSetupData data : setupDataList) {
                domainPlayers.add(new Player(
                        data.slotIndex,
                        data.name,
                        PlayerColor.fromString(data.colorName)
                ));
            }
            new GameView(setupDataList).displayWindow();
        }).displayWindow();
    }

    private JButton createImageButton(String path) {
        ImageIcon raw = new ImageIcon(path);
        int w = Math.max(1, (int) Math.round(raw.getIconWidth() * BUTTON_SCALE));
        int h = Math.max(1, (int) Math.round(raw.getIconHeight() * BUTTON_SCALE));

        Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_FAST);
        JButton btn = new JButton(new ImageIcon(scaled));
        btn.setPreferredSize(new Dimension(w, h));
        btn.setMaximumSize(new Dimension(w, h));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuView().displayWindow());
    }
}