package bin.resource;

import bin.graphics.Color;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Collections;
import java.util.Arrays;

public class GradientGenerator {

  private static final Comparator<ColorPosition> comparator = (ColorPosition o1, ColorPosition o2) -> o1.position - o2.position;

  private static int mixColors(int color1, int color2, float mixRatio) {
    int red = (int) (mixRatio * (float) ((color1 >> 16) & 255) + (1 - mixRatio) * (float) ((color2 >> 16) & 255));
    int green = (int) (mixRatio * (float) ((color1 >> 8) & 255) + (1 - mixRatio) * (float) ((color2 >> 8) & 255));
    int blue = (int) (mixRatio * (float) (color1 & 255) + (1 - mixRatio) * (float) (color2 & 255));
    return ((red & 255) << 16) + ((green & 255) << 8) + (blue & 255);
  }

  private static int colorToInt(Color color) {
    int red = (int)(color.getRed() * 256) * 0x010000;
    int green = (int)(color.getGreen() * 256) * 0x000100;
    int blue = (int)(color.getBlue() * 256) * 0x000001;
    return red + green + blue;
  }

  public static BufferedImage newGradient(int cols) {
    return new BufferedImage(cols, 256, BufferedImage.TYPE_INT_RGB);
  }

  public static void setColumn(BufferedImage image, int col, ColorPosition[] gradientPositions, ColorPosition[] solidPositions) {
    ArrayList<ColorPosition> sortable = new ArrayList<ColorPosition>(Arrays.asList(gradientPositions));
    sortable.add(new ColorPosition(new Color("#ffffff"), 1, -1));
    sortable.add(new ColorPosition(new Color("#ffffff"), 1, 256));
    Collections.sort(sortable, comparator);
    Iterator<ColorPosition> positions = sortable.iterator();
    ColorPosition low = positions.next();
    ColorPosition high = positions.next();
    for (int i = 0; i < 256; i++) {
      while (positions.hasNext() && i > high.position) {
        low = high;
        high = positions.next();
      }
      int color = mixColors(colorToInt(high.color), colorToInt(low.color), (float)(i - low.position) / (float)(high.position - low.position));
      image.setRGB(col, i, color);
    }
    for (ColorPosition current : solidPositions) {
      for (int i = 0; i < current.width; i++) {
        image.setRGB(col, current.position + (i - (int)(current.width / 2)), colorToInt(current.color));
      }
    }
  }
}
