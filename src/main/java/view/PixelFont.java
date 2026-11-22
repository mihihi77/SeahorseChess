package view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Draws text using the glyphs from the pixel font sheet (resources/font.png). */
public class PixelFont {

    private static final String SHEET_NAME = "font.png";
    private static final int CELL_SIZE = 32;
    private static final int SHEET_COLS = 9;

    /**
     * Characters in the exact order they appear in font.png (left to right, top to bottom).
     * Every row below matches one 9-column row of the sheet.
     */
    private static final String CHAR_ORDER =
            "abcdefghi" +
                    "jklmnopqr" +
                    "stuvwxyz0" +
                    "123456789" +
                    ".,!?";

    private static PixelFont instance;

    private final int cellW;
    private final int cellH;
    private final Map<Character, BufferedImage> glyphs = new HashMap<>();
    // color (as ARGB int) -> glyph char -> tinted image, so each glyph is tinted only once
    private final Map<Integer, Map<Character, BufferedImage>> tintCache = new HashMap<>();

    /** One shared instance for the whole app. */
    public static synchronized PixelFont getInstance() {
        if (instance == null) {
            instance = new PixelFont(SHEET_NAME, CELL_SIZE, CELL_SIZE);
        }
        return instance;
    }

    public PixelFont(String imagePath, int cellW, int cellH) {
        this.cellW = cellW;
        this.cellH = cellH;
        loadFontSheet(imagePath);
    }

    /** True if the sheet has a glyph for this character (case-insensitive). */
    public boolean supports(char c) {
        return glyphs.containsKey(Character.toLowerCase(c));
    }

    // ---------------------------------------------------------------- loading

    private void loadFontSheet(String path) {
        try {
            BufferedImage sheet = loadImage(path);
            for (int i = 0; i < CHAR_ORDER.length(); i++) {
                int x = (i % SHEET_COLS) * cellW;
                int y = (i / SHEET_COLS) * cellH;
                if (x + cellW <= sheet.getWidth() && y + cellH <= sheet.getHeight()) {
                    glyphs.put(CHAR_ORDER.charAt(i), sheet.getSubimage(x, y, cellW, cellH));
                }
            }
        } catch (IOException e) {
            System.err.println("Cannot load " + path + ": " + e.getMessage());
        }
    }

    private BufferedImage loadImage(String fileName) throws IOException {
        URL url = getClass().getResource("/" + fileName);
        if (url != null) return ImageIO.read(url);

        File file = new File("resources/" + fileName);
        if (file.exists()) return ImageIO.read(file);

        throw new IOException("Not found: resources/" + fileName);
    }

    // ---------------------------------------------------------------- glyphs

    /** Returns the glyph for c in the given color, or null if the font does not have it. */
    private BufferedImage glyphFor(char c, Color color) {
        BufferedImage base = glyphs.get(c);
        if (base == null || color == null || Color.WHITE.equals(color)) return base;

        return tintCache
                .computeIfAbsent(color.getRGB(), key -> new HashMap<>())
                .computeIfAbsent(c, key -> tint(base, color));
    }

    private static BufferedImage tint(BufferedImage src, Color color) {
        BufferedImage tinted = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int rgb = color.getRGB() & 0x00FFFFFF;
        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int alpha = (src.getRGB(x, y) >> 24) & 0xff;
                if (alpha > 0) {
                    tinted.setRGB(x, y, (alpha << 24) | rgb);
                }
            }
        }
        return tinted;
    }

    // ---------------------------------------------------------------- measuring

    private int defaultWordSpacing(int scale, int spacing) {
        return (cellW / 2) * scale + spacing;
    }

    /** How far the cursor moves after c. Unknown characters are skipped, in both measuring and drawing. */
    private int advance(char c, int scale, int spacing, int wordSpacing) {
        if (c == ' ') return wordSpacing;
        if (!glyphs.containsKey(c)) return 0;
        return cellW * scale + spacing;
    }

    public int getTextWidth(String text, int scale, int spacing) {
        return getTextWidth(text, scale, spacing, defaultWordSpacing(scale, spacing));
    }

    public int getTextWidth(String text, int scale, int spacing, int wordSpacing) {
        if (text == null || text.isEmpty()) return 0;

        int total = 0;
        boolean lastWasGlyph = false;
        for (char c : text.toLowerCase(Locale.ROOT).toCharArray()) {
            int step = advance(c, scale, spacing, wordSpacing);
            total += step;
            if (c != ' ' && step != 0) lastWasGlyph = true;
            else if (c == ' ') lastWasGlyph = false;
        }
        // the spacing after the last glyph is not part of the visible text
        return Math.max(0, lastWasGlyph ? total - spacing : total);
    }

    // ---------------------------------------------------------------- drawing

    public void drawText(Graphics2D g2d, String text, int x, int y, int scale, int spacing, Color color) {
        drawText(g2d, text, x, y, scale, spacing, defaultWordSpacing(scale, spacing), color);
    }

    public void drawText(Graphics2D g2d, String text, int x, int y,
                         int scale, int spacing, int wordSpacing, Color color) {
        if (text == null) return;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int cursorX = x;
        for (char c : text.toLowerCase(Locale.ROOT).toCharArray()) {
            BufferedImage glyph = glyphFor(c, color);
            if (glyph != null) {
                g2d.drawImage(glyph, cursorX, y, cellW * scale, cellH * scale, null);
            }
            cursorX += advance(c, scale, spacing, wordSpacing);
        }
    }

    public void drawCenteredText(Graphics2D g2d, String text, int centerX, int y,
                                 int scale, int spacing, Color color) {
        drawCenteredText(g2d, text, centerX, y, scale, spacing, defaultWordSpacing(scale, spacing), color);
    }

    public void drawCenteredText(Graphics2D g2d, String text, int centerX, int y,
                                 int scale, int spacing, int wordSpacing, Color color) {
        int width = getTextWidth(text, scale, spacing, wordSpacing);
        drawText(g2d, text, centerX - width / 2, y, scale, spacing, wordSpacing, color);
    }
}