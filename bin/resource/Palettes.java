package bin.resource;

import bin.graphics.Color;
import bin.resource.palette.Palette;

public class Palettes{
  //ocean, land, mointain, snow, pole, cloud, atmosphere lower, atmosphere upper
  public static Palette earth1 = new Palette(new Color("#0000ff"), //ocean
                                             new Color("#00ff00"), //land
                                             new Color("#BC5E20"), //mountain
                                             new Color("#ffffff"), //snow
                                             new Color("#ffffff"), //pole
                                             new Color("#ffffff", .7f), //cloud
                                             new Color("#03fcf8"), //atmosphere lower
                                             new Color("#0000ff", .3f) //atmosphere upper
                                             );
  public static Palette earth2 = new Palette(new Color("#00C0CE"), //ocean
                                             new Color("#58FF2C"), //land
                                             new Color("#B29000"), //mountain
                                             new Color("#ffffff"), //snow
                                             new Color("#B290FF"), //pole
                                             new Color("#ffffff", .7f), //cloud
                                             new Color("#03FFAF"), //atmosphere lower
                                             new Color("#00ACFF", .4f) //atmosphere upper
                                             );
}
