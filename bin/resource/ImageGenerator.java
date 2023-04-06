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

  public static BufferedImage SphericalOpenSimplexNoise(long seed, double featureSize, int width) {
    double radius = width / (2f * Math.PI) / featureSize;
    double dTheta = (2f * Math.PI) / width;
    int height = (int) (2 * radius * featureSize);
    double dPhi = (Math.PI) / height;
    OpenSimplexNoise noise = new OpenSimplexNoise(seed);
    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    int i = 0;
    for (double theta = 0; i < width; theta += dTheta) {
      int j = 0;
      for (double phi = 0; j < height; phi += dPhi) {
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);
        double value = noise.eval(x, y, z);
        int color = 0x010101 * (int)((value + 1) * 127.5);
        newImage.setRGB(i, j, color);
        j++;
      }
      i++;
    }
    return newImage;
  }

  public static BufferedImage FNLTexture(FastNoiseLite fnl, int width, int height) {
    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        double value = fnl.GetNoise(i, j, 0f);
        int color = 0x010101 * (int)((value + 1) * 127.5);
        newImage.setRGB(i, j, color);
      }
    }
    return newImage;
  }

  public static BufferedImage SphericalFNL(FastNoiseLite fnl, int width) {
    double radius = width / (2f * Math.PI);
    double dTheta = (2f * Math.PI) / width;
    int height = (int) (2 * radius);
    double dPhi = (Math.PI) / height;
    BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    int i = 0;
    for (double theta = 0; i < width; theta += dTheta) {
      int j = 0;
      for (double phi = 0; j < height; phi += dPhi) {
        double x = radius * Math.sin(phi) * Math.cos(theta);
        double y = radius * Math.sin(phi) * Math.sin(theta);
        double z = radius * Math.cos(phi);
        double value = fnl.GetNoise((float) x, (float) y, (float) z);
        int color = 0x010101 * (int)((value + 1) * 127.5);
        newImage.setRGB(i, j, color);
        j++;
      }
      i++;
    }
    return newImage;
  }

}
