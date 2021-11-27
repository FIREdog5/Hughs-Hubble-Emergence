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

}
