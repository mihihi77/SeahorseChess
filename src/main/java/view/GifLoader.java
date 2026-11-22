package view;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Decodes an animated GIF into fully composed frames (disposal methods applied),
 * so each frame can simply be drawn as a normal image.
 */
public final class GifLoader {

    private static final int DEFAULT_DELAY_MS = 100;
    private static final int MIN_DELAY_MS = 20;

    /** The decoded frames and how long each one should stay on screen. */
    public static final class Animation {
        public final List<BufferedImage> frames = new ArrayList<>();
        public final List<Integer> delaysMs = new ArrayList<>();

        public boolean isEmpty() {
            return frames.isEmpty();
        }
    }

    private GifLoader() {
    }

    /** Never returns null. On any problem it prints an error and returns an empty animation. */
    public static Animation load(String path) {
        Animation animation = new Animation();

        File file = new File(path);
        if (!file.exists()) {
            System.err.println("Cannot find GIF: " + path);
            return animation;
        }

        try (ImageInputStream stream = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("gif");
            if (!readers.hasNext()) return animation;

            ImageReader reader = readers.next();
            try {
                reader.setInput(stream, false);
                decodeFrames(reader, animation);
            } finally {
                reader.dispose();
            }
        } catch (IOException ex) {
            System.err.println("Failed to read GIF: " + ex.getMessage());
        }
        return animation;
    }

    private static void decodeFrames(ImageReader reader, Animation out) throws IOException {
        int count = reader.getNumImages(true);
        if (count <= 0) return;

        Dimension size = readLogicalScreenSize(reader);
        BufferedImage canvas = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        BufferedImage previousCanvas = copy(canvas);

        for (int i = 0; i < count; i++) {
            IIOMetadataNode root = frameMetadataRoot(reader, i);
            IIOMetadataNode gce = findChild(root, "GraphicControlExtension");
            IIOMetadataNode imgDesc = findChild(root, "ImageDescriptor");

            String disposal = (gce != null) ? gce.getAttribute("disposalMethod") : "none";
            int delayMs = delayFromHundredths(intAttr(gce, "delayTime", 0));
            int dx = intAttr(imgDesc, "imageLeftPosition", 0);
            int dy = intAttr(imgDesc, "imageTopPosition", 0);

            if ("restoreToPrevious".equalsIgnoreCase(disposal)) {
                previousCanvas = copy(canvas);
            }

            BufferedImage rawFrame = reader.read(i);
            Graphics2D g = canvas.createGraphics();
            g.drawImage(rawFrame, dx, dy, null);
            g.dispose();

            out.frames.add(copy(canvas));
            out.delaysMs.add(delayMs);

            // prepare the canvas for the next frame
            if ("restoreToBackgroundColor".equalsIgnoreCase(disposal)) {
                Graphics2D clear = canvas.createGraphics();
                clear.setComposite(AlphaComposite.Clear);
                clear.fillRect(dx, dy, rawFrame.getWidth(), rawFrame.getHeight());
                clear.dispose();
            } else if ("restoreToPrevious".equalsIgnoreCase(disposal)) {
                canvas = copy(previousCanvas);
            }
        }
    }

    private static int delayFromHundredths(int hundredths) {
        int delayMs = (hundredths > 0) ? hundredths * 10 : DEFAULT_DELAY_MS;
        return Math.max(MIN_DELAY_MS, delayMs);
    }

    private static Dimension readLogicalScreenSize(ImageReader reader) throws IOException {
        int width = reader.getWidth(0);
        int height = reader.getHeight(0);

        IIOMetadata streamMeta = reader.getStreamMetadata();
        if (streamMeta != null) {
            IIOMetadataNode root = (IIOMetadataNode) streamMeta.getAsTree(streamMeta.getNativeMetadataFormatName());
            IIOMetadataNode lsd = findChild(root, "LogicalScreenDescriptor");
            width = intAttr(lsd, "logicalScreenWidth", width);
            height = intAttr(lsd, "logicalScreenHeight", height);
        }
        return new Dimension(Math.max(1, width), Math.max(1, height));
    }

    private static IIOMetadataNode frameMetadataRoot(ImageReader reader, int index) throws IOException {
        IIOMetadata meta = reader.getImageMetadata(index);
        return (IIOMetadataNode) meta.getAsTree(meta.getNativeMetadataFormatName());
    }

    private static IIOMetadataNode findChild(IIOMetadataNode parent, String name) {
        for (int i = 0; i < parent.getLength(); i++) {
            if (parent.item(i).getNodeName().equalsIgnoreCase(name)) {
                return (IIOMetadataNode) parent.item(i);
            }
        }
        return null;
    }

    private static int intAttr(IIOMetadataNode node, String name, int fallback) {
        if (node == null) return fallback;
        String value = node.getAttribute(name);
        return (value == null || value.isEmpty()) ? fallback : Integer.parseInt(value);
    }

    private static BufferedImage copy(BufferedImage src) {
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copy.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return copy;
    }
}