package bin.graphics;

public class Color {
  private float red;
  private float green;
  private float blue;
  private float alpha;
  public Color (String hex) {
    this.setRed(Integer.parseInt(hex.substring(hex.length() - 6, hex.length() - 4), 16) / 256f);
    this.setGreen(Integer.parseInt(hex.substring(hex.length() - 4, hex.length() - 2), 16) / 256f);
    this.setBlue(Integer.parseInt(hex.substring(hex.length() - 2, hex.length()), 16) / 256f);
    this.setAlpha(1);
  }

  public Color (String hex, float a) {
    this.setRed(Integer.parseInt(hex.substring(hex.length() - 6, hex.length() - 4), 16) / 256f);
    this.setGreen(Integer.parseInt(hex.substring(hex.length() - 4, hex.length() - 2), 16) / 256f);
    this.setBlue(Integer.parseInt(hex.substring(hex.length() - 2, hex.length()), 16) / 256f);
    this.setAlpha(a);
  }

  public Color (float r, float g, float b) {
    this.setRed(r);
    this.setGreen(g);
    this.setBlue(b);
    this.setAlpha(1);
  }

  public Color (float r, float g, float b, float a) {
    this.setRed(r);
    this.setGreen(g);
    this.setBlue(b);
    this.setAlpha(a);
  }

  private void setRed(float value) {
    this.red = Math.max(0f, Math.min(1f, value));
  }

  private void setGreen(float value) {
    this.green = Math.max(0f, Math.min(1f, value));
  }

  private void setBlue(float value) {
    this.blue = Math.max(0f, Math.min(1f, value));
  }

  private void setAlpha(float value) {
    this.alpha = Math.max(0f, Math.min(1f, value));
  }

  public float getRed() {
    return this.red;
  }

  public float getGreen() {
    return this.green;
  }

  public float getBlue() {
    return this.blue;
  }

  public float getAlpha() {
    return this.alpha;
  }

  public static Color colorFromHSV(float hue, float saturation, float value) {

    //from https://medium.com/@bantic/hand-coding-a-color-wheel-with-canvas-78256c9d7d43
    //hue is range [0-2pi]
    //s, v are range [0-1]

    float chroma = value * saturation;
    float hue1 = hue / (float)Math.PI * 3;
    float x = chroma * (1- (float)Math.abs((hue1 % 2) - 1));
    float[] rgb = new float[3];
    if (hue1 >= 0 && hue1 <= 1) {
      rgb = new float[]{chroma, x, 0f};
    } else if (hue1 >= 1 && hue1 <= 2) {
      rgb = new float[]{x, chroma, 0f};
    } else if (hue1 >= 2 && hue1 <= 3) {
      rgb = new float[]{0f, chroma, x};
    } else if (hue1 >= 3 && hue1 <= 4) {
      rgb = new float[]{0f, x, chroma};
    } else if (hue1 >= 4 && hue1 <= 5) {
      rgb = new float[]{x, 0f, chroma};
    } else if (hue1 >= 5 && hue1 <= 6) {
      rgb = new float[]{chroma, 0f, x};
    }
    float m = value - chroma;

    return new Color(rgb[0] + m, rgb[1] + m, rgb[2] + m);
  }
}
