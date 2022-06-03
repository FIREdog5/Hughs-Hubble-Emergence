package bin.graphics;

public class Color {
  protected float red;
  protected float green;
  protected float blue;
  protected float alpha;

  protected Color refColor;

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

  public Color (Color refColor) {
    this.refColor = refColor;
    this.transformRef();
  }

  protected void transformRef() {
    if (this.refColor == null) {
      return;
    }
    this.setRGBA(this.refColor.getRGBA());
  }

  public void setRed(float value) {
    this.red = Math.max(0f, Math.min(1f, value));
  }

  public void setGreen(float value) {
    this.green = Math.max(0f, Math.min(1f, value));
  }

  public void setBlue(float value) {
    this.blue = Math.max(0f, Math.min(1f, value));
  }

  public void setAlpha(float value) {
    this.alpha = Math.max(0f, Math.min(1f, value));
  }

  public void setRGB(float[] rgb) {
    this.setRed(rgb[0]);
    this.setGreen(rgb[1]);
    this.setBlue(rgb[2]);
  }

  public void setRGBA(float[] rgba) {
    this.setRed(rgba[0]);
    this.setGreen(rgba[1]);
    this.setBlue(rgba[2]);
    this.setAlpha(rgba[3]);
  }

  public float getRed() {
    this.transformRef();
    return this.red;
  }

  public float getGreen() {
    this.transformRef();
    return this.green;
  }

  public float getBlue() {
    this.transformRef();
    return this.blue;
  }

  public float getAlpha() {
    this.transformRef();
    return this.alpha;
  }

  public float[] getRGB() {
    this.transformRef();
    return new float[]{this.red, this.green, this.blue};
  }

  public float[] getRGBA() {
    this.transformRef();
    return new float[]{this.red, this.green, this.blue, this.alpha};
  }

  public void setHSV(float hue, float saturation, float value) {

    // from https://medium.com/@bantic/hand-coding-a-color-wheel-with-canvas-78256c9d7d43
    // hue is range [0-2pi]
    // s, v are range [0-1]

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

    this.setRed(rgb[0] + m);
    this.setGreen(rgb[1] + m);
    this.setBlue(rgb[2] + m);
  }

  public void setHSV(float[] hsv) {
    this.setHSV(hsv[0], hsv[1], hsv[2]);
  }

  public float[] getHSV() {
    this.transformRef();

    // from https://en.wikipedia.org/wiki/HSL_and_HSV#From_RGB

    float xMax = Math.max(this.red, Math.max(this.green, this.blue));
    float xMin = Math.min(this.red, Math.min(this.green, this.blue));
    float v = xMax;
    float c = Math.abs(xMax - xMin);
    float h = 0;
    if (c == 0) {
      h = 0;
    } else if (v == this.red) {
      h = (float)(Math.PI / 3f) * (0 + (this.green - this.blue) / c);
    } else if (v == this.green) {
      h = (float)(Math.PI / 3f) * (2 + (this.blue - this.red) / c);
    } else if (v == this.blue) {
      h = (float)(Math.PI / 3f) * (4 + (this.red - this.green) / c);
    }
    h = h < 0? (((float)Math.PI * 2) + h) % ((float)Math.PI * 2) : h;
    float s = v == 0 ? 0f : c / v;
    return new float[]{h, s, v};
  }

  public String getHex() {
    return "#" + Integer.toHexString((int)(this.getRed() * 255)) + Integer.toHexString((int)(this.getGreen() * 255)) + Integer.toHexString((int)(this.getBlue() * 255));
  }

  public static Color colorFromHSV(float hue, float saturation, float value) {
    Color rColor = new Color("#ffffff");
    rColor.setHSV(hue, saturation, value);
    return rColor;
  }

  public static Color randomColor() {
    float randHue = (float)Math.random() * (float)Math.PI * 2;
    Color rColor = new Color("#ffffff");
    rColor.setHSV(randHue, 1f, 1f);
    return rColor;
  }


}
