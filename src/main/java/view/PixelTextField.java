package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PixelTextField extends JPanel {
    private StringBuilder text = new StringBuilder();
    private final int maxLength;
    private boolean focused = false;
    private boolean showCursor = true;

    public PixelTextField(int maxLength) {
        this.maxLength = maxLength;
        setPreferredSize(new Dimension(280, 48));
        setBackground(new Color(20, 28, 48)); // Màu nền tối đồng bộ Menu
        setFocusable(true);

        // Con trỏ nhấp nháy mỗi 0.5s
        new Timer(500, e -> {
            if (focused) {
                showCursor = !showCursor;
                repaint();
            }
        }).start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { focused = true; repaint(); }
            @Override
            public void focusLost(FocusEvent e) { focused = false; showCursor = false; repaint(); }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = Character.toLowerCase(e.getKeyChar());
                if (c == '\b' && text.length() > 0) {
                    text.deleteCharAt(text.length() - 1);
                } else if (text.length() < maxLength && (Character.isLetterOrDigit(c) || c == ' ' || c == '-' || c == '_')) {
                    text.append(c);
                }
                repaint();
            }
        });
    }

    public String getText() { return text.toString().trim(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Vẽ viền pixel
        g2d.setColor(focused ? new Color(255, 215, 0) : new Color(90, 110, 150));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);

        // Hiển thị placeholder mờ nếu chưa nhập gì
        String display = text.isEmpty() && !focused ? "enter name..." : text.toString();
        Color textColor = text.isEmpty() && !focused ? Color.GRAY : Color.WHITE;

        PixelFont font = PixelFont.getInstance();
        font.drawText(g2d, display, 12, 8, 1, -10, textColor);

        if (focused && showCursor) {
            int cursorX = 12 + font.getTextWidth(text.toString(), 1, -10) + 2;
            g2d.setColor(Color.WHITE);
            g2d.fillRect(cursorX, 12, 3, 22);
        }
    }
}