package pixelizer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Pixelizer {
    private static int width;
    private static int height;
    private static BufferedImage image;

    public static void getAveragePixel(String path, boolean replace, JProgressBar progressBar) throws IOException {
        getImage(path);

        List<Integer> red = new ArrayList<>();
        List<Integer> green = new ArrayList<>();
        List<Integer> blue = new ArrayList<>();

        progressBar.setMaximum(width * height);
        progressBar.setValue(0);
        int progress = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixel = image.getRGB(col, row);
                int redPixel = (pixel & 0x00ff0000) >> 16;
                red.add(redPixel);
                int greenPixel = (pixel & 0x0000ff00) >> 8;
                green.add(greenPixel);
                int bluePixel = pixel & 0x000000ff;
                blue.add(bluePixel);
                progressBar.setValue(++progress);
            }
        }
        int averageRed = findAverage(red);
        int averageGreen = findAverage(green);
        int averageBlue = findAverage(blue);
        int p = (255 << 24) | (averageRed << 16) | (averageGreen << 8) | averageBlue;
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, p);

        if (!replace) {
            StringBuilder builder = new StringBuilder(path);
            builder.insert(path.length() - 4, "_pixel");
            path = builder.toString();
        }

        File f = new File(path);
        ImageIO.write(img, "PNG", f);
    }

    public static void pixelSort(String path, boolean replace, JProgressBar progressBar) throws IOException {
        getImage(path);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        List<Integer> pixels = new ArrayList<>();

        progressBar.setMaximum((width * height) + 100);
        progressBar.setValue(0);
        int progress = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pixels.add(image.getRGB(col, row));
                progressBar.setValue(++progress);
            }
        }

        pixels.sort(Integer::compareTo);

        int[] pixelArray = pixels.stream().mapToInt(i -> i).toArray();

        final int[] a = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelArray, 0, a, 0, pixelArray.length);


        if (!replace) {
            StringBuilder builder = new StringBuilder(path);
            builder.insert(path.length() - 4, "_pixel");
            path = builder.toString();
        }
        File f = new File(path);
        ImageIO.write(result, "PNG", f);

        progress += 100;
        progressBar.setValue(progress);
    }

    public static void pixelRandomizer(String path, boolean replace, JProgressBar progressBar) throws IOException {
        getImage(path);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        List<Integer> pixels = new ArrayList<>();

        progressBar.setMaximum((width * height) + 100);
        progressBar.setValue(0);
        int progress = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pixels.add(image.getRGB(col, row));
                progressBar.setValue(++progress);
            }
        }

        Collections.shuffle(pixels, new Random());

        int[] pixelArray = pixels.stream().mapToInt(i -> i).toArray();

        final int[] a = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixelArray, 0, a, 0, pixelArray.length);


        if (!replace) {
            StringBuilder builder = new StringBuilder(path);
            builder.insert(path.length() - 4, "_pixel");
            path = builder.toString();
        }
        File f = new File(path);
        ImageIO.write(result, "PNG", f);

        progress += 100;
        progressBar.setValue(progress);
    }

    private static void getImage(String path) throws IOException {
        image = ImageIO.read(new File(path));
        width = image.getWidth();
        height = image.getHeight();
    }

    private
    static int findAverage(List<Integer> pixels) {
        int total = 0;
        for (Integer i : pixels) {
            total += i;
        }
        total = total / pixels.size();
        return total;
    }
}
