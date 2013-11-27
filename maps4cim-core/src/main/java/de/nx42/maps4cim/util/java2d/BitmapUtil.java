package de.nx42.maps4cim.util.java2d;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BitmapUtil {


    public static BufferedImage resize(BufferedImage img, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, img.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, 0, 0, width, height, 0, 0, img.getWidth(), img.getHeight(), null);
        g.dispose();
        return resized;
    }

    public static BufferedImage resize(BufferedImage img, double scale) {
        int width = (int) Math.round(img.getWidth() * scale);
        int height = (int) Math.round(img.getHeight() * scale);
        return resize(img, width, height);
    }

    public static BufferedImage resize(BufferedImage img, int edgeLength) {
        double scale = edgeLength / ((double) (Math.max(img.getWidth(), img.getHeight())));
        return resize(img, scale);
    }

    /**
     * Resizes the input image to a squarish new image.
     * Instead of stretching the input image, the side ratio is preserved,
     * using transparency.
     * @param img the image to resize
     * @param edgeLength the edge length of the new image, e.g. 256 will lead to
     * a 256x256px partly transparent image
     * @return the resized image
     */
    public static BufferedImage resizeAndRectify(BufferedImage img, int edgeLength) {
        // skip squaring, if image is equilateral
        if(img.getWidth() == img.getHeight()) {
            return resize(img, edgeLength);
        }

        // calculate gap
        float width = img.getWidth();
        float height = img.getHeight();
        float ratio = Math.min(width, height) / Math.max(width, height);
        float cutoff = (1 - ratio) / 2;
        int cutoffAbs = Math.round(cutoff * edgeLength);

        // apply gap (top/bottom or left/right)
        int dx1 = 0;
        int dy1 = 0;
        int dx2 = edgeLength;
        int dy2 = edgeLength;
        if(width > height) {
            dy1 = cutoffAbs;
            dy2 = edgeLength - cutoffAbs;
        } else {
            dx1 = cutoffAbs;
            dx2 = edgeLength - cutoffAbs;
        }

        // draw image with transparent parts in new buffer
        BufferedImage resized = new BufferedImage(edgeLength, edgeLength, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(img, dx1, dy1, dx2, dy2, 0, 0, img.getWidth(), img.getHeight(), null);
        g.dispose();

        return resized;
    }


    public static byte[] writeFormat(BufferedImage img, String formatName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, formatName, baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();
        return bytes;
    }

    public static byte[] writePng(BufferedImage img) throws IOException {
        return writeFormat(img, "PNG");
    }

    public static byte[] writeJpg(BufferedImage img) throws IOException {
        return writeFormat(img, "JPG");
    }


}
