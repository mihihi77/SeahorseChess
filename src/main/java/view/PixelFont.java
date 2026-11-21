package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class PixelFont {
    private static PixelFont instance; // Dùng chung 1 instance cho toàn app
    private final Map<Character, BufferedImage> glyphMap = new HashMap<>();
    private final int cellW;
    private final int cellH;

    private static final String CHAR_ORDER =
            "abcdefgh" +
                    "ijklmnop" +
                    "qrstuvwx" +
                    "yz012345" +
                    "6789/:!-";

    public static PixelFont getInstance() {
        if (instance == null) {
            instance = new PixelFont("font.png", 32, 32);
        }
        return instance;
    }

    public PixelFont(String imagePath, int cellW, int cellH) {
        this.cellW = cellW;
        this.cellH = cellH;
        loadFontSheet(imagePath);
    }

    private void loadFontSheet(String path) {
        try {
            BufferedImage sheet = loadImage(path);
            int cols = sheet.getWidth() / cellW;
            for (int i = 0; i < CHAR_ORDER.length(); i++) {
                char c = CHAR_ORDER.charAt(i);
                int x = (i % cols) * cellW;
                int y = (i / cols) * cellH;
                if (x + cellW <= sheet.getWidth() && y + cellH <= sheet.getHeight()) {
                    glyphMap.put(c, sheet.getSubimage(x, y, cellW, cellH));
                }
            }
        } catch (IOException e) {
            System.err.println("Lỗi nạp font.png: " + e.getMessage());
        }
    }

    private BufferedImage loadImage(String fileName) throws IOException {
        URL url = getClass().getResource("/" + fileName);
        if (url != null) return ImageIO.read(url);

        File file = new File("resources/" + fileName);
        if (file.exists()) return ImageIO.read(file);

        throw new IOException("Không tìm thấy: resources/" + fileName);
    }

    private BufferedImage tintGlyph(BufferedImage src, Color color) {
        if (color == null || color.equals(Color.WHITE)) return src;
        BufferedImage tinted = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int argb = src.getRGB(x, y);
                int alpha = (argb >> 24) & 0xff;
                if (alpha > 0) {
                    int newPixel = (alpha << 24) | (color.getRed() << 16) | (color.getGreen() << 8) | color.getBlue();
                    tinted.setRGB(x, y, newPixel);
                }
            }
        }
        return tinted;
    }

    public int getTextWidth(String text, int scale, int spacing) {
        return getTextWidth(text, scale, spacing, (cellW / 2) * scale + spacing);
    }

    public int getTextWidth(String text, int scale, int spacing, int wordSpacing) {
        if (text == null || text.isEmpty()) return 0;
        int total = 0;
        for (char c : text.toLowerCase().toCharArray()) {
            if (c == ' ') {
                total += wordSpacing;
            } else {
                total += (cellW * scale) + spacing;
            }
        }
        return Math.max(0, total - spacing);
    }

    public void drawText(Graphics2D g2d, String text, int x, int y, int scale, int spacing, Color color) {
        drawText(g2d, text, x, y, scale, spacing, (cellW / 2) * scale + spacing, color);
    }

    public void drawText(Graphics2D g2d, String text, int x, int y, int scale, int spacing, int wordSpacing, Color color) {
        if (text == null) return;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int cursorX = x;
        for (char c : text.toLowerCase().toCharArray()) {
            if (c == ' ') {
                cursorX += wordSpacing;
                continue;
            }
            BufferedImage glyph = glyphMap.get(c);
            if (glyph != null) {
                BufferedImage coloredGlyph = tintGlyph(glyph, color);
                g2d.drawImage(coloredGlyph, cursorX, y, cellW * scale, cellH * scale, null);
                cursorX += (cellW * scale) + spacing;
            }
        }
    }

    public void drawCenteredText(Graphics2D g2d, String text, int centerX, int y, int scale, int spacing, Color color) {
        int width = getTextWidth(text, scale, spacing);
        drawText(g2d, text, centerX - (width / 2), y, scale, spacing, color);
    }

    public void drawCenteredText(Graphics2D g2d, String text, int centerX, int y, int scale, int spacing, int wordSpacing, Color color) {
        int width = getTextWidth(text, scale, spacing, wordSpacing);
        drawText(g2d, text, centerX - (width / 2), y, scale, spacing, wordSpacing, color);
    }
}