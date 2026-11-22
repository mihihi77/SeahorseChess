package view;

import javax.swing.*;
import java.awt.*;

/** A flat pixel-style button with a centered pixel-font label. Use setSelected() to highlight it. */
public class PixelButton extends JButton {

    private static final Color FILL_DISABLED = new Color(18, 24, 38);
    private static final Color FILL_SELECTED = new Color(38, 66, 112);
    private static final Color FILL_NORMAL = new Color(22, 36, 64);
    private static final Color BORDER_NORMAL = new Color(130, 160, 210);

    private final String label;

    public PixelButton(String label, int x, int y, int w, int h) {
        this.label = label;
        setBounds(x, y, w, h);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        boolean selected = isSelected();

        g2d.setColor(!isEnabled() ? FILL_DISABLED : selected ? FILL_SELECTED : FILL_NORMAL);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        boolean highlighted = getModel().isRollover() || selected;
        g2d.setColor(highlighted ? Theme.ACCENT : BORDER_NORMAL);
        g2d.setStroke(new BasicStroke(selected ? 4 : 3));
        g2d.drawRect(2, 2, getWidth() - 4, getHeight() - 4);

        PixelFont.getInstance().drawCenteredText(
                g2d, label,
                getWidth() / 2,
                Math.max(4, (getHeight() - 48) / 2),
                2, -28, 24,
                Color.WHITE
        );
        g2d.dispose();
    }
}