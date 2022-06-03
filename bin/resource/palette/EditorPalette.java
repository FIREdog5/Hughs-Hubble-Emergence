package bin.resource.palette;

import bin.graphics.Color;
import bin.resource.ImageResource;
import bin.Wrapper;

public class EditorPalette extends Palette{
  private Wrapper<ImageResource> paletteSamplerWrapped = null;

  public EditorPalette (Wrapper<ImageResource> paletteSamplerWrapped, Color lowerAtmosphereColor, Color upperAtmosphereColor) {
    super(null, lowerAtmosphereColor, upperAtmosphereColor);
    this.paletteSamplerWrapped = paletteSamplerWrapped;
  }

  public ImageResource getPaletteSampler() {
    return this.paletteSamplerWrapped.get();
  }

  public void setPaletteSampler(Wrapper<ImageResource> paletteSampler) {
    this.paletteSamplerWrapped = paletteSamplerWrapped;
  }
  // public Palette (int seed, PalletPreset preset) {
  //
  // }
}
