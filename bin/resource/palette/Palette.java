package bin.resource.palette;

import bin.graphics.Color;
import bin.resource.ImageResource;

public class Palette {
  public ImageResource paletteSampler = null;
  public Color lowerAtmosphereColor = null;
  public Color upperAtmosphereColor = null;

  public Palette (ImageResource paletteSampler, Color lowerAtmosphereColor, Color upperAtmosphereColor) {
    this.paletteSampler = paletteSampler;
    this.lowerAtmosphereColor = lowerAtmosphereColor;
    this.upperAtmosphereColor = upperAtmosphereColor;
  }
  // public Palette (int seed, PalletPreset preset) {
  //
  // }
}
