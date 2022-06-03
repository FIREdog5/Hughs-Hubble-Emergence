package bin.resource.palette;

import bin.graphics.Color;
import bin.resource.ImageResource;

public class Palette {
  private ImageResource paletteSampler = null;
  private Color lowerAtmosphereColor = null;
  private Color upperAtmosphereColor = null;

  public Palette (ImageResource paletteSampler, Color lowerAtmosphereColor, Color upperAtmosphereColor) {
    this.paletteSampler = paletteSampler;
    this.lowerAtmosphereColor = lowerAtmosphereColor;
    this.upperAtmosphereColor = upperAtmosphereColor;
  }

  public ImageResource getPaletteSampler() {
    return this.paletteSampler;
  }
  public Color getLowerAtmosphereColor() {
    return this.lowerAtmosphereColor;
  }
  public Color getUpperAtmosphereColor() {
    return this.upperAtmosphereColor;
  }

  public void setPaletteSampler(ImageResource paletteSampler) {
    this.paletteSampler = paletteSampler;
  }
  public void setLowerAtmosphereColor(Color lowerAtmosphereColor) {
    this.lowerAtmosphereColor = lowerAtmosphereColor;
  }
  public void setUpperAtmosphereColor(Color upperAtmosphereColor) {
    this.upperAtmosphereColor = upperAtmosphereColor;
  }
  // public Palette (int seed, PalletPreset preset) {
  //
  // }
}
