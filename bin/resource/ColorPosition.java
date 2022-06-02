package bin.resource;

import bin.graphics.Color;

public class ColorPosition {
  public Color color;
  public int width;
  public int position;

  public ColorPosition() {
    this.color = new Color("#ffffff");
    this.width = 1;
    this.position = 128;
  }

  public ColorPosition(Color color, int width, int position) {
    this.color = color;
    this.width = width;
    this.position = position;
  }

  public ColorPosition(ColorPosition ref) {
    this.color = ref.color;
    this.width = ref.width;
    this.position = ref.position >= 255 ? ref.position - 1 : ref.position + 1;
  }

}
