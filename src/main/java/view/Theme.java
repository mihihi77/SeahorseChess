package view;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Shared constants and small helpers used by every screen. */
public final class Theme {

    public static final int WIN_W = 1280;
    public static final int WIN_H = 860;

    public static final Color BACKGROUND = new Color(4, 24, 46);
    public static final Color ACCENT = Color.decode("#ffb700");
    public static final Color ERROR = Color.decode("#ff4d6d");

    public static final String LOGO_PATH = "resources/logo.png";

    /** Player colors, in the order they are shown in the setup screen. */
    public static final Map<String, Color> PLAYER_COLORS;

    static {
        Map<String, Color> colors = new LinkedHashMap<>();
        colors.put("RED", Color.decode("#ff4d6d"));
        colors.put("BLUE", Color.decode("#106198"));
        colors.put("GREEN", Color.decode("#0ca8a7"));
        colors.put("YELLOW", Color.decode("#ffb700"));
        PLAYER_COLORS = Collections.unmodifiableMap(colors);
    }

    private Theme() {
    }

    /** Creates a fixed-size frame with the game logo and "exit on close" behaviour. */
    public static JFrame createFrame(String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        ImageIcon logo = new ImageIcon(LOGO_PATH);
        if (logo.getIconWidth() > 0) {
            frame.setIconImage(logo.getImage());
        }
        return frame;
    }

    /** Puts the panel into the frame, centers the frame on screen and shows it. */
    public static void showFrame(JFrame frame, JPanel content) {
        frame.setContentPane(content);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}