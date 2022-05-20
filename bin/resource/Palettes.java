package bin.resource;

import bin.graphics.Color;
import bin.resource.palette.Palette;

public class Palettes{
  //ocean, land, mointain, snow, pole, cloud, atmosphere lower, atmosphere upper
  public static Palette earth1 = new Palette(ImageResources.biomeShaderTest,
                                             new Color("#03fcf8"), //atmosphere lower
                                             new Color("#0000ff", .3f) //atmosphere upper
                                             );
}
