package bin.resource;
import bin.Pair;

import java.awt.image.RescaleOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ImagePreprocessor {
  public static BufferedImage wrapPoles(BufferedImage originalImage) {
    // for (int i = 0; i < originalImage.getHeight(); i++) {
    //   int circleWidth = originalImage.getHeight() - (int) Math.sqrt(Math.pow(originalImage.getHeight() / 2f, 2) - Math.pow(i - originalImage.getHeight() / 2f, 2));
    //   float circleRatio = (originalImage.getWidth() + 2f * circleWidth) / (float) originalImage.getWidth();
    //   for (int j = 0; j < originalImage.getWidth(); j++) {
    //     int color = originalImage.getRGB((int)(j / circleRatio + circleWidth / 2), i);
    //     newImage.setRGB(j, i, color);
    //   }
    // }
    // return newImage;
    BufferedImage newImage = new BufferedImage(originalImage.getWidth() * 3 / 2, originalImage.getHeight(), originalImage.getType());
    for (int i = 0; i < originalImage.getWidth() * 3 / 4; i++) {
      for (int j = 0; j < originalImage.getHeight(); j++) {
        int color = originalImage.getRGB(i, j);
        newImage.setRGB(i, j, color);
        newImage.setRGB(i + originalImage.getWidth() * 3 / 4, j, color);
      }
    }

    for (int i = 0; i < originalImage.getWidth() / 4; i++) {
      float blend = 1 - Math.abs(i - originalImage.getWidth() / 8f) / (originalImage.getWidth() / 8f);
      for (int j = 0; j < originalImage.getHeight(); j++) {

        int oldColor;
        int newColor;

        if (i < originalImage.getWidth() / 8) {
          oldColor = originalImage.getRGB(i + originalImage.getWidth() * 3 / 4, j);
          newColor = newImage.getRGB(i + originalImage.getWidth() * 11 / 8, j);
          newImage.setRGB(i + originalImage.getWidth() * 11 / 8, j, mixColors(oldColor, newColor, blend));
        }

        oldColor = originalImage.getRGB(i + originalImage.getWidth() * 3 / 4, j);
        newColor = newImage.getRGB(i + originalImage.getWidth() * 5 / 8, j);
        newImage.setRGB(i + originalImage.getWidth() * 5 / 8, j, mixColors(oldColor, newColor, blend));

        if (i >= originalImage.getWidth() / 8) {
          oldColor = originalImage.getRGB(i + originalImage.getWidth() * 3 / 4, j);
          newColor = newImage.getRGB(i - originalImage.getWidth() / 8, j);
          newImage.setRGB(i - originalImage.getWidth() / 8, j, mixColors(oldColor, newColor, blend));
        }
      }
    }
    return newImage;
  }

  public static BufferedImage doubleImage(BufferedImage originalImage) {
    BufferedImage newImage = new BufferedImage(originalImage.getWidth() * 2, originalImage.getHeight(), originalImage.getType());
    for (int i = 0; i < originalImage.getWidth(); i++) {
      for (int j = 0; j < originalImage.getHeight(); j++) {
        int color = originalImage.getRGB(i, j);
        newImage.setRGB(i, j, color);
        newImage.setRGB(i + originalImage.getWidth(), j, color);
      }
    }
    return newImage;
  }

  private static int mixColors(int color1, int color2, float mixRatio) {
    int red = (int) (mixRatio * (float) ((color1 >> 16) & 255) + (1 - mixRatio) * (float) ((color2 >> 16) & 255));
    int green = (int) (mixRatio * (float) ((color1 >> 8) & 255) + (1 - mixRatio) * (float) ((color2 >> 8) & 255));
    int blue = (int) (mixRatio * (float) (color1 & 255) + (1 - mixRatio) * (float) (color2 & 255));
    return ((red & 255) << 16) + ((green & 255) << 8) + (blue & 255);
  }

  private static float colorStrength(int color) {
    int red = (color >> 16) & 255;
    int green = (color >> 8) & 255;
    int blue = color & 255;
    return (red + green + blue) / (255f * 3f);
  }

  private static int strengthToColor(float strength) {
    return 0x010101 * (int)(strength * 255);
  }

  private static int clampColor(int color) {
    return Math.min(0xffffff, Math.max(color, 0x0));
  }

  private static float clampStrength(float strength) {
    return Math.min(1.0f, Math.max(strength, 0f));
  }

  public static BufferedImage mask(BufferedImage originalImage, float threshold) {
    BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int i = 0; i < originalImage.getWidth(); i++) {
      for (int j = 0; j < originalImage.getHeight(); j++) {
        int color = originalImage.getRGB(i, j);
        if (colorStrength(color) < threshold) {
          newImage.setRGB(i, j, 0);
        } else {
          newImage.setRGB(i, j, color);
        }
      }
    }
    return newImage;
  }

  public static BufferedImage flatMask(BufferedImage originalImage, float threshold) {
    BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    for (int i = 0; i < originalImage.getWidth(); i++) {
      for (int j = 0; j < originalImage.getHeight(); j++) {
        int color = originalImage.getRGB(i, j);
        if (colorStrength(color) < threshold) {
          newImage.setRGB(i, j, 0);
        } else {
          newImage.setRGB(i, j, 0xffffffff);
        }
      }
    }
    return newImage;
  }

  public static BufferedImage combineImagesMult(BufferedImage image1, BufferedImage image2) {
    if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
      System.out.println("incompatable images");
      return null;
    }
    BufferedImage newImage = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < image1.getWidth(); i++) {
      for (int j = 0; j < image1.getHeight(); j++) {
        int color1 = image1.getRGB(i, j);
        int color2 = image2.getRGB(i, j);
        newImage.setRGB(i, j, clampColor(strengthToColor(colorStrength(color1) * colorStrength(color2))));
      }
    }
    return newImage;
  }

  public static BufferedImage combineImagesAdd(BufferedImage image1, BufferedImage image2) {
    if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
      System.out.println("incompatable images");
      return null;
    }
    BufferedImage newImage = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < image1.getWidth(); i++) {
      for (int j = 0; j < image1.getHeight(); j++) {
        int color1 = image1.getRGB(i, j);
        int color2 = image2.getRGB(i, j);
        newImage.setRGB(i, j, clampColor(strengthToColor(colorStrength(color1) + colorStrength(color2))));
      }
    }
    return newImage;
  }

  public static BufferedImage combineImagesSubtract(BufferedImage image1, BufferedImage image2) {
    if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
      System.out.println("incompatable images");
      return null;
    }
    BufferedImage newImage = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < image1.getWidth(); i++) {
      for (int j = 0; j < image1.getHeight(); j++) {
        int color1 = image1.getRGB(i, j);
        int color2 = image2.getRGB(i, j);
        newImage.setRGB(i, j, clampColor(strengthToColor(colorStrength(color1) - colorStrength(color2))));
      }
    }
    return newImage;
  }

  public static BufferedImage adjustContrast(BufferedImage image, float scale, float offset){
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    RescaleOp rescaleOp = new RescaleOp(scale, offset, null);
    rescaleOp.filter(image, newImage);
    return newImage;
  }

  public static BufferedImage invert(BufferedImage image) {
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int color1 = image.getRGB(i, j);
        newImage.setRGB(i, j, clampColor(strengthToColor(1f-colorStrength(color1))));
      }
    }
    return newImage;
  }

  public static BufferedImage normalize(BufferedImage image) {
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    float minStrength = colorStrength(image.getRGB(0, 0));
    float maxStrength = colorStrength(image.getRGB(0, 0));
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int color1 = image.getRGB(i, j);
        float currStrength = colorStrength(color1);
        if (currStrength > maxStrength) {
          maxStrength = currStrength;
        }
        if (currStrength < minStrength) {
          minStrength = currStrength;
        }
      }
    }
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int color1 = image.getRGB(i, j);
        float currStrength = colorStrength(color1);
        float adjustedStrength = (currStrength - minStrength) / (maxStrength - minStrength);
        newImage.setRGB(i, j, clampColor(strengthToColor(adjustedStrength)));
      }
    }
    return newImage;
  }

  public static BufferedImage normalizeWithBounds(BufferedImage image, float minValue, float maxValue) {
    if (minValue < 0d || minValue > 255d || maxValue < 0d || maxValue > 255d || minValue > maxValue) {
      return image;
    }
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    float minStrength = colorStrength(image.getRGB(0, 0));
    float maxStrength = colorStrength(image.getRGB(0, 0));
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int color1 = image.getRGB(i, j);
        float currStrength = colorStrength(color1);
        if (currStrength > maxStrength) {
          maxStrength = currStrength;
        }
        if (currStrength < minStrength) {
          minStrength = currStrength;
        }
      }
    }
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int color1 = image.getRGB(i, j);
        float currStrength = colorStrength(color1);
        float adjustedStrength = (currStrength - minStrength) / (maxStrength - minStrength) * (maxValue / 255f - minValue / 255f) + minValue / 255f;
        newImage.setRGB(i, j, clampColor(strengthToColor(adjustedStrength)));
      }
    }
    return newImage;
  }

  public static BufferedImage remap(BufferedImage image, ArrayList<Pair<Integer, Integer>> mappings) {
    //TODO push this onto the gpu (very low pri)
    float[] remaps = new float[256];
    for (int i = 0; i <= 255 - 1; i++) {
      for (int k = 0; k < mappings.size() - 1; k++) {
        Pair<Integer, Integer> lower = mappings.get(k);
        Pair<Integer, Integer> upper = mappings.get(k + 1);
        if (i >= lower.getKey() && i <= upper.getKey()) {
          float strength = ((float)(i - lower.getKey()) / (float)(upper.getKey() - lower.getKey()) * (float)(upper.getValue() - lower.getValue()) + lower.getValue()) / 255f;
          remaps[i] = strength;
          break;
        }
      }
    }
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int color1 = image.getRGB(i, j);
        int strength = (int) (colorStrength(color1) * 255f);
        newImage.setRGB(i, j, clampColor(strengthToColor(remaps[strength])));
      }
    }
    return newImage;
  }

  public static BufferedImage bucket(BufferedImage image, int buckets) {
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int color1 = image.getRGB(i, j);
        int strength = (int) (colorStrength(color1) * 255f);
        strength = (int) (Math.floor(strength / (255f / buckets)) * (255f / (buckets - 1)));
        newImage.setRGB(i, j, clampColor(strengthToColor(strength / 255f)));
      }
    }
    return newImage;
  }

  public static BufferedImage biasPoles(BufferedImage image) {
    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < image.getWidth(); i++) {
      for (int j = 0; j < image.getHeight(); j++) {
        int color1 = image.getRGB(i, j);
        int strength = (int) (colorStrength(color1) * 255f);
        strength = (int) (Math.abs(strength - (255f / 2f)) * 2f);
        if (j < 100 || j > 700) {
          strength = 255;
        }
        newImage.setRGB(i, j, clampColor(strengthToColor(strength / 255f)));
        // this code will make the poles more pronounced, but i didn't really like it.
        // float fstrength = (float) strength / 255f;
        // // change scale. higher = more poles.
        // float scale = 6f;
        // fstrength = (float) Math.log(fstrength * scale + 1f) / (float) Math.log(scale + 1f);
        // newImage.setRGB(i, j, clampColor(strengthToColor(fstrength)));
      }
    }
    return newImage;
  }

}
