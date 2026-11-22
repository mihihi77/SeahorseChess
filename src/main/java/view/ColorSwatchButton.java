package view;

import javax.swing.*;
import java.awt.*;

/**
 * A square button filled with a player color.
 * Selected = white border. Disabled = the color is already taken by another player (drawn dimmed).
 */
public class ColorSwatchButton extends JButton {

    private static final double DIM_FACTOR = 0.35;
    private static final int DIM_MIN = 25;

    private final Color color;

    public ColorSwatchButton(String colorName, Color color, int x, int y, int size) {
        this.color = color;
        setBounds(x, y, size, size);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText(colorName);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setColor(isEnabled() ? color : dimmed(color));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (isSelected()) {
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);
        } else {
            g2d.setColor(new Color(15, 20, 30));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
        }
        g2d.dispose();
    }

    private static Color dimmed(Color c) {
        return new Color(
                Math.max(DIM_MIN, (int) (c.getRed() * DIM_FACTOR)),
                Math.max(DIM_MIN, (int) (c.getGreen() * DIM_FACTOR)),
                Math.max(DIM_MIN, (int) (c.getBlue() * DIM_FACTOR))
        );
    }
}