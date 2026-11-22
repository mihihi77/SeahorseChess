package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameSetupView {

    public static class PlayerSetupData {
        public final int slotIndex;
        public final String name;
        public final String colorName;
        public final Color color;

        public PlayerSetupData(int slotIndex, String name, String colorName, Color color) {
            this.slotIndex = slotIndex;
            this.name = name;
            this.colorName = colorName;
            this.color = color;
        }
    }

    public interface OnStartGameListener {
        void onStartGame(List<PlayerSetupData> players);
    }

    private static final int TITLE_Y = 36;
    private static final int PLAYERS_LABEL_X = 110;
    private static final int PLAYERS_LABEL_Y = 132;
    private static final int MODE_BUTTON_Y = 120;
    private static final int MODE_BUTTON_W = 160;
    private static final int MODE_BUTTON_H = 62;
    private static final int ROWS_X = 110;
    private static final int ROWS_Y = 210;
    private static final int ROWS_H = 490;
    private static final int ERROR_Y = 720;
    private static final int NAV_BUTTON_Y = 765;
    private static final int NAV_BUTTON_W = 280;
    private static final int NAV_BUTTON_H = 64;

    private static final int TEXT_SPACING = -28;
    private static final int TEXT_WORD_SPACING = 24;
    private static final int TITLE_WORD_SPACING = 36;
    private static final Color LABEL_COLOR = new Color(210, 235, 255);

    private static final int MAX_NAME_LENGTH = 12;

    private static final Map<String, String> OPPOSITE_COLOR = Map.of(
            "YELLOW", "RED",
            "RED", "YELLOW",
            "GREEN", "BLUE",
            "BLUE", "GREEN"
    );

    private final MenuView parentMenu;
    private final OnStartGameListener startListener;
    private final List<PlayerRow> rows = new ArrayList<>();

    private JFrame frame;
    private JPanel setupPanel;
    private JPanel rowsContainer;
    private PixelButton btn2Players;
    private PixelButton btn4Players;

    private int playerCount = 4;
    private String errorMessage = "";

    public GameSetupView(MenuView parentMenu, OnStartGameListener startListener) {
        this.parentMenu = parentMenu;
        this.startListener = startListener;
    }

    public void displayWindow() {
        frame = Theme.createFrame("LUDO Chess - Setup");
        setupPanel = createSetupPanel();

        addModeButtons();
        addRowsContainer();
        addNavigationButtons();

        rebuildPlayerRows();
        Theme.showFrame(frame, setupPanel);
    }

    private JPanel createSetupPanel() {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                PixelFont font = PixelFont.getInstance();

                g2d.setColor(Theme.BACKGROUND);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                font.drawCenteredText(g2d, "game setup", getWidth() / 2, TITLE_Y,
                        3, TEXT_SPACING, TITLE_WORD_SPACING, Color.WHITE);
                font.drawText(g2d, "players:", PLAYERS_LABEL_X, PLAYERS_LABEL_Y,
                        2, TEXT_SPACING, TEXT_WORD_SPACING, LABEL_COLOR);

                if (!errorMessage.isEmpty()) {
                    font.drawCenteredText(g2d, errorMessage, getWidth() / 2, ERROR_Y,
                            2, TEXT_SPACING, TEXT_WORD_SPACING, Theme.ERROR);
                }
                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(Theme.WIN_W, Theme.WIN_H));
        return panel;
    }

    private void addModeButtons() {
        btn2Players = new PixelButton("2p", 400, MODE_BUTTON_Y, MODE_BUTTON_W, MODE_BUTTON_H);
        btn4Players = new PixelButton("4p", 600, MODE_BUTTON_Y, MODE_BUTTON_W, MODE_BUTTON_H);

        btn2Players.addActionListener(e -> setPlayerCount(2));
        btn4Players.addActionListener(e -> setPlayerCount(4));

        setupPanel.add(btn2Players);
        setupPanel.add(btn4Players);
    }

    private void addRowsContainer() {
        rowsContainer = new JPanel(null);
        rowsContainer.setOpaque(false);
        rowsContainer.setBounds(ROWS_X, ROWS_Y, Theme.WIN_W - 2 * ROWS_X, ROWS_H);
        setupPanel.add(rowsContainer);
    }

    private void addNavigationButtons() {
        PixelButton backButton = new PixelButton("back", 260, NAV_BUTTON_Y, NAV_BUTTON_W, NAV_BUTTON_H);
        PixelButton startButton = new PixelButton("start", 740, NAV_BUTTON_Y, NAV_BUTTON_W, NAV_BUTTON_H);

        backButton.addActionListener(e -> goBack());
        startButton.addActionListener(e -> handleStartClicked());

        setupPanel.add(backButton);
        setupPanel.add(startButton);
    }

    private void setPlayerCount(int count) {
        playerCount = count;
        rebuildPlayerRows();
    }

    private void goBack() {
        frame.dispose();
        if (parentMenu != null) {
            parentMenu.displayWindow();
        }
    }

    private void showError(String message) {
        errorMessage = message;
        setupPanel.repaint();
    }

    private void rebuildPlayerRows() {
        btn2Players.setSelected(playerCount == 2);
        btn4Players.setSelected(playerCount == 4);

        rowsContainer.removeAll();
        rows.clear();
        errorMessage = "";

        List<String> colorNames = new ArrayList<>(Theme.PLAYER_COLORS.keySet());
        int gapY = (playerCount == 2) ? 36 : 16;
        int rowHeight = (rowsContainer.getHeight() - gapY * (playerCount - 1)) / playerCount;

        for (int i = 0; i < playerCount; i++) {
            String defaultColor = (playerCount == 2)
                    ? (i == 0 ? "RED" : "YELLOW")
                    : colorNames.get(i % colorNames.size());

            PlayerRow row = new PlayerRow(i, defaultColor, i * (rowHeight + gapY),
                    rowsContainer.getWidth(), rowHeight);
            rows.add(row);
            rowsContainer.add(row.panel);
        }

        refreshColorButtons();
        rowsContainer.revalidate();
        rowsContainer.repaint();
        setupPanel.repaint();
    }

    private void handleColorPicked(PlayerRow picker, String colorName) {
        picker.selectedColor = colorName;
        errorMessage = "";

        if (playerCount == 2 && rows.size() == 2) {
            PlayerRow other = rows.get(picker.index == 0 ? 1 : 0);
            other.selectedColor = OPPOSITE_COLOR.getOrDefault(colorName, other.selectedColor);
        }

        refreshColorButtons();
        setupPanel.repaint();
    }

    private void refreshColorButtons() {
        for (PlayerRow row : rows) {
            for (Map.Entry<String, ColorSwatchButton> entry : row.colorButtons.entrySet()) {
                String colorName = entry.getKey();
                ColorSwatchButton button = entry.getValue();
                button.setEnabled(!isTakenByAnotherRow(row, colorName));
                button.setSelected(colorName.equals(row.selectedColor));
                button.repaint();
            }
        }
    }

    private boolean isTakenByAnotherRow(PlayerRow row, String colorName) {
        for (PlayerRow other : rows) {
            if (other != row && colorName.equals(other.selectedColor)) return true;
        }
        return false;
    }

    private void handleStartClicked() {
        List<PlayerSetupData> players = new ArrayList<>();
        Set<String> usedColors = new HashSet<>();

        for (PlayerRow row : rows) {
            if (!usedColors.add(row.selectedColor)) {
                showError("duplicate color!");
                return;
            }

            String typedName = row.nameField.getText();
            String name = typedName.isEmpty() ? "p" + (row.index + 1) : typedName;
            players.add(new PlayerSetupData(
                    row.index + 1, name, row.selectedColor, Theme.PLAYER_COLORS.get(row.selectedColor)));
        }

        errorMessage = "";
        frame.dispose();

        if (startListener != null) {
            startListener.onStartGame(players);
        } else {
            System.out.println("Ready with " + players.size() + " players:");
            for (PlayerSetupData p : players) {
                System.out.println(" - Slot " + p.slotIndex + ": " + p.name + " (" + p.colorName + ")");
            }
        }
    }

    private class PlayerRow {
        private static final int SLOT_LABEL_X = 24;
        private static final int SLOT_LABEL_W = 95;
        private static final int NAME_FIELD_X = 135;
        private static final int NAME_FIELD_W = 480;
        private static final int COLORS_START_X = 665;
        private static final int COLOR_GAP = 20;

        final int index;
        final JPanel panel;
        final PixelTextField nameField;
        final Map<String, ColorSwatchButton> colorButtons = new LinkedHashMap<>();
        String selectedColor;

        PlayerRow(int index, String initialColor, int y, int width, int height) {
            this.index = index;
            this.selectedColor = initialColor;

            panel = new JPanel(null);
            panel.setBounds(0, y, width, height);
            panel.setBackground(new Color(12, 34, 62));
            panel.setBorder(BorderFactory.createLineBorder(new Color(65, 95, 135), 3));

            addSlotLabel(height);

            int fieldH = Math.min(64, height - 24);
            nameField = new PixelTextField(MAX_NAME_LENGTH);
            nameField.setBounds(NAME_FIELD_X, (height - fieldH) / 2, NAME_FIELD_W, fieldH);
            panel.add(nameField);

            addColorButtons(initialColor, height);
        }

        private void addSlotLabel(int height) {
            String text = "p" + (index + 1);
            JLabel label = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    PixelFont.getInstance().drawText(g2d, text, 4, Math.max(4, (getHeight() - 48) / 2),
                            2, TEXT_SPACING, TEXT_WORD_SPACING, new Color(220, 235, 255));
                    g2d.dispose();
                }
            };
            label.setBounds(SLOT_LABEL_X, 8, SLOT_LABEL_W, height - 16);
            panel.add(label);
        }

        private void addColorButtons(String initialColor, int height) {
            int size = Math.min(54, height - 28);
            int x = COLORS_START_X;

            for (Map.Entry<String, Color> entry : Theme.PLAYER_COLORS.entrySet()) {
                String colorName = entry.getKey();

                ColorSwatchButton button = new ColorSwatchButton(
                        colorName, entry.getValue(), x, (height - size) / 2, size);
                button.setSelected(colorName.equals(initialColor));
                button.addActionListener(e -> handleColorPicked(this, colorName));

                colorButtons.put(colorName, button);
                panel.add(button);
                x += size + COLOR_GAP;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameSetupView(null, null).displayWindow());
    }
}