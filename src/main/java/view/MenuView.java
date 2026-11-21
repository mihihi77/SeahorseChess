package view;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MenuView {

    private static final int WIN_W = 700;
    private static final int WIN_H = 700;
    private static final Color BG_FALLBACK_COLOR = new Color(4, 24, 46);

    private static final double BTN_SCALE = 0.85;

    private static final String BG_GIF_PATH = "resources/menu/mainview.gif";
    private static final String PLAY_BTN_PATH = "resources/menu/PLAY.png";
    private static final String SETTING_BTN_PATH = "resources/menu/SETTING.png";
    private static final String RANK_BTN_PATH = "resources/menu/RANKING.png";

    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel buttonPanel;

    private final List<BufferedImage> gifFrames = new ArrayList<>();
    private final List<Integer> frameDelaysMs = new ArrayList<>();
    private int gifWidth = 640;
    private int gifHeight = 430;
    private int currentFrameIdx = 0;
    private Timer gifTimer;

    private final String fullWelcomeText = "welcome to";
    private int visibleCharCount = 0;
    private Timer welcomeTimer;

    public MenuView() {
        loadGifFrames(BG_GIF_PATH);

        welcomeTimer = new Timer(120, e -> {
            if (visibleCharCount < fullWelcomeText.length()) {
                visibleCharCount++;
                if (mainPanel != null) mainPanel.repaint();
            } else {
                welcomeTimer.stop();
            }
        });

        if (!gifFrames.isEmpty()) {
            int firstDelay = frameDelaysMs.isEmpty() ? 100 : frameDelaysMs.get(0);
            gifTimer = new Timer(firstDelay, e -> {
                currentFrameIdx = (currentFrameIdx + 1) % gifFrames.size();
                int nextDelay = frameDelaysMs.isEmpty() ? 100 : frameDelaysMs.get(currentFrameIdx);
                gifTimer.setDelay(nextDelay);
                if (mainPanel != null) mainPanel.repaint();
            });
        }
    }

    private Rectangle getGifBounds(int panelW, int panelH) {
        int drawW = gifFrames.isEmpty() ? gifWidth : gifFrames.get(currentFrameIdx).getWidth();
        int drawH = gifFrames.isEmpty() ? gifHeight : gifFrames.get(currentFrameIdx).getHeight();
        int drawX = (panelW - drawW) / 2;
        int drawY = (panelH - drawH) / 2;
        return new Rectangle(drawX, drawY, drawW, drawH);
    }

    private void layoutButtonsWithinGif(Rectangle gifRect) {
        if (buttonPanel == null) return;

        Dimension btnSize = buttonPanel.getPreferredSize();
        buttonPanel.setSize(btnSize);

        int btnX = gifRect.x + (gifRect.width - btnSize.width) / 2;
        int preferredY = gifRect.y + (int) (gifRect.height * 0.53);
        int minY = gifRect.y + 120;
        int maxY = gifRect.y + gifRect.height - btnSize.height - 8;
        int btnY = Math.max(minY, Math.min(preferredY, maxY));

        buttonPanel.setLocation(btnX, btnY);
    }

    private void loadGifFrames(String path) {
        gifFrames.clear();
        frameDelaysMs.clear();

        File file = new File(path);
        if (!file.exists()) {
            System.err.println("Không tìm thấy GIF: " + path);
            return;
        }

        try (ImageInputStream stream = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) return;

            ImageReader reader = readers.next();
            reader.setInput(stream, false);

            int count = reader.getNumImages(true);
            if (count <= 0) return;

            IIOMetadata streamMeta = reader.getStreamMetadata();
            int logicalW = reader.getWidth(0);
            int logicalH = reader.getHeight(0);
            if (streamMeta != null) {
                String streamFmt = streamMeta.getNativeMetadataFormatName();
                IIOMetadataNode streamRoot = (IIOMetadataNode) streamMeta.getAsTree(streamFmt);
                IIOMetadataNode lsd = getNode(streamRoot, "LogicalScreenDescriptor");
                if (lsd != null) {
                    String wAttr = lsd.getAttribute("logicalScreenWidth");
                    String hAttr = lsd.getAttribute("logicalScreenHeight");
                    if (wAttr != null && !wAttr.isEmpty()) logicalW = Integer.parseInt(wAttr);
                    if (hAttr != null && !hAttr.isEmpty()) logicalH = Integer.parseInt(hAttr);
                }
            }

            gifWidth = Math.max(1, logicalW);
            gifHeight = Math.max(1, logicalH);

            BufferedImage canvas = new BufferedImage(gifWidth, gifHeight, BufferedImage.TYPE_INT_ARGB);
            BufferedImage previousCanvas = copyImage(canvas);

            for (int i = 0; i < count; i++) {
                IIOMetadata frameMeta = reader.getImageMetadata(i);
                IIOMetadataNode root = (IIOMetadataNode) frameMeta.getAsTree(frameMeta.getNativeMetadataFormatName());
                IIOMetadataNode gce = getNode(root, "GraphicControlExtension");
                IIOMetadataNode imgDesc = getNode(root, "ImageDescriptor");

                String disposal = "none";
                int delayMs = 100;
                if (gce != null) {
                    disposal = gce.getAttribute("disposalMethod");
                    String delayStr = gce.getAttribute("delayTime");
                    if (delayStr != null && !delayStr.isEmpty()) {
                        int hundredths = Integer.parseInt(delayStr);
                        if (hundredths > 0) delayMs = hundredths * 10;
                    }
                }

                int dx = 0;
                int dy = 0;
                if (imgDesc != null) {
                    String xAttr = imgDesc.getAttribute("imageLeftPosition");
                    String yAttr = imgDesc.getAttribute("imageTopPosition");
                    if (xAttr != null && !xAttr.isEmpty()) dx = Integer.parseInt(xAttr);
                    if (yAttr != null && !yAttr.isEmpty()) dy = Integer.parseInt(yAttr);
                }

                if ("restoreToPrevious".equalsIgnoreCase(disposal)) {
                    previousCanvas = copyImage(canvas);
                }

                BufferedImage rawFrame = reader.read(i);
                Graphics2D gCanvas = canvas.createGraphics();
                gCanvas.drawImage(rawFrame, dx, dy, null);
                gCanvas.dispose();

                gifFrames.add(copyImage(canvas));
                frameDelaysMs.add(Math.max(20, delayMs));

                if ("restoreToBackgroundColor".equalsIgnoreCase(disposal)) {
                    Graphics2D gClear = canvas.createGraphics();
                    gClear.setComposite(AlphaComposite.Clear);
                    gClear.fillRect(dx, dy, rawFrame.getWidth(), rawFrame.getHeight());
                    gClear.dispose();
                } else if ("restoreToPrevious".equalsIgnoreCase(disposal)) {
                    canvas = copyImage(previousCanvas);
                }
            }

            reader.dispose();
        } catch (IOException ex) {
            System.err.println("Lỗi đọc GIF: " + ex.getMessage());
        }
    }

    private BufferedImage copyImage(BufferedImage src) {
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return copy;
    }

    private IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        return null;
    }

    public void displayWindow() {
        mainFrame = new JFrame("LUDO Chess");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        ImageIcon logo = new ImageIcon("resources/logo.png");
        if (logo.getIconWidth() > 0) {
            mainFrame.setIconImage(logo.getImage());
        }

        mainPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(
                        RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
                );

                g2d.setColor(BG_FALLBACK_COLOR);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                Rectangle gifRect = getGifBounds(getWidth(), getHeight());
                if (!gifFrames.isEmpty()) {
                    BufferedImage currentFrame = gifFrames.get(currentFrameIdx);
                    g2d.drawImage(currentFrame, gifRect.x, gifRect.y, null);
                }

                String subText = fullWelcomeText.substring(0, visibleCharCount);
                int welcomeY = gifRect.y + (int) (gifRect.height * 0.09);
                int welcomeCenterX = gifRect.x + gifRect.width / 2;

                PixelFont.getInstance().drawCenteredText(
                        g2d,
                        subText,
                        welcomeCenterX,
                        welcomeY,
                        1,
                        -16,
                        20,
                        Color.WHITE
                );

                layoutButtonsWithinGif(gifRect);
                g2d.dispose();
            }
        };
        mainPanel.setPreferredSize(new Dimension(WIN_W, WIN_H));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        JButton playButton = createButton(PLAY_BTN_PATH, BTN_SCALE);
        JButton settingButton = createButton(SETTING_BTN_PATH, BTN_SCALE);
        JButton rankingButton = createButton(RANK_BTN_PATH, BTN_SCALE);

        playButton.addActionListener(e -> System.out.println("Start game"));
        settingButton.addActionListener(e -> System.out.println("Open settings"));
        rankingButton.addActionListener(e -> System.out.println("Open ranking"));

        buttonPanel.add(playButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 1)));
        buttonPanel.add(settingButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 1)));
        buttonPanel.add(rankingButton);

        layoutButtonsWithinGif(getGifBounds(WIN_W, WIN_H));
        mainPanel.add(buttonPanel);

        mainFrame.setContentPane(mainPanel);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        welcomeTimer.start();
        if (gifTimer != null) gifTimer.start();
    }

    private JButton createButton(String path, double scale) {
        ImageIcon raw = new ImageIcon(path);
        int w = Math.max(1, (int) Math.round(raw.getIconWidth() * scale));
        int h = Math.max(1, (int) Math.round(raw.getIconHeight() * scale));

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
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuView().displayWindow());
    }
}