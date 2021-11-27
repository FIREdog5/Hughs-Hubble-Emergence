package bin.resource;

import java.awt.image.BufferedImage;

public class ImageGenerator {
  //2400 x 800
  public static BufferedImage OpenSimpleNoiseTexture(long seed, double featureSize, int width, int height) {
    OpenSimplexNoise noise = new OpenSimplexNoise(seed);
    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        double value = noise.eval(i / featureSize, j / featureSize, 0.0);
        int color = 0x010101 * (int)((value + 1) * 127.5);
        newImage.setRGB(i, j, color);
      }
    }
    return newImage;
  }
}
