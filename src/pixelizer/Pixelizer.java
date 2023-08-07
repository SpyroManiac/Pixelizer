package pixelizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Pixelizer {
    public static void getAveragePixel(String path, boolean replace) throws IOException {
        BufferedImage image = null;
        List<Integer> red = new ArrayList<Integer>();
        List<Integer> green = new ArrayList<Integer>();
        List<Integer> blue = new ArrayList<Integer>();


        image = ImageIO.read(new File(path));

        int width = image.getWidth();
        int height = image.getHeight();

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int pixel = image.getRGB(col, row);
                int redPixel = (pixel & 0x00ff0000) >> 16;
                red.add(redPixel);
                int greenPixel = (pixel & 0x0000ff00) >> 8;
                green.add(greenPixel);
                int bluePixel = pixel & 0x000000ff;
                blue.add(bluePixel);
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
