package bin.resource.palette;

import bin.graphics.Color;

public class Palette {
  public Color oceanColor = null;
  public Color landColor = null;
  public Color mountainColor = null;
  public Color snowColor = null;
  public Color poleColor = null;
  public Color cloudColor = null;
  public Color lowerAtmosphereColor = null;
  public Color upperAtmosphereColor = null;

  public Palette (Color oceanColor, Color landColor, Color mountainColor, Color snowColor, Color poleColor, Color cloudColor, Color lowerAtmosphereColor, Color upperAtmosphereColor) {
    this.oceanColor = oceanColor;
    this.landColor = landColor;
    this.mountainColor = mountainColor;
    this.snowColor = snowColor;
    this.poleColor = poleColor;
    this.cloudColor = cloudColor;
    this.lowerAtmosphereColor = lowerAtmosphereColor;
    this.upperAtmosphereColor = upperAtmosphereColor;
  }
  // public Palette (int seed, PalletPreset preset) {
  //
  // }
}
