package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

/** A single-line text field that renders its text with the pixel font. */
public class PixelTextField extends JPanel {

    private static final int DEFAULT_W = 480;
    private static final int DEFAULT_H = 64;
    private static final int BLINK_MS = 500;
    private static final int PADDING_X = 16;
    private static final int BORDER_INSET = 2;
    private static final int CURSOR_GAP = 4;

    private static final Color FIELD_BG = new Color(20, 28, 48);
    private static final Color BORDER_IDLE = new Color(90, 110, 150);
    private static final String PLACEHOLDER = "enter name...";

    private final StringBuilder text = new StringBuilder();
    private final int maxLength;
    private final Timer blinkTimer;

    private boolean focused = false;
    private boolean showCursor = true;

    public PixelTextField(int maxLength) {
        this.maxLength = maxLength;
        setPreferredSize(new Dimension(DEFAULT_W, DEFAULT_H));
        setBackground(FIELD_BG);
        setFocusable(true);

        blinkTimer = new Timer(BLINK_MS, e -> {
            if (focused) {
                showCursor = !showCursor;
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                focused = true;
                showCursor = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                focused = false;
                showCursor = false;
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleTyped(Character.toLowerCase(e.getKeyChar()));
            }
        });
    }

    // The blink timer only runs while the field is on screen, so removed fields don't leak timers.
    @Override
    public void addNotify() {
        super.addNotify();
        blinkTimer.start();
    }

    @Override
    public void removeNotify() {
        blinkTimer.stop();
        super.removeNotify();
    }

    public String getText() {
        return text.toString().trim();
    }

    private void handleTyped(char c) {
        if (c == '\b') {
            if (!text.isEmpty()) text.deleteCharAt(text.length() - 1);
        } else if (text.length() < maxLength && isAllowed(c)) {
            text.append(c);
        }
        repaint();
    }

    /** Only characters the pixel font can actually draw are accepted. */
    private static boolean isAllowed(char c) {
        if (c == ' ') return true;
        boolean validShape = Character.isLetterOrDigit(c) || c == '-' || c == '_';
        return validShape && PixelFont.getInstance().supports(c);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(focused ? Theme.ACCENT : BORDER_IDLE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(BORDER_INSET, BORDER_INSET, getWidth() - 2 * BORDER_INSET, getHeight() - 2 * BORDER_INSET);

        boolean showPlaceholder = text.isEmpty() && !focused;
        String display = showPlaceholder ? PLACEHOLDER : text.toString();
        Color textColor = showPlaceholder ? Color.GRAY : Color.WHITE;

        PixelFont font = PixelFont.getInstance();
        int scale = (getHeight() >= 56) ? 2 : 1;
        int spacing = (scale == 2) ? -25 : -14;
        int textY = Math.max(4, (getHeight() - 32 * scale) / 2);

        font.drawText(g2d, display, PADDING_X, textY, scale, spacing, textColor);

        if (focused && showCursor) {
            int cursorX = PADDING_X + font.getTextWidth(text.toString(), scale, spacing) + CURSOR_GAP;
            g2d.setColor(Color.WHITE);
            g2d.fillRect(cursorX, textY + 6, 4, 24 * scale);
        }
    }
}